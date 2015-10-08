<html>
<head>
<title>One moment please, you will be automatically redirected ...</title>
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Expires" content="Fri, 05 JUL 2002,05:00:00 GMT" />
</head>
<%
  String location = (String) request.getAttribute(org.oscm.example.common.Constants.ATTRIBUTE_LOCATION);
%>
<body 	bgcolor="#EAEAEA" 
		link="#000000" 
		vlink="#000000" 
		alink="#000000" 
		text="#000000" 
		onLoad="JavaScript: window.status = 'we redirect to the page <%=location%>';
																		return false;" >
<%
response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY);
response.setHeader("Location", request.getContextPath() + location);
%>
<label><font size="1" face="verdana,arial,helvetica" color="red"><strong>One moment please, you will be automatically redirected ...</strong></font></label>
</body>
</html>