package thirtytwo.degrees.elasticqs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        //ctx.scan(App.class.getPackage().toString());
        ctx.refresh();

        Queue<String> testq = ctx.getBean("test-q", Queue.class);
        BlockingQueue<String> testq2 = ctx.getBean("test-q2", BlockingQueue.class);
        RedisTemplate<String, String> redis = ctx.getBean(RedisTemplate.class);

        String foo = redis.opsForValue().get("foo");
        System.out.printf("%s\n", foo);

        //ListOperations<String,String> listOps = redis.opsForList();

        for (int i=0; i < 10; i++) {
            String value = i + " " + System.currentTimeMillis();
            Long count = testq.push(value);
            System.out.printf("testq count after push #%s: %s\n", i, count);
            testq2.add(value);
            System.out.printf("testq2 count after push #%s: %s\n", i, testq2.size());
        }

        QueueListener<String> listener = new QueueListener<String>(testq, 2) {
            @Override
            public void received(String value) {
                System.out.printf("testq popped: %s\n", value);
            }
        };

        new Thread(listener).start();
        new Thread(new Consumer(testq2)).start();
    }

    static class Consumer implements Runnable {
        private final BlockingQueue<String> queue;
        Consumer(BlockingQueue<String> q) { queue = q; }
        public void run() {
            try {
                while (true) { consume(queue.take()); }
            } catch (InterruptedException ex) { ex.printStackTrace(); }
        }
        void consume(String value) {
            System.out.printf("testq2 popped: %s\n", value);
        }
    }

}
