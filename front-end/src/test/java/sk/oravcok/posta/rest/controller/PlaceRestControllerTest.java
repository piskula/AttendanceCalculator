package sk.oravcok.posta.rest.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.facade.PlaceFacade;
import sk.oravcok.posta.rest.configuration.RestContextConfiguration;
import sk.oravcok.posta.rest.configuration.URI;

import javax.inject.Inject;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    }

    @BeforeMethod(dependsOnMethods = "initPlaces")
    public void initMocksBehaviour() {
        //findById
        when(placeFacade.findPlaceById(1l)).thenReturn(window1);
    }

    @Test
    public void ffindPlaceById() throws Exception {

        mockMvc.perform(get(URI.PLACES + "/" + window1.getId()))
                .andExpect(status().isOk());

    }

}
