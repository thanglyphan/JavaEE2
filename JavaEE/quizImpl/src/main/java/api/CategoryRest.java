package api;

/**
 * Created by thang on 31.10.2016.
 */

import web.CategoryRestApi;
import businesslayer.QuizEJB;
import com.google.common.base.Throwables;
import datalayer.Category;
import dto.CategoryDto;
import dto.SubCategoryDto;
import dto.collection.ListDto;
import dto.hal.HalLink;
import io.swagger.annotations.ApiParam;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) //avoid creating new transactions
public class CategoryRest implements CategoryRestApi {
    private Category category;
    @Context
    UriInfo uriInfo;
    @Context private HttpServletResponse response;

    @EJB
    protected QuizEJB quizEJB;


    @Override
    public ListDto<CategoryDto> get(
            @ApiParam("Offset in the list of news") @DefaultValue("0") Integer offset,
            @ApiParam("Limit of news in a single retrieved page") @DefaultValue("10") Integer limit,
            @ApiParam("Whether to retrieve or not votes and comments for the given news") @DefaultValue("false") boolean expand) {

        if(offset < 0){
            throw new WebApplicationException("Negative offset: "+offset, 400);
        }

        if(limit < 1){
            throw new WebApplicationException("Limit should be at least 1: "+limit, 400);
        }
        int maxResults = 50;
        List<Category> categoryList;

        categoryList = quizEJB.getWholePackage(expand, maxResults);



        if(offset != 0 && offset >=  categoryList.size()){
            throw new WebApplicationException("Offset "+ offset + " out of bound "+categoryList.size(), 400);
        }

        ListDto<CategoryDto> dto = Converter.transformCollection(
                categoryList, offset, limit, expand);

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("/categories")
                .queryParam("limit", limit)
                .queryParam("expand", expand);

        dto._links.self = new HalLink(builder.clone()
                .queryParam("offset", offset)
                .build().toString()
        );

        if (!categoryList.isEmpty() && offset > 0) {
            dto._links.previous = new HalLink(builder.clone()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            );
        }
        if (offset + limit < categoryList.size()) {
            dto._links.next = new HalLink(builder.clone()
                    .queryParam("offset", offset + limit)
                    .build().toString()
            );
        }
        return dto;
    }


    @Override
    public Long createCategory(@ApiParam("Categoryname") CategoryDto dto) {
        Long id;
        try {
            this.category = quizEJB.createCategory(dto.rootCategory);
            id = category.getId();

            //Set the header response URI, in "Location".
            response.setHeader("Location",  uriInfo.getAbsolutePath().toString() + "/" + id.toString());
        } catch (Exception e) {
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public CategoryDto getById(@ApiParam(ID_PARAM) Long id, @ApiParam("Expand the response with info on sub and subsub such as ID and title") @DefaultValue("false") boolean expand) {
        Category category = quizEJB.get(id);
        if(category != null){
            return Converter.transform(quizEJB.get(id), expand);
        }else{
            throw new WebApplicationException("Not found, not root category: " + id, 404);
        }
    }

    @Override
    public void delete(@ApiParam(ID_PARAM) Long id) {
        quizEJB.deleteCategory(id);
    }

    @Override
    public Long createSubCategory(@ApiParam("Categoryname") SubCategoryDto dto) {
        Long id;
        try{
            Category found = quizEJB.get(Long.parseLong(dto.rootId));

            id = quizEJB.createCategorySub(found, dto.subCategory).getId();
            //Set the header response URI, in "Location".
            response.setHeader("Location",  uriInfo.getAbsolutePath().toString() + "/" + id.toString());
        }catch (Exception e){
            throw wrapException(e);
        }

        return id;
    }

    //TODO: TEST DENNE
    @Override
    public void patch(@ApiParam("The unique id of the counter") Long id, @ApiParam("Change root category") String text) {
        try {
            Category category = quizEJB.get(id);

            quizEJB.updatePatch(id, text);
            Converter.transform(category, false);

        } catch (Exception e) {
            throw new WebApplicationException("Invalid ID. Cannot find requested object:: > ID " + id, 404);
        }
    }



    //------------------------------------------------ DEPRECATED ------------------------------------------------//

    @Override
    public Response deprecatedGetSubCategoriesByParentId(@ApiParam(ID_PARAM) Long id) {
        return Response.status(Response.Status.MOVED_PERMANENTLY)
                .location(UriBuilder.fromUri("/subcategories/").queryParam("parentId", id).build())
                .build();
    }


    protected WebApplicationException wrapException(Exception e) throws WebApplicationException {
        Throwable cause = Throwables.getRootCause(e);
        if (cause instanceof ConstraintViolationException) {
            return new WebApplicationException("Invalid constraints on input: " + cause.getMessage(), 400);
        } else {
            return new WebApplicationException("Internal error", 500);
        }
    }

}
