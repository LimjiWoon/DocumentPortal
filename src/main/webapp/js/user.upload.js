function openIDCheck(){
	window.name = "parentForm";
	window.open("UserIDCheck", "chkForm", 
	"toolbar=no, location=no, menubar=no, width=500, height=300, resizable=no, scrollbars=no");
}

function toggleSubmitButton() {
    var submitBtn = document.getElementById("submitBtn");

    submitBtn.disabled = true;
}



document.addEventListener('DOMContentLoaded', function() {
    //inputID
    var userIDInput = document.querySelector('#userID');
    userIDInput.addEventListener('input', checkID);

    var userNameInput = document.querySelector('#userName');
    userNameInput.addEventListener('input', checkName);

	document.getElementById("userID").addEventListener('input', toggleSubmitButton);
});