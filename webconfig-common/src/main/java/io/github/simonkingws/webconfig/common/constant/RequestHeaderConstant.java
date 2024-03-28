package io.github.simonkingws.webconfig.common.constant;

import java.util.Collection;
import java.util.Collections;

/**
 * 请求头常量
 *
 * @author: ws
 * @date: 2024/1/31 15:24
 */
public interface RequestHeaderConstant {

    /**
     * 每次请求的唯一标识
     */
    String TRACE_ID = "trace-id";

    /**
     *  链路的条数
     */
    String TRACE_SUM = "trace-sum";

    /**
     *  链路起始
     */
    String START_POS = "start-pos";

    /**
     *  链路起始
     */
    String END_POS = "end-pos";


    /**
     *  链路轨迹起始时间
     */
    String TRACE_START_MS = "trace-start-ms";

    /**
     *  用户ID
     */
    String USER_ID = "user-id";

    /**
     *  用户名称
     */
    String USER_NAME = "user-name";

    /**
     *  用户类型
     */
    String USER_TYPE = "user-type";

    /**
     *  登录的令牌
     */
    String TOKEN = "token";

    /**
     *  登录的令牌
     */
    String OPEN_TRACE_COLLECT = "open-trace-collect";

    String[] HEADERS = {TRACE_ID, TRACE_SUM, START_POS, END_POS, USER_ID, USER_NAME, USER_TYPE, TOKEN, OPEN_TRACE_COLLECT};

    /** 以下参数不再ThreaLocal中传递 */
    /** Fiegn请求标识 */
    String FIEGN_MARK_KEY = "fiegn-request-mark";
    Collection<String> FIEGN_MARK_VAL = Collections.singletonList("fiegn-request");
    String FIEGN_METHOD_NAME = "fiegn-method-name";
    String FIEGN_CLASS_NAME = "fiegn-class-name";
    String FIEGN_CONSUMER_APPLICATION_NAME = "fiegn-consumer-application-name";

    /**
     * 子链路的唯一标识
     */
    String SPAN_ID = "span-id";

    /** 请求中唯一标识 */
    String ONCE_ACCESS_TOKEN = "once-access-token";
}
