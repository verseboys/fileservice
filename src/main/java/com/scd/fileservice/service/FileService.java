package com.scd.fileservice.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
public interface FileService {

    String upload(MultipartFile multipartFile, String type) throws Exception;

    String download(String fileId, HttpServletResponse response) throws Exception;
}
