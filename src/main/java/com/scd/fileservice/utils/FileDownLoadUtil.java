package com.scd.fileservice.utils;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
public class FileDownLoadUtil {

    public static void outputFile(HttpServletResponse response, InputStream inputStream, String filename) throws UnsupportedEncodingException {
        filename = filename.replace(" ", "_");
        filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType(MediaType.MULTIPART_FORM_DATA.toString());
        //2.设置文件头
        response.setHeader("Content-Disposition", "attachment;fileName="+filename);
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        try{
            bis = new BufferedInputStream(inputStream);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
