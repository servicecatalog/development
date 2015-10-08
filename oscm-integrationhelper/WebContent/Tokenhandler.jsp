<%@ page import="org.oscm.integrationhelper.BssClient" %>

<!-- This JSP is an example of the integration of a  
     token handler in a custom Web application. 
     The URL pattern, "/Tokenhandler.jsp", must be 
     used as the login path in the technical service 
     definition for the application -->
      
<html>
<head>
<%
BssClient.processHTTPRequest(request, response, application); 
%>
</head>
<body>
Welcome to the Integration Helpers

<a href=""></a>

</body>
</html>
