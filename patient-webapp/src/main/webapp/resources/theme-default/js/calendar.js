$(document).ready(function() {
	//resize on the start
	resizeCalendarContainer();
	
	//resize on the resize event
	$( window ).resize(function() {
		resizeCalendarContainer();
	});
	
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
	    	alert("selecting: " + start.format() + " -> " + end.format());
	    }
	   
	});

});

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