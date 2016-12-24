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
import sk.oravcok.posta.dao.EmployeeDao;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.exception.DataManipulationException;
import sk.oravcok.posta.exception.ServiceExceptionTranslateAspect;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * @author Ondrej Oravcok
 * @version 20-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class EmployeeServiceTest extends AbstractTestNGSpringContextTests {

    @Mock
    private EmployeeDao employeeDao;

    private EmployeeService employeeService;

    @Captor
    ArgumentCaptor<Employee> argumentCaptor;

    private Employee vettel;
    private Employee ricciardo;

    private long notPersistedId = 7l;
    private long alreadyExistingId = 13l;
    private long createdEmployeeId = 50l;
    private long updatedEmployeeId = 51l;

    @BeforeClass
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);

        //We need to setup proxy correctly because of using exception translation
        //through Aspect on mocked objects
        ServiceExceptionTranslateAspect serviceExceptionTranslateAspect = new ServiceExceptionTranslateAspect();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new EmployeeServiceImpl());
        aspectJProxyFactory.addAspect(serviceExceptionTranslateAspect);

        employeeService = aspectJProxyFactory.getProxy();
        ReflectionTestUtils.setField(employeeService, "employeeDao", employeeDao);
    }

    @BeforeMethod
    public void initEmployees() {
        vettel = new Employee();
        vettel.setName("Sebastian");
        vettel.setSurname("Vettel");
        vettel.setTitle("Mr.");
        vettel.setAddress("134 Zurich, Switzerland");
        vettel.setEmail("seb@ferrari.it");
        vettel.setPhone("+9 67 67 34 897");
        vettel.setBirth(LocalDate.of(1987, 7, 3));
        vettel.setAnnotation("4 times World Champion");

        ricciardo = new Employee();
        ricciardo.setId(1l);
        ricciardo.setName("Daniel");
        ricciardo.setSurname("Ricciardo");
        ricciardo.setTitle("Mr.");
        ricciardo.setAddress("234 Perth, Australia");
        ricciardo.setEmail("daniel.ricciardo@redbullracing.com");
        ricciardo.setPhone("+420 673 122 897");
        ricciardo.setBirth(LocalDate.of(1989, 7, 1));
    }

    @BeforeMethod(dependsOnMethods = "initEmployees")
    public void setUpMocksBehaviour() {
        //BEHAVIOUR OF: findById()
        when(employeeDao.findById(0l)).thenReturn(null);
        when(employeeDao.findById(1l)).thenReturn(ricciardo);

        doAnswer((InvocationOnMock invocation) -> { //there is no check for null in DAO for findById
            throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
        }).when(employeeDao).findById(null);

        //BEHAVIOUR OF: findEmployeesByKey()
        when(employeeDao.findByFullName("non-existing")).thenReturn(Arrays.asList());
        when(employeeDao.findByFullName("Daniel Ricciardo")).thenReturn(Arrays.asList(ricciardo));
        when(employeeDao.findByFullName("Ricciardo Daniel")).thenReturn(Arrays.asList(ricciardo));

        //BEHAVIOUR OF: create()
        doAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Employee employee = (Employee) invocation.getArguments()[0];
            if(employee.getId() != null && employee.getId().equals(alreadyExistingId)) {
                throw new EntityExistsException("This is EntityManager test case.");
            }

            if(employee.getName() == null || employee.getSurname() == null
                    || (employee.getEmail() != null && !employee.getEmail().matches(".+@.+\\....?"))) {
                throw new ConstraintViolationException("This should be tested in Persistence Validation.", null);
            }
            employee.setId(createdEmployeeId);
            return null;    //happy day scenario
        }).when(employeeDao).create(any(Employee.class));

        //BEHAVIOUR OF: update()
        doAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Employee employee = (Employee) invocation.getArguments()[0];

            if(employee.getName() == null || employee.getSurname() == null
                    || (employee.getEmail() != null && !employee.getEmail().matches(".+@.+\\....?"))) {
                throw new ConstraintViolationException("This should be tested in Persistence Validation.", null);
            }
            if(employee.getId() == null) {
                employee.setId(updatedEmployeeId);
            }

            return employee;    //happy day scenario
        }).when(employeeDao).update(any(Employee.class));

        //BEHAVIOUR OF: remove()
        doAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArguments()[0] == null) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
            }

            Employee employee = (Employee) invocation.getArguments()[0];
            if(employee.getId() == alreadyExistingId) {
                return null;    //happy day scenario
            }
            if(employee.getId() == notPersistedId) {
                throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");   //employee is not saved
            }

            return null;
        }).when(employeeDao).remove(any(Employee.class));
    }

    @Test
    public void createATODO_EmployeeTest() {
//    public void createEmployeeTest() {
        Employee verstappen = new Employee();
        verstappen.setName("Max");
        verstappen.setSurname("Verstappen");
        verstappen.setTitle("Mr.");
        verstappen.setAddress("Hasselt, Belgium");
        verstappen.setEmail("max33verstappen@redbullracing.com");
        verstappen.setPhone("+33 567 234 89 47");
        verstappen.setBirth(LocalDate.of(1997, 9, 30));

        employeeService.create(verstappen);
        verify(employeeDao).create(argumentCaptor.capture());
        assertNotNull(verstappen);
        assertEquals((long) verstappen.getId(), createdEmployeeId);
        assertDeepEquals(argumentCaptor.getValue(), verstappen);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullEmployeeTest() {
        employeeService.create(null);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createAlreadyExistingEmployee() {
        vettel.setId(alreadyExistingId);
        employeeService.create(vettel);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createNullNameEmployeeTest() {
        vettel.setName(null);
        employeeService.create(vettel);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createNullSurnameEmployeeTest() {
        vettel.setSurname(null);
        employeeService.create(vettel);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void createInvalidEmailEmployeeTest() {
        vettel.setEmail(".hopky@cupky");
        employeeService.create(vettel);
    }

    @Test
    public void updateEmployeeTest() {
        assertNotNull(ricciardo.getId());
        Employee updated = employeeService.update(ricciardo);
        verify(employeeDao).update(argumentCaptor.capture());
        assertEquals(updated.getId(), ricciardo.getId());
        assertDeepEqualsWithoutIds(updated, ricciardo);
    }

    @Test
    public void updateNonExistingEmployeeTest() {
        assertNull(vettel.getId());
        Employee updated = employeeService.update(vettel);
        assertEquals(updated.getId(), vettel.getId());
        assertDeepEqualsWithoutIds(updated, vettel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullEmployeeTest() {
        employeeService.update(null);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void updateNullNameEmployeeTest() {
        ricciardo.setName(null);
        employeeService.update(ricciardo);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void updateNullSurnameEmployeeTest() {
        ricciardo.setSurname(null);
        employeeService.update(ricciardo);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void updateInvalidEmailEmployeeTest() {
        ricciardo.setEmail(".@..f");
        employeeService.update(ricciardo);
    }

    @Test
    public void findEmployeeByIdTest() {
        Employee found = employeeService.findById(1l);
        assertEquals(found.getId(), ricciardo.getId());
        assertDeepEqualsWithoutIds(found, ricciardo);
    }

    @Test
    public void findNotExistingEmployeeByIdTest() {
        assertNull(employeeService.findById(0l));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findEmployeeByNullIdTest() {
        employeeService.findById(null);
    }

    @Test
    public void findEmployeesByKey() {
        List<Employee> result1 = employeeService.findEmployeesByKey("Daniel Ricciardo");
        assertNotNull(result1);
        assertEquals(result1.get(0).getId(), ricciardo.getId());
        assertDeepEqualsWithoutIds(result1.get(0), ricciardo);

        List<Employee> result2 = employeeService.findEmployeesByKey("Daniel Ricciardo");
        assertEquals(result1, result2);
    }

    @Test
    public void findNonExistingEmployeesByKey() {
        List<Employee> result = employeeService.findEmployeesByKey("non-existing");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findEmployeesByNullKey() {
        employeeService.findEmployeesByKey(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findEmployeesByEmptyKey() {
        employeeService.findEmployeesByKey("");
    }

    @Test
    public void removeEmployeeTest() {
        ricciardo.setId(alreadyExistingId);
        employeeService.remove(ricciardo);
        verify(employeeDao).remove(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getId(), Long.valueOf(alreadyExistingId));
        assertDeepEqualsWithoutIds(argumentCaptor.getValue(), ricciardo);
    }

    @Test(expectedExceptions = DataManipulationException.class)
    public void removeNonExistingEmployeeTest() {
        vettel.setId(notPersistedId);
        employeeService.remove(vettel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullEmployeeTest() {
        employeeService.remove(null);
    }

    private void assertDeepEqualsWithoutIds(Employee e1, Employee e2) {
        assertEquals(e1.getName(), e2.getName());
        assertEquals(e1.getSurname(), e2.getSurname());
        assertEquals(e1.getTitle(), e2.getTitle());
        assertEquals(e1.getAddress(), e2.getAddress());
        assertEquals(e1.getEmail(), e2.getEmail());
        assertEquals(e1.getPhone(), e2.getPhone());
        assertEquals(e1.getBirth(), e2.getBirth());
        assertEquals(e1.getAnnotation(), e2.getAnnotation());
    }

    private void assertDeepEquals(Employee e1, Employee e2) {
        assertEquals(e1.getId(), e2.getId());
        assertDeepEqualsWithoutIds(e1, e2);
    }

}
