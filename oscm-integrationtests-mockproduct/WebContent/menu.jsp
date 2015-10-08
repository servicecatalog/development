<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.util.Map"%>
<%@page import="org.oscm.integrationtests.mockproduct.operation.OperationRegistry"%>
<%@page import="org.oscm.integrationtests.mockproduct.InitServlet"%>
<%@page import="org.oscm.integrationtests.mockproduct.ConnectionInfo"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="images/default.css" type="text/css" />
<title>Mock Product - Menu</title>
</head>

<body>
  <div class="oplist">
  <h1>Tools</h1>
  <ul>
    <li><a href="requestlog.jsp#end" target="requests">Refresh</a></li>
    <li><a href="clear.jsp" target="operation">Clear</a></li>
    <li><a href="settings.jsp" target="operation">Edit Settings</a></li>
  </ul>
  <h1>Web Service Operations</h1>
  <ul>
  <% final OperationRegistry registry = (OperationRegistry) application.getAttribute(InitServlet.OPERATIONREGISTRY);
     for (String name : registry.getOperations()) {
  %>
    <li><a href="operation.jsp?operation=<%= name %>" target="operation"><%= name %></a></li>
  <% } %>
  </ul>
  </div>
</body>

</html>