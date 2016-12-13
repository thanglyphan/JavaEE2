package dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by thang on 04.11.2016.
 */

@ApiModel("A question")
public class QuestionDto {
    @ApiModelProperty(value = "The id of the category", hidden = true)
    public String id;
    @ApiModelProperty("The id of the question")
    public String quizId;
    @ApiModelProperty(value = "The question", notes = "POST/PUT only")
    public String question;
    @ApiModelProperty(value = "Choice one", notes = "POST/PUT only")
    public String choiceOne;
    @ApiModelProperty(value = "Choice two", notes = "POST/PUT only")
    public String choiceTwo;
    @ApiModelProperty(value = "Choice three", notes = "POST/PUT only")
    public String choiceThree;
    @ApiModelProperty(value = "Choice four", notes = "POST/PUT only")
    public String choiceFour;

//        QuizDto dto = new QuizDto(null, quizId, question, strings[0], strings[1], strings[2], strings[3]);

    public QuestionDto(){

    }
    public QuestionDto(String id, String quizId, String question, String choiceOne, String choiceTwo, String choiceThree, String choiceFour){
        this.id = id;
        this.quizId = quizId;
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = choiceThree;
        this.choiceFour = choiceFour;
    }
}
