package xyz.yzblog.core.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: redis缓存配置
 */
@EnableCaching
@Configuration
@ConditionalOnExpression("${redis.enabled:false}")
public class RedisCacheConfig extends CachingConfigurerSupport {


    public static final String TTL_KEY_5MIN = "5MIN";
    public static final String TTL_KEY_10MIN = "10MIN";
    public static final String TTL_KEY_2HOUR = "2HOUR";


    /**
     * Redis注解配置
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        // 默认配置，过期时间指定是30分钟
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        defaultCacheConfig.entryTtl(Duration.ofMinutes(30));

        //配置自定义注解 缓存时间及对应Key前缀
        String code = "cache";
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();

        //定义5分钟缓存配置
        redisCacheConfigurationMap.put(TTL_KEY_5MIN, redisCacheConfiguration.entryTtl(Duration.ofMinutes(10)).prefixKeysWith(code));
        //定义10分钟缓存配置
        redisCacheConfigurationMap.put(TTL_KEY_10MIN, redisCacheConfiguration.entryTtl(Duration.ofMinutes(10)).prefixKeysWith(code));
        //定义2小时缓存配置
        redisCacheConfigurationMap.put(TTL_KEY_2HOUR, redisCacheConfiguration.entryTtl(Duration.ofHours(2)).prefixKeysWith(code));

        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig, redisCacheConfigurationMap);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置序列化
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        // 设置对象序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // key序列化
        redisTemplate.setKeySerializer(stringSerializer);
        // value序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key序列化
        redisTemplate.setHashKeySerializer(stringSerializer);
        // Hash value序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    /**
     * 生成Key规则配置
     * @return
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName())
                    .append(".")
                    .append(method.getName())
                    .append("(")
                    .append(Arrays.stream(objects)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")))
                    .append(")");
            return sb.toString();
        };

    }

}
