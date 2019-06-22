package com.scd.fileservice.common;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
public class CommonConstant {

    // files type set  存储文件 id 集合
    public static final String FILES = "files";
    // file:{id} type hash 存储文件信息
    public static final String FILE_INFO = "file:%s";
    // file:breakinfo:%s type hash 存储断点文件上传信息
    public static final String FILE_BREAK_INFO = "file:breakinfo:%s";
    // file:breakrecord:%s type list 存储上传块记录 0 未上传、1 已上传
    public static final String FILE_BREAK_RECORD = "file:breakrecord:{id}";

    public static final String CHUNK_UPLOADED = "1";

    // 整块存储在服务器
    public static final String STORE_TYPE_ALL = "all";
    // 切片存储服务器
    public static final String STORE_TYPE_BLOCK = "chunk";

    public enum FILEINFO {

        filename("file_name"),
        filesize("file_size"),
        fileaddress("file_address"),
        uploadtype("upload_type"),
        uploadtime("upload_time"),
        storetype("store_type");

        private String value;

        FILEINFO(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }

    public enum BREAKINFO {

        chunksize("chunk_size"),
        filestatus("file_status");

        private String value;

        BREAKINFO(String value){
            this.value = value;
        }
    }

    public enum REPONSE {
        SUCCESS(1),
        ERROR(0);

        private int code;

        REPONSE(int code){
            this.code = code;
        }
        public int getCode(){
            return code;
        }
    }
}
