package sk.oravcok.posta.service;

import org.springframework.stereotype.Service;
import sk.oravcok.posta.dao.EmployeeDao;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.exception.ServiceExceptionTranslate;

import javax.inject.Inject;
import java.util.List;

/**
 * implemetnation of EmployeeService
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
@Service
@ServiceExceptionTranslate
public class EmployeeServiceImpl implements EmployeeService {

    @Inject
    private EmployeeDao employeeDao;

    @Override
    public void create(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee is null - cannot create");
        }
        employeeDao.create(employee);
    }

    @Override
    public Employee update(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee is null - cannot update");
        }
        return employeeDao.update(employee);
    }

    @Override
    public Employee findById(Long id) {
        if(id == null){
            throw new IllegalArgumentException("id of employee is null - cannot find by id");
        }
        return employeeDao.findById(id);
    }

    @Override
    public List<Employee> findEmployeesByKey(String key) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty - cannot filter employees");
        }
        return employeeDao.findByFullName(key);
    }

    @Override
    public List<Employee> findAll() {
        return employeeDao.findAll();
    }

    @Override
    public void remove(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee is null");
        }
        employeeDao.remove(employee);
    }

}
