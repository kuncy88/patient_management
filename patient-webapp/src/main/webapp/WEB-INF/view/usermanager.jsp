<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script src="<c:url value="/resources/js/usermanager.js" />"></script>

<c:set var="next_offset" value="${offset + limit}" />
<c:set var="prev_offset" value="${offset - limit}" />


<c:if test="${not empty message}">
	<div class='alert alert-${cls}'>${message}</div>
</c:if>

<div class="panel panel-default">
	<div class="panel-heading">
		<div class="container-fluid panel-container">
			<div class="col-xs-6 text-left">
				<ul class="pagination panel-pagination usermanager-pagination">
					<li <c:if test="${prev_offset < 0}"> class="disabled"</c:if>>
						<a href="/usermanager?o=${prev_offset}"> &#60;&#60; <spring:message
								code="label.previous" />
					</a>
					</li>
					<li <c:if test="${end}"> class="disabled"</c:if>><a
						href="/usermanager?o=${next_offset}"> <spring:message
								code="label.next" /> &#62;&#62;
					</a></li>
				</ul>
			</div>
			<div class="col-xs-6 text-right">
				<a href="/usermanager/addUser">
					<button type="button" class="btn btn-success">
						<span class="glyphicon glyphicon-pencil"></span> New user
					</button>
				</a>
			</div>

		</div>
	</div>
	<div class="panel-body row">
		<table class="table table-striped table-hover">
			<tr>
				<th><spring:message code="label.fullname" /></th>
				<th><spring:message code="label.username" /></th>
				<th><spring:message code="label.email" /></th>
				<th><spring:message code="label.active" /></th>
				<th><spring:message code="label.registration_time" /></th>
				<th><spring:message code="label.action" /></th>
			</tr>

			<c:forEach items="${userList}" var="user">
				<c:if test="${user.id != userId}">
					<tr <c:if test="${not user.active}">class="opacity-red"</c:if>>
						<td><c:out value="${user.fullname}" /></td>
						<td><c:out value="${user.userName}" /></td>
						<td><c:out value="${user.email}" /></td>
						<td><c:choose>
								<c:when test="${user.active}">
									<spring:message code="label.yes" />
								</c:when>
								<c:otherwise>
									<spring:message code="label.no" />
								</c:otherwise>
							</c:choose></td>
						<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
								value="${user.createDate}" /></td>
						<td>
							<button type="button" class="btn btn-xs btn-primary"
								onclick="location.href='/usermanager/addUser?row=${user.id}'">
								<span class="glyphicon glyphicon-edit"></span>&nbsp;
							</button>
							<button type="button" class="btn btn-xs btn-danger delete-user"
								data-id="<c:out value="${user.id}" />"
								data-dialog="<spring:message code="dialog.text.delete" />">
								<span class="glyphicon glyphicon-trash"></span>&nbsp;
							</button>
						</td>
					</tr>
				</c:if>
			</c:forEach>
		</table>
		<div class="container-fluid">
			<div class="col-xs-6 text-left usermanager-footer-pager">
				<ul class="pagination panel-pagination usermanager-pagination">
					<li <c:if test="${prev_offset < 0}"> class="disabled"</c:if>>
						<a href="/usermanager?o=${prev_offset}"> &#60;&#60; <spring:message
								code="label.previous" />
					</a>
					</li>
					<li <c:if test="${end}"> class="disabled"</c:if>><a
						href="/usermanager?o=${next_offset}"> <spring:message
								code="label.next" /> &#62;&#62;
					</a></li>
				</ul>
			</div>
		</div>
	</div>
</div>