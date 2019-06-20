package com.scd.filesdk.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.scd.filesdk.common.ServiceInfo;

import java.io.*;
import java.net.URLEncoder;

/**
 * @author chengdu
 * @date 2019/6/20.
 */
public class QnUtil {

    public static String getToken(String accessKey, String secretKey, String bucket){
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    public static String upload(InputStream inputStream, String key, String token){
        return "";
    }

    public void uploadTest(){
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = ServiceInfo.QN.accessKey;
        String secretKey = ServiceInfo.QN.secretKey;
        String bucket = ServiceInfo.QN.bucket;
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        try {
            String filePath = "C:\\Users\\chengdu\\Desktop\\filetype\\jcommon-1.0.16.jar";
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            System.out.println(upToken);
            key = FileUtil.getFileName(filePath);
            try {
                InputStream inputStream = new FileInputStream(filePath);
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            } catch (FileNotFoundException e){

            }
        } catch (Exception ex) {
            //ignore
        }
    }

    public static void main(String[] args) throws Exception {
    }
}
