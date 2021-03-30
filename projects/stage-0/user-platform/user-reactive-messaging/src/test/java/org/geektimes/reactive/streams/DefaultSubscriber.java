package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @ClassName: DefaultSubscriber
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/29 21:55
 * @version: 1.0
 */
public class DefaultSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;

    private int count = 0;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
    }

    @Override
    public void onNext(Object o) {
        // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
        if (++count > 2) {
            subscription.cancel();
            System.out.printf("收到数据：%s, 已丢弃\n", o);
            return;
        }
        System.out.println("收到数据：" + o);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("遇到异常：" + t);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据完成");
    }
}
