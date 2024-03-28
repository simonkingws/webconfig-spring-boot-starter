package io.github.simonkingws.webconfig.trace.admin.mapper;

import io.github.simonkingws.webconfig.trace.admin.dto.MethodInvokeDTO;
import io.github.simonkingws.webconfig.trace.admin.mapper.generate.GenerateMapper;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingMethod;
import io.github.simonkingws.webconfig.trace.admin.vo.MethodStatVO;
import io.github.simonkingws.webconfig.trace.admin.vo.ServerInvokeVO;

import java.util.List;

/**
 * <p>
 * 链路方法信息 Mapper 接口
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
public interface TraceWalkingMethodMapper extends GenerateMapper<TraceWalkingMethod> {

    List<ServerInvokeVO> statServerInvokeCount();

    List<MethodStatVO> getMethodInvokeStat(MethodInvokeDTO methodInvokeDto);
}
