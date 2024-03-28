package io.github.simonkingws.webconfig.trace.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 节点信息
 *
 * @author: ws
 * @date: 2024/3/13 13:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphItemVO implements Serializable {

    private static final long serialVersionUID = -7731855769855970502L;

    /** 节点名称 */
    private String name;

    /** 节点x坐标*/
    private int x;

    /** 节点Y坐标 */
    private int y;
}
