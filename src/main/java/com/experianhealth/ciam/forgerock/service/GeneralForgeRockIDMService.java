package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.forgerock.model.FRQuery;

import javax.json.JsonPatch;
import java.util.List;
import java.util.Optional;

/**
 * Defines the general API methods that all ForgeRock IDM Services will support
 *
 * @param <T> The class for the model object that the IDM service returns
 */
public interface GeneralForgeRockIDMService<T> {

    Optional<T> getById(String token, String id);
    List<T> getAll(String token);
    T create(String token, T entity);

    List<T> search(String token, FRQuery query);

    T replace(String token, String id, T entity);

    T modify(String token, String id, JsonPatch patch);

    Optional<T> delete(String token, String id);
}
