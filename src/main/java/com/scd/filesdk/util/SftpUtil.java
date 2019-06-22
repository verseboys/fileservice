package com.scd.filesdk.util;

import com.jcraft.jsch.*;
import com.scd.filesdk.common.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 多线程情况下有问题
 * @author chengdu
 * @date 2019/6/18.
 */
public class SftpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SftpUtil.class);

    private static ChannelSftp channelSftp = null;

    /**
     * 连接SFTP
     * @param host
     * @param port
     * @param username
     * @param password
     * @throws JSchException
     */
    public static void  connectSftp(String host, int port, String username, String password) throws JSchException {
        if(isSftpConnected(host, port, username)){
            return ;
        }
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
        channelSftp = (ChannelSftp) channel;
        LOGGER.info("Connected to " + host);
    }

    /**
     * 判断sftp是否为连接状态
     * @param host
     * @param port
     * @param username
     * @return
     * @throws JSchException
     */
    public static boolean isSftpConnected(String host, int port, String username) throws JSchException{
        boolean connected = false;
        if (channelSftp != null && channelSftp.isConnected()) {
            Session oldSession = channelSftp.getSession();
            if (oldSession.isConnected()) {
                if (oldSession.getHost().equals(host) && oldSession.getPort() == port
                        && oldSession.getUserName().equals(username)) {
                    LOGGER.info("client has connected sftp, host " + host + ",port " + port);
                    connected = true;
                }
            }
        }
        return connected;
    }

    public static void sftpQuit() {
        try {
            Session session = channelSftp.getSession();
            if (session.isConnected()) {
                session.disconnect();
            }
            if (channelSftp.isConnected()) {
                channelSftp.quit();
                channelSftp.disconnect();
            }
            LOGGER.info("client disconnected sftp");
        }catch (JSchException e){
            LOGGER.error("client disconnected sftp error {}",e);
        }
        LOGGER.info("client disconnected sftp");
    }

    /**
     * cd 到指定文件夹
     * @param filepath
     * @throws SftpException
     */
    public static void cdAndMkdirs(String filepath) throws SftpException {
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
                System.out.println(path);
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
    public static String upload(InputStream inputStream, String destPath, String filename) throws SftpException {
        // 创建远程目录文件夹
        cdAndMkdirs(destPath);
        channelSftp.put(inputStream, filename);
        // 回到home连接目录
        String loginPath = channelSftp.getHome();
        channelSftp.cd(loginPath);
        return loginPath + destPath;
    }

    public static String upload(String srcpath, String destPath, String filename) throws SftpException {
        // 创建远程目录文件夹
        cdAndMkdirs(destPath);
        channelSftp.put(srcpath, filename);
        // 回到home连接目录
        String loginPath = channelSftp.getHome();
        channelSftp.cd(loginPath);
        return loginPath + destPath + "/" + filename;
    }

    /**
     * 下载文件
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public static InputStream download(String remotePath) throws SftpException{
        return channelSftp.get(remotePath);
    }

    public static void main(String[] args) throws Exception {
        String host = ServiceInfo.SFTP.host;
        int port = ServiceInfo.SFTP.port;
        String username = ServiceInfo.SFTP.username;
        String password = ServiceInfo.SFTP.password;
        connectSftp(host, port, username, password);
        connectSftp(host, port, username, password);
        System.out.println(channelSftp.isConnected());
        System.out.println(channelSftp.getHome());
        String testPath = "/12345";
        String path = "C:\\Users\\chengdu\\Desktop\\logback.xml";
        InputStream inputStream = new FileInputStream(path);
        upload(inputStream, testPath, "logback3.xml");
        sftpQuit();
    }
}
