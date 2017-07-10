/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 06.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.applicationservice.data;

import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author stavreva
 *
 */
public class ReleaseMessage {

    @SerializedName("id")
    private UUID id;
    @SerializedName("etag")
    private UUID etag;
    @SerializedName("status")
    private Status status;
    @SerializedName("operation")
    private Operation operation;
    @SerializedName("instance")
    private UUID instance;
    @SerializedName("services")
    private Map<String, String> services;   

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEtag() {
        return etag;
    }

    public void setEtag(UUID etag) {
        this.etag = etag;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public UUID getInstance() {
        return instance;
    }

    public void setInstance(UUID instance) {
        this.instance = instance;
    }

    public Map<String, String> getServices() {
        return services;
    }

    public void setServices(Map<String, String> services) {
        this.services = services;
    }

    public void putService(String key, String value) {
        this.services.put(key, value);
    }
    
    public static ReleaseMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ReleaseMessage.class);
    }

    public enum Status {
        PENDING, DEPLOYED, DELETED, FAILED
    }

    public enum Operation {
        NEW, UPD, DEL
    }
    
}
