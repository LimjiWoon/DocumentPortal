function selectAll(selectAll)  {
  const checkboxes 
       = document.getElementsByName('checkedDocumentCode');
  
  checkboxes.forEach((checkbox) => {
    checkbox.checked = selectAll.checked;
  })
}


function submitCheckedDocuments(actionURL) {
    var form = document.getElementById('documentForm');
    
	if (actionURL === 'DocumentDelete') {
	    if (!confirm('정말 삭제하시겠습니까?')) {
	        return;
	    }
	}
	
    form.action = actionURL;
    
    form.submit();
}

function submitForm(action, fileName, categoryCode, clientCode) {
    var form = document.createElement('form');
    form.method = 'POST';
    form.action = action;

    var inputFileName = document.createElement('input');
    inputFileName.type = 'hidden';
    inputFileName.name = 'fileName';
    inputFileName.value = fileName;
    form.appendChild(inputFileName);

	var inputCategoryCode = document.createElement('input');
	inputCategoryCode.type = 'hidden';
	inputCategoryCode.name = 'categoryCode';
	inputCategoryCode.value = categoryCode;
	form.appendChild(inputCategoryCode);

	var inputClientCode = document.createElement('input');
	inputClientCode.type = 'hidden';
	inputClientCode.name = 'clientCode';
	inputClientCode.value = clientCode;
	form.appendChild(inputClientCode);

    document.body.appendChild(form);
    form.submit();
}


function checkAndUpload() {
	var fileInput = document.getElementById('fileName');
	var clientCode = document.getElementById('clientCode').value;
	var categoryCode = document.getElementById('categoryCode').value;

	var formData = new FormData();
	formData.append("fileName", fileInput.files[0]);
	formData.append("clientCode", clientCode);
	formData.append("categoryCode", categoryCode);

    $.ajax({
        url: 'DocumentUpToUp',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function(response) {
            if (response === 'true') {
				var confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
				confirmationModal.show();
            } else {
                $('#DocumentInfo').submit();
            }
        }
    });
}



function checkAndUpdate() {
	var fileInput = document.getElementById('fileName');
	var clientCode = document.getElementById('clientCode').value;
	var categoryCode = document.getElementById('categoryCode').value;
	var originFileName = document.getElementById('originFileName').value;
	
	var pass = ((clientCode === document.getElementById('originClientCode').value
				|| (clientCode === '미선택' && document.getElementById('originClientCode').value === '0')) 
				&& categoryCode === document.getElementById('originCategoryCode').value) ? 1 : 0;
	

	var formData = new FormData();
	formData.append("fileName", fileInput.files[0]);
	formData.append("clientCode", clientCode);
	formData.append("categoryCode", categoryCode);
	formData.append("originFileName", originFileName); 
	formData.append("pass", pass); 

    $.ajax({
        url: 'DocumentUpToUp',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function(response) {
            if (response === 'true') {
				var confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
				confirmationModal.show();
            } else {
                $('#DocumentInfo').submit();
            }
        }
    });
}

function confirmUpload(isConfirmed) {
    if (isConfirmed) {
        $('#DocumentInfo').submit();
    } else {
		var confirmationModal = bootstrap.Modal.getInstance(document.getElementById('confirmationModal'));
		confirmationModal.hide();
    }
}



function checkDocumentName(event) {
    var pattern = /[\\/:*?"<>|]/g; // 허용되지 않는 문자 패턴
    var currentValue = event.target.value;

    // 허용된 문자를 제외한 모든 문자를 제거
    if (pattern.test(currentValue)) {
        event.target.value = currentValue.replace(pattern, '');
    }
}