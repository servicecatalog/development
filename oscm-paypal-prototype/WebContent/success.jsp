<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
	<head>
	</head>
	
	<body>
      cool, alles roger :)<br />
      Transaktion durchführen:<br />
      <form action="Pay" method="post">
          <p>
            <input name="preapprovalKey" type="text" value="${preapprovalKey}" />
            <p>Email:<br><input name="email" type="text" size="30" maxlength="40" value="seller_1310720588_biz@est.fujitsu.com" /></p>
            <p>Amount:<br><input name="amount" type="text" size="30" maxlength="40" /></p>
            <input name="getMoney" type="submit" value="Pay" />
          </p>
      </form>
      <br />
      <form action="PreapprovalRequest" method="post">
            <input name="preapprovalKey" type="hidden" value="${preapprovalKey}" />
        <input name="Cancel Preapproval" type="submit" value="Cancel Preapproval" />
      </form>
	</body>
</html>
