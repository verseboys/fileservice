package com.scd.fileservice.service;

import com.scd.filesdk.model.param.BreakParam;
import com.scd.fileservice.model.vo.BreakStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
public interface FileService {

    String upload(MultipartFile multipartFile, String type) throws Exception;

    String download(String fileId, HttpServletResponse response) throws Exception;

    String breakUpload(BreakParam breakParam, String type);

    BreakStatus checkBreakUploadStatus(String md5);

}
