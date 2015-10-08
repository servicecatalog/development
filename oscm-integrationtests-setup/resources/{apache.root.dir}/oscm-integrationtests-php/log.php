<?php
$GLOBALS["g_reqests_log"] = "requests.log";
$GLOBALS["g_nl"] = "\n";

function logRequest($methodname, $inbound=true, $in, $out) {
    $f = fopen($GLOBALS["g_reqests_log"], "a+");

    $queryParam = '';
    foreach($in as $key => $value) {
        $queryParam .= '&' . $key . '=' . urlencode($value); 
    }

    logHeader($f, $methodname, $inbound, $queryParam);
    
    foreach($in as $key => $value) {
        logNameValuePair($f, $key, $value);
    }

    logNameValuePair($f, 'result', $out);
    logFooter($f);
    fclose($f);
}

function logHeader($f, $name, $inbound=true, $queryParam='') {
    fwrite($f, '<div class="logentry">' . $GLOBALS["g_nl"]);
    if ($inbound) {
        fwrite($f, '<h1 class="INBOUND">');
    } else {
        if ($name == 'SessionService.resolveUserToken') {
            fwrite($f, '<div style="float: right;">' . $GLOBALS["g_nl"]);
            fwrite($f, '[' . $GLOBALS["g_nl"]);
            fwrite($f, '<a target="operation" href="operation.php?operation=SessionService.resolveUserToken' . $queryParam . '">retry</a>' . $GLOBALS["g_nl"]);
            fwrite($f, '|' . $GLOBALS["g_nl"]);
            fwrite($f, '<a target="operation" href="operation.php?operation=SessionService.deleteServiceSession' . $queryParam . '">logout</a>' . $GLOBALS["g_nl"]);
            fwrite($f, ']' . $GLOBALS["g_nl"]);
            fwrite($f, '</div>' . $GLOBALS["g_nl"]);
        }
        fwrite($f, '<h1 class="OUTBOUND">');
    }
    fwrite($f, $name);
    fwrite($f, '</h1>' . $GLOBALS["g_nl"]);
    
    fwrite($f, '<table class="params">' . $GLOBALS["g_nl"]);
    fwrite($f, '  <thead><tr><td>Parameter</td><td>Value</td></tr></thead>' . $GLOBALS["g_nl"]);
    fwrite($f, '  <tbody>' . $GLOBALS["g_nl"]);
}

function logFooter($f) {
    fwrite($f, '  </tr>' . $GLOBALS["g_nl"]);
    fwrite($f, '  </tbody>' . $GLOBALS["g_nl"]);
    fwrite($f, '</table>' . $GLOBALS["g_nl"]);
    fwrite($f, '</div>' . $GLOBALS["g_nl"]);
}

function logNameValuePair($f, $name, $value) {
    fwrite($f, '  <tr>' . $GLOBALS["g_nl"]);
    fwrite($f, '  <td>');
    fwrite($f, $name);
    fwrite($f, '  </td>' . $GLOBALS["g_nl"]);
    fwrite($f, '  <td><pre>');
    
    if (!empty($value)) {
        ob_start();
        var_dump($value);
        fwrite($f, ob_get_contents());
        ob_end_clean();
    }
    
    fwrite($f, '  </pre></td>');
    fwrite($f, '  </tr>' . $GLOBALS["g_nl"]);
}

?>