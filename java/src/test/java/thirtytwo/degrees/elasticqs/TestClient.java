package thirtytwo.degrees.elasticqs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hello world!
 *
 */
public class TestClient
{
    static final Random random = new Random();
    public static void main( String[] args )
    {
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        //ctx.scan(TestClient.class.getPackage().toString());
        ctx.refresh();

        final Queue<String> testq = ctx.getBean("test-q", Queue.class);
        final AtomicLong count = new AtomicLong(0);

        Runnable producer = new Runnable() {
            public void run() {
                while (true) {
                    String[] values = new String[3];
                    for (int j=0; j < values.length; j++) {
                        long i = count.incrementAndGet();
                        sendMsg(i, testq);
                        values[j] = i + " " + System.currentTimeMillis();
                    }
                    Long size = testq.push(values);
                    int delay = random.nextInt(2000);
                    System.out.printf("testq count after push #%s: %s, sleeping %s\n", count, size, delay);


                    sleep(delay);
                }
            }};

        new Thread(producer).start();

        for (int i=0; i < 4; i++)
            startListener(testq, i);
    }

    private static void sendMsg(long count, Queue<String> testq) {
        if (count % 7 == 0) {
            testq.redis.convertAndSend("odd", count +" is divisible by 7");
        }
        if (count % 8 == 0) {
            testq.redis.convertAndSend("even", count +" is divisible by 8");
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static QueueSubscription<String> startListener(final Queue<String> testq, final int id) {
        QueueSubscription<String> listener = new QueueSubscription<String>(testq, 0) {
            @Override
            public void handle(String value) {
                System.out.printf("%s testq popped: %s\n", id, value);
                sleep(random.nextInt(1000));
            }
        };
        new Thread(listener).start();
        return listener;
    }

}
