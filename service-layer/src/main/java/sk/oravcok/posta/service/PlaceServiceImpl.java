package sk.oravcok.posta.service;

import org.springframework.stereotype.Service;
import sk.oravcok.posta.dao.PlaceDao;
import sk.oravcok.posta.entity.Place;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
@Service
public class PlaceServiceImpl implements PlaceService {

    @Inject
    private PlaceDao placeDao;

    @Override
    public void create(Place place) {
        if (place == null) {
            throw new IllegalArgumentException("Place is null - cannot create");
        }
        placeDao.create(place);
    }

    @Override
    public Place update(Place place) {
        if (place == null) {
            throw new IllegalArgumentException("Place is null - cannot update");
        }
        return placeDao.update(place);
    }

    @Override
    public Place findById(Long id) {
        if(id == null){
            throw new IllegalArgumentException("id of place is null - cannot find by id");
        }
        return placeDao.findById(id);
    }

    @Override
    public Place findByName(String name) {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("name of place is null (or empty) - cannot find by name");
        }
        return placeDao.findByName(name);
    }

    @Override
    public List<Place> findAll() {
        return placeDao.findAll();
    }

    @Override
    public void remove(Place place) {
        if(place == null){
            throw new IllegalArgumentException("place is null - cannot remove");
        }
        placeDao.remove(place);
    }

}
