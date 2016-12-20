package sk.oravcok.posta.service;

import org.springframework.stereotype.Service;
import sk.oravcok.posta.dao.JobDao;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.exception.ServiceExceptionTranslate;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Ondrej Oravcok on 20-Dec-16.
 */
@Service
@ServiceExceptionTranslate
public class JobServiceImpl implements JobService {

    @Inject
    private JobDao jobDao;

    @Override
    public void create(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Job is null - cannot create");
        }
        jobDao.create(job);
    }

    @Override
    public Job update(Job job) {
        if(job == null) {
            throw new IllegalArgumentException("Job is null - cannot update");
        }
        return jobDao.update(job);
    }

    @Override
    public Job findById(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("id of job is null - cannot find by id");
        }
        return jobDao.findById(id);
    }

    @Override
    public List<Job> findAll() {
        return jobDao.findAll();
    }

    @Override
    public List<Job> findJobsOfEmployee(Employee employee) {
        if(employee == null) {
            throw new IllegalArgumentException("employee is null - cannot find his jobs");
        }
        return jobDao.findJobsOfEmployee(employee);
    }

    @Override
    public List<Job> findJobsOfPlace(Place place) {
        if(place == null) {
            throw new IllegalArgumentException("place is null - cannot find jobs on it");
        }
        return jobDao.findJobsOfPlace(place);
    }

    @Override
    public List<Job> findJobsOfEmployeeBetweenDays(Employee employee, LocalDate fromDay, LocalDate toDay) {
        if(employee == null) {
            throw new IllegalArgumentException("employee is null - cannot find his jobs");
        }
        if(fromDay == null || toDay == null) {
            throw new IllegalArgumentException("LocalDates are null - cannot find jobs of employee");
        }
        return jobDao.findJobsOfEmployeeBetweenDays(employee, fromDay, toDay);
    }

    @Override
    public List<Job> findJobsOfPlaceBetweenDays(Place place, LocalDate fromDay, LocalDate toDay) {
        if(place == null) {
            throw new IllegalArgumentException("place is null - cannot find jobs on it");
        }
        if(fromDay == null || toDay == null) {
            throw new IllegalArgumentException("LocalDates are null - cannot find jobs of employee");
        }
        return jobDao.findJobsOfPlaceBetweenDays(place, fromDay, toDay);
    }

    @Override
    public void remove(Job job) {
        if(job == null) {
            throw new IllegalArgumentException("job is null - cannot remove");
        }
        jobDao.remove(job);
    }
}
