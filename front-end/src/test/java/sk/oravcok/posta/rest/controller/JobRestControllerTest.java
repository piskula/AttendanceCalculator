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
import sk.oravcok.posta.dto.EmployeeDTO;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.dto.PlaceDTO;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.JobFacade;
import sk.oravcok.posta.rest.configuration.RestContextConfiguration;
import sk.oravcok.posta.rest.configuration.URI;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author Ondrej Oravcok
 * @version 22-Jan-17.
 */
@WebAppConfiguration
@ContextConfiguration(classes = RestContextConfiguration.class)
public class JobRestControllerTest extends AbstractTestNGSpringContextTests {

    @Mock
    private JobFacade jobFacade;

    @Inject
    @InjectMocks
    private JobRestController jobRestController;

    private MockMvc mockMvc;

    private EmployeeDTO webber;
    private EmployeeDTO rosberg;
    private PlaceDTO window;
    private LocalDate monday;
    private LocalDate tuesday;

    private JobDTO mondayWindowRosberg;
    private JobDTO tuesdayWindowRosberg;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(jobRestController).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @BeforeMethod
    public void initJobs() {
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

        window = new PlaceDTO();
        window.setId(1l);
        window.setName("Window 1");
        window.setPlaceType(PlaceType.WINDOW);
        window.setAnnotation("Window number 1");

        monday = LocalDate.of(2017, 1, 23);
        tuesday = LocalDate.of(2017, 1, 24);

        mondayWindowRosberg = new JobDTO();
        mondayWindowRosberg.setId(1l);
        mondayWindowRosberg.setJobDate(monday);
        mondayWindowRosberg.setPlace(window);
        mondayWindowRosberg.setEmployee(rosberg);
        mondayWindowRosberg.setJobStart(LocalTime.of(12, 20));
        mondayWindowRosberg.setJobEnd(LocalTime.of(16, 30));

        tuesdayWindowRosberg = new JobDTO();
        tuesdayWindowRosberg.setId(2l);
        tuesdayWindowRosberg.setJobDate(tuesday);
        tuesdayWindowRosberg.setPlace(window);
        tuesdayWindowRosberg.setEmployee(rosberg);
        tuesdayWindowRosberg.setJobStart(LocalTime.of(12, 20));
        tuesdayWindowRosberg.setJobEnd(LocalTime.of(16, 30));
    }

    @BeforeMethod(dependsOnMethods = "initJobs")
    public void initMocksBehaviour() {
        //findJobById
        doThrow(new NonExistingEntityException("Job with id 0 does not exist")).when(jobFacade).findJobById(0l);
        when(jobFacade.findJobById(1l)).thenReturn(mondayWindowRosberg);
        when(jobFacade.findJobById(2l)).thenReturn(tuesdayWindowRosberg);

        //findAllJobs
        when(jobFacade.findAllJobs()).thenReturn(Arrays.asList(mondayWindowRosberg, tuesdayWindowRosberg));
    }

    @Test
    public void findJobByIdTest() throws Exception {
        mockMvc.perform(get(URI.JOBS + "/findId/" + mondayWindowRosberg.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.place").value(mondayWindowRosberg.getPlace()))
//                .andExpect(jsonPath("$.employee").value(mondayWindowRosberg.getEmployee()))
                .andExpect(jsonPath("$.employee.id").value(mondayWindowRosberg.getEmployee().getId()))
                .andExpect(jsonPath("$.employee.name").value(mondayWindowRosberg.getEmployee().getName()))
                .andExpect(jsonPath("$.employee.surname").value(mondayWindowRosberg.getEmployee().getSurname()))
                .andExpect(jsonPath("$.employee.title").value(mondayWindowRosberg.getEmployee().getTitle()))
                .andExpect(jsonPath("$.employee.email").value(mondayWindowRosberg.getEmployee().getEmail()))
                .andExpect(jsonPath("$.employee.address").value(mondayWindowRosberg.getEmployee().getAddress()))
                .andExpect(jsonPath("$.employee.birth.[0]").value(mondayWindowRosberg.getEmployee().getBirth().getYear()))
                .andExpect(jsonPath("$.employee.birth.[1]").value(mondayWindowRosberg.getEmployee().getBirth().getMonthValue()))
                .andExpect(jsonPath("$.employee.birth.[2]").value(mondayWindowRosberg.getEmployee().getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.employee.phone").value(mondayWindowRosberg.getEmployee().getPhone()))
                .andExpect(jsonPath("$.employee.annotation").value(mondayWindowRosberg.getEmployee().getAnnotation()));


        mockMvc.perform(get(URI.JOBS + "/findId/" + tuesdayWindowRosberg.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobDate.[0]").value(tuesdayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(tuesdayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(tuesdayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value(tuesdayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(tuesdayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value(tuesdayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(tuesdayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.place").value(tuesdayWindowRosberg.getPlace()))
//                .andExpect(jsonPath("$.employee").value(tuesdayWindowRosberg.getEmployee()))
                .andExpect(jsonPath("$.employee.id").value(tuesdayWindowRosberg.getEmployee().getId()))
                .andExpect(jsonPath("$.employee.name").value(tuesdayWindowRosberg.getEmployee().getName()))
                .andExpect(jsonPath("$.employee.surname").value(tuesdayWindowRosberg.getEmployee().getSurname()))
                .andExpect(jsonPath("$.employee.title").value(tuesdayWindowRosberg.getEmployee().getTitle()))
                .andExpect(jsonPath("$.employee.email").value(tuesdayWindowRosberg.getEmployee().getEmail()))
                .andExpect(jsonPath("$.employee.address").value(tuesdayWindowRosberg.getEmployee().getAddress()))
                .andExpect(jsonPath("$.employee.birth.[0]").value(tuesdayWindowRosberg.getEmployee().getBirth().getYear()))
                .andExpect(jsonPath("$.employee.birth.[1]").value(tuesdayWindowRosberg.getEmployee().getBirth().getMonthValue()))
                .andExpect(jsonPath("$.employee.birth.[2]").value(tuesdayWindowRosberg.getEmployee().getBirth().getDayOfMonth()))
                .andExpect(jsonPath("$.employee.phone").value(tuesdayWindowRosberg.getEmployee().getPhone()))
                .andExpect(jsonPath("$.employee.annotation").value(tuesdayWindowRosberg.getEmployee().getAnnotation()));
    }

}
