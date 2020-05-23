package xyz.yzblog.core.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: JWt中用户信息
 */
@Data
@Builder
@ApiModel("JWt中用户信息")
public class JwtUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户Id",required = true,example = "10000001")
    private String userId;

    @ApiModelProperty(value = "用户名",required = true,example = "XXXX")
    private String username;


}
