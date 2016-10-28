package sk.oravcok.posta.entity;

import sk.oravcok.posta.enums.PlaceType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by Ondrej Oravcok on 27-Oct-16.
 */
@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Enumerated
    @NotNull
    private PlaceType placeType;

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

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
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
        if(!(object instanceof Place)) return false;

        final Place other = (Place) object;

        if (name != null ? !name.equals(other.name) : other.name != null) return false;
        if (placeType != null ? !placeType.equals(other.placeType) : other.placeType != null) return false;
        if (annotation != null ? !annotation.equals(other.annotation) : other.annotation != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, placeType, annotation);
    }
}
