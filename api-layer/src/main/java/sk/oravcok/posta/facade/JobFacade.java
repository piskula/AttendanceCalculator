package sk.oravcok.posta.facade;

import sk.oravcok.posta.dto.JobCreateDTO;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.dto.JobUpdateDTO;
import sk.oravcok.posta.exception.NonExistingEntityException;

import java.time.LocalDate;
import java.util.List;

/**
 * Facade interface for Job
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
public interface JobFacade {

    /**
     * Creates job.
     *
     * @param jobCreateDTO entity to be created
     * @return id of newly created job
     * @throws IllegalArgumentException if job is null
     * @thrown NonExistingEntityException if Place or Emplyee does not exist
     */
    Long createJob(JobCreateDTO jobCreateDTO);

    /**
     * Updates job.
     *
     * @param jobUpdateDTO entity to be updated
     * @throws IllegalArgumentException   if job is null
     * @throws NonExistingEntityException on attempt to update non existing job
     * @throws NonExistingEntityException if Place or Employee does not exist
     */
    void updateJob(JobUpdateDTO jobUpdateDTO);

    /**
     * Returns all jobs.
     *
     * @return list of all job entities or empty list if no job exists
     */
    List<JobDTO> findAllJobs();

    /**
     * Returns job according to given id.
     *
     * @param jobId
     * @return job identified by unique id
     * @throws NonExistingEntityException if job for given id doesn't exist
     * @throws IllegalArgumentException if jobId is null
     */
    JobDTO findJobById(Long jobId);

    /**
     * Returns jobs of specific Employee
     *
     * @param employeeId id of employee
     * @return its jobs
     * @throws IllegalArgumentException if employeeId is null
     * @throws NonExistingEntityException if Employee does not exist
     */
    List<JobDTO> findJobsOfEmployee(Long employeeId);

    /**
     * Returns jobs of specific Place
     *
     * @param placeId id of place
     * @return jobs of this place
     * @throws IllegalArgumentException if placeId is null
     * @throws NonExistingEntityException if place does not exist
     */
    List<JobDTO> findJobsOfPlace(Long placeId);

    /**
     * Returns jobs of employee between specific days
     *
     * @param employeeId id of employee
     * @param fromDay start day (including)
     * @param toDay end day (including)
     * @return these jobs
     * @throws IllegalArgumentException if employeeId, fromDay or toDay is null
     * @throws IllegalArgumentException if toDay is before fromDay
     * @throws NonExistingEntityException if Employee does not exist
     */
    List<JobDTO> findJobsOfEmployeeBetweenDays(Long employeeId, LocalDate fromDay, LocalDate toDay);

    /**
     * Returns jobs on place between specific days
     *
     * @param placeId id of place
     * @param fromDay start day (including)
     * @param toDay end day (including)
     * @return these jobs
     * @throws IllegalArgumentException if placeId, fromDay, toDay is null
     * @throws IllegalArgumentException if toDay is before fromDay
     * @throws NonExistingEntityException if Place does not exist
     */
    List<JobDTO> findJobsOfPlaceBetweenDays(Long placeId, LocalDate fromDay, LocalDate toDay);

    /**
     * Deletes job.
     *
     * @param jobId id of report to delete
     * @throws NonExistingEntityException if job for given id doesn't exist
     * @throws IllegalArgumentException if jobId is null
     */
    void removeJob(Long jobId);

}
