<%@ page import="org.oscm.integrationhelper.BssClient" %>
<%
response.sendRedirect(BssClient.logoutUser(request.getSession()));
%>