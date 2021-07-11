package org.geektimes.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: SimplePublisher
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/29 21:53
 * @version: 1.0
 */
public class SimplePublisher<T> implements Publisher<T> {

    private List<Subscriber> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> s) {
        SubscriptionAdapter subscription = new SubscriptionAdapter(s);
        s.onSubscribe(subscription);
        subscribers.add(subscription.getSubscriber());
    }

    public void publish(T data) {
        subscribers.forEach(subscriber -> {
            subscriber.onNext(data);
        });
    }

    public static void main(String[] args) {
        SimplePublisher publisher = new SimplePublisher();

        publisher.subscribe(new BusinessSubscriber(4));

        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }
}