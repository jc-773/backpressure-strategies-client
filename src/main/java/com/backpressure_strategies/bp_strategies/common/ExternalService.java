package com.backpressure_strategies.bp_strategies.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;

import reactor.core.publisher.Flux;

@Service
public class ExternalService {
    
    private WebClient client;

    @Autowired
    public ExternalService() {
        client = WebClient.create();
    }

    public Flux<WebTraffic> consumeWebTraffic() {
        return client.get()
            .uri("http://localhost:8080/sim/web/traffic")
            .retrieve()
            .bodyToFlux(WebTraffic.class);
    }
}
