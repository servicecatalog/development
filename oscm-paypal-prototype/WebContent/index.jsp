<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset= iso-8859-1">
        <title>Paypal Prototype </title>
	</head>
	<body>
      <div style="font-size:70%; font-family:Verdana; background-color:lightgrey; padding:20px; border:thin solid grey; margin:25px;">
        ${paypalRequest}
      </div>
      <div style="font-size:70%; font-family:Verdana; background-color:#${backgroundResponse}; padding:20px; border:thin solid grey; margin:25px;">
        ${paypalResponse}
      </div>
	  <form action="PreapprovalRequest" method="post">
		<p>
			<input name="SendPreapprovalRequest" type="submit" value="PreapprovalRequest" />
		</p>
      </form>
	</body>
</html>
