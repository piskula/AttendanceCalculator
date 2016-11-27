package sk.oravcok.posta.dto;

import java.util.Objects;

/**
 * DTO for updating Job
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
public class JobUpdateDTO extends JobCreateDTO {

    Long id;

    //end of attributes

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return true;
        if(object == null) return false;
        if(!(object instanceof JobUpdateDTO)) return false;

        final JobUpdateDTO other = (JobUpdateDTO) object;
        if (id != null ? !id.equals(other.getId()) : other.getId() != null) return false;

        return super.equals(other);
    }

    @Override
    public int hashCode(){
        return super.hashCode() + Objects.hash(id);
    }

    @Override
    public String toString(){
        return "JobUpdateDTO{id=" + id + ", " + super.toStringValues();
    }
}
