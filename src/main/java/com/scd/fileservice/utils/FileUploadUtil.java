package com.scd.fileservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chengdu
 * @date 2019/5/26.
 */
@Component
public class FileUploadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    @Value("${remote.file.url}")
    private String fileurl;

    @Autowired
    private RestTemplate restTemplate;

    public void uploadFileByPath(String filePath){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource resource = new FileSystemResource(new File(filePath));
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("file", resource);
        paramMap.add("aliasename","scd");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap);
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(fileurl, httpEntity, Object.class);
        LOGGER.info("upload result---"+responseEntity.getBody());
    }

    public void uploadFileByInputStream(InputStream inputStream, String fileName) throws IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes){
            @Override
            public String getFilename(){
                return fileName;
            }
        };
        paramMap.add("file", byteArrayResource);
        paramMap.add("name","scd");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap);
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(fileurl, httpEntity, Object.class);
        LOGGER.info("upload result---"+responseEntity.getBody());
    }

    public void uploadFileByInputStreamEx(InputStream inputStream, String fileName) throws IOException{
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes){
            @Override
            public String getFilename(){
                return fileName;
            }
        };
        paramMap.add("file", byteArrayResource);
        paramMap.add("name","scd");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap);
        String url = "http://localhost:8081/mongodbstu/single/fileupload.do";
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        LOGGER.info("upload result---{}",result.getBody());
    }
}
