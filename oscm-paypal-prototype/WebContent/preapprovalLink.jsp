<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset= iso-8859-1">
        <title>Preapproval link to Paypal page</title>
	</head>

	<body>
        <div style="font-size:70%; font-family:Verdana; background-color:lightgrey; padding:20px; border:thin solid grey; margin:25px;">
          ${paypalRequest}
        </div>
        <div style="font-size:70%; font-family:Verdana; background-color:#${backgroundResponse}; padding:20px; border:thin solid grey; margin:25px;">
		  ${paypalResponse}
        </div>
        <% if (request.getSession().getAttribute("preapprovalKey") != null) { %>
		  <a href="https://www.sandbox.paypal.com/webscr?cmd=_ap-preapproval&preapprovalkey=${preapprovalKey}" target="_blank">In PayPal best&auml;tigen</a>
        <% } %>
	</body>
</html>
