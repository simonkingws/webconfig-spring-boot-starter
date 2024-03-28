package io.github.simonkingws.webconfig.trace.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 链路查询的实体
 *
 * @author: ws
 * @date: 2024/3/7 11:07
 */
@Data
public class TraceWalkingDTO implements Serializable {

    private static final long serialVersionUID = -1211078479163768649L;

    /** 链路ID */
    private String traceId;

    /** 是否有异常 */
    private Boolean exceptionFlag;

    /** 调用时间：开始时间 */
    private Date invokeTimeStart;

    /** 调用时间：结束时间 */
    private Date invokeTimeEnd;

    /** 查询的条数 */
    private Integer topSum;

    /** 用户的ID */
    private String userId;

    public static TraceWalkingDTO empty() {
        return new TraceWalkingDTO();
    }

}
