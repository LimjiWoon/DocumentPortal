function setUserID() {
	if (!userID || sessionStorage.getItem('check') != 1 || userID == ''){
		alert('유효하지 않는 접근입니다.')
		history.back();
	} else{
		document.getElementById("userIDInput").value = userID;
	} 
  
}

document.addEventListener('DOMContentLoaded', setUserID);