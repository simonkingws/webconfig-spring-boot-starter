package io.github.simonkingws.webconfig.trace.admin.dto;

import io.github.simonkingws.webconfig.trace.admin.enums.Activation;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 方法调用的参数
 *
 * @author: ws
 * @date: 2024/3/20 16:40
 */
@Data
public class MethodInvokeDTO implements Serializable {

    private static final long serialVersionUID = -5781202296116739461L;

    /** 调用时间：开始时间 */
    private Date invokeTimeStart;

    /** 调用时间：结束时间 */
    private Date invokeTimeEnd;

    /** 调用方法类名 */
    private String className;

    /** 调用方法 */
    private String methodName;

    /** 活跃度高低排序 */
    private Activation activation;

    /** 查询的条数 */
    private Integer topSum;

    public static MethodInvokeDTO empty() {
        MethodInvokeDTO methodInvokeDTO = new MethodInvokeDTO();
        methodInvokeDTO.setActivation(Activation.LOW);
        methodInvokeDTO.setTopSum(5);

        Date date = new Date();
        methodInvokeDTO.setInvokeTimeEnd(date);
        methodInvokeDTO.setInvokeTimeStart(DateUtils.addMonths(new Date(), -3));
        return methodInvokeDTO;
    }
}
