package org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration;

public class CompleteJobConfig {

    private long jobKey;

    private String payload;

    public CompleteJobConfig() {
    }

    public CompleteJobConfig(long jobKey, String payload) {
        this.jobKey = jobKey;
        this.payload = payload;
    }

    public long getJobKey() {
        return jobKey;
    }

    public void setJobKey(long jobKey) {
        this.jobKey = jobKey;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
