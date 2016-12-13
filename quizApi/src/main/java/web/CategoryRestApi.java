package web; /**
 * Created by thang on 31.10.2016.
 */

import dto.CategoryDto;
import dto.SubCategoryDto;
import dto.collection.ListDto;
import io.swagger.annotations.*;
import io.swagger.jaxrs.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/categories", description = "Handling of creating and retrieving categories")
@Path("/categories")
@Produces({
        Formats.BASE_JSON,
        Formats.V1_JSON
})
public interface CategoryRestApi {
    String ID_PARAM = "The numeric id of the categories";


    //Categories GET AND POST.

    //Method start
    @ApiOperation("Get all the categories")
    @GET
    @Produces(Formats.HAL_V1)
    ListDto<CategoryDto> get(

            @ApiParam("Offset in the list of news")
            @QueryParam("offset")
            @DefaultValue("0")
                    Integer offset,
            @ApiParam("Limit of news in a single retrieved page")
            @QueryParam("limit")
            @DefaultValue("10")
                    Integer limit,
            @ApiParam("Expand the response with info on sub and subsub such as ID and title")
            @QueryParam("expand")
            @DefaultValue("false")
                    boolean expand
    );

    //Method start
    @ApiOperation("Create a new category")
    @POST
    @Consumes({Formats.V1_JSON, Formats.BASE_JSON})
    @Produces(Formats.BASE_JSON)
    @ApiResponse(code = 201, message = "The id of newly created category")
    Long createCategory(@ApiParam("Categoryname") CategoryDto dto);

    //Categories GET AND POST END//

    //Method start
    @ApiOperation("Get a single category specified by id")
    @GET
    @Path("/{id}")
    CategoryDto getById(
            @ApiParam(ID_PARAM) @PathParam("id") Long id,
            @ApiParam("Expand the response with info on sub and subsub such as ID and title")
            @QueryParam("expand")
            @DefaultValue("false")
                    boolean expand
    );

    @ApiOperation("Delete category specified by id")
    @DELETE
    @Path("/{id}")
    void delete(@ApiParam(ID_PARAM) @PathParam("id") Long id);

    @ApiOperation("Create a new sub category")
    @POST
    @Path("/{id}/subcategories")
    @Consumes({Formats.V1_JSON, Formats.BASE_JSON})
    @Produces(Formats.BASE_JSON)
    @ApiResponse(code = 201, message = "The id of newly created sub category")
    Long createSubCategory(@ApiParam("SubCategoryName") SubCategoryDto dto);

    //Method start
    @ApiOperation("Modify the rootcategory")
    @PATCH
    @Path("/{id}")
    @Consumes({Formats.V1_JSON, Formats.BASE_JSON})
    @Produces(Formats.BASE_JSON)
    void patch(@ApiParam("The unique id of the counter") @PathParam("id") Long id,
               @ApiParam("Change root category") String text);



    //------------------------------------------------ DECREPATED ------------------------------------------------//

    //Method start
    @ApiOperation("Deprecated. Use \"{id}/subcategories\" instead.")
    @ApiResponses({
            @ApiResponse(code = 301, message = "Deprecated URI. Moved permanently.")
    })
    @GET
    @Path("/{id}/subcategories")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    Response deprecatedGetSubCategoriesByParentId(@ApiParam(ID_PARAM) @PathParam("id") Long id);
}