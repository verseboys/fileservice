package com.scd.thread.apachepool;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author chengdu
 * @date 2019/7/1.
 */
public class DequeTest {

    public static void main(String[] args){
        Deque<Integer> deque = new LinkedList<Integer>();
        deque.addFirst(1);
        deque.addFirst(2);
    }
}
