package io.github.simonkingws.webconfig.trace.admin.service;

import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingUser;

/**
 * <p>
 * 链路登录用户信息 服务类
 * </p>
 *
 * @author ws
 * @since 2024-03-05
 */
public interface TraceWalkingUserService {

    TraceWalkingUser getUserByUsername(String username);

    void insert(TraceWalkingUser user);

    void disabledUser(TraceWalkingUser user);
}
