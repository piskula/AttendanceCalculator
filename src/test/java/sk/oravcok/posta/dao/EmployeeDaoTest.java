package sk.oravcok.posta.dao;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.PersistenceApplicationContext;
import sk.oravcok.posta.entity.Employee;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;

/**
 * Created by User on 26-Oct-16.
 */
@ContextConfiguration(classes = PersistenceApplicationContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class EmployeeDaoTest extends AbstractTestNGSpringContextTests {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    private EmployeeDao employeeDao;

    private Employee employeeFull;

    @BeforeMethod
    public void setEmployees(){
        employeeFull = new Employee();
        employeeFull.setName("Jozko");
        employeeFull.setSurname("Buchnat");
        employeeFull.setTitle("Ing.");
        employeeFull.setBirth(LocalDate.of(1992, Month.DECEMBER, 27));
        employeeFull.setAddress("Kollarova 15");
        employeeFull.setPhone("+54321012345");
        employeeFull.setAnnotation("najlepsi zamestnanec roka");
    }

    @Test
    public void createEmployeeTest(){
        employeeDao.create(employeeFull);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullEmployeeTest(){
        employeeDao.create(null);
    }

}
