package com.backpressure_strategies.bp_strategies.common;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;


/*
 * My own custom queue built as a secondary buffer for WebTraffic events
 */

public class WebTrafficDeque {
    
    private LinkedWebTrafficList queue;

    public WebTrafficDeque() {
        queue = new LinkedWebTrafficList();
    }

    public boolean isEmpty() {
        return queue.getValues().size() > 0 ? false : true;
    }

    public void append(WebTraffic element) {
        queue.inserTail(element);
    }

    public void appendPriority(WebTraffic element) {
        queue.insertHead(element);
    }

    public WebTraffic pop() {
        return queue.get(queue.getValues().size());
    }

    public WebTraffic popPriority() {
        return queue.get(1);
    }
}
