package sk.oravcok.posta.entity;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by Ondrej Oravcok on 27-Oct-16.
 */
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Employee employee;

    @NotNull
    @ManyToOne
    private Place place;

    @NotNull
    private LocalTime jobStart;

    @NotNull
    private LocalTime jobEnd;

    @NotNull
    private LocalDate jobDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public LocalTime getJobStart() {
        return jobStart;
    }

    public void setJobStart(LocalTime jobStart) {
        if(jobEnd != null)
            if(jobEnd.isBefore(jobStart))
                throw new IllegalArgumentException("Job end cannot be before start!");
        this.jobStart = jobStart;
    }

    public LocalTime getJobEnd() {
        return jobEnd;
    }

    public void setJobEnd(LocalTime jobEnd) {
        if(jobStart != null)
            if(jobStart.isAfter(jobEnd))
                throw new IllegalArgumentException("Job start cannot be after end!");
        this.jobEnd = jobEnd;
    }

    public LocalDate getJobDate() {
        return jobDate;
    }

    public void setJobDate(LocalDate jobDate) {
        this.jobDate = jobDate;
    }
}
