//calendar unit in minutes
var unitInterval = 15;
//used date format
var dateformat = "YYYY-MM-DD HH:mm:ss";

$(document).ready(function() {
	//resize the calendar to the container end of the page load
	resizeCalendarContainer();
	
	//resize resize the calendar to the container when the user resize the window
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
	    titleFormat: 'YYYY, MMM D',
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
	    //this is the select event when the user select the units
	    select: function(start, end, jsEvent, view) {
	    	//we have to clone this object because this is a references.
	    	//if we didn't clone this, we would change the original value.
	    	var tmpStart = start.clone();
	    	var tmpEnd = end.clone();
	    	
	    	//with this condition we can select just one unit, which point to the future
	    	if (start.isBefore(moment()) || !start.add(unitInterval, 'minutes').isSame(end, 'minutes')){
	    		
	            $('#calendar').fullCalendar('unselect');
	            return false;
	        }
	    	//show the handler form
	    	$('#calendarFormModal').appointmentFormHandler('show', 'new', null, tmpStart, tmpEnd);
	    },
	    //load all of events into the calendar
	    events: function(start, end, timezone, callback) {
	    	//use the ajax request to reach the data
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
	        				//create an event
	        				events.push(createAppointmentEvent(item));
	        			});
	        			//add events to the calendar
	        			callback(events);
	        		}
	        	}
	        });
	    },
	    //after we added an event to the calendar
	    eventAfterRender: function(event, element) {
	    	//add tooltip
	    	element.attr("title", event.description)
	    	element.data("html", true);
	    	element.tooltip();
	    },
	    //if the user click an event
	    eventClick: function(calEvent, jsEvent, view) {
	    	//the click event is allowed if the event is in the future
	    	if (!calEvent.start.isBefore(moment())){
	    		//show the handler form
	    		$('#calendarFormModal').appointmentFormHandler('show', 'update', calEvent.id);
	    	}
	    },
	    //change the view elements
	    viewRender: function(view, element){
	    	var headers = $("th.fc-widget-header:not('.fc-axis'):not('.fc-past')");
	    	//add extra icon to the column header(use this the reschedule)
	    	var span = $("<span />")
	    		.addClass("glyphicon glyphicon-refresh appointment-change-icon cPointer")
	    		.html("&nbsp;").click(function(){
	    			//reschedule an day to the another day(show the form)
	    			var date = $(this).parent().data("date");
	    			$('#reScheduleFormModal').reScheduleFormHandler('show', date);
	    		});
	    	headers.append(span);
	    }
	});

});

/**
 * Create an appointment event which we can add to the calendar.
 * 
 * @param item Data from database which we can make the event object
 * 
 * @return The new event object.
 * */
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