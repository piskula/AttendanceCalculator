package sk.oravcok.posta.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.oravcok.posta.dto.JobCreateDTO;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.dto.JobUpdateDTO;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.mapping.BeanMappingService;
import sk.oravcok.posta.service.JobService;

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
    private BeanMappingService beanMappingService;

    @Override
    public Long createJob(JobCreateDTO jobCreateDTO) {
        if(jobCreateDTO == null){
            throw new IllegalArgumentException("job cannot be null");
        }

        Job job = beanMappingService.mapTo(jobCreateDTO, Job.class);

        //TODO

        jobService.create(job);
        return job.getId();
    }

    @Override
    public void updateJob(JobUpdateDTO jobUpdateDTO) {
        if(jobUpdateDTO == null){
            throw new IllegalArgumentException("job cannot be null");
        }

        Job job = beanMappingService.mapTo(jobUpdateDTO, Job.class);
        if(jobService.findById(job.getId()) == null){
            throw new NonExistingEntityException("Cannot update non existing Job");
        }

        //TODO

        jobService.update(job);
    }

    @Override
    public List<JobDTO> findAllJobs() {
        return null;
    }

    @Override
    public JobDTO findJobById(Long jobId) {
        return null;
    }

    @Override
    public List<JobDTO> findJobsOfEmployee(Long employeeId) {
        return null;
    }

    @Override
    public List<JobDTO> findJobsOfPlace(Long placeId) {
        return null;
    }

    @Override
    public List<JobDTO> findJobsOfEmployeeBetweenDays(Long employeeId, LocalDate fromDay, LocalDate toDay) {
        return null;
    }

    @Override
    public List<JobDTO> findJobsOfPlaceBetweenDays(Long placeId, LocalDate fromDay, LocalDate toDay) {
        return null;
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
        jobService.remove(new Job(jobId));
    }

}
