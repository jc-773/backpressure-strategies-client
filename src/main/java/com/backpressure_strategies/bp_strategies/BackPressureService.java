package com.backpressure_strategies.bp_strategies;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

@Service
public class BackPressureService {

    private static final Logger log = LoggerFactory.getLogger(BackPressureService.class);
    
    private ExternalService externalService;

    @Autowired
    public BackPressureService(ExternalService externalService) {
        this.externalService = externalService;
    }

    /*
     * lets just say, hypothetically, that the consumer is a little slower than the bursts we may get from the publisher. 
     * To mimic that, I will add a delay
     * 
     * I also added in a BufferOverFlowStrategy as a third argument to onBackpressureBuffer
     * 
     * I recommend trying to run the app without it to see how Spring and Java throw an OverflowException adn closes the conncection once the queue's capacity is full
     * 
     * Adding an 'drop latest' overflow strategy allows the connection to stay open and continuing to process web traffic
     * 
     * BUT LOOK HOW MUCH DATA IS DROPPED  🙈
    */
    public Flux<WebTraffic> backPressureBuffer() {
        var publisher = externalService.consumeWebTraffic();
        log.info("publisher consumed..");
        return publisher
            .filter(f -> isIpValid(f))
            .onBackpressureBuffer(10, dropped -> log.warn("item {} dropped due to buffer meeting capacity", dropped), BufferOverflowStrategy.DROP_OLDEST)
            .delayElements(Duration.ofSeconds(1));
    }

    private boolean isIpValid(WebTraffic webTraffic) {
        return !webTraffic.IP().equalsIgnoreCase("null") || !webTraffic.IP().isEmpty();
    }
}
