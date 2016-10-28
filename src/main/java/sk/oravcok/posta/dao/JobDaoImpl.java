package sk.oravcok.posta.dao;

import org.springframework.stereotype.Repository;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Ondrej Oravcok on 28-Oct-16.
 */
@Transactional
@Repository
public class JobDaoImpl implements JobDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(Job job) {
        entityManager.persist(job);
    }

    @Override
    public Job update(Job job) {
        return entityManager.merge(job);
    }

    @Override
    public void remove(Job job) {
        if(job == null){
            throw new IllegalArgumentException("Deleting null Job entity.");
        }
        entityManager.remove(findById(job.getId()));
    }

    @Override
    public Job findById(Long id) {
        return entityManager.find(Job.class, id);
    }

    @Override
    public List<Job> findAll() {
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j", Job.class);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfEmployee(Employee employee) {
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.employee = :employee",
                Job.class).setParameter("employee", employee);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfPlace(Place place) {
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.place = :place",
                Job.class).setParameter("place", place);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfDate(LocalDate date) {
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.jobDate = :date",
                Job.class).setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfEmployeeAndDate(Employee employee, LocalDate date) {
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.employee = :employee AND j.jobDate = :date",
                Job.class).setParameter("employee", employee).setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfPlaceAndDate(Place place, LocalDate date) {
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.place = :place AND j.jobDate = :date",
                Job.class).setParameter("place", place).setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfPlaceBetweenDays(Place place, LocalDate startDate, LocalDate endDate) {
        if(startDate.isEqual(endDate)){
            return findJobsOfPlaceAndDate(place, startDate);
        }
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.place = :place AND j.jobDate BETWEEN :startdate AND :enddate",
                Job.class).setParameter("place", place).setParameter("startdate", startDate).setParameter("enddate", endDate);
        return query.getResultList();
    }

    @Override
    public List<Job> findJobsOfEmployeeBetweenDays(Employee employee, LocalDate startDate, LocalDate endDate) {
        if(startDate.isEqual(endDate)){
            return findJobsOfEmployeeAndDate(employee, startDate);
        }
        TypedQuery<Job> query = entityManager.createQuery("SELECT j FROM Job j WHERE j.employee = :employee AND j.jobDate BETWEEN :startdate AND :enddate",
                Job.class).setParameter("employee", employee).setParameter("startdate", startDate).setParameter("enddate", endDate);
        return query.getResultList();
    }
}
