package io.github.simonkingws.webconfig.trace.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.simonkingws.webconfig.trace.admin.mapper.TraceWalkingServerMapper;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingServer;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingServerService;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务之间的调用信息 服务实现类
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
@Slf4j
@Service
public class TraceWalkingServerServiceImpl implements TraceWalkingServerService {

    @Autowired
    private TraceWalkingServerMapper traceWalkingServerMapper;

    @Override
    public void processTraceWalkingServer(List<TraceItem> traceItems) {
        Set<String> set = new HashSet<>();
        traceItems.forEach(traceItem -> {
            set.add(traceItem.getConsumerApplicatName() +"," + traceItem.getProviderApplicatName());
        });

        // 更新服务调用关系
        for (String cp : set) {
            String[] split = cp.split(",");
            if (!split[0].equalsIgnoreCase(split[1])) {
                LambdaQueryWrapper<TraceWalkingServer> wrapper = Wrappers.lambdaQuery(TraceWalkingServer.class)
                        .eq(TraceWalkingServer::getCallerServerName, split[0])
                        .eq(TraceWalkingServer::getCalleeServerName, split[1]);
                TraceWalkingServer one = traceWalkingServerMapper.selectOne(wrapper);
                if (one == null) {
                    one = new TraceWalkingServer();
                    one.setCallerServerName(split[0]);
                    one.setCalleeServerName(split[1]);
                    one.setCreatedTime(new Date());
                    one.setUpdatedTime(new Date());

                    try {
                        traceWalkingServerMapper.insert(one);
                    }catch (Exception e){
                        log.warn("traceWalkingServer 插入数据库数据可能存在重复的唯一键：", e);
                    }
                }else {
                    one.setUpdatedTime(new Date());

                    traceWalkingServerMapper.updateById(one);
                }
            }
        }
        log.info("......[TraceWalkingServer]数据采集成功......");
    }

    @Override
    public List<TraceWalkingServer> getAllServerList() {
        return traceWalkingServerMapper.selectList(Wrappers.lambdaQuery(TraceWalkingServer.class));
    }
}
