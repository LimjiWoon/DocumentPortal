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
import com.client.*;
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
        ClientDAO clientDAO = new ClientDAO();
        Part filePart = request.getPart("fileName");
		String fileName = null;
		if (filePart != null && filePart.getSize() >= 0) {
		    fileName = XSSEscape.changeCategoryName(getFileName(filePart));
		}
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String originCategoryCode = XSSEscape.isNumber(request.getParameter("originCategoryCode"));
		String clientCode = XSSEscape.isNumber(request.getParameter("clientCode"));
		String originClientCode = XSSEscape.isNumber(request.getParameter("originClientCode"));
		String originFileName = XSSEscape.changeCategoryName(request.getParameter("originFileName"));
		String fileNameToCheck = null;
		String clientName = null;
		String originCategoryName = null;
		ClientDTO client;

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

		//clientCode 처리
		if (clientCode != null) {
			client = clientDAO.getClientInfo(Integer.parseInt(clientCode));
			if (client != null) {
				clientName = client.getClientName();
			}
		}
		
		//originClientCode 처리
		if (originClientCode != null) {
			client = clientDAO.getClientInfo(Integer.parseInt(originClientCode));
			if (client != null) {
				originCategoryName = client.getClientName();
			}
		}
		
		// 공통 처리: 파일 경로 설정 및 파일 존재 여부 확인
		String filePath = getServletContext().getRealPath(File.separator + documentDAO.getRoot(categoryCode) + File.separator + clientName + File.separator + fileName);
		String fileOriginalPath = getServletContext().getRealPath(File.separator + documentDAO.getRoot(originCategoryCode) + File.separator + originCategoryName + File.separator + fileNameToCheck);
		File file = new File(filePath);

		System.out.println("fileName: " + fileName);
		System.out.println("categoryCode: " + categoryCode);
		System.out.println("originCategoryCode: " + originCategoryCode);
		System.out.println("clientCode: " + clientCode);
		System.out.println("originClientCode: " + originClientCode);
		System.out.println("originFileName: " + originFileName);
		System.out.println("fileNameToCheck: " + fileNameToCheck);
		System.out.println("clientName: " + clientName);
		System.out.println("originCategoryName: " + originCategoryName);
		System.out.println("filePath: " + filePath);
		System.out.println("fileOriginalPath: " + fileOriginalPath);
		
		//각각 상황에 따른 덮어 쓰기 실행 여부 확인
		if (filePath.equals(fileOriginalPath)) {
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
