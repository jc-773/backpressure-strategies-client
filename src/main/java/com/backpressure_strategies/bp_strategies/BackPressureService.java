package com.backpressure_strategies.bp_strategies;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class BackPressureService {

    private static final Logger log = LoggerFactory.getLogger(BackPressureService.class);
    
    private ExternalService externalService;

    @Autowired
    public BackPressureService(ExternalService externalService) {
        this.externalService = externalService;
    }

    //lets just say, hypothetically, that the consumer is a little slower than the bursts we may get from the publisher. To mimic that, I will add a delay
    public Flux<WebTraffic> backPressureBuffer() {
        var publisher = externalService.consumeWebTraffic();
        log.info("publisher consumed..");
        return publisher
            .onBackpressureBuffer(10, dropped -> log.warn("item {} dropped due to buffer meeting capacity", dropped))
            .delayElements(Duration.ofSeconds(2));
    }
}
