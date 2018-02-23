/**
 * @author Csaba Kun
 * 
 * This widget handle the reschedule process. We can reschedule an day to the another day. 
 * We have to use this widget with a html form. It will show an form where the user can choose new date.
 * The server communication is executed by the jquery ajax. 
 * */
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
	//init the boostrap date picker
	_initPicker: function(){
		//search an object which we use the datepicker
		var object = this._html.input.newDate.parent();
		object.datetimepicker({
			format: 'YYYY-MM-DD',
			ignoreReadonly: true,
			minDate: moment().format("YYYY-MM-DD 00:00:00"),
			locale:  moment.locale('en', {
		        week: { dow: 1 }		//first of week day
		    }),
		});
	},
	//init the event when the user click the form submit button
	_initSubmitEvent: function(){
		var $this = this;
		
		this._html.btn.save.on("click", function(){
			$.ajax({
				url: $this._html.form.attr("action"),
				method: "post",
				data: $this._html.form.serialize(),
				success: function(data){
					
					switch(data){
						case 1:{			//it was everything ok
							//get all events from the calendar which we want to move to another day
							//this function will get all calendar event therefore we will filter these events
							var events = $this._html.calendar.fullCalendar('clientEvents', function(event){
								//filter the events
								return (event.start.format("YYYYMMDD") == moment($this._html.input.oldDate.val()).format("YYYYMMDD"));
							});
							
							//check the day different between the old and the new date
							//so we can that how many day have to move these events
							var dayDiff = moment($this._html.input.newDate.val()).diff(moment(moment($this._html.input.oldDate.val())), "days");
							
							$.each(events, function(i, item){
								//move the events to another day
								//these variable is references, just we have to change this value
								item.start.add(dayDiff, "days");
								item.end.add(dayDiff, "days");
								
								//remove the events from the calendar
								$this._html.calendar.fullCalendar('removeEvents', item.id);
							});
							//add the events to the new day
							$this._html.calendar.fullCalendar('renderEvents', events);
							//close the dialog window
							$this.close();
							break;
						}
						case -1:{			//the day is reserved(there are appointment which are in conflict)
							alert(localization['alert.text.appointment.reserved']);
							break;
						}
						case -2:{			//the date format is not correct
							alert(localization['alert.text.appointment.date_problem']);
							break;
						}
						default:{			//perhapse database error
							alert(localization['alert.text.appointment.unknown_error']);
							break;
						}
					}
				}
			});
		});
	},
	//add html element and init events and create widget object
	_create: function(){
		this._html.form = this.element.find(".modal-body > form");
		
		this._html.btn.save = $("button.reschedule-submit");
		
		this._html.input.newDate = this._html.form.find("#newDate");
		this._html.input.oldDate = this._html.form.find("#oldDate");
		
		this._html.calendar = $('#calendar');
		
		this._initPicker();
		this._initSubmitEvent();
	},
	//this will execute when the user close the window
	_onClose: function(){
		//reset the form data
		this._html.form[0].reset();
	},
	//open the bootsrap window
	//@param oldDate This is the date from which we want to move the events
	show: function(oldDate){
		var $this = this;
		this._var.oldDate = oldDate;
		
		this._html.input.oldDate.val(this._var.oldDate);
		
		//init the close event
		this.element.modal('show').on('hidden.bs.modal', function () {
			$this._onClose();
		});
		
		//thehes are the dates which we can't use.
		this._html.input.newDate.parent().data("DateTimePicker").disabledDates([moment(this._var.oldDate)]);
	},
	//window close event
	close: function(){
		this._onClose();
		this.element.modal('hide');
	},
});