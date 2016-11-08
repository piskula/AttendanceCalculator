package sk.oravcok.posta.dao;

import sk.oravcok.posta.entity.Place;

import java.util.List;

/**
 * Created by Ondrej Oravcok on 27-Oct-16.
 */
public interface PlaceDao {

    /**
     * Save new place to DB
     *
     * @param place to be persisted
     */
    void create(Place place);

    /**
     * Update already saved Place in DB
     *
     * @param place to be updated
     * @return updated place
     */
    Place update(Place place);

    /**
     * Removes place from DB
     *
     * @param place to be removed
     */
    void remove(Place place);

    /**
     * search for place by its specific id
     *
     * @param id of place to be look for
     * @return specific place
     */
    Place findById(Long id);

    /**
     * gives you all places stored in DB
     *
     * @return all places
     */
    List<Place> findAll();

    /**
     * find specific place by it's name
     *
     * @param name given name
     * @return specific place, if found
     */
    Place findByName(String name);
}
