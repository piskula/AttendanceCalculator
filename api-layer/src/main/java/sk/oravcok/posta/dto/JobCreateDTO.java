package sk.oravcok.posta.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * DTO for create Job
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
public class JobCreateDTO {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long placeId;

    @NotNull
    private LocalTime jobStart;

    @NotNull
    private LocalTime jobEnd;

    @NotNull
    private LocalDate jobDate;

    //end of attributes


    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
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
        if(object == null) return false;
        if(!(object instanceof JobCreateDTO)) return false;

        final JobCreateDTO other = (JobCreateDTO) object;

        if (employeeId != null ? !employeeId.equals(other.getEmployeeId()) : other.getEmployeeId() != null) return false;
        if (placeId != null ? !placeId.equals(other.getPlaceId()) : other.getPlaceId() != null) return false;
        if (jobStart != null ? !jobStart.equals(other.getJobStart()) : other.getJobStart() != null) return false;
        if (jobEnd != null ? !jobEnd.equals(other.getJobEnd()) : other.getJobEnd() != null) return false;
        if (jobDate != null ? !jobDate.equals(other.getJobDate()) : other.getJobDate() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(employeeId, placeId, jobStart, jobEnd, jobDate);
    }

    @Override
    public String toString(){
        return "JobCreateDTO{" + this.toStringValues();
    }

    protected String toStringValues(){
        return "employeeId=" + employeeId +
                ", placeId=" + placeId +
                ", jobStart=" + jobStart +
                ", jobEnd=" + jobEnd +
                ", jobDate=" + jobDate +
                '}';
    }
}
