package org.geektimes;

import org.geektimes.projects.user.web.listener.DBConnectionInitializerListener;
import org.geektimes.web.FuYi;

/**
 * @ClassName: App
 * @Description: 项目入口
 * @author: zhoujian
 * @date: 2021/3/5 13:21
 * @version: 1.0
 */
public class App {
    public static void main(String[] args) {
        FuYi.run(App.class);
//        FuYi.getServletContext().addListener(DBConnectionInitializerListener.class);
    }
}
