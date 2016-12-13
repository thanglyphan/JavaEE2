package datalayer;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by thang on 01.11.2016.
 */
@Entity
@NamedQueries(value = {
        @NamedQuery(name = CategorySub.FIND_ALL, query = "SELECT a FROM  CategorySub a")
})
public class CategorySub implements Serializable{
    public static final String FIND_ALL = "CategorySub.find_absolute_all";

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Category category;

    @NotBlank
    private String categorySubName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizList;


















    /*----------------------------------------------GETTER AND SETTER----------------------------------------------*/
    public String getCategorySubName() {
        return categorySubName;
    }

    public void setCategorySubName(String categorySubName) {
        this.categorySubName = categorySubName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Quiz> getQuizList() {
        return quizList;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
    }
}
