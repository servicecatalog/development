<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isErrorPage="true" 
  import="org.oscm.app.ui.common.ExceptionHandler"
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <title></title>
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/error.css"/>
</head>
<body>

<div id="w_004_h_e_g_header">

  <!-- Product Series Logos -->
  <div id="w_004_h_e_logo">

    <div id="w_004_h_e_g_header_r">
      <div id="w_004_h_e_g_header_ie6">
        <!-- Fujitsu logo -->
        <div id="w_004_h_e_logo_r"></div>

      </div>
    </div>
    
  </div>

</div>

<p>
&nbsp;
</p>
<p>
&nbsp;
</p>

<center>
<table cellspacing="0" cellpadding="0" border="0"><tr><td>
<div style="text-align:left;">
  <div class="rich-panel-header"><%=ExceptionHandler.getErrorTitle(request)%></div>
  
  <div style="border: 1px solid grey; width: 300px; height: 80px; padding: 5px 5px 5px 5px;">
    <%=ExceptionHandler.getErrorText(request)%>
  </div>
</div>
</td></tr></table>
</center>

</body>
</html>
