<?xml version="1.0" encoding="ISO-8859-1" ?>

<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="org.oscm.saml2.sp.AuthnRequestConverter"%>
<%@page import="org.oscm.converter.XMLConverter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
  <link href="../css/style.css" type="text/css" rel="stylesheet" />
  <title>SAML 2.0 Response from IdP</title>
</head>

<body>
  <%
      String[] relayStates = request.getParameterValues("RelayState");
      String[] samlResponses = request.getParameterValues("SAMLResponse");

      byte[] samlResponseByte = Base64.decodeBase64(samlResponses[0]);
      String samlResponse = new String(samlResponseByte, "UTF-8");

      AuthnRequestConverter requestGen = new AuthnRequestConverter();
      samlResponse = XMLConverter.removeEOLCharsFromXML(samlResponse);
      String samlResponsePretty = requestGen
              .convertToPrettyEscapedString(samlResponse);
  %>

  <p><a href="request.jsp">Back to mock</a></p>
  
  <p style="margin: 30px 0px 0px 0px"><b>Referer URL</b></p>
  <pre class="codediv"><code><%=request.getHeader("referer")%></code></pre>

  <p style="margin: 30px 0px 0px 0px"><b>RelayState</b></p>
  <pre class="codediv"><code><%=relayStates[0]%></code></pre>
  
  <p style="margin: 30px 0px 0px 0px"><b>SAMLResponse</b></p>
  <pre class="codediv"><code><%=samlResponsePretty%></code></pre>

</body>
</html>