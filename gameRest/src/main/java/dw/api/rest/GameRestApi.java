package dw.api.rest;

/**
 * Created by thang on 31.10.2016.
 */

import dto.QuestionDto;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Api(value = "/games" , description = "The game api, get and post information")
@Path("/games")
@Produces(MediaType.APPLICATION_JSON) // states that, when a method returns something, it is in Json
public interface GameRestApi {
    //Method start
    @ApiOperation("Get a random game")
    @GET
    @Path("/random")
    @ApiResponse(code = 200, message = "Got random game")
    QuestionDto getRandomGame() throws Exception;

    @ApiOperation("Get a random game")
    @POST
    Response answerQuestion(
            @ApiParam("Quiz ID here") @QueryParam("qid") String quizId,
            @ApiParam("Question ID here")@QueryParam("questionid") String questionId,
            @ApiParam("Your answer, choose a number")@QueryParam("index") String index) throws Exception;

}