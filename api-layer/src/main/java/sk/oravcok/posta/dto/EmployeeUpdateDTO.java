package sk.oravcok.posta.dto;

import java.util.Objects;

/**
 * DTO for update Employee
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
public class EmployeeUpdateDTO extends EmployeeCreateDTO {

    private Long id;

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
        if(!(object instanceof EmployeeUpdateDTO)) return false;

        final EmployeeUpdateDTO other = (EmployeeUpdateDTO) object;
        if (id != null ? !id.equals(other.getId()) : other.getId() != null) return false;

        return super.equals(other);
    }

    @Override
    public int hashCode(){
        return super.hashCode() + Objects.hash(id);
    }

    @Override
    public String toString(){
        return "EmployeeUpdateDTO{id=" + id + ", " + super.toStringValues();
    }
}
