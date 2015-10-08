<?php
	require_once 'bss_soap_client.php';
	if ($_SERVER['REQUEST_METHOD'] == 'POST' ) {
		bss_soap_client::setBaseUrl($_REQUEST['baseUrl']);
		bss_soap_client::setUserName($_REQUEST['username']);
		bss_soap_client::setPassword($_REQUEST['password']);
	} 
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" href="default.css" type="text/css" />
  <title>Mock Product - Settings</title>
</head>

<body>
  <div class="requestform" >
  <h1>Settings</h1>
  
  <form action="settings.php" method="post">
  <table class="params">
    <thead>
      <tr>
        <td>Setting</td>
        <td>Value</td>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Base URL</td>
        <td><input id="id_baseUrl" type="text" name="baseUrl" value="<?php echo bss_soap_client::getBaseUrl();?>"/></td>
      </tr>
      <tr>
        <td>User Name</td>
        <td><input type="text" name="username" value="<?php  echo bss_soap_client::getUsername();?>"/></td>
      </tr>
      <tr>
        <td>Password</td>
        <td><input type="password" name="password" value="<?php echo bss_soap_client::getPassword();?>"/></td>
      </tr>
      <tr>
        <td>Authentication</td>
        <td>
          <select name="authentication">
            <option>BASICAUTH</option>
            <option>CLIENTCERT</option>
          </select>
        </td>
      </tr>
    </tbody>
  </table>
  <input id="btn_save" type="submit" value="Save"/>
  </form>
  </div>
</body>

</html>