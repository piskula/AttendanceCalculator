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
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dao.EmployeeDao;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.exception.ServiceExceptionTranslateAspect;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Created by Ondrej Oravcok on 20-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class EmployeeServiceTest extends AbstractTestNGSpringContextTests {

    @Mock
    private EmployeeDao employeeDao;

    private EmployeeService employeeService;

    @Captor
    ArgumentCaptor<Employee> argumentCaptor;

    private Employee vettel;

    private long alreadyExistingId = 13l;
    private long createdEmployeeId = 50l;

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
        vettel.setId(1l);
        vettel.setName("Sebastian");
        vettel.setSurname("Vettel");
        vettel.setTitle("Mr.");
        vettel.setAddress("134 Zurich, Switzerland");
        vettel.setEmail("seb@ferrari.it");
        vettel.setPhone("+9 67 67 34 897");
        vettel.setBirth(LocalDate.of(1987, 7, 3));
    }

    @BeforeMethod(dependsOnMethods = "initEmployees")
    public void setUpMocksBehaviour() {
        //BEHAVIOUR OF: findById()
        when(employeeDao.findById(0l)).thenReturn(null);
        when(employeeDao.findById(1l)).thenReturn(vettel);

        doAnswer((InvocationOnMock invocation) -> {
            throw new InvalidDataAccessApiUsageException("This case should be tested on DAO layer.");
        }).when(employeeDao).findById(null);

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
    }

}
