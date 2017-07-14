/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 06.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.kafka.records;

import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

/**
 * @author stavreva
 *
 */
public class ReleaseRecord {
    

    @SerializedName("version")
    private int version;
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

    public static ReleaseRecord fromJson(String json) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(json, ReleaseRecord.class);
        } catch (JsonSyntaxException e) {
            //log error
            return null;
        }

    }
    
   

    public enum Status {
        @SerializedName(SerializedValues.OPTION_CREATNG)
        CREATING, //

        @SerializedName(SerializedValues.OPTION_UPDATING)
        UPDATING, //

        @SerializedName(SerializedValues.OPTION_DELETING)
        DELETING, //

        @SerializedName(SerializedValues.OPTION_PENDING)
        PENDING, //

        @SerializedName(SerializedValues.OPTION_DEPLOYED)
        DEPLOYED, //

        @SerializedName(SerializedValues.OPTION_DELETED)
        DELETED, //

        @SerializedName(SerializedValues.OPTION_FAILED)
        FAILED; //
        
        public static class SerializedValues {
            public static final String OPTION_CREATNG = "creating";
            public static final String OPTION_UPDATING = "updating";
            public static final String OPTION_DELETING = "deleting";
            public static final String OPTION_PENDING = "pending";
            public static final String OPTION_DEPLOYED = "deployed";
            public static final String OPTION_DELETED = "deleted";            
            public static final String OPTION_FAILED = "failed";
        }
    }

}
