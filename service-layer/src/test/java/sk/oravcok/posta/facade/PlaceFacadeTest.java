package sk.oravcok.posta.facade;

import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dto.PlaceCreateDTO;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.dto.PlaceUpdateDTO;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.mapping.BeanMappingServiceImpl;
import sk.oravcok.posta.service.PlaceService;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private Place window2;
    private Place back3;

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

        window2 = new Place(2l);
        window2.setName("Marshall");
        window2.setPlaceType(PlaceType.WINDOW);
        window2.setAnnotation("Main CEO office");

        back3 = new Place(3l);
        back3.setName("Firehouse");
        back3.setPlaceType(PlaceType.BACKGROUND);
        back3.setAnnotation(null);
    }

    @BeforeMethod(dependsOnMethods = "initEntities")
    public void setUpMockitoBehaviour(){
        //findById
        when(placeService.findById(0l)).thenReturn(null);
        when(placeService.findById(1l)).thenReturn(window1);
        when(placeService.findById(2l)).thenReturn(window2);
        when(placeService.findById(3l)).thenReturn(back3);

        //findByName
        when(placeService.findByName("not-existing")).thenReturn(null);
        when(placeService.findByName("Window 1")).thenReturn(window1);
        when(placeService.findByName("Marshall")).thenReturn(window2);
        when(placeService.findByName("Firehouse")).thenReturn(back3);
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
    public void createPlaceNullTest() {
        placeFacade.createPlace(null);
    }

    @Test
    public void updatePlaceTest() {
        PlaceUpdateDTO window18 = new PlaceUpdateDTO();
        window18.setId(1l); //mock set to return window1
        window18.setName("Window 18");
        window18.setPlaceType(PlaceType.WINDOW);
        window18.setAnnotation("behind the doors");

        placeFacade.updatePlace(window18);
        verify(placeService).update(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getId(), Long.valueOf(1l));
        assertEquals(argumentCaptor.getValue().getName(), "Window 18");
        assertEquals(argumentCaptor.getValue().getPlaceType(), PlaceType.WINDOW);
        assertEquals(argumentCaptor.getValue().getAnnotation(), "behind the doors");
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void updateNonExistingPlaceTest() {
        PlaceUpdateDTO window18 = new PlaceUpdateDTO();
        window18.setId(0l); //mock set to return NULL
        window18.setName("Window 18");
        window18.setPlaceType(PlaceType.WINDOW);
        window18.setAnnotation("behind the doors");

        placeFacade.updatePlace(window18);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullPlaceTest() {
        placeFacade.updatePlace(null);
    }

    @Test
    public void removePlaceTest() {
        placeFacade.removePlace(1l);    //mock set to return window1
        verify(placeService).remove(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getId(), Long.valueOf(1l));
        assertEquals(argumentCaptor.getValue().getName(), window1.getName());
        assertEquals(argumentCaptor.getValue().getPlaceType(), window1.getPlaceType());
        assertEquals(argumentCaptor.getValue().getAnnotation(), window1.getAnnotation());
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void removeNonExistingPlaceTest() {
        placeFacade.removePlace(0l);    //mock set to return non-existing
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullPlaceTest() {
        placeFacade.removePlace(null);
    }

    @Test
    public void getPlaceById() {
        PlaceDTO place = placeFacade.findPlaceById(1l);
        assertEquals(place.getId(), Long.valueOf(1l));
        assertEquals(place.getName(), "Window 1");
        assertEquals(place.getPlaceType(), PlaceType.WINDOW);
        assertEquals(place.getAnnotation(), "Main personal office");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getPlaceByNullIdTest() {
        placeFacade.findPlaceById(null);
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void getNonExistingPlaceByIdTest() {
        placeFacade.findPlaceById(0l);
    }

    @Test
    public void getPlaceByNameTest() {
        PlaceDTO place = placeFacade.findPlaceByName("Window 1");
        assertEquals(place.getId(), Long.valueOf(1l));
        assertEquals(place.getName(), "Window 1");
        assertEquals(place.getPlaceType(), PlaceType.WINDOW);
        assertEquals(place.getAnnotation(), "Main personal office");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getPlaceByNullNameTest() {
        placeFacade.findPlaceByName(null);
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void getNonExistingPlaceByNameTest() {
        placeFacade.findPlaceByName("non-existing");
    }

    @Test
    public void getAllPlacesTest() {
        List<Place> placeList = Arrays.asList(window1, window2, back3);
        when(placeService.findAll()).thenReturn(placeList);

        List<PlaceDTO> placeDtoList = placeFacade.findAllPlaces();
        assertEquals(placeDtoList.size(), 3);

        for(int i = 0; i < 3; i++) {
            Place place = placeList.get(i);
            PlaceDTO placeDTO = placeDtoList.get(i);
            assertEquals(place.getId(), placeDTO.getId());
            assertEquals(place.getName(), placeDTO.getName());
            assertEquals(place.getAnnotation(), placeDTO.getAnnotation());
            assertEquals(place.getPlaceType(), placeDTO.getPlaceType());
        }
    }

    @Test
    public void getAllPlacesEmptyTest() {
        when(placeService.findAll()).thenReturn(new ArrayList<Place>());

        List<PlaceDTO> dtoList = placeFacade.findAllPlaces();
        assertNotNull(dtoList);
        assertEquals(dtoList.size(), 0);
    }

}
