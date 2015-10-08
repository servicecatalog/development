<?php
require_once 'lib/nusoap.php';

session_start();
class bss_soap_client extends nusoap_client {

    function bss_soap_client($servicename) {
        parent::nusoap_client(bss_soap_client::getBaseUrl() . $servicename . '/v1.3/BASIC?wsdl', true);
        $this->setCredentials(bss_soap_client::getUsername(), bss_soap_client::getPassword(), 'basic', array());
    }
    
    function getBaseUrl() {
        if (isset($_SESSION['baseUrl'])) {
            return $_SESSION['baseUrl'];
        }
        return 'http://' . $_SERVER['SERVER_NAME'] . ':8180/';
    } 
    
    function setBaseUrl($baseUrl) {
        $_SESSION['baseUrl'] = $baseUrl;
    }

    function getUsername() {
        if (isset($_SESSION['username'])) {
            return $_SESSION['username'];
        }
        return '1000';
    } 
    
    function setUsername($username) {
        $_SESSION['username'] = $username;
    }
    
    function getPassword() {
        if (isset($_SESSION['password'])) {
            return $_SESSION['password'];
        }
        return 'admin123';
    } 
    
    function setPassword($password) {
        $_SESSION['password'] = $password;
    }
    
}
?>