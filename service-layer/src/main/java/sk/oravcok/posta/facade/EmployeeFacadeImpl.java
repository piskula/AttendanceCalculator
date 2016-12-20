package sk.oravcok.posta.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.oravcok.posta.dto.EmployeeCreateDTO;
import sk.oravcok.posta.dto.EmployeeDTO;
import sk.oravcok.posta.dto.EmployeeUpdateDTO;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.service.EmployeeService;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of EmployeeFacade
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
@Service
@Transactional
public class EmployeeFacadeImpl implements EmployeeFacade {

    @Inject
    private EmployeeService employeeService;

    @Inject
    private BeanMappingService beanMappingService;

    @Override
    public Long createEmployee(EmployeeCreateDTO employeeCreateDTO) {
        if (employeeCreateDTO == null) {
            throw new IllegalArgumentException("employee cannot be null");
        }
        Employee employee = beanMappingService.mapTo(employeeCreateDTO, Employee.class);

        employeeService.create(employee);
        return employee.getId();
    }

    @Override
    public void updateEmployee(EmployeeUpdateDTO employeeUpdateDTO) {
        if (employeeUpdateDTO == null) {
            throw new IllegalArgumentException("employee cannot be null");
        }
        Employee employee = beanMappingService.mapTo(employeeUpdateDTO, Employee.class);

        if(employeeService.findById(employee.getId()) == null){
            throw new NonExistingEntityException("Can not update non existing employee");
        }

        employeeService.update(employee);
    }

    @Override
    public List<EmployeeDTO> findAllEmployees() {
        return beanMappingService.mapTo(employeeService.findAll(), EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO findEmployeeById(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is null");
        }
        Employee employee = employeeService.findById(employeeId);
        if (employee == null) {
            throw new NonExistingEntityException("Employee with id=" + employeeId + " does not exist");
        }
        return beanMappingService.mapTo(employee, EmployeeDTO.class);
    }

    @Override
    public List<EmployeeDTO> findEmployeesByKey(String key) {
        if(key == null) {
            throw new IllegalArgumentException("searching key is null - cannot look for employees");
        }
        return beanMappingService.mapTo(employeeService.findEmployeesByKey(key), EmployeeDTO.class);
    }

    @Override
    public void removeEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is null");
        }
        Employee employee = employeeService.findById(employeeId);
        if (employee == null) {
            throw new NonExistingEntityException("Cannot remove non existing Employee (with id=" + employeeId + ")");
        }
        employeeService.remove(employee);
    }

}
