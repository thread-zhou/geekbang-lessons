package org.geektimes.rest.util;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static java.lang.String.valueOf;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.geektimes.rest.util.PathUtils.ENCODED_SLASH;
import static org.geektimes.rest.util.PathUtils.SLASH;

/**
 * @InterfaceName: URLUtils
 * @Description: URL Utilities
 * @author: zhoujian
 * @date: 2021/3/27 18:43
 * @version: 1.0
 */
public interface URLUtils {

    String DEFAULT_ENCODING = System.getProperty("org.geektimes.url.encoding", "UTF-8");

    String AND = "&";

    String EQUAL = "=";

    String TEMPLATE_VARIABLE_START = "{";

    String TEMPLATE_VARIABLE_END = "}";

    /**
     * 编码，默认使用 {@link #DEFAULT_ENCODING} UTF-8
     * @author zhoujian
     * @date 20:29 2021/3/29
     * @param content 待编码内容
     * @return java.lang.String
     **/
    static String encode(String content) {
        return encode(content, DEFAULT_ENCODING);
    }

    /**
     * 编码
     * @author zhoujian
     * @date 20:28 2021/3/29
     * @param content 待编码内容
     * @param encoding 编码方式
     * @return java.lang.String
     **/
    static String encode(String content, String encoding) {
        String encodedContent = null;
        try {
            encodedContent = URLEncoder.encode(content, encoding);
        } catch (UnsupportedEncodingException | NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
        return encodedContent;
    }

    /**
     * 根据 encodeSlashInPath 选择是否对 {@link PathUtils#SLASH}进行编码
     * @author zhoujian
     * @date 20:33 2021/3/29
     * @param templateValues 模板参数
     * @param encodeSlashInPath
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    static Map<String, Object> encodeSlash(Map<String, ?> templateValues, boolean encodeSlashInPath) {

        final Map<String, Object> encodedSlashTemplateValues;

        if (encodeSlashInPath) {
            encodedSlashTemplateValues = new HashMap<>();
            for (Map.Entry<String, ?> entry : templateValues.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    String str = (String) value;
                    value = StringUtils.replace(str, SLASH, ENCODED_SLASH);
                }
                encodedSlashTemplateValues.put(name, value);
            }
        } else {
            encodedSlashTemplateValues = (Map<String, Object>) templateValues;
        }

        return encodedSlashTemplateValues;
    }


    /**
     * 参数处理，变量替换 （支持多值参数）
     * @author zhoujian
     * @date 20:43 2021/3/29
     * @param params 参数列表（多值参数）
     * @param templateValues 模板参数列表
     * @param encoded 是否进行编码
     * @return javax.ws.rs.core.MultivaluedMap<java.lang.String,java.lang.String>
     **/
    static MultivaluedMap<String, String> resolveParams(MultivaluedMap<String, String> params,
                                                        Map<String, ?> templateValues, boolean encoded) {

        MultivaluedMap<String, String> resolvedParams = new MultivaluedHashMap<>();

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            String name = entry.getKey();
            String resolvedName = resolveVariables(name, templateValues, encoded);

            for (String element : entry.getValue()) {
                resolvedParams.add(resolvedName, resolveVariables(element, templateValues, encoded));
            }
        }

        return resolvedParams;
    }

    /**
     * 模板参数处理，传入的参数数组必须按照模板的顺序一一对应，返回模板参数与参数值的映射
     * @author zhoujian
     * @date 20:47 2021/3/29
     * @param template 待处理的模板
     * @param values 待替换的变量数组
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    static Map<String, Object> toTemplateVariables(String template, Object... values) {
        if (isBlank(template)) {
            return emptyMap();
        }

        int start = 0;
        int end = 0;

        int index = 0;

        final int length = values == null ? 0 : values.length;

        Map<String, Object> templateVariables = new LinkedHashMap<>();

        for (; ; ) {

            start = template.indexOf(TEMPLATE_VARIABLE_START, end);
            end = template.indexOf(TEMPLATE_VARIABLE_END, start);

            if (start == -1 || end == -1) {
                break;
            }

            String variableName = template.substring(start + 1, end);

            if (!templateVariables.containsKey(variableName)) {

                Object variableValue = index < length ? values[index++] : null;

                templateVariables.put(variableName, variableValue);
            }
        }

        return unmodifiableMap(templateVariables);
    }

    /**
     * 参数处理，传入数组，数组必须按照模板的顺序一一对应
     * @author zhoujian
     * @date 20:50 2021/3/29
     * @param template 待处理模板
     * @param templateValues 参数数组
     * @param encoded 是否进行编码
     * @return java.lang.String
     **/
    static String resolveVariables(String template, Object[] templateValues, boolean encoded) {
        return resolveVariables(template, toTemplateVariables(template, templateValues), encoded);
    }

    /**
     * 处理变量 变量替换
     * @author zhoujian
     * @date 20:36 2021/3/29
     * @param template 模板
     * @param templateValues 目标变量
     * @param encoded 是否进行编码
     * @return java.lang.String
     **/
    static String resolveVariables(String template, Map<String, ?> templateValues, boolean encoded) {
        if (isBlank(template)) {
            return null;
        }

        if (templateValues == null || templateValues.isEmpty()) {
            return template;
        }

        StringBuilder resolvedTemplate = new StringBuilder(template);

        int start = 0;
        int end = 0;

        for (; ; ) {

            start = resolvedTemplate.indexOf(TEMPLATE_VARIABLE_START, end);
            end = resolvedTemplate.indexOf(TEMPLATE_VARIABLE_END, start);

            if (start == -1 || end == -1) {
                break;
            }

            String variableName = resolvedTemplate.substring(start + 1, end);

            Object value = templateValues.get(variableName);

            if (value == null) { // variable not found, go to next
                continue;
            }

            String variableValue = valueOf(value);
            if (encoded) {
                variableValue = encode(variableValue);
            }

            resolvedTemplate.replace(start, end + 1, variableValue);
        }

        return resolvedTemplate.toString();
    }

    /**
     * QueryString 解析
     * @author zhoujian
     * @date 20:54 2021/3/29
     * @param queryString
     * @return java.util.Map<java.lang.String,java.util.List<java.lang.String>>
     **/
    static Map<String, List<String>> resolveParameters(String queryString) {
        if (isNotBlank(queryString)) {
            Map<String, List<String>> parametersMap = new LinkedHashMap();
            String[] queryParams = StringUtils.split(queryString, AND);
            if (queryParams != null) {
                for (String queryParam : queryParams) {
                    String[] paramNameAndValue = StringUtils.split(queryParam, EQUAL);
                    if (paramNameAndValue.length > 0) {
                        String paramName = paramNameAndValue[0];
                        String paramValue = paramNameAndValue.length > 1 ? paramNameAndValue[1] : StringUtils.EMPTY;
                        List<String> paramValueList = parametersMap.get(paramName);
                        if (paramValueList == null) {
                            paramValueList = new LinkedList<>();
                            parametersMap.put(paramName, paramValueList);
                        }
                        paramValueList.add(paramValue);
                    }
                }
            }
            return unmodifiableMap(parametersMap);
        }
        return emptyMap();
    }

    static String toQueryString(Map<String, List<String>> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return null;
        }

        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String paramName = entry.getKey();
            for (String paramValue : entry.getValue()) {
                queryStringBuilder.append(paramName).append(EQUAL).append(paramValue).append(AND);
            }
        }
        // remove last "&"
        return queryStringBuilder.substring(0, queryStringBuilder.length() - 1);
    }


}
