package com.backpressure_strategies.bp_strategies.common;

import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    
    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static <T> Subscriber<T> subscribe() {
        return new DefaultSubscriber<>("");
    }

     public static <T> Subscriber<T> subscribe(String name) {
        return new DefaultSubscriber<>(name);
    }
}
