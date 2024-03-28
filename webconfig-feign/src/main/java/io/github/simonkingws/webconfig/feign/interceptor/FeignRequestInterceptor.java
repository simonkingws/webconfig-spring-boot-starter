package io.github.simonkingws.webconfig.feign.interceptor;

import io.github.simonkingws.webconfig.common.constant.RequestHeaderConstant;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.core.WebconfigProperies;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import io.github.simonkingws.webconfig.common.util.SpringContextHolder;
import feign.MethodMetadata;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Fiegn 请求头请求拦截器
 *
 * @author: ws
 * @date: 2024/1/31 17:55
 */
@Slf4j
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Autowired
    private WebconfigProperies webconfigProperies;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (BooleanUtils.isTrue(webconfigProperies.getFeignInterceptor())) {
            RequestContextLocal requestContextLocal = RequestHolder.get();
            if (requestContextLocal != null) {
                // 获取调用的方法
                MethodMetadata methodMetadata = requestTemplate.methodMetadata();
                String className = methodMetadata.targetType().getName();
                String methodName = methodMetadata.method().getName();
                String currentPos = String.format(TraceConstant.INVOKE_METHOND_NAME, className, methodName);
                String applicationName = SpringContextHolder.getApplicationName();

                // 增加链路信息
                Map<String, Collection<String>> collectionMap = requestContextLocal.context2HeaderMap();
                collectionMap.put(RequestHeaderConstant.FIEGN_MARK_KEY, RequestHeaderConstant.FIEGN_MARK_VAL);
                collectionMap.put(RequestHeaderConstant.FIEGN_METHOD_NAME, Collections.singletonList(methodName));
                collectionMap.put(RequestHeaderConstant.FIEGN_CLASS_NAME, Collections.singletonList(className));
                collectionMap.put(RequestHeaderConstant.FIEGN_CONSUMER_APPLICATION_NAME, Collections.singletonList(applicationName));

                Integer traceSum = Optional.ofNullable(requestContextLocal.getTraceSum()).orElse(0);
                traceSum++;

                // 更新动态Header信息
                collectionMap.put(RequestHeaderConstant.TRACE_SUM, Collections.singletonList(String.valueOf(traceSum)));
                collectionMap.put(RequestHeaderConstant.END_POS, Collections.singletonList(currentPos));
                collectionMap.put(RequestHeaderConstant.SPAN_ID, Collections.singletonList(String.valueOf(Instant.now().toEpochMilli())));

                requestTemplate.headers(collectionMap);
            }
        }
    }
}
