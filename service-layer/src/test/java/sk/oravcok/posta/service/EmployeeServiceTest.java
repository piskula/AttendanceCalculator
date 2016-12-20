package sk.oravcok.posta.service;

import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.dao.EmployeeDao;

/**
 * Created by Ondrej Oravcok on 20-Dec-16.
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class EmployeeServiceTest extends AbstractTestNGSpringContextTests {

    @Mock
    private EmployeeDao employeeDao;

    //TODO

}
