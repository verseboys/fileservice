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
    // file:breakrecord:%s type string 存储上传块记录 0 未上传、1 已上传
    public static final String FILE_BREAK_RECORD = "file:breakrecord:%s";
    // file:breakaddress:%s type List 存储切片存储地址
    public static final String FILE_BREAK_ADDRESS = "file:breakaddress:%s";

    public static final String CHUNK_NOT_UPLOADED = "0";
    public static final String CHUNK_UPLOADED = "1";

    // 整块存储在服务器
    public static final String STORE_TYPE_ALL = "all";
    // 切片存储服务器
    public static final String STORE_TYPE_BLOCK = "block";


    public static final String RESULT_PART_UPLOAD = "part_uploded";

    public static final String RESULT_ALL_UPLOAD = "all_uploaded";

    public static final String STR_TRUE = "true";

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
        chunknum("chunk_num"),
        filestatus("file_status");

        private String value;

        BREAKINFO(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }

    public enum RESPONSE {
        SUCCESS(1),
        ERROR(0);

        private int code;

        RESPONSE(int code){
            this.code = code;
        }
        public int getCode(){
            return code;
        }
    }

    public enum FILESTATUS {
        // 未上传
        NOT("0"),
        // 上传部分
        PART("1"),
        // 已上传完成
        ALL("2");

        private String status;

        FILESTATUS(String status){
            this.status = status;
        }

        public String getStatus(){
            return status;
        }
    }
}
