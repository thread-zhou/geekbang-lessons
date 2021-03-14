package org.geektimes.web.mvc.render;

import org.geektimes.web.FuYi;
import org.geektimes.web.mvc.bean.ModelAndView;
import org.geektimes.web.mvc.handler.RequestHandlerChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName: ViewRender
 * @Description: 将ModelAndView中的信息存到HttpServletRequest中并跳转到对应页面
 * @author: zhoujian
 * @date: 2021/3/5 20:58
 * @version: 1.0
 */
public class ViewRender implements Render{

    private ModelAndView mv;

    public ViewRender(Object mv) {
        if (mv instanceof ModelAndView) {
            this.mv = (ModelAndView) mv;
        } else if (mv instanceof String) {
            this.mv = new ModelAndView().setView((String) mv);
        } else {
            throw new RuntimeException("ModelAndView 返回类型不合法");
        }
    }

    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        HttpServletRequest req = handlerChain.getRequest();
        HttpServletResponse resp = handlerChain.getResponse();
        String path = mv.getView();
        Map<String, Object> model = mv.getModel();
        model.forEach(req::setAttribute);
        req.getRequestDispatcher(FuYi.getConfiguration().getViewPath() + path).forward(req, resp);
    }
}
