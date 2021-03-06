/**
 * Links the code that is displayed on the ARD to a USER account
 * Also handles the unlinking of the ARD from the User account
 * 
 * @Authors Jacky Huynh, Danny Spencer, Ido Shalev
 */
$(function() {
	var messagable = createElemMessagable("#message", "#message-loader");
	
	$("#device-register").click(function() {
		var code = $("#code").val();
		
		$.ajax({
			type: "POST",
			url: ctx+"/api/ard",
			data: code,
			contentType: "text/plain",
			complete: ajaxMessageClosure(messagable, function(registered, message) {
				showRemoveButton(registered);
			})
		});
	});
	
	$("#device-remove").click(function() {
		if (confirm("Are you sure you want to unlink this device from your account?")) {
			$.ajax({
				type: "DELETE",
				url: ctx+"/api/ard",
				contentType : "application/json; charset=utf-8",
				complete: ajaxMessageClosure(messagable, function(removed, message) {
					showRemoveButton(!removed);
				})
			});
		}
	});
	
	function updateARD() {
		$.ajax({
			type: "GET",
			url: ctx+"/api/ard",
			contentType : "application/json; charset=utf-8",
			complete: ajaxMessageClosure(messagable, function(hasDevice, message) {
				showRemoveButton(hasDevice);
			})
		});
	}
	
	function showRemoveButton(show) {
		if (show) {
			$("#device-remove").show();
		} else {
			$("#device-remove").hide();
		}
	}
	
	updateARD();
});
