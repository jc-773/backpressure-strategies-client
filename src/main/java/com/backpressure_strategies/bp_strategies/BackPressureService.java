package com.backpressure_strategies.bp_strategies;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backpressure_strategies.bp_strategies.common.ExternalService;
import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

@Service
public class BackPressureService {

    private static final Logger log = LoggerFactory.getLogger(BackPressureService.class);
    
    private ExternalService externalService;

    private List<WebTraffic> listOfDroppedItems; 

    @Autowired
    public BackPressureService(ExternalService externalService) {
        this.externalService = externalService;
        this.listOfDroppedItems = new ArrayList<>();
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
     * 
     * This happens because the WebTraffic items are being enqueued faster than the overflow strategy can dequeue them
    */
    public Flux<WebTraffic> backPressureBuffer() {
        var publisher = externalService.consumeWebTraffic();
        log.info("publisher consumed..");
        return publisher
            .filter(f -> isIpValid(f))
            .onBackpressureBuffer(10, dropped -> addDroppedItemsToList(dropped), BufferOverflowStrategy.DROP_OLDEST)
            .delayElements(Duration.ofSeconds(1));
    }

    private boolean isIpValid(WebTraffic webTraffic) {
        return !webTraffic.getIP().equalsIgnoreCase("null") || !webTraffic.getIP().isEmpty();
    }

    private void addDroppedItemsToList(WebTraffic droppedTraffic) {
        listOfDroppedItems.add(droppedTraffic);
        log.info("there have been {} dropped items.", listOfDroppedItems.size());
    }
}
