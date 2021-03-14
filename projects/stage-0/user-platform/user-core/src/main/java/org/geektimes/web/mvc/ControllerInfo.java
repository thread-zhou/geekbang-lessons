package org.geektimes.web.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @ClassName: ControllerInfo
 * @Description: ControllerInfo 存储Controller相关信息
 *
 * Controller分发器类似于Bean容器，只不过后者是存放Bean的而前者是存放Controller的，
 * 然后根据一些条件可以简单的获取对应的Controller。
 * @author: zhoujian
 * @date: 2021/3/4 17:39
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerInfo {

    /**
     * controller类
     */
    private Class<?> controllerClass;

    /**
     * 执行的方法
     */
    private Method invokeMethod;

    /**
     * 方法参数别名对应参数类型
     */
    private Map<String, Class<?>> methodParameter;
}
