/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 03.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.applicationservice.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.ParameterDefinition;
import org.oscm.domobjects.ParameterSet;
import org.oscm.domobjects.Subscription;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author stavreva
 *
 */
public class SubscriptionMessage {

    private static final String TEMPLATE_PREFIX = "template.";
    private static final String TARGET = "target";
    private static final String NAMESPACE = "namespace";
    private static final String LABELS_PREFIX = "labels.";
    private static final String PARAMETERS_PREFIX = "parameters.";

    @SerializedName("id")
    UUID id;
    @SerializedName("etag")
    UUID etag;
    @SerializedName("operation")
    String operation;
    @SerializedName("namespace")
    String namespace;
    @SerializedName("template")
    Map<String, String> template = new HashMap<>();
    @SerializedName("target")
    String target;
    @SerializedName("labels")
    Map<String, String> labels = new HashMap<>();
    @SerializedName("parameters")
    Map<String, String> parameters = new HashMap<>();

    public SubscriptionMessage(Subscription subscription) {
        this.id = subscription.getUuid();
        this.etag = UUID.randomUUID();
        ParameterSet paramSet = subscription.getParameterSet();
        List<Parameter> params = paramSet.getParameters();

        this.operation = getOperation(subscription).name();
        
        if (Operation.DEL.name().equals(this.operation)) {
            return;
        }
        
        for (Parameter param : params) {
            ParameterDefinition paramDef = param.getParameterDefinition();
            String paramId = paramDef.getParameterId();
            if (paramId != null) {
                if (TARGET.equals(paramId)) {
                    this.target = param.getValue();
                } else if (NAMESPACE.equals(paramId)) {
                    this.namespace = param.getValue();
                } else if (paramId.contains(TEMPLATE_PREFIX)) {
                    paramId = paramId.substring(TEMPLATE_PREFIX.length());
                    template.put(paramId, param.getValue());
                } else if (paramId.contains(LABELS_PREFIX)) {
                    paramId = paramId.substring(LABELS_PREFIX.length());
                    labels.put(paramId, param.getValue());
                } else if (paramId.contains(PARAMETERS_PREFIX)) {
                    paramId = paramId.substring(PARAMETERS_PREFIX.length());
                    parameters.put(paramId, param.getValue());
                }
            }
        }
    }

    Operation getOperation(Subscription subscription) {
        if (SubscriptionStatus.DEACTIVATED.equals(subscription.getStatus())) {
            return Operation.DEL;
        }
        if (SubscriptionStatus.PENDING.equals(subscription.getStatus())) {
            return Operation.NEW;
        }         
        if (SubscriptionStatus.PENDING_UPD
                .equals(subscription.getStatus())) {
            return Operation.UPD;
        }
        return null;
    }

    public enum Operation {
        NEW, UPD, DEL
    }

    public String toJson() {
        String jsonString = null;
        Gson gson = new Gson();
        jsonString = gson.toJson(this);
        return jsonString;
    }

}
