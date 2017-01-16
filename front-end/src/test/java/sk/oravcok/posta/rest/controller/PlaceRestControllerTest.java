package sk.oravcok.posta.rest.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.dto.FindTextDTO;
import sk.oravcok.posta.dto.PlaceCreateDTO;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.DataManipulationException;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.PlaceFacade;
import sk.oravcok.posta.rest.configuration.RestContextConfiguration;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.ValidationException;

import javax.inject.Inject;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Tests for PlaceRestController
 *
 * @author Ondrej Oravcok
 * @version 26-Dec-16.
 */
@WebAppConfiguration
@ContextConfiguration(classes = RestContextConfiguration.class)
public class PlaceRestControllerTest extends AbstractTestNGSpringContextTests {

    @Mock
    private PlaceFacade placeFacade;

    @Inject
    @InjectMocks
    private PlaceRestController placeRestController;

    private MockMvc mockMvc;

    private PlaceDTO window1;
    private PlaceDTO window2;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(placeRestController).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @BeforeMethod
    public void initPlaces() {
        window1 = new PlaceDTO();
        window1.setId(1l);
        window1.setName("Window 1");
        window1.setPlaceType(PlaceType.WINDOW);
        window1.setAnnotation("Window number 1");

        window2 = new PlaceDTO();
        window2.setId(2l);
        window2.setName("Window 2");
        window2.setPlaceType(PlaceType.WINDOW);
    }

    @BeforeMethod(dependsOnMethods = "initPlaces")
    public void initMocksBehaviour() {
        //findById
        doThrow(new NonExistingEntityException("Place with id=0 does not exist")).when(placeFacade).findPlaceById(0l);
        when(placeFacade.findPlaceById(1l)).thenReturn(window1);
        when(placeFacade.findPlaceById(2l)).thenReturn(window2);

        //findByName
        doThrow(new NonExistingEntityException("Place with name=non-existing does not exist")).when(placeFacade).findPlaceByName("non-existing");
        when(placeFacade.findPlaceByName("Window 1")).thenReturn(window1);
        when(placeFacade.findPlaceByName("Window 2")).thenReturn(window2);

        //findAllPlaces
        when(placeFacade.findAllPlaces()).thenReturn(Arrays.asList(window1, window2));
    }

    @Test
    public void findPlaceByIdTest() throws Exception {

        mockMvc.perform(get(URI.PLACES + "/findId/" + window1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window1.getName()))
                .andExpect(jsonPath("$.placeType").value(window1.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window1.getAnnotation()));

        mockMvc.perform(get(URI.PLACES + "/findId/" + window2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window2.getName()))
                .andExpect(jsonPath("$.placeType").value(window2.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window2.getAnnotation()));
    }

    @Test
    public void findPlaceByNameTest() throws Exception {

        FindTextDTO findWindow1 = new FindTextDTO();
        FindTextDTO findWindow2 = new FindTextDTO();
        findWindow1.setText(window1.getName());
        findWindow2.setText(window2.getName());

        mockMvc.perform(get(URI.PLACES + "/findName").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findWindow1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window1.getName()))
                .andExpect(jsonPath("$.placeType").value(window1.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window1.getAnnotation()));

