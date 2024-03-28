package io.github.simonkingws.webconfig.core.wrapper;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 *
 * @author: ws
 * @date: 2024/1/29 17:19
 */
public class ParameterTrimWrapper extends AbstractRequestParamsWrapper {
    /**
     * Constructs a request object wrapping the given request.
     */
    public ParameterTrimWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    String cleanParams(String params) {
        return StringUtils.hasText(params) ? params.trim() : params;
    }
}
