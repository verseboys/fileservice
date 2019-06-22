package com.scd.filesdk.engine;


import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;

import java.io.InputStream;

/**
 * @author chengdu
 */
public abstract class BaseEngine {

    public abstract String upload(String filePath) throws Exception;

    public abstract String upload(InputStream inputStream, String filename) throws Exception ;

    public abstract String upload(byte[] fbyte, String filename) throws Exception;

    public abstract InputStream download(String remotePath) throws Exception;

    public abstract BreakResult upload(BreakParam breakParam);
}
