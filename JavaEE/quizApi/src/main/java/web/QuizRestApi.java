package web; /**
 * Created by thang on 31.10.2016.
 */

import dto.QuizDto;
import dto.collection.ListDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;

import javax.ws.rs.*;

@Api(value = "/quizzes" , description = "The quiz api, get and post information")
// when the url is "<base>/news", then this class will be used to handle it
@Path("/quizzes")
@Produces({
        Formats.BASE_JSON,
        Formats.V1_JSON
})
public interface QuizRestApi {
    String ID_PARAM ="The numeric id of the categories";

    @ApiOperation("Get all the available quizzes")
    @GET
    @Produces(Formats.HAL_V1)
    ListDto<QuizDto> get(
            @ApiParam("Offset in the list of news")
            @QueryParam("offset")
            @DefaultValue("0")
                    Integer offset,
            @ApiParam("Limit of news in a single retrieved page")
            @QueryParam("limit")
            @DefaultValue("10")
                    Integer limit,
            @ApiParam("Represent a subcategory id, to filter quizzes that belong to such subcategory")
            @QueryParam("filter")
            @DefaultValue("0")
                    String filter
    );

    @ApiOperation("Create a new quiz")
    @POST
    @Consumes({Formats.V1_JSON, Formats.BASE_JSON})
    @Produces(Formats.BASE_JSON)
    @ApiResponse(code = 201, message = "The id of newly created category")
    Long createQuiz(@ApiParam("Create a quiz") QuizDto dto);


    @ApiOperation("Get a single quiz specified by id")
    @GET
    @Path("/{id}")
    QuizDto getById(@ApiParam(ID_PARAM) @PathParam("id") Long id);

    @ApiOperation("Delete a quiz with the given id")
    @DELETE
    @Path("/{id}")
    void delete(@ApiParam(ID_PARAM) @PathParam("id") Long id);

    @ApiOperation("Modify the quiz")
    @Path("/random")
    @GET
    QuizDto random();

    @ApiOperation("Modify the quiz")
    @Path("/{id}")
    @PATCH
    @Consumes({Formats.V1_JSON, Formats.BASE_JSON})
    @Produces(Formats.BASE_JSON)
    void patch(@ApiParam("The unique id of the quiz")
               @PathParam("id")
                       Long id,
               //
               @ApiParam("Change quiz name")
                       String text);
/*
    @ApiOperation("Get a single quiz specified by id")
    @GET
    @Path("/{id}")
    QuizDto getById(@ApiParam(ID_PARAM) @PathParam("id") Long id);

    @ApiOperation("Delete a quiz with the given id")
    @DELETE
    @Path("/{id}")
    void delete(@ApiParam(ID_PARAM) @PathParam("id") Long id);


    @ApiOperation("Update an existing quiz")
    @PUT
    @Path("/{id}")
    @Consumes(web.Formats.V1_JSON)
    void update(
            @ApiParam(ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("Update the quiz with json")
                    QuizDto dto);

    @ApiOperation("Modify the quiz")
    @Path("/{id}")
    @PATCH
    @Consumes({web.Formats.V1_JSON, web.Formats.BASE_JSON})
    @Produces(web.Formats.BASE_JSON)
    void patch(@ApiParam("The unique id of the quiz")
               @PathParam("id")
                       Long id,
               //
               @ApiParam("Change quiz name")
                       String text);

    //------------------------------------------------ DECREPATED ------------------------------------------------//
    @ApiOperation("Deprecated. Use \"id\" instead")
    @GET
    @Path("/id/{id}")
    @Deprecated
    Response deprecatedGetById(@ApiParam(ID_PARAM) @PathParam("id") Long id);

    @ApiOperation("Deprecated. Use \"id\" instead")
    @DELETE
    @Path("/id/{id}")
    @Deprecated
    Response deprecatedDelete(@ApiParam(ID_PARAM) @PathParam("id") Long id);


    @ApiOperation("Deprecated. Use \"id\" instead")
    @PUT
    @Path("/id/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Deprecated
    Response deprecatedUpdate(
            @ApiParam(ID_PARAM)
            @PathParam("id")
                    Long id,
            //
            @ApiParam("The sub sub category that will replace the old one. Cannot change its id though.")
                    QuizDto dto);

    @ApiOperation("Deprecated. Use \"id\" instead")
    @Path("/id/{id}")
    @PATCH
    @Consumes({web.Formats.V1_JSON, web.Formats.BASE_JSON})
    @Produces(web.Formats.BASE_JSON)
    @Deprecated
    Response deprecatedPatch(@ApiParam("The unique id of the quiz")
                             @PathParam("id")
                                     Long id,
                             //
                             @ApiParam("Change quiz name")
                                     String text);
*/
}