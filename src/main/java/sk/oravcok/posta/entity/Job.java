package sk.oravcok.posta.entity;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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
    @Column(nullable = false)
    private LocalTime jobStart;

    @NotNull
    @Column(nullable = false)
    private LocalTime jobEnd;

    @NotNull
    @Column(nullable = false)
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
        if (jobStart == null){
            this.jobStart = null;
        }else{
            if(jobEnd != null)
                if(jobEnd.isBefore(jobStart))
                    throw new IllegalArgumentException("Job end cannot be before start!");
            this.jobStart = jobStart;
        }
    }

    public LocalTime getJobEnd() {
        return jobEnd;
    }

    public void setJobEnd(LocalTime jobEnd) {
        if(jobEnd == null) {
            this.jobEnd = null;
        }else{
            if(jobStart != null)
                if(jobStart.isAfter(jobEnd))
                    throw new IllegalArgumentException("Job start cannot be after end!");
            this.jobEnd = jobEnd;
        }
    }

    public LocalDate getJobDate() {
        return jobDate;
    }

    public void setJobDate(LocalDate jobDate) {
        this.jobDate = jobDate;
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return true;
        if(object == null) return false;
        if(!(object instanceof Job)) return false;

        final Job other = (Job) object;

        if (employee != null ? !employee.equals(other.employee) : other.employee != null) return false;
        if (place != null ? !place.equals(other.place) : other.place != null) return false;
        if (jobStart != null ? !jobStart.equals(other.jobStart) : other.jobStart != null) return false;
        if (jobEnd != null ? !jobEnd.equals(other.jobEnd) : other.jobEnd != null) return false;
        if (jobDate != null ? !jobDate.equals(other.jobDate) : other.jobDate != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(employee, place, jobStart, jobEnd, jobDate);
    }
}
