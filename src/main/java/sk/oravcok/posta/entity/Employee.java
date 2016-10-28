package sk.oravcok.posta.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Ondrej Oravcok on 26-Oct-16.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String title;

    private LocalDate birth;

    private String phone;

    private String address;

    @Pattern(regexp = ".+@.+\\....?")
    private String email;

    private String annotation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        if(!(object instanceof Employee)) return false;

        final Employee other = (Employee) object;

        if (name != null ? !name.equals(other.name) : other.name != null) return false;
        if (surname != null ? !surname.equals(other.surname) : other.surname != null) return false;
        if (title != null ? !title.equals(other.title) : other.title != null) return false;
        if (birth != null ? !birth.equals(other.birth) : other.birth != null) return false;
        if (phone != null ? !phone.equals(other.phone) : other.phone != null) return false;
        if (address != null ? !address.equals(other.address) : other.address != null) return false;
        if (email != null ? !email.equals(other.email) : other.email != null) return false;
        if (annotation != null ? !annotation.equals(other.annotation) : other.annotation != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, surname, title, birth, phone, address, email, annotation);
    }
}
