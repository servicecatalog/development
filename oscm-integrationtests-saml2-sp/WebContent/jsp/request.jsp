<%@page import="org.oscm.saml2.sp.AuthnRequestConverter"%>
<%@page import="org.oscm.saml2.sp.RedirectURLBuilder"%>
<%@page import="org.oscm.saml2.api.*"%>
<%@ page import="org.oscm.saml2.api.model.protocol.AuthnRequestType"%>
<%@ page import="org.apache.commons.codec.binary.Base64"%>
<%@ page import="javax.xml.bind.JAXBElement"%>
<%@ page import="org.oscm.converter.XMLConverter"%>
<%@ page import="java.net.*"%>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link href="../css/style.css" type="text/css" rel="stylesheet" />
<title>Service Provider SAML 2.0 POST Authentication Request</title>
</head>
<body>
  <h1 style="margin-bottom: 6px">Mock Service Provider submits a SAML 2.0 (Single Sign-On) authentication request
    to an Identity Provider</h1>

  <div style="padding: 6px 0px; border-top: solid 1px #3366cc; border-bottom: solid 1px #3366cc">
    <b>Step 1: Generating a SAML 2.0 Authentication Request</b>
  </div>
  <form name="ServiceProviderForm" action="request.jsp" method="post">
    <table>
      <tr>
        <td valign="top"><b>IdP POST URL</b></td>
        <td><input id="IDP_POST_URL" name="IDP_POST_URL" size="80"
          value="http://estbesdev2.lan.est.fujitsu.de:9080/openam/SSOPOST/metaAlias/idp"> <br> <small>The
            POST URL of the Identity Provider</small></td>
      </tr>
      <tr>
        <td valign="top"><b>IdP Redirect/GET URL</b></td>
        <td><input id="IDP_REDIR_URL" name="IDP_REDIR_URL" size="80"
          value="http://estbesdev2.lan.est.fujitsu.de:9080/openam/SSORedirect/metaAlias/idp"> <br> <small>The
            HTTP GET URL of the Identity Provider using the SAML Redirect protocol</small></td>
      </tr>
      <tr>
        <td valign="top"><b>Issuer (content string of the element &lt;Issuer&gt;)</b></td>
        <td><input id="issuer" name="issuer" size="80" value=<%=request.getServerName()%>> <br> <small>URL
            of this page</small></td>
      </tr>
      <tr>
        <td valign="top"><b>relayState</b></td>
        <td><input id="relayState" name="relayState" size="80" value=<%=session.getId()%>> <br> <small>Internal
            HTTP session ID</small></td>
      </tr>
    </table>
    <p />
    <input id="generateRequest" type="submit" value="Generate SAML Request">
  </form>


  <%
      String idpPostURL = request.getParameter("IDP_POST_URL");
      if (idpPostURL != null && idpPostURL.trim().length() > 0) {
          String authID = request.getParameter("authID");
          String issuer = request.getParameter("issuer");
          String idpRedirectURL = request.getParameter("IDP_REDIR_URL");
          String relayState = request.getParameter("relayState");

          AuthnRequestGenerator requestGen = new AuthnRequestGenerator(issuer, false);
          JAXBElement<AuthnRequestType> authnRequest = requestGen
                  .generateAuthnRequest();

          RedirectURLBuilder<AuthnRequestType> redirectURLBuilder = new RedirectURLBuilder<AuthnRequestType>();
          URL redirectURL = redirectURLBuilder
                  .addRelayState(relayState.trim())
                  .addSamlRequest(authnRequest)
                  .addRedirectEndpoint(new URL(idpRedirectURL)).getURL();

          RedirectEncoder coding = new RedirectEncoder();
          AuthnRequestConverter converter = new AuthnRequestConverter();
          String s = converter.convertToString(authnRequest);
          s = XMLConverter.removeEOLCharsFromXML(s);
          String samlRequestDEFLATEBase64 = coding
                  .encodeForRedirectBinding(s);
          String samlRequestBase64 = Base64.encodeBase64String(s.getBytes());
          samlRequestBase64 = XMLConverter.removeEOLCharsFromXML(samlRequestBase64);
  %>



  <div style="padding: 6px 0px; border-top: solid 1px #3366cc; border-bottom: solid 1px #3366cc">
    <b>Step 2: Submitting the SAML Request</b>
  </div>

  <p>You can now review the generated SAML request before submitting it to the identity provider. You can choose
    between a HTTP POST or GET submit.</p>

  <div style="margin: 20px 0px 0px 0px"></div>
  <p>
    <b>Generated SAML AuthnRequest</b>
  </p>
  <pre class="codediv">
    <code><%=converter.convertToPrettyEscapedString(authnRequest)%></code>
  </pre>
  <p>
    <b>Generated SAML AuthnRequest in Base64 encoding</b>
  </p>
  <pre class="codediv">
    <code><%=samlRequestBase64%></code>
  </pre>
  <form method="post" action=<%=idpPostURL.trim()%>>
    <input type="hidden" name="SAMLRequest" value=<%=samlRequestBase64%> /> <input type="hidden" name="RelayState"
      value=<%=relayState.trim()%> /> <input type="submit" value="Submit POST Request" />
  </form>

  <div style="margin: 50px 0px 0px 0px"></div>
  <p>
    <b>Generated SAML AuthnRequest encoding: DEFLATE -&gt; Base64 -&gt; URLEncode (UTF-8)</b>
  </p>
  <pre class="codediv">
    <code><%=samlRequestDEFLATEBase64%></code>
  </pre>
  <p>
    <b>Generated redirect URL</b>
  </p>
  <pre class="codediv">
    <code><%=redirectURL.toExternalForm()%></code>
  </pre>
  <p />
  <input id="sendRequest" type="button" value="Submit Redirect Request"
    onclick="javascript: window.location='<%=redirectURL.toExternalForm()%>'" />

  <%
      }
  %>

</body>
</html>