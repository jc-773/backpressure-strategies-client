package com.backpressure_strategies.bp_strategies.model;

import org.jspecify.annotations.Nullable;

public class WebTraffic {

    private String url;
    private String IP;
    private long timeStamp;
    @Nullable
    private boolean fromDrain;

    public WebTraffic(String url, String IP, long timeStamp) {
        this.url = url;
        this.IP = IP;
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isFromDrain() {
        return fromDrain;
    }

    public void setFromDrain(boolean fromDrain) {
        this.fromDrain = fromDrain;
    }
}
