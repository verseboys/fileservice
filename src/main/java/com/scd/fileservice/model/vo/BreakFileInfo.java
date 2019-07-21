package com.scd.fileservice.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author chengdu
 * @date 2019/7/21.
 */
@Data
public class BreakFileInfo {

    private boolean isExists;

    private boolean isBlock;

    private String message;

    private String uploadType;

    private Long chunkSize;

    private List<String> fileAddress;
}
