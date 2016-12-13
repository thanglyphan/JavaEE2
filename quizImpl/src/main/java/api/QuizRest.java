package api;

/**
 * Created by thang on 31.10.2016.
 */

import businesslayer.QuizEJB;
import com.google.common.base.Throwables;
import datalayer.CategorySub;
import datalayer.Quiz;
import dto.QuizDto;
import dto.collection.ListDto;
import dto.hal.HalLink;
import io.swagger.annotations.ApiParam;
import web.QuizRestApi;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;


@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) //avoid creating new transactions
public class QuizRest implements QuizRestApi {

    @Context
    UriInfo uriInfo;
    @Context private HttpServletResponse response;

    private CategorySub categorySub;

    @EJB
    protected QuizEJB quizEJB;

    @Override
    public ListDto<QuizDto> get(
            @ApiParam("Offset in the list of news") @DefaultValue("0") Integer offset,
            @ApiParam("Limit of news in a single retrieved page") @DefaultValue("10") Integer limit,
            @ApiParam("Represent a subcategory id, to filter quizzes that belong to such subcategory") @DefaultValue("0") String filter)
    {
        if(offset < 0){
            throw new WebApplicationException("Negative offset: "+offset, 400);
        }

        if(limit < 1){
            throw new WebApplicationException("Limit should be at least 1: "+limit, 400);
        }

        List<Quiz> quizList;
        int maxResults = 50;
        if(!filter.equals("0")){
            quizList = quizEJB.getQuizListWithMaxLimitAndFilterBySubID(filter);
        }else{
            quizList = quizEJB.getQuizListWithMaxLimit(maxResults);
        }


        if(offset != 0 && offset >=  quizList.size()){
            throw new WebApplicationException("Offset "+ offset + " out of bound "+quizList.size(), 400);
        }

        ListDto<QuizDto> dto = Converter.transformCollectionQuiz(
                quizList, offset, limit);

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("/quizzes")
                .queryParam("limit", limit);

        dto._links.self = new HalLink(builder.clone()
                .queryParam("offset", offset)
                .build().toString()
        );

        if (!quizList.isEmpty() && offset > 0) {
            dto._links.previous = new HalLink(builder.clone()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            );
        }
        if (offset + limit < quizList.size()) {
            dto._links.next = new HalLink(builder.clone()
                    .queryParam("offset", offset + limit)
                    .build().toString()
            );
        }
        return dto;
    }



    @Override
    public Long createQuiz(@ApiParam("Create a quiz") QuizDto dto) {
        Long id;
        try{
            this.categorySub = quizEJB.getSub(Long.parseLong(dto.subCategoryId));

            id = quizEJB.createQuiz(categorySub.getId().toString(), dto.quizName).getId();
            //Set the header response URI, in "Location".
            response.setHeader("Location",  uriInfo.getAbsolutePath().toString() + "/" + id.toString());

        }catch (Exception e){
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public QuizDto getById(@ApiParam(ID_PARAM) Long id) {
        return Converter.transform(quizEJB.getQuiz(id));
    }

    @Override
    public void delete(@ApiParam(ID_PARAM) Long id) {
        quizEJB.deleteQuiz(id);
    }

    @Override
    public QuizDto random() {
        int limit = 1000;
        List<Quiz> list = quizEJB.getQuizListWithMaxLimit(limit);
        //Shuffle list to get quiz at random.
        Collections.shuffle(list);
        return Converter.transform(quizEJB.getQuiz(list.get(0).getId()));

    }

    @Override
    public void patch(@ApiParam("The unique id of the quiz") Long id, @ApiParam("Change quiz name") String text) {
        try {
            Quiz quiz = quizEJB.getQuiz(id);

            quizEJB.updatePatchQuiz(id, text);
            Converter.transform(quiz);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Invalid instructions. Should contain just a number: \"" + text + "\"");
        }

    }

    //----------------------------------------------------------

    protected WebApplicationException wrapException(Exception e) throws WebApplicationException{

        /*
            Errors:
            4xx: the user has done something wrong, eg asking for something that does not exist (404)
            5xx: internal server error (eg, could be a bug in the code)
         */

        Throwable cause = Throwables.getRootCause(e);
        if(cause instanceof ConstraintViolationException){
            return new WebApplicationException("Invalid constraints on input: "+cause.getMessage(), 400);
        } else {
            return new WebApplicationException("Internal error", 500);
        }
    }
}
