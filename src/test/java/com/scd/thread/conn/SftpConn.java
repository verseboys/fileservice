package com.scd.thread.conn;

import lombok.Data;

/**
 * @author chengdu
 * @date 2019/6/30.
 */
@Data
public class SftpConn {

    private Integer id;

    public SftpConn(Integer id) {
        this.id = id;
    }
}
