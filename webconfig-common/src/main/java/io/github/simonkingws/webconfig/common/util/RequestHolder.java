package io.github.simonkingws.webconfig.common.util;


import com.alibaba.fastjson2.JSON;
import io.github.simonkingws.webconfig.common.constant.MDCKey;
import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import io.github.simonkingws.webconfig.common.core.WebconfigProperies;
import io.github.simonkingws.webconfig.common.process.RequestContextLocalPostProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 全局请求参数管理器
 *
 * @author ws
 * @date 2024/1/31 16:07
 */
@Slf4j
public class RequestHolder {

    private static final StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
    private static final WebconfigProperies webconfigProperies = SpringContextHolder.getBean(WebconfigProperies.class);
    private static final RestTemplate restTemplate = SpringContextHolder.getBean(RestTemplate.class);

    private RequestHolder() {}

    private static final ThreadLocal<RequestContextLocal> THREAD_LOCAL = new ThreadLocal<>();

    public static void add(RequestContextLocal requestContextLocal) {
        THREAD_LOCAL.set(requestContextLocal);
    }

    public static RequestContextLocal get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
        MDC.clear();
        TraceContextHolder.remove();
    }

    public static void remove(RequestContextLocalPostProcess requestContextLocalPostProcess) {
        log.debug("当前请求的本地副本被清除.");
        RequestContextLocal local = THREAD_LOCAL.get();
        try {
            if (local != null) {
                local.setTraceEndMs(Instant.now().toEpochMilli());

                // 异步处理链路数据
                asyncHandleTrace(local, requestContextLocalPostProcess, TraceContextHolder.getTraceItems());
            }
        }finally {
            remove();
        }
    }

    /**
     * 异步处理李娜烤炉信息
     *
     * @author ws
     * @date 2024/2/26 18:59
     */
    private static void asyncHandleTrace(RequestContextLocal local, RequestContextLocalPostProcess requestContextLocalPostProcess, List<TraceItem> traceItems) {
        CompletableFuture.runAsync(() -> {
            // 线程池的中的线程变量是共享的，所以需要特殊处理，方式多线程队共享资源的变更
            String traceId = local.getTraceId();
            try {
                Boolean openTraceCollect = local.getOpenTraceCollect();
                if (BooleanUtils.isTrue(openTraceCollect)) {
                    if (StringUtils.isNotBlank(traceId)) {
                        // 重置MDC
                        MDC.put(MDCKey.TRACEID, traceId);

                        // 获取整条链路采集的链路数据
                        List<TraceItem> traceItemList = filterEnableTraceItemList(local, traceItems);

                        // 清除缓存的的链路数据
                        stringRedisTemplate.delete(traceId);

                        if (!CollectionUtils.isEmpty(traceItemList)) {
                            // 输出链路信息
                            String traceWalking = getCompeleteTraceWalking(traceItemList);
                            log.info("当前请求的完整的链路信息：{}", traceWalking);

                            local.setTraceSum(traceItemList.size());
                            local.setTraceWalking(traceWalking);
                            local.setEndPos(traceItemList.get(traceItemList.size() - 1).getMethodName());

                            // 推送数据
                            pushCollectData2Server(traceItemList);

                        }
                    }
                }

                // 调用销毁的方法
                if (requestContextLocalPostProcess != null) {
                    requestContextLocalPostProcess.destroy(local);
                }
            }catch (Exception e){
                log.info("处理采集的链路数据异常：", e);
            }
        });
    }

    private static List<TraceItem> filterEnableTraceItemList(RequestContextLocal local, List<TraceItem> traceItems) {
        List<TraceItem> traceItemList = new LinkedList<>(traceItems);
        if (!CollectionUtils.isEmpty(traceItemList)) {
            // 补充开始的方法的结束时间
            TraceItem traceBegin = traceItemList.get(0);
            traceBegin.setInvokeEndTime(local.getTraceEndMs());
            traceBegin.setSpanEndMs(local.getTraceEndMs());
        }

        // 处理缓存数据
        List<String> traces = stringRedisTemplate.opsForList().range(local.getTraceId(), 0, -1);
        if (!CollectionUtils.isEmpty(traces)) {
            List<TraceItem> collect = traces.stream().map(traceItem -> JSON.parseObject(traceItem, TraceItem.class)).collect(Collectors.toList());
            traceItemList.addAll(collect);
        }
        if (CollectionUtils.isEmpty(traceItemList)) {
            return Collections.emptyList();
        }

        // 分离异常链路，只保存最早的一条
        List<TraceItem> nomalList = traceItemList.stream().filter(item -> !item.getMethodName().equals(TraceConstant.EXCEPTION_METHOD_NAME)).collect(Collectors.toList());
        List<TraceItem> exceptionList = traceItemList.stream().filter(item -> item.getMethodName().equals(TraceConstant.EXCEPTION_METHOD_NAME)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(exceptionList)) {
            if (exceptionList.size() > 1) {
                exceptionList.sort(Comparator.comparing(TraceItem::getOrder));
            }
            TraceItem traceItem = exceptionList.get(0);
            traceItem.setOrder(nomalList.size());
            traceItem.setSpanEndMs(local.getTraceEndMs());

            nomalList.add(traceItem);
        }

        // 重排序
        nomalList.sort(Comparator.comparing(TraceItem::getOrder));

        // 补充用户信息
        nomalList.forEach(item -> {
            item.setRequestUrl(local.getRequestUrl());
            item.setUserId(local.getUserId());
            item.setUserName(local.getUserName());
        });

        return nomalList;
    }

    private static void pushCollectData2Server(List<TraceItem> traceItemList) {
        try {
            String traceCollectAddress = webconfigProperies.getTraceCollectAddress();
            if (StringUtils.isNotBlank(traceCollectAddress)) {
                String url = String.format(TraceConstant.COLLECT_PATH, webconfigProperies.getProtocol(), traceCollectAddress);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.parseMediaType(TraceConstant.JSON_CONTENT_TYPE));
                HttpEntity<List<TraceItem>> httpEntity = new HttpEntity<>(traceItemList, httpHeaders);

                ResponseEntity<Void> responseEntity = restTemplate.postForEntity(url, httpEntity, Void.class);
                if (responseEntity.getStatusCode().value() == 200) {
                    log.info(">>>>>>>>>当前请求的完链路信息推送成功！<<<<<<<<<<");
                }else{
                    log.info(">>>>>>>>>当前请求的完链路信息推送失败!<<<<<<<<<<");
                }
            }
        }catch (Exception e){
            log.warn("采集的链路服务器数据异常：", e);
        }
    }

    private static String getCompeleteTraceWalking(List<TraceItem> traceItemList) {
        StringBuilder sb = new StringBuilder();
        traceItemList.forEach(item -> {
            String methodName = item.getClassName() + SymbolConstant.DOT + item.getMethodName();
            String trace = String.format(TraceConstant.INVOKE_TRACE, item.getOrder(), item.getConsumerApplicatName(), item.getProviderApplicatName(), methodName);
            sb.append(trace).append(SymbolConstant.ARROW);
        });

        return sb.substring(0, sb.length() - SymbolConstant.ARROW.length());
    }
}
