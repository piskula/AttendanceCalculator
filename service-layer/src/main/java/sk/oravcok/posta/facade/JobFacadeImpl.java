package sk.oravcok.posta.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.oravcok.posta.dto.JobCreateDTO;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.dto.JobUpdateDTO;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.service.EmployeeService;
import sk.oravcok.posta.service.JobService;
import sk.oravcok.posta.service.PlaceService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of job facade
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
@Service
@Transactional
public class JobFacadeImpl implements JobFacade {

    @Inject
    private JobService jobService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private PlaceService placeService;

    @Inject
    private BeanMappingService beanMappingService;

    @Override
    public Long createJob(JobCreateDTO jobCreateDTO) {
        if(jobCreateDTO == null){
            throw new IllegalArgumentException("job is null - cannot create job");
        }

        Job job = beanMappingService.mapTo(jobCreateDTO, Job.class);

        job.setEmployee(getEmployeeOrThrowException(jobCreateDTO.getEmployeeId()));
        job.setPlace(getPlaceOrThrowException(jobCreateDTO.getPlaceId()));

        jobService.create(job);
        return job.getId();
    }

    @Override
    public void updateJob(JobUpdateDTO jobDTO) {
        if(jobDTO == null){
            throw new IllegalArgumentException("job is null - cannot update job");
        }

        Job job = beanMappingService.mapTo(jobDTO, Job.class);
        if(job.getId() == null){
            throw new IllegalArgumentException("jobId is null - cannot update job");
        }
        if(jobService.findById(job.getId()) == null){
            throw new NonExistingEntityException("Cannot update non existing Job");
        }

        job.setEmployee(getEmployeeOrThrowException(jobDTO.getEmployeeId()));
        job.setPlace(getPlaceOrThrowException(jobDTO.getPlaceId()));

        jobService.update(job);
    }

    @Override
    public List<JobDTO> findAllJobs() {
        return beanMappingService.mapTo(jobService.findAll(), JobDTO.class);
    }

    @Override
    public JobDTO findJobById(Long jobId) {
        if(jobId == null){
            throw new IllegalArgumentException("jobId is null - cannot find job by id");
        }

        Job job = jobService.findById(jobId);
        if(job == null){
            throw new NonExistingEntityException("Job with id " + jobId + " does not exist");
        }
        return beanMappingService.mapTo(job, JobDTO.class);
    }

    @Override
    public List<JobDTO> findJobsOfEmployee(Long employeeId) {
        Employee employee = getEmployeeOrThrowException(employeeId);
        return beanMappingService.mapTo(jobService.findJobsOfEmployee(employee), JobDTO.class);
    }

    @Override
    public List<JobDTO> findJobsOfPlace(Long placeId) {
        Place place = getPlaceOrThrowException(placeId);
        return beanMappingService.mapTo(jobService.findJobsOfPlace(place), JobDTO.class);
    }

    @Override
    public List<JobDTO> findJobsOfEmployeeBetweenDays(Long employeeId, LocalDate fromDay, LocalDate toDay) {
        if(fromDay == null){
            throw new IllegalArgumentException("fromDay is null - cannot find jobs of employee between days");
        }
        if(toDay == null){
            throw new IllegalArgumentException("toDay is null - cannot find jobs of employee between days");
        }
        if(fromDay.isAfter(toDay)){
            throw new IllegalArgumentException("fromDay must be before toDay");
        }

        Employee employee = getEmployeeOrThrowException(employeeId);
        return beanMappingService.mapTo(jobService.findJobsOfEmployeeBetweenDays(employee, fromDay, toDay), JobDTO.class);
    }

    @Override
    public List<JobDTO> findJobsOfPlaceBetweenDays(Long placeId, LocalDate fromDay, LocalDate toDay) {
        if(fromDay == null){
            throw new IllegalArgumentException("fromDay is null - cannot find jobs of place between days");
        }
        if(toDay == null){
            throw new IllegalArgumentException("toDay is null - cannot find jobs of place between days");
        }
        if(fromDay.isAfter(toDay)){
            throw new IllegalArgumentException("fromDay must be before toDay");
        }

        Place place = getPlaceOrThrowException(placeId);
        return beanMappingService.mapTo(jobService.findJobsOfPlaceBetweenDays(place, fromDay, toDay), JobDTO.class);
    }

    @Override
    public void removeJob(Long jobId) {
        if(jobId == null){
            throw new IllegalArgumentException("jobId is null");
        }

        Job job = jobService.findById(jobId);
        if(job == null){
            throw new NonExistingEntityException("Job with id=" + jobId + " does not exist");
        }
        jobService.remove(job);
    }

    /**
     * @param employeeId employee id
     * @return existing employee if already exists
     * @throws IllegalArgumentException if employeeId is null
     * @throws NonExistingEntityException if employee with this id does not exist
     */
    private Employee getEmployeeOrThrowException(Long employeeId){
        if(employeeId == null){
            throw new IllegalArgumentException("employeeId cannot be null when working with Job");
        }

        Employee employee = employeeService.findById(employeeId);
        if(employee == null){
            throw new NonExistingEntityException("employee (for job) does not exist: employeeId=" + employeeId);
        }
        return employee;
    }

    /**
     * @param placeId place id
     * @return existing place if already exists
     * @throws IllegalArgumentException if placeId is null
     * @throws NonExistingEntityException if place with this id does not exist
     */
    private Place getPlaceOrThrowException(Long placeId){
        if(placeId == null){
            throw new IllegalArgumentException("placeId cannot be null when working with Job");
        }

        Place place = placeService.findById(placeId);
        if(place == null){
            throw new NonExistingEntityException("place (for job) does not exist: placeId=" + placeId);
        }
        return place;
    }

}
