package datalayer;

import datalayer.essentials.Question;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by thang on 25.10.2016.
 */
@Entity
@NamedQueries(value = {
        @NamedQuery(name = Quiz.FIND_ALL, query = "SELECT a FROM Quiz a")
})
public class Quiz implements Serializable{
    public static final String FIND_ALL = "Quiz.find_all";

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String categorySubId;

    @NotBlank
    private String quizName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> question;








    /*----------------------------------------------GETTER AND SETTER----------------------------------------------*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCategorySubId() {
        return categorySubId;
    }

    public void setCategorySubId(String categorySubId) {
        this.categorySubId = categorySubId;
    }




    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public List<Question> getQuestion() {
        return question;
    }

    public void setQuestion(List<Question> question) {
        this.question = question;
    }
}
