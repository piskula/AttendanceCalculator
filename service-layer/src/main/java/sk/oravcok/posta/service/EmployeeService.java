package sk.oravcok.posta.service;

import sk.oravcok.posta.entity.Employee;

import java.util.List;

/**
 * Business logic for Employee
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
public interface EmployeeService {

    /**
     * Creates new Employee. If calories are not set, their values is computed.
     *
     * @param employee to be created
     * @throws IllegalArgumentException if employee is null
     */
    void create(Employee employee);

    /**
     * Updates Employee.
     *
     * @param employee entity to be updated
     * @return updated employee entity
     * @throws IllegalArgumentException if employee is null
     */
    Employee update(Employee employee);

    /**
     * Returns the employee entity attached to the given id.
     *
     * @param id id of the employee entity to be returned
     * @return the employee entity with given id
     */
    Employee findById(Long id);

    /**
     * Returns all employee entities.
     *
     * @return all employees
     */
    List<Employee> findAll();

    /**
     * Removes the employee entity from persistence context.
     *
     * @param employee employee to be removed
     * @throws IllegalArgumentException if employee is null
     */
    void remove(Employee employee);

}
