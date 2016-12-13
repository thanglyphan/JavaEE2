package dto.hal;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by thang on 27.11.2016.
 */
public class HalLinkSet {

    @ApiModelProperty("Link to the current resource")
    public HalLink self;
}