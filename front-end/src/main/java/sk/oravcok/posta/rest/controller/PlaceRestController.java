package sk.oravcok.posta.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sk.oravcok.posta.dto.*;
import sk.oravcok.posta.exception.DataManipulationException;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.PlaceFacade;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.ExistingResourceException;
import sk.oravcok.posta.rest.exception.RequestedResourceNotFound;
import sk.oravcok.posta.rest.exception.ValidationException;

import javax.inject.Inject;
import java.util.List;

/**
 * Place REST controller
 *
 * @author Ondrej Oravcok
 * @version 25-Dec-16.
 */
@RestController
@RequestMapping(URI.PLACES)
public class PlaceRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceRestController.class);

    @Inject
    private PlaceFacade placeFacade;

    /**
     * gives you all places available in system
     *
     * e.g. curl -i -X GET http://localhost:8080/posta/rest/places
     *
     * @return all places
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<PlaceDTO> findAllPlaces() {
        LOGGER.info("getting all places");
        return placeFacade.findAllPlaces();
    }

    /**
     * gives you specific Place by id
     *
     * e.g. curl -i -X GET http://localhost:8080/posta/rest/places/findId/3
     *
     * @param id to find
     * @return specific Place
     */
    @RequestMapping(value = "/findId/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final PlaceDTO findPlaceById(@PathVariable("id") long id) {
        try {
            LOGGER.info("getting Place with id=" + id);
            return placeFacade.findPlaceById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Place with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }
    }

    /**
     * find Place by name in system
     *
     * curl -X GET -i -H "Content-Type: application/json" --data '{"name":"Priehradka c.2"}' http://localhost:8080/posta/rest/places/findName
     *
     * @param place to find by name
     * @return specific place
     */
    @RequestMapping(value = "/findName", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final PlaceDTO findPlaceByName(@RequestBody FindTextDTO place) {
        if(place.getText() == null || place.getText().isEmpty()) {
            throw new ValidationException("Name of place to search is invalid");
        }
        try {
            LOGGER.info("getting Place with name=" + place.getText());
            return placeFacade.findPlaceByName(place.getText());
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Place with name=" + place.getText() + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument name is illegal", e);
        }
    }

    /**
     * creates new Place in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Priehradka c.22","placeType":"WINDOW","annotation":"priehradka 22"}' http://localhost:8080/posta/rest/places
     *
     * @param place to create
     * @return created place
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final PlaceDTO createPlace(@RequestBody PlaceCreateDTO place) {
        try {
            LOGGER.info("creating Place " + place);
            Long id = placeFacade.createPlace(place);
            return placeFacade.findPlaceById(id);
        } catch (DataManipulationException e) {
            throw new ValidationException("DataManipulationException thrown while creating place.", e);
        } catch (Exception e) {
            throw new ExistingResourceException(e);
        }
    }

    /**
     * removes Place from system
     *
     * curl -i -X DELETE http://localhost:8080/posta/rest/places/3
     *
     * @param id of Place to remove
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final void deletePlace(@PathVariable("id") long id) {
        try {
            LOGGER.info("deleting Place with id=" + id);
            placeFacade.removePlace(id);
        } catch (NonExistingEntityException | IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Place with id=" + id + " does not exist in system.", e);
        }
    }

    /**
     * updates Place in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Priehradka c.4 - vynovena na BACKGROUND","placeType":"BACKGROUND","annotation":"priehradka 4 vynovena"}' http://localhost:8080/posta/rest/places/update/4
     *
     * @param id of updating Place
     * @param placeDTO values, which we need to actualize
     * @return actualised Place
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final PlaceDTO updatePlace(@PathVariable("id") long id, @RequestBody PlaceDTO placeDTO) {
        PlaceDTO previous;
        try {
            LOGGER.info("updating Place with id=" + id + ", with values " + placeDTO);
            previous = placeFacade.findPlaceById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Place with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }

        PlaceDTO actual = mergeDtosForUpdate(previous, placeDTO);
        try {
            placeFacade.updatePlace(actual);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Place place=" + actual + " does not exist in system.", e);
        } catch (Exception e) {
            throw new RequestedResourceNotFound("Exception thrown while updating existing place.", e);
        }

        return actual;
    }

    private PlaceDTO mergeDtosForUpdate(PlaceDTO previous, PlaceDTO actual) {
        PlaceDTO place = new PlaceDTO();
        place.setId(previous.getId());

        place.setName(actual.getName() != null ? actual.getName() : previous.getName());
        place.setPlaceType(actual.getPlaceType() != null ? actual.getPlaceType() : previous.getPlaceType());
        place.setAnnotation(actual.getAnnotation() != null ? actual.getAnnotation() : previous.getAnnotation());

        return place;
    }

}
