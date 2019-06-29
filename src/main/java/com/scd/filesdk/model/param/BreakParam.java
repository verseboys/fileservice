package com.scd.filesdk.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Data
@ApiModel(value = "break point model")
public class BreakParam {

    private String uid;

    private String id;

    // 总分片数量
    @ApiModelProperty(required = true)
    private int chunks;

    // 当前为第几块分片
    @ApiModelProperty(required = true)
    private int chunk;

    // 切片大小
    @ApiModelProperty(required = true)
    private long chunkSize;

    // 文件总大小
    @ApiModelProperty(required = true)
    private long size;

    //文件名
    @ApiModelProperty(required = true)
    private String name;

    //分片对象
    @ApiModelProperty(required = true)
    private MultipartFile file;
    // MD5
    @ApiModelProperty(required = true)
    private String md5;

}
