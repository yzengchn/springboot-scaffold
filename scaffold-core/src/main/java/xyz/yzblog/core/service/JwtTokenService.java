package xyz.yzblog.core.service;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import xyz.yzblog.core.config.properties.TokenProperties;
import xyz.yzblog.core.exception.ServiceException;
import xyz.yzblog.core.web.response.ResultCode;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: JwtTokenService
 */
@Slf4j
public class JwtTokenService {

    private final static String USERID_KEY = "userId";
    private final static String USERNAME_KEY = "username";

    private TokenProperties properties;

    public void setProperties(TokenProperties properties) {
        this.properties = properties;
    }

    /**
     * 解析jwt
     * @param jsonWebToken
     * @return
     */
    public Claims parseJWT(String jsonWebToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(properties.getSecret()))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (ExpiredJwtException  eje) {
            log.error("===== Token过期 =====", eje);
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "Token过期");
        } catch (Exception e){
            log.error("===== token解析异常 =====", e);
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "token解析异常");
        }
    }

    /**
     * 构建jwt
     * @param userId
     * @param username
     * @return
     */
    public String createJWT(String userId, String username) {
        try {
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(properties.getSecret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    .claim(USERNAME_KEY, username)
                    .setSubject(userId)
                    .setIssuer(properties.getIssuer())
                    .setIssuedAt(new Date())
                    .setAudience(properties.getAudience())
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            int TTLMillis = properties.getExpiresSecond();
            if (TTLMillis >= 0) {
                Date exp = DateUtil.offsetSecond(now, TTLMillis);
                builder.setExpiration(exp)
                        .setNotBefore(now);
            }
            //生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "签名失败");
        }
    }

    /**
     * 从token中获取用户ID
     * @param token
     * @return
     */
    public String getUserId(String token){
        return parseJWT(token).getSubject();
    }

    /**
     * 是否已过期
     * @param token
     * @return
     */
    public boolean isExpiration(String token) {
        return parseJWT(token).getExpiration().before(new Date());
    }
}
