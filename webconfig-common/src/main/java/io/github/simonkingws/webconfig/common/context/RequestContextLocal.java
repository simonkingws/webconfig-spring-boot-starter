package io.github.simonkingws.webconfig.common.context;

import io.github.simonkingws.webconfig.common.constant.RequestHeaderConstant;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 * 请求的上下文对象
 *
 * @author: ws
 * @date: 2024/1/31 15:06
 */
@Data
@Builder
public class RequestContextLocal implements Serializable {

    private static final long serialVersionUID = 4038049315156683942L;

    /**
     *  用户ID
     */
    private String userId;

    /**
     *  用户名称
     */
    private String userName;

    /**
     *  用户类型
     */
    private String userType;

    /**
     *  登录的令牌
     */
    private String token;


    /**
     *  每次请求链路的唯一标识
     */
    private String traceId;

    /**
     *  子链路的标识，也是子链路的开始时间：时间戳
     */
    private Long spanId;

    /**
     *  链路的条数
     */
    private Integer traceSum;

    /**
     *  链路起始
     */
    private String startPos;

    /**
     *  链路起始
     */
    private String endPos;

    /**
     *  链路轨迹
     */
    private String traceWalking;

    /**
     *  总链路起始时间
     */
    private Long traceStartMs;

    /**
     *  总链路结束
     */
    private Long traceEndMs;


    /**
     *  扩展信息
     */
    private Map<String, String> extendContext;

    /** 是否采集链路数据 */
    private Boolean openTraceCollect;


    /** 远程调用的方法名： 次参数传输不传递 */
    private String rpcMethodName;

    /** 请求的入口Url： 次参数传输不传递 */
    private String requestUrl;


    /**
     * 从请求头中构建上线下文参数
     *
     * @author ws
     * @date 2024/1/31 15:19
     */
    public static RequestContextLocal buildContext(HttpServletRequest request){
        String traceId = request.getHeader(RequestHeaderConstant.TRACE_ID);
        String traceSum = request.getHeader(RequestHeaderConstant.TRACE_SUM);
        String traceStartMs = request.getHeader(RequestHeaderConstant.TRACE_START_MS);

        RequestContextLocalBuilder builder = RequestContextLocal.builder()
                .traceId(StringUtils.isNotBlank(traceId) ? traceId : UUID.randomUUID().toString())
                .spanId(Instant.now().toEpochMilli())
                .traceSum(StringUtils.isNotBlank(traceSum) ? Integer.parseInt(traceSum) : 0)
                .startPos(request.getHeader(RequestHeaderConstant.START_POS))
                .traceStartMs(StringUtils.isNotBlank(traceStartMs) ? Long.parseLong(traceStartMs) : Instant.now().toEpochMilli())
                .userId(request.getHeader(RequestHeaderConstant.USER_ID))
                .userName(request.getHeader(RequestHeaderConstant.USER_NAME))
                .userType(request.getHeader(RequestHeaderConstant.USER_TYPE))
                .token(request.getHeader(RequestHeaderConstant.TOKEN))
                .openTraceCollect(Boolean.parseBoolean(request.getHeader(RequestHeaderConstant.OPEN_TRACE_COLLECT)));

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Map<String, String> otherHeaders = new LinkedHashMap<>();
            while (headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                if (!ArrayUtils.contains(RequestHeaderConstant.HEADERS, headerName)) {
                    otherHeaders.put(headerName, request.getHeader(headerName));
                }
            }

            builder.extendContext(otherHeaders);
        }

        return builder.build();
    }

    public Map<String, Collection<String>> context2HeaderMap(){
        Map<String, Collection<String>> headers = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(this.traceId)) {
            headers.put(RequestHeaderConstant.TRACE_ID, Collections.singletonList(this.traceId));
        }
        if (this.traceSum != null) {
            headers.put(RequestHeaderConstant.TRACE_SUM, Collections.singletonList(this.traceSum.toString()));
        }
        if (StringUtils.isNotBlank(this.startPos)) {
            headers.put(RequestHeaderConstant.START_POS, Collections.singletonList(this.startPos));
        }
        if (StringUtils.isNotBlank(this.endPos)) {
            headers.put(RequestHeaderConstant.END_POS, Collections.singletonList(this.endPos));
        }
        if (this.traceStartMs != null) {
            headers.put(RequestHeaderConstant.TRACE_START_MS, Collections.singletonList(this.traceStartMs.toString()));
        }
        if (StringUtils.isNotBlank(this.userId)){
            headers.put(RequestHeaderConstant.USER_ID, Collections.singletonList(this.userId));
        }
        if (StringUtils.isNotBlank(this.userName)) {
            headers.put(RequestHeaderConstant.USER_NAME, Collections.singletonList(this.userName));
        }
        if (StringUtils.isNotBlank(this.userType)) {
            headers.put(RequestHeaderConstant.USER_TYPE, Collections.singletonList(this.userType));
        }
        if (StringUtils.isNotBlank(this.token)) {
            headers.put(RequestHeaderConstant.TOKEN, Collections.singletonList(this.token));
        }
        headers.put(RequestHeaderConstant.OPEN_TRACE_COLLECT, Collections.singletonList(this.openTraceCollect + ""));
        if (!CollectionUtils.isEmpty(this.extendContext)) {
            extendContext.forEach((k, v) -> headers.put(k, Collections.singletonList(v)));
        }

        return headers;
    }
}
