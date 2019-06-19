package com.scd.filesdk.engine;

import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chengdu
 */
public abstract class BaseEngine {

    public abstract String upload(String filePath) throws Exception;

    public abstract String upload(InputStream inputStream, String filename) throws Exception ;

    public abstract String upload(byte[] fbyte, String filename) throws Exception;
}
