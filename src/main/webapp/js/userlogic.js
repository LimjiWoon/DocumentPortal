function PasswordChange(){
  var popup = window.open('PasswordRenew', '비밀번호 변경', 'toolbar=no, location=no, menubar=no, width=600, height=370');
  var checkWindowClosed = setInterval(function() { 
    if (popup.closed) { 
      clearInterval(checkWindowClosed);
    } 
  }, 500);
}

function passwordChange(){
  var popup = window.open('PasswordRenew', '비밀번호 변경', 'toolbar=no, location=no, menubar=no, width=600, height=370');
  var checkWindowClosed = setInterval(function() { 
    if (popup.closed) { 
      clearInterval(checkWindowClosed);
	  history.back();
    } 
  }, 500);
}

function createAndSubmitForm(item) {
  var form = document.createElement("form");
  form.setAttribute("method", "post");
  form.setAttribute("action", "UpPwd");

  var input = document.createElement("input");
  input.setAttribute("type", "hidden"); // 숨겨진 input으로 사용
  input.setAttribute("id", "item");
  input.setAttribute("name", "item");
  input.setAttribute("value", item); // item 값을 설정

  form.appendChild(input);

  document.body.appendChild(form);
  form.submit();
}


function check(){
	let userpw = document.forms["checkPassword"]["newPassword"];
	if (userpw == null) {
		userpw = document.forms["checkPassword"]["userPassword"];
	}
	
	let reg = /^[0-9a-fA-F]{64}$/;

	if (reg.test(userpw.value)) {
	    return true;
	}
		
	if (userpw.value.indexOf(" ") != -1) {
		alert("비밀번호에 공백을 포함할 수 없습니다.");
		userpw.focus();
		return false;
	}

	reg = /^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[~?!@#$%^&*_-]).{10,20}$/;

	if(!reg.test(userpw.value)){
		alert("비밀번호는 10자 이상 20자리 이하에 영문, 숫자, 특수문자를 포함해야합니다.");
		userpw.focus();
		return false;
	}
	
	return true;
}



function checkID(event) {
    var pattern = /^[a-zA-Z0-9]*$/;
    var currentValue = this.value;

    if (!pattern.test(currentValue)) {
        // 허용된 문자를 제외한 모든 문자를 제거
        this.value = currentValue.replace(/[^a-zA-Z0-9]/g, '');
    }
}

function checkName(event) {
    var pattern = /^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\s.\-']*$/;
    var currentValue = this.value;

    if (!pattern.test(currentValue)) {
        // 허용된 문자를 제외한 모든 문자를 제거
        this.value = currentValue.replace(/[^a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\s.\-']/g, '');
    }
}


function downloadExcel(newActionURL, hiddenValue) {
    var form = document.getElementById('SearchFilter');
    form.action = newActionURL;

	var hiddenInput = document.createElement('input');
	hiddenInput.type = 'hidden';
	hiddenInput.name = 'code';
	hiddenInput.value = hiddenValue;

	form.appendChild(hiddenInput);

    // 폼 제출
    form.submit();
}

function searchExcel(newActionURL) {
    var form = document.getElementById('SearchFilter');
    form.action = newActionURL;

    // 폼 제출
    form.submit();
}

function changeLock(button, userCode) {
  var newStatus;
   if (button.classList.contains('btn-outline-success')) {
    button.classList.remove('btn-outline-success');
    button.classList.add('btn-outline-danger');
    button.textContent = 'O';
    newStatus = 'O';
  } else {
    button.classList.remove('btn-outline-danger');
    button.classList.add('btn-outline-success');
    button.textContent = 'X';
    newStatus = 'X';
  }

  var xhr = new XMLHttpRequest();
  xhr.open('POST', 'UserLock', true);
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhr.send('userCode=' + encodeURIComponent(userCode) + '&status=' + encodeURIComponent(newStatus));

  xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
      console.log('Status updated successfully');
  } else if (xhr.readyState === 4) {
      console.log('error');
    };
  }  
}