package io.github.simonkingws.webconfig.trace.admin.controller;

import com.alibaba.fastjson2.JSON;
import io.github.simonkingws.webconfig.trace.admin.dto.MethodInvokeDTO;
import io.github.simonkingws.webconfig.trace.admin.dto.TraceWalkingDTO;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingCompete;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingCompeteService;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingMethodService;
import io.github.simonkingws.webconfig.trace.admin.vo.MethodStatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 分析采集的链路数据
 *
 * @author: ws
 * @date: 2024/3/6 15:20
 */
@Slf4j
@Controller
@RequestMapping("/analyze")
public class AnalyzePageController {

    @Autowired
    private TraceWalkingCompeteService traceWalkingCompeteService;
    @Autowired
    private TraceWalkingMethodService traceWalkingMethodService;


    /**
     * 根据条件获取Topx的数据
     *
     * @author ws
     * @date 2024/3/14 13:17
     */
    @GetMapping("/loadCompeteTopX")
    public String loadCompeteTopX(TraceWalkingDTO traceWalkingDto, Model model) {
        List<TraceWalkingCompete> competeList = traceWalkingCompeteService.getCompeteByCondition(traceWalkingDto);
        model.addAttribute("walkingCompeteList", competeList);
        return "index::walkingCompeteList";
    }

    /**
     * 根据条件获取调用方法的统计
     *
     * @author ws
     * @date 2024/3/14 13:17
     */
    @GetMapping("/loadMethodStat")
    public String loadMethodStat(MethodInvokeDTO methodInvokeDto, Model model) {
        List<MethodStatVO> methodStatList = traceWalkingMethodService.getMethodInvokeStat(methodInvokeDto);
        model.addAttribute("methodStatList", methodStatList);
        model.addAttribute("methodStatListJson", JSON.toJSONString(methodStatList));
        return "index::methdStatList";
    }
}
