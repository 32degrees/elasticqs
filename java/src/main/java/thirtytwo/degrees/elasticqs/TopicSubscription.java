package thirtytwo.degrees.elasticqs;

/**
 * User: gibbsb
 * Date: 3/20/12
 * Time: 4:43 PM
 */
public abstract class TopicSubscription {
    protected String topic;

    public TopicSubscription(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void handleMessage(String message) {
        onMessage(message);
    }

    protected abstract void onMessage(String message);
}
