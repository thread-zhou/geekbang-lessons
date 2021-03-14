package org.geektimes.web.mvc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ModelAndView
 * @Description: 简易版 ModelAndView
 *
 * 为了能够方便的传递参数到前端，创建一个工具bean，相当于spring中简化版的ModelAndView
 * @author: zhoujian
 * @date: 2021/3/4 13:28
 * @version: 1.0
 */
public class ModelAndView {

    /**
     * 页面路径
     */
    private String view;

    /**
     * 页面data数据
     */
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView setView(String view) {
        this.view = view;
        return this;
    }
    public String getView() {
        return view;
    }
    public ModelAndView addObject(String attributeName, Object attributeValue) {
        model.put(attributeName, attributeValue);
        return this;
    }
    public ModelAndView addAllObjects(Map<String, ?> modelMap) {
        model.putAll(modelMap);
        return this;
    }
    public Map<String, Object> getModel() {
        return model;
    }
}
