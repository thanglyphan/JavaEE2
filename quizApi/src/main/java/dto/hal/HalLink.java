package dto.hal;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by thang on 27.11.2016.
 */
public class HalLink {

    @ApiModelProperty("URL of the link")
    public String href;

    public HalLink(){}

    public HalLink(String href){
        this.href = href;
    }
}