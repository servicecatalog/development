<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.oscm.integrationtests.mockproduct.InitServlet"%>
<%@page import="org.oscm.integrationtests.mockproduct.RequestLog"%>
<%@page import="org.oscm.integrationtests.mockproduct.RequestLogEntry"%>
<%@page import="org.oscm.integrationtests.mockproduct.RequestLogEntry.RequestDirection"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Map"%>


<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="org.oscm.integrationtests.mockproduct.QuickLink"%>
<%@page import="org.oscm.integrationtests.mockproduct.Quote"%><html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="images/default.css" type="text/css" />
<title>Mock Product - Request Log</title>
</head>

<body>
  <div class="requestlog">
  <% final RequestLog log = (RequestLog) application.getAttribute(InitServlet.REQUESTLOG);
     if ( Boolean.parseBoolean(request.getParameter("clear")) ) {
         log.clear();
     }
     int index = 1;
     for (RequestLogEntry entry : log.getEntries()) {
  %>
  <div class="logentry" id="logentry:<%= index %>">
    <div class="timestamp">
      <%= entry.getHost() %> - 
      <%= SimpleDateFormat.getDateTimeInstance().format(new Date(entry.getTimestamp())) %>
    </div>
    
    <h1 class="<%= entry.getDirection().name() %>"><%= entry.getTitle() %></h1>
    
    <div style="float: right;">
      <% final List<QuickLink> links = entry.getQuickLinks();
         if (!links.isEmpty()) {
             boolean more = false;
      %>
      [
        <% for (QuickLink link : links) {
             if (more) {
             %>
               |
             <% } %>
             <a id="logentry:<%= index %>:<%= link.getTitle() %>" href="operation.jsp?<%= link.getQueryString() %>" target="operation"><%= link.getTitle() %></a>
             <% 
               more = true;
             %>
        <% }
        %>
      ]
      <% } %>
    </div>
    
    <table class="params" id="params:<%= index %>">
      <thead>
        <tr>
          <td>Parameter</td>
          <td>Value</td>
        </tr>
      </thead>
      <tbody>
        <% 
          int paramIndex=1;
          for (Map.Entry<String, String> parameter : entry.getParameters().entrySet()) { %>
        <tr>
          <td id="params:<%= index %>:key:<%= paramIndex %>"><%= Quote.html(parameter.getKey()) %></td>
          <td id="params:<%= index %>:value:<%= paramIndex %>"><%= Quote.html(parameter.getValue()) %></td>
        </tr>
        <%   paramIndex++;
           } %>
        <tr>
          <td>result</td>
          <td id="result:<%= index %>"><%= Quote.html(entry.getResult()) %></td>
        </tr>
      </tbody>
    </table>
    <% if (entry.getException() != null) { %>
      <pre class="trace" id="error:<%= index %>"><%= Quote.html(entry.getExceptionTrace()) %></pre>
    <% } 
       index++; %>
  </div>
  <% } %>
  
  </div>
  <a name="end"/>
</body>

</html>