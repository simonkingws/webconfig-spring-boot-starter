package io.github.simonkingws.webconfig.trace.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.simonkingws.webconfig.trace.admin.mapper.TraceWalkingUserMapper;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingUser;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 链路登录用户信息 服务实现类
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
@Service
public class TraceWalkingUserServiceImpl implements TraceWalkingUserService {

    @Autowired
    private TraceWalkingUserMapper traceWalkingUserMapper;

    @Override
    public TraceWalkingUser getUserByUsername(String username) {
        LambdaQueryWrapper<TraceWalkingUser> wrapper = Wrappers.lambdaQuery(TraceWalkingUser.class)
                .eq(TraceWalkingUser::getUsername, username);

        return traceWalkingUserMapper.selectOne(wrapper);
    }

    @Override
    public void insert(TraceWalkingUser user) {
        traceWalkingUserMapper.insert(user);
    }

    @Override
    public void disabledUser(TraceWalkingUser user) {
        user.setEnable(false);
        user.setUpdatedTime(new Date());
        traceWalkingUserMapper.updateByPrimaryKey(user);
    }
}
