package io.github.simonkingws.webconfig.common.exception;

/**
 * webconfig自定义异常
 *
 * @author: ws
 * @date: 2024/1/30 16:38
 */
public class WebConfigException extends RuntimeException{
    private static final long serialVersionUID = -4332357251761282622L;

    public WebConfigException() {
    }

    public WebConfigException(String errorMsg) {
        super(errorMsg);
    }

    public WebConfigException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

    public WebConfigException(Throwable throwable) {
        super(throwable);
    }
}
