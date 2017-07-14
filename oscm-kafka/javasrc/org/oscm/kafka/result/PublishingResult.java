package org.oscm.kafka.result;

public enum PublishingResult {

    SUCCESS, ERROR;

    public boolean isError() {
        return this.equals(ERROR);
    }

    public boolean isSuccess() {
        return !isError();
    }
    
}