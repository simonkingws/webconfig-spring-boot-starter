package io.github.simonkingws.webconfig.trace.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 调用方法的统计
 *
 * @author: ws
 * @date: 2024/3/20 16:35
 */
@Data
public class MethodStatVO implements Serializable {

    private static final long serialVersionUID = 7346937268503873713L;

    /** 方法全限定类名 */
    private String className;
    /** 方法名称 */
    private String methodName;
    /** 方法调用的次数 */
    private Long invokeCount;

    /** 方法调用的次数 */
    private Date invokeTime;

}
