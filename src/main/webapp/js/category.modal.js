document.addEventListener('DOMContentLoaded', function() {
	document.querySelectorAll('button[data-bs-toggle="modal"][data-type]').forEach(button => {
	    button.addEventListener('click', handleButtonClick);
	});
	
    // 모달이 닫힐 때 내용 제거
    document.getElementById('modalContainer').addEventListener('hidden.bs.modal', function () {
        this.innerHTML = ''; // 모달 내용 제거
    });
});

// button 클릭 시 호출되는 함수
function handleButtonClick(event) {
    const dataType = this.getAttribute('data-type');
    fetchModalButton(dataType);
}

// button용 모달창 띄우기
function fetchModalButton(dataType) {
    fetch('Modal', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'dataType=' + encodeURIComponent(dataType) + '&selectType=0'
    })
    .then(response => response.text())
    .then(html => {
        const modalContainer = document.getElementById('modalContainer');

        // 이전 모달 내용 제거
        modalContainer.innerHTML = html;

        // 새로 생성된 모달을 초기화하고 표시
        const newModal = new bootstrap.Modal(modalContainer.querySelector('.modal'));
        newModal.show();
    })
    .catch(error => console.error('Error:', error));
}



function check(){
	let categoryName = document.forms["CategoryUpload"]["categoryName"];
	
	let reg = /[\\/:*?"<>|]/;

	if(reg.test(categoryName.value)){
		alert("문서 목록 명에  \\  /  :  *  ?  \"  <  >  | 가 들어갈 수 없습니다.");
		categoryName.focus();
		return false;
	}
	
	return true;
}

function checkCategoryName(event) {
    var pattern = /[\\/:*?"<>|]/g; // 허용되지 않는 문자 패턴
    var currentValue = event.target.value;

    // 허용된 문자를 제외한 모든 문자를 제거
    if (pattern.test(currentValue)) {
        event.target.value = currentValue.replace(pattern, '');
    }
}
