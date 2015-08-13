package br.com.axxiom.core.db;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class DatabaseChangeInterceptor extends HandlerInterceptorAdapter {

    private String paramName = "dbkey";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String param = request.getParameter(paramName);
        if (param != null && !param.isEmpty()) {
            RoutingDataSource.setDatasourceKey(param);
        } else {
            RoutingDataSource.setDatasourceKey(null);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        String param = request.getParameter(paramName);
        if (modelAndView != null) {
            if (param != null && !param.isEmpty()) {
                modelAndView.addObject("dbkey", request.getParameter(paramName));
            } else {
                modelAndView.addObject("dbkey", "");
            }
        }
    }
}
