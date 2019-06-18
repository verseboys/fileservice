package com.scd.filesdk.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
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
    public static void connectSftp(String host, int port, String username, String password) throws JSchException {
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
        if(channelSftp != null && channelSftp.isConnected()) {
            Session oldSession = channelSftp.getSession();
            if(oldSession.isConnected()) {
                if (oldSession.getHost().equals(host) && oldSession.getPort() == port
                        && username.equals(username)) {
                    LOGGER.info("client has connected sftp, host " + host+",port " + port);
                    connected = true;
                }
            }
        }
        return connected;
    }

    public static void sftpQuit() throws JSchException {
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

    /**
     * cd 到指定文件夹
     * @param filepath
     * @throws SftpException
     */
    public static void cdAndMkdirs(String filepath) throws SftpException {
        filepath.replace("\\","/");
        String[] paths = filepath.split("/");
        // 最后一个文件名的数据不 cd
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
    public static String upload(InputStream inputStream, String destPath, String filename) throws SftpException {
        // 创建远程目录文件夹
        cdAndMkdirs(destPath);
        channelSftp.put(inputStream, filename);
        // 回到home连接目录
        String loginPath = channelSftp.getHome();
        channelSftp.cd(loginPath);
        return loginPath + destPath;
    }

    public static void main(String[] args) throws Exception {
        String host = "192.168.1.104";
        int port = 22;
        String username = "test";
        String password = "test";
        connectSftp(host, port, username, password);
        connectSftp(host, port, username, password);
        System.out.println(channelSftp.isConnected());
        System.out.println(channelSftp.getHome());
        String testPath = "12345/a/bb";
        String path = "C:\\Users\\chengdu\\Desktop\\logback.xml";
        InputStream inputStream = new FileInputStream(path);
        upload(inputStream, testPath, "logback.xml");
        sftpQuit();
    }
}
