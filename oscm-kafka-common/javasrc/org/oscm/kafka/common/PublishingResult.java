package org.oscm.kafka.common;

public enum PublishingResult {

    SUCCESS, ERROR;

    public boolean isError() {
        return this.equals(ERROR);
    }

    public boolean isSuccess() {
        return !isError();
    }
    
}