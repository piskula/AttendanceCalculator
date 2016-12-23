package sk.oravcok.posta.service;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dao.PlaceDao;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.ServiceExceptionTranslateAspect;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Ondrej Oravcok
 * @version 20-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class PlaceServiceTest extends AbstractTestNGSpringContextTests {

    @Mock
    private PlaceDao placeDao;

    private PlaceService placeService;

    @Captor
    ArgumentCaptor<Place> argumentCaptor;

    private Place window1;
    private Place background1;

    private long notPersistedId = 17l;
    private long alreadyExistingId = 88l;
    private long createdPlaceId = 100l;
    private long updatedPlaceId = 101l;

    @BeforeClass
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);

        //We need to setup proxy correctly because of using exception translation
        //through Aspect on mocked objects
        ServiceExceptionTranslateAspect serviceExceptionTranslateAspect = new ServiceExceptionTranslateAspect();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new PlaceServiceImpl());
        aspectJProxyFactory.addAspect(serviceExceptionTranslateAspect);

        placeService = aspectJProxyFactory.getProxy();
        ReflectionTestUtils.setField(placeService, "placeDao", placeDao);
    }

    @BeforeMethod
    public void initPlaces() {
        window1 = new Place();
        window1.setName("Window 1");
        window1.setPlaceType(PlaceType.WINDOW);
        window1.setAnnotation("Closed window due to reconstruction");

        background1 = new Place();
        background1.setId(1l);
        background1.setName("Background 1");
        background1.setPlaceType(PlaceType.WINDOW);
        background1.setAnnotation("Working behind customers eyes");
    }

    @BeforeMethod(dependsOnMethods = "initPlaces")
    public void setUpMocksBehaviour() {
        //BEHAVIOUR OF: findById()
        when(placeDao.findById(0l)).thenReturn(null);
        when(placeDao.findById(1l)).thenReturn(window1);

        doAnswer((InvocationOnMock invocation) -> {
            throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
        }).when(placeDao).findById(null);

        //BEHAVIOUR OF: findPlaceByName()
        when(placeDao.findByName("non-existing")).thenReturn(null);
        when(placeDao.findByName("Window 1")).thenReturn(window1);
        when(placeDao.findByName("Background 1")).thenReturn(background1);

        //BEHAVIOUR OF: create()
        doAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Place place = (Place) invocation.getArguments()[0];
            if(place.getId() != null && place.getId().equals(alreadyExistingId)) {
                throw new EntityExistsException("This is EntityManager test case.");
            }

            if(place.getName() == null || place.getPlaceType() == null) {
                throw new ConstraintViolationException("This should be tested in Persistence Validation.", null);
            }
            place.setId(createdPlaceId);
            return null;    //happy day scenario
        }).when(placeDao).create(any(Place.class));

        //BEHAVIOUR OF: update()
        doAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Place place = (Place) invocation.getArguments()[0];

            if(place.getName() == null || place.getPlaceType() == null) {
                throw new ConstraintViolationException("This should be tested in Persistence Validation.", null);
            }
            if(place.getId() == null) {
                place.setId(updatedPlaceId);
            }

            return place;   //happy day scenario
        }).when(placeDao).update(any(Place.class));

        //BEHAVIOUR OF: remove()
        doAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Place place = (Place) invocation.getArguments()[0];
            if(place.getId() == alreadyExistingId) {
                return null;    //happy day scenario
            }
            if(place.getId() == notPersistedId) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");   //place is not saved
            }

            return null;
        }).when(placeDao).remove(any(Place.class));
    }

    @Test
    public void createPlaceTest() {
        Place window2 = new Place();
        window2.setName("Window 2");
        window2.setPlaceType(PlaceType.WINDOW);
        window2.setAnnotation("very fast window");

        placeService.create(window2);
        verify(placeDao).create(argumentCaptor.capture());
        assertNotNull(window2);
        assertEquals((long) window2.getId(), createdPlaceId);
        assertDeepEqualsWithoutId(argumentCaptor.getValue(), window2);
    }

    //TODO

    private void assertDeepEqualsWithoutId(Place p1, Place p2) {
        assertEquals(p1.getName(), p2.getName());
        assertEquals(p1.getPlaceType(), p2.getPlaceType());
        assertEquals(p1.getAnnotation(), p2.getAnnotation());
    }
}
