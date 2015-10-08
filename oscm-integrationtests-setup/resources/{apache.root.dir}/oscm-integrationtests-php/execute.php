<?php 
require_once 'bss_soap_client.php';
require_once 'log.php';
require_once 'OperationRegistry.php';

// retrieve the operation
$op = OperationRegistry::get($_REQUEST['operation']);

// create and initialize operation parameters
$params = array();
if ($op->service == 'EventService') {
    $params = '<ns2:recordEventForSubscription xmlns:ns2="http://oscm.org/v1.3">'
            . '<subscriptionKey>' . $_REQUEST['subscriptionKey'] . '</subscriptionKey>'
            . '<event>'
            . '<ns2:actor>' . $_REQUEST['actor'] . '</ns2:actor>'
            . '<ns2:eventId>' . $_REQUEST['eventId'] . '</ns2:eventId>'
            . '<ns2:multiplier>' . $_REQUEST['multiplier'] . '</ns2:multiplier>'
            . '<ns2:occurrenceTime>' . $_REQUEST['occurrenceTime'] . '</ns2:occurrenceTime>'
            . '</event>'
            . '</ns2:recordEventForSubscription>';
} else if ($op->service == 'SessionService') {
    $params = array('subscriptionKey' => $_REQUEST['subscriptionKey'], 'sessionId' => $_REQUEST['sessionId']);
    if ($op->method == 'resolveUserToken') {
        $params['userToken'] = $_REQUEST['userToken'];
    }
}

// create soap client and execute request
$client = new bss_soap_client($op->service);
$result = $client->call($op->method, $params);
if ($client->getError()) {
    logRequest($op->name, false, $params, $client->getError());
} else {
    if (is_string($params)) {
        $params = array(str_replace('&gt;&lt;', '&gt;<br/>&lt;', htmlspecialchars($params)));
    }
    logRequest($op->name, false, $params, $result['return']);
}

include 'requests.php'; 
?>