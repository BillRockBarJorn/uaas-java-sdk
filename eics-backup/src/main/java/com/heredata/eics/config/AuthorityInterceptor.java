package com.heredata.eics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证授权拦截器
 * @author wuzz
 * @since 2024/6/28
 */
public class AuthorityInterceptor implements HandlerInterceptor {

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String accessKey = httpServletRequest.getHeader("accessKey");
        if (accessKey == null) accessKey = httpServletRequest.getParameter("accessKey");
        String secretKey = httpServletRequest.getHeader("secretKey");
        if (secretKey == null) secretKey = httpServletRequest.getParameter("secrectKey");
        if (this.accessKey.equals(accessKey) && this.secretKey.equals(secretKey)) {
            return true;
        }
        httpServletResponse.setStatus(403);
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
