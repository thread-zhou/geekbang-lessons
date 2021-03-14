package org.geektimes.manager.mbean.context;

import org.geektimes.web.core.ComponentContext;

import javax.management.*;
import java.util.*;

/**
 * @ClassName: ComponentContextMBean
 * @Description: ComponentContextMBean
 * @author: zhoujian
 * @date: 2021/3/14 17:13
 * @version: 1.0
 */
public class ComponentContextMBean implements DynamicMBean {

    private Map<String, Object> attributes = new HashMap<>();

    private MBeanInfo mBeanInfo;

    public ComponentContextMBean(ComponentContext componentContext){
        initComponentContextMBean(componentContext);
    }

    private void initComponentContextMBean(ComponentContext componentContext){
        List<String> componentNames  = componentContext.getComponentNames();
        MBeanAttributeInfo[] mBeanAttributeInfos = new MBeanAttributeInfo[componentNames.size()];
        AttributeList attributeList = new AttributeList(componentNames.size());
        int index = 0;
        for (String componentName : componentNames) {
            Object component = componentContext.getComponent(componentName);
            attributeList.add(new Attribute(componentName, component));
            mBeanAttributeInfos[index] = new MBeanAttributeInfo(componentName,
                    component.getClass().getPackage().getName(), componentName, true, false, false);
            index ++;
        }
        setAttributes(attributeList);
        mBeanInfo = new MBeanInfo(getClass().getName(), getClass().getName(), mBeanAttributeInfos, null, null, null);
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (!attributes.containsKey(attribute)) {
            throw new AttributeNotFoundException("...");
        }
        return attributes.get(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        attributes.put(attribute.getName(), attribute.getValue());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String attribute : attributes) {
            try {
                Object attributeValue = getAttribute(attribute);
                attributeList.add(new Attribute(attribute, attributeValue));
            } catch (AttributeNotFoundException | MBeanException | ReflectionException e) {
            }
        }
        return attributeList;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList attributeList = new AttributeList();
        for (Object attribute : attributes) {
            try {
                Attribute target = (Attribute) attribute;
                setAttribute(target);
                attributeList.add(target);
            }catch (AttributeNotFoundException | InvalidAttributeValueException | MBeanException | ReflectionException e) {
            }
        }
        return attributeList;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return mBeanInfo;
    }
}
