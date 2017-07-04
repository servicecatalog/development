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

    private static final String TEMPLATE = "template";
    private static final String LABELS_PREFIX = "labels.";
    private static final String VALUES_PREFIX = "values.";
    private static final String SUBCRIPTION_STATUS_EXISTS = "SUBSCRIBED";
    private static final String SUBCRIPTION_STATUS_DELETED = "DELETED";

    public String getJson(Subscription subscription) {
        String jsonString = null;
        Gson gson = new Gson();
        jsonString = gson.toJson(new SubscriptionObject(subscription));
        return jsonString;
    }

    public String getJsonForDelete(Subscription subscription) {
        String jsonString = null;
        Gson gson = new Gson();
        jsonString = gson.toJson(new SubscriptionObject(subscription));
        return jsonString;
    }

    class SubscriptionObject {

        @SerializedName("uuid")
        UUID uuid;
        @SerializedName("status")
        String status;
        @SerializedName("template")
        String template;
        @SerializedName("labels")
        Map<String, String> labels = new HashMap<>();
        @SerializedName("parameters")
        Map<String, String> parameters = new HashMap<>();

        SubscriptionObject(Subscription subscription) {
            this.uuid = subscription.getUuid();
            ParameterSet paramSet = subscription.getParameterSet();
            List<Parameter> params = paramSet.getParameters();

            if (SubscriptionStatus.DEACTIVATED
                    .equals(subscription.getStatus())) {
                this.status = SUBCRIPTION_STATUS_DELETED;
            } else {
                this.status = SUBCRIPTION_STATUS_EXISTS;
                for (Parameter param : params) {
                    ParameterDefinition paramDef = param
                            .getParameterDefinition();
                    String paramId = paramDef.getParameterId();
                    if (paramId != null) {
                        if (TEMPLATE.equals(paramId)) {
                            this.template = param.getValue();
                        } else if (paramId.contains(LABELS_PREFIX)) {
                            paramId = paramId.substring(LABELS_PREFIX.length());
                            labels.put(paramId, param.getValue());
                        } else if (paramId.contains(VALUES_PREFIX)) {
                            paramId = paramId.substring(VALUES_PREFIX.length());
                            parameters.put(paramId, param.getValue());
                        }
                    }
                }
            }

        }
    }

}
