<%@ page import="org.oscm.saml.sp.HtmlEncoder" %>
<%@ page import="java.net.*" %>

<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <link href="../css/style.css" type="text/css" rel="stylesheet"/>
  <title>BES SAML request</title>
</head>
<body>
  <h1 style="margin-bottom:6px">Mock Service Provider submits a SAML-based (Single Sign-On) authentication statement assert request to the BES Identity Provider</h1>
  <p><div style="padding:6px 0px;border-top:solid 1px #3366cc;border-bottom:solid 1px #3366cc"><b>Step 1: Mock Service Provider generates SAML Request</b></div></p>
  <p>When an unauthenticated user tries to reach a hosted service, such as Mock, Mock will send a SAML request to the partner, which acts as the identity provider in the SAML transaction. In this step, you can click the <b>Generate SAML Request</b> button, prompting Mock to create the SAML request. The request contains the following variables:</p>

  <form name="ServiceProviderForm" action="request.jsp" method="post">
  <table cellpadding="3">
    <tr>
      <td valign="top"><b>IDP_URL</b></td><td><input id="IDP_URL" name="IDP_URL" size="80" value="http://localhost:8180/oscm-portal/saml/identityProvider.jsf">
        <br><small>(The URL of the Identity Provider)</small></td>
    </tr><tr>
      <td valign="top"><b>ACS_URL</b></td><td><input id="ACS_URL" name="ACS_URL" size="80" value="http://localhost:8680/oscm-integrationtests-saml-sp/jsp/response.jsp">
        <br><small>(The AC Service URL at the Service Provider, to redirect the SAML response to)</small></td>
    </tr><tr>
      <td valign="top"><b>TARGET</b></td><td><input id="TARGET" name="TARGET" size="80" value="http://barzu.kumoki.info/olc/">
        <br><small>(The URL of the resource being accessed in the Service Provider)</small></td>
    </tr><tr>
      <td valign="top"><b>authID</b></td><td><input id="authID" name="authID" size="80" value="4040406c-1530-11e0-e869-0110283fdfd0">
        <br><small>(A 160-bit string containing randomly generated characters)</small></td>
    </tr>
  </table>
  <p><center><input id="generateRequest" type="submit" value="Generate SAML Request"></center></form>
  
  
  <% 
    String idpURL = request.getParameter("IDP_URL");
    if(idpURL != null && idpURL.trim().length() > 0) {
    
      String acs = request.getParameter("ACS_URL");
      String acs_URLencoded = URLEncoder.encode(acs, "UTF-8");
      
      String target = request.getParameter("TARGET");
      String target_URLencoded = URLEncoder.encode(target, "UTF-8");
      
      String authID = request.getParameter("authID");
      String authID_URLencoded = URLEncoder.encode(authID, "UTF-8");
      
      String parameterSeparator = idpURL.contains("?") ? "&" : "?";
      String redirectURL = idpURL + parameterSeparator+"ACS="+acs_URLencoded + "&"+"TARGET="+target_URLencoded + "&"+"authID="+authID_URLencoded;
      String redirectURL_HTMLencoded = HtmlEncoder.htmlEncode(redirectURL);
      String redirectURL_notURLencoded = HtmlEncoder.htmlEncode(idpURL + parameterSeparator+"ACS="+acs + "&"+"TARGET="+target + "&"+"authID="+authID);
   %>
   
      <p><div style="padding:6px 0px;border-top:solid 1px #3366cc;border-bottom:solid 1px #3366cc"><b>Step 2: Submitting the SAML Request</b></div></p>
      <p>You can now review the generated SAML request before submitting it to the identity provider.</p>
      <p><b>Generated SAML Request URL</b></p>
      
      <div id="GeneratedSamlRequestUrl" class="codediv"><%=redirectURL_HTMLencoded%></div><br>
      <small>( <b>Not URL-encoded:</b> <%=redirectURL_notURLencoded%> )</small>
      
      <p><center><input id="sendRequest" type="button" value="Send SAML Request" onclick="javascript: window.location='<%=redirectURL%>'"/></center></p>
      
  <%
    }
  %>

</body>
</html>