const startDateInput = document.getElementById('startDate');
const endDateInput = document.getElementById('endDate');


// 날짜 입력 시 유효성 검사
startDateInput.addEventListener('change', validateDates);
endDateInput.addEventListener('change', validateDates);

function validateDates() {
    const startDateValue = startDateInput.value;
    const endDateValue = endDateInput.value;

    if (startDateValue && endDateValue && startDateValue > endDateValue) {
        alert('시작 날짜는 종료 날짜보다 이전이거나 같아야 합니다.');
        endDateInput.value = startDateValue; // 종료 날짜를 시작 날짜와 동일하게 설정
    }
}

// 폼 제출 시 유효성 검사
document.getElementById('search').addEventListener('submit', function(event) {
    const startDateValue = startDateInput.value;
    const endDateValue = endDateInput.value;

    if (startDateValue > endDateValue) {
        alert('시작 날짜는 종료 날짜보다 이전이거나 같아야 합니다.');
        event.preventDefault(); // 폼 제출 방지
    }
});