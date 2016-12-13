package api;

/**
 * Created by thang on 31.10.2016.
 */


import businesslayer.QuizEJB;
import com.google.common.base.Throwables;
import datalayer.Category;
import datalayer.CategorySub;
import datalayer.Quiz;
import datalayer.essentials.Question;
import dto.QuestionDto;
import io.swagger.annotations.ApiParam;
import web.QuestionAnswersRestApi;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
    The actual implementation could be a EJB, eg if we want to handle
    transactions and dependency injections with @EJB.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) //avoid creating new transactions
public class QuestionAnswersRest implements QuestionAnswersRestApi {


    @EJB
    protected QuizEJB quizEJB;

    @Override
    public Long createQuestion(@ApiParam("Create a question") QuestionDto dto) {
        Long id;
        try {
            Quiz found = quizEJB.getQuiz(Long.parseLong(dto.quizId));

            Question newQ = quizEJB.createQuestion(found, dto.question);
            id = newQ.getQuestionsId();
            quizEJB.createAnswerToQuestion(newQ, dto.choiceOne, dto.choiceTwo, dto.choiceThree, dto.choiceFour);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid parameters: ", 500);
        }
        return id;
    }

    @Override
    public Response getQuestion(Long id) {
        HashMap<String, List<String>> listQuestionsMap = new HashMap<>();
        List<String> answerList = new ArrayList<>();
        Question found;
        try{
            found = quizEJB.getQuestion(id);
        }catch (Exception e){
            throw new WebApplicationException("Invalid question ID: "+ id, 404);
        }
        answerList.add(found.getAnswer().getChoiceOne());
        answerList.add(found.getAnswer().getChoiceTwo());
        answerList.add(found.getAnswer().getChoiceThree());
        answerList.add(found.getAnswer().getChoiceFour());

        listQuestionsMap.put(found.getQuestion(), answerList);
        return Response.ok(listQuestionsMap).build();
    }

    @Override
    public void patch(@ApiParam("The unique id of the question") Long id, @ApiParam("Change question") String text) {
        try {
            Question question = quizEJB.getQuestion(id);

            String questionText;
            questionText = text;

            quizEJB.updatePatchQuestion(id, questionText);
            Converter.transform(question);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid instructions. Should contain just a number: \"" + text + "\"", 404);
        }

    }

    @Override
    public Response getAnswerById(@ApiParam("Quiz ID here") Long id, @ApiParam("Type in questionID") Long question) {
        String answer = "";

        Quiz found = quizEJB.getQuiz(id);
        Question foundQuestion = quizEJB.getQuestion(question);
        if (found == null || foundQuestion == null) {
            throw new WebApplicationException("Invalid parameters: ", 500);
        }else{
            answer = foundQuestion.getAnswer().getSolutionToAnswer();
        }

        if (answer.equals("")) {
            throw new WebApplicationException("Invalid question: ", 405);
        }

        return Response.ok(answer).build();
    }
}
