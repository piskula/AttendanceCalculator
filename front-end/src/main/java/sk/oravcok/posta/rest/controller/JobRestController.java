package sk.oravcok.posta.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sk.oravcok.posta.dto.JobCreateDTO;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.dto.JobSearchDTO;
import sk.oravcok.posta.dto.JobUpdateDTO;
import sk.oravcok.posta.exception.DataManipulationException;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.JobFacade;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.ExistingResourceException;
import sk.oravcok.posta.rest.exception.RequestedResourceNotFound;
import sk.oravcok.posta.rest.exception.ValidationException;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Job REST controller
 *
 * @author Ondrej Oravcok
 * @version 16-Jan-17.
 */
@RestController
@RequestMapping(URI.JOBS)
public class JobRestController {

    private static final String ERROR_NOT_PLACE_NEITHER_EMPLOYEE = "You have to fill exactly one option: employeeId or placeId.";

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRestController.class);

    @Inject
    private JobFacade jobFacade;

    /**
     * gives you all jobs available in system
     *
     * e.g. curl -i -X GET http://localhost:8080/posta/rest/jobs
     *
     * @return all jobs
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<JobDTO> findAllJobs() {
        LOGGER.info("getting all jobs");
        return jobFacade.findAllJobs();
    }

    /**
     * gives you specific Job by id
     *
     * e.g. curl -i -X GET http://localhost:8080/posta/rest/jobs/findId/3
     *
     * @param id to find
     * @return specific Job
     */
    @RequestMapping(value = "/findId/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final JobDTO findJobById(@PathVariable("id") long id) {
        try {
            LOGGER.info("getting Job with id=" + id);
            return jobFacade.findJobById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Job with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }
    }

    /**
     * creates new Job in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"employeeId":"3","placeId":"2","jobDate":"2017-01-20","jobStart":[14,15],"jobEnd":[14,25]}' http://localhost:8080/posta/rest/jobs
     *
     * @param job to create
     * @return created job
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final JobDTO createJob(@RequestBody JobCreateDTO job) {
        try {
            LOGGER.info("creating Job " + job);
            Long id = jobFacade.createJob(job);
            return jobFacade.findJobById(id);
        } catch (DataManipulationException e) {
            throw new ValidationException("DataManipulationException thrown while creating job.", e);
        } catch (Exception e) {
            throw new ExistingResourceException(e);
        }
    }

    /**
     * removes Job from system
     *
     * curl -i -X DELETE http://localhost:8080/posta/rest/jobs/3
     *
     * @param id of Job to remove
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final void deleteJob(@PathVariable("id") long id) {
        try {
            LOGGER.info("deleting Job with id=" + id);
            jobFacade.removeJob(id);
        } catch (NonExistingEntityException | IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Job with id=" + id + " does not exist in system.", e);
        }
    }

    /**
     * updates Job in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"employeeId":"2","jobStart":[6,10]}' http://localhost:8080/posta/rest/jobs/update/3
     *
     * @param id of updating Job
     * @param jobDTO values, which we need to actualize
     * @return actualised Job
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final JobUpdateDTO updateJob(@PathVariable("id") long id, @RequestBody JobUpdateDTO jobDTO) {
        JobDTO previous;
        try {
            LOGGER.info("updating Job with id=" + id + ", with values " + jobDTO);
            previous = jobFacade.findJobById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Job with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }

        JobUpdateDTO actual = mergeDtosForUpdate(previous, jobDTO);
        try {
            jobFacade.updateJob(actual);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Job job=" + actual + " does not exist in system.", e);
        } catch (Exception e) {
            throw new RequestedResourceNotFound("Exception thrown while updating existing job.", e);
        }

        return actual;
    }

    private JobUpdateDTO mergeDtosForUpdate(JobDTO previous, JobUpdateDTO actual) {
        JobUpdateDTO job = new JobUpdateDTO();
        job.setId(previous.getId());

        job.setPlaceId(actual.getPlaceId() != null ? actual.getPlaceId() : previous.getPlace().getId());
        job.setEmployeeId(actual.getEmployeeId() != null ? actual.getEmployeeId() : previous.getEmployee().getId());
        job.setJobDate(actual.getJobDate() != null ? actual.getJobDate() : previous.getJobDate());
        job.setJobStart(actual.getJobStart() != null ? actual.getJobStart() : previous.getJobStart());
        job.setJobEnd(actual.getJobEnd() != null ? actual.getJobEnd() : previous.getJobEnd());

        return job;
    }

    /**
     * find Jobs by criteria in system
     *
     * curl -X POST -i -H "Content-Type: application/json" --data '{"employeeId":"1"}' http://localhost:8080/posta/rest/jobs/findByCriteria
     *
     * @param criteria to find by specific criteria
     * @return specific jobs
     */
    @RequestMapping(value = "/findByCriteria", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<JobDTO> findJobsByCriteria(@RequestBody JobSearchDTO criteria) {
        if(criteria.getPlaceId() != null && criteria.getEmployeeId() == null
                && criteria.getJobDateStart() == null && criteria.getJobDateEnd() == null) {
            try {
                LOGGER.info("getting jobs of place with placeId=" + criteria.getPlaceId());
                return jobFacade.findJobsOfPlace(criteria.getPlaceId());
            } catch (NonExistingEntityException e) {
                throw new RequestedResourceNotFound("Place with id=" + criteria.getPlaceId() + " does not exist in system.", e);
            } catch (IllegalArgumentException e) {
                throw new RequestedResourceNotFound("Argument placeId is illegal.", e);
            }
        }

        if(criteria.getPlaceId() == null && criteria.getEmployeeId() != null
                && criteria.getJobDateStart() == null && criteria.getJobDateEnd() == null) {
            try {
                LOGGER.info("getting jobs of employee with employeeId=" + criteria.getEmployeeId());
                return jobFacade.findJobsOfEmployee(criteria.getEmployeeId());
            } catch (NonExistingEntityException e) {
                throw new RequestedResourceNotFound("Employee with id=" + criteria.getEmployeeId() + " does not exist in system.", e);
            } catch (IllegalArgumentException e) {
                throw new RequestedResourceNotFound("Argument employeeId is illegal.", e);
            }
        }

        //Some of 'DATES' fields is filled
        if(criteria.getJobDateStart() != null || criteria.getJobDateEnd() != null) {
            if(criteria.getJobDateStart() == null || criteria.getJobDateEnd() == null) {
                //exactly one of 'DATES' fields is filled, so we need jobs of [places/employees/all] of one exact date
                LocalDate exactDate = criteria.getJobDateStart() != null ? criteria.getJobDateStart() : criteria.getJobDateEnd();
                if(criteria.getEmployeeId() == null && criteria.getPlaceId() == null) {
                    return jobFacade.findJobsOfDay(exactDate);
                }
                if(criteria.getEmployeeId() != null) {
                    return findJobsOfEmployeeOfDay(criteria.getEmployeeId(), exactDate, null);
                }
                if(criteria.getPlaceId() != null) {
                    return findJobsOfPlaceOfDay(criteria.getPlaceId(), exactDate, null);
                }
                //TODO implement find jobs of whole one day
                throw new ValidationException(ERROR_NOT_PLACE_NEITHER_EMPLOYEE);
            }
            else {
                //both dates are filled, so we need jobs of [places/employees] between some days
                if(criteria.getEmployeeId() != null) {
                    return findJobsOfEmployeeOfDay(criteria.getEmployeeId(), criteria.getJobDateStart(), criteria.getJobDateEnd());
                }
                if(criteria.getPlaceId() != null) {
                    return findJobsOfPlaceOfDay(criteria.getPlaceId(), criteria.getJobDateStart(), criteria.getJobDateEnd());
                }
                if(criteria.getJobDateStart().isBefore(criteria.getJobDateEnd())) {
                    List<JobDTO> result = new ArrayList<>();
                    for (LocalDate day = criteria.getJobDateStart(); !day.isAfter(criteria.getJobDateEnd()); day = day.plusDays(1)) {
                        result.addAll(jobFacade.findJobsOfDay(day));
                    }
                    return result;
                }
//                throw new ValidationException(ERROR_NOT_PLACE_NEITHER_EMPLOYEE);
            }
        }

//        if(criteria.getJobDateStart() != null && criteria.getJobDateEnd() != null) {
//            if(criteria.getJobDateStart().isBefore(criteria.getJobDateEnd())) {
//                List<JobDTO> result = new ArrayList<>();
//                for (LocalDate day = criteria.getJobDateStart(); !day.isAfter(criteria.getJobDateEnd()); day = day.plusDays(1)) {
//                    result.addAll(jobFacade.findJobsOfDay(day));
//                }
//                return result;
//            }
//        }
        throw new ValidationException("You have only following criteria options to specify: only employee, only place, only exact day, employee with date/dates, place with date/dates");
    }

    private List<JobDTO> findJobsOfEmployeeOfDay(Long employeeId, LocalDate exactDate, LocalDate toDay) {
        if(toDay == null) {
            try {
                LOGGER.info("getting jobs of Employee with employeeId=" + employeeId + " of day=" + exactDate);
                return jobFacade.findJobsOfEmployeeBetweenDays(employeeId, exactDate, exactDate);
            } catch (NonExistingEntityException e) {
                throw new RequestedResourceNotFound("Employee with id=" + employeeId + " does not exist in system.", e);
            } catch (IllegalArgumentException e) {
                throw new RequestedResourceNotFound("Argument employeeId is illegal.", e);
            }
        }
        else {
            try {
                LOGGER.info("getting jobs of Employee with employeeId=" + employeeId + " between days=" + exactDate +" till " + toDay);
                return jobFacade.findJobsOfEmployeeBetweenDays(employeeId, exactDate, toDay);
            } catch (NonExistingEntityException e) {
                throw new RequestedResourceNotFound("Employee with id=" + employeeId + " does not exist in system.", e);
            } catch (IllegalArgumentException e) {
                throw new RequestedResourceNotFound("Argument employeeId is illegal.", e);
            }
        }

    }

    private List<JobDTO> findJobsOfPlaceOfDay(Long placeId, LocalDate exactDate, LocalDate toDay) {
        if(toDay == null) {
            try {
                LOGGER.info("getting jobs of Place with placeId=" + placeId + " of day=" + exactDate);
                return jobFacade.findJobsOfPlaceBetweenDays(placeId, exactDate, exactDate);
            } catch (NonExistingEntityException e) {
                throw new RequestedResourceNotFound("Place with id=" + placeId + " does not exist in system.", e);
            } catch (IllegalArgumentException e) {
                throw new RequestedResourceNotFound("Argument placeId is illegal.", e);
            }
        }
        else {
            try {
                LOGGER.info("getting jobs of Place with placeId=" + placeId + " between days=" + exactDate + "till" + toDay);
                return jobFacade.findJobsOfPlaceBetweenDays(placeId, exactDate, toDay);
            } catch (NonExistingEntityException e) {
                throw new RequestedResourceNotFound("Place with id=" + placeId + " does not exist in system.", e);
            } catch (IllegalArgumentException e) {
                throw new RequestedResourceNotFound("Argument placeId is illegal.", e);
            }
        }
    }

}
