package com.scd.fileservice.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author chengdu
 * @date 2019/6/23.
 */
@Data
public class BreakStatus {

    private String fileStatus;

    private Long chunkSize;

    private List<Integer> notUploaded;

    public BreakStatus(String fileStatus){
        this.fileStatus = fileStatus;
    }

    public BreakStatus(String fileStatus, Long chunkSize, List<Integer> data) {
        this.fileStatus = fileStatus;
        this.chunkSize = chunkSize;
        this.notUploaded = data;
    }
}
