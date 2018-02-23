<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title><tiles:getAsString name="title" /></title>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="<c:url value="/resources/css/jquery-ui.css" />" >
<link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />" >
<link rel="stylesheet" href="<c:url value="/resources/css/main.css" />" >
<!-- Optional theme -->
<link rel="stylesheet" href="<c:url value="/resources/css/bootstrap-theme.min.css" />" >

<script src="<c:url value="/resources/js/jquery.min.js" />"></script>
<script src="<c:url value="/resources/js/jquery-ui.min.js" />"></script>

<!-- Latest compiled and minified JavaScript -->
<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>

<script src="<c:url value="/resources/js/formcheck.js" />"></script>
<script src="<c:url value="/resources/js/jquery.cookie.js" />"></script>

</head>
<body>
	<c:if test="${not empty message}">
		<div class="col-sm-6 alert alert-${cls} fade in alert-dismissable pm-main-alert">
			<a href="#" class="close" data-dismiss="alert" aria-label="close" title="close">×</a>
	  		${message }
		</div>
	</c:if>
	
	<tiles:insertAttribute name="navbar" />
	
	<div class="container-fluid body-content">
		<tiles:insertAttribute name="body" />
	</div>
	
	<!--Footer-->
	<div class="navbar-default navbar-fixed-bottom pm-page-footer">
		 <!--Copyright-->
	    <div class="container-fluid">
	        <p class="text-right"><tiles:insertAttribute name="footer" /></p>
		</div>
	</div>
</body>
</html>