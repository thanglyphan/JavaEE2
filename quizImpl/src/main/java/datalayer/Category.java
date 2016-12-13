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
        @NamedQuery(name = Category.FIND_ALL, query = "SELECT a FROM  Category a")
})

public class Category implements Serializable {
    public static final String FIND_ALL = "Category.find_absolute_all";


    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String categoryName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategorySub> categorySubs;

















    /*----------------------------------------------GETTER AND SETTER----------------------------------------------*/
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<CategorySub> getCategorySubs() {
        return categorySubs;
    }

    public void setCategorySubs(List<CategorySub> categorySubs) {
        this.categorySubs = categorySubs;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
