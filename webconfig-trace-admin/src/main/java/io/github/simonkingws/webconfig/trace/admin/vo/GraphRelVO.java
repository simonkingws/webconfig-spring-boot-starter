package io.github.simonkingws.webconfig.trace.admin.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点关系
 *
 * @author: ws
 * @date: 2024/3/13 13:24
 */
@Data
public class GraphRelVO implements Serializable {

    private static final long serialVersionUID = 4275541376533598952L;

    /** source */
    private String source;

    /** target */
    private String target;

    /** lineStyle */
    private JSONObject lineStyle;

    public GraphRelVO(String source, String target) {
        this.source = source;
        this.target = target;

        JSONObject json = new JSONObject();
        json.put("curveness", 0.2);
        this.lineStyle = json;
    }
}
