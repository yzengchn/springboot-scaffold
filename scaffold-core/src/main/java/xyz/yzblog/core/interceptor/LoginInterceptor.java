package xyz.yzblog.core.interceptor;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.yzblog.core.annotation.IgnoreAuth;
import xyz.yzblog.core.exception.ServiceException;
import xyz.yzblog.core.service.JwtTokenService;
import xyz.yzblog.core.web.model.JwtUser;
import xyz.yzblog.core.web.response.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: 登录拦截器
 */
@AllArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private JwtTokenService tokenService;

    public static final String LOGIN_JWTUSER_KEY = "LOGIN_JWTUSER_KEY";
    public static final String LOGIN_TOKEN_KEY = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        IgnoreAuth annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreAuth.class);
        } else {
            return true;
        }

        //如果有@IgnoreAuth注解，则不验证token
        if (annotation != null) {
            return true;
        }

        //从header中获取token
        String token = request.getHeader(LOGIN_TOKEN_KEY);
        //如果header中不存在token，则从参数中获取token
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(LOGIN_TOKEN_KEY);
        }

        //token为空
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED, "请先登录");
        }

        //查询token信息
        JwtUser jwtUser = JwtUser.builder()
                .userId(tokenService.getUserId(token))
                .build();
        //设置到request中
        request.setAttribute(LOGIN_JWTUSER_KEY, jwtUser);
        return true;
    }

}
