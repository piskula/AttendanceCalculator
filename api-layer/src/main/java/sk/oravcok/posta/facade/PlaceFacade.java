package sk.oravcok.posta.facade;

import sk.oravcok.posta.dto.PlaceCreateDTO;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.dto.PlaceUpdateDTO;

import java.util.List;

/**
 * Facade interface for Place
 *
 * Created by Ondrej Oravcok on 20-Nov-16.
 */
public interface PlaceFacade {

    /**
     * Creates place.
     *
     * @param place entity to be created
     * @return id of newly created place
     * @throws IllegalArgumentException if place is null
     */
    Long createPlace(PlaceCreateDTO place);

    /**
     * Updates place.
     *
     * @param place entity to be updated
     * @throws IllegalArgumentException   if place is null
     * @throws NonExistingEntityException on attempt to update non existing place
     */
    void updatePlace(PlaceUpdateDTO place);

    /**
     * Returns all places.
     *
     * @return list of all place entities or empty list if no place exists
     */
    List<PlaceDTO> getAllPlaces();

    /**
     * Returns place according to given id.
     *
     * @param placeId
     * @return place identified by unique id
     * @throws NonExistingEntityException if place for given id doesn't exist
     * @throws IllegalArgumentException if placeId is null
     */
    PlaceDTO getPlaceById(Long placeId);

    /**
     * Deletes place.
     *
     * @param placeId id of report to delete
     * @throws NonExistingEntityException if place for given id doesn't exist
     * @throws IllegalArgumentException if placeId is null
     */
    void removePlace(Long placeId);
    
}
