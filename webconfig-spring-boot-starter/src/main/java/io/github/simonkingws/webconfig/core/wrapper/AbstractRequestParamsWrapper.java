package io.github.simonkingws.webconfig.core.wrapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求参数统一处理包装类（xxs、参数去除空格使用）
 *
 * @author: ws
 * @date: 2024/1/29 17:26
 */
public abstract class AbstractRequestParamsWrapper extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     */
    public AbstractRequestParamsWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 对数组参数进行特殊字符过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanParams(values[i]);
        }
        return encodedValues;
    }

    /**
     * 对参数中特殊字符进行过滤
     */
    @Override
    public String getParameter(String name) {
        return cleanParams(super.getParameter(name));
    }

    /**
     * 对参数集中特殊字符进行过滤
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            for (int i = 0; i < values.length; i++) {
                values[i] = cleanParams(values[i]);
            }
            map.put(key, values);
        }
        return map;
    }

    /**
     * 获取attribute,特殊字符过滤
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String) {
            return cleanParams((String) value);
        }
        return value;
    }

    /**
     * 对请求头部进行特殊字符过滤
     */
    @Override
    public String getHeader(String name) {
        return cleanParams(super.getHeader(name));
    }

    /**
     * 对于json参数的过滤
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        String contentType = super.getHeader(HttpHeaders.CONTENT_TYPE);
        // 文本数据需要处理
        if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)
                || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType)
                || MediaType.TEXT_HTML_VALUE.equalsIgnoreCase(contentType)
                || MediaType.TEXT_PLAIN_VALUE.equalsIgnoreCase(contentType) ) {

            String jsonStr = StreamUtils.copyToString(super.getInputStream(), StandardCharsets.UTF_8);
            // 流内容为空直接返回
            if (!StringUtils.hasText(jsonStr)) {
                return super.getInputStream();
            }

            // 内容过滤
            jsonStr = cleanParams(jsonStr);

            // 重新封装留流信息
            ByteArrayInputStream bis = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8));
            return new WrapperServletInputStream(bis);
        }

        // 非文本数据直接返回流
        return super.getInputStream();
    }

    abstract String cleanParams(String params);
}
