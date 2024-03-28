package io.github.simonkingws.webconfig.trace.admin.controller;

import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingMethod;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingServer;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingMethodService;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingServerService;
import io.github.simonkingws.webconfig.trace.admin.vo.GraphItemVO;
import io.github.simonkingws.webconfig.trace.admin.vo.GraphRelVO;
import io.github.simonkingws.webconfig.trace.admin.vo.GraphVO;
import io.github.simonkingws.webconfig.trace.admin.vo.ServerInvokeVO;
import io.github.simonkingws.webconfig.common.core.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 分析采集的链路数据
 *
 * @author: ws
 * @date: 2024/3/6 15:20
 */
@Slf4j
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    @Autowired
    private TraceWalkingServerService traceWalkingServerService;
    @Autowired
    private TraceWalkingMethodService traceWalkingMethodService;

    /**
     * 服务拓扑图的数据
     *
     * @author ws
     * @date 2024/3/7 10:46
     */
    @GetMapping("/topologyMap")
    public JsonResult<?> topologyMap(){
        List<TraceWalkingServer> serverList = traceWalkingServerService.getAllServerList();
        GraphVO vo = handler2Gragh(serverList);
        return JsonResult.ofSuccess(vo);
    }

    /**
     * 服务列表被调用次数柱状图
     *
     * @author ws
     * @date 2024/3/7 10:52
     */
    @GetMapping("/serverHistogram")
    public JsonResult<?> serverHistogram(){
        List<ServerInvokeVO> invokeList = traceWalkingMethodService.statServerInvokeCount();
        return JsonResult.ofSuccess(invokeList);
    }

    /**
     * 查询链路明细
     *
     * @author ws
     * @date 2024/3/8 17:02
     */
    @GetMapping("/traceDetail")
    public JsonResult<?> traceDetail(String traceId){
        List<TraceWalkingMethod> methodList = traceWalkingMethodService.getMethodTraceList(traceId);
        return JsonResult.ofSuccess(methodList);
    }

    private GraphVO handler2Gragh(List<TraceWalkingServer> serverList) {
        if (CollectionUtils.isEmpty(serverList)) {
            return null;
        }

        List<String> distinctList = new ArrayList<>();
        serverList.forEach(item -> {
            if (!distinctList.contains(item.getCallerServerName())) {
                distinctList.add(item.getCallerServerName());
            }
            if (!distinctList.contains(item.getCalleeServerName())) {
                distinctList.add(item.getCalleeServerName());
            }
        });
        int size = distinctList.size();
        // 根据总数计算刻度
        double degree = 360.0 / size;

        // 排列节点的位置
        List<GraphItemVO> itemList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            long x = Math.round(300 + 300 * Math.cos(Math.toRadians(degree * i)));
            long y = Math.round(300 + 300 * Math.sin(Math.toRadians(degree * i)));
            itemList.add(new GraphItemVO(distinctList.get(i-1), (int)x, (int)y));
        }

        // 增加关联关系
        List<GraphRelVO> refList = new ArrayList<>();
        serverList.forEach(item -> {
            refList.add(new GraphRelVO(item.getCallerServerName(), item.getCalleeServerName()));
        });

        return new GraphVO(itemList, refList);
    }
}
