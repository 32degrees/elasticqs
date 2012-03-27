package thirtytwo.degrees.elasticqs;

/**
 * User: gibbsb
 * Date: 3/20/12
 * Time: 4:13 PM
 */
public class PrintTopicSubscription extends TopicSubscription {

    public PrintTopicSubscription(String topic) {
        super(topic);
    }

    public void onMessage(String message) {
        System.out.printf("%s sent %s\n", topic, message);
    }
}
