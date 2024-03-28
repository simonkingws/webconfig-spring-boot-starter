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
 * 服务之间的调用信息
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
@Getter
@Setter
@TableName("trace_walking_server")
public class TraceWalkingServer implements Serializable {


    private static final long serialVersionUID = 8724962460985165713L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 调用方服务的名称
     */
    private String callerServerName;

    /**
     * 被调用方服务的名称
     */
    private String calleeServerName;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
