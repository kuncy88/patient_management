$(function(){
	setNavigation();
});
/**
 * https://www.itworld.com/article/2832973/development/setting-an-active-menu-item-based-on-the-current-url-with-jquery.html
 * 
 * This method will set the active menu item(add new class). Search all of the menu item and set the active class by href.
 * */
function setNavigation(){
	var path = window.location.pathname;
	path = path.replace(/\/$/, "");
	path = decodeURIComponent(path);

	$(".nav a").each(function () {
		var href = $(this).attr('href');
	    if (path.substring(0, href.length) === href) {
	    	$(this).closest('li').addClass('active');
	    }
	});
}