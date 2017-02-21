package sk.oravcok.posta.rest.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import sk.oravcok.posta.dto.*;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.JobFacade;
import sk.oravcok.posta.rest.configuration.RestContextConfiguration;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.ValidationException;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    public void findNonExistingJobByIdTest() throws Exception {
        mockMvc.perform(get(URI.JOBS + "/findId/0"))
                .andExpect(status().is(404));
    }

    @Test
    public void findAllJobsTest() throws Exception {
        mockMvc.perform(get(URI.JOBS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[0]").value(tuesdayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[1]").value(tuesdayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[2]").value(tuesdayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[0]").value(tuesdayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[1]").value(tuesdayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[0]").value(tuesdayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[1]").value(tuesdayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].place.id").value(tuesdayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].employee.id").value(tuesdayWindowRosberg.getEmployee().getId().intValue()))

                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

    @Test
    public void createJobTest() throws Exception {
        JobCreateDTO jobCreateDTO = new JobCreateDTO();
        jobCreateDTO.setEmployeeId(webber.getId());
        jobCreateDTO.setPlaceId(window.getId());
        jobCreateDTO.setJobDate(LocalDate.of(2017, 1, 27));
        jobCreateDTO.setJobStart(LocalTime.of(6, 20));
        jobCreateDTO.setJobEnd(LocalTime.of(12, 10));

        JobDTO jobDto = new JobDTO();
        jobDto.setEmployee(webber);
        jobDto.setPlace(window);
        jobDto.setJobDate(LocalDate.of(2017, 1, 27));
        jobDto.setJobStart(LocalTime.of(6, 20));
        jobDto.setJobEnd(LocalTime.of(12, 10));

        doReturn(273l).when(jobFacade).createJob(jobCreateDTO);
        doReturn(jobDto).when(jobFacade).findJobById(273l);

        mockMvc.perform(post(URI.JOBS).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(jobCreateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.id").value(jobDto.getEmployee().getId().intValue()))
                .andExpect(jsonPath("$.place").value(jobDto.getPlace()))
                .andExpect(jsonPath("$.jobDate.[0]").value(jobDto.getJobDate().getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(jobDto.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(jobDto.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value(jobDto.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(jobDto.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value(jobDto.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(jobDto.getJobEnd().getMinute()));
    }

    @Test
    public void createMissingEmployeeJobTest() throws Exception {
        JobCreateDTO jobCreateDTO = new JobCreateDTO();
        //missing employeeId or other NotNull parameter
        jobCreateDTO.setPlaceId(window.getId());
        jobCreateDTO.setJobDate(LocalDate.of(2017, 1, 27));
        jobCreateDTO.setJobStart(LocalTime.of(6, 20));
        jobCreateDTO.setJobEnd(LocalTime.of(12, 10));

        doThrow(new ValidationException("DataManipulationException thrown while creating job."))
                .when(jobFacade).createJob(jobCreateDTO);

        mockMvc.perform(post(URI.JOBS).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(jobCreateDTO)))
                .andExpect(status().is(422));
    }

    @Test
    public void updateJobTest() throws Exception {
        JobUpdateDTO job1Update = new JobUpdateDTO();   //job1 = Monday, Window(id=1), Rosberg(id=2), date
        job1Update.setEmployeeId(1l);   //set to Webber
        job1Update.setPlaceId(2l);  //not set
        job1Update.setJobDate(tuesday);
        job1Update.setJobStart(LocalTime.of(18, 50));
        job1Update.setJobEnd(LocalTime.of(19, 10));

        mockMvc.perform(post(URI.JOBS + "/update/" + mondayWindowRosberg.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(job1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(webber.getId().intValue()))
                .andExpect(jsonPath("$.placeId").value("2"))
                .andExpect(jsonPath("$.jobDate.[0]").value(tuesday.getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(tuesday.getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(tuesday.getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value("18"))
                .andExpect(jsonPath("$.jobStart.[1]").value("50"))
                .andExpect(jsonPath("$.jobEnd.[0]").value("19"))
                .andExpect(jsonPath("$.jobEnd.[1]").value("10"));
    }

    @Test
    public void updatePartialJobEndTest() throws Exception {
        JobUpdateDTO job1Update = new JobUpdateDTO();   //job1 = Monday, Window(id=1), Rosberg(id=2), date
        job1Update.setJobEnd(LocalTime.of(19, 10));

        mockMvc.perform(post(URI.JOBS + "/update/" + mondayWindowRosberg.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(job1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(mondayWindowRosberg.getEmployee().getId().intValue()))
                .andExpect(jsonPath("$.placeId").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.jobDate.[0]").value(monday.getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(monday.getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(monday.getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value("19"))
                .andExpect(jsonPath("$.jobEnd.[1]").value("10"));
    }

    @Test
    public void updatePartialJobStartTest() throws Exception {
        JobUpdateDTO job1Update = new JobUpdateDTO();   //job1 = Monday, Window(id=1), Rosberg(id=2), date
        job1Update.setJobStart(LocalTime.of(4, 40));

        mockMvc.perform(post(URI.JOBS + "/update/" + mondayWindowRosberg.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(job1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(mondayWindowRosberg.getEmployee().getId().intValue()))
                .andExpect(jsonPath("$.placeId").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.jobDate.[0]").value(monday.getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(monday.getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(monday.getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value("4"))
                .andExpect(jsonPath("$.jobStart.[1]").value("40"))
                .andExpect(jsonPath("$.jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()));
    }

    @Test
    public void updatePartialJobDateTest() throws Exception {
        JobUpdateDTO job1Update = new JobUpdateDTO();   //job1 = Monday, Window(id=1), Rosberg(id=2), date
        job1Update.setJobDate(LocalDate.of(2017, 1, 27));

        mockMvc.perform(post(URI.JOBS + "/update/" + mondayWindowRosberg.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(job1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(mondayWindowRosberg.getEmployee().getId().intValue()))
                .andExpect(jsonPath("$.placeId").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.jobDate.[0]").value("2017"))
                .andExpect(jsonPath("$.jobDate.[1]").value("1"))
                .andExpect(jsonPath("$.jobDate.[2]").value("27"))
                .andExpect(jsonPath("$.jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()));
    }

    @Test
    public void updatePartialJobPlaceTest() throws Exception {
        JobUpdateDTO job1Update = new JobUpdateDTO();   //job1 = Monday, Window(id=1), Rosberg(id=2), date
        job1Update.setPlaceId(222l);

        mockMvc.perform(post(URI.JOBS + "/update/" + mondayWindowRosberg.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(job1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(mondayWindowRosberg.getEmployee().getId().intValue()))
                .andExpect(jsonPath("$.placeId").value("222"))
                .andExpect(jsonPath("$.jobDate.[0]").value(monday.getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(monday.getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(monday.getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()));
    }

    @Test
    public void updatePartialJobEmployeeTest() throws Exception {
        JobUpdateDTO job1Update = new JobUpdateDTO();   //job1 = Monday, Window(id=1), Rosberg(id=2), date
        job1Update.setEmployeeId(222l);

        mockMvc.perform(post(URI.JOBS + "/update/" + mondayWindowRosberg.getId()).contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(job1Update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("222"))
                .andExpect(jsonPath("$.placeId").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.jobDate.[0]").value(monday.getYear()))
                .andExpect(jsonPath("$.jobDate.[1]").value(monday.getMonthValue()))
                .andExpect(jsonPath("$.jobDate.[2]").value(monday.getDayOfMonth()))
                .andExpect(jsonPath("$.jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()));
    }

    @Test
    public void updateNonExistingJobTest() throws Exception {
        mockMvc.perform(post(URI.JOBS + "/update/0").contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(mondayWindowRosberg)))
                .andExpect(status().is(404));
    }

    private static String convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }

    @Test
    public void deleteJobTest() throws Exception {
        doNothing().when(jobFacade).removeJob(mondayWindowRosberg.getId());
        mockMvc.perform(delete(URI.JOBS + "/" + mondayWindowRosberg.getId())).andExpect(status().isOk());
    }

    @Test
    public void deleteNotExistingJobTest() throws Exception {
        doThrow(new NonExistingEntityException("Job with id=0 does not exist in system.")).when(jobFacade).removeJob(0l);
        mockMvc.perform(delete(URI.JOBS + "/0")).andExpect(status().is(404));
    }

    @Test
    public void findJobsByEmployeeCriteriaTest() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setEmployeeId(rosberg.getId());

        when(jobFacade.findJobsOfEmployee(rosberg.getId())).thenReturn(Arrays.asList(mondayWindowRosberg, tuesdayWindowRosberg));

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[0]").value(tuesdayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[1]").value(tuesdayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[2]").value(tuesdayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[0]").value(tuesdayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[1]").value(tuesdayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[0]").value(tuesdayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[1]").value(tuesdayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].place.id").value(tuesdayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].employee.id").value(tuesdayWindowRosberg.getEmployee().getId().intValue()))

                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

    @Test
    public void findJobsByPlaceCriteriaTest() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setPlaceId(window.getId());

        when(jobFacade.findJobsOfPlace(window.getId())).thenReturn(Arrays.asList(mondayWindowRosberg, tuesdayWindowRosberg));

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[0]").value(tuesdayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[1]").value(tuesdayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[2]").value(tuesdayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[0]").value(tuesdayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[1]").value(tuesdayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[0]").value(tuesdayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[1]").value(tuesdayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].place.id").value(tuesdayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].employee.id").value(tuesdayWindowRosberg.getEmployee().getId().intValue()))

                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

    @Test
    public void findJobsByEmployeePlaceCriteriaTest() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setEmployeeId(rosberg.getId());
        jobSearchDTO.setPlaceId(window.getId());

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().is(422));
    }

    @Test
    public void findJobsByDayCriteriaTest() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setJobDateStart(monday);

        when(jobFacade.findJobsOfDay(monday)).thenReturn(Arrays.asList(mondayWindowRosberg));

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

    @Test
    public void findJobsByDateDateCriteriaTest() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setJobDateStart(monday);
        jobSearchDTO.setJobDateEnd(tuesday);

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().is(422));
    }

    @Test
    public void findJobsByEmployeeFirstDate() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setEmployeeId(rosberg.getId());
        jobSearchDTO.setJobDateStart(mondayWindowRosberg.getJobDate());

        when(jobFacade.findJobsOfEmployeeBetweenDays(rosberg.getId(), mondayWindowRosberg.getJobDate(), mondayWindowRosberg.getJobDate())).thenReturn(Arrays.asList(mondayWindowRosberg));

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

    @Test
    public void findJobsByEmployeeSecondDate() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setEmployeeId(rosberg.getId());
        jobSearchDTO.setJobDateEnd(mondayWindowRosberg.getJobDate());

        when(jobFacade.findJobsOfEmployeeBetweenDays(rosberg.getId(), mondayWindowRosberg.getJobDate(), mondayWindowRosberg.getJobDate())).thenReturn(Arrays.asList(mondayWindowRosberg));

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

    @Test
    public void findJobsByEmployeeBetweenDate() throws Exception {
        JobSearchDTO jobSearchDTO = new JobSearchDTO();
        jobSearchDTO.setEmployeeId(rosberg.getId());
        jobSearchDTO.setJobDateStart(mondayWindowRosberg.getJobDate());
        jobSearchDTO.setJobDateEnd(tuesdayWindowRosberg.getJobDate());

        when(jobFacade.findJobsOfEmployeeBetweenDays(rosberg.getId(), mondayWindowRosberg.getJobDate(), tuesdayWindowRosberg.getJobDate())).thenReturn(Arrays.asList(mondayWindowRosberg, tuesdayWindowRosberg));

        mockMvc.perform(post(URI.JOBS + "/findByCriteria").contentType(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(jobSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[0]").value(tuesdayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[1]").value(tuesdayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobDate.[2]").value(tuesdayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[0]").value(tuesdayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobStart.[1]").value(tuesdayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[0]").value(tuesdayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].jobEnd.[1]").value(tuesdayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].place.id").value(tuesdayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + tuesdayWindowRosberg.getId() + ")].employee.id").value(tuesdayWindowRosberg.getEmployee().getId().intValue()))

                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[0]").value(mondayWindowRosberg.getJobDate().getYear()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[1]").value(mondayWindowRosberg.getJobDate().getMonthValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobDate.[2]").value(mondayWindowRosberg.getJobDate().getDayOfMonth()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[0]").value(mondayWindowRosberg.getJobStart().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobStart.[1]").value(mondayWindowRosberg.getJobStart().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[0]").value(mondayWindowRosberg.getJobEnd().getHour()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].jobEnd.[1]").value(mondayWindowRosberg.getJobEnd().getMinute()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].place.id").value(mondayWindowRosberg.getPlace().getId().intValue()))
                .andExpect(jsonPath("$.[?(@.id==" + mondayWindowRosberg.getId() + ")].employee.id").value(mondayWindowRosberg.getEmployee().getId().intValue()));
    }

}
