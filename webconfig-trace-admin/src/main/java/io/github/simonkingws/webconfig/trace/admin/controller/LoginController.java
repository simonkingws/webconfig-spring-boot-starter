package io.github.simonkingws.webconfig.trace.admin.controller;

import io.github.simonkingws.webconfig.trace.admin.constant.LoginConstant;
import io.github.simonkingws.webconfig.trace.admin.dto.MethodInvokeDTO;
import io.github.simonkingws.webconfig.trace.admin.dto.TraceWalkingDTO;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingCompete;
import io.github.simonkingws.webconfig.trace.admin.model.TraceWalkingUser;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingCompeteService;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingMethodService;
import io.github.simonkingws.webconfig.trace.admin.service.TraceWalkingUserService;
import io.github.simonkingws.webconfig.trace.admin.vo.MethodStatVO;
import io.github.simonkingws.webconfig.common.core.JsonResult;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 登录控制层
 *
 * @author: ws
 * @date: 2024/3/7 13:33
 */
@Validated
@Controller
public class LoginController {

    @Autowired
    private TraceWalkingUserService traceWalkingUserService;
    @Autowired
    private TraceWalkingCompeteService traceWalkingCompeteService;
    @Autowired
    private TraceWalkingMethodService traceWalkingMethodService;
    @Autowired
    private AnalyzeController analyzeController;

    /**
     * 跳转登录页面
     *
     * @author ws
     * @date 2024/3/7 13:36
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 登陆成功后跳转的页面
     *
     * @author ws
     * @date 2024/3/7 13:36
     */
    @GetMapping(value = {"", "/", "/index"})
    public String index(Model model) {
        // 加载服务调用的次数
        JsonResult<?> serverHistogram = analyzeController.serverHistogram();
        model.addAttribute("serverHistogram", serverHistogram.getData());

        // 加载topx的数据
        List<TraceWalkingCompete> competeList = traceWalkingCompeteService.getCompeteByCondition(TraceWalkingDTO.empty());
        model.addAttribute("walkingCompeteList", competeList);

        // 加载方法的调用统计
        MethodInvokeDTO empty = MethodInvokeDTO.empty();
        List<MethodStatVO> methodStatList = traceWalkingMethodService.getMethodInvokeStat(empty);
        model.addAttribute("methodStatList", methodStatList);
        model.addAttribute("methodInvokeDto", empty);
        return "index";
    }

    /**
     * 登陆成功后跳转的页面
     *
     * @author ws
     * @date 2024/3/7 13:36
     */
    @PostMapping("/login/doLogin")
    @ResponseBody
    public JsonResult<?> doLogin(@NotBlank(message = "用户名不能为空！") String username,
                                 @NotBlank(message = "密码不能为空！") String password,
                                 HttpServletRequest request) {

        TraceWalkingUser user = traceWalkingUserService.getUserByUsername(username);
        Assert.notNull(user, "该用户登录信息不存在！");
        Assert.state(StringUtils.equalsIgnoreCase(user.getPassword(), DigestUtils.md5DigestAsHex(password.getBytes())), "该用户的用户名或密码有误！");
        Assert.state(BooleanUtils.isTrue(user.getEnable()), "该用户已注销！");

        // 登录成功
        request.getSession().setAttribute(LoginConstant.LOGIN_USER, user);
        return JsonResult.ofSuccess();
    }

    /**
     * 用户注册
     *
     * @author ws
     * @date 2024/3/7 14:43
     */
    @PostMapping("/login/doRegister")
    @ResponseBody
    public JsonResult<?> doRegister(@NotBlank(message = "用户名不能为空！") String username,
                                    @NotBlank(message = "密码不能为空！") String password,
                                    @NotBlank(message = "确认密码不能为空！") String confirmPwd) {
        Assert.state(StringUtils.equals(password, confirmPwd), "用户密码和确认密码不一致！");

        TraceWalkingUser user = traceWalkingUserService.getUserByUsername(username);
        Assert.isNull(user, "该用户信息已存在！");

        user = new TraceWalkingUser();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setEnable(true);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        traceWalkingUserService.insert(user);
        return JsonResult.ofSuccess();
    }

    /**
     * 用户注销
     *
     * @author ws
     * @date 2024/3/25 9:38
     */
    @PostMapping("/logout")
    @ResponseBody
    public JsonResult<?> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(LoginConstant.LOGIN_USER);
        return JsonResult.ofSuccess();
    }
}
