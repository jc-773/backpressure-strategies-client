package com.backpressure_strategies.bp_strategies.model;

public class WebTraffic {
    
    private String url;
    private String IP;
    private long timeStamp;

    public WebTraffic(String url, String iP, long timeStamp) {
        this.url = url;
        IP = iP;
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

    
}
