package thirtytwo.degrees.elasticqs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

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

        final Queue<String> testq = ctx.getBean("test-q", Queue.class);
        final AtomicLong count = new AtomicLong(0);
        final Random random = new Random();

        Runnable producer = new Runnable() {
            public void run() {
                while (true) {
                    String[] values = new String[3];
                    for (int j=0; j < values.length; j++) {
                        long i = count.getAndIncrement();
                        values[j] = i + " " + System.currentTimeMillis();
                    }
                    Long size = testq.push(values);
                    int delay = random.nextInt(800);
                    System.out.printf("testq count after push #%s: %s, sleeping %s\n", count, size, delay);
                    sleep(delay);
                }
            }};

        new Thread(producer).start();

        for (int i=0; i < 4; i++)
            startListener(testq, i);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static QueueListener<String> startListener(final Queue<String> testq, final int id) {
        QueueListener<String> listener = new QueueListener<String>(testq, 0) {
            @Override
            public void received(String value) {
                System.out.printf("%s testq popped: %s\n", id, value);
                sleep(400);
            }
        };
        new Thread(listener).start();
        return listener;
    }

}
