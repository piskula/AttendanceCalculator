package sk.oravcok.posta.dao;

import org.springframework.stereotype.Repository;
import sk.oravcok.posta.entity.Place;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Ondrej Oravcok on 27-Oct-16.
 */
@Transactional
@Repository
public class PlaceDaoImpl implements PlaceDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(Place place) {
        entityManager.persist(place);
    }

    @Override
    public Place update(Place place) {
        return entityManager.merge(place);
    }

    @Override
    public void remove(Place place) {
        if(place == null){
            throw new IllegalArgumentException("Deleting null Place entity.");
        }
        entityManager.remove(findById(place.getId()));
    }

    @Override
    public Place findById(Long id) {
        return entityManager.find(Place.class, id);
    }

    @Override
    public List<Place> findAll() {
        TypedQuery<Place> query = entityManager.createQuery("SELECT p FROM Place p", Place.class);
        return query.getResultList();
    }

    @Override
    public Place findByName(String name) {
        try{
            TypedQuery<Place> query = entityManager.createQuery("SELECT p FROM Place p WHERE p.name = :name",
                    Place.class).setParameter("name", name);
            return query.getSingleResult();
        } catch(NoResultException e){
            return null;
        }
    }
}
