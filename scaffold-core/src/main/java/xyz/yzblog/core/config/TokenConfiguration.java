package xyz.yzblog.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.yzblog.core.config.properties.TokenProperties;
import xyz.yzblog.core.service.JwtTokenService;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: Token配置
 */
@Configuration
@EnableConfigurationProperties(TokenProperties.class)
@ConditionalOnExpression("${jwt.token.enabled:false}")
public class TokenConfiguration {

    private TokenProperties properties;


    @Autowired
    public TokenConfiguration(TokenProperties properties){
        this.properties = properties;
    }

    @Bean
    public JwtTokenService tokenService(){
        JwtTokenService service = new JwtTokenService();
        service.setProperties(properties);
        return service;
    }
}
