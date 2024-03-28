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
 * 完整的链路信息
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
@Getter
@Setter
@TableName("trace_walking_compete")
public class TraceWalkingCompete implements Serializable {


    private static final long serialVersionUID = -5269501186261832068L;
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
     * 方法请求的入口Url
     */
    private String requestUrl;

    /**
     * 链路开始的时间
     */
    private Date traceStartTime;

    /**
     * 链路的结束时间
     */
    private Date traceEndTime;

    /**
     * 链路的耗时（单位：毫秒）
     */
    private Integer traceTimeConsume;

    /**
     * 链路所在服务的名称
     */
    private String applicationName;

    /**
     * 链路的起点
     */
    private String traceStartPos;

    /**
     * 链路的终点
     */
    private String traceEndPos;

    /**
     * 链路数
     */
    private Integer traceSum;

    /**
     * 调用的方法数（包含异常方法）
     */
    private Integer invokeMethodSum;

    /**
     * 是否有异常（0：否 1：是）
     */
    private Boolean exceptionFlag;

    /**
     * 异常信息
     */
    private String exceptionMsg;

    /**
     * 调用链路的用户ID
     */
    private String userId;

    /**
     * 调用链路的用户姓名
     */
    private String userName;

    /**
     * 创建时间
     */
    private Date createdTime;
}
