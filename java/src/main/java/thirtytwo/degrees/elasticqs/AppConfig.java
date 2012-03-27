package thirtytwo.degrees.elasticqs;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.collections.DefaultRedisList;

import java.util.Collection;
import java.util.HashMap;

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

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        HashMap<MessageListener, Collection<? extends Topic>> listeners = new HashMap<MessageListener, Collection<? extends Topic>>();
        addSubscription(listeners, oddListener());
        addSubscription(listeners, evenListener());
        container.setMessageListeners(listeners);
        return container;
    }

    private void addSubscription(HashMap<MessageListener, Collection<? extends Topic>> listeners, MessageListenerAdapter key) {
        TopicSubscription subscription = (TopicSubscription) key.getDelegate();
        listeners.put(key, Lists.newArrayList(new ChannelTopic(subscription.getTopic())));
    }

    @Bean(name = "oddListener")
    public MessageListenerAdapter oddListener() {
        return new MessageListenerAdapter(new PrintTopicSubscription("odd"));
    }

    @Bean(name = "evenListener")
    public MessageListenerAdapter evenListener() {
        return new MessageListenerAdapter(new PrintTopicSubscription("even"));
    }
}
