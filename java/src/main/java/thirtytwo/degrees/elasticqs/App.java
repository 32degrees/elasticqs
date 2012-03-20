package thirtytwo.degrees.elasticqs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

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
        RedisTemplate<String, String> redis = ctx.getBean(RedisTemplate.class);

        String foo = redis.opsForValue().get("foo");
        System.out.printf("%s\n", foo);

        ListOperations<String,String> listOps = redis.opsForList();

        for (int i=0; i < 10; i++) {
            Long count = testq.push(i + " " + System.currentTimeMillis());
            System.out.printf("count after push #%s: %s\n", i, count);
        }

        QueueListener<String> listener = new QueueListener<String>(testq, 2) {
            @Override
            public void received(String value) {
                System.out.printf("popped: %s\n", value);
            }
        };

        new Thread(listener).start();
/*        while (true) {
            String val = redis.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection conn) throws DataAccessException {
                    List<byte[]> value = conn.bRPop(2, "test-q".getBytes());
                    if (value == null)
                        return null;
                    System.out.println(new String(value.get(0)));
                    return new String(value.get(1));
                }});
            System.out.printf("popped: %s\n", val);
            //val = listOps.rightPop("test-q");
        }*/
    }
}
