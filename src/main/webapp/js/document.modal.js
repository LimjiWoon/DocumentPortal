document.addEventListener('DOMContentLoaded', function() {
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

// select가 변경될 때 호출되는 함수
function handleSelectChange(event) {
    const selectedValue = event.target.value;

    // 값이 비어 있지 않을 때만 모달을 띄웁니다.
    if (selectedValue) {
        // 선택된 값으로 모달을 호출하는 함수 실행
        fetchModalSelect(selectedValue);
    }
}


// select용 모달창 띄우기
function fetchModalSelect(dataType) {
    fetch('Modal', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'dataType=' + encodeURIComponent(dataType) + '&selectType=2'
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
