package thirtytwo.degrees.elasticqs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.collections.DefaultRedisList;

@Configuration
public class AppConfig {
    @Bean
    public SayHello sayHello() {
        SayHello sayHello = new SayHello();
        sayHello.setName("32degrees");
        return sayHello;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setUsePool(true);
        return factory;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean(name = "test-q")
    public Queue<String> testQueue() {
        return new Queue<String>("test-q", redisTemplate());
    }

    @Bean(name = "test-q2")
    public DefaultRedisList testQueue2() {
        return new DefaultRedisList("test-q2", redisTemplate());
    }
}
