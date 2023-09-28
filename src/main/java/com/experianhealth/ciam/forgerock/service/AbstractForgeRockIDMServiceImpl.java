package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.EnvironmentSettings;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.forgerock.model.FRQuery;
import com.experianhealth.ciam.forgerock.service.helper.GeneralJsonMapper;
import com.experianhealth.ciam.forgerock.service.helper.HTTPRequestProcessor;

import javax.json.JsonPatch;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implements all the methods defined in the GeneralForgeRockIDMService interface.
 *
 * Classes extending this abstract class must provide a Class reference to the entity/model class
 * that the concrete implementation is working with and also provide and implementation of
 * String getBasePath() for the path of the IDM service.
 *
 * @param <T> The class for the model object that the IDM service returns
 */
public abstract class AbstractForgeRockIDMServiceImpl<T> implements GeneralForgeRockIDMService<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractForgeRockIDMServiceImpl.class.getName());
    Class<T> entityClass;
    GeneralJsonMapper<T> jsonMapper;
    protected final String idmBaseUrl;
    protected final String baseUrl;

    AbstractForgeRockIDMServiceImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.idmBaseUrl = EnvironmentSettings.getIdmBaseUrl();
        this.baseUrl = idmBaseUrl + "/" + this.getBasePath();
        this.jsonMapper = new GeneralJsonMapper<T>(entityClass);
    }

    @Override
    public Optional<T> getById(String token, String id) {
        try {
            return Optional.ofNullable(
                jsonMapper.mapJson(
                        HTTPRequestProcessor.sendGetRequest(
                                token, URI.create(getEndpointWithId(id))
                        )
                )
            );
        } catch (CIAMNotFoundException notFoundException) {
            LOGGER.info(notFoundException.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<T> getAll(String token) {
        return search(token, FRQuery.queryAll());
    }

    @Override
    public T create(String token, T entity) {
        return jsonMapper.mapJson(HTTPRequestProcessor.sendPostRequest(URI.create(baseUrl), token, entity));
    }

    @Override
    public List<T> search(String token, FRQuery query) {
        return jsonMapper.mapJsonToList(HTTPRequestProcessor.sendGetRequest(token, query.buildURI(baseUrl)));
    }

    @Override
    public T replace(String token, String id, T entity) {
        return jsonMapper.mapJson(HTTPRequestProcessor.sendPutRequest(token, URI.create(getEndpointWithId(id)), entity));
    }

    @Override
    public T modify(String token, String id, JsonPatch patch) {
        return jsonMapper.mapJson(HTTPRequestProcessor.sendPatchRequest(token, URI.create(getEndpointWithId(id)), patch));
    }

    @Override
    public Optional<T> delete(String token, String id) {
        try {
            return Optional.of(jsonMapper.mapJson(HTTPRequestProcessor.sendDeleteRequest(token, URI.create(getEndpointWithId(id)))));
        } catch (CIAMNotFoundException notFoundException) {
            LOGGER.info(notFoundException.getMessage());
            return Optional.empty();
        }
    }

    protected String getEndpointWithId(String id) {
        return baseUrl + "/" + id;
    }

    abstract String getBasePath();
}
