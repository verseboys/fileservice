package com.scd.filesdk.exception;

/**
 * @author chengdu
 * @date 2019/7/3.
 */
public class ConstructorPoolException extends RuntimeException {

    public ConstructorPoolException(){
        super();
    }

    public ConstructorPoolException(String message){
        super(message);
    }
}
