<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html>

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>File Billing Adapter index page</title>
  </head>

  <body>
    <h1>File Billing Adapter</h1>

    <form action="subscriptionpm" method="GET">
      <table cellpadding="10" style="border-collapse: collapse; border-width: 0">
        <tr>
          <td>Subscription ID:</td>
          <td><input type="text" name="subscriptionId" size="25"></td>
        </tr>
        <tr>
          <td>Organization ID:</td>
          <td><input type="text" name="tenantId" size="25"></td>
        </tr>
        <tr>  
          <td><input type="submit" value="Show subscription PM" name="buttonShowSubPm"></td>
          <td><input type="submit" value="Push subscription PM" name="buttonPushSubPm"></td>
        </tr>
      </table>
    </form>
    
    <br/>
    <br/>
    
    <form action="showservicepm" method="GET">
      <table cellpadding="10" style="border-collapse: collapse; border-width: 0">
        <tr>
          <td>Instance type:</td>
          <td><input type="text" name="instanceType" size="15"></td>
          <td>Region:</td>
          <td><input type="text" name="region" size="15"></td>
        </tr>
        <tr>
          <td>OS:</td>
          <td><input type="text" name="os" size="15"></td>
          <td>Locale:</td>
          <td><input type="text" name="locale" size="2"></td>
        </tr>
        <tr>
          <td>Customer ID (optional):</td>
          <td><input type="text" name="customerId" size="15"></td>
          <td><input type="submit" value="Show service price model" name="buttonShowServicePm"></td>
        </tr>
      </table>
    </form>
    
    <br/>
    <br/>
    
    <c:if test="${not empty errorMessage}">
       <p><span style="color: red; font-size: 80%;">
         <c:out value="${errorMessage}"></c:out>
       </span></p>
    </c:if>
    
    <c:if test="${not empty successMessage}">
       <p><span style="color: green; font-size: 80%;">
         <c:out value="${successMessage}"></c:out>
       </span></p>         
    </c:if>        
  </body>

</html>
