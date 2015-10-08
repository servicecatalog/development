<?php
require_once 'lib/nusoap.php';
require_once 'log.php';

// extend the server class with some log
class log_soap_server extends soap_server {
    function invoke_method() {
        parent::invoke_method();
        logRequest('ProvisioningService.' . $this->methodname, true, $this->methodparams, $this->methodreturn);
    }
}

function getBaseResultOk() {
    return setRcOk(array());
}

function setRcOk($result) {
    $result['tns:rc'] = 0;
    $result['tns:desc'] = 'OK';
    return $result;
}

// instantiate server object
$server = new log_soap_server();

// setup wsdl generation, provide operations with input/output parameters
$server->configureWSDL( 'ProvisioningService', 'http://oscm.org/v1.3');
$server->wsdl->addComplexType(
    'ServiceParameter',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'tns:parameterId' => array('name'=>'parameterId','type'=>'xsd:string'),
        'tns:parameterName' => array('name'=>'parameterName','type'=>'tns:LocalizedInfo'),
        'tns:value' => array('name'=>'value','type'=>'xsd:string')
    )
);

$server->wsdl->addComplexType(
    'ServiceParameterArray',
    'complexType',
    'array',
    '',
    'SOAP-ENC:Array',
    array(),
    array( array('ref'=>'SOAP-ENC:arrayType','wsdl:arrayType'=>'tns:ServiceParameter[]')),
    'tns:ServiceParameter'
);

$server->wsdl->addComplexType(
    'InstanceRequest',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'organizationId' => array('name'=>'organizationId','type'=>'xsd:string'),
        'organizationName' => array('name'=>'organizationName','type'=>'xsd:string'),
        'subscriptionId' => array('name'=>'subscriptionId','type'=>'xsd:string'),
        'defaultLocale' => array('name'=>'defaultLocale','type'=>'xsd:string'),
        'loginUrl' => array('name'=>'loginUrl','type'=>'xsd:string'),
        'parameterValue' => array('name'=>'parameterValue','type'=>'tns:ServiceParameterArray')
    )
);

$server->wsdl->addComplexType(
    'User',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'roleIdentifier' => array('name'=>'roleIdentifier','type'=>'xsd:string'),
        'applicationUserId' => array('name'=>'applicationUserId','type'=>'xsd:string'),
        'userId' => array('name'=>'rc','type'=>'xsd:string'),
        'userName' => array('name'=>'rc','type'=>'xsd:string'),
        'email' => array('name'=>'rc','type'=>'xsd:string'),
        'locale' => array('name'=>'locale','type'=>'xsd:string')
    )
);

$server->wsdl->addComplexType(
    'UserArray',
    'complexType',
    'array',
    '',
    'SOAP-ENC:Array',
    array(),
    array( array('ref'=>'SOAP-ENC:arrayType','wsdl:arrayType'=>'tns:User[]')),
    'tns:User'
);

/* add return types */
$server->wsdl->addComplexType(
    'InstanceInfo',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'tns:instanceId' => array('name'=>'instanceId','type'=>'xsd:string'),
        'tns:accessInfo' => array('name'=>'accessInfo','type'=>'xsd:string'),
        'tns:baseUrl' => array('name'=>'baseUrl','type'=>'xsd:string'),
        'tns:loginPath' => array('name'=>'loginPath','type'=>'xsd:string')
    )
);

$server->wsdl->addComplexType(
    'BaseResult',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'tns:rc' => array('name'=>'rc','type'=>'xsd:int'),
        'tns:desc' => array('name'=>'desc','type'=>'xsd:string')
    )
);

$server->wsdl->addComplexType(
    'InstanceResult',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'tns:instance' => array('name'=>'instance','type'=>'tns:InstanceInfo'),
        'tns:rc' => array('name'=>'rc','type'=>'xsd:int'),
        'tns:desc' => array('name'=>'desc','type'=>'xsd:string')
    )
);

$server->wsdl->addComplexType(
    'Users',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'tns:roleIdentifier' => array('name'=>'roleIdentifier','type'=>'xsd:string'),
        'tns:applicationUserId' => array('name'=>'applicationUserId','type'=>'xsd:string'),
        'tns:userId' => array('name'=>'rc','type'=>'xsd:string'),
        'tns:userName' => array('name'=>'rc','type'=>'xsd:string'),
        'tns:email' => array('name'=>'rc','type'=>'xsd:string'),
        'tns:locale' => array('name'=>'locale','type'=>'xsd:string')
    )
);

