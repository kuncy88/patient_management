<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

 <div class="login-container">
 	<c:if test="${not empty message}">
		<div class="alert alert-${cls}" role="alert">${message }</div>	
	</c:if>
	<form name="loginForm" action="<c:url value='/j_spring_security_check' />" method="POST" >
			<h1 class="text-center">Patient managment</h1>
			<div class="alert alert-danger form-error"></div>
			<div class="form-group input-group">
				<span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
				<input type="text" name="username" class="form-control" id="userNameInput" 
					placeholder="<spring:message code="label.username"/>"
					data-pattern='[".{1,}"]' />
				<div class="error-container">
					<span class="label label-danger"><spring:message code="error.username"/></span>
				</div>
			</div>
			<div class="form-group input-group ">
				<span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
				<input type="password" name="password" id="passwordInput" class="form-control"
					placeholder="<spring:message code="label.password"/>"
					data-pattern='[".{1,}"]' />
				<div class="error-container">
					<span class="label label-danger"><spring:message code="error.password"/></span>
				</div>
			</div>
			<div class="form-check">
				<input type="checkbox" class="form-check-input" id="remember" />
				<label class="form-check-label" for="remember"><spring:message code="label.remember"/></label>
			</div>
			<div>
				<button type="submit" class="btn btn-primary form-control"><spring:message code="label.login"/></button>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				
				<p>&nbsp;</p>
				<div class="text-right version"><spring:message code="version"/></div>
			</div>
	</form>
	<script type="text/javascript">
		$(document).ready(function(){
			$(".login-container form").formChecker({
				checkInline: false,
				onAfterLoad: function(form){
					form.find(".error-container").hide();
				},
				onSubmit: function(form, success){
					return formRemember(form, success);
				}
			});
			
			var username = $.cookie("username");
			if(typeof username != "undefined"){
				$("#remember").prop("checked", true);
				$("#userNameInput").val(username);
			}
		});
	</script>
</div>
