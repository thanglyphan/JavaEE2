package web;

/**
 * Created by thang on 12.10.2016.
 */

import dto.QuestionDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/qa" , description = "The quiz api, get and post information")
// when the url is "<base>/news", then this class will be used to handle it
@Path("/qa")
@Produces(MediaType.APPLICATION_JSON) // states that, when a method returns something, it is in Json
public interface QuestionAnswersRestApi {
    String ID_PARAM ="The numeric id of the categories";

    @ApiOperation("Create a new question!")
    @POST
    @Consumes(Formats.V1_JSON)
    @ApiResponse(code = 200, message = "The id of newly created question")
    Long createQuestion(@ApiParam("Create a question") QuestionDto dto);
/*
    @ApiOperation("Get the answer to a question")
    @GET
    @Path("/{id}/{answerToQuestion}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    Response getAnswer(
            @ApiParam("Quiz ID here") @PathParam("id") Long id,
            @ApiParam("Type in your question(needs to exist in the quiz)") @PathParam("answerToQuestion") String question);
*/
    @ApiOperation("Get question by id")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/question/{id}")
    Response getQuestion(@ApiParam("Question ID here") @PathParam("id") Long id);

    @ApiOperation("Modify the question")
    @Path("/{id}")
    @PATCH
    @Consumes({Formats.V1_JSON, Formats.BASE_JSON})
    @Produces(Formats.BASE_JSON)
    void patch(@ApiParam("The unique id of the question")
               @PathParam("id")
                       Long id,
               //
               @ApiParam("Change question")
                       String text);

    @ApiOperation("Get the answer to a question")
    @GET
    @Path("/{id}/{answerToQuestionv2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    Response getAnswerById(
            @ApiParam("Quiz ID here") @PathParam("id") Long id,
            @ApiParam("Type in questionID") @PathParam("answerToQuestionv2") Long question);
}