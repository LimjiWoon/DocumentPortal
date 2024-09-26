package com.myservlet;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myclass.XSSEscape;
import com.user.UserDTO;
import com.document.*;
import com.log.LogDAO;

/**
 * Servlet implementation class DocumentViewDownloadAction
 */
@WebServlet("/DocumentDownload")
public class DocumentDownloadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentDownloadAction() {
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


		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		
		//사용자 권한 확인
		if (user == null || !user.isDocument()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			return;
		}
		

	    String searchField = XSSEscape.changeDocumentField(request.getParameter("searchField"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    String filterCategory = XSSEscape.isNumber(request.getParameter("filterCategory"));
	    String filterClient = XSSEscape.isNumber(request.getParameter("filterClient"));

		DocumentDAO documentDAO = new DocumentDAO();
	    ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
		
	    
	    //0. 파일 정보 List를 생성
	    if (searchField != null && searchText != null){
		    list = documentDAO.getDownload(startDate, endDate, filterCategory, filterClient, searchField, searchText);
	    }
	    else {
		    list = documentDAO.getDownload(startDate, endDate, filterCategory, filterClient, null, null);
	    }
		
		
		//1. 파일 경로 List 설정
		ArrayList<String> filePaths = new ArrayList<String>();
		LogDAO logDAO = new LogDAO();
		for (DocumentDTO document: list) {
			String categoryCode = Integer.toString(document.getCategoryCode());
			String clientName = document.getClientName();
			String fileName = document.getFileName();
			String categoryRoot = documentDAO.getRoot(categoryCode);
			if(categoryCode != null && fileName != null && categoryRoot != null || clientName == null) {
				filePaths.add(getServletContext().getRealPath(categoryRoot) + File.separator + clientName + File.separator + fileName);
				logDAO.logUpload(user.getUserCode(), fileName, "client", "download", categoryCode+ "/" + document.getClientCode() + " 의 문서 다운로드");
		
			}
		}
		
		logDAO.logClose();
		
        // 2. ZIP 파일명 설정
        String zipFileName = "download.zip";
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

        // 3. ZIP 파일 생성 및 파일 추가
        try (ServletOutputStream servletOutputStream = response.getOutputStream();
        	     ZipOutputStream zipOutputStream = new ZipOutputStream(servletOutputStream)) {

        	    Set<String> existingFileNames = new HashSet<>(); // 중복 체크를 위한 Set 생성

        	    for (String filePath : filePaths) {
        	        File file = new File(filePath);
        			
        	        if (!file.exists()){
        	            continue;
        	        }

        	        try (FileInputStream fileInputStream = new FileInputStream(file)) {
        	            // 4. ZipEntry 생성 및 중복 이름 처리
        	            String originalFileName = file.getName();
        	            String zipEntryName = originalFileName;

        	            int count = 1;
        	            // 파일 이름이 중복되면 숫자를 추가
        	            while (existingFileNames.contains(zipEntryName)) {
        	                String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        	                String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        	                zipEntryName = baseName + "(" + count + ")" + extension;
        	                count++;
        	            }

        	            existingFileNames.add(zipEntryName); // 현재 이름을 Set에 추가

        	            // ZipEntry 생성 및 추가
        	            ZipEntry zipEntry = new ZipEntry(zipEntryName);
        	            zipOutputStream.putNextEntry(zipEntry);

        	            // 5. 파일 데이터를 읽어서 ZipOutputStream에 쓰기
        	            byte[] buffer = new byte[1024];
        	            int bytesRead;
        	            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        	                zipOutputStream.write(buffer, 0, bytesRead);
        	            }

        	            zipOutputStream.closeEntry();
        	        }
        	    }

            zipOutputStream.finish(); // ZIP 파일 완성

        } catch (IOException e) {
            // 에러 처리
        	documentDAO.errorLogUpload(e);
        }
        documentDAO.documentClose();
		return;
	    
		
		
	}

}
