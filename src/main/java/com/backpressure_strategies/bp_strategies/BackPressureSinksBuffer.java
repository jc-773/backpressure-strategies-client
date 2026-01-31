package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backpressure_strategies.bp_strategies.common.ExternalService;
import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class BackPressureSinksBuffer {
    private static final Logger log = LoggerFactory.getLogger(BackPressureSinksBuffer.class);

    private ExternalService externalService;
    private final Sinks.Many<WebTraffic> sink;
    private Queue<WebTraffic> backupBufferQueue;

    @Autowired
    public BackPressureSinksBuffer(ExternalService externalService) {
        this.externalService = externalService;
        this.backupBufferQueue = new ConcurrentLinkedQueue<>();
        sink = Sinks.many()
                .multicast()
                .onBackpressureBuffer(100);
    }

    public Flux<WebTraffic> backPressureBuffer() {
        externalService.consumeWebTraffic()
                .subscribe(event -> {
                    Sinks.EmitResult result = sink.tryEmitNext(event);
                    if (result.isFailure()) {
                        log.info("the sink is full; adding event to buffer queue");
                        backupBufferQueue.add(event);
                    }
                });
        addBufferedEventsToSink();
        return sink.asFlux()
                .delayElements(Duration.ofSeconds(1));
    }

    private void addBufferedEventsToSink() {
        Flux.interval(Duration.ofSeconds(1))
                .subscribe(tick -> {
                    if (sink.currentSubscriberCount() > 0 && !backupBufferQueue.isEmpty()) {
                        var item = backupBufferQueue.poll();
                        Sinks.EmitResult result = sink.tryEmitNext(item);
                        if (result == Sinks.EmitResult.FAIL_OVERFLOW) {
                            backupBufferQueue.add(item);
                        } else {
                            log.info("re-fed buffered item {} to sink", item);
                        }
                    }
                });
    }
}
