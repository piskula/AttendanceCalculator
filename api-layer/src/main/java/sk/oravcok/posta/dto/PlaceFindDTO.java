package sk.oravcok.posta.dto;

import java.util.Objects;

/**
 * DTO for find Place
 *
 * Created by Ondrej Oravcok on 25-Dec-16.
 */
public class PlaceFindDTO {

    private String name;

    //end of attributes

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof PlaceFindDTO)) return false;

        final PlaceDTO other = (PlaceDTO) obj;

        if (name != null ? !name.equals(other.getName()) : other.getName() != null) return false;

        return true;
    }
}
