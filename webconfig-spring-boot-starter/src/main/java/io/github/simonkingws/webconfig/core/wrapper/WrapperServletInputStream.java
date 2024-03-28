package io.github.simonkingws.webconfig.core.wrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 参数处理包装的ServletInputStream
 *
 * @author: ws
 * @date: 2024/1/29 17:21
 */
public class WrapperServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream bais;

    public WrapperServletInputStream(ByteArrayInputStream bais) {
        this.bais = bais;
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {

    }

    @Override
    public int read() throws IOException {
        return bais.read();
    }
}
