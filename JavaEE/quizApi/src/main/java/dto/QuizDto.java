package dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;


@ApiModel("A Quiz")
public class QuizDto {
    @ApiModelProperty(value = "The id of the QUIZ", hidden = true)
    public String id;
    @ApiModelProperty("The sub sub category")
    public String subCategoryId;
    @ApiModelProperty("The quiz name")
    public String quizName;
    @ApiModelProperty(value = "Q and A", hidden = true)
    public Map<String, List<String>> questionsAndAnswersList;


    public QuizDto(){}

    public QuizDto(String id, String subCategoryId, String quizName) { //, String subcategory, String subSubcategory, String question, String... answers) {
        this.id = id;
        this.subCategoryId = subCategoryId;
        this.quizName = quizName;
    }

}
