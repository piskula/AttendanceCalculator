package sk.oravcok.posta.facade;

import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dto.EmployeeCreateDTO;
import sk.oravcok.posta.dto.EmployeeDTO;
import sk.oravcok.posta.dto.EmployeeUpdateDTO;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.mapping.BeanMappingServiceImpl;
import sk.oravcok.posta.service.EmployeeService;
import sk.oravcok.posta.service.EmployeeServiceImpl;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Ondrej Oravcok
 * @version 20-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class EmployeeFacadeTest extends AbstractTestNGSpringContextTests {

    @Mock
    private EmployeeService employeeService;

    @Spy
    @Inject
    private final BeanMappingService beanMappingService = new BeanMappingServiceImpl();

    @InjectMocks
    private final EmployeeFacade employeeFacade = new EmployeeFacadeImpl();

    @Captor
    ArgumentCaptor<Employee> argumentCaptor;

    private Employee webber;

    @BeforeClass
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void initEntities() {
        webber = new Employee(1l);
        webber.setName("Mark");
        webber.setSurname("Webber");
        webber.setTitle("Mr.");
        webber.setEmail("mark.webber@porsche.com");
        webber.setAddress("Imola Park, Italy");
        webber.setBirth(LocalDate.of(1976, 8, 27));
        webber.setPhone("+61 491 570 110");
        webber.setAnnotation("very good driver");
    }

    @BeforeMethod(dependsOnMethods = "initEntities")
    public void initMocksBehaviour() {
        //find by id
        when(employeeService.findById(0l)).thenReturn(null);
        when(employeeService.findById(1l)).thenReturn(webber);

        //find by name
        when(employeeService.findEmployeesByKey("non-existing")).thenReturn(new ArrayList<>());
        when(employeeService.findEmployeesByKey("Mark Webber")).thenReturn(Arrays.asList(webber));
        when(employeeService.findEmployeesByKey("Webber Mark")).thenReturn(Arrays.asList(webber));
    }

    @Test
    public void createEmployeeTest() {
        EmployeeCreateDTO rosberg = new EmployeeCreateDTO();
        rosberg.setName("Nico");
        rosberg.setSurname("Rosberg");
        rosberg.setTitle("Mr.");
        rosberg.setEmail("nico.rosberg@mercedes.de");
        rosberg.setAddress("Monte Carlo, Monaco");
        rosberg.setBirth(LocalDate.of(1985, 6, 27));
        rosberg.setPhone("+49 69 209 777 777");
        rosberg.setAnnotation("previous years very good driver");

        employeeFacade.createEmployee(rosberg);
        verify(employeeService).create(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getName(), "Nico");
        assertEquals(argumentCaptor.getValue().getSurname(), "Rosberg");
        assertEquals(argumentCaptor.getValue().getTitle(), "Mr.");
        assertEquals(argumentCaptor.getValue().getEmail(), "nico.rosberg@mercedes.de");
        assertEquals(argumentCaptor.getValue().getAddress(), "Monte Carlo, Monaco");
        assertEquals(argumentCaptor.getValue().getBirth(), LocalDate.of(1985, 6, 27));
        assertEquals(argumentCaptor.getValue().getPhone(), "+49 69 209 777 777");
        assertEquals(argumentCaptor.getValue().getAnnotation(), "previous years very good driver");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullEmployeeTest() {
        employeeFacade.createEmployee(null);
    }

    @Test
    public void updateEmployeeTest() {
        EmployeeUpdateDTO rosberg = new EmployeeUpdateDTO();
        rosberg.setId(1l);  //mock set to return webber (id=1)
        rosberg.setName("Nico");
        rosberg.setSurname("Rosberg");
        rosberg.setTitle("Mr.");
        rosberg.setEmail("nico.rosberg@mercedes.de");
        rosberg.setAddress("Monte Carlo, Monaco");
        rosberg.setBirth(LocalDate.of(1985, 6, 27));
        rosberg.setPhone("+49 69 209 777 777");
        rosberg.setAnnotation("previous years very good driver");

        employeeFacade.updateEmployee(rosberg);
        verify(employeeService).update(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getId(), Long.valueOf(1l));
        assertEquals(argumentCaptor.getValue().getName(), "Nico");
        assertEquals(argumentCaptor.getValue().getSurname(), "Rosberg");
        assertEquals(argumentCaptor.getValue().getTitle(), "Mr.");
        assertEquals(argumentCaptor.getValue().getEmail(), "nico.rosberg@mercedes.de");
        assertEquals(argumentCaptor.getValue().getAddress(), "Monte Carlo, Monaco");
        assertEquals(argumentCaptor.getValue().getBirth(), LocalDate.of(1985, 6, 27));
        assertEquals(argumentCaptor.getValue().getPhone(), "+49 69 209 777 777");
        assertEquals(argumentCaptor.getValue().getAnnotation(), "previous years very good driver");
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void updateNonExistingEmployeeTest() {
        EmployeeUpdateDTO rosberg = new EmployeeUpdateDTO();
        rosberg.setId(0l);  //mock set to return non-existing (id=0)
        rosberg.setName("Nico");
        rosberg.setSurname("Rosberg");
        rosberg.setTitle("Mr.");
        rosberg.setEmail("nico.rosberg@mercedes.de");
        rosberg.setAddress("Monte Carlo, Monaco");
        rosberg.setBirth(LocalDate.of(1985, 6, 27));
        rosberg.setPhone("+49 69 209 777 777");
        rosberg.setAnnotation("previous years very good driver");

        employeeFacade.updateEmployee(rosberg);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullEmployeeTest() {
        employeeFacade.updateEmployee(null);
    }

    @Test
    public void removeEmployeeTest() {
        employeeFacade.removeEmployee(1l);  //mock set to return webber (id=1)
        verify(employeeService).remove(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getId(), Long.valueOf(1l));
        assertEquals(argumentCaptor.getValue().getName(), webber.getName());
        assertEquals(argumentCaptor.getValue().getSurname(), webber.getSurname());
        assertEquals(argumentCaptor.getValue().getTitle(), webber.getTitle());
        assertEquals(argumentCaptor.getValue().getEmail(), webber.getEmail());
        assertEquals(argumentCaptor.getValue().getAddress(), webber.getAddress());
        assertEquals(argumentCaptor.getValue().getBirth(), webber.getBirth());
        assertEquals(argumentCaptor.getValue().getPhone(), webber.getPhone());
        assertEquals(argumentCaptor.getValue().getAnnotation(), webber.getAnnotation());
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void removeNonExistingEmployeeTest() {
        employeeFacade.removeEmployee(0l);  //mock set to return non-existing
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeNullEmployeeTest() {
        employeeFacade.removeEmployee(null);
    }

    @Test
    public void getEmployeeByIdTest() {
        EmployeeDTO employee = employeeFacade.findEmployeeById(1l);
        assertEquals(employee.getId(), Long.valueOf(1l));
        assertEquals(employee.getName(), "Mark");
        assertEquals(employee.getSurname(), "Webber");
        assertEquals(employee.getTitle(), "Mr.");
        assertEquals(employee.getEmail(), "mark.webber@porsche.com");
        assertEquals(employee.getAddress(), "Imola Park, Italy");
        assertEquals(employee.getBirth(), LocalDate.of(1976, 8, 27));
        assertEquals(employee.getPhone(), "+61 491 570 110");
        assertEquals(employee.getAnnotation(), "very good driver");
    }

    @Test(expectedExceptions = NonExistingEntityException.class)
    public void getNonExistingEmployeeById() {
        employeeFacade.findEmployeeById(0l);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getEmployeeByNullId() {
        employeeFacade.findEmployeeById(null);
    }

    @Test
    public void getEmployeeByKeyTest() {
        List<EmployeeDTO> employees1 = employeeFacade.findEmployeesByKey("Webber Mark");
        assertEquals(employees1.get(0).getId(), Long.valueOf(1l));

        List<EmployeeDTO> employees2 = employeeFacade.findEmployeesByKey("Mark Webber");
        assertEquals(employees2.get(0).getId(), Long.valueOf(1l));
    }

    @Test
    public void getNonExistingEmployeesByKeyTest() {
        List<EmployeeDTO> emptyList = employeeFacade.findEmployeesByKey("non-existing");
        assertNotNull(emptyList);
        assertEquals(emptyList.size(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getEmployeeByNullKeyTest() {
        employeeFacade.findEmployeesByKey(null);
    }
}
