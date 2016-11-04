package sk.oravcok.posta.dao;

import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Ondrej Oravcok on 27-Oct-16.
 */
public interface JobDao {

    /**
     * Save new job to DB
     *
     * @param job to be persisted
     */
    void create(Job job);

    /**
     * Update already saved Job in DB
     *
     * @param job to be updated
     * @return updated job
     */
    Job update(Job job);

    /**
     * Removes job from DB
     *
     * @param job to be removed
     */
    void remove(Job job);

    /**
     * search for job by its specific id
     *
     * @param id of job to be look for
     * @return specific job
     */
    Job findById(Long id);

    /**
     * gives you all jobs stored in DB
     *
     * @return all jobs
     */
    List<Job> findAll();

    /**
     * find jobs of employee
     *
     * @param employee given employee
     * @return specific jobs
     */
    List<Job> findJobsOfEmployee(Employee employee);

    /**
     * find jobs of place
     *
     * @param place given place
     * @return specific jobs
     */
    List<Job> findJobsOfPlace(Place place);

    /**
     * find jobs of date
     *
     * @param date given place
     * @return specific jobs
     */
    List<Job> findJobsOfDate(LocalDate date);

    /**
     * find jobs of place and date
     *
     * @param place given place
     * @param date given date
     * @return specific jobs
     */
    List<Job> findJobsOfPlaceAndDate(Place place, LocalDate date);

    /**
     * find jobs of employee and date
     *
     * @param employee given employee
     * @param date given date
     * @return specific jobs
     */
    List<Job> findJobsOfEmployeeAndDate(Employee employee, LocalDate date);

    /**
     * find jobs of place between days
     *
     * @param place given place
     * @param startDate given start date
     * @param endDate given end date
     * @return specific jobs
     */
    List<Job> findJobsOfPlaceBetweenDays(Place place, LocalDate startDate, LocalDate endDate);

    /**
     * find jobs of employee between days
     *
     * @param employee given employee
     * @param startDate given start date
     * @param endDate given end date
     * @return specific jobs
     */
    List<Job> findJobsOfEmployeeBetweenDays(Employee employee, LocalDate startDate, LocalDate endDate);
}
