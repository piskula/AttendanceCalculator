package sk.oravcok.posta.dto;

/**
 * DTO for update Place
 *
 * Created by Ondrej Oravcok on 20-Nov-16.
 */
public class PlaceUpdateDTO extends PlaceCreateDTO {

    private Long id;

    //end of attributes

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
