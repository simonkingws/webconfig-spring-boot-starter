package io.github.simonkingws.webconfig.common.core;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Map;

/**
 * 统一返回参数信息
 *
 * @author: ws
 * @date: 2024/1/29 10:39
 */
@Data
@Builder
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = -5988947991247718443L;

    /**
     *  返回成功的标识
     */
    private Boolean success;
    /**
     *  提示信息
     */
    private String msg;
    /**
     *  返回的数据
     */
    private T data;
    /**
     *  code
     */
    private String code;
    /**
     *  扩展信息
     */
    private Map<String, Object> extInfo;

    @Tolerate
    public JsonResult() {
    }

    public static JsonResult<?> ofFail(String msg){
        return JsonResult.builder().success(false).msg(msg).build();
    }

    public static JsonResult<?> ofFail(String msg, String code){
        return JsonResult.builder().success(false).msg(msg).code(code).build();
    }

    public static <T> JsonResult<T> ofSuccess(){
        return JsonResult.<T>builder().success(true).build();
    }

    public static <T> JsonResult<T> ofSuccess(T data){
        return JsonResult.<T>builder().success(true).data(data).build();
    }

    public static <T> JsonResult<T> ofSuccess(T data, String code){
        return JsonResult.<T>builder().success(true).data(data).code(code).build();
    }
}
