<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.util.Map"%>
<%@page import="org.oscm.integrationtests.mockproduct.operation.OperationRegistry"%>
<%@page import="org.oscm.integrationtests.mockproduct.InitServlet"%>
<%@page import="org.oscm.integrationtests.mockproduct.operation.IOperationDescriptor"%>


<%@page import="org.oscm.integrationtests.mockproduct.Quote"%><html xmlns="http://www.w3.org/1999/xhtml">

<%
    final OperationRegistry registry = (OperationRegistry) application
            .getAttribute(InitServlet.OPERATIONREGISTRY);
    IOperationDescriptor<?> operation = registry.getOperation(request
            .getParameter("operation"));
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="images/default.css" type="text/css" />
<title>Mock Product - <%=operation.getName()%></title>
</head>

<body>
  <div class="requestform">
    <h1><%=operation.getName()%></h1>
    <% if (operation.getComment() != null) { %>
    <p><%=operation.getComment()%></p>
    <% } %>
    <form action="execute#end" method="post" target="requests">
      <input type="hidden" name="operation" value="<%=operation.getName()%>" />
      <table class="params">
        <thead>
          <tr>
            <td>Parameter</td>
            <td>Value</td>
          </tr>
        </thead>
        <tbody>
          <%
              for (String param : operation.getParameters()) {
                  final String fieldName = "param_" + param;
                  String preset = request.getParameter(fieldName);
                  if (preset == null) {
                      preset = "";
                  }
          %>
          <tr>
            <td><%=param%></td>
            <td><input type="text" name="<%=fieldName%>" value="<%=Quote.html(preset)%>" /></td>
          </tr>
          <%
              }
          %>
        </tbody>
      </table>
      <input type="submit" value="Execute" />
    </form>
  </div>
</body>

</html>