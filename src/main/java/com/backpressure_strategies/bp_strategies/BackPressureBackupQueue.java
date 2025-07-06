package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backpressure_strategies.bp_strategies.common.ExternalService;
import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class BackPressureBackupQueue {
    private static final Logger log = LoggerFactory.getLogger(BackPressureBackupQueue.class);
    
    private ExternalService externalService;

    private Queue<WebTraffic> backupBufferQueue;
    
    @Autowired
    public BackPressureBackupQueue(ExternalService externalService) {
        this.externalService = externalService;
        this.backupBufferQueue = new ConcurrentLinkedDeque<>();
    }

    public Flux<WebTraffic> backPressureBuffer() {
        var publisher = externalService.consumeWebTraffic();
        log.info("publisher consumed..");
        return publisher
            .filter(f -> isIpValid(f))
            .limitRate(25)
            .onBackpressureBuffer(100, dropped -> addDroppedItemsToBackupBuffer(dropped), BufferOverflowStrategy.DROP_OLDEST)
            .delayElements(Duration.ofSeconds(1));
    }

    private boolean isIpValid(WebTraffic webTraffic) {
        return !webTraffic.getIP().equalsIgnoreCase("null") || !webTraffic.getIP().isEmpty();
    }

    private void addDroppedItemsToBackupBuffer(WebTraffic droppedTraffic) {
       backupBufferQueue.add(droppedTraffic);
       log.info("{} was added to the queue and popped", droppedTraffic);
    }
}