        mockMvc.perform(get(URI.PLACES + "/findName").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findWindow2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window2.getName()))
                .andExpect(jsonPath("$.placeType").value(window2.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window2.getAnnotation()));
    }

    @Test
    public void findNonExistingPlaceByIdTest() throws Exception {
        mockMvc.perform(get(URI.PLACES + "/findId/0"))
                .andExpect(status().is(404));
    }

    @Test
    public void findNonExistingPlaceByNameTest() throws Exception {
        FindTextDTO findNonExisting = new FindTextDTO();
        findNonExisting.setText("non-existing");

        mockMvc.perform(get(URI.PLACES + "/findName").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findNonExisting)))
                .andExpect(status().is(404));
    }

    @Test
    public void findPlaceByEmptyNameTest() throws Exception {
        FindTextDTO findEmpty = new FindTextDTO();
        findEmpty.setText("");

        mockMvc.perform(get(URI.PLACES + "/findName").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findEmpty)))
                .andExpect(status().is(422));
    }

    @Test
    public void findPlaceByNullNameTest() throws Exception {
        FindTextDTO findNull = new FindTextDTO();
        findNull.setText(null);

        mockMvc.perform(get(URI.PLACES + "/findName").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findNull)))
                .andExpect(status().is(422));
    }

    private static String convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    @Test
    public void findAllPlacesTest() throws Exception {
        mockMvc.perform(get(URI.PLACES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + window1.getId() + ")].name").value(window1.getName()))
                .andExpect(jsonPath("$.[?(@.id==" + window1.getId() + ")].placeType").value(window1.getPlaceType().toString()))
                .andExpect(jsonPath("$.[?(@.id==" + window1.getId() + ")].annotation").value(window1.getAnnotation()))

                .andExpect(jsonPath("$.[?(@.id==" + window2.getId() + ")].name").value(window2.getName()))
                .andExpect(jsonPath("$.[?(@.id==" + window2.getId() + ")].placeType").value(window2.getPlaceType().toString()))
                .andExpect(jsonPath("$.[?(@.id==" + window2.getId() + ")].annotation").value(window2.getAnnotation()));
    }

    @Test
    public void createPlaceTest() throws Exception {
        PlaceCreateDTO backgroundCreateDto = new PlaceCreateDTO();
        backgroundCreateDto.setName("Background 1");
        backgroundCreateDto.setPlaceType(PlaceType.BACKGROUND);
        backgroundCreateDto.setAnnotation("Background - nothing to do");

        PlaceDTO backgroundDto = new PlaceDTO();
        backgroundDto.setName("Background 1");
        backgroundDto.setPlaceType(PlaceType.BACKGROUND);
        backgroundDto.setAnnotation("Background - nothing to do");

        doReturn(367l).when(placeFacade).createPlace(backgroundCreateDto);
        doReturn(backgroundDto).when(placeFacade).findPlaceById(367l);

        mockMvc.perform(post(URI.PLACES).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(backgroundCreateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(backgroundDto.getName()))
                .andExpect(jsonPath("$.placeType").value(backgroundDto.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(backgroundDto.getAnnotation()));
    }

    @Test
    public void createIncompletePlaceTest() throws Exception {
        PlaceCreateDTO backgroundCreateDto = new PlaceCreateDTO();
        backgroundCreateDto.setPlaceType(PlaceType.BACKGROUND);
        backgroundCreateDto.setAnnotation("Background - nothing to do");

        doThrow(new ValidationException("DataManipulationException thrown while creating place."))
                .when(placeFacade).createPlace(backgroundCreateDto);

        mockMvc.perform(post(URI.PLACES).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(backgroundCreateDto)))
                .andExpect(status().is(422));
    }

    @Test
    public void updatePlaceTest() throws Exception {
        PlaceDTO window1Update = new PlaceDTO();
        window1Update.setName("Window 1 updated to background");
        window1Update.setPlaceType(PlaceType.BACKGROUND);
        window1Update.setAnnotation("Window number 1 updated to background");

        mockMvc.perform(post(URI.PLACES + "/update/" + window1.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(window1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window1Update.getName()))
                .andExpect(jsonPath("$.placeType").value(window1Update.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window1Update.getAnnotation()));
    }

    @Test
    public void updateNamePartialPlaceTest() throws Exception {
        PlaceDTO window1Update = new PlaceDTO();
        window1Update.setName("Window 1 updated");

        mockMvc.perform(post(URI.PLACES + "/update/" + window1.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(window1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window1Update.getName()))
                .andExpect(jsonPath("$.placeType").value(window1.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window1.getAnnotation()));
    }

    @Test
    public void updateTypePartialPlaceTest() throws Exception {
        PlaceDTO window1Update = new PlaceDTO();
        window1Update.setPlaceType(PlaceType.BACKGROUND);

        mockMvc.perform(post(URI.PLACES + "/update/" + window1.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(window1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window1.getName()))
                .andExpect(jsonPath("$.placeType").value(window1Update.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window1.getAnnotation()));
    }

    @Test
    public void updateAnnotationPartialPlaceTest() throws Exception {
        PlaceDTO window1Update = new PlaceDTO();
        window1Update.setAnnotation("Window number 1 updated");

        mockMvc.perform(post(URI.PLACES + "/update/" + window1.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(window1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(window1.getName()))
                .andExpect(jsonPath("$.placeType").value(window1.getPlaceType().toString()))
                .andExpect(jsonPath("$.annotation").value(window1Update.getAnnotation()));
    }

    @Test
    public void deletePlaceTest() throws Exception {
        doNothing().when(placeFacade).removePlace(window1.getId());
        mockMvc.perform(delete(URI.PLACES + "/" + window1.getId())).andExpect(status().isOk());
    }

    @Test
    public void deleteNotExistingPlaceTest() throws Exception {
        doThrow(new NonExistingEntityException("Place with id=0 does not exist in system.")).when(placeFacade).removePlace(0l);
        mockMvc.perform(delete(URI.PLACES + "/0")).andExpect(status().is(404));
    }

}
