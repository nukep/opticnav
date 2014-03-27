$(function() {
	var messagable = createElemMessagable("#message");
	$("#register_Account").submit(function(event) {
		object = new Object();

		var username = $("#username").val();
		var password = $("#password").val();
		var confirmPassword = $("#confirmPassword").val();
		
		object.username = username;
		object.password = password;
		
		if (password != confirmPassword) {
			showErrorMessage(messagable, "Passwords do not match");
		}
		
		else if(username == "" || password == "" || confirmPassword == "") {
			showErrorMessage(messagable, "One or more fields are missing");
		}
		else{
			$.ajax({
				 type: "POST",
				 url: ctx+"/api/account/register",
				 data: JSON.stringify(object),
				 contentType: "application/json; charset=utf-8",
				 complete: ajaxMessageClosureRedirectOnSuccess(messagable, "/", "message")
				});
		}
		event.preventDefault();
	});
});
	
