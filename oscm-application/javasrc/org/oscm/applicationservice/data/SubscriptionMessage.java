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

    private static final String RELEASE_ID = "release.id";
    private static final String CHART_ID = "chart.id";
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

        @SerializedName("subscription.uuid")
        UUID uuid;
        @SerializedName("subscription.status")
        String status;
        @SerializedName("chart.id")
        String chartid;
        @SerializedName("release.id")
        String releaseid;
        Map<String, String> paramMap = new HashMap<>();

        SubscriptionObject(Subscription subscription) {
            this.uuid = subscription.getUuid();
            ParameterSet paramSet = subscription.getParameterSet();
            List<Parameter> params = paramSet.getParameters();

            if (SubscriptionStatus.DEACTIVATED
                    .equals(subscription.getStatus())) {
                this.status = SUBCRIPTION_STATUS_DELETED;
                for (Parameter param : params) {
                    ParameterDefinition paramDef = param
                            .getParameterDefinition();
                    String paramId = paramDef.getParameterId();
                    if (paramId != null && RELEASE_ID.equals(paramId)) {
                        this.releaseid = param.getValue();
                        break;
                    }
                }
            } else {
                this.status = SUBCRIPTION_STATUS_EXISTS;
                for (Parameter param : params) {
                    ParameterDefinition paramDef = param
                            .getParameterDefinition();
                    String paramId = paramDef.getParameterId();
                    if (paramId != null) {
                        if (CHART_ID.equals(paramId)) {
                            this.chartid = param.getValue();
                        } else if (RELEASE_ID.equals(paramId)) {
                            this.releaseid = param.getValue();
                        } else if (paramId.contains(VALUES_PREFIX)) {
                            paramId = paramId.substring(VALUES_PREFIX.length());
                            paramMap.put(paramId, param.getValue());
                        }
                    }
                }
            }

        }
    }

}
