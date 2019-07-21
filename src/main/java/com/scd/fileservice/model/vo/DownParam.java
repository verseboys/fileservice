package com.scd.fileservice.model.vo;

import lombok.Data;

/**
 * @author chengdu
 * @date 2019/7/21.
 */
@Data
public class DownParam {

    private String fileId;

    private Integer chunk;

    private String uploadType;
}
