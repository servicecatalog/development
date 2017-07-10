/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 30.06.17 10:22
 *
 ******************************************************************************/

package org.oscm.kafka.service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;

import org.oscm.kafka.common.PublishingResult;

//@Local
public interface KafkaProducerService {

//    @RolesAllowed({ "ORGANIZATION_ADMIN", "SUBSCRIPTION_MANAGER",
//            "UNIT_ADMINISTRATOR" })
//    PublishingResult publishSubscription(String subscriptionJSON);
}
