<%@ page import="org.oscm.integrationhelper.BssClient" %>
<%@ page import="org.oscm.integrationhelper.Constants" %>
<html>
<head>
</head>
<body>

<h1>Welcome to the Integration Helpers</h1>

<p>&nbsp;</p>
<p>
Hello <%= session.getAttribute(Constants.USER_ID) %>,
</p>

<p>
Welcome to the Integration Helpers.
</p>

<p>
<a href="./Logout.jsp">Logout</a>
</p>

</body>
</html>
