var unitInterval = 15;				//in minutes
var dateformat = "YYYY-MM-DD HH:mm:ss";
$(document).ready(function() {
	//resize on the start
	resizeCalendarContainer();
	
	//resize on the resize event
	$( window ).resize(function() {
		resizeCalendarContainer();
	});
	
	//init the form handler widget
	$('#calendarFormModal').appointmentFormHandler();
	
	//init the reschedule form widget
	$('#reScheduleFormModal').reScheduleFormHandler();

	//add calendar widget
	$('#calendar').fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'agendaWeek,agendaDay,listDay'
		},
		height: 'parent',
		scrollTime: '08:00:00',
		defaultView: 'agendaWeek',
		weekNumbers: true,
	    weekNumbersWithinDays: true,
	    weekNumberCalculation: 'ISO',
	    timeFormat: 'HH:mm',
	    navLinks: true,
	    editable: true,
	    allDaySlot: false,
	    slotDuration: '00:15:00',
	    slotLabelFormat: 'HH:mm',
	    slotEventOverlap: false,
	    selectable: true,
	    selectHelper: true,
	    selectConstraint:{
	    	start: '00:00', 
	        end: '24:00', 
	    },
	    select: function(start, end, jsEvent, view) {
	    	var tmpStart = start.clone();
	    	var tmpEnd = end.clone();

	    	if (start.isBefore(moment()) || !start.add(unitInterval, 'minutes').isSame(end, 'minutes')){
	    		
	            $('#calendar').fullCalendar('unselect');
	            return false;
	        }
	    	
	    	$('#calendarFormModal').appointmentFormHandler('show', 'new', null, tmpStart, tmpEnd);
	    },
	    events: function(start, end, timezone, callback) {
	        $.ajax({
	        	url: '/mycalendar/getAppointmentList',
	        	method: 'post',
	        	dataType: 'json',
	        	data:{
	        		_csrf: $("#csrf").val(),
	        		from: start.format(dateformat),
	        		to: end.format(dateformat),
	        		userId: loginUser
	        	},
	        	success: function(data){
	        		if(data != null){
	        			var events = [];
	        			$.each(data, function(i, item){
	        				events.push(createAppointmentEvent(item));
	        			});
	        			callback(events);
	        		}
	        	}
	        });
	    },
	    eventAfterRender: function(event, element) {
	    	element.attr("title", event.description)
	    	element.data("html", true);
	    	element.tooltip();
	    },
	    eventClick: function(calEvent, jsEvent, view) {
	    	if (!calEvent.start.isBefore(moment())){
	    		$('#calendarFormModal').appointmentFormHandler('show', 'update', calEvent.id);
	    	}
	    },
	    viewRender: function(view, element){
	    	var headers = $("th.fc-widget-header:not('.fc-axis'):not('.fc-past')");

	    	var span = $("<span />")
	    		.addClass("glyphicon glyphicon-refresh appointment-change-icon cPointer")
	    		.html("&nbsp;").click(function(){
	    			var date = $(this).parent().data("date");
	    			$('#reScheduleFormModal').reScheduleFormHandler('show', date);
	    		});
	    	headers.append( span);
	    }
	});

});

$.widget("custom.reScheduleFormHandler", {
	_html:{
		form: null,
		btn: {},
		input: {},
		calendar: null
	},
	_var:{
		oldDate: null
	},
	_initPicker: function(){
		var object = this._html.input.newDate.parent();
		object.datetimepicker({
			format: 'YYYY-MM-DD',
			ignoreReadonly: true
		}).on("dp.change", function (e) {
			object.data("DateTimePicker").minDate(moment());
        });
	},
	_initSubmitEvent: function(){
		var $this = this;
		
		this._html.btn.save.on("click", function(){
			$.ajax({
				url: $this._html.form.attr("action"),
				method: "post",
				data: $this._html.form.serialize(),
				success: function(data){
					
					switch(data){
						case 1:{	
							var events = $this._html.calendar.fullCalendar('clientEvents', function(event){
								return (event.start.format("YYYYMMDD") == moment($this._html.input.oldDate.val()).format("YYYYMMDD"));
							});
							
							var dayDiff = moment($this._html.input.newDate.val()).diff(moment(moment($this._html.input.oldDate.val())), "days");
							var copyEvents = [];
							$.each(events, function(i, item){
								item.start.add(dayDiff, "days");
								item.end.add(dayDiff, "days");
								
								$this._html.calendar.fullCalendar('removeEvents', item.id);
							});
							$this._html.calendar.fullCalendar('renderEvents', events);
							
							$this.close();
							break;
						}
						case -1:{
							alert(localization['alert.text.appointment.reserved']);
							break;
						}
						case -2:{
							alert(localization['alert.text.appointment.date_problem']);
							break;
						}
						default:{
							alert(localization['alert.text.appointment.unknown_error']);
							break;
						}
					}
				}
			});
		});
	},
	_create: function(){
		this._html.form = this.element.find(".modal-body > form");
		
		this._html.btn.save = $("button.reschedule-submit");
		
		this._html.input.newDate = this._html.form.find("#newDate");
		this._html.input.oldDate = this._html.form.find("#oldDate");
		
		this._html.calendar = $('#calendar');
		
		this._initPicker();
		this._initSubmitEvent();
	},
	_onClose: function(){
		this._html.form[0].reset();
	},
	show: function(oldDate){
		var $this = this;
		this._var.oldDate = oldDate;
		
		this._html.input.oldDate.val(this._var.oldDate);
		
		this.element.modal('show').on('hidden.bs.modal', function () {
			$this._onClose();
		});
		
		this._html.input.newDate.parent().data("DateTimePicker").disabledDates([moment(this._var.oldDate)]);
	},
	close: function(){
		this._onClose();
		this.element.modal('hide');
	},
});

