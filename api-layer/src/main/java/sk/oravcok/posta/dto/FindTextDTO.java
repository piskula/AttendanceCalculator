package sk.oravcok.posta.dto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Ondrej Oravcok
 * @version 13-Jan-17.
 */
public class FindTextDTO {

    @NotNull
    private String text;

    //end of attributes

    //it was not possible to assign it in constructor directly

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof FindTextDTO)) return false;

        final FindTextDTO other = (FindTextDTO) obj;

        if (text != null ? !text.equals(other.getText()) : other.getText() != null) return false;

        return true;
    }

    @Override
    public int hashCode(){
        return Objects.hash(text);
    }

    @Override
    public String toString(){
        return "FindTextDTO{text=" + text +"}";
    }
}
