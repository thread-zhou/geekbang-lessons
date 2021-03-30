package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @ClassName: DefaultSubscription
 * @Description: Subscription 与 Subscriber 是一一对应
 * @author: zhoujian
 * @date: 2021/3/29 21:55
 * @version: 1.0
 */
public class DefaultSubscription implements Subscription {

    private boolean canceled = false;

    private final Subscriber subscriber;

    public DefaultSubscription(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {

    }

    @Override
    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }
}
