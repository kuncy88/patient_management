$(function(){
	
	$(".pager .disabled a, .pagination .disabled a").on("click", function(e){
		e.preventDefault();
	});
});