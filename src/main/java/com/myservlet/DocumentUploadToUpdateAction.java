package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.document.DocumentDAO;
import com.myclass.XSSEscape;
import com.user.UserDTO;


/**
 * Servlet implementation class DocumentUploadToUpdate
 */
@WebServlet("/DocumentUpToUp")
@MultipartConfig
public class DocumentUploadToUpdateAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentUploadToUpdateAction() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 직접 url을 타이핑하여 접근하는 것을 차단한다 -> 오로지 동작을 위한 servlet임
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null) {
	        request.setAttribute("errorMessage", "로그인을 해주세요.");
		} else {
	        request.setAttribute("errorMessage", "Url을 직접 입력하여 들어올 수 없습니다.");
		}
	    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
	}

  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        DocumentDAO documentDAO = new DocumentDAO();
        Part filePart = request.getPart("fileName");
		String fileName = null;
		if (filePart != null && filePart.getSize() >= 0) {
		    fileName = XSSEscape.changeCategoryName(getFileName(filePart));
		}
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String clientCode = XSSEscape.isClientCode(request.getParameter("clientCode"));
		String originFileName = XSSEscape.changeCategoryName(request.getParameter("originFileName"));
		String fileNameToCheck = null;
		boolean pass = false;
		int result;
		
		if (request.getParameter("pass") != null) {
			pass = request.getParameter("pass").equals("1");
		}

		// 1. 원본 파일명과 원본 카테고리 코드가 없을 경우 -> upload 에서 넘어옴
		if (originFileName == null) {
		    fileNameToCheck = fileName;
		} 
		// 2. 원본 파일명과 원본 카테고리 코드가 있지만 파일이 없는 경우 -> update 에서 넘어왔지만 file upload 안함
		else if (fileName == null) {
		    fileNameToCheck = originFileName;
		} 
		// 3. 원본 파일명과 원본 카테고리 코드가 있지만 파일도 있는 경우 -> update 에서 넘어오고 file upload 도 함
		else {
		    fileNameToCheck = fileName;
		}

		result = documentDAO.documentUpdateCheck(categoryCode, clientCode, fileNameToCheck);
        documentDAO.documentClose();
		
		//각각 상황에 따른 덮어 쓰기 실행 여부 확인
		//업로드 업데이트 구분
		if (originFileName == null) {
			if (result == 1) {
		        response.getWriter().write("true");
		    } else {
		        response.getWriter().write("false");
		    }
		} else {
			if (pass) {
			    // 파일 위치를 변경하지 않고 파일을 올리거나 파일을 올리지 않은 경우
			    if (fileName == null) {
			        // 파일을 올리지 않았을 때
			        response.getWriter().write("false");
			    } else {
			        // 파일을 올리려는 경우
			        if (fileName.equals(originFileName)) {
			            response.getWriter().write("false"); // 같은 파일명으로 덮어쓸 때
			        } else {
			            response.getWriter().write(result == 1 ? "true" : "false");
			        }
			    }
			} else {
			    // 파일 위치를 변경하는 경우
			    response.getWriter().write(result == 1 ? "true" : "false");
			}


		}
		
	}
	
    // 파일명 추출 헬퍼 메서드
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) return null; // 헤더가 없는 경우에 대비

        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
