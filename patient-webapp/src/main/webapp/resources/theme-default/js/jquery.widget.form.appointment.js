/**
 * @author Csaba Kun
 * 
 * This widget handle the appointment process. We can add, change or remove an appointment with this.
 * We have to use this widget with a html form. The server communication is executed by the jquery ajax. 
 * */
$.widget("custom.appointmentFormHandler", {
	_var:{
		/**
		 * We can use three mode, currently:
		 * 1. new: add new appointment
		 * 2. update: change an appointment data or move to another time/day
		 * 3. show: show an appointment data
		 * */
		mode: "new"
	},
	_html:{
		form: null,
		btn: {},
		input: {},
		calendar: null
	},
	//search the form element
	_create : function() {
		//the html form
		this._html.form = this.element.find(".modal-body > form");
		
		//the controller buttons
		this._html.btn.remove = $("button.appointment-remove");
		this._html.btn.save = $("button.appointment-submit");
		this._html.btn.addUser = this._html.form.find("#add_new_user");
		this._html.btn.addUser.data("text", this._html.btn.addUser.html());
		this._html.btn.addUser.data("title", this._html.btn.addUser.attr("title"));
		
		this._html.input.patient = this._html.form.find("input#patient");
		this._html.input.patientId = this._html.form.find("input[name='patientId']");
		this._html.input.appointmentId = this._html.form.find("input[name='appointmentId']");
		this._html.input.csrf = this._html.form.find("#csrf");
		this._html.input.startTime = this._html.form.find("#appointment_start");
		this._html.input.endTime = this._html.form.find("#appointment_end");
		this._html.input.description = this._html.form.find("#description");
		this._html.input.tags = this._html.form.find("#notes");
		
		//this is the calendar, so we can reach the calendar methods
		this._html.calendar = $('#calendar');
		
		//init the events
		this._initSubmitEvent();
		this._iniRemoveEvent();
		this._initPatientAutocomplete();
		this._initAddNewUserEvent();
	},
	//init the event when the user click the form submit button
	_initSubmitEvent: function(){
		//it is necessary for the scope
		var $this = this;
		this._html.btn.save.click(function(){
			$(this).prop("disabled", true);
			$.post({
				url: $this._html.form.attr("action"),
				data: $this._html.form.serialize(),
				success: function(data){

					if(data.validated){			//the form data was valid
						switch(data.result){
							case 1:{			//the operation was success
								//update the current event on the calendar and close the window
								$this._html.calendar.fullCalendar('removeEvents', data.appointment.id);
								
								$this._html.calendar.fullCalendar('renderEvent', 
									createAppointmentEvent(data.appointment));
								
								$this.close();
								break;
							}
							case -1:{			//the appointment is reserved
								alert(localization['alert.text.appointment.reserved']);
								break;
							}
							case -2:{			//the new date is not correct
								alert(localization['alert.text.appointment.date_problem']);
								break;
							}
							default:{			//perhaps database error
								alert(localization['alert.text.appointment.unknown_error']);
								break;
							}
						}
					} else {				//hoops, the form is not vlaid
						$(".form-input-error").hide();
						//show the error
						//the i variable will contains the name of error input
						$.each(data.errorMessages, function(i, text){
							$("#" + i + "_error").show();
						});
					}
				}
			}).fail(function(error){
				alert(error.status + " (" + error.statusText + ")");
			}).always(function(){
				$this._html.btn.save.prop("disabled", false);
			});			
		});
	},
	//init the remove event(we want to delete an appointment)
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
		        	if(data != null && data == true){		//the operation was ok
		        		$this._html.calendar.fullCalendar('removeEvents', id);
		        		
						$this.close();
		        	} else {								//hoops, there is any problem
		        		alert(localization['alert.text.appointment.delete_error'])
		        	}
		        }).fail(function(error){
					alert(error.status + " (" + error.statusText + ")");
				}).always(function(){
					$this._html.btn.remove.attr("disabled", false);
				});
			} else {
				$this._html.btn.remove.attr("disabled", false);
			}
		});
	},
	//init the autocomplete event, so the user can filter the patient by name
	//just you have to write into the input element.
	_initPatientAutocomplete: function(){
		var form = this._html.form;
		var $this = this;
	
		this._html.input.patient.typeahead({
			displayKey: 'name',
			source:  function (query, process) {
				//get user list by name
				$.post('/usermanager/userList', { 
		        		query: query,
		        		_csrf: $this._html.input.csrf.val()
		        	}, function (data) {
		        		if(data != null && data.userList != null){	
		        			//it was found
		        	        process(data.userList);
		        		}else{
		        			process({});
		        		}
		        	}
		        );
			},
			updater: function(item) {
				//add the user id to the hidden element
				$this._html.input.patientId.val(item.id);
		        return item;
		    }
		}).on("input", function(){
			//if the user remove the text from the input element then we remove the hidden variable
			if($(this).val().length == 0){
				$this._html.input.patientId.val("");
			}
		});
	},
	//open the usermanager window
	_initAddNewUserEvent: function(){
		this._html.btn.addUser.click(function(){
			var win = window.open($(this).data("href"), '_blank');
			win.focus();
		});
	},
	//set the value of form element
	_changeModalContent: function(id, start, end){
		//set the title
		this.changeTitle();
		
		$(".form-input-error").hide();

		//add datetime picker widget
		this._html.input.startTime.parent().datetimepicker({
			format: 'YYYY-MM-DD HH:mm:00',
            sideBySide: true,
            stepping: unitInterval,
			minDate: moment().format("YYYY-MM-DD 00:00:00"),
			locale:  moment.locale('en', {
		        week: { dow: 1 }
		    }),
		});
		//Set the visible and invisible element. It is depend on the selected mode.
		if(this._var.mode == "new"){
			this._html.btn.save.show();
			this._html.btn.remove.hide();
			
			this._html.input.description.removeClass("readonly").addClass("editable");
			this._html.input.tags.removeClass("readonly").addClass("editable");
			this._html.input.patient.removeClass("readonly").addClass("editable");
			this._html.btn.addUser.prop("disabled", false)
				.html(this._html.btn.addUser.data("text"))
				.attr("title", this._html.btn.addUser.data("title"));
			
			this._html.input.startTime.parent().datetimepicker('ignoreReadonly', false);
		} else if(this._var.mode == 'update'){
			this._html.btn.save.show();
			this._html.btn.remove.show();
			
			this._html.input.description.removeClass("readonly").addClass("editable");
			this._html.input.tags.removeClass("readonly").addClass("editable");
			this._html.input.patient.removeClass("readonly").addClass("editable");
			this._html.btn.addUser.prop("disabled", false)
				.html(this._html.btn.addUser.data("text"))
				.attr("title", this._html.btn.addUser.data("title"));
			
			this._html.input.startTime.parent().datetimepicker('ignoreReadonly', true);
		} else if(this._var.mode == 'show'){
			this._html.btn.remove.hide();
			this._html.btn.save.hide();
			
			this._html.input.description.addClass("readonly").removeClass("editable");
			this._html.input.tags.addClass("readonly").removeClass("editable");
			this._html.input.patient.addClass("readonly").removeClass("editable");
			this._html.btn.addUser.prop("disabled", true)
				.html("&nbsp;")
				.attr("title", "");
			
			this._html.input.startTime.parent().datetimepicker('ignoreReadonly', false);
		} 
		
		$(".readonly").prop("readonly", true);
		$(".editable").prop("readonly", false);
		
		if(id == null){			//we will add new event
			start = (start == null) ? "" : start.format(dateformat);
			end = (end == null) ? "" : end.format(dateformat);
			
			this._html.input.startTime.val(start);
			this._html.input.endTime.val(end);
		}else{					//we want to change an appointment so we have to load the data from database by row id
			this._loadDataFromDatabase(id);
		}
	},
	//window close events(reset the form data)
	_onClose: function(){
		this._html.form[0].reset();
		this._html.input.patientId.val("0");
		this._html.input.appointmentId.val("");
	},
	//load an appointment data from the database
	//@param id the appointment row id in the atabase
	_loadDataFromDatabase: function(id){
		var $this = this;
		$.post('/mycalendar/getAppointment', { 
	    		id: id,
	    		_csrf: this._html.form.find("#csrf").val()
	    	}, function (data) {
	    		if(data != null){	//set the value of form element
	    			var user = data.patient;
	    			if(hasRolePatient && $this._var.mode == 'show'){
	    				user = data.doctor;
	    			}
	    			$this._html.input.patientId.val(data.patient.id);
	    			$this._html.input.appointmentId.val(data.id);
	    			
	    			var name = user.fullname;
    				if(name == null || name.length == 0){
    					name = user.userName;
    				}
    				if($this._html.input.patient.is("input")){
    					$this._html.input.patient.val(name + " - " + user.email);
    				} else {
    					$this._html.input.patient.html(name + " - " + user.email);
    				}
	    			$this._html.input.description.val(data.description);
	    			
	    			var notes = "";
	    			if(data.notes != null && data.notes.length > 0 && data.notes[0] != ""){
	    				notes = data.notes.join(" ");
    				}
	    			$this._html.input.tags.val(notes);
	    			
	    			$this._html.input.startTime.val(moment(data.timet).format(dateformat));
	    			$this._html.input.endTime.val(moment(data.timet).add(unitInterval, 'minutes').format(dateformat));
	    		}else{			//read data was unsuccessfully
	    			alert(localization['alert.text.load.failed']);
	    		}
	    	}
	    ).fail(function(error){
			alert(error.status + " (" + error.statusText + ")");
			$this.close();
		});
	},
	/**
	 * Show the window which we can handle the appointments
	 * 
	 * @param string mode What kind of action want to use(new, update or show data)
	 * @param long id If we want to update or show an appointment then we have to add this params. Otherwise it can be null.
	 * @param moment start Begin of appointment
	 * @param moment end End of appointment
	 * */
	show: function(mode, id, start, end) {
		this._var.mode = mode;
		var $this = this;
		
		this._changeModalContent(id, start, end);
		
		//init the window close event
		this.element.modal('show').on('hidden.bs.modal', function () {
			$this._onClose();
		});		
	},
	//window close events
	close: function(){
		this._onClose();
		this.element.modal('hide');
	},
	//change the window title by mode
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