package sk.oravcok.posta.facade;

import org.springframework.stereotype.Service;
import sk.oravcok.posta.dto.PlaceCreateDTO;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.dto.PlaceUpdateDTO;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Implementation of PlaceFacade
 *
 * Created by Ondrej Oravcok on 20-Nov-16.
 */
@Service
@Transactional
public class PlaceFacadeImpl implements PlaceFacade {

    @Override
    public Long createPlace(PlaceCreateDTO place) {
        return null;
    }

    @Override
    public void updatePlace(PlaceUpdateDTO place) {

    }

    @Override
    public List<PlaceDTO> getAllPlaces() {
        return null;
    }

    @Override
    public PlaceDTO getPlaceById(Long placeId) {
        return null;
    }

    @Override
    public void removePlace(Long placeId) {

    }

}
