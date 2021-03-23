package org.geektimes.boot.wrapper;


/**
 * @ClassName: PrioritizedWrapper
 * @Description: 支持排序的包装类
 *
 * 默认权重为: 权重越高则优先级越高, 按照整数从大到小排序
 *
 * @author: zhoujian
 * @date: 2021/3/23 21:07
 * @version: 1.0
 */
public class PrioritizedWrapper<W> implements Comparable<PrioritizedWrapper<W>> {

    /**
     * 被包装的对象
     **/
    private final W wrapped;

    /**
     * 排序权重
     **/
    private final int priority;

    /**
     * 被包装对象的名称
     **/
    private final String name;

    public PrioritizedWrapper(W wrapped, int priority, String name){
        this.wrapped = wrapped;
        this.priority = priority;
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public W getWrapped() {
        return wrapped;
    }

    public String getName() {
        return name;
    }

    /**
     * 内置的比较器, 权重越高越优先
     * @author zhoujian
     * @date 21:13 2021/3/23
     * @param o
     * @return int
     **/
    @Override
    public int compareTo(PrioritizedWrapper<W> o) {
        return Integer.compare(o.getPriority(), this.getPriority());
    }
}
