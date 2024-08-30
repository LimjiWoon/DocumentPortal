package com.myservlet;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.document.DocumentDAO;
import com.myclass.XSSEscape;


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
		if (filePart != null && filePart.getSize() > 0) {
		    fileName = XSSEscape.changeCategoryName(getFileName(filePart));
		}
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String originCategoryCode = XSSEscape.isNumber(request.getParameter("originCategoryCode"));
		String originFileName = XSSEscape.changeCategoryName(request.getParameter("originFileName"));
		String categoryRoot = documentDAO.getRoot(categoryCode);
		String fileNameToCheck = null;

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
		
		// 공통 처리: 파일 경로 설정 및 파일 존재 여부 확인
		String filePath = getServletContext().getRealPath(File.separator + categoryRoot + File.separator + fileNameToCheck);
		File file = new File(filePath);
		
		//각각 상황에 따른 덮어 쓰기 실행 여부 확인
		if (originCategoryCode != null && originCategoryCode.equals(categoryCode)) {
			if(fileName == null) {
			    response.getWriter().write("false");
			} else {
				if (originFileName.equals(fileName)) {
				    response.getWriter().write("false");
				} else if (file.exists()) {
				    response.getWriter().write("true");
				} else {
				    response.getWriter().write("false");
				}
			}
		} else if (file.exists()) {
		    response.getWriter().write("true");
		} else {
		    response.getWriter().write("false");
		}
	}
	
    // 파일명 추출 헬퍼 메서드
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
