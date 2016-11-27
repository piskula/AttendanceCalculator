package sk.oravcok.posta.dao;

import org.springframework.stereotype.Repository;
import sk.oravcok.posta.entity.Employee;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Ondrej Oravcok on 26-Oct-16.
 */
@Repository
public class EmployeeDaoImpl implements EmployeeDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(Employee employee) {
        entityManager.persist(employee);
    }

    public Employee update(Employee employee) {
        return entityManager.merge(employee);
    }

    public void remove(Employee employee) {
        if(employee == null){
            throw new IllegalArgumentException("Deleting null Employee entity.");
        }
        entityManager.remove(findById(employee.getId()));
    }

    public Employee findById(Long id) {
        return entityManager.find(Employee.class, id);
    }

    public List<Employee> findAll() {
        TypedQuery<Employee> query = entityManager.createQuery("SELECT e FROM Employee e", Employee.class);
        return query.getResultList();
    }

    public Employee findByName(String name){
        try{
            TypedQuery<Employee> query = entityManager.createQuery("SELECT e FROM Employee e WHERE e.name = :name",
                    Employee.class).setParameter("name", name);
            return query.getSingleResult();
        } catch(NoResultException e){
            return null;
        }
    }

    public Employee findBySurname(String surname){
        try{
            TypedQuery<Employee> query = entityManager.createQuery("SELECT e FROM Employee e " +
                            "WHERE e.surname = :surname", Employee.class).setParameter("surname", surname);
            return query.getSingleResult();
        } catch(NoResultException e){
            return null;
        }
    }

    public List<Employee> findByFullName(String name) {
        TypedQuery<Employee> q = entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE concat(e.name, ' ', e.surname, ' ', e.name) LIKE :givenName",
                Employee.class).setParameter("givenName", "%" + name + "%");
        return q.getResultList();
    }
}
