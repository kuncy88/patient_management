/**
 * @author Csaba Kun
 * 
 * @version 1.0
 * 
 * @date 04.02.2018
 * 
 * This widget check an form that it is valid or not. All of the inputs have to set data-pattern
 * which we want to control. This pattern is an array which contain regular expressions. If the
 * pattern is not match then the submit event will disable. We can set an error
 * container to the input. The class name is error-container. If this container is exists then the
 * error message will show here. This error container must be in the same element which there is the 
 * input element(same parent element).
 * 
 * <input data-pattern='[".{1,}"]' type="text" class="form-control" id="username" name="username">
 * <div class="error-container">
 * 		<span class="label label-danger">Please, add correct the username</span>
 * </div>
 * 
 * Options:
 * 		checkInline(default: false): 	If it is true then it means, do the input validation when the user write into the input.
 * 										Otherwise the input validation will be done when the user click to the sumbit button.
 * 		onInpunt(default: null):		This function will be called when the user write into the input element.
 * 		onBeforeLoad(default: null):	This function will be called before all of the element will get the input event.	
 * 		onAfterLoad(default: null):		This function will be called after all of the element will get the input event and triggered.					
 * 
 */
$.widget("custom.formChecker", {
	options : {
		checkInline : false,
		onInput : null,
		onBeforeLoad : null,
		onAfterLoad : null,
		onAfterSubmit: null
	},
	html : {
		submit : null,							//this is the form submit button
		formItems : null						//all of the form input element
	},
	_error : false,								//this an marker (error happend or not)
	_create : function() {
		$this = this;
		//call the before load event
		if (typeof this.options.onBeforeLoad == "function") {
			this.options.onBeforeLoad($(this.element));
		}

		// searching for the submit button
		this.html.submit = $(this.element).find("[type='submit']");
		this.html.formItems = $(this.element).find(":input");

		// add input event to the input objects
		this._initChecker();

		// if it is necessary that we check the form inline.
		if (this.options.checkInline) {
			//triggered all of the form element input events
			this._inputsTrigger();
			
			//create submit events
			$(this.element).on("submit", function(e) {
				//calling extra events
				if (typeof $this.options.onSubmit == "function") {
					$this._error = !$this.options.onSubmit($(this), !$this._error);
				}
				return !$this._error;
			});
		} else {
			//we don't need to check the form in inline mode.
			//we will check this form when the user will click to the submit button
			$(this.element).on("submit", function(e) {				
				$this.html.submit.prop("disabled", true);
				
				$this._error = false;
				//check all of the form elements
				$this._inputsTrigger();
				//calling extra events
				if (typeof $this.options.onSubmit == "function") {
					$this._error = !$this.options.onSubmit($(this), !$this._error);
				}
				
				if($this._error){
					$this.html.submit.prop("disabled", false);
				}
				
				return !$this._error;
			});
		}
		
		if (typeof this.options.onAfterLoad == "function") {
			this.options.onAfterLoad($(this.element));
		}
	},
	//this method will triggered all of the form elements which contains data-pattern attributes
	_inputsTrigger: function(){
		$.each(this.html.formItems, function(k, v) {
			if ($(v).attr("data-pattern")) {
				$(v).trigger("input");
			}
		});
	},
	//add inpunt events all of the from elements which contains data-pattern attributes
	_initChecker : function() {
		var $this = this;

		$.each(this.html.formItems, function(k, v) {
			if ($(v).attr("data-pattern")) {
				//add events
				$this._inputCheck($(v), $(v).data("pattern"));
			}
		});
	},
	//add inpunt events to the one form element
	//@param input form element
	//@param patterns Regular expressions
	_inputCheck : function(input, patterns) {
		$this = this;
		input.on("input", function() {
			//check the element with the patterns
			var ok = $this._patternTest(input, patterns);
			// if the pattern test was successful
			if (ok) {
				//calling the extra input events
				if (typeof $this.options.onInput == "function") {
					ok = $this.options.onInput($(this));
				}
			}
			if (!ok) {		//successful
				//show the error message
				input.closest("div.form-group").addClass("has-error")
						.removeClass("has-success");
				input.parent().find(".error-container").fadeIn();
				
				if ($this.options.checkInline) {
					$this.html.submit.prop("disabled", true);
				} else {
					$this._error = true;
				}
			} else {		//unsuccessful
				//remove the error message
				input.closest("div.form-group").addClass("has-success")
						.removeClass("has-error");
				input.parent().find(".error-container").fadeOut();
				if ($this.options.checkInline) {
					$this.html.submit.prop("disabled", false);
				}
			}
		});
	},
	//check an element with the regular expressions
	_patternTest : function(input, patterns) {
		var ok = true;
		$.each(patterns, function(i, pattern) {
			pattern = new RegExp(pattern);
			if (ok && !pattern.test(input.val())) {
				ok = false;
			}
		});
		return ok;
	}
});

/**
 * Save login form data into the cookie
 */
function formRemember(form, success) {
	var remember = $(form).find("#remember");
	if (remember.is(":checked")) {
		$.cookie("username", $(form).find("#userNameInput").val(), {
			expires : 20 * 365
		});
	} else {
		$.removeCookie("username");
	}
	return success;
}