function getID(){
	document.getElementById("userID").value = opener.document.getElementById("userID").value;
	document.getElementById("useBtn").disabled = true;
}

function IDCheck(){
	var id = document.getElementById("userID").value;
	
	var xhr = new XMLHttpRequest();
	xhr.open('POST', 'UserIDCheck', true);
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xhr.send('userID=' + encodeURIComponent(id));

	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
		    if (xhr.responseText.trim() == "0") {
		        document.getElementById("useBtn").disabled = true;
				document.getElementById("msg").innerHTML = "사용 불가능한 아이디입니다.";
				document.getElementById("msg").style.color = "red";
		    } else if (xhr.responseText.trim() == "1") {
		        document.getElementById("useBtn").disabled = false;
		        document.getElementById("msg").innerHTML = "사용 가능한 아이디입니다.";
				document.getElementById("msg").style.color = "black";
		    } 
		}
	}
}

function sendCheckValue(){
	var parentSubmitBtn = opener.document.getElementById("submitBtn");
	opener.document.getElementById("userID").value = document.getElementById("userID").value;
	
	if (parentSubmitBtn) {
	    parentSubmitBtn.disabled = false;  // 버튼을 활성화
	}
	
	if(opener != null){
		opener.chkForm = null;
		self.close();
	}
}

document.addEventListener('DOMContentLoaded', function() {
    //inputID
    var userIDInput = document.querySelector('#userID');
    userIDInput.addEventListener('input', checkID);
});