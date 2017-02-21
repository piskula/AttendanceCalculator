package sk.oravcok.posta.service;

import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for Job
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
public interface JobService {

    /**
     * Creates new Job.
     *
     * @param job to be created
     * @throws IllegalArgumentException if job is null
     */
    void create(Job job);

    /**
     * Updates Job.
     *
     * @param job entity to be updated
     * @return updated job entity
     * @throws IllegalArgumentException if job is null
     */
    Job update(Job job);

    /**
     * Returns the job entity attached to the given id.
     *
     * @param id id of the job entity to be returned
     * @return the job entity with given id
     * @throws IllegalArgumentException if id is null
     */
    Job findById(Long id);

    /**
     * Returns all job entities.
     *
     * @return all jobs
     */
    List<Job> findAll();

    /**
     * Returns jobs of specific employee
     *
     * @param employee to find his jobs
     * @return these jobs
     * @throws IllegalArgumentException if employee is null
     */
    List<Job> findJobsOfEmployee(Employee employee);

    /**
     * Returns jobs of specific place
     *
     * @param place to find its jobs
     * @return these jobs
     * @throws IllegalArgumentException if place is null
     */
    List<Job> findJobsOfPlace(Place place);

    /**
     * Returns jobs of specific day
     *
     * @param exactDay to find jobs of this specific day
     * @return these jobs
     * @throws IllegalArgumentException if exactDay is null
     */
    List<Job> findJobsOfDay(LocalDate exactDay);

    /**
     * Returns jobs of employee between specific days
     *
     * @param employee to find his jobs
     * @param fromDay start day (including)
     * @param toDay end day (including)
     * @return these jobs
     * @throws IllegalArgumentException if employee, fromDay or toDay is null
     */
    List<Job> findJobsOfEmployeeBetweenDays(Employee employee, LocalDate fromDay, LocalDate toDay);

    /**
     * Returns jobs of employee between specific days
     *
     * @param place to find its jobs
     * @param fromDay start day (including)
     * @param toDay end day (including)
     * @return these jobs
     * @throws IllegalArgumentException if place, fromDay or toDay is null
     */
    List<Job> findJobsOfPlaceBetweenDays(Place place, LocalDate fromDay, LocalDate toDay);

    /**
     * Removes the job entity from persistence context.
     *
     * @param job job to be removed
     * @throws IllegalArgumentException if job is null
     */
    void remove(Job job);

}
