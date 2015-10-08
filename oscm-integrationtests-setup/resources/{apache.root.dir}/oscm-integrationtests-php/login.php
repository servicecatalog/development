<?php 
require_once 'bss_soap_client.php';
require_once 'log.php';

$service = "SessionService";
$method = "resolveUserToken";
$params = array('subscriptionKey' => $_REQUEST['subKey'], 'sessionId' => $_REQUEST['bssId'], 'userToken' => $_REQUEST['usertoken']);

$client = new bss_soap_client($service);
$result = $client->call($method, $params); 
if ($client->getError()) {
    logRequest("$service.$method", false, $params, $client->getError());
} else {
    logRequest("$service.$method", false, $params, $result["return"]);
}

include "index.php"; 
?>