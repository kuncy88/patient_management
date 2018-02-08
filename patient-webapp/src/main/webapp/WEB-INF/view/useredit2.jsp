<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value="/resources/js/useredit.js" />"></script>

<c:if test="${not empty message}">
	<div class='alert alert-${cls}'>${message}</div>
</c:if>

<h4 class="alert alert-info" role="alert"><spring:message code="user.label.create-new-user"/></h4>
<form class="form-horizontal useredit-form" action="/saveUser" method="POST">	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="username"><spring:message code="label.username"/>:</label>
    	<div class="col-sm-10">
     		<input data-pattern='[".{1,}"]' type="text" class="form-control" id="username" name="username" placeholder="<spring:message code="user.placeholder.username"/>">
    		<div class="error-container">
				<span class="label label-danger">Please, add correct the username</span>
			</div>
    	</div>
	</div>

	<div class="form-group">
    	<label class="control-label col-sm-2" for="password"><spring:message code="label.password"/>:</label>
    	<div class="col-sm-10">
     		<input data-pattern='[".{1,}"]' type="password" class="form-control" id="password" name="password" placeholder="<spring:message code="user.placeholder.password"/>">
    	</div>
	</div>
	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="password2"><spring:message code="label.password-again"/>:</label>
    	<div class="col-sm-10">
     		<input data-pattern='[".{1,}"]' type="password" class="form-control" id="password2" name="password2" placeholder="<spring:message code="user.palceholder.password2"/>">
    		<div class="error-container">
				<span class="label label-danger"><spring:message code="user.error.password"/></span>
			</div>
    	</div>
	</div>
	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="fullname"><spring:message code="label.fullname"/>:</label>
    	<div class="col-sm-10">
     		<input type="text" class="form-control" id="fullname" name="fullname" placeholder="<spring:message code="user.placeholder.fullname"/>">
    	</div>
	</div>	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="email"><spring:message code="label.email"/>:</label>
    	<div class="col-sm-10">
     		<input type="email" class="form-control" id="email" name="email" placeholder="<spring:message code="user.placeholder.email"/>">
    	</div>
	</div>
	
	<div class="form-group" title="<spring:message code="user.title.active"/>">
    	<label class="control-label col-sm-2" for="active_yes"><spring:message code="label.active"/>:</label>
    	<div class="col-sm-10">
     		<label class="radio-inline"><input type="radio" id="active_yes" name="active" checked value="yes"><spring:message code="label.yes"/></label>
     		<label class="radio-inline"><input type="radio" name="active" value="no"><spring:message code="label.no"/></label>
    	</div>
	</div>
	
	<div class="form-group" title="<spring:message code="user.title.group"/>">
    	<label class="control-label col-sm-2"><spring:message code="user.label.groups"/>:</label>
    	<div class="col-sm-10">
     		<div class="checkbox">
				<label><input type="checkbox" name="groups[]" value="Admin"><spring:message code="label.administrator"/></label>
			</div>
			<div class="checkbox">
				<label><input type="checkbox" name="groups[]" value="Doctor"><spring:message code="label.doctor"/></label>
			</div>
			<div class="checkbox">
				<label><input type="checkbox" name="groups[]" value="Patient"><spring:message code="label.patient"/></label>
			</div>
    	</div>
	</div>
	<div class="form-group"> 
    	<div class="col-sm-offset-2 col-sm-10">
    		<button type="submit" class="btn btn-success">
    			<span class="glyphicon glyphicon-floppy-saved"></span> <spring:message code="user.label.save-user-data"/>
    		</button>
    	</div>
	</div>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>