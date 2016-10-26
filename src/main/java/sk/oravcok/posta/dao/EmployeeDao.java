package sk.oravcok.posta.dao;

import sk.oravcok.posta.entity.Employee;

import java.util.List;

/**
 * Created by Ondrej Oravcok on 26-Oct-16.
 */
public interface EmployeeDao {

    /**
     * Save new employee to DB
     *
     * @param employee to be persisted
     */
    void create(Employee employee);

    /**
     * Update already saved Employee in DB
     *
     * @param employee to be updated
     * @return updated employee
     */
    Employee update(Employee employee);

    /**
     * Removes employee from DB
     *
     * @param employee to be removed
     */
    void remove(Employee employee);

    /**
     * search for employee by its specific id
     *
     * @param id of employee to be look for
     * @return specific employee
     */
    Employee findById(Long id);

    /**
     * gives you all employees stored in DB
     *
     * @return all employees
     */
    List<Employee> findAll();

    /**
     * find specific employee by it's name
     *
     * @param name given name
     * @return specific employee, if found
     */
    public Employee findByName(String name);

}
