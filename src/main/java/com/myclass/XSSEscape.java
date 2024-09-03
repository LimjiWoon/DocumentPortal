package com.myclass;

public class XSSEscape {
	public static String escapeHtml(String input) {
        if (input == null) return null;
        
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;")
                    .replace("%", "&#37;");
    }
	
	
	//사용자 아이디 비밀번호 검증
	public static String checkID(String input) {
	    if (input == null) return null;
	    if (input.matches("[A-Za-z0-9]{1,20}")) {
	        return input;
	    }
	    
	    return null;
	}
	
	public static String checkPassword(String input) {
		if (input == null) return null;
		if (input.length() <= 20)
			return escapeHtml(input);
		
		return null;
	}
	
	//아래는 사용자 검색을 위한 input을 검증하고 복원하기 위한 메소드 들이다.
	public static String changeUserField(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "userName";
		case "2":
			return "userCode";
		default:
			return null;
		}
    }
	
	public static String restoreUserField(String input) {
        if (input == null) return null;

		switch(input) {
		case "userName":
			return "1";
		case "userCode":
			return "2";
		default:
			return null;
		}
    }
	
	public static String changeOrder(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "ASC";
		case "2":
			return "DESC";
		default:
			return null;
		}
    }
	
	//공용 검색 속성
	public static String changeText(String input) {
        if (input == null) return null;
        if (input.trim() == "") return "";
		
		return escapeHtml(input);
    }
	
	public static String restoreOrder(String input) {
        if (input == null) return null;
        
		switch(input) {
		case "ASC":
			return "1";
		case "DESC":
			return "2";
		default:
			return null;
		}
	}
	
	public static String changeChangeDate(String input) {
        if (input == null) return "0";

		switch(input) {
		case "1":
			return "1";
		case "2":
			return "2";
		case "3":
			return "3";
		default:
			return "0";
		}
	}
	
	//사용자 등록을 위한 XSS 검증 메소드
	public static String changePermisson(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "1";
		case "0":
			return "0";
		default:
			return null;
		}
    }

	public static String changeUserCode(String input) {
        if (input == null) return null;
        if (input.equals("0")) return null;
        if (!input.matches("^[0-9]{1,6}$")) return null;
		
		return escapeHtml(input);
    }
	

	public static String changeUserID(String input) {
        if (input == null) return null;
        if (!input.matches("^[a-zA-Z0-9]{1,20}$")) return null;
		
		return escapeHtml(input);
    }
	
	public static String changeUserName(String input) {
        if (input == null) return null;
		if (!input.matches("^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s.\\-']{1,20}$")) return null;
		
		return escapeHtml(input);
    }
	
	public static String changeUserPassword(String input) {
		PasswordLogic logic = new PasswordLogic();
        if (input == null) return null;
        if (input.length() == 64) {
        	if (input.matches("^[A-Fa-f0-9]{64}$")) {
        	    return input;
        	}
        } else {
            if (!input.matches("^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[~?!@#$%^&*_-]).{10,20}$")) {
                return null;
            }
        }
		return logic.getSHA256(escapeHtml(input));
    }
	
	
	//고객사 검색 검증을 위한 메소드
	public static String changeClientName(String input) {
        if (input == null) return null;
        if (!input.matches("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s()]{1,25}$")) {
            return null;
        }
		
		return escapeHtml(input);
    }
	
	public static String changeClientField(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "c.clientCode";
		case "2":
			return "c.clientName";
		case "4":
			return "u.userName";
		case "5":
			return "c.isUse";
		case "6":
			return "c.dateOfUpdate";
		default:
			return null;
		}
    }
	
	public static String restoreClientField(String input) {
        if (input == null) return null;

		switch(input) {
		case "c.clientCode":
			return "1";
		case "c.clientName":
			return "2";
		case "u.userName":
			return "4";
		case "c.isUse":
			return "5";
		case "c.dateOfUpdate":
			return "6";
		default:
			return null;
		}
    }
	
	//문서 목록 검색 검증을 위한 메소드
	public static String changeCategoryField(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "cat.categoryCode";
		case "2":
			return "cat.categoryName";
		case "3":
			return "u.userName";
		case "4":
			return "cat.dateOfCreate";
		default:
			return null;
		}
    }
	
	public static String restoreCategoryField(String input) {
        if (input == null) return null;

		switch(input) {
		case "cat.categoryCode":
			return "1";
		case "cat.categoryName":
			return "2";
		case "u.userName":
			return "3";
		case "cat.dateOfCreate":
			return "4";
		default:
			return null;
		}
    }
	
	public static String isNumber(String input) {
	    if (input == null) return null;
	    if (!input.matches("^[0-9]+$")) return null;
	    
	    return input;
	}


	public static String changeCategoryName(String input) {
	    if (input == null) return null;
	    if (input.matches(".*[\\\\/:*?\"<>|].*")) {
	        return null;
	    }
	    
	    return escapeHtml(input.trim());
	}

	public static String changeCategoryRoot(String input) {
	    if (input == null) return null;
	    if (input.matches(".*[\\\\:*?\"<>|].*")) {
	        return null;
	    }
	    
	    return escapeHtml(input.trim());
	}

	public static String[] changeDocumentDownload(String input) {
	    if (input == null) return null;
	    if (input.matches(".*[\\\\:*?\"<>|].*")) {
	        return null;
	    }
	    if (!input.matches("^(?:[^/]*(/[^/]*)?)?$")) {
	    	return null;
	    }
	    String now = escapeHtml(input);
	    
	    return now.trim().split("/");
	}
	
	public static boolean isTrue(String input) {
		if (input == null || input == "") return false;
		
		return true;
	}
	
	//문서 검색 검증을 위한 메소드
	public static String changeDocumentField(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "f.fileTitle";
		case "2":
			return "c.clientName";
		case "3":
			return "cat.categoryName";
		case "4":
			return "u.userName";
		case "5":
			return "f.dateOfUpdate";
		default:
			return null;
		}
    }
	
	public static String restoreDocumentField(String input) {
        if (input == null) return null;

		switch(input) {
		case "f.fileTitle":
			return "1";
		case "c.clientName":
			return "2";
		case "cat.categoryName":
			return "3";
		case "u.userName":
			return "4";
		case "f.dateOfUpdate":
			return "5";
		default:
			return null;
		}
    }
	
	public static String checkDate(String input) {
	    if (input == null) return null;
	    // yyyy-mm-dd 형식을 검출하는 정규 표현식
	    if (!input.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) return null;
	    
	    return input;
	}
	
	public static String changeLogHow(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "create";
		case "2":
			return "update";
		case "3":
			return "delete";
		default:
			return null;
		}
    }
	
	public static String restoreLogHow(String input) {
        if (input == null) return null;

		switch(input) {
		case "create":
			return "1";
		case "update":
			return "2";
		case "delete":
			return "3";
		default:
			return null;
		}
    }
	
	public static String changeLogWhere(String input) {
        if (input == null) return null;

		switch(input) {
		case "1":
			return "client";
		case "2":
			return "category";
		case "3":
			return "file";
		default:
			return null;
		}
    }
	
	public static String restoreLogWhere(String input) {
        if (input == null) return null;

		switch(input) {
		case "client":
			return "1";
		case "category":
			return "2";
		case "file":
			return "3";
		default:
			return null;
		}
    }

	
}