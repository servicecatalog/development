<%@ page import="javax.faces.application.ViewExpiredException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page isErrorPage="true"%>
<%
    // Reload if session has timed out
    Throwable t = exception.getCause();
    if (t != null && t instanceof ViewExpiredException) {
        response.sendRedirect("default.jsf");
        return;
    }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>APP VMware Controller Konfiguration</title>
<link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>

    <h1>APP VMware Controller Konfiguration</h1>
    <span class="statusPanelClass"> The following error occured during the last operation:
        <p>
            Statuscode:
            <%=pageContext.getErrorData().getStatusCode()%><br /> Request-URI:
            <%=pageContext.getErrorData().getRequestURI()%><br /> Servletname:
            <%=pageContext.getErrorData().getServletName()%><br /> Exception:
            <%=exception%>
        </p>
    </span>
</body>
</html>
