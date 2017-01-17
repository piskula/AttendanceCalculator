package sk.oravcok.posta.rest.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import sk.oravcok.posta.dto.EmployeeCreateDTO;
import sk.oravcok.posta.dto.EmployeeDTO;
import sk.oravcok.posta.dto.FindTextDTO;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.EmployeeFacade;
import sk.oravcok.posta.rest.configuration.RestContextConfiguration;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.ValidationException;

import javax.inject.Inject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Tests for EmployeeRestController
 *
 * @author Ondrej Oravcok
 * @version 15-Jan-17.
 */
@WebAppConfiguration
@ContextConfiguration(classes = RestContextConfiguration.class)
public class EmployeeRestControllerTest extends AbstractTestNGSpringContextTests {

    @Mock
    private EmployeeFacade employeeFacade;

    @Inject
    @InjectMocks
    private EmployeeRestController employeeRestController;

    private MockMvc mockMvc;

    private EmployeeDTO webber;
    private EmployeeDTO rosberg;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(employeeRestController).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @BeforeMethod
    public void initEmployees() {
        webber = new EmployeeDTO();
        webber.setId(1l);
        webber.setName("Mark");
        webber.setSurname("Webber");
        webber.setTitle("Mr.");
        webber.setEmail("mark.webber@porsche.com");
        webber.setAddress("Imola Park, Italy");
        webber.setBirth(LocalDate.of(1976, 8, 27));
        webber.setPhone("+61 491 570 110");
        webber.setAnnotation("very good driver");

        rosberg = new EmployeeDTO();
        rosberg.setId(2l);
        rosberg.setName("Nico");
        rosberg.setSurname("Rosberg");
        rosberg.setTitle("Mr.");
        rosberg.setEmail("nico.rosberg@mercedes.de");
        rosberg.setAddress("Monte Carlo, Monaco");
        rosberg.setBirth(LocalDate.of(1985, 6, 27));
    }

    @BeforeMethod(dependsOnMethods = "initEmployees")
    public void initMocksBehaviour() {
        //findById
        doThrow(new NonExistingEntityException("Employee with id=0 does not exist")).when(employeeFacade).findEmployeeById(0l);
        when(employeeFacade.findEmployeeById(1l)).thenReturn(webber);
        when(employeeFacade.findEmployeeById(2l)).thenReturn(rosberg);

        //findByName
        doThrow(new NonExistingEntityException("Employee with name=non-existing does not exist")).when(employeeFacade).findEmployeesByKey("non-existing");
        when(employeeFacade.findEmployeesByKey("Mark Webber")).thenReturn(Arrays.asList(webber));
        when(employeeFacade.findEmployeesByKey("Nico Rosberg")).thenReturn(Arrays.asList(rosberg));
        when(employeeFacade.findEmployeesByKey("er")).thenReturn(Arrays.asList(webber, rosberg));

        //findAllEmployees
        when(employeeFacade.findAllEmployees()).thenReturn(Arrays.asList(webber, rosberg));
    }

    @Test
    public void findEmployeeByIdTest() throws Exception {

        mockMvc.perform(get(URI.EMPLOYEES + "/findId/" + webber.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(webber.getName()))
                .andExpect(jsonPath("$.surname").value(webber.getSurname()))
                .andExpect(jsonPath("$.title").value(webber.getTitle()))
                .andExpect(jsonPath("$.email").value(webber.getEmail()))
                .andExpect(jsonPath("$.address").value(webber.getAddress()))
                .andExpect(jsonPath("$.birth.[0]").value(webber.getBirth().getYear()))
                .andExpect(jsonPath("$.birth.[1]").value(webber.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.birth.[2]").value(webber.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.phone").value(webber.getPhone()))
                .andExpect(jsonPath("$.annotation").value(webber.getAnnotation()));

        mockMvc.perform(get(URI.EMPLOYEES + "/findId/" + rosberg.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(rosberg.getName()))
                .andExpect(jsonPath("$.surname").value(rosberg.getSurname()))
                .andExpect(jsonPath("$.title").value(rosberg.getTitle()))
                .andExpect(jsonPath("$.email").value(rosberg.getEmail()))
                .andExpect(jsonPath("$.address").value(rosberg.getAddress()))
                .andExpect(jsonPath("$.birth.[0]").value(rosberg.getBirth().getYear()))
                .andExpect(jsonPath("$.birth.[1]").value(rosberg.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.birth.[2]").value(rosberg.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.phone").value(rosberg.getPhone()))
                .andExpect(jsonPath("$.annotation").value(rosberg.getAnnotation()));
    }

    @Test
    public void findNonExistingEmployeeById() throws Exception {
        mockMvc.perform(get(URI.EMPLOYEES + "/findId/0"))
                .andExpect(status().is(404));
    }

    @Test
    public void findEmployeesByKeyTest() throws Exception {

        FindTextDTO searchFor = new FindTextDTO();
        searchFor.setText("er");

        mockMvc.perform(get(URI.EMPLOYEES + "/find").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(searchFor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].name").value(webber.getName()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].surname").value(webber.getSurname()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].title").value(webber.getTitle()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].email").value(webber.getEmail()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].address").value(webber.getAddress()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].birth.[0]").value(webber.getBirth().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].birth.[1]").value(webber.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].birth.[2]").value(webber.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].phone").value(webber.getPhone()))
                .andExpect(jsonPath("$.[?(@.id==" + webber.getId() + ")].annotation").value(webber.getAnnotation()))

                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].name").value(rosberg.getName()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].surname").value(rosberg.getSurname()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].title").value(rosberg.getTitle()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].email").value(rosberg.getEmail()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].address").value(rosberg.getAddress()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].birth.[0]").value(rosberg.getBirth().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].birth.[1]").value(rosberg.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].birth.[2]").value(rosberg.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].phone").value(rosberg.getPhone()))
                .andExpect(jsonPath("$.[?(@.id==" + rosberg.getId() + ")].annotation").value(rosberg.getAnnotation()));
    }

    @Test
    public void findEmployeesByEmptyKeyTest() throws Exception {
        FindTextDTO findEmpty = new FindTextDTO();
        findEmpty.setText("");

        mockMvc.perform(get(URI.EMPLOYEES + "/find").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findEmpty)))
                .andExpect(status().is(422));
    }

    @Test
    public void findEmployeesByNullKeyTest() throws Exception {
        FindTextDTO findNull = new FindTextDTO();
        findNull.setText(null);

        mockMvc.perform(get(URI.EMPLOYEES + "/find").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(findNull)))
                .andExpect(status().is(422));
    }

    @Test
    public void createEmployeeTest() throws Exception {
        EmployeeCreateDTO vettelCreateDto = new EmployeeCreateDTO();
        vettelCreateDto.setName("Sebastian");
        vettelCreateDto.setSurname("Vettel");
        vettelCreateDto.setTitle("Mr.");
        vettelCreateDto.setAddress("134 Zurich, Switzerland");
        vettelCreateDto.setEmail("seb@ferrari.it");
        vettelCreateDto.setPhone("+9 67 67 34 897");
        vettelCreateDto.setBirth(LocalDate.of(1987, 7, 3));
        vettelCreateDto.setAnnotation("4 times World Champion");

        EmployeeDTO vettelDto = new EmployeeDTO();
        vettelDto.setName("Sebastian");
        vettelDto.setSurname("Vettel");
        vettelDto.setTitle("Mr.");
        vettelDto.setAddress("134 Zurich, Switzerland");
        vettelDto.setEmail("seb@ferrari.it");
        vettelDto.setPhone("+9 67 67 34 897");
        vettelDto.setBirth(LocalDate.of(1987, 7, 3));
        vettelDto.setAnnotation("4 times World Champion");

        doReturn(367l).when(employeeFacade).createEmployee(vettelCreateDto);
        doReturn(vettelDto).when(employeeFacade).findEmployeeById(367l);

        mockMvc.perform(post(URI.EMPLOYEES).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(vettelCreateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(vettelDto.getName()))
                .andExpect(jsonPath("$.surname").value(vettelDto.getSurname()))
                .andExpect(jsonPath("$.title").value(vettelDto.getTitle()))
                .andExpect(jsonPath("$.email").value(vettelDto.getEmail()))
                .andExpect(jsonPath("$.address").value(vettelDto.getAddress()))
                .andExpect(jsonPath("$.birth.[0]").value(vettelDto.getBirth().getYear()))
                .andExpect(jsonPath("$.birth.[1]").value(vettelDto.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.birth.[2]").value(vettelDto.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.phone").value(vettelDto.getPhone()))
                .andExpect(jsonPath("$.annotation").value(vettelDto.getAnnotation()));
    }

    @Test
    public void createIncompleteEmployeeTest() throws Exception {
        EmployeeCreateDTO vettelCreateDto = new EmployeeCreateDTO();
        vettelCreateDto.setSurname("Vettel");

        doThrow(new ValidationException("DataManipulationException thrown while creating employee."))
                .when(employeeFacade).createEmployee(vettelCreateDto);

        mockMvc.perform(post(URI.EMPLOYEES).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(vettelCreateDto)))
                .andExpect(status().is(422));
    }

    @Test
    public void updateEmployeeTest() throws Exception {
        EmployeeDTO webberUpdate = new EmployeeDTO();
        webberUpdate.setName("Mark2");
        webberUpdate.setSurname("Webber2");
        webberUpdate.setTitle("Mr.2");
        webberUpdate.setEmail("mark.webber2@porsche.com");
        webberUpdate.setAddress("Imola Park, Italy2");
        webberUpdate.setBirth(LocalDate.of(1977, 9, 28));
        webberUpdate.setPhone("+61 491 570 111");
        webberUpdate.setAnnotation("very good driver2");

        mockMvc.perform(post(URI.EMPLOYEES + "/update/" + webber.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(webberUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(webberUpdate.getName()))
                .andExpect(jsonPath("$.surname").value(webberUpdate.getSurname()))
                .andExpect(jsonPath("$.title").value(webberUpdate.getTitle()))
                .andExpect(jsonPath("$.email").value(webberUpdate.getEmail()))
                .andExpect(jsonPath("$.address").value(webberUpdate.getAddress()))
                .andExpect(jsonPath("$.birth.[0]").value(webberUpdate.getBirth().getYear()))
                .andExpect(jsonPath("$.birth.[1]").value(webberUpdate.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.birth.[2]").value(webberUpdate.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.phone").value(webberUpdate.getPhone()))
                .andExpect(jsonPath("$.annotation").value(webberUpdate.getAnnotation()));
    }

    @Test
    public void updateNamePartialPlaceTest() throws Exception {
        EmployeeDTO webberUpdate = new EmployeeDTO();
        webberUpdate.setName("Webber updated");

        mockMvc.perform(post(URI.EMPLOYEES + "/update/" + webber.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(webberUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(webberUpdate.getName()))
                .andExpect(jsonPath("$.surname").value(webber.getSurname()))
                .andExpect(jsonPath("$.title").value(webber.getTitle()))
                .andExpect(jsonPath("$.email").value(webber.getEmail()))
                .andExpect(jsonPath("$.address").value(webber.getAddress()))
                .andExpect(jsonPath("$.birth.[0]").value(webber.getBirth().getYear()))
                .andExpect(jsonPath("$.birth.[1]").value(webber.getBirth().getMonthValue()))
                .andExpect(jsonPath("$.birth.[2]").value(webber.getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.phone").value(webber.getPhone()))
                .andExpect(jsonPath("$.annotation").value(webber.getAnnotation()));
    }

    @Test
    public void deleteEmployeeTest() throws Exception {
        doNothing().when(employeeFacade).removeEmployee(webber.getId());
        mockMvc.perform(delete(URI.EMPLOYEES + "/" + webber.getId())).andExpect(status().isOk());
    }

    @Test
    public void deleteNotExistingEmployeeTest() throws Exception {
        doThrow(new NonExistingEntityException("Employee with id=0 does not exist in system.")).when(employeeFacade).removeEmployee(0l);
        mockMvc.perform(delete(URI.EMPLOYEES + "/0")).andExpect(status().is(404));
    }

    private static String convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }

}
