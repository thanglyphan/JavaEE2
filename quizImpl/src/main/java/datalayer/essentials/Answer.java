package datalayer.essentials;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by thang on 25.10.2016.
 */
@Entity
public class Answer extends Solution implements Serializable{
    @NotBlank
    private String choiceOne;

    @NotBlank
    private String choiceTwo;

    @NotBlank
    private String choiceThree;

    @NotBlank
    private String choiceFour;
















    /*----------------------------------------------GETTER AND SETTER----------------------------------------------*/


    public String getChoiceOne() {
        return choiceOne;
    }

    public void setChoiceOne(String choiceOne) {
        this.choiceOne = choiceOne;
    }

    public String getChoiceTwo() {
        return choiceTwo;
    }

    public void setChoiceTwo(String choiceTwo) {
        this.choiceTwo = choiceTwo;
    }

    public String getChoiceThree() {
        return choiceThree;
    }

    public void setChoiceThree(String choiceThree) {
        this.choiceThree = choiceThree;
    }

    public String getChoiceFour() {
        return choiceFour;
    }

    public void setChoiceFour(String choiceFour) {
        this.choiceFour = choiceFour;
    }

}
