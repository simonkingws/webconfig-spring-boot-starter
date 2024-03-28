package io.github.simonkingws.webconfig.core.wrapper;

import io.github.simonkingws.webconfig.core.util.XssUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Xss 攻击请求过滤
 *
 * @author: ws
 * @date: 2024/1/29 16:30
 */
public class XssWrapper extends AbstractRequestParamsWrapper {

    /**
     * Constructs a request object wrapping the given request.
     */
    public XssWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    String cleanParams(String unSafeValue) {
        return StringUtils.hasText(unSafeValue) ? XssUtil.clean(unSafeValue) : unSafeValue;
    }
}
