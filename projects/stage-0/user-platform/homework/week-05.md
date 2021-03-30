# 第五周作业

## 内容

### 修复本程序 org.geektimes.reactive.streams 包下

### 继续完善 my-rest-client POST 方法

## 完成情况

- [x] 修复本程序 org.geektimes.reactive.streams 包下

1. 通过代码阅读，发现此处并无逻辑错误，而是对于背压情况发生时如何处理消息的方式，背压是由订阅方经特定业务场景下进行触发，这里可以存在两种情况

场景：订阅方一定已经收到了该则消息

- 在当前业务场景下，订阅方需在收到消息的情境下进行是否背压的判定，若需要进行背压，应该如何处理已接受到的消息(此处直接丢弃)

```java
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
```
- 触发背压时，也进行消息的消费，而后

```java
@Override
public void onNext(Object o) {
        // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
        if (++count > 2) {
        subscription.cancel();
        System.out.println("收到数据：" + o);
        return;
        }
        System.out.println("收到数据：" + o);
        }
```

--- 

- [ ] 继续完善 my-rest-client POST 方法

