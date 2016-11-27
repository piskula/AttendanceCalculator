package sk.oravcok.posta.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.oravcok.posta.dto.PlaceCreateDTO;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.dto.PlaceUpdateDTO;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.service.PlaceService;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of PlaceFacade
 *
 * Created by Ondrej Oravcok on 20-Nov-16.
 */
@Service
@Transactional
public class PlaceFacadeImpl implements PlaceFacade {

    @Inject
    PlaceService placeService;

    @Inject
    BeanMappingService beanMappingService;

    @Override
    public Long createPlace(PlaceCreateDTO placeCreateDTO) {
        if (placeCreateDTO == null) {
            throw new IllegalArgumentException("place cannot be null");
        }
        Place place = beanMappingService.mapTo(placeCreateDTO, Place.class);

        placeService.create(place);
        return place.getId();
    }

    @Override
    public void updatePlace(PlaceUpdateDTO placeUpdateDTO) {
        if (placeUpdateDTO == null) {
            throw new IllegalArgumentException("place cannot be null");
        }
        Place place = beanMappingService.mapTo(placeUpdateDTO, Place.class);

        if(placeService.findById(place.getId()) == null){
            throw new NonExistingEntityException("Can not update non existing employee");
        }

        placeService.update(place);
    }

    @Override
    public List<PlaceDTO> findAllPlaces() {
        return beanMappingService.mapTo(placeService.findAll(), PlaceDTO.class);
    }

    @Override
    public PlaceDTO findPlaceById(Long placeId) {
        if (placeId == null) {
            throw new IllegalArgumentException("placeId is null");
        }

        Place place = placeService.findById(placeId);
        if (place == null) {
            throw new NonExistingEntityException("Place with id=" + placeId + " does not exist");
        }
        return beanMappingService.mapTo(place, PlaceDTO.class);
    }

    @Override
    public PlaceDTO findPlaceByName(String placeName) {
        if (placeName == null) {
            throw new IllegalArgumentException("placeName is null");
        }

        Place place = placeService.findByName(placeName);
        if (place == null) {
            throw new NonExistingEntityException("Place with name=" + placeName + " does not exist");
        }
        return beanMappingService.mapTo(place, PlaceDTO.class);
    }

    @Override
    public void removePlace(Long placeId) {
        if (placeId == null) {
            throw new IllegalArgumentException("placeId is null");
        }

        Place place = placeService.findById(placeId);
        if (place == null) {
            throw new NonExistingEntityException("Place with id=" + placeId + " does not exist");
        }
        placeService.remove(new Place(place.getId()));
    }

}
