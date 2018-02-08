<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${not empty message}">
	<div class='alert alert-${cls}'>${message}</div>
</c:if>

<div class="panel panel-default">
	<div class="panel-heading">
		<a href="/usermanager/addUser">
			<button type="button" class="btn btn-success">
				<span class="glyphicon glyphicon-pencil"></span> New user
			</button>
		</a>
	</div>
	<div class="panel-body row">
		<table class="table table-striped table-hover">
			<tr>
				<th>Fullname</th>
				<th>Username</th>
				<th>Email</th>
				<th>Active</th>
				<th>Registration time</th>
				<th>Action</th>
			</tr>
			
			<tr>
				<td>Kis Géza</td>
				<td>kgeza</td>
				<td>kgeza@gmail.com</td>
				<td>Igen</td>
				<td>2013-02-10 12:34:10</td>
				<td>
					<button type="button" class="btn btn-xs btn-primary">
					  	<span class="glyphicon glyphicon-edit"></span>&nbsp;
					</button>
					<button type="button" class="btn btn-xs btn-danger">
					  	<span class="glyphicon glyphicon-trash"></span>&nbsp;
					</button>
				</td>
			</tr>
			
			<tr>
				<td>Nagy Béla</td>
				<td>bnagy</td>
				<td>bnagy@pmsoft.com</td>
				<td>Nem</td>
				<td>2015-12-10 02:30:14</td>
				<td>
					<button type="button" class="btn btn-xs btn-primary">
					  	<span class="glyphicon glyphicon-edit"></span>&nbsp;
					</button>
					<button type="button" class="btn btn-xs btn-danger">
					  	<span class="glyphicon glyphicon-trash"></span>&nbsp;
					</button>
				</td>
			</tr>
			
			<tr>
				<td>John Wick</td>
				<td>wjohn</td>
				<td>wjhon@pmsoft.com</td>
				<td>Igen</td>
				<td>2013-02-10 08:10:23</td>
				<td>
					<button type="button" class="btn btn-xs btn-primary">
					  	<span class="glyphicon glyphicon-edit"></span>&nbsp;
					</button>
					<button type="button" class="btn btn-xs btn-danger">
					  	<span class="glyphicon glyphicon-trash"></span>&nbsp;
					</button>
				</td>
			</tr>
		</table>
		<nav aria-label="Page navigation" class='text-center'>
			<ul class="pagination">
		    	<li>
		      		<a href="#" aria-label="Previous">
		        		<span aria-hidden="true">&laquo;</span>
		      		</a>
		    	</li>
		    	<li><a href="#">1</a></li>
		    	<li><a href="#">2</a></li>
		    	<li><a href="#">3</a></li>
		    	<li><a href="#">4</a></li>
		    	<li><a href="#">5</a></li>
		    	<li>
		      		<a href="#" aria-label="Next">
		        		<span aria-hidden="true">&raquo;</span>
		      		</a>
		    	</li>
			</ul>
		</nav>
	</div>
</div>
