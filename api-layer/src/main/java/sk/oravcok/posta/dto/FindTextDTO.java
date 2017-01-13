package sk.oravcok.posta.dto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Ondrej Oravcok
 * @version 13-Jan-17.
 */
public class FindTextDTO {

    @NotNull
    private String name;

    //end of attributes

    //it was not possible to assign it in constructor directly

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof FindTextDTO)) return false;

        final FindTextDTO other = (FindTextDTO) obj;

        if (name != null ? !name.equals(other.getName()) : other.getName() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    @Override
    public String toString(){
        return "FindTextDTO{name=" + name +"}";
    }
}
