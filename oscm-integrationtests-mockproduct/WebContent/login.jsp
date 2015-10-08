<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="java.util.*"%>
<%@ page import="org.oscm.integrationtests.mockproduct.*"%>
<%@ page import="org.oscm.integrationtests.mockproduct.operation.*"%>
<%@ page import="org.oscm.integrationtests.mockproduct.RequestLogEntry.RequestDirection"%>
<%@ page import="org.oscm.intf.SessionService"%>

<%
    // perfrom the resolveUserToken() and the deleteSession() call
    
    application.getAttribute("InitServlet.REQUESTLOG");
    final RequestLog log = (RequestLog) getServletContext()
            .getAttribute(InitServlet.REQUESTLOG);
    RequestLogEntry entry = log.createEntry("HTTPRequest.login",
            RequestDirection.INBOUND);
    entry.setHost(request.getRemoteHost());

    final Enumeration<?> names = request.getParameterNames();
    final Map<String, String> params = new HashMap<String, String>();
    while (names.hasMoreElements()) {
        final String name = (String) names.nextElement();
        entry.addParameter(name, request.getParameter(name));
        params.put(name, request.getParameter(name));
    }

    final ConnectionInfo connection = ConnectionInfo.get(request);

    final IOperationDescriptor<SessionService> resolveUserToken = new SessionService_resolveUserToken();
    entry = log.createEntry(resolveUserToken.getName(),
            RequestDirection.OUTBOUND);
    entry.setHost(connection.getBaseUrl());
    for (String key : new String[] { "bssId", "subKey", "usertoken" }) {
        entry.addParameter(key, params.get(key));
    }

    try {
        final SessionService service = PortFactory.getPort(
                connection, resolveUserToken.getServiceType());
        final String userId = service.resolveUserToken(Long.parseLong(request
                .getParameter("subKey")), request.getParameter("bssId"), params.get("usertoken"));
        session.setAttribute("userId", userId);
        entry.setResult(userId);
    } catch (Exception e) {
        entry.setException(e);
    }

    final IOperationDescriptor<SessionService> deleteSession = new SessionService_deleteServiceSession();
    entry = log.createEntry(deleteSession.getName(),
            RequestDirection.OUTBOUND);
    entry.setHost(connection.getBaseUrl());
    for (String key : new String[] { "subKey", "bssId" }) {
        entry.addParameter(key, params.get(key));
    }

    try {
        final SessionService service = PortFactory.getPort(
                connection, deleteSession.getServiceType());
        service.deleteServiceSession(Long.parseLong(request.getParameter("subKey")),
                request.getParameter("bssId"));
    } catch (Exception e) {
        entry.setException(e);
    }
%>


<html>
<head>
</head>
<body>

<table width="100%" cellspacing="0" cellpadding="0" border="0">
  <tr style="background: silver;">
    <td><span style="font-size: larger;">resolveUserToken and deleteSession</span></td>
    <td width="100"><span style="font-weight: bold;"><%=session.getAttribute("userId")%></span></td>
  </tr>
</table>

</body>
</html>
