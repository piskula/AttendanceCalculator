package sk.oravcok.posta.dto;

import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Place;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * DTO for Job
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
public class JobDTO {

    private Long id;

    @NotNull
    private Employee employee;

    @NotNull
    private Place place;

    @NotNull
    private LocalTime jobStart;

    @NotNull
    private LocalTime jobEnd;

    @NotNull
    private LocalDate jobDate;

    //end od attributes

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
        this.jobStart = jobStart;
    }

    public LocalTime getJobEnd() {
        return jobEnd;
    }

    public void setJobEnd(LocalTime jobEnd) {
        this.jobEnd = jobEnd;
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
        if(!(object instanceof JobDTO)) return false;

        final JobDTO other = (JobDTO) object;

        if (employee != null ? !employee.equals(other.getEmployee()) : other.getEmployee() != null) return false;
        if (place != null ? !place.equals(other.getPlace()) : other.getPlace() != null) return false;
        if (jobStart != null ? !jobStart.equals(other.getJobStart()) : other.getJobStart() != null) return false;
        if (jobEnd != null ? !jobEnd.equals(other.getJobEnd()) : other.getJobEnd() != null) return false;
        if (jobDate != null ? !jobDate.equals(other.getJobDate()) : other.getJobDate() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(employee, place, jobStart, jobEnd, jobDate);
    }

    @Override
    public String toString(){
        return "JobDTO{" +
                "id=" + id +
                ", employee=" + employee +
                ", place=" + place +
                ", jobStart=" + jobStart +
                ", jobEnd=" + jobEnd +
                ", jobDate=" + jobDate +
                '}';
    }
}
