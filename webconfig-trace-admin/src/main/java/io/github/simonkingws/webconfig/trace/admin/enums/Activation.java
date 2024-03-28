package io.github.simonkingws.webconfig.trace.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法的活跃度
 *
 * @author: ws
 * @date: 2024/3/20 16:47
 */
@Getter
@AllArgsConstructor
public enum Activation {

    /** 活跃度高 */
    HIGH(1),

    /** 活跃度低 */
    LOW(2);

    private final Integer code;
}
