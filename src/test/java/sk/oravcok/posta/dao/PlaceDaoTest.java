package sk.oravcok.posta.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.TransactionSystemException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.PersistenceApplicationContext;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * Created by Ondrej Oravcok on 27-Oct-16.
 */
@ContextConfiguration(classes = PersistenceApplicationContext.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class PlaceDaoTest extends AbstractTestNGSpringContextTests {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    PlaceDao placeDao;

    Place window1;
    Place background1;

    @BeforeMethod
    public void fillPlacesWithData(){
        window1 = new Place();
        window1.setName("Priehradka 1");
        window1.setPlaceType(PlaceType.WINDOW);
        window1.setAnnotation("vsetko vybavime");

        background1 = new Place();
        background1.setName("V zazemi 1");
        background1.setPlaceType(PlaceType.BACKGROUND);
        background1.setAnnotation("tu sa iba spi");
    }

    @Test
    public void createPlaceTest(){
        placeDao.create(window1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullPlaceTest(){
        placeDao.create(null);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createNameNullPlaceTest(){
        window1.setName(null);
        placeDao.create(window1);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createPlaceTypeNullPlaceTest(){
        window1.setPlaceType(null);
        placeDao.create(window1);
    }

    @Test
    public void updatePlaceTest(){
        placeDao.create(window1);
        window1.setName("Priehradka 2");
        window1.setPlaceType(PlaceType.BACKGROUND);
        window1.setAnnotation("nic nevybavime");
        Place updated = placeDao.update(window1);
        assertDeepEquals(updated, window1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullPlaceTest(){
        placeDao.create(window1);
        placeDao.update(null);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void updatePlaceNullNameTest(){
        placeDao.create(window1);
        window1.setName(null);
        placeDao.update(window1);
        entityManager.flush();
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void updatePlaceNullPlaceTypeTest(){
        placeDao.create(window1);
        window1.setPlaceType(null);
        placeDao.update(window1);
        entityManager.flush();
    }

    @Test
    public void updateNotExistingPlaceTest(){
        placeDao.create(window1);
        placeDao.update(background1);
        Assert.assertEquals(placeDao.findAll().size(), 2);
    }

    @Test
    public void removePlaceTest(){
        placeDao.create(window1);
        placeDao.create(background1);
        Assert.assertEquals(2, placeDao.findAll().size());

        placeDao.remove(background1);
        Assert.assertEquals(1, placeDao.findAll().size());

        Place notRemoved = placeDao.findAll().get(0);
        assertDeepEquals(notRemoved, window1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullPlaceTest(){
        placeDao.create(window1);
        placeDao.create(background1);

        placeDao.remove(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNotExistingPlaceTest(){
        placeDao.create(window1);
        placeDao.remove(background1);
    }

    @Test
    public void findAllTest() {
        placeDao.create(window1);
        placeDao.create(background1);

        Assert.assertEquals(placeDao.findAll().size(), 2);
    }

    @Test
    public void findAllEmptyTest() {
        Assert.assertEquals(placeDao.findAll().size(), 0);
    }

    @Test
    public void findPlaceByIdTest() {
        placeDao.create(window1);
        placeDao.create(background1);

        Place result1 = placeDao.findById(window1.getId());
        assertDeepEquals(result1, window1);

        Place result2 = placeDao.findById(background1.getId());
        assertDeepEquals(result2, background1);
    }

    @Test
    public void findPlaceByNameTest(){
        placeDao.create(window1);
        placeDao.create(background1);

        Place result = placeDao.findByName("Priehradka 1");
        Assert.assertNotNull(result);
        assertDeepEquals(window1, result);
    }

    @Test
    public void findPlaceByBadNameTest(){
        placeDao.create(window1);
        placeDao.create(background1);

        Place result = placeDao.findByName("Garaz");
        Assert.assertNull(result);
    }

    private void assertDeepEquals(Place p1, Place p2){
        Assert.assertEquals(p1.getName(), p2.getName());
        Assert.assertEquals(p1.getPlaceType(), p2.getPlaceType());
        Assert.assertEquals(p1.getAnnotation(), p2.getAnnotation());
    }
}
