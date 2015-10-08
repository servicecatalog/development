<?php

class OperationDescription {

    var $name;
    var $service;
    var $method;
    var $params;
    
    function OperationDescription($service, $method, $params) {
        $this->name = $service . "." . $method;
        $this->service = $service;
        $this->method = $method;
        $this->params = $params;
    }

}
?>