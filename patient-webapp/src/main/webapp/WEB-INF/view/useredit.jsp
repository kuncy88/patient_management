<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script src="<c:url value="/resources/js/useredit.js" />"></script>

<h2>
	<c:choose>
		<c:when test="${not modify}"><spring:message code="user.label.create-new-user"/></c:when>
		<c:otherwise><spring:message code="user.label.modify-user" /></c:otherwise>
	</c:choose>
</h2>

<c:set var="passwordHasBindError">
	<form:errors path="userForm" class="alert alert-danger" element="div" />
</c:set>

<security:authorize access="hasRole('ROLE_PATIENT')" var="hasRolePatient"></security:authorize>

<form:form class="form-horizontal useredit-form" modelAttribute="userForm" method="post">	
	${passwordHasBindError}
	<spring:bind path="username">
	<div class="form-group ${status.error ? 'has-error' : ''}">
    	<form:label class="control-label col-sm-2" path="username"><spring:message code="label.username"/>:</form:label>
    	<div class="col-sm-10">
    		<spring:message code="user.placeholder.username" var="placeholder_username"/>
     		<form:input data-pattern='[".{1,45}"]' class="form-control" path="username" placeholder="${placeholder_username}" />
     		<form:errors path="username" class="label label-danger"/>
    	</div>
	</div>
	</spring:bind>

	<c:if test="${not modify or userForm.id == userId}">
		<spring:bind path="password">
		<div class="form-group ${status.error || not empty passwordHasBindError ? 'has-error' : ''}">
	    	<form:label class="control-label col-sm-2" path="password"><spring:message code="label.password"/>:</form:label>
	    	<div class="col-sm-10">
	    		<spring:message code="user.placeholder.password" var="placeholder_password"/>
	     		<form:password data-pattern='[".{6,}"]' id="password" class="form-control" path="password" placeholder="${placeholder_password}" />
	     		<form:errors path="password" class="label label-danger"/>
	    	</div>
		</div>
		</spring:bind>
		
		<spring:bind path="confirmPassword">
		<div class="form-group ${status.error || not empty passwordHasBindError ? 'has-error' : ''}">
	    	<form:label class="control-label col-sm-2" path="confirmPassword"><spring:message code="label.password-again"/>:</form:label>
	    	<div class="col-sm-10">
	    		<spring:message code="user.placeholder.password2" var="placeholder_password2"/>
	     		<form:password data-pattern='[".{6,}"]' id="password2" class="form-control" path="confirmPassword" placeholder="${placeholder_password2}" />
	     		<form:errors path="confirmPassword" class="label label-danger"/>
	    	</div>
		</div>
		</spring:bind>
	</c:if>
	
	<spring:bind path="email">
	<div class="form-group ${status.error ? 'has-error' : ''}">
    	<form:label class="control-label col-sm-2" path="email"><spring:message code="label.email"/>:</form:label>
    	<div class="col-sm-10">
    		<spring:message code="user.placeholder.email" var="placeholder_email"/>
     		<form:input type="text" data-pattern='["^[_A-Za-z0-9-\\\+]+(\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\\.[A-Za-z0-9]+)*(\\\.[A-Za-z]{2,})$"]' class="form-control" path="email" placeholder="${placeholder_email}" />
     		<form:errors path="email" class="label label-danger"/>
    	</div>
	</div>
	</spring:bind>
	
	<spring:bind path="fullname">
	<div class="form-group">
    	<form:label class="control-label col-sm-2" path="fullname"><spring:message code="label.fullname"/>:</form:label>
    	<div class="col-sm-10">
    		<spring:message code="user.placeholder.fullname" var="placeholder_fullname"/>
     		<form:input type="text" data-pattern='[".{0,255}"]' class="form-control" path="fullname" placeholder="${placeholder_fullname}" />
    	</div>
	</div>	
	</spring:bind>
	
	<c:if test="${modify and userForm.id != userId}">
		<spring:bind path="resetPassword">
		<div class="form-group" title="<spring:message code="user.title.reset_password"/>">
	    	<label class="control-label col-sm-2" for="resetPassword1"><spring:message code="user.label.reset_password"/>:</label>
	    	<div class="col-sm-10">
	     		<div class="checkbox">
	     			<spring:message code="user.placeholder.reset_password" var="reset_password"/>
					<label><form:checkbox path="resetPassword" value="reset" class="chk-reset-password" /><span class="chk-reset-password-span">${reset_password}</span></label>
				</div>
	    	</div>
		</div>	
		</spring:bind>
	</c:if>
	
	<spring:bind path="active">
	<c:choose>
		<c:when test="${not hasRolePatient}">
			<div class="form-group" title="<spring:message code="user.title.active"/>">
		    	<label class="control-label col-sm-2" for="active1"><spring:message code="label.active"/>:</label>
		    	<div class="col-sm-10">
		     		<label class="radio-inline"><form:radiobutton path="active" checked="checked" value="true" /><spring:message code="label.yes"/></label>
		     		<label class="radio-inline"><form:radiobutton path="active" value="false" /><spring:message code="label.no"/></label>
		    	</div>
			</div>
		</c:when>
		<c:otherwise>
			<form:hidden path="active" value="true" />
		</c:otherwise>
	</c:choose>
	</spring:bind>
	
	<spring:bind path="groups">
	<c:choose>
		<c:when test="${not hasRolePatient}">
			<div class="form-group" title="<spring:message code="user.title.group"/>">
		    	<label class="control-label col-sm-2"><spring:message code="user.label.groups"/>:</label>
		    	<div class="col-sm-10">
		     		<div class="checkbox">
						<label><form:checkbox path="groups" value="Admin" /><spring:message code="label.administrator"/></label>
					</div>
					<div class="checkbox">
						<label><form:checkbox path="groups" value="Doctor" class="group-just-one" /><spring:message code="label.doctor"/></label>
					</div>
					<div class="checkbox">
						<label><form:checkbox path="groups" value="Patient" class="group-just-one" /><spring:message code="label.patient"/></label>
					</div>
		    	</div>
			</div>
		</c:when>
		<c:otherwise>
			<form:hidden path="groups" value="Patient" />
		</c:otherwise>
	</c:choose>
	</spring:bind>
	
	<form:hidden path="id" />
	
	<div class="form-group"> 
    	<div class="col-sm-offset-2 col-sm-10">
    		<button type="submit" class="btn btn-success">
    			<span class="glyphicon glyphicon-floppy-saved"></span> <spring:message code="user.label.save-user-data"/>
    		</button>
    	</div>
	</div>
</form:form>