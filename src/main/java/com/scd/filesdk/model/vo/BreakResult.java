package com.scd.filesdk.model.vo;

import lombok.Data;

import java.io.File;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Data
public class BreakResult {

    private String filePath;

    private boolean writeSuccess;

    private File tempFile;
}
