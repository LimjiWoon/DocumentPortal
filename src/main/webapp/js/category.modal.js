document.addEventListener('DOMContentLoaded', function() {
    // 모든 버튼에 click 이벤트 리스너를 한 번만 등록합니다.
    document.querySelectorAll('button[data-bs-toggle="modal"]').forEach(button => {
        button.addEventListener('click', handleButtonClick);
    });

    // 모달이 닫힐 때 내용 제거
    document.getElementById('modalContainer').addEventListener('hidden.bs.modal', function () {
        this.innerHTML = ''; // 모달 내용 제거
    });
	
	// select 요소의 변경을 감지하여 모달 띄우기
	const selectElement = document.getElementById('ChangeSelect');
	if (selectElement) {
	    selectElement.addEventListener('change', handleSelectChange);
	}
});

// 모달의 부모 요소에 이벤트 리스너 등록
document.addEventListener('click', function(event) {
    // 이벤트가 발생한 요소가 'modalValue'라는 ID를 가진 요소인지 확인
    if (event.target && event.target.id === 'modalValue') {
        // data-* 속성에서 값 가져오기
        const buttonValue = event.target.dataset.value;
        const buttonName = event.target.dataset.name;
        const buttonLv = event.target.dataset.lv;

        // 부모 페이지의 특정 input 값 변경
        document.getElementById('categoryCode').value = buttonValue;
        document.getElementById('nowCategoryName').value = buttonName;
        document.getElementById('categoryLv').value = buttonLv;
		
		const modalElement = document.getElementById('CategoryModal'); // 모달의 ID
		const modalInstance = bootstrap.Modal.getInstance(modalElement); // 모달 인스턴스 가져오기 또는 생성
		modalInstance.hide(); // 모달 닫기
    }
});


// select가 변경될 때 호출되는 함수
function handleSelectChange(event) {
    const selectedValue = event.target.value;

    // 값이 비어 있지 않을 때만 모달을 띄웁니다.
    if (selectedValue) {
        // 선택된 값으로 모달을 호출하는 함수 실행
        fetchModalSelect(selectedValue);
    }
}

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

// select용 모달창 띄우기
function fetchModalSelect(dataType) {
    fetch('Modal', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'dataType=' + encodeURIComponent(dataType) + '&selectType=1'
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
