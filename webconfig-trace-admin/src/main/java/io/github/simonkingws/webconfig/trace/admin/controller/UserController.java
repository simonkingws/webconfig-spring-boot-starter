package io.github.simonkingws.webconfig.trace.admin.controller;

import io.github.simonkingws.webconfig.trace.admin.constant.LoginConstant;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingUser;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingUserService;
import io.github.simonkingws.webconfig.common.core.JsonResult;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息控制层
 *
 * @author: ws
 * @date: 2024/3/7 14:57
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TraceWalkingUserService traceWalkingUserService;

    /**
     * 注销用户
     *
     * @author ws
     * @date 2024/3/7 14:53
     */
    @PostMapping("/disabledUser")
    public JsonResult<?> disabledUser(HttpServletRequest request) {
        String userName = RequestHolder.get().getUserName();
        TraceWalkingUser user = traceWalkingUserService.getUserByUsername(userName);
        Assert.notNull(user, "该用户信息已不存在！");

        traceWalkingUserService.disabledUser(user);
        log.info("用户：{} 已注销", userName);
        request.getSession().removeAttribute(LoginConstant.LOGIN_USER);
        return JsonResult.ofSuccess();
    }
}
