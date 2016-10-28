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
import java.util.List;

/**
 * Created by Ondrej Oravcok on 26-Oct-16.
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
    public void createNameNullEmployeeTest(){
        employeeFull.setName(null);
        employeeDao.create(employeeFull);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void createSurnameNullEmployeeTest(){
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
        assertDeepEquals(result1, employeeNotFull);

        Employee result2 = employeeDao.findById(employeeFull.getId());
        assertDeepEquals(result2, employeeFull);
    }

    @Test
    public void findEmployeeByNameTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Employee result = employeeDao.findByName("Mojko");
        Assert.assertNotNull(result);
        assertDeepEquals(employeeNotFull, result);
    }

    @Test
    public void findEmployeeByBadNameTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Employee result = employeeDao.findByName("Tvojko");
        Assert.assertNull(result);
    }

    @Test
    public void findEmployeeBySurnameTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Employee result = employeeDao.findBySurname("Capasik");
        Assert.assertNotNull(result);
        assertDeepEquals(employeeNotFull, result);
    }

    @Test
    public void findEmployeeByBadSurnameTest(){
        employeeDao.create(employeeNotFull);
        employeeDao.create(employeeFull);

        Employee result = employeeDao.findBySurname("Piskota");
        Assert.assertNull(result);
    }

    @Test
    public void findEmployeesByFullNameTest(){
        Employee buchnat2 = new Employee();
        buchnat2.setName("Alojz");
        buchnat2.setSurname("Buchnat");

        employeeDao.create(employeeFull);
        employeeDao.create(employeeNotFull);
        employeeDao.create(buchnat2);

        List<Employee> result = employeeDao.findByFullName("Buch");
        Assert.assertEquals(result.size(), 2);

        if(result.get(0).getName().equals("Alojz")){
            assertDeepEquals(result.get(0), buchnat2);
            assertDeepEquals(result.get(1), employeeFull);
        }
        else{
            assertDeepEquals(result.get(1), buchnat2);
            assertDeepEquals(result.get(0), employeeFull);
        }
    }

    @Test
    public void findEmployeeByFullNameTest(){
        employeeDao.create(employeeFull);
        employeeDao.create(employeeNotFull);

        Assert.assertEquals(employeeDao.findByFullName("Jozko Buchnat").size(), 1);
    }

    @Test
    public void findEmployeeByFullSurnameTest(){
        employeeDao.create(employeeFull);
        employeeDao.create(employeeNotFull);

        Assert.assertEquals(employeeDao.findByFullName("Buchnat Jozko").size(), 1);
    }

    private void assertDeepEquals(Employee e1, Employee e2){
        Assert.assertEquals(e1.getName(), e2.getName());
        Assert.assertEquals(e1.getSurname(), e2.getSurname());
        Assert.assertEquals(e1.getTitle(), e2.getTitle());
        Assert.assertEquals(e1.getBirth(), e2.getBirth());
        Assert.assertEquals(e1.getPhone(), e2.getPhone());
        Assert.assertEquals(e1.getAddress(), e2.getAddress());
        Assert.assertEquals(e1.getEmail(), e2.getEmail());
        Assert.assertEquals(e1.getAnnotation(), e2.getAnnotation());
    }

}
