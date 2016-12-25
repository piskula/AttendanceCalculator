package sk.oravcok.posta.facade;

import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dto.JobCreateDTO;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.dto.JobUpdateDTO;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.mapping.BeanMappingServiceImpl;
import sk.oravcok.posta.service.EmployeeService;
import sk.oravcok.posta.service.JobService;
import sk.oravcok.posta.service.PlaceService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * @author Ondrej Oravcok
 * @version 20-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class JobFacadeTest extends AbstractTestNGSpringContextTests {

    @Mock
    private JobService jobService;

    @Mock
    private PlaceService placeService;

    @Mock
    private EmployeeService employeeService;

    @Spy
    @Inject
    private final BeanMappingService beanMappingService = new BeanMappingServiceImpl();

    @InjectMocks
    private final JobFacade jobFacade = new JobFacadeImpl();

    @Captor
    ArgumentCaptor<Job> argumentCaptor;

    Place window;
    Place background;
    Employee vettel;
    Employee webber;
    private LocalDate monday;
    private LocalDate tuesday;

    private Job mondayWindowVettel;
    private Job mondayBackgroundVettel;
    private Job tuesdayWindowWebber;
    private Job tuesdayBackgroundWebber;

    @BeforeClass
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void initJobs() {
        window = new Place(1l);
        window.setName("Window");
        window.setPlaceType(PlaceType.WINDOW);
        window.setAnnotation("base window");

        background = new Place(2l);
        background.setName("Background");
        background.setPlaceType(PlaceType.BACKGROUND);
        background.setAnnotation("sleeping on boxes");

        vettel = new Employee(1l);
        vettel.setName("Sebastian");
        vettel.setSurname("Vettel");
        vettel.setTitle("Mr.");
        vettel.setAddress("134 Zurich, Switzerland");
        vettel.setEmail("seb@ferrari.it");
        vettel.setPhone("+9 67 67 34 897");
        vettel.setBirth(LocalDate.of(1987, 7, 3));
        vettel.setAnnotation("4 times World Champion");

        webber = new Employee(2l);
        webber.setName("Mark");
        webber.setSurname("Webber");
        webber.setTitle("Mr.");
        webber.setEmail("mark.webber@porsche.com");
        webber.setAddress("Imola Park, Italy");
        webber.setBirth(LocalDate.of(1976, 8, 27));
        webber.setPhone("+61 491 570 110");
        webber.setAnnotation("multi-21");

        monday = LocalDate.of(2016, 12, 26);
        tuesday = LocalDate.of(2016, 12, 27);

        mondayWindowVettel = new Job(1l);
        mondayWindowVettel.setPlace(window);
        mondayWindowVettel.setEmployee(vettel);
        mondayWindowVettel.setJobDate(monday);
        mondayWindowVettel.setJobStart(LocalTime.of(7, 10));
        mondayWindowVettel.setJobEnd(LocalTime.of(14, 20));

        tuesdayWindowWebber = new Job(2l);
        tuesdayWindowWebber.setPlace(window);
        tuesdayWindowWebber.setEmployee(webber);
        tuesdayWindowWebber.setJobDate(tuesday);
        tuesdayWindowWebber.setJobStart(LocalTime.of(12, 30));
        tuesdayWindowWebber.setJobEnd(LocalTime.of(18, 10));

        mondayBackgroundVettel = new Job(3l);
        mondayBackgroundVettel.setPlace(background);
        mondayBackgroundVettel.setEmployee(vettel);
        mondayBackgroundVettel.setJobDate(monday);
        mondayBackgroundVettel.setJobStart(LocalTime.of(14, 30));
        mondayBackgroundVettel.setJobEnd(LocalTime.of(14, 50));

        tuesdayBackgroundWebber = new Job(4l);
        tuesdayBackgroundWebber.setPlace(background);
        tuesdayBackgroundWebber.setEmployee(webber);
        tuesdayBackgroundWebber.setJobDate(tuesday);
        tuesdayBackgroundWebber.setJobStart(LocalTime.of(6, 50));
        tuesdayBackgroundWebber.setJobEnd(LocalTime.of(11, 55));
    }

    @BeforeMethod(dependsOnMethods = "initJobs")
    public void initMocksBehaviour() {
        when(placeService.findById(1l)).thenReturn(window);
        when(placeService.findById(2l)).thenReturn(background);
        when(employeeService.findById(1l)).thenReturn(vettel);
        when(employeeService.findById(2l)).thenReturn(webber);

        when(jobService.findById(0l)).thenReturn(null);
        when(jobService.findById(1l)).thenReturn(mondayWindowVettel);
        when(jobService.findById(2l)).thenReturn(tuesdayWindowWebber);
        when(jobService.findById(3l)).thenReturn(mondayBackgroundVettel);
        when(jobService.findById(4l)).thenReturn(tuesdayBackgroundWebber);

        when(jobService.findJobsOfEmployee(vettel)).thenReturn(Arrays.asList(mondayWindowVettel, mondayBackgroundVettel));
        when(jobService.findJobsOfEmployee(webber)).thenReturn(Arrays.asList(tuesdayWindowWebber, tuesdayBackgroundWebber));

        when(jobService.findJobsOfPlace(window)).thenReturn(Arrays.asList(mondayWindowVettel, tuesdayWindowWebber));
        when(jobService.findJobsOfPlace(background)).thenReturn(Arrays.asList(mondayBackgroundVettel, tuesdayBackgroundWebber));

        when(jobService.findJobsOfEmployeeBetweenDays(vettel, monday, monday)).thenReturn(Arrays.asList(mondayWindowVettel));
        when(jobService.findJobsOfEmployeeBetweenDays(webber, monday, tuesday)).thenReturn(Arrays.asList(tuesdayBackgroundWebber, tuesdayWindowWebber));

        when(jobService.findJobsOfPlaceBetweenDays(window, monday, monday)).thenReturn(Arrays.asList(mondayWindowVettel));
        when(jobService.findJobsOfPlaceBetweenDays(background, monday, tuesday)).thenReturn(Arrays.asList(mondayBackgroundVettel, tuesdayBackgroundWebber));
    }

    @Test
    public void createJobTest() {
        JobCreateDTO work = new JobCreateDTO();
        //create with same values as mondayWindowVettel
        work.setEmployeeId(mondayWindowVettel.getEmployee().getId());
        work.setPlaceId(mondayWindowVettel.getPlace().getId());
        work.setJobDate(mondayWindowVettel.getJobDate());
        work.setJobStart(mondayWindowVettel.getJobStart());
        work.setJobEnd(mondayWindowVettel.getJobEnd());

        jobFacade.createJob(work);
        verify(jobService).create(argumentCaptor.capture());
        assertDeepEqualsWithoutIds(argumentCaptor.getValue(), mondayWindowVettel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullJobTest() {
        jobFacade.createJob(null);
    }

    @Test
    public void updateJobTest() {
        JobUpdateDTO work = new JobUpdateDTO();
        //create with same values as mondayWindowVettel
        work.setEmployeeId(mondayWindowVettel.getEmployee().getId());
        work.setPlaceId(mondayWindowVettel.getPlace().getId());
        work.setJobDate(mondayWindowVettel.getJobDate());
        work.setJobStart(mondayWindowVettel.getJobStart());
        work.setJobEnd(mondayWindowVettel.getJobEnd());
        work.setId(2l); //mock set to return tuesdayWindowWebber (id=2)

        jobFacade.updateJob(work);
        verify(jobService).update(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getId(), Long.valueOf(2l));
        assertDeepEqualsWithoutIds(argumentCaptor.getValue(), mondayWindowVettel);
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void updateNonExistingJobTest() {
        JobUpdateDTO work = new JobUpdateDTO();
        //create with same values as mondayWindowVettel
        work.setEmployeeId(mondayWindowVettel.getEmployee().getId());
        work.setPlaceId(mondayWindowVettel.getPlace().getId());
        work.setJobDate(mondayWindowVettel.getJobDate());
        work.setJobStart(mondayWindowVettel.getJobStart());
        work.setJobEnd(mondayWindowVettel.getJobEnd());
        work.setId(0l); //mock set to return non-existing

        jobFacade.updateJob(work);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullJobTest() {
        jobFacade.updateJob(null);
    }

    @Test
    public void removeJobTest() {
        jobFacade.removeJob(1l);    //mock set to return mondayWindowVettel (id=1)
        verify(jobService).remove(argumentCaptor.capture());
        assertDeepEquals(argumentCaptor.getValue(), mondayWindowVettel);
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void removeNonExistingJobTest() {
        jobFacade.removeJob(0l);    //mock set to return non-existing
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullJobTest() {
        jobFacade.removeJob(null);
    }

    @Test
    public void findJobByIdTest() {
        JobDTO job = jobFacade.findJobById(1l);
        assertDeepEquals(job, mondayWindowVettel);
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void findNonExistingJobByIdTest() {
        jobFacade.findJobById(0l);  //mock set to return non-existing
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findJobByNullIdTest() {
        jobFacade.findJobById(null);
    }

    @Test
    public void findJobsOfEmployeeTest() {
        Set<JobDTO> jobsOfVettel = new HashSet<>(jobFacade.findJobsOfEmployee(vettel.getId()));
        Set<JobDTO> jobsOfWebber = new HashSet<>(jobFacade.findJobsOfEmployee(webber.getId()));

        assertEquals(jobsOfVettel.size(), 2);
        assertEquals(jobsOfWebber.size(), 2);

        for(JobDTO current : jobsOfVettel) {
            assertDeepEquals(current, current.getId().equals(1l) ? mondayWindowVettel : mondayBackgroundVettel);
        }
        for(JobDTO current : jobsOfWebber) {
            assertDeepEquals(current, current.getId().equals(2l) ? tuesdayWindowWebber : tuesdayBackgroundWebber);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findJobsOfNullEmployeeTest() {
        jobFacade.findJobsOfEmployee(null);
    }

    @Test
    public void findJobsOfPlaceTest() {
        Set<JobDTO> jobsOfWindow = new HashSet<>(jobFacade.findJobsOfPlace(window.getId()));
        Set<JobDTO> jobsOfBackground = new HashSet<>(jobFacade.findJobsOfPlace(background.getId()));

        assertEquals(jobsOfWindow.size(), 2);
        assertEquals(jobsOfBackground.size(), 2);

        for(JobDTO current : jobsOfWindow) {
            assertDeepEquals(current, current.getId().equals(1l) ? mondayWindowVettel : tuesdayWindowWebber);
        }
        for(JobDTO current : jobsOfBackground) {
            assertDeepEquals(current, current.getId().equals(3l) ? mondayBackgroundVettel : tuesdayBackgroundWebber);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findJobsOfNullPlaceTest() {
        jobFacade.findJobsOfPlace(null);
    }

    @Test
    public void findJobsOfEmployeeDateTest() {
        List<JobDTO> jobsOfMondayVettel = jobFacade.findJobsOfEmployeeBetweenDays(vettel.getId(), monday, monday);
        Set<JobDTO> jobsOfMondayTuesdayWebber = new HashSet<>(jobFacade.findJobsOfEmployeeBetweenDays(webber.getId(), monday, tuesday));

        assertDeepEquals(jobsOfMondayVettel.get(0), mondayWindowVettel);
        assertEquals(jobsOfMondayTuesdayWebber.size(), 2);

        for(JobDTO current : jobsOfMondayTuesdayWebber) {
            assertDeepEquals(current, current.getId().equals(2l) ? tuesdayWindowWebber : tuesdayBackgroundWebber);
        }
    }

    @Test
    public void findJobsOfNullEmployeeDateTest() {  //in case of any null param should throw IllegalArgumentException
        try{ jobFacade.findJobsOfEmployeeBetweenDays(null, monday, monday); fail(); } catch(IllegalArgumentException e) {}
        try{ jobFacade.findJobsOfEmployeeBetweenDays(1l, null, monday); fail(); } catch(IllegalArgumentException e) {}
        try{ jobFacade.findJobsOfEmployeeBetweenDays(1l, monday, null); fail(); } catch(IllegalArgumentException e) {}
    }

    @Test
    public void findJobsOfPlaceDateTest() {
        List<JobDTO> jobsOfMondayWindow = jobFacade.findJobsOfPlaceBetweenDays(window.getId(), monday, monday);
        Set<JobDTO> jobsOfMondayTuesdayBackground = new HashSet<>(jobFacade.findJobsOfPlaceBetweenDays(background.getId(), monday, tuesday));

        assertDeepEquals(jobsOfMondayWindow.get(0), mondayWindowVettel);
        assertEquals(jobsOfMondayTuesdayBackground.size(), 2);

        for(JobDTO current : jobsOfMondayTuesdayBackground) {
            assertDeepEquals(current, current.getId().equals(3l) ? mondayBackgroundVettel : tuesdayBackgroundWebber);
        }
    }

    @Test
    public void findJobsOfNullPlaceDateTest() {  //in case of any null param should throw IllegalArgumentException
        try{ jobFacade.findJobsOfPlaceBetweenDays(null, monday, monday); fail(); } catch(IllegalArgumentException e) {}
        try{ jobFacade.findJobsOfPlaceBetweenDays(1l, null, monday); fail(); } catch(IllegalArgumentException e) {}
        try{ jobFacade.findJobsOfPlaceBetweenDays(1l, monday, null); fail(); } catch(IllegalArgumentException e) {}
    }

    private void assertDeepEquals(Job j1, Job j2) {
        assertEquals(j1.getId(), j2.getId());
        assertDeepEqualsWithoutIds(j1, j2);
    }

    private void assertDeepEqualsWithoutIds(Job j1, Job j2) {
        assertEquals(j1.getEmployee(), j2.getEmployee());
        assertEquals(j1.getPlace(), j2.getPlace());
        assertEquals(j1.getJobStart(), j2.getJobStart());
        assertEquals(j1.getJobEnd(), j2.getJobEnd());
        assertEquals(j1.getJobDate(), j2.getJobDate());
    }

    private void assertDeepEquals(JobDTO j1, Job j2) {
        assertEquals(j1.getId(), j2.getId());
        assertEquals(j1.getEmployee(), j2.getEmployee());
        assertEquals(j1.getPlace(), j2.getPlace());
        assertEquals(j1.getJobStart(), j2.getJobStart());
        assertEquals(j1.getJobEnd(), j2.getJobEnd());
        assertEquals(j1.getJobDate(), j2.getJobDate());
    }
}
