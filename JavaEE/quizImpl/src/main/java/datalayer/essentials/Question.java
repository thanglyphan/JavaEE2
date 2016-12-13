package datalayer.essentials;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by thang on 25.10.2016.
 */
@Entity
public class Question implements Serializable{
    @Id
    @GeneratedValue
    private Long questionsId;

    @NotBlank
    private String question;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Answer answer;







    /*----------------------------------------------GETTER AND SETTER----------------------------------------------*/


    public Long getQuestionsId() {
        return questionsId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

}
