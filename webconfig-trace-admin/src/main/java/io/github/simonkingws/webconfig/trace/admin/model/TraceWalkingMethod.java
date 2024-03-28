package io.github.simonkingws.webconfig.trace.admin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 链路方法信息
 * </p>
 *
 * @author ws
 * @since 2024-03-21
 */
@Getter
@Setter
@TableName("trace_walking_method")
public class TraceWalkingMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 链路的唯一标识
     */
    private String traceId;

    /**
     * 子链路的标识（进入子链路的时间戳）
     */
    private String spanId;

    /**
     * 子链路开始的时间
     */
    private Date spanStartTime;

    /**
     * 子链路的结束时间
     */
    private Date spanEndTime;

    /**
     * 子链路的耗时（单位：毫秒）
     */
    private Integer spanTimeConsume;

    /**
     * 方法调用的开始时间
     */
    private Date methodStartTime;

    /**
     * 方法调用的结束时间
     */
    private Date methodEndTime;

    /**
     * 方法调用的耗时（单位：毫秒）
     */
    private Integer methodTimeConsume;

    /**
     * 方法所在消费端服务的名称
     */
    private String consumerServerName;

    /**
     * 方法所在调用端服务的名称
     */
    private String providerServerName;

    /**
     * 方法请求的入口Url
     */
    private String requestUrl;

    /**
     * 方法所在的类
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 是否异常方法（0：否 1：是）
     */
    private Boolean exceptionFlag;

    /**
     * 异常信息
     */
    private String exceptionMsg;

    /**
     * 方法执行的顺序号
     */
    private Integer invokeOrder;

    /**
     * 创建时间
     */
    private Date createdTime;
}
