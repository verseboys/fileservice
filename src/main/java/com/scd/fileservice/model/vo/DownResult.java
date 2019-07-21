package com.scd.fileservice.model.vo;

import lombok.Data;

/**
 * @author chengdu
 * @date 2019/7/21.
 */
@Data
public class DownResult {

    private boolean status;

    private byte[] fileBytes;
}
