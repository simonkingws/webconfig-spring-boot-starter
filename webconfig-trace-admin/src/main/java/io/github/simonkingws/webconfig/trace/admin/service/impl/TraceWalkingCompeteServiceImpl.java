package io.github.simonkingws.webconfig.trace.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.simonkingws.webconfig.trace.admin.constant.SqlFieldConstant;
import io.github.simonkingws.webconfig.trace.admin.dto.TraceWalkingDTO;
import io.github.simonkingws.webconfig.trace.admin.mapper.TraceWalkingCompeteMapper;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingCompete;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingCompeteService;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 完整的链路信息 服务实现类
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
@Slf4j
@Service
public class TraceWalkingCompeteServiceImpl implements TraceWalkingCompeteService {

    @Autowired
    private TraceWalkingCompeteMapper traceWalkingCompeteMapper;

    @Override
    public void processTraceWalkingCompelete(List<TraceItem> traceItems) {
        TraceWalkingCompete compete = new TraceWalkingCompete();

        // 解析链路的入口数据
        TraceItem traceItem = traceItems.get(0);
        compete.setTraceId(traceItem.getTraceId());
        compete.setTraceStartTime(new Date(traceItem.getSpanId()));
        compete.setApplicationName(traceItem.getConsumerApplicatName());
        compete.setTraceStartPos(traceItem.getMethodName());
        compete.setRequestUrl(traceItem.getRequestUrl());

        // 解析链路的出口数据
        int size = traceItems.size();
        TraceItem lastTraceItem = traceItems.get(size -1);
        compete.setExceptionFlag(StringUtils.equals(lastTraceItem.getMethodName(), TraceConstant.EXCEPTION_METHOD_NAME));
        compete.setTraceEndTime(new Date(lastTraceItem.getSpanEndMs()));

        String endPos = lastTraceItem.getMethodName();
        if (compete.getExceptionFlag()) {
            String execptionMsg = lastTraceItem.getExecptionMsg();
            if (StringUtils.isNotEmpty(execptionMsg) && execptionMsg.length() > SqlFieldConstant.EXCEPTION_MSG_LENGTH) {
                execptionMsg = execptionMsg.substring(0, SqlFieldConstant.EXCEPTION_MSG_LENGTH);
            }
            compete.setExceptionMsg(execptionMsg);
            if (size > 1) {
                // 如果有异常取前一个节点
                TraceItem ti = traceItems.get(size - 2);
                endPos = ti.getMethodName();
            }
        }
        compete.setTraceEndPos(endPos);

        // 解析汇总数据
        compete.setTraceSum((int)traceItems.stream().map(TraceItem::getSpanId).distinct().count());
        compete.setInvokeMethodSum(size);
        compete.setTraceTimeConsume((int)(lastTraceItem.getSpanEndMs() - traceItem.getSpanId()));
        compete.setUserId(lastTraceItem.getUserId());
        compete.setUserName(lastTraceItem.getUserName());
        compete.setCreatedTime(new Date());

        // 数据入库
        traceWalkingCompeteMapper.insert(compete);
        log.info("......[TraceWalkingCompete]数据采集成功......");

    }

    @Override
    public List<TraceWalkingCompete> getCompeteByCondition(TraceWalkingDTO traceWalkingDto) {
        Date invokeTimeEnd = traceWalkingDto.getInvokeTimeEnd();
        if (invokeTimeEnd != null) {
            LocalDateTime localDateTime = invokeTimeEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MAX);
            traceWalkingDto.setInvokeTimeEnd(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        }

        LambdaQueryWrapper<TraceWalkingCompete> queryWrapper = Wrappers.lambdaQuery(TraceWalkingCompete.class)
                .eq(StringUtils.isNoneBlank(traceWalkingDto.getTraceId()), TraceWalkingCompete::getTraceId, traceWalkingDto.getTraceId())
                .eq(traceWalkingDto.getExceptionFlag() != null, TraceWalkingCompete::getExceptionFlag, traceWalkingDto.getExceptionFlag())
                .ge(traceWalkingDto.getInvokeTimeStart() != null, TraceWalkingCompete::getTraceStartTime, traceWalkingDto.getInvokeTimeStart())
                .le(traceWalkingDto.getInvokeTimeEnd() != null, TraceWalkingCompete::getTraceEndTime, traceWalkingDto.getInvokeTimeEnd())
                .eq(StringUtils.isNoneBlank(traceWalkingDto.getUserId()), TraceWalkingCompete::getUserId, traceWalkingDto.getUserId())
                .orderByDesc(TraceWalkingCompete::getTraceStartTime)
                .last("LIMIT " + Optional.ofNullable(traceWalkingDto.getTopSum()).orElse(10));
        return traceWalkingCompeteMapper.selectList(queryWrapper);
    }
}
