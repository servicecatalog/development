<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@page import="java.util.Date"%>
<%@page import="java.net.URLEncoder"%>
<%@ page import="java.io.File"%>
<%@ page import="org.oscm.example.common.Constants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <title><%=Constants.TITLE%></title>
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/header.css"/>
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/common.css">
  
<script type="text/javascript">
function selectAll(flag) {
  var elements = document.getElementsByName("<%=Constants.PARAM_ELEMENT%>");
  for (var i=0; i<elements.length; i++) {
    elements[i].checked = flag;
  }
}
</script>
</head>

<body>


<table cellspacing="0" cellpadding="0" border="0" height="100%" width="100%">
<colgroup>
  <col width="3"/>
  <col/>
  <col width="3"/>
</colgroup>
<tr>
  <td colspan="3">
    <div id="w_004_h_e_g_header">
      <!-- Product Series Logos -->
      <div id="w_004_h_e_logo">
    
        <div id="w_004_h_e_g_header_r">
          <div id="w_004_h_e_g_header_ie6">
            <!-- Fujitsu logo -->
            <div id="w_004_h_e_logo_r"></div>
    
            <!-- username/logout -->
            <div id="w_004_h_e_user">
              <p>
                
                User ID : <span class="w_004_h_e_login_name" id="headerUserId"><%=session.getAttribute(Constants.USER_ID)%></span>
                <a href="example.do?method=logout">Logout</a>
              </p>
            </div>
          </div>
        </div>
        
      </div>
    </div>
</td>
</tr>

<tr>
  <td class="mainBg" style="height:3px;" colspan="3">
  </td>
</tr>
<tr>
<td class="mainBg"height="100%" ></td>
<td valign="top" style="border:1px solid black; padding:4px;">

<b>You can create folders, upload files and share them with other users.</b><br/><br/>

<b>Current Dir: /<c:out value="${path}"/></b><br/>

<c:forEach var="it" items="${error_list}">
<p class="example_error"><c:out value="${it}"/>
</p>
</c:forEach>

<form action="example.do" enctype="multipart/form-data" method="post">
<table border="1" width="100%" cellspacing="0" cellpadding="2">
  <tr>
    <td width="40" class="example_th">
      &nbsp;
    </td>
    <td width="*" class="example_th">
      Name
    </td>
    <td align="right" width="100" class="example_th">
      Size&nbsp;
    </td>
    <td width="100" class="example_th">
      Type
    </td>
    <td width="200" class="example_th">
      Date
    </td>
  </tr>
<c:if test="${not empty parent_path or parent_path eq ''}">
  <tr>
    <td>
      &nbsp;
    </td>
    <td>
      <a href="example.do?path=<c:out value="${parent_path}"/>">[..]</a>
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp;
    </td>
  </tr>
</c:if>
  
<c:forEach var="it" items="${dir_elements}">
<%
  File it = (File) pageContext.getAttribute("it");
  pageContext.setAttribute("lastModified", new Date(it.lastModified()));
  String subdir = (String)request.getAttribute("path");
  if ( subdir.length() > 0 ) {
    subdir += "/";
  }
  subdir += it.getName();
  pageContext.setAttribute("subdir", URLEncoder.encode(subdir, Constants.URL_ENCODING));
%>
  <c:if test="${it.directory}">
  <tr>
    <td align="center">
      <input type="checkbox" name="<%=Constants.PARAM_ELEMENT%>" value="<c:out value="${it.name}"/>"/>
    </td>
    <td>
      <a href="example.do?path=<c:out value="${subdir}"/>">[<c:out value="${it.name}"/>]</a>
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      DIR
    </td>
    <td>
      <fmt:formatDate value="${lastModified}" pattern="MMM dd, yyyy h:mm:ss a"/>
    </td>
  </tr>
  </c:if>
</c:forEach>
<c:forEach var="it" items="${dir_elements}">
<%
  java.io.File it =(java.io.File) pageContext.getAttribute("it");
  pageContext.setAttribute("length", Long.valueOf(it.length()));
  pageContext.setAttribute("lastModified", new Date(it.lastModified()));
%>
  <c:if test="${it.file}">
  <tr>
    <td align="center">
      <input type="checkbox" name="<%=Constants.PARAM_ELEMENT%>" value="<c:out value="${it.name}"/>"/>
    </td>
    <td>
      <a href="example.do?method=Get&<%=Constants.PARAM_ELEMENT%>=<c:out value="${it.name}"/>" target="_blank"><c:out value="${it.name}"/></a>
    </td>
    <td align="right">
      <fmt:formatNumber value="${length}" pattern="###,###"/>&nbsp;
    </td>
    <td>
      FILE
    </td>
    <td>
      <fmt:formatDate value="${lastModified}" pattern="MMM dd, yyyy h:mm:ss a"/>
    </td>
  </tr>
  </c:if>
</c:forEach>

</table>

<input type="checkbox" name="all" onClick="selectAll(this.checked)"/> Select all<br/>
<br/>

<input type="hidden" name="<%=Constants.PARAM_PATH%>" value="<c:out value="${path}"/>"/>
<input type="text" name="<%=Constants.PARAM_NAME%>" value="<c:out value="${name}"/>" style="width:208px;"/>
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_CREATE%>" class="example_button"/>
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_MOVE%>" class="example_button"/>
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_COPY%>" class="example_button"/>
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_RENAME%>" class="example_button"/>
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_DELETE%>" class="example_button"/>
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_ZIP.replaceAll("&","&amp")%>" class="example_button" style="width:139px;"/>
<br/>

<input type="file" name="method" size="90" style="margin-top:2px;" />
<input type="submit" name="<%=Constants.PARAM_METHOD%>" value="<%=Constants.METHOD_UPLOAD%>" class="example_button"/>

</form>

</td>
<td class="mainBg"></td>
</tr></table>

</body>
</html>
	