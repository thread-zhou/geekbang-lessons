# geekbang-lessons

极客时间课程工程

## JNDI

> JNDI：（Java Naming and Directory Interface）号称依赖查找的一个工具

- jndi是延迟初始化（并未马上进行初始化），而是在lookup时进行初始化，这里可以考虑缓存

## ClassLoader


### 疑问：

- 什么是Checked异常、什么是NoCheck异常

### 待证实

- SPI早于JNDI初始化

## 相关技术

- 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）

- static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）