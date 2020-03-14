package com.scd.fileservice.model.vo;

import lombok.Data;

/**
 * @author James
 */
@Data
public class BreakUploadResult {
    private Integer chunk;

    private String fileId;

    private String status;

    private boolean allUploaded;
}
