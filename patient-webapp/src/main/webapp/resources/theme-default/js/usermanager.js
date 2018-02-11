$(function(){
	$(".pager .disabled a, .pagination .disabled a").on("click", function(e){
		e.preventDefault();
	});
	
	$(".delete-user").on("click", function(){
		if(confirm($(this).data("dialog"))){
			location.href="/usermanager/deleteUser?row=" + $(this).data("id");
		}
	});
});