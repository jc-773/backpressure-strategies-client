package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

@Service
public class BackPressureLimitRate {
    private static final Logger log = LoggerFactory.getLogger(BackPressureService.class);
    
    private ExternalService externalService;

    private List<WebTraffic> listOfDroppedItems; 

    @Autowired
    public BackPressureLimitRate(ExternalService externalService) {
        this.externalService = externalService;
        this.listOfDroppedItems = new ArrayList<>();
    }

    public Flux<WebTraffic> backPressureBuffer() {
        var publisher = externalService.consumeWebTraffic();
        log.info("publisher consumed..");
        return publisher
            .filter(f -> isIpValid(f))
            .limitRate(25)
            .onBackpressureBuffer(100, dropped -> addDroppedItemsToList(dropped), BufferOverflowStrategy.DROP_OLDEST)
            .delayElements(Duration.ofSeconds(1));
    }

    private boolean isIpValid(WebTraffic webTraffic) {
        return !webTraffic.IP().equalsIgnoreCase("null") || !webTraffic.IP().isEmpty();
    }

    private void addDroppedItemsToList(WebTraffic droppedTraffic) {
        listOfDroppedItems.add(droppedTraffic);
        log.info("there have been {} dropped items.", listOfDroppedItems.size());
    }
}
