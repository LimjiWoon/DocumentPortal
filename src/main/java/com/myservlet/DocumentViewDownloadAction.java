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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myclass.XSSEscape;
import com.user.UserDTO;
import com.client.ClientDAO;
import com.document.DocumentDAO;
import com.log.LogDAO;

/**
 * Servlet implementation class DocumentViewDownloadAction
 */
public class DocumentViewDownloadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentViewDownloadAction() {
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
		
		
		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
		String clientName = XSSEscape.changeClientName(request.getParameter("clientName"));
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String categoryRoot = "";
		String folderPath = getServletContext().getRealPath("");
		String[] checkedDocumentCodes = request.getParameterValues("checkedDocumentCode");
		DocumentDAO documentDAO = new DocumentDAO();
		
		// 1. 다중 다운로드 할 때
		if (checkedDocumentCodes != null) {
			// 1-1. 파일 경로 List 설정
			ArrayList<String> filePaths = new ArrayList<String>();
			LogDAO logDAO = new LogDAO();
			ClientDAO clientDAO = new ClientDAO();
			String[] rootAndName;
			for (String code: checkedDocumentCodes) {
				rootAndName = XSSEscape.changeDocumentDownload(code);
				if (rootAndName != null) {
					categoryCode = XSSEscape.isNumber(rootAndName[0]);
					clientName = XSSEscape.changeClientName(rootAndName[1]);
					fileName = XSSEscape.changeCategoryName(rootAndName[2]);
					categoryRoot = documentDAO.getRoot(categoryCode);
					if(categoryCode != null && fileName != null && categoryRoot != null || clientName == null) {
						filePaths.add(folderPath + File.separator + categoryRoot + File.separator + clientName + File.separator + fileName);
						logDAO.logUpload(user.getUserCode(), fileName, "client", "download", categoryCode+ "/" + clientDAO.getClientCode(clientName) + " 의 문서 다운로드");
					}
						
					
				}
			}
			
			logDAO.logClose();
			clientDAO.clientClose();
			
	        // 1-2. ZIP 파일명 설정
	        String zipFileName = "download.zip";
	        response.setContentType("application/zip");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

	        // 1-3. ZIP 파일 생성 및 파일 추가
	        try (ServletOutputStream servletOutputStream = response.getOutputStream();
	        	     ZipOutputStream zipOutputStream = new ZipOutputStream(servletOutputStream)) {

	        	    Set<String> existingFileNames = new HashSet<>(); // 중복 체크를 위한 Set 생성

	        	    for (String filePath : filePaths) {
	        	        File file = new File(filePath);
	        	        if (!file.exists()){
	        	            continue;
	        	        }

	        	        try (FileInputStream fileInputStream = new FileInputStream(file)) {
	        	            // 1-4. ZipEntry 생성 및 중복 이름 처리
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

	        	            // 1-5. 파일 데이터를 읽어서 ZipOutputStream에 쓰기
	        	            byte[] buffer = new byte[1024];
	        	            int bytesRead;
	        	            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
	        	                zipOutputStream.write(buffer, 0, bytesRead);
	        	            }

	        	            zipOutputStream.closeEntry();
	        	        }
	        	    }

	            zipOutputStream.finish(); // 1-6. ZIP 파일 완성 후 다운로드

	        } catch (IOException e) {
	        	documentDAO.errorLogUpload(e);
	        }
	        documentDAO.documentClose();
			return;
	    }
			
		
		// 2. 단일 파일 다운로드 할 때 파일이 이상할 경우
		categoryRoot = documentDAO.getRoot(categoryCode);
		
		// 입력값 검증
		if (fileName == null || categoryCode== null || categoryRoot == null || clientName == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
	        documentDAO.documentClose();
			return;
		}
		
		// 2-1. 파일 경로 설정
		File file = new File(folderPath + File.separator + categoryRoot + File.separator + clientName + File.separator + fileName);
		
		// 2-2. 파일 존재 확인
		if (!file.exists()) {
	        request.setAttribute("errorMessage", "파일이 없습니다.");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
	        documentDAO.documentClose();
			return;
        }
		
		// 2-3. 파일 다운로드 설정
		String mineType = getServletContext().getMimeType(file.toString());
		if(mineType == null) {
			response.setContentType("application/octet-stream");
		}
		
		String downloadName = null;
		if (request.getHeader("user-agent").indexOf("MSIE") == -1) {
			downloadName = new String(fileName.getBytes("UTF-8"), "8859_1");
		} else {
			downloadName = new String(fileName.getBytes("EUK-KR"), "8859_1");
		}
		
		response.setHeader("Content-Disposition", "attachment; filename=\""
					+ downloadName + "\";");
		
		FileInputStream fileInputStream = new FileInputStream(file);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		LogDAO logDAO = new LogDAO();
		ClientDAO clientDAO = new ClientDAO();
		
		//로그에 기록
		logDAO.logUpload(user.getUserCode(), fileName, "client", "download", categoryCode+ "/" + clientDAO.getClientCode(clientName) + " 의 문서 다운로드");
		
		logDAO.logClose();
		clientDAO.clientClose();
		
		byte b[] = new byte[1024];
		int data = 0;
		
		// 2-4. 파일 다운로드
		while ((data = (fileInputStream.read(b, 0, b.length))) != -1) {
			servletOutputStream.write(b, 0, data);
		}

        documentDAO.documentClose();
		servletOutputStream.flush();
		servletOutputStream.close();
		fileInputStream.close();
		
	}

}
