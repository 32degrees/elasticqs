package thirtytwo.degrees.elasticqs;

import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * User: gibbsb
 * Date: 3/20/12
 * Time: 10:22 AM
 */
public class Queue<T> {
    String name;
    RedisTemplate<String, T> redis;
    BoundListOperations<String, T> ops;

    public Queue(String name, RedisTemplate<String, T> redis) {
        this.name = name;
        this.redis = redis;
        this.ops = redis.boundListOps(name);
    }

    public Long push(T... values) {
        Long count = 0L;
        for (T value: values)
            count = ops.leftPush(value);

        return count;
    }
    
    public Long size() {
        return ops.size();
    }

}
