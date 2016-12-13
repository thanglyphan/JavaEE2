package dto.hal;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by thang on 27.11.2016.
 */
public class HalObject {

    @ApiModelProperty("HAL links")
    public HalLinkSet _links;
}