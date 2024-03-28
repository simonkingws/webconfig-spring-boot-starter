package io.github.simonkingws.webconfig.common.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 本地线程容器的链路变量
 *
 * @author: ws
 * @date: 2024/2/29 10:05
 */
@Getter
@ToString
@NoArgsConstructor
public class TraceContextLocal implements Serializable {

    private static final long serialVersionUID = 1299935412865174645L;

    /** 现成中的链路信息 */
    private final List<TraceItem> traceItems = new LinkedList<>();


    public static TraceContextLocal ofInstance() {
        return new TraceContextLocal();
    }
}
