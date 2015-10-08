<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<% final ConnectionInfo connection = ConnectionInfo.get(request);
%>


<%@page import="org.oscm.integrationtests.mockproduct.Quote"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="images/default.css" type="text/css" />
<title>Mock Product - Settings</title>
</head>

<body>
  <div class="requestform" >
  <h1>Settings</h1>
  <form action="savesettings" method="post">
  <table class="params">
    <thead>
      <tr>
        <td>Setting</td>
        <td>Value</td>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Base URL</td>
        <td><input type="text" name="baseUrl" value="<%= Quote.html(connection.getBaseUrl()) %>"/></td>
      </tr>
      <tr>
        <td>User Name</td>
        <td><input type="text" name="username" value="<%= Quote.html(connection.getUsername()) %>"/></td>
      </tr>
      <tr>
        <td>Password</td>
        <td><input type="password" name="password" value="<%= Quote.html(connection.getPassword()) %>"/></td>
      </tr>
      <tr>
        <td>Authentication</td>
        <td>
          <select name="authentication">
            <option <%= connection.isClientCert() ? "" : "selected='selected'" %>>BASICAUTH</option>
            <option <%= connection.isClientCert() ? "selected='selected'" : "" %>>CLIENTCERT</option>
          </select>
        </td>
      </tr>
    </tbody>
  </table>
  <input type="submit" value="Save"/>
  </form>
  </div>
</body>


<%@page import="org.oscm.integrationtests.mockproduct.ConnectionInfo"%></html>