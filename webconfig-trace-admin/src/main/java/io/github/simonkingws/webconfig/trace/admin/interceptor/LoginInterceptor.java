package io.github.simonkingws.webconfig.trace.admin.interceptor;

import io.github.simonkingws.webconfig.trace.admin.constant.LoginConstant;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingUser;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 *
 * @author: ws
 * @date: 2024/3/7 12:58
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录信息保存在session中
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(LoginConstant.LOGIN_USER);
        if (attribute == null) {
            // 用户未登录
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // 用户登录，将用户信息写进本地线程
        TraceWalkingUser user = (TraceWalkingUser) attribute;
        RequestContextLocal local = RequestHolder.get();
        local.setUserId(user.getId().toString());
        local.setUserName(user.getUsername());

        // 防止密码泄露，特殊处理密码
        user.setPassword(LoginConstant.ENCRYPT_PWS);
        session.setAttribute(LoginConstant.LOGIN_USER, user);
        return true;
    }
}