$.widget("custom.appointmentFormHandler", {
	_var:{
		mode: "new"
	},
	_html:{
		form: null,
		btn: {},
		input: {},
		calendar: null
	},
	_create : function() {
		this._html.form = this.element.find(".modal-body > form");
		
		this._html.btn.remove = $("button.appointment-remove");
		this._html.btn.save = $("button.appointment-submit");
		this._html.btn.addUser = this._html.form.find("#add_new_user");
		
		this._html.input.patient = this._html.form.find("input#patient");
		this._html.input.patientId = this._html.form.find("input[name='patientId']");
		this._html.input.appointmentId = this._html.form.find("input[name='appointmentId']");
		this._html.input.csrf = this._html.form.find("#csrf");
		this._html.input.startTime = this._html.form.find("#appointment_start");
		this._html.input.endTime = this._html.form.find("#appointment_end");
		this._html.input.description = this._html.form.find("#description");
		this._html.input.tags = this._html.form.find("#notes");
		
		this._html.calendar = $('#calendar');
		
		this._initSubmitEvent();
		this._iniRemoveEvent();
		this._initPatientAutocomplete();
		this._initAddNewUserEvent();
	},
	_initSubmitEvent: function(){
		var $this = this;
		this._html.btn.save.click(function(){
			$(this).prop("disabled", true);
			$.post({
				url: $this._html.form.attr("action"),
				data: $this._html.form.serialize(),
				success: function(data){

					if(data.validated){			//the form data was valid
						switch(data.result){
							case 1:{	
								$this._html.calendar.fullCalendar('removeEvents', data.appointment.id);
								
								$this._html.calendar.fullCalendar('renderEvent', 
									createAppointmentEvent(data.appointment));
								
								$this.close();
								break;
							}
							case -1:{
								alert(localization['alert.text.appointment.reserved']);
								break;
							}
							case -2:{
								alert(localization['alert.text.appointment.date_problem']);
								break;
							}
							default:{
								alert(localization['alert.text.appointment.unknown_error']);
								break;
							}
						}
					} else {
						$(".form-input-error").hide();
						$.each(data.errorMessages, function(i, text){
							$("#" + i + "_error").show();
						});
					}
					
					$this._html.btn.save.prop("disabled", false);
				}
			});			
		});
	},
	_iniRemoveEvent: function(){
		var $this = this;
		
		this._html.btn.remove.on("click", function(){
			$this._html.btn.remove.attr("disabled", true);
			
			if(confirm(localization['confirm.text.appointment.delete'])){
				var id = $this._html.input.appointmentId.val();
				
				$.post('/mycalendar/removeAppointment', { 
		        	id: id,
		        	_csrf: $this._html.input.csrf.val()
		        }, function (data) {
		        	if(data != null && data == true){
		        		$this._html.calendar.fullCalendar('removeEvents', id);
		        		
						$this.close();
		        	} else {
		        		alert(localization['alert.text.appointment.delete_error'])
		        	}
		        	$this._html.btn.remove.attr("disabled", false);
		        });
			} else {
				$this._html.btn.remove.attr("disabled", false);
			}
		});
	},
	_initPatientAutocomplete: function(){
		var form = this._html.form;
		var $this = this;
	
		this._html.input.patient.typeahead({
			displayKey: 'name',
			source:  function (query, process) {
		       $.post('/usermanager/userList', { 
		        		query: query,
		        		_csrf: $this._html.input.csrf.val()
		        	}, function (data) {
		        		if(data != null && data.userList != null){		        	        
		        	        process(data.userList);
		        		}else{
		        			process({});
		        		}
		        	}
		        );
			},
			updater: function(item) {
				$this._html.input.patientId.val(item.id);
		        return item;
		    }
		}).on("input", function(){
			if($(this).val().length == 0){
				$this._html.input.patientId.val("");
			}
		});
	},
	_initAddNewUserEvent: function(){
		this._html.btn.addUser.click(function(){
			var win = window.open($(this).data("href"), '_blank');
			win.focus();
		});
	},
	_changeModalContent: function(id, start, end){
		this.changeTitle();
		$(".form-input-error").hide();
		
		console.log(this._html.input.startTime);
		if(this._var.mode != "update"){
			this._html.btn.remove.hide();
			this._html.input.startTime.prop("readonly", true);
		} else{
			this._html.btn.remove.show();
			this._html.input.startTime.prop("readonly", false);
		}
		
		if(id == null){
			start = (start == null) ? "" : start.format(dateformat);
			end = (end == null) ? "" : end.format(dateformat);
			
			this._html.input.startTime.val(start);
			this._html.input.endTime.val(end);
		}else{
			this._loadDataFromDatabase(id);
		}
	},
	_onClose: function(){
		this._html.form[0].reset();
		this._html.input.patientId.val("0");
		this._html.input.appointmentId.val("");
	},
	_loadDataFromDatabase: function(id){
		var $this = this;
		$.post('/mycalendar/getAppointment', { 
	    		id: id,
	    		_csrf: this._html.form.find("#csrf").val()
	    	}, function (data) {
	    		if(data != null){
	    			$this._html.input.patientId.val(data.patient.id);
	    			$this._html.input.appointmentId.val(data.id);
	    			
	    			var name = data.patient.fullname;
    				if(name == null || name.length == 0){
    					name = data.patient.userName;
    				}
	    			$this._html.input.patient.val(name + " - " + data.patient.email);
	    			$this._html.input.description.val(data.description);
	    			
	    			var notes = "";
	    			if(data.notes != null && data.notes.length > 0 && data.notes[0] != ""){
	    				notes = data.notes.join(" ");
    				}
	    			$this._html.input.tags.val(notes);
	    			
	    			$this._html.input.startTime.val(moment(data.timet).format(dateformat));
	    			$this._html.input.endTime.val(moment(data.timet).add(unitInterval, 'minutes').format(dateformat));
	    		}else{
	    			alert(localization['alert.text.load.failed']);
	    		}
	    	}
	    );
	},
	show: function(mode, id, start, end) {
		this._var.mode = mode;
		var $this = this;
		
		this._changeModalContent(id, start, end);
		
		this.element.modal('show').on('hidden.bs.modal', function () {
			$this._onClose();
		});		
	},
	close: function(){
		this._onClose();
		this.element.modal('hide');
	},
	changeTitle: function(title){
		if(typeof title == "undefined"){
			switch(this._var.mode){
				case "new": {
					title = localization['calendar.form.title.new'];
					break;
				} 
				case "update": {
					title = localization['calendar.form.title.update'];
					break;
				} 
				case "show": {
					title = localization['calendar.form.title.show'];
					break;
				} 
				default: {
					title = "";
					break;
				}
			}
		}
		this.element.find(".modal-title").html(title);
	}
});

function createAppointmentEvent(item){
	var user = item.patient;
	
	var name = user.fullname;
	var description = user.email;
	
	if(name == null || name.length == 0){
		name = user.userName;
	}
	
	if(item.notes != null && item.notes.length > 0 && item.notes[0] != ""){
		description += "<br \>#" + item.notes.join(" #");
	}
	
	var event = {
		id: item.id,
        title: name,
        description: description,
        start: moment(item.timet),
        end: moment(item.timet).add(unitInterval, "minutes"),
        editable: false,
        className: "cPointer"	
	};
	return event;
}

/**
 * Resize the container of calendar.
 * */
function resizeCalendarContainer(){
	//full height
	var full = $(window).outerHeight(true);
	
	var header = $(".pm-page-header").outerHeight(true);
	var bottom = $(".pm-page-footer").outerHeight(true);
	
	//resize the container
	$("#calendar").height(full-header-bottom-20);
}