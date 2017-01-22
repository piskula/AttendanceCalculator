package sk.oravcok.posta.dto;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Ondrej Oravcok
 * @version 20-Jan-17.
 */
public class JobSearchDTO {

    private Long employeeId;

    private Long placeId;

    private LocalDate jobDateStart;

    private LocalDate jobDateEnd;

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

    public LocalDate getJobDateStart() {
        return jobDateStart;
    }

    public void setJobDateStart(LocalDate jobDateStart) {
        this.jobDateStart = jobDateStart;
    }

    public LocalDate getJobDateEnd() {
        return jobDateEnd;
    }

    public void setJobDateEnd(LocalDate jobDateEnd) {
        this.jobDateEnd = jobDateEnd;
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return true;
        if(!(object instanceof JobSearchDTO)) return false;

        final JobSearchDTO other = (JobSearchDTO) object;

        if (employeeId != null ? !employeeId.equals(other.getEmployeeId()) : other.getEmployeeId() != null) return false;
        if (placeId != null ? !placeId.equals(other.getPlaceId()) : other.getPlaceId() != null) return false;
        if (jobDateStart != null ? !jobDateStart.equals(other.getJobDateStart()) : other.getJobDateStart() != null) return false;
        if (jobDateEnd != null ? !jobDateEnd.equals(other.getJobDateEnd()) : other.getJobDateEnd() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(employeeId, placeId, jobDateStart, jobDateEnd);
    }

    @Override
    public String toString(){
        return "JobSearchDTO{" +
                "employeeId=" + employeeId +
                ", placeId=" + placeId +
                ", jobDateStart=" + jobDateStart +
                ", jobDateEnd=" + jobDateEnd +
                '}';
    }

}
