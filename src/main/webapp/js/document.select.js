$(document).ready(function() {

	$('#categoryCode').select2({
	    theme: "bootstrap-5",
	    width: '100%',
	    placeholder: '문서 목록 선택',
		allowClear: true,
		language: {
		            noResults: function() {
		                return "검색 결과가 없습니다.";
		            }
		        }
	});
	
	$('#clientCode').select2({
	    theme: "bootstrap-5",
	    width: '100%',
	    placeholder: '고객사 선택',
		allowClear: true,
		language: {
		            noResults: function() {
		                return "검색 결과가 없습니다.";
		            }
		        }
	});

	$('#filterCategory').select2({
	    theme: "bootstrap-5",
	    width: '100%',
	    placeholder: '문서 목록 선택',
		allowClear: true,
		dropdownParent: $('#SearchFilterModal'),
		language: {
		            noResults: function() {
		                return "검색 결과가 없습니다.";
		            }
		        }
	});

	$('#filterClient').select2({
	    theme: "bootstrap-5",
	    width: '100%',
	    placeholder: '고객사 선택',
		allowClear: true,
		dropdownParent: $('#SearchFilterModal'),
		language: {
		            noResults: function() {
		                return "검색 결과가 없습니다.";
		            }
		        }
	});

	
	$('#filterDownloadCategory').select2({
	    theme: "bootstrap-5",
	    width: '100%',
	    placeholder: '문서 목록 선택',
		allowClear: true,
		dropdownParent: $('#DownloadFilterModal'),
		language: {
		            noResults: function() {
		                return "검색 결과가 없습니다.";
		            }
		        }
	});
	
	$('#filterDownloadClient').select2({
	    theme: "bootstrap-5",
	    width: '100%',
	    placeholder: '고객사 선택',
	    allowClear: true,
		dropdownParent: $('#DownloadFilterModal'),
		language: {
		            noResults: function() {
		                return "검색 결과가 없습니다.";
		            }
		        }
	});
	
});