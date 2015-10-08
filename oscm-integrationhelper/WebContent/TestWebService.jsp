<%@ page import="org.oscm.integrationhelper.BssClient" %>

<!-- This JSP is testing the web service call to the Open Service Catalog Manager -->
      
<html>
<head>
<title>Test web service call to Catalog Manager</title>
</head>
<body>
<% 
    if (null != request.getParameter("self")) {
        BssClient.testWebServiceCall(request, response, application); 
    }
%>
    <form name="form" action="TestWebService.jsp" method="post">
        <input type="hidden" name="self" value="self" />
        Test web service call to Catalog Manager&nbsp;&nbsp;<input type="submit" value="Submit"/>
    </form>
</body>
</html>
