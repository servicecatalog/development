<?php

require_once 'OperationDescription.php';

class OperationRegistry {

    function get( $name ) {
        $op = new OperationDescription ("AccountService", "getOrganizationData", array());
        if ($name == $op->name) {
            return $op;
        }
        
        $op = new OperationDescription ("EventService", "recordEventForSubscription", 
            array("subscriptionKey", "eventId", "occurrenceTime", "actor", "multiplier"));
        if ($name == $op->name) {
            return $op;
        }

        $op = new OperationDescription ("SessionService", "resolveUserToken", 
            array("subscriptionKey", "sessionId", "userToken"));
        if ($name == $op->name) {
            return $op;
        }

        $op = new OperationDescription ("SessionService", "deleteServiceSession", 
            array("subscriptionKey", "sessionId"));
        if ($name == $op->name) {
            return $op;
        }

    }

}

?>