$server->wsdl->addComplexType(
    'UserResult',
    'complexType',
    'struct',
    'sequence',
    '',
    array(
        'tns:users' => array('name'=>'users','type'=>'tns:Users', 'minOccurs' => '0', 'maxOccurs' => 'unbounded'),
        'tns:rc' => array('name'=>'rc','type'=>'xsd:int'),
        'tns:desc' => array('name'=>'desc','type'=>'xsd:string')
    )
);

/* add instance methods */
$server->register(
    'createInstance',
    array( 'request'=>'tns:InstanceRequest' ),
    array( 'return'=>'tns:InstanceResult'),
    false,
    'urn:createInstance',
    false,
    false,
    'Creates a new Instance' );
function createInstance( $request ){
    global $server;
    $server->debug('createInstance: ' . $request['subscriptionId']);
    // set the return values
    $result = array(
        'tns:instance' => array(
            'tns:instanceId'=>$request['subscriptionId'] ) );
    return setRcOk($result);
}

$server->register(
    'asyncCreateInstance',
    array( 'request'=>'tns:InstanceRequest' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    'urn:asyncCreateInstance',
    false,
    false,
    'Asynchronous request to creation a new Instance' );
function asyncCreateInstance( $request ){
    return getBaseResultOk();
}

$server->register( 'deleteInstance',
    array( 'instanceId'=>'xsd:string' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    'urn:deleteInstance',
    false,
    false,
    'Deletes an Instance');
function deleteInstance( $instanceId ) {
    return getBaseResultOk();
}

$server->register( 'modifyParameterSet',
    array( 'instanceId'=>'xsd:string', 'parameterValues'=>'tns:ServiceParameterArray' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    false,
    false,
    false,
    'Modifies the parameter values of an instance');
function modifyParameterSet( $instanceId, $parameterValues ){
    return getBaseResultOk();
}

$server->register( 'activateInstance',
    array( 'instanceId'=>'xsd:string' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    false,
    false,
    false,
    'Activates an instance');
function activateInstance( $instanceId ) {
    return getBaseResultOk();
}

$server->register( 'deactivateInstance',
    array( 'instanceId'=>'xsd:string' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    'urn:deactivateInstance',
    false,
    false,
    'Deactivates an instance');
function deactivateInstance( $instanceId ) {
    return getBaseResultOk();
}

/* add user methods */
$server->register( 'createUsers',
    array( 'instanceId'=>'xsd:string', 'users'=>'tns:UserArray' ),
    array( 'return'=>'tns:UserResult'),
    false,
    'urn:createUsers',
    false,
    false,
    'Creates new users in the instance');
function createUsers( $instanceId, $users ) {
    if (count($users) > 0 && isset($users['userId'])) {
        $users = array($users);
    }

    // set the return values
    $resultUsers = array();
    foreach($users as $user) {
        $resultUsers[] = array(
            'tns:applicationUserId' => $user['userId'],
            'tns:userId' => $user['userId'],
            'tns:email' => $user['email'],
            'tns:locale' => $user['locale'] );
    }
    $result = array('tns:users' => $resultUsers);
    return setRcOk($result);
}

$server->register( 'deleteUsers',
    array( 'instanceId'=>'xsd:string', 'users'=>'tns:UserArray' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    false,
    false,
    false,
    'Deletes users from the instance');
function deleteUsers( $instanceId, $users ) {
    if (count($users) > 0 && isset($users['userId'])) {
        $users = array($users);
    }
    return getBaseResultOk();
}

$server->register( 'updateUsers',
    array( 'instanceId'=>'xsd:string', 'users'=>'tns:UserArray' ),
    array( 'return'=>'tns:BaseResult'),
    false,
    false,
    false,
    false,
    'Updates users in the instance');
function updateUsers( $instanceId, $users ) {
    if (count($users) > 0 && isset($users['userId'])) {
        $users = array($users);
    }
    return getBaseResultOk();
}

/* add utility methods */
$server->register( 'sendPing',
    array( 'arg'=>'xsd:string' ),
    array( 'return'=>'xsd:string'),
    false,
    false,
    false,
    false,
    'Return the parameter');
function sendPing( $pingText ) {
    return $pingText;
}


/*
 * call the service method to initiate the transaction and to send the response
 */
$HTTP_RAW_POST_DATA = isset($HTTP_RAW_POST_DATA) ? $HTTP_RAW_POST_DATA : '';
$server->service($HTTP_RAW_POST_DATA);
?>
