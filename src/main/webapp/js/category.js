document.addEventListener('DOMContentLoaded', function() {
  // 모달이 표시될 때 실행되는 이벤트 핸들러
  const categoryUpdateModal = document.getElementById('CategoryUpdateModal');
  categoryUpdateModal.addEventListener('show.bs.modal', function (event) {
    // 이벤트를 발생시킨 버튼을 가져옵니다.
    const button = event.relatedTarget;
    
    // 버튼의 data-* 속성에서 값을 가져옵니다.
    const categoryName = button.getAttribute('data-category-name');
    const categoryCode = button.getAttribute('data-category-code');
    
    // 모달 내부의 input 및 textarea 요소를 선택합니다.
    const modalCategoryNameInput = categoryUpdateModal.querySelector('#categoryName');
    const modalCategoryCodeInput = categoryUpdateModal.querySelector('#categoryCode');
	const hiddenCategoryCodeInput = categoryUpdateModal.querySelector('#hiddenCategoryCode');
    
    // input 요소의 값을 설정합니다.
    modalCategoryNameInput.value = categoryName;
    modalCategoryCodeInput.value = categoryCode;
	hiddenCategoryCodeInput.value = categoryCode;
  });
});

function submitForm() {
  var form = document.getElementById('categoryUpdateForm');
  
  if (form.checkValidity()) {
    form.submit(); // 폼이 유효하면 제출
  } else {
    form.reportValidity(); // 폼이 유효하지 않으면 브라우저의 기본 오류 메시지 표시
  }
}