package sk.oravcok.posta.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.oravcok.posta.dto.JobDTO;
import sk.oravcok.posta.exception.NonExistingEntityException;
import sk.oravcok.posta.facade.JobFacade;
import sk.oravcok.posta.rest.configuration.URI;
import sk.oravcok.posta.rest.exception.RequestedResourceNotFound;

import javax.inject.Inject;
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
    public final JobDTO findPlaceById(@PathVariable("id") long id) {
        try {
            LOGGER.info("getting Job with id=" + id);
            return jobFacade.findJobById(id);
        } catch (NonExistingEntityException e) {
            throw new RequestedResourceNotFound("Job with id=" + id + " does not exist in system.", e);
        } catch (IllegalArgumentException e) {
            throw new RequestedResourceNotFound("Argument id is illegal.", e);
        }
    }

}
