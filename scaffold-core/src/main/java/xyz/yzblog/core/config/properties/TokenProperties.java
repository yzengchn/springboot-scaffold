package xyz.yzblog.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: Token配置
 */
@Data
@ConfigurationProperties(prefix = "jwt.token")
public class TokenProperties {

    /**
     * JWT的接收对象
     */
    private String issuer;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 签发主体
     */
    private String audience;
    /**
     * 过期时间
     */
    private int expiresSecond;

}
