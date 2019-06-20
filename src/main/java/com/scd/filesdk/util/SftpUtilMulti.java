package com.scd.filesdk.util;

import com.jcraft.jsch.*;
import org.slf4j.*;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author chengdu
 * @date 2019/6/19.
 */
public class SftpUtilMulti {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpUtilMulti.class);

    /**
     * 连接SFTP
     * @param host
     * @param port
     * @param username
     * @param password
     * @throws JSchException
     */
    public static ChannelSftp connectSftp(String host, int port, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        jsch.getSession(username, host, port);
        Session sshSession = jsch.getSession(username, host, port);
        sshSession.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        // Kerberos 验证
        sshConfig.put("PreferredAuthentications","publickey,keyboard-interactive,password");
        sshSession.setConfig(sshConfig);
        sshSession.connect();
        LOGGER.info("SFTP Session connected.");
        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
        LOGGER.info("Connected to " + host);
        return channelSftp;
    }

    public static void sftpQuit(ChannelSftp channelSftp) throws JSchException {
        Session session = channelSftp.getSession();
        if(session.isConnected()) {
            session.disconnect();
        }
        if(channelSftp.isConnected()){
            channelSftp.quit();
            channelSftp.disconnect();
        }
        LOGGER.info("client disconnected sftp");
    }

    public static void cdAndMkdirs(ChannelSftp channelSftp, String filepath) throws SftpException {
        filepath.replace("\\","/");
        String[] paths = filepath.split("/");
        for(int i = 0; i < paths.length; i++){
            String path = paths[i];
            path = path.trim();
            if(path.length() == 0){
                continue;
            }
            try {
                channelSftp.cd(path);
            }catch (Exception e){
                channelSftp.mkdir(path);
                LOGGER.info("mkdir "+path + " success");
                channelSftp.cd(path);
            }
        }
    }

    /**
     * 上传文件到远程主机
     * @param inputStream
     * @param destPath
     * @throws SftpException
     */
    public static String upload(ChannelSftp channelSftp, InputStream inputStream,
                                String destPath, String filename) throws SftpException {
        // 创建远程目录文件夹
        cdAndMkdirs(channelSftp, destPath);
        channelSftp.put(inputStream, filename);
        String loginPath = channelSftp.getHome();
        return loginPath + destPath + "/" + filename;
    }
}
