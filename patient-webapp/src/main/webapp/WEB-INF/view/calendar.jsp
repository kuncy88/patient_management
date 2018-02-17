<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link href='<c:url value="/resources/component/calendar/fullcalendar.min.css" />' rel='stylesheet' />
<link href='<c:url value="/resources/component/bs-datetimepicker/datetimepicker.min.css" />' rel='stylesheet' />

<script src='<c:url value="/resources/component/calendar/lib/moment.min.js" />'></script>
<script src='<c:url value="/resources/component/calendar/fullcalendar.min.js" />'></script>
<script src='<c:url value="/resources/component/bs-datetimepicker/datetimepicker.min.js" />'></script>
<script src='<c:url value="/resources/component/typeahead/typeahead.min.js" />'></script>
<script src="<c:url value="/resources/js/calendar.js" />"></script>

<div id="calendar"></div>

<!-- Modal -->
<div id="reScheduleFormModal" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
    	<div class="modal-content">
    		<div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal">&times;</button>
        		<h4 class="modal-title"><spring:message code="reschedule.form.title" /></h4>
      		</div>
      		<div class="modal-body">
      			<form class="form-horizontal reschedule-form" action="/mycalendar/reSchedule" method="post">
      				<div class='form-group'>
      					<label class="control-label col-sm-2" for="newdate">
							<spring:message code="label.new_date" />
						</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" name="newDate" id="newdate" placeholder="<spring:message code="reschedule.form.placeholder.newdate" />">
					    </div>
      				</div>
      				<input type="hidden" name="oldDate" />
      			</form>
      		</div>
      		<div class="modal-footer">
      			<button type="button" class="btn btn-success submit">
					<span class='glyphicon glyphicon-floppy-saved'>&nbsp;</span><spring:message code="label.save" />
				</button>
        		<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.cancel" />
				</button>
     		</div>
    	</div>
	</div>
</div>

<!-- Modal -->
<div id="calendarFormModal" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
    	<div class="modal-content">
      		<div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal">&times;</button>
        		<h4 class="modal-title"></h4>
      		</div>
      		<div class="modal-body">
        		<form class="form-horizontal appointment-form" action="/mycalendar/saveAppointment" method="post">
        			<div class="error-container form-input-error" id="doctorId_error">
						<spring:message code='appointmentForm.error.doctorId' />
					</div>
        			<div class='form-group'>
        				<label class="control-label col-sm-2" for="patient">
        					<spring:message code="calendar.label.patient" />:
        				</label>
        				<div class="col-sm-10">
      						<div class="input-group">
      							<input class="form-control" id="patient" 
        						placeholder="<spring:message code="calendar.label.patient.placeholder" />" />
      							<span class="input-group-btn">
	        						<button type="button" id="add_new_user" class="btn btn-info" data-href="/usermanager/addUser">
		        						<spring:message code="label.add" />
		        					</button>
	        					</span>
      						</div>
      						<div class="error-container form-input-error" id="patientId_error">
      							<spring:message code='appointmentForm.error.patientId' />
      						</div>
        				</div>
        			</div>
        			
        			<div class='form-group' title="<spring:message code="calendar.label.description.placeholder" />">
        				<label  class="control-label col-sm-2" for="notes">
        					<spring:message code="calendar.label.description" />:
        				</label>
        				<div class="col-sm-10">
	        				<textarea 
	        					class="form-control" 
	        					name="description" 
	        					id="description" 
	        					style="resize:none" 
	        					rows=3 
	        					placeholder="<spring:message code="calendar.label.description.placeholder" />" 
	        				></textarea>
	        				<div class="error-container form-input-error" id="description_error">
	    						<spring:message code='appointmentForm.error.description' />
	    					</div>
        				</div>
        			</div>
        			
        			<div class='form-group' title="<spring:message code="calendar.label.date.notes.placeholder" />">
        				<label  class="control-label col-sm-2" for="notes">
        					<spring:message code="calendar.label.date.notes" />:
        				</label>
        				<div class="col-sm-10">
	        				<textarea 
	        					class="form-control" 
	        					name="notes" 
	        					id="notes" 
	        					style="resize:none" 
	        					rows=2 
	        					placeholder="<spring:message code="calendar.label.date.notes.placeholder" />" 
	        				></textarea>
        				</div>
        			</div>
        			
        			<div class='form-group'>
        				<label class="control-label col-sm-2" for="appointment_start">
        					<spring:message code="calendar.label.date.start" />:
        				</label>
        				<div class="col-sm-4">
        					<input class="form-control" type="datetime" name="startTime" id="appointment_start" readonly />
        					<div class="error-container form-input-error" id="startTime_error">
	    						<spring:message code='appointmentForm.error.startTime' />
	    					</div>
        				</div>
        				
        				<label class="control-label col-sm-2" for="appointment_end">
        					<spring:message code="calendar.label.date.end" />:
        				</label>
        				<div class="col-sm-4">
        					<input class="form-control" type="datetime" name="endTime" id="appointment_end" readonly />
        					<div class="error-container form-input-error" id="endTime_error">
	    						<spring:message code='appointmentForm.error.endTime' />
	    					</div>
        				</div>
        			</div>
        			
        			<input type="hidden" name="appointmentId" value="0" />
        			<input type="hidden" name="patientId" value="0" />
        			<input type="hidden" name="doctorId" value="${userId}" />
        			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" id="csrf" />
        		</form>
      		</div>
      		<div class="modal-footer">
      			<div class="col-sm-6 text-left">
      				<button type="button" class="btn btn-danger btn-remove appointment-remove">
						<span class='glyphicon glyphicon-remove'>&nbsp;</span><spring:message code="label.remove" />
					</button>
      			</div>
      			<div class="col-sm-6 text-right">
	      			<button type="button" class="btn btn-success appointment-submit">
						<span class='glyphicon glyphicon-floppy-saved'>&nbsp;</span><spring:message code="label.save" />
					</button>
	        		<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.cancel" />
					</button>
				</div>
      		</div>
    	</div>
	</div>
</div>

<script type="text/javascript">
	var localization = new Array();
	localization['calendar.form.title.new'] = "<spring:message code='calendar.form.title.new' />";
	localization['calendar.form.title.update'] = "<spring:message code='calendar.form.title.update' />";
	localization['calendar.form.title.show'] = "<spring:message code='calendar.form.title.show' />";
	
	localization['alert.text.load.failed'] = "<spring:message code='alert.text.load.failed' />";
	localization['alert.text.appointment.reserved'] = "<spring:message code='alert.text.appointment.reserved' />";
	localization['alert.text.appointment.date_problem'] = "<spring:message code='alert.text.appointment.date_problem' />";
	localization['alert.text.appointment.unknown_error'] = "<spring:message code='alert.text.appointment.unknown_error' />";
	localization['alert.text.appointment.delete_error'] = "<spring:message code='alert.text.appointment.delete_error' />";
	
	localization['confirm.text.appointment.delete'] = "<spring:message code='confirm.text.appointment.delete' />";
	
	var loginUser = "${userId}";
</script>