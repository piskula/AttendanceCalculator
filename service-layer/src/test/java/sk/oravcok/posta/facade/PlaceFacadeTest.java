package sk.oravcok.posta.facade;

import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dto.PlaceCreateDTO;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.mapping.BeanMappingServiceImpl;
import sk.oravcok.posta.service.PlaceService;

import javax.inject.Inject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests for PlaceFacade
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class PlaceFacadeTest extends AbstractTestNGSpringContextTests {

    @Mock
    private PlaceService placeService;

    @Spy
    @Inject
    private final BeanMappingService beanMappingService = new BeanMappingServiceImpl();

    @InjectMocks
    private final PlaceFacade placeFacade = new PlaceFacadeImpl();

    @Captor
    ArgumentCaptor<Place> argumentCaptor;

    private Place window1;

    @BeforeClass
    public void setUpMockito(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void initEntities(){
        window1 = new Place(1l);
        window1.setName("Window 1");
        window1.setPlaceType(PlaceType.WINDOW);
        window1.setAnnotation("Main personal office");
    }

    @BeforeMethod(dependsOnMethods = "initEntities")
    public void setUpMockitoBehaviour(){
        //findById
        when(placeService.findById(0l)).thenReturn(null);
        when(placeService.findById(1l)).thenReturn(window1);

        //findByName
        when(placeService.findByName("not-existing")).thenReturn(null);
        when(placeService.findByName("Window 1")).thenReturn(window1);
    }

    @Test
    public void _thisTestConfigurationTest(){
        assertNotNull(placeService);
        assertNotNull(beanMappingService);
        assertNotNull(placeFacade);
    }

    @Test
    public void createPlaceTest(){
        PlaceCreateDTO window18 = new PlaceCreateDTO();
        window18.setName("Window 18");
        window18.setPlaceType(PlaceType.WINDOW);

        placeFacade.createPlace(window18);
        verify(placeService).create(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getName(), "Window 18");
        assertEquals(argumentCaptor.getValue().getPlaceType(), PlaceType.WINDOW);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createPlaceNullTest(){
        placeFacade.createPlace(null);
    }
}
