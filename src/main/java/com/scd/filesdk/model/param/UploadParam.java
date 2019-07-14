package com.scd.filesdk.model.param;

import lombok.Data;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
@Data
public class UploadParam {

    private String fileName;

    private String groupName;

    public UploadParam(){
    }

    public UploadParam(String fileName){
        this.fileName = fileName;
    }

    public UploadParam(String fileName, String groupName){
        this.fileName = fileName;
        this.groupName = groupName;
    }
}
