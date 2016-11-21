package sk.oravcok.posta.service;

import sk.oravcok.posta.entity.Place;

import java.util.List;

/**
 * Business logic for Place
 *
 * Created by Ondrej Oravcok on 20-Nov-16.
 */
public interface PlaceService {

    /**
     * Creates new Place
     *
     * @param place to be created
     * @throws IllegalArgumentException if place is null
     */
    void create(Place place);

    /**
     * Updates Place
     *
     * @param place entity to be updated
     * @return updated place entity
     * @throws IllegalArgumentException if place is null
     */
    Place update(Place place);

    /**
     * Returns the place entity attached to the given id.
     *
     * @param id id of the place entity to be returned
     * @return the place entity with given id
     */
    Place findById(Long id);

    /**
     * Returns the place entity attached to the given id.
     *
     * @param name of the place entity to be returned
     * @return the place entity with given id
     */
    Place findByName(String name);

    /**
     * Returns all place entities.
     *
     * @return all places
     */
    List<Place> findAll();

    /**
     * Removes the place entity from persistence context.
     *
     * @param place place to be removed
     * @throws IllegalArgumentException if place is null
     */
    void remove(Place place);

}
