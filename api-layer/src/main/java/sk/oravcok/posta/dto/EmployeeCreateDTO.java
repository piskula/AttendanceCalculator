package sk.oravcok.posta.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO for creating Employee
 *
 * Created by Ondrej Oravcok on 18-Nov-16.
 */
public class EmployeeCreateDTO {

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String title;

    private LocalDate birth;

    private String phone;

    private String address;

    private String email;

    private String annotation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object object){
        if(this == object) return true;
        if(!(object instanceof EmployeeCreateDTO)) return false;

        final EmployeeCreateDTO other = (EmployeeCreateDTO) object;

        if (name != null ? !name.equals(other.getName()) : other.getName() != null) return false;
        if (surname != null ? !surname.equals(other.getSurname()) : other.getSurname() != null) return false;
        if (title != null ? !title.equals(other.getTitle()) : other.getTitle() != null) return false;
        if (birth != null ? !birth.equals(other.getBirth()) : other.getBirth() != null) return false;
        if (phone != null ? !phone.equals(other.getPhone()) : other.getPhone() != null) return false;
        if (address != null ? !address.equals(other.getAddress()) : other.getAddress() != null) return false;
        if (email != null ? !email.equals(other.getEmail()) : other.getEmail() != null) return false;
        if (annotation != null ? !annotation.equals(other.getAnnotation()) : other.getAnnotation() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, surname, title, birth, phone, address, email, annotation);
    }

    @Override
    public String toString(){
        return "EmployeeCreateDTO{"
                + "name=" + name
                + ", surname=" + surname
                + ", title=" + title
                + ", birth=" + birth
                + ", phone=" + phone
                + ", address=" + address
                + ", email=" + email
                + ", annotation=" + annotation
                +"}";
    }
}
