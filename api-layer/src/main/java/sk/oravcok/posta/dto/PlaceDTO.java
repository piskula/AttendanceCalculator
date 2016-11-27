package sk.oravcok.posta.dto;

import sk.oravcok.posta.enums.PlaceType;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * DTO for Place
 *
 * Created by Ondrej Oravcok on 20-Nov-16.
 */
public class PlaceDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private PlaceType placeType;

    private String annotation;

    //end of attributes

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
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof PlaceDTO)) return false;

        final PlaceDTO other = (PlaceDTO) obj;

        if (name != null ? !name.equals(other.getName()) : other.getName() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, placeType, annotation);
    }

    @Override
    public String toString(){
        return "PlaceDTO{"
                + "id=" + id
                + ", name=" + name
                + ", placeType=" + placeType
                + ", annotation=" + annotation
                +"}";
    }

}
