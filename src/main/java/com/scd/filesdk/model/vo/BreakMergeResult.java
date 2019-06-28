package com.scd.filesdk.model.vo;

import lombok.Data;

/**
 * @author chengdu
 * @date 2019/6/28.
 */

@Data
public class BreakMergeResult {

    private int chunk;

    private String fileName;

    private boolean mergeStatus;

}
