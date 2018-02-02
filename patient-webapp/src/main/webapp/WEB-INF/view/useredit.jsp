<h4 class="alert alert-info" role="alert">Create new user</h4>
<form class="form-horizontal" action="">
	<div class="form-group">
    	<label class="control-label col-sm-2" for="fullname">Fullname:</label>
    	<div class="col-sm-10">
     		<input type="text" class="form-control" id="fullname" name="fullname" placeholder="Enter name">
    	</div>
	</div>	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="email">Email:</label>
    	<div class="col-sm-10">
     		<input type="email" class="form-control" id="email" name="email" placeholder="Enter email">
    	</div>
	</div>
	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="password">Password:</label>
    	<div class="col-sm-10">
     		<input type="text" class="form-control" id="password" name="password" placeholder="Enter password">
    	</div>
	</div>
	
	<div class="form-group">
    	<label class="control-label col-sm-2" for="password2">Password again:</label>
    	<div class="col-sm-10">
     		<input type="text" class="form-control" id="password2" name="password2" placeholder="Enter password again">
    	</div>
	</div>
	<div class="form-group">
    	<label class="control-label col-sm-2" for="active_yes">Active:</label>
    	<div class="col-sm-10">
     		<label class="radio-inline"><input type="radio" id="active_yes" name="active" checked>Yes</label>
     		<label class="radio-inline"><input type="radio" name="active">No</label>
    	</div>
	</div>
	
	<div class="form-group">
    	<label class="control-label col-sm-2">User group(s):</label>
    	<div class="col-sm-10">
     		<div class="checkbox">
				<label><input type="checkbox" name="groups[]" value="admin">Administrator</label>
			</div>
			<div class="checkbox">
				<label><input type="checkbox" name="groups[]" value="doctor">Doctor</label>
			</div>
			<div class="checkbox">
				<label><input type="checkbox" name="groups[]" value="patient">Patient</label>
			</div>
    	</div>
	</div>
	<div class="form-group"> 
    	<div class="col-sm-offset-2 col-sm-10">
    		<button type="submit" class="btn btn-success">
    			<span class="glyphicon glyphicon-floppy-saved"></span> Save user data
    		</button>
    	</div>
	</div>
</form>