<%@ page import="org.oscm.saml.api.SamlDecoder" %>
<%@ page import="org.oscm.saml.api.AuthenticationStatement" %>
<%@ page import="org.oscm.saml.api.Assertion" %>
<%@ page import="org.oscm.saml.sp.ResponseValidator" %>
<%@ page import="org.oscm.saml.sp.HtmlEncoder" %>
<%@ page import="org.oscm.saml.sp.HtmlFormatter" %>
<%@ page import="org.oscm.saml.api.Response" %>
<%@ page import="org.oscm.saml.api.ResponseParser" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.net.URLEncoder" %>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link href="../css/style.css" type="text/css" rel="stylesheet" />
<title>BES SAML Response</title>
</head>

<body>
<h1>Mock Service Provider</h1>
<%
    String target = request.getParameter("TARGET");
    String samlResponse = request.getParameter("SAMLResponse");
    samlResponse = SamlDecoder.decodeBase64(samlResponse);
    if (samlResponse != null) {
%>
<br/><br/>
<b> Target </b>
<p>
<div class="codediv"><%=HtmlEncoder.htmlEncode(target)%></div>
<br/><br/>

<b> Generated and Signed SAML Response </b>
<p>
<div class="codediv"><%=HtmlFormatter.format(HtmlEncoder.htmlEncode(samlResponse))%></div>

<%
    // Version 1: parsing and unmarshalling with own framework
    ResponseParser parser = new ResponseParser(samlResponse);
    Response samlResponseObject = parser.unmarshall();
    Assertion assertion = samlResponseObject.getAssertions().iterator().next();
    AuthenticationStatement authnStatement = assertion.getAuthenticationStatements().iterator().next();
    boolean hasValidSignature = ResponseValidator.hasValidSignature(samlResponse);
%>
        <p>The SAML response contains the following variables:</p>
        <p>        
        <ul>
          <li>
            <p>
              <b>Has valid signature</b> - <span id="HasValidSignature"><%=hasValidSignature%></span>
                <br><small>(<i>true</i> if the signature was successfully validated using the public key, <i>false</i> otherwise.)</small>
            </p>
          </li>
          <li>
	        <p>
	          <b>ResponseId</b> - <span id="ResponseId"><%=HtmlEncoder.htmlEncode(samlResponseObject.getID())%></span>
              <br><small>(A 160-bit string containing a set of randomly generated characters.)</small>
	        </p>
	      </li>
          <li>
            <p>
              <b>IssueInstant</b> - <span id="IssueInstant"><%=HtmlEncoder.htmlEncode(Response.formatIssueInstant(samlResponseObject.getIssueInstant()))%></span>
              <br><small>(A timestamp indicating the date and time that the SAML response was generated.)</small>
            </p>
          </li>
          <li>
            <p>
              <b>StatusCode</b> - <span id="StatusCode"><%=HtmlEncoder.htmlEncode(samlResponseObject.getStatus().getStatusCode().getValue())%></span>
              <br><small>(A code indicating if the SAML response generation was successful or, if not, the error code.)</small>
            </p>
          </li>
          <li>
            <p>
              <b>AssertionId</b> - <span id="AssertionId"><%=HtmlEncoder.htmlEncode(assertion.getID())%></span>
              <br><small>(A 160-bit string containing a set of randomly generated characters.)</small>
            </p>
          </li>
          <li>
            <p>
              <b>(Assertion) Issuer</b> - <span id="AssertionIssuer"><%=HtmlEncoder.htmlEncode(assertion.getIssuer())%></span>
              <br><small>(The authority issuing the SAML assertion.)</small>
            </p>
          </li>
          <li>
            <p>
              <b>NameIdentifier</b> - <span id="NameIdentifier"><%=HtmlEncoder.htmlEncode(authnStatement.getSubject().getNameIdentifier().getNameIdentifier())%></span>
              <br><small>(The username for the authenticated user.)</small>
            </p>
          </li>
          <li>
            <p>
              <b>AuthenticationMethod</b> - <span id="AuthenticationMethod"><%=HtmlEncoder.htmlEncode(authnStatement.getAuthenticationMethod().getValue())%></span>
              <br><small>(The method used to authenticate the user.)</small> 
            </p>
          </li>
          <li>
            <p>
              <b>AuthenticationInstant</b> - <span id="ResponseId"><%=HtmlEncoder.htmlEncode(Response.formatIssueInstant(authnStatement.getAuthenticationInstant()))%></span>
              <br><small>(A timestamp indicating the date and time that you authenticated the user.)</small> 
            </p>
          </li>
        </ul>
        <p>

    <%
        }
    %>
</body>
</html>
