package datalayer.essentials;

import datalayer.Quiz;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * Created by thang on 25.10.2016.
 */

@Entity
public class Solution implements Serializable{
    @Id
    @GeneratedValue
    private Long solutionId;

    @NotBlank
    private String solutionToAnswer;














    /*----------------------------------------------GETTER AND SETTER----------------------------------------------*/

    public String getSolutionToAnswer() {
        return solutionToAnswer;
    }

    public void setSolutionToAnswer(String solutionToAnswer) {
        this.solutionToAnswer = solutionToAnswer;
    }
}
