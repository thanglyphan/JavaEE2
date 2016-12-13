package dto;

/**
 * Created by thang on 01.11.2016.
 */
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("A Sub Category")
public class SubCategoryDto {
    @ApiModelProperty(value = "The id of the sub category", hidden = true)
    public String id;
    @ApiModelProperty("The root category id")
    public String rootId;
    @ApiModelProperty(value = "The root category", hidden = true)
    public String rootCategory;
    @ApiModelProperty("The sub category")
    public String subCategory;


    public SubCategoryDto() {
    }

    public SubCategoryDto(String id, String rootId, String subCategory) { //, String subcategory, String subSubcategory, String question, String... answers) {
        this.id = id;
        this.rootId = rootId;
        this.subCategory = subCategory;
    }
}