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

document.addEventListener('DOMContentLoaded', function() {
  // 모달이 표시될 때 실행되는 이벤트 핸들러
  const clientUpdateModal = document.getElementById('ClientUpdateModal');
  clientUpdateModal.addEventListener('show.bs.modal', function (event) {
    // 이벤트를 발생시킨 버튼을 가져옵니다.
    const button = event.relatedTarget;
    
    // 버튼의 data-* 속성에서 값을 가져옵니다.
    const clientName = button.getAttribute('data-client-name');
    const clientCode = button.getAttribute('data-client-code');
    const clientContent = button.getAttribute('data-client-content');
    
    // 모달 내부의 input 및 textarea 요소를 선택합니다.
    const modalClientNameInput = clientUpdateModal.querySelector('#clientName');
    const modalClientCodeInput = clientUpdateModal.querySelector('#clientCode');
    const modalClientContentTextarea = clientUpdateModal.querySelector('#clientContent');
    
    // input 요소의 값을 설정합니다.
    modalClientNameInput.value = clientName;
    modalClientCodeInput.value = clientCode;
    modalClientContentTextarea.value = clientContent;

    // 추가로 hidden input 필드에도 값을 설정합니다.
    const hiddenClientNameInput = clientUpdateModal.querySelector('#hiddenClientName');
    const hiddenClientContentInput = clientUpdateModal.querySelector('#hiddenClientContent');

    hiddenClientNameInput.value = clientName;
    hiddenClientContentInput.value = clientContent;
  });
});
