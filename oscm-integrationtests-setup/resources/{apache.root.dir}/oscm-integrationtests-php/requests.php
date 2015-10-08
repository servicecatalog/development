<?php
	require_once 'log.php'; 
	if(isset($_REQUEST['ac']) && $_REQUEST['ac']=='clear') {
    	$f = fopen($g_reqests_log, "w");
    	fclose($f);
	} 
?>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" href="default.css" type="text/css" />
  <title>Mock Product - Request Log</title>
</head>

<body>
  <div class="requestlog">
	<?php include $g_reqests_log; ?>
  </div>
  <a name="end"></a>
</body>
</html>