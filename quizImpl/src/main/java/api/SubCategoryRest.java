package api;

/**
 * Created by thang on 31.10.2016.
 */

import web.SubCategoryRestApi;
import businesslayer.QuizEJB;
import com.google.common.base.Throwables;
import datalayer.CategorySub;

import dto.SubCategoryDto;
import io.swagger.annotations.ApiParam;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/*
    The actual implementation could be a EJB, eg if we want to handle
    transactions and dependency injections with @EJB.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) //avoid creating new transactions
public class SubCategoryRest implements SubCategoryRestApi {

    @Context
    UriInfo uriInfo;

    @EJB
    protected QuizEJB quizEJB;


    @Override
    public List<SubCategoryDto> get(
            @ApiParam("Filter sub only parentid") @DefaultValue("0") String parentid)
    {

        List<CategorySub> subList;
        int maxResults = 50;
        if(!parentid.equals("0")){
            subList = quizEJB.getCategoryListSubWithMaxByParentID(maxResults, parentid);
        }else{
            subList = quizEJB.getCategoryListSubWithMax(maxResults);
        }

        return Converter.transformSub(subList);
    }


    @Override
    public SubCategoryDto getById(@ApiParam(ID_PARAM) Long id) {
        return Converter.transform(quizEJB.getSub(id));
    }

}
