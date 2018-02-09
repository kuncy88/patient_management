<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value="/resources/js/navbar.js" />"></script>
<nav class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
		        <span class="sr-only">Toggle navigation</span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
	      	</button>
			<a class="navbar-brand" href="/">PM Soft</a>
		</div>
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="/mycalendar"><spring:message code="navbar.menu.mycalendar"/> </a></li>
				<li><a href="/usermanager"><spring:message code="navbar.menu.usermanager"/> </a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
          			<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
          				<span class="glyphicon glyphicon-user"></span> 
          				<spring:message code="navbar.menu.welcome"/> <c:out value="${userFullname}"></c:out>
          				<span class="caret"></span>
          			</a>
		        	<ul class="dropdown-menu">
			            <li><a href="/usermanager/addUser?row=${userId}"><spring:message code="navbar.menu.myaccount"/></a></li>
			            <li><a href="/about"><spring:message code="navbar.menu.about"/></a></li>
			            <li role="separator" class="divider"></li>
			            <li><a href="/logout"><spring:message code="navbar.menu.logout"/></a></li>
		          	</ul>
	      		</li>
			</ul>
		</div> <!-- /.navbar-collapse -->
	</div> <!-- /.container-fluid -->
</nav>