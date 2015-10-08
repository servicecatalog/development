<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="images/default.css" type="text/css" />
<title>Mock Product - Settings</title>
</head>

<body>
  <div class="requestform" >
  <h1>Clear request log</h1>
  <form action="requestlog.jsp" target="requests">
  <input type="hidden" name="clear" value="true"/>
  <input type="submit" name="submit" value="Clear"/>
  </form>
  </div>
</body>

</html>