package thirtytwo.degrees.elasticqs;

import com.google.common.collect.Iterables;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

import java.util.List;

/**
 * User: gibbsb
 * Date: 3/20/12
 * Time: 10:16 AM
 */
public abstract class QueueListener<T> implements Runnable {
    Queue<T> queue;
    int timeout;

    public QueueListener(Queue<T> queue) {
        this(queue, 0);
    }

    public QueueListener(Queue<T> queue, int timeout) {
        this.queue = queue;
        this.timeout = timeout;
    }

    public void run() {
        while (true) {
            T val = queue.redis.execute(new RedisCallback<T>() {
                @Override
                public T doInRedis(RedisConnection conn) throws DataAccessException {
                    List<byte[]> value = conn.bRPop(timeout, queue.name.getBytes());
                    if (value == null || Iterables.isEmpty(value))
                        return null;
                    return (T) queue.redis.getValueSerializer().deserialize(value.get(1));
                }});
            received(val);
        }
    }

    public abstract void received(T value);
}
