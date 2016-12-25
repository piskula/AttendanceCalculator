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
import sk.oravcok.posta.dao.JobDao;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.exception.DataManipulationException;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.exception.ServiceExceptionTranslateAspect;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * @author Ondrej Oravcok
 * @version 24-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class JobServiceTest extends AbstractTestNGSpringContextTests {

    @Mock
    private JobDao jobDao;

    private JobService jobService;

    @Captor
    ArgumentCaptor<Job> argumentCaptor;

    Place window;
    Place background;
    Employee vettel;
    Employee ricciardo;
    private LocalDate monday;
    private LocalDate tuesday;

    private Job mondayWindowVettel;
    private Job mondayWindowRicciardo;
    private Job mondayBackgroundVettel;
    private Job tuesdayBackgroundRicciardo;

    private long notPersistedId = 7l;
    private long alreadyExistingId = 13l;
    private long createdJobId = 50l;
    private long updatedJobId = 51l;

    @BeforeClass
    public void SetUpMockito() {
        MockitoAnnotations.initMocks(this);

        //We need to setup proxy correctly because of using exception translation
        //through Aspect on mocked objects
        ServiceExceptionTranslateAspect serviceExceptionTranslateAspect = new ServiceExceptionTranslateAspect();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new JobServiceImpl());
        aspectJProxyFactory.addAspect(serviceExceptionTranslateAspect);

        jobService = aspectJProxyFactory.getProxy();
        ReflectionTestUtils.setField(jobService, "jobDao", jobDao);
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

        ricciardo = new Employee(2l);
        ricciardo.setName("Daniel");
        ricciardo.setSurname("Ricciardo");
        ricciardo.setTitle("Mr.");
        ricciardo.setAddress("234 Perth, Australia");
        ricciardo.setEmail("daniel.ricciardo@redbullracing.com");
        ricciardo.setPhone("+420 673 122 897");
        ricciardo.setBirth(LocalDate.of(1989, 7, 1));

        monday = LocalDate.of(2016, 12, 26);
        tuesday = LocalDate.of(2016, 12, 27);

        mondayWindowVettel = new Job();
        mondayWindowVettel.setPlace(window);
        mondayWindowVettel.setEmployee(vettel);
        mondayWindowVettel.setJobDate(monday);
        mondayWindowVettel.setJobStart(LocalTime.of(7, 10));
        mondayWindowVettel.setJobEnd(LocalTime.of(14, 20));

        mondayWindowRicciardo = new Job(1l);
        mondayWindowRicciardo.setPlace(window);
        mondayWindowRicciardo.setEmployee(ricciardo);
        mondayWindowRicciardo.setJobDate(monday);
        mondayWindowRicciardo.setJobStart(LocalTime.of(12, 30));
        mondayWindowRicciardo.setJobEnd(LocalTime.of(18, 10));

        mondayBackgroundVettel = new Job(2l);
        mondayBackgroundVettel.setPlace(background);
        mondayBackgroundVettel.setEmployee(vettel);
        mondayBackgroundVettel.setJobDate(monday);
        mondayBackgroundVettel.setJobStart(LocalTime.of(14, 30));
        mondayBackgroundVettel.setJobEnd(LocalTime.of(14, 50));

        tuesdayBackgroundRicciardo = new Job(3l);
        tuesdayBackgroundRicciardo.setPlace(background);
        tuesdayBackgroundRicciardo.setEmployee(ricciardo);
        tuesdayBackgroundRicciardo.setJobDate(tuesday);
        tuesdayBackgroundRicciardo.setJobStart(LocalTime.of(6, 50));
        tuesdayBackgroundRicciardo.setJobEnd(LocalTime.of(11, 55));
    }

    @BeforeMethod(dependsOnMethods = "initJobs")
    public void setUpMocksBehaviour () {
        //BEHAVIOUR OF: findById()
        when(jobDao.findById(0l)).thenReturn(null);
        when(jobDao.findById(1l)).thenReturn(mondayWindowRicciardo);

        doAnswer((InvocationOnMock invocation) -> { //there is no check for null in DAO for findById
            throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
        }).when(jobDao).findById(null);

        //BEHAVIOUR OF: findJobsOfDate()
        when(jobDao.findJobsOfDate(monday)).thenReturn(Arrays.asList(mondayWindowVettel, mondayWindowRicciardo, mondayBackgroundVettel));
        when(jobDao.findJobsOfDate(tuesday)).thenReturn(Arrays.asList(tuesdayBackgroundRicciardo));

        //BEHAVIOUR OF: findJobsOfPlace()
        when(jobDao.findJobsOfPlace(window)).thenReturn(Arrays.asList(mondayWindowVettel, mondayWindowRicciardo));
        when(jobDao.findJobsOfPlace(background)).thenReturn(Arrays.asList(mondayBackgroundVettel, tuesdayBackgroundRicciardo));

        //BEHAVIOUR OF: findJobsOfEmployee()
        when(jobDao.findJobsOfEmployee(vettel)).thenReturn(Arrays.asList(mondayWindowVettel, mondayBackgroundVettel));
        when(jobDao.findJobsOfEmployee(ricciardo)).thenReturn(Arrays.asList(mondayWindowRicciardo, tuesdayBackgroundRicciardo));

        //BEHAVIOUR OF: findJobsOfEmployeeBetweenDates()
        when(jobDao.findJobsOfEmployeeBetweenDays(vettel, monday, monday)).thenReturn(Arrays.asList(mondayWindowVettel, mondayBackgroundVettel));
        when(jobDao.findJobsOfEmployeeBetweenDays(ricciardo, monday, tuesday)).thenReturn(Arrays.asList(mondayWindowRicciardo, tuesdayBackgroundRicciardo));

        //BEHAVIOUR OF: findJobsOfPlaceBetweenDates()
        when(jobDao.findJobsOfPlaceBetweenDays(window, monday, monday)).thenReturn(Arrays.asList(mondayWindowVettel, mondayWindowRicciardo));
        when(jobDao.findJobsOfPlaceBetweenDays(background, monday, tuesday)).thenReturn(Arrays.asList(mondayBackgroundVettel, tuesdayBackgroundRicciardo));

        //BEHAVIOUR OF: create()
        doAnswer((InvocationOnMock invocation) -> {
            if(invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Job job = (Job) invocation.getArguments()[0];
            if(job.getId() != null && job.getId().equals(alreadyExistingId)) {
                throw new EntityExistsException("This is EntityManager test case.");
            }

            if(job.getPlace() == null || job.getEmployee() == null || job.getJobDate() == null
                    || job.getJobStart() == null || job.getJobEnd() == null || job.getJobStart().isAfter(job.getJobEnd())) {
                throw new ConstraintViolationException("This should be tested in Persistence Validation.", null);
            }
            job.setId(createdJobId);
            return null;    //happy day scenario
        }).when(jobDao).create(any(Job.class));

        //BEHAVIOUR OF: update()
        doAnswer((InvocationOnMock invocation) -> {
            if(invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Job job = (Job) invocation.getArguments()[0];
            if(job.getId() != null && job.getId().equals(alreadyExistingId)) {
                throw new EntityExistsException("This is EntityManager test case.");
            }

            if(job.getPlace() == null || job.getEmployee() == null || job.getJobDate() == null
                    || job.getJobStart() == null || job.getJobEnd() == null || job.getJobStart().isAfter(job.getJobEnd())) {
                throw new ConstraintViolationException("This should be tested in Persistence Validation.", null);
            }
            if(job.getId() == null) {
                job.setId(updatedJobId);
            }
            return job;    //happy day scenario
        }).when(jobDao).update(any(Job.class));

        //BEHAVIOUR OF: remove()
        doAnswer((InvocationOnMock invocation) -> {
            if(invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Job job = (Job) invocation.getArguments()[0];
            if(job.getId() == alreadyExistingId) {
                return null;    //happy day scenario
            }
            if(job.getId() == notPersistedId) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");   //job is not saved
            }

            return null;
        }).when(jobDao).remove(any(Job.class));
    }

    @Test
    public void createA_TODO_JobTest() {
//    public void createJobTest() {
        jobService.create(mondayWindowVettel);
        verify(jobDao).create(argumentCaptor.capture());
        assertNotNull(mondayWindowVettel);
        assertEquals((long) mondayWindowVettel.getId(), createdJobId);
        assertDeepEquals(argumentCaptor.getValue(), mondayWindowVettel);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createAlreadyExistingJobTest() {
        mondayWindowVettel.setId(alreadyExistingId);
        jobService.create(mondayWindowVettel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullJobTest() {
        jobService.create(null);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createWrongJobEndJobTest() {
        mondayWindowVettel.setJobEnd(LocalTime.of(1, 20));
        jobService.create(mondayWindowVettel);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createWrongJobStartJobTest() {
        mondayWindowVettel.setJobStart(LocalTime.of(22, 10));
        jobService.create(mondayWindowVettel);
    }

    @Test
    public void updateJobTest() {
        assertNotNull(mondayWindowRicciardo.getId());
        Job updated = jobService.update(mondayWindowRicciardo);
        verify(jobDao).update(argumentCaptor.capture());
        assertDeepEquals(argumentCaptor.getValue(), mondayWindowRicciardo);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullJobTest() {
        jobService.update(null);
    }

    @Test
    public void updateNonExistingJobTest() {
        assertNull(mondayWindowVettel.getId());
        Job updated = jobService.update(mondayWindowVettel);
        assertDeepEquals(updated, mondayWindowVettel);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void updateWrongJobEndJobTest() {
        mondayWindowRicciardo.setJobEnd(LocalTime.of(1, 20));
        jobService.update(mondayWindowRicciardo);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void updateWrongJobStartJobTest() {
        mondayWindowRicciardo.setJobStart(LocalTime.of(22, 10));
        jobService.update(mondayWindowRicciardo);
    }

    @Test
    public void removeJobTest() {
        mondayWindowRicciardo.setId(alreadyExistingId);
        jobService.remove(mondayWindowRicciardo);
        verify(jobDao).remove(argumentCaptor.capture());
        assertDeepEquals(argumentCaptor.getValue(), mondayWindowRicciardo);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void removeNonExistingJobTest() {
        mondayWindowVettel.setId(notPersistedId);
        jobService.remove(mondayWindowVettel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullJobTest() {
        jobService.remove(null);
    }

    @Test
    public void findJobByIdTest() {
        Job found = jobService.findById(1l);
        assertDeepEquals(found, mondayWindowRicciardo);
    }

    @Test
    public void findNonExisitngJobByIdTest() {
        assertNull(jobService.findById(0l));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findByNullIdJobTest() {
        jobService.findById(null);
    }

    @Test
    public void findJobsOfEmployeeTest() {
        List<Job> vettelResult = jobService.findJobsOfEmployee(vettel);
        List<Job> ricciardoResult = jobService.findJobsOfEmployee(ricciardo);

        assertEquals(vettelResult.size(), 2);
        assertEquals(ricciardoResult.size(), 2);

        assertEquals(new HashSet<>(vettelResult), new HashSet<>(Arrays.asList(mondayBackgroundVettel, mondayWindowVettel)));
        assertEquals(new HashSet<>(ricciardoResult), new HashSet<>(Arrays.asList(tuesdayBackgroundRicciardo, mondayWindowRicciardo)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findJobsOfNullEmployeeTest() {
        jobService.findJobsOfEmployee(null);
    }

    @Test
    public void findJobsOfPlaceTest() {
        List<Job> windowResult = jobService.findJobsOfPlace(window);
        List<Job> backgroundResult = jobService.findJobsOfPlace(background);

        assertEquals(windowResult.size(), 2);
        assertEquals(backgroundResult.size(), 2);

        assertEquals(new HashSet<>(windowResult), new HashSet<>(Arrays.asList(mondayWindowRicciardo, mondayWindowVettel)));
        assertEquals(new HashSet<>(backgroundResult), new HashSet<>(Arrays.asList(tuesdayBackgroundRicciardo, mondayBackgroundVettel)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findJobsOfNullPlaceTest() {
        jobService.findJobsOfPlace(null);
    }

    @Test
    public void findJobsOfEmployeeDateTest() {
        List<Job> mondayVettel = jobService.findJobsOfEmployeeBetweenDays(vettel, monday, monday);
        List<Job> mondayTuesdayRicciardo = jobService.findJobsOfEmployeeBetweenDays(ricciardo, monday, tuesday);

        assertEquals(mondayVettel.size(), 2);
        assertEquals(mondayTuesdayRicciardo.size(), 2);

        assertEquals(new HashSet<>(mondayVettel), new HashSet<>(Arrays.asList(mondayBackgroundVettel, mondayWindowVettel)));
        assertEquals(new HashSet<>(mondayTuesdayRicciardo), new HashSet<>(Arrays.asList(tuesdayBackgroundRicciardo, mondayWindowRicciardo)));
    }

    @Test
    public void findJobsOfNullEmployeeDateTest() {  //in case of any null param should throw IllegalArgumentException
        try{ jobService.findJobsOfEmployeeBetweenDays(null, monday, monday); fail(); } catch(IllegalArgumentException e){}
        try{ jobService.findJobsOfEmployeeBetweenDays(vettel, null, monday); fail(); } catch(IllegalArgumentException e){}
        try{ jobService.findJobsOfEmployeeBetweenDays(vettel, monday, null); fail(); } catch(IllegalArgumentException e){}
    }

    @Test
    public void findJobsOfPlaceDateTest() {
        List<Job> mondayWindow = jobService.findJobsOfPlaceBetweenDays(window, monday, monday);
        List<Job> mondayTuesdayBackground = jobService.findJobsOfPlaceBetweenDays(background, monday, tuesday);

        assertEquals(mondayWindow.size(), 2);
        assertEquals(mondayTuesdayBackground.size(), 2);

        assertEquals(new HashSet<>(mondayWindow), new HashSet<>(Arrays.asList(mondayWindowRicciardo, mondayWindowVettel)));
        assertEquals(new HashSet<>(mondayTuesdayBackground), new HashSet<>(Arrays.asList(tuesdayBackgroundRicciardo, mondayBackgroundVettel)));
    }

    @Test
    public void findJobsOfNullPlaceDateTest() { //in case of any null param should throw IllegalArgumentException
        try{ jobService.findJobsOfPlaceBetweenDays(null, monday, monday); fail(); } catch(IllegalArgumentException e){}
        try{ jobService.findJobsOfPlaceBetweenDays(window, null, monday); fail(); } catch(IllegalArgumentException e){}
        try{ jobService.findJobsOfPlaceBetweenDays(window, monday, null); fail(); } catch(IllegalArgumentException e){}
    }

    private void assertDeepEqualsWithoutIds(Job j1, Job j2) {
        assertEquals(j1.getEmployee(), j2.getEmployee());
        assertEquals(j1.getPlace(), j2.getPlace());
        assertEquals(j1.getJobDate(), j2.getJobDate());
        assertEquals(j1.getJobStart(), j2.getJobStart());
        assertEquals(j1.getJobEnd(), j2.getJobEnd());
    }

    private void assertDeepEquals(Job j1, Job j2) {
        assertEquals(j1.getId(), j2.getId());
        assertDeepEqualsWithoutIds(j1, j2);
    }

}
