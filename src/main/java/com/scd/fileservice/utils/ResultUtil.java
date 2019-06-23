package com.scd.fileservice.utils;

import com.scd.fileservice.common.CommonConstant;
import com.scd.fileservice.model.vo.Result;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
public class ResultUtil {

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.RESPONSE.SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> successMsg(T data, String msg){
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.RESPONSE.SUCCESS.getCode());
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(T data){
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.RESPONSE.ERROR.getCode());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> errorMsg(T data,String msg){
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.RESPONSE.ERROR.getCode());
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> errorWithOutData(String msg){
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.RESPONSE.ERROR.getCode());
        result.setMsg(msg);
        return result;
    }
}
