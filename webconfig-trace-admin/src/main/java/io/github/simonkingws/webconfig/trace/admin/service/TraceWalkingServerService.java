package io.github.simonkingws.webconfig.trace.admin.service;

import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingServer;
import io.github.simonkingws.webconfig.common.context.TraceItem;

import java.util.List;

/**
 * <p>
 * 服务之间的调用信息 服务类
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
public interface TraceWalkingServerService {

    void processTraceWalkingServer(List<TraceItem> traceItems);

    List<TraceWalkingServer> getAllServerList();
}
