<?php
	require_once 'OperationRegistry.php';
	$op = OperationRegistry::get($_REQUEST['operation']);
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" href="default.css" type="text/css" />
  <title>Mock Product - <?php echo $op->name;?></title>
</head>

<body>
  <div class="requestform" >
    <h1><?php echo $op->name;?></h1>

    <form action="execute.php#end" method="post" target="requests">
      <input type="hidden" name="operation" value="<?php echo $op->name;?>"/>
      
      <table class="params">
        <thead>
          <tr>
            <td>Parameter</td>
            <td>Value</td>
          </tr>
        </thead>
        <tbody>
	  		<?php
				foreach($op->params as $param) {
    				$out = '<tr>'
         			. '<td>' . $param . '</td>'
         			. '<td><input type="text" name="' . $param . '" value="' . $_REQUEST[$param] . '"/></td>'
         			. '</tr>';
    				echo $out;
				}
	  		?>
        </tbody>
     </table>
     
     <input type="submit" value="Execute"/>
    </form>
  </div>
</body>
</html>