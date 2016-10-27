package sk.oravcok.posta.dao;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.TransactionSystemException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.PersistenceApplicationContext;
import sk.oravcok.posta.entity.Employee;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
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
    private Employee employeeNotFull;

    @BeforeMethod
    public void setEmployees(){
        employeeFull = new Employee();
        employeeFull.setName("Jozko");
        employeeFull.setSurname("Buchnat");
        employeeFull.setTitle("Ing.");
        employeeFull.setBirth(LocalDate.of(1992, Month.DECEMBER, 27));
        employeeFull.setPhone("+54321012345");
        employeeFull.setAddress("Kollarova 15");
        employeeFull.setEmail("jozko.buchnat@capase.sk");
        employeeFull.setAnnotation("najlepsi zamestnanec roka");

        employeeNotFull = new Employee();
        employeeNotFull.setName("Mojko");
        employeeNotFull.setSurname("Capasik");
    }

    @Test
    public void createEmployeeTest(){
        employeeDao.create(employeeFull);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullEmployeeTest(){
        employeeDao.create(null);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createNameNullEmployee(){
        employeeFull.setName(null);
        employeeDao.create(employeeFull);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createSurnameNullEmployee(){
        employeeFull.setSurname(null);
        employeeDao.create(employeeFull);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createInvalidEmailEmployee(){
        employeeFull.setEmail("mojnovyemajlik");
        employeeDao.create(employeeFull);
    }

    @Test
    public void updateEmployeeTest(){
        employeeDao.create(employeeFull);
        employeeFull.setName("Misko");
        employeeFull.setSurname("Parnican");
        employeeFull.setTitle(null);
        employeeFull.setBirth(LocalDate.of(1968, Month.APRIL, 30));
        employeeFull.setPhone("+421976908453");
        employeeFull.setAddress("Jostova 13");
        employeeFull.setEmail("misko.parnican@momo.com");
        employeeFull.setAnnotation("najhorsi momo");
        Employee updated = employeeDao.update(employeeFull);
        assertDeepEquals(updated, employeeFull);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullEmployee(){
        employeeDao.create(employeeNotFull);
        employeeDao.update(employeeFull);

        employeeDao.update(null);
    }

    @Test(expectedExceptions = TransactionSystemException.class)
    public void updateNameNullEmployee(){
        employeeDao.create(employeeFull);
        employeeFull.setName(null);
        employeeDao.update(employeeFull);
    }

    @Test(expectedExceptions = TransactionSystemException.class)
    public void updateSurnameNullEmployeeTest(){
        employeeDao.create(employeeFull);
        employeeFull.setSurname(null);
        employeeDao.update(employeeFull);
    }

    @Test(expectedExceptions = TransactionSystemException.class)
    public void updateInvalidEmailEmployeeTest(){
        employeeDao.create(employeeFull);
        employeeFull.setEmail("supermagegigamail@mojnovy");
        employeeDao.update(employeeFull);
    }

    @Test
    public void updateNotExistingEmployeeTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.update(employeeFull);
        Assert.assertEquals(2, employeeDao.findAll().size());
    }

    @Test
    public void removeEmployeeTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.update(employeeFull);
        Assert.assertEquals(2, employeeDao.findAll().size());

        employeeDao.remove(employeeNotFull);
        Assert.assertEquals(1, employeeDao.findAll().size());

        Employee notRemoved = employeeDao.findAll().get(0);
        assertDeepEquals(notRemoved, employeeFull);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullEmployeeTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.update(employeeFull);

        employeeDao.remove(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNotExistingEmployeeTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.remove(employeeFull);
    }

    @Test
    public void findAllTest() {
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Assert.assertEquals(employeeDao.findAll().size(), 2);
    }

    @Test
    public void findAllEmptyTest() {
        Assert.assertEquals(employeeDao.findAll().size(), 0);
    }

    @Test
    public void findEmployeeByIdTest() {
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Employee result1 = employeeDao.findById(employeeNotFull.getId());
        assertDeepEquals(result1, result1);

        Employee result2 = employeeDao.findById(employeeFull.getId());
        assertDeepEquals(result2, employeeFull);
    }

    @Test
    public void findEmployeeByNameTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Employee result = employeeDao.findByName("Misko");
        Assert.assertNotNull(result);
        assertDeepEquals(employeeFull, result);
    }


    private void assertDeepEquals(Employee e1, Employee e2){
        Assert.assertEquals(e1.getName(), e2.getName());
        Assert.assertEquals(e1.getSurname(), e2.getSurname());
        Assert.assertEquals(e1.getTitle(), e2.getTitle());
        if(e1.getBirth() != null || e2.getBirth() != null)
            Assert.assertTrue(e1.getBirth().equals(e2.getBirth()));
        Assert.assertEquals(e1.getPhone(), e2.getPhone());
        Assert.assertEquals(e1.getAddress(), e2.getAddress());
        Assert.assertEquals(e1.getEmail(), e2.getEmail());
        Assert.assertEquals(e1.getAnnotation(), e2.getAnnotation());
    }

}
