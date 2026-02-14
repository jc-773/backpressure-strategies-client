package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backpressure_strategies.bp_strategies.common.ExternalService;
import com.backpressure_strategies.bp_strategies.common.WebTrafficDeque;
import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class BackPressureBackupQueue implements BackPressureQueueInterface {
    private static final Logger log = LoggerFactory.getLogger(BackPressureBackupQueue.class);

    private ExternalService externalService;

    private WebTrafficDeque backupBufferQueue;

    private final int MAX_SIZE = 10;

    @Autowired
    public BackPressureBackupQueue(ExternalService externalService) {
        this.externalService = externalService;
        this.backupBufferQueue = new WebTrafficDeque(10);
    }

    public Flux<WebTraffic> backPressureBuffer() {
        // publisher that emits flux's of webtraffic
        var publisher = externalService.consumeWebTraffic();
        AtomicLong bufferSize = new AtomicLong();
        log.info("publisher consumed..");

        // separate flux that handles draining back up back to subscribers
        Flux<WebTraffic> backupFlux = Flux.defer(() -> {
            if (isQueueBelowWaterMark(bufferSize)) {
                log.info("Queue is below water mark. Will begin draining events");
                return Flux.fromIterable(backupBufferQueue.drain());
            }
            return Flux.empty();
        });
        return Flux
                .merge(publisher.publishOn(Schedulers.fromExecutorService(Executors.newVirtualThreadPerTaskExecutor())),
                        backupFlux)
                // filter each webtraffic object with a local validation methodd
                .filter(this::isIpValid)
                // for webevents that make it past filter, incremement buffersize count
                .doOnNext(e -> bufferSize.incrementAndGet())
                .onBackpressureBuffer(
                        MAX_SIZE,
                        dropped -> {
                            bufferSize.decrementAndGet();
                            log.info("buffer size decremented. buffer size now = {}", bufferSize.get());
                            addDroppedItemsToBackupBuffer(dropped);
                        },
                        BufferOverflowStrategy.DROP_OLDEST)
                .delayElements(Duration.ofSeconds(1));

    }

    private boolean isQueueBelowWaterMark(AtomicLong l) {
        var x = Double.longBitsToDouble(l.get());
        return ((double) x / this.MAX_SIZE < 0.95);

    }

    private boolean isIpValid(WebTraffic webTraffic) {
        return !webTraffic.getIP().equalsIgnoreCase("null") || !webTraffic.getIP().isEmpty();
    }

    private void addDroppedItemsToBackupBuffer(WebTraffic droppedTraffic) {
        // backupBufferQueue.add(droppedTraffic);
        droppedTraffic.setFromDrain(true);
        backupBufferQueue.insertHead(droppedTraffic);
        log.info("{} was added to the backup buffer queue", droppedTraffic);
    }
}
