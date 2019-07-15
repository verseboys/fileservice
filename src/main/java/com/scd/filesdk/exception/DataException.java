package com.scd.filesdk.exception;

/**
 * @author chengdu
 * @date 2019/7/15.
 */
public class DataException extends RuntimeException {

    public DataException(){
        super();
    }

    public DataException(String message){
        super(message);
    }
}
