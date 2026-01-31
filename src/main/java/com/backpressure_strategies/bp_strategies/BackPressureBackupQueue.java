package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final int MAX_SIZE = 100;

    @Autowired
    public BackPressureBackupQueue(ExternalService externalService) {
        this.externalService = externalService;
        this.backupBufferQueue = new WebTrafficDeque(25);
    }

    public Flux<WebTraffic> backPressureBuffer() {
        // publisher that emits flux's of webtraffic
        var publisher = externalService.consumeWebTraffic();
        AtomicInteger bufferSize = new AtomicInteger();
        log.info("publisher consumed..");

        // TODO: Have to step away but finish refill mechanism

        // return publisher
        // // filter each webtraffic object with a local validation methodd
        // .filter(this::isIpValid)
        // .doOnNext(e -> bufferSize.incrementAndGet())
        // .onBackpressureBuffer(
        // MAX_SIZE,
        // dropped -> {
        // bufferSize.decrementAndGet();
        // addDroppedItemsToBackupBuffer(dropped);
        // },
        // BufferOverflowStrategy.DROP_OLDEST)
        // .map(e -> {
        // var x = bufferSize.decrementAndGet();
        // determineLoadFactorForFilling(e, x);
        // })
        // .delayElements(Duration.ofSeconds(1));

        // TODO: remove placeholder
        return null;
    }

    private WebTraffic determineLoadFactorForFilling(WebTraffic e, int x) {
        while ((double) x / this.MAX_SIZE < 0.95) {
            backupBufferQueue.drain();
        }
        return e;
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
