package com.scd.fileservice.model.vo;

import lombok.Data;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Data
public class Result<T> {

    private Integer code;

    private T data;

    private String msg;
}
