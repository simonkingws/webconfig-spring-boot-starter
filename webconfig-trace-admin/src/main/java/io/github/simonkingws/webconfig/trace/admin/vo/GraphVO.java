package io.github.simonkingws.webconfig.trace.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 服务调用关系实体
 *
 * @author: ws
 * @date: 2024/3/13 13:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphVO implements Serializable {

    private static final long serialVersionUID = 1691338890286329093L;

    /** 节点 */
    private List<GraphItemVO> itemList;

    /** 关系*/
    private List<GraphRelVO> refList;
}
