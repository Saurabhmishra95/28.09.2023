package com.experianhealth.ciam.forgerock.service.helper;

import com.experianhealth.ciam.exception.CIAMRuntimeException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.List;

/**
 * Helper class for deserializing ForgeRock JSON into entity classes
 * @param <T> The class for the model object that the IDM service returns
 */
public class GeneralJsonMapper<T> {

    Class<T> entityClass;
    public GeneralJsonMapper(Class<T> entityClass){
        this.entityClass = entityClass;
    }
    /**
     * Deserialize the JSON response from the reader into a User object
     *
     * @param reader Reader containing the JSON response
     * @return User object deserialized from the JSON response
     */
    public T mapJson(Reader reader) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(reader, entityClass);
        } catch (Exception e) {
            throw new CIAMRuntimeException("Failed to read User result", e);
        }
    }
    public List<T> mapJsonToList(Reader reader) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(reader);
            JsonNode resultsNode = rootNode.path("result");

            List<T> entities = mapper.readValue(resultsNode.toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, entityClass));

            return entities;
        } catch (Exception e) {
            throw new CIAMRuntimeException("Failed to read list results", e);
        }
    }

}
