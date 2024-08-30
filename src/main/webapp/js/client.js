function changeUse(button, clientCode) {
  var newStatus;
   if (button.classList.contains('btn-outline-success')) {
    button.classList.remove('btn-outline-success');
    button.classList.add('btn-outline-danger');
    button.textContent = 'X';
    newStatus = 'X';
  } else {
    button.classList.remove('btn-outline-danger');
    button.classList.add('btn-outline-success');
    button.textContent = 'O';
    newStatus = 'O';
  }

  var xhr = new XMLHttpRequest();
  xhr.open('POST', 'ClientUse', true);
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhr.send('clientCode=' + encodeURIComponent(clientCode) + '&status=' + encodeURIComponent(newStatus));

  xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
      console.log('Status updated successfully');
  } else if (xhr.readyState === 4) {
      console.log('error');
    };
  }  
}

function checkName(event) {
    var pattern = /[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\s()]/g;
    var currentValue = this.value;

    if (pattern.test(currentValue)) {
        // 허용된 문자를 제외한 모든 문자를 제거
        this.value = currentValue.replace(pattern, '');
    }
}