package sk.oravcok.posta.dao;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.PersistenceApplicationContext;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

/**
 * Created by Ondrej Oravcok on 28-Oct-16.
 */
@ContextConfiguration(classes = PersistenceApplicationContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class JobDaoTest extends AbstractTestNGSpringContextTests {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JobDao jobDao;

    @Inject
    PlaceDao placeDao;

    @Inject
    EmployeeDao employeeDao;

    private Employee susie;
    private Employee andrew;

    private Place window1;
    private Place garage1;

    private Job andrewWindow1;
    private Job susieWindow1;
    private Job andrewGarage;
    private Job susieGarage;

    @BeforeMethod
    public void initValues(){
        susie = new Employee();
        susie.setName("Susie");
        susie.setSurname("Wolf");
        employeeDao.create(susie);

        window1 = new Place();
        window1.setPlaceType(PlaceType.WINDOW);
        window1.setName("Window 1");
        placeDao.create(window1);

        andrew = new Employee();
        andrew.setName("Andrew");
        andrew.setSurname("Daniels");
        employeeDao.create(andrew);

        garage1 = new Place();
        garage1.setPlaceType(PlaceType.BACKGROUND);
        garage1.setName("Garage 1");
        placeDao.create(garage1);

        andrewWindow1 = new Job();
        andrewWindow1.setJobDate(LocalDate.of(2016, Month.OCTOBER, 28));
        andrewWindow1.setJobStart(LocalTime.of(6, 25));
        andrewWindow1.setJobEnd(LocalTime.of(13, 40));
        andrewWindow1.setPlace(window1);
        andrewWindow1.setEmployee(andrew);

        susieWindow1 = new Job();
        susieWindow1.setJobDate(LocalDate.of(2016, Month.OCTOBER, 28));
        susieWindow1.setJobStart(LocalTime.of(12, 22));
        susieWindow1.setJobEnd(LocalTime.of(18, 00));
        susieWindow1.setPlace(window1);
        susieWindow1.setEmployee(susie);

        andrewGarage = new Job();
        andrewGarage.setJobDate(LocalDate.of(2016, Month.OCTOBER, 28));
        andrewGarage.setJobStart(LocalTime.of(8, 10));
        andrewGarage.setJobEnd(LocalTime.of(14, 0));
        andrewGarage.setPlace(garage1);
        andrewGarage.setEmployee(andrew);

        susieGarage = new Job();
        susieGarage.setJobDate(LocalDate.of(2016, Month.OCTOBER, 29));
        susieGarage.setJobStart(LocalTime.of(7, 55));
        susieGarage.setJobEnd(LocalTime.of(15, 10));
        susieGarage.setPlace(garage1);
        susieGarage.setEmployee(susie);
    }

    @Test
    public void createJobTest(){
        Job job = new Job();
        job.setEmployee(susie);
        job.setPlace(window1);
        job.setJobDate(LocalDate.now());
        job.setJobStart(LocalTime.of(14, 20));
        job.setJobEnd(LocalTime.of(17, 45));
        jobDao.create(job);

        Assert.assertEquals(jobDao.findAll().get(0).getEmployee().getName(), "Susie");
        Assert.assertEquals(jobDao.findAll().get(0).getPlace().getName(), "Window 1");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullJobTest(){
        jobDao.create(null);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createEmployeeNullJobTest(){
        andrewWindow1.setEmployee(null);
        jobDao.create(andrewWindow1);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createPlaceNullJobTest(){
        andrewWindow1.setPlace(null);
        jobDao.create(andrewWindow1);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createJobStartNullJobTest(){
        andrewWindow1.setJobStart(null);
        jobDao.create(andrewWindow1);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createJobEndNullJobTest(){
        andrewWindow1.setJobEnd(null);
        jobDao.create(andrewWindow1);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createJobDateNullJobTest(){
        andrewWindow1.setJobDate(null);
        jobDao.create(andrewWindow1);
    }

    @Test
    public void createMultipleJobsTest(){
        jobDao.create(andrewWindow1);
        jobDao.create(susieWindow1);
        jobDao.create(andrewGarage);
        jobDao.create(susieGarage);
        Assert.assertEquals(jobDao.findAll().size(), 4);
    }

    @Test
    public void updateJobTest(){
        jobDao.create(andrewWindow1);
        andrewWindow1.setJobDate(LocalDate.of(2015, Month.SEPTEMBER, 27));
        andrewWindow1.setJobStart(LocalTime.of(5, 20));
        andrewWindow1.setJobEnd(LocalTime.of(12, 35));
        andrewWindow1.setPlace(garage1);
        andrewWindow1.setEmployee(susie);
        Job updated = jobDao.update(andrewWindow1);
        assertDeepEquals(updated, andrewWindow1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullJobTest(){
        jobDao.create(andrewWindow1);
        jobDao.update(null);
    }

    private void assertDeepEquals(Job j1, Job j2){
        Assert.assertEquals(j1.getJobStart(), j2.getJobStart());
        Assert.assertEquals(j1.getJobEnd(), j2.getJobEnd());
        Assert.assertEquals(j1.getJobDate(), j2.getJobDate());
        Assert.assertEquals(j1.getPlace(), j2.getPlace());
        Assert.assertEquals(j1.getEmployee(), j2.getEmployee());
    }
}
