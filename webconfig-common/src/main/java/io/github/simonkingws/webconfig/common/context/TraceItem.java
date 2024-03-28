package io.github.simonkingws.webconfig.common.context;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * 子链路信息
 *
 * @author: ws
 * @date: 2024/2/26 15:54
 */
@Data
@Builder
public class TraceItem implements Serializable {

    private static final long serialVersionUID = 8345886464536102086L;

    /**
     *  每次请求链路的唯一标识
     */
    private String traceId;

    /**
     *  子链路的标识，也是子链路的开始时间：时间戳
     */
    private Long spanId;

    /**
     *  当前方法的开始调用时间
     */
    private Long invokeStartTime;

    /**
     *  当前方法的结束调用时间
     */
    private Long invokeEndTime;

    /**
     *  子链路结束时间
     */
    private Long spanEndMs;

    /**
     *  调用方法消费端的服务名称
     */
    private String consumerApplicatName;

    /**
     *  调用方法提供端的服务名称
     */
    private String providerApplicatName;

    /**
     *  调用的方法的入口Url
     */
    private String requestUrl;

    /**
     *  调用的方法
     */
    private String methodName;

    /**
     *  调用的方法的类型
     */
    private String className;

    /**
     *  调用的方法的类型
     */
    private String execptionMsg;

    /**
     *  调用的顺序
     */
    private Integer order;

    /**
     *  当前用户ID
     */
    private String userId;

    /**
     *  当前用户姓名
     */
    private String userName;

    @Tolerate
    public TraceItem() {

    }

    public static TraceItem copy2TraceItem(RequestContextLocal local){
        return TraceItem.builder()
                .traceId(local.getTraceId())
                .spanId(local.getSpanId())
                .order(local.getTraceSum())
                .build();
    }
}
