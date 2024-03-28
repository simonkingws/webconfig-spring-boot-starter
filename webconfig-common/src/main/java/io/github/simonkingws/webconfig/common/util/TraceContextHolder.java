package io.github.simonkingws.webconfig.common.util;


import com.alibaba.fastjson2.JSON;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.TraceContextLocal;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 链路信息管理器
 *
 * @author ws
 * @date 2024/1/31 16:07
 */
public class TraceContextHolder {

    private TraceContextHolder() {}

    private static final ThreadLocal<TraceContextLocal> THREAD_LOCAL = new ThreadLocal<>();

    /***
     * 线程中增加上下文
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static void add(TraceContextLocal traceContextLocal) {
        THREAD_LOCAL.set(traceContextLocal);
    }

    /***
     * 上下文内容是否为空
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static boolean isNotEmpty() {
        return !isEmpty();
    }

    /***
     * 上下文内容是否为空
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static boolean isEmpty() {
        TraceContextLocal traceContextLocal = THREAD_LOCAL.get();
        return traceContextLocal == null || CollectionUtils.isEmpty(traceContextLocal.getTraceItems());
    }

    /***
     * 上下文内容是否有异常链路
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static boolean hasException() {
        if (isEmpty()) {
            return false;
        }
        long count = getTraceItems().stream().filter(item -> item.getMethodName().equals(TraceConstant.EXCEPTION_METHOD_NAME)).count();
        return count > 0;
    }

    /***
     * 转化上下文对象为String
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static List<String> toStringList() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        return getTraceItems().stream().map(JSON::toJSONString).collect(Collectors.toList());
    }

    /***
     * 获取上下文对象
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static TraceContextLocal get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 获取上下文对象中的内容
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static List<TraceItem> getTraceItems() {
        return Optional.ofNullable(get()).map(TraceContextLocal::getTraceItems).orElse(null);
    }

    /**
     * 添加上下文的内容
     *
     * @author ws
     * @date 2024/2/29 14:38
     */
    public static void addTraceItem(TraceItem traceItem) {
        TraceContextLocal traceContextLocal = get();
        if (traceContextLocal == null) {
            traceContextLocal = TraceContextLocal.ofInstance();

            // 添加到现成容器中
            add(traceContextLocal);
        }

        traceContextLocal.getTraceItems().add(traceItem);
    }

    /***
     * 删除获取上下文对象
     *
     * @author ws
     * @date 2024/2/29 11:19
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
