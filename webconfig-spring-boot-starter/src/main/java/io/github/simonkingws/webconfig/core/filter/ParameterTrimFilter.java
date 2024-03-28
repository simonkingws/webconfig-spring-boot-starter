package io.github.simonkingws.webconfig.core.filter;

import io.github.simonkingws.webconfig.core.wrapper.ParameterTrimWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ParameterTrimFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 请求参数过滤
        chain.doFilter(new ParameterTrimWrapper(req), resp);
    }
}
