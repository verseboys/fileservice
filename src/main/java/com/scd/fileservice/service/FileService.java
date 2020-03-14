package com.scd.fileservice.service;

import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.param.UploadParam;
import com.scd.fileservice.model.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
public interface FileService {

    String upload(MultipartFile multipartFile, String type, UploadParam uploadParam) throws Exception;

    String download(String fileId, HttpServletResponse response) throws Exception;

    BreakUploadResult breakUpload(BreakParam breakParam, String type);

    BreakStatus checkBreakUploadStatus(String md5);

    BreakFileInfo checkFile(String fileId);

    byte[] downloadChunk(DownParam downParam) throws Exception;

    DeleteResult deleteOne(String fileId);
}
