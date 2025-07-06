package com.backpressure_strategies.bp_strategies.common;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;

public class WebTrafficNode {
    private WebTraffic value;
    private WebTrafficNode next;

    public WebTrafficNode(WebTraffic value) {
        this.value = value;
    }

    public WebTrafficNode(WebTraffic value, WebTrafficNode next) {
        this.value = value;
        this.next = next;
    }
   
    public WebTraffic getValue() {
        return value;
    }

    public void setValue(WebTraffic value) {
        this.value = value;
    }

    public WebTrafficNode getNext() {
        return next;
    }
    
    public void setNext(WebTrafficNode next) {
        this.next = next;
    }
}
