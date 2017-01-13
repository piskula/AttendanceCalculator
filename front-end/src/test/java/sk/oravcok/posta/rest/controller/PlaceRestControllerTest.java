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
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.PlaceFacade;
import sk.oravcok.posta.rest.configuration.RestContextConfiguration;
import sk.oravcok.posta.rest.configuration.URI;

import javax.inject.Inject;

import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

//    @BeforeClass
//    public void setUpMockito() {
//        MockitoAnnotations.initMocks(this);
//    }

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

        doThrow(new NonExistingEntityException("Place with name=non-existing does not exist")).when(placeFacade).findPlaceByName("non-existing");
        when(placeFacade.findPlaceByName("Window 1")).thenReturn(window1);
        when(placeFacade.findPlaceByName("Window 2")).thenReturn(window2);
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
        findWindow1.setName(window1.getName());
        findWindow2.setName(window2.getName());

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
    public void findNonExistingPlaceById() throws Exception {
        mockMvc.perform(get(URI.PLACES + "/findId/0"))
                .andExpect(status().is(404));
    }

    @Test
    public void findNonExistingPlaceByName() throws Exception {
        FindTextDTO findNonExisting = new FindTextDTO();
        findNonExisting.setName("non-existing");
        
        mockMvc.perform(get(URI.PLACES + "/findName").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findNonExisting)))
                .andExpect(status().is(404));
    }

    private static String convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

}
