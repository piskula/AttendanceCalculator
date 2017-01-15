package sk.oravcok.posta.facade;

import sk.oravcok.posta.dto.EmployeeCreateDTO;
import sk.oravcok.posta.dto.EmployeeDTO;
import sk.oravcok.posta.exception.NonExistingEntityException;

import java.util.List;

/**
 * Facade interface for Employee
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
public interface EmployeeFacade {

    /**
     * Creates employee.
     *
     * @param employee entity to be created
     * @return id of newly created employee
     * @throws IllegalArgumentException   if employee is null
     */
    Long createEmployee(EmployeeCreateDTO employee);

    /**
     * Updates employee.
     *
     * @param employee entity to be updated
     * @throws IllegalArgumentException   if employee is null
     * @throws NonExistingEntityException on attempt to update non existing employee
     */
    void updateEmployee(EmployeeDTO employee);

    /**
     * Returns all employees.
     *
     * @return list of all employee entities or empty list if no employee exists
     */
    List<EmployeeDTO> findAllEmployees();

    /**
     * Returns employee according to given id.
     *
     * @param employeeId
     * @return employee identified by unique id
     * @throws NonExistingEntityException if employee for given id doesn't exist
     * @throws IllegalArgumentException if employeeId is null
     */
    EmployeeDTO findEmployeeById(Long employeeId);

    /**
     * searching for employees in name + surname
     *
     * @param key what to search
     * @return employees, which meets the condition
     */
    List<EmployeeDTO> findEmployeesByKey(String key);

    /**
     * Deletes employee.
     *
     * @param employeeId id of report to delete
     * @throws NonExistingEntityException if employee for given id doesn't exist
     * @throws IllegalArgumentException if employeeId is null
     */
    void removeEmployee(Long employeeId);

}
