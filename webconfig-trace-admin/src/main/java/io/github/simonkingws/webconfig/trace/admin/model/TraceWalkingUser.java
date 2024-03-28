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
 * 链路登录用户信息
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
@Getter
@Setter
@TableName("trace_walking_user")
public class TraceWalkingUser implements Serializable {


    private static final long serialVersionUID = 5291751956934499671L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否有效（0：注销 1：有效）
     */
    private Boolean enable;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
