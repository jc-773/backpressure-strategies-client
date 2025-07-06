package com.backpressure_strategies.bp_strategies;

import org.springframework.web.bind.annotation.RestController;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class BackPressureController {

    private BackPressureService backPressureService;
    private BackPressureLimitRate limitRateService;

    @Autowired
    public BackPressureController(BackPressureService backPressureService, BackPressureLimitRate limitRateService) {
        this.backPressureService = backPressureService;
        this.limitRateService = limitRateService;
    }
    

    @GetMapping(value="/backpressure/strategy/onbuffer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<WebTraffic> getOnBufferStrategy() {
        return limitRateService.backPressureBuffer();
    }
    
}
