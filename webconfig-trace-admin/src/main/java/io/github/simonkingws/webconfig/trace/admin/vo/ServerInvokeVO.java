package io.github.simonkingws.webconfig.trace.admin.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务被调用次数实体
 *
 * @author: ws
 * @date: 2024/3/7 10:54
 */
@Data
public class ServerInvokeVO implements Serializable {

    private static final long serialVersionUID = -5835782703228616787L;

    /** 被调用服务的名称 */
    private String serverName;

    /** 被调用服务的次数 */
    private Long invokeCount;
}
