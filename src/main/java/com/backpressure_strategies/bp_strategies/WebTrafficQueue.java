package com.backpressure_strategies.bp_strategies;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebTrafficQueue {
    private Queue<WebTraffic> queue;

    public WebTrafficQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    public boolean isEmpty() {
        return false;
    }

    public void append(WebTraffic e) {

    }

    public void appendLeft() {

    }

    public int pop() {
        return -1;
    }

    public int popLeft() {
        return -1;
    }
}
