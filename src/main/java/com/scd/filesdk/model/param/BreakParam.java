package com.scd.filesdk.model.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Data
public class BreakParam {

    private String uid;

    private String id;
    //总分片数量
    private int chunks;

    //当前为第几块分片
    private int chunk;

    private long chunkSize;

    private long size;
    //文件名
    private String name;
    //分片对象
    private MultipartFile file;
    // MD5
    private String md5;

}
