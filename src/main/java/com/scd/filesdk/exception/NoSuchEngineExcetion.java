package com.scd.filesdk.exception;

import java.util.NoSuchElementException;

/**
 * @author chengdu
 */
public class NoSuchEngineExcetion extends RuntimeException {

    public NoSuchEngineExcetion(){
        super();
    }

    public NoSuchEngineExcetion(String message) {
        super(message);
    }
}
