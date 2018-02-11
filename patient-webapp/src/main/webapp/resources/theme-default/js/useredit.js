$(function(){
	
	$(".useredit-form").formChecker({
		checkInline: true,
		onInput: function(currentInput){
			if(currentInput.attr("type") == "password"){
				var password1 = $("#password");
				var password2 = $("#password2");
				
				var ok = (currentInput.val() == password1.val() && currentInput.val() == password2.val());
				if(ok){
					password1.closest("div.form-group").addClass("has-success").removeClass("has-error");
					password2.closest("div.form-group").addClass("has-success").removeClass("has-error");
					
					password2.parent().find(".error-container").fadeOut();
				}else{
					password1.closest("div.form-group").addClass("has-error").removeClass("has-success");
					password2.closest("div.form-group").addClass("has-error").removeClass("has-success");
					
					password2.parent().find(".error-container").fadeIn();
				}
				
				return ok;
			}
			return true;
		}
	});
	
	checkboxCheckJustOne("input.group-just-one");
	
	$(".chk-reset-password").on("change", function(){
		if($(this).is(":checked")){
			$(".chk-reset-password-span").fadeIn();
		}else{
			$(".chk-reset-password-span").fadeOut();
		}
	});
});

/**
 * We can check in just one checkbox.
 * 
 * @param obj The html checkboxes which concern this rule
 * */
function checkboxCheckJustOne(obj){
	$(obj).on("click", function(e){
		if($(this).is(":checked")){
			var $this = $(this);
			$(obj).each(function(k, v){
				if($(v).val() != $this.val()){
					$(v).prop("checked", false);
				}
			});
		}
	});
}