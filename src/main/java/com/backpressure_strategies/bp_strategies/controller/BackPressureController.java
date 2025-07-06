package com.backpressure_strategies.bp_strategies.controller;

import org.springframework.web.bind.annotation.RestController;

import com.backpressure_strategies.bp_strategies.BackPressureBackupQueue;
import com.backpressure_strategies.bp_strategies.BackPressureLimitRate;
import com.backpressure_strategies.bp_strategies.BackPressureService;
import com.backpressure_strategies.bp_strategies.BackPressureSinksBuffer;
import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class BackPressureController {

    private BackPressureService backPressureService;
    private BackPressureLimitRate limitRateService;
    private BackPressureBackupQueue backPressureQueue;
    private BackPressureSinksBuffer backPressureSinksBuffer;

    @Autowired
    public BackPressureController(BackPressureService backPressureService, BackPressureLimitRate limitRateService, BackPressureBackupQueue backPressureQueue
    ,BackPressureSinksBuffer backPressureSinksBuffer) {
        this.backPressureService = backPressureService;
        this.limitRateService = limitRateService;
        this.backPressureQueue = backPressureQueue;
        this.backPressureSinksBuffer = backPressureSinksBuffer;
    }
    

    @GetMapping(value="/backpressure/strategy/onbuffer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<WebTraffic> getOnBufferStrategy() {
        return backPressureSinksBuffer.backPressureBuffer();
    }
    
}
