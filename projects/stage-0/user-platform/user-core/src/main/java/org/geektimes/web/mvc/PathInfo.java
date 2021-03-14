package org.geektimes.web.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PathInfo
 * @Description: PathInfo 存储http相关信息
 *
 * 用于存放请求路径和请求方法类型
 * @author: zhoujian
 * @date: 2021/3/4 17:42
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathInfo {

    /**
     * http请求方法
     */
    private String httpMethod;

    /**
     * http请求路径
     */
    private String httpPath;
}
