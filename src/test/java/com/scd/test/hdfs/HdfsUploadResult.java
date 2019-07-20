package com.scd.test.hdfs;

import lombok.Data;

/**
 * @author James Chen
 * @date 20/07/19
 */
@Data
public class HdfsUploadResult {

    private boolean status;
    private String fileName;
    private String storePath;
    private long time;
}

