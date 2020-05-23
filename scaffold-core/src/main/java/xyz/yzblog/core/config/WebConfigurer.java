package xyz.yzblog.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.yzblog.core.interceptor.LoginInterceptor;
import xyz.yzblog.core.service.JwtTokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: web配置
 */
@Slf4j
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String env;//当前激活的配置文件

    @Value("${jwt.token.enabled}")
    private boolean jwt;//是否开启jwt

    @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * 解决跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS", "HEAD")
                .maxAge(3600 * 24);
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //非生产环境统计接口耗时
        if(!"prod".equals(env)){
            registry.addInterceptor(new HandlerInterceptor() {
                private ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    startTimeThreadLocal.set(System.currentTimeMillis());
                    log.info("┌开始[{}] {}", request.getRequestURI(), request.getMethod());
                    return true;
                }
                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                }
                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                    log.info("└结束[{}] 耗时:{}(ms)", request.getRequestURI(), System.currentTimeMillis() - startTimeThreadLocal.get());
                }
            });
        }

        //登录拦截器
        if(jwt){
            registry.addInterceptor(new LoginInterceptor(jwtTokenService))
                    .addPathPatterns("/**")
                    .excludePathPatterns("/error/**")
                    // 静态资源
                    .excludePathPatterns("/js/**", "/css/**", "/images/**", "/lib/**","/fonts/**")
                    // swagger-ui
                    .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
        }
    }


}
