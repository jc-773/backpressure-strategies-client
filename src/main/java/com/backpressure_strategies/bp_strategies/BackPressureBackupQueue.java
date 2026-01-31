package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backpressure_strategies.bp_strategies.common.ExternalService;
import com.backpressure_strategies.bp_strategies.common.WebTrafficDeque;
import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

@Service
public class BackPressureBackupQueue {
    private static final Logger log = LoggerFactory.getLogger(BackPressureBackupQueue.class);

    private ExternalService externalService;

    private WebTrafficDeque backupBufferQueue;

    @Autowired
    public BackPressureBackupQueue(ExternalService externalService) {
        this.externalService = externalService;
        this.backupBufferQueue = new WebTrafficDeque(25);
    }

    public Flux<WebTraffic> backPressureBuffer() {
        // publisher that emits flux's of webtraffic
        var publisher = externalService.consumeWebTraffic();
        log.info("publisher consumed..");

        return publisher
                // filter each webtraffic object with a local validation methodd
                .filter(f -> isIpValid(f))
                // limit 25 webtraffic events at a time (is this even necessary with
                // onBackPressureBuffer set to n)
                .limitRate(25)
                // do, intentionally created to not return anything
                .doOnNext(l -> log.info("web traffic event: {} was consumed", l))
                // (maxSize, Consumer<Webtraffic> callback, actual strategy you want to use)
                .onBackpressureBuffer(100, dropped -> addDroppedItemsToBackupBuffer(dropped),
                        BufferOverflowStrategy.DROP_OLDEST)
                // TODO: once queue becomes empty enough to start taking events, should dump
                // from backup
                .delayElements(Duration.ofSeconds(1));
    }

    private boolean isIpValid(WebTraffic webTraffic) {
        return !webTraffic.getIP().equalsIgnoreCase("null") || !webTraffic.getIP().isEmpty();
    }

    private void addDroppedItemsToBackupBuffer(WebTraffic droppedTraffic) {
        // backupBufferQueue.add(droppedTraffic);
        backupBufferQueue.insertHead(droppedTraffic);
        log.info("{} was added to the queue and popped", droppedTraffic);
    }
}
