package sk.oravcok.posta.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sk.oravcok.posta.dto.EmployeeCreateDTO;
import sk.oravcok.posta.dto.EmployeeDTO;
import sk.oravcok.posta.dto.FindTextDTO;
import sk.oravcok.posta.exception.DataManipulationException;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.EmployeeFacade;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.ExistingResourceException;
import sk.oravcok.posta.rest.exception.RequestedResourceNotFound;
import sk.oravcok.posta.rest.exception.ValidationException;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * Employee REST controller
 *
 * @author Ondrej Oravcok
 * @version 15-Jan-17.
 */
@RestController
@RequestMapping(URI.EMPLOYEES)
public class EmployeeRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeRestController.class);

    @Inject
    private EmployeeFacade employeeFacade;

    /**
     * gives you all employees available in system
     *
     * e.g. curl -i -X GET http://localhost:8080/posta/rest/employees
     *
     * @return all employees
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<EmployeeDTO> findAllEmployees() {
        LOGGER.info("getting all employees");
        return employeeFacade.findAllEmployees();
    }

    /**
     * gives you specific Employee by id
     *
     * e.g. curl -i -X GET http://localhost:8080/posta/rest/employees/findId/3
     *
     * @param id to find
     * @return specific Employee
     */
    @RequestMapping(value = "/findId/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final EmployeeDTO findEmployeeById(@PathVariable("id") long id) {
        try {
            LOGGER.info("getting Employee with id=" + id);
            return employeeFacade.findEmployeeById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Employee with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }
    }

    /**
     * find Employees by name/surname in system
     *
     * curl -X GET -i -H "Content-Type: application/json" --data '{"name":"Mark Webber"}' http://localhost:8080/posta/rest/employees/find
     *
     * @param findTextDTO searching key
     * @return specific employees
     */
    @RequestMapping(value = "/find", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<EmployeeDTO> findEmployeesByKey(@RequestBody FindTextDTO findTextDTO) {
        if(findTextDTO.getText() == null || findTextDTO.getText().isEmpty()) {
            throw new ValidationException("Text to search (employees) is invalid");
        }
        try {
            LOGGER.info("getting Employees matching name=" + findTextDTO.getText());
            return employeeFacade.findEmployeesByKey(findTextDTO.getText());
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Employees matching name=" + findTextDTO.getText() + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument key is illegal", e);
        }
    }

    /**
     * creates new Employee in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Michael","surname":"Schumacher","birth":"1971-12-06"}' http://localhost:8080/posta/rest/employees
     *
     * @param employee to create
     * @return created employee
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final EmployeeDTO createEmployee(@RequestBody EmployeeCreateDTO employee) {
        try {
            LOGGER.info("creating Employee " + employee);
            Long id = employeeFacade.createEmployee(employee);
            return employeeFacade.findEmployeeById(id);
        } catch (DataManipulationException e) {
            throw new ValidationException("DataManipulationException thrown while creating employee.", e);
        } catch (Exception e) {
            throw new ExistingResourceException(e);
        }
    }

    /**
     * removes Employee from system
     *
     * curl -i -X DELETE http://localhost:8080/posta/rest/employees/3
     *
     * @param id of Employee to remove
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final void deleteEmployee(@PathVariable("id") long id) {
        try {
            LOGGER.info("deleting Employee with id=" + id);
            employeeFacade.removeEmployee(id);
        } catch (NonExistingEntityException | IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Employee with id=" + id + " does not exist in system.", e);
        }
    }

    /**
     * updates Employee in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Updated Name"}' http://localhost:8080/posta/rest/employees/update/4
     *
     * @param id of updating Employee
     * @param employeeDTO values, which we need to actualize
     * @return actualised Employee
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final EmployeeDTO updateEmployee(@PathVariable("id") long id, @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO previous;
        try {
            LOGGER.info("updating Employee with id=" + id + ", with values " + employeeDTO);
            previous = employeeFacade.findEmployeeById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Employee with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }

        EmployeeDTO actual = mergeDtosForUpdate(previous, employeeDTO);
        try {
            employeeFacade.updateEmployee(actual);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Employee employee=" + actual + " does not exist in system.", e);
        } catch (Exception e) {
            throw new RequestedResourceNotFound("Exception thrown while updating existing employee.", e);
        }

        return actual;
    }

    private EmployeeDTO mergeDtosForUpdate(EmployeeDTO previous, EmployeeDTO actual) {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(previous.getId());

        employee.setName(actual.getName() != null ? actual.getName() : previous.getName());
        employee.setSurname(actual.getSurname() != null ? actual.getSurname() : previous.getSurname());
        employee.setTitle(actual.getTitle() != null ? actual.getTitle() : previous.getTitle());

        if(actual.getBirth() != null) {
            employee.setBirth(actual.getBirth().isBefore(LocalDate.of(1940,1,1)) ? null : actual.getBirth());
        } else {
            employee.setBirth(previous.getBirth());
        }

        employee.setPhone(actual.getPhone() != null ? actual.getPhone() : previous.getPhone());
        employee.setAddress(actual.getAddress() != null ? actual.getAddress() : previous.getAddress());

        if(actual.getEmail() != null) {
            employee.setEmail(actual.getEmail().isEmpty() ? null : actual.getEmail());
        } else {
            employee.setEmail(previous.getEmail());
        }

        employee.setAnnotation(actual.getAnnotation() != null ? actual.getAnnotation() : previous.getAnnotation());

        return employee;
    }

}
