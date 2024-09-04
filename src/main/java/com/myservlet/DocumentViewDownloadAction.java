package com.myservlet;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.*;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myclass.XSSEscape;
import com.document.DocumentDAO;

/**
 * Servlet implementation class DocumentViewDownloadAction
 */
@WebServlet("/DocumentViewDownload")
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");

		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
		String clientName = XSSEscape.changeClientName(request.getParameter("clientName"));
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String categoryRoot = "";
		String folderPath = getServletContext().getRealPath("");
		String[] checkedDocumentCodes = request.getParameterValues("checkedDocumentCode");
		DocumentDAO documentDAO = new DocumentDAO();
		
		//다중 다운로드 할 때
		if (checkedDocumentCodes != null) {
			//1. 파일 경로 List 설정
			ArrayList<String> filePaths = new ArrayList<String>();
			String[] rootAndName;
			for (String code: checkedDocumentCodes) {
				rootAndName = XSSEscape.changeDocumentDownload(code);
				if (rootAndName != null) {
					categoryCode = XSSEscape.isNumber(rootAndName[0]);
					fileName = XSSEscape.changeCategoryName(rootAndName[1]);
					categoryRoot = documentDAO.getRoot(categoryCode);
					if(categoryCode != null && fileName != null && categoryRoot != null)
						filePaths.add(folderPath + File.separator + categoryRoot + File.separator + fileName);
				}
			}
			
	        // 2. ZIP 파일명 설정
	        String zipFileName = "download.zip";
	        response.setContentType("application/zip");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

	        // 3. ZIP 파일 생성 및 파일 추가
	        try (ServletOutputStream servletOutputStream = response.getOutputStream();
	             ZipOutputStream zipOutputStream = new ZipOutputStream(servletOutputStream)) {

	            for (String filePath : filePaths) {
	                File file = new File(filePath);
	                if (!file.exists()){
	                	continue;
	                }
	                try (FileInputStream fileInputStream = new FileInputStream(file)) {
	                    // 4. ZipEntry 생성 및 추가
	                    ZipEntry zipEntry = new ZipEntry(file.getName());
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
	            e.printStackTrace();
	        }
			return;
	    }
			
		
		// 단일 파일 다운로드 할 때 파일이 이상할 경우
		categoryRoot = documentDAO.getRoot(categoryCode);
		
		if (fileName == null || categoryCode== null || categoryRoot == null || clientName == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
		
		File file = new File(folderPath + File.separator + categoryRoot + File.separator + clientName + File.separator + fileName);
		
		if (!file.exists()) {
	        request.setAttribute("errorMessage", "파일이 없습니다.");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
        }
		
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
		
		byte b[] = new byte[1024];
		int data = 0;
		
		while ((data = (fileInputStream.read(b, 0, b.length))) != -1) {
			servletOutputStream.write(b, 0, data);
		}
		
		servletOutputStream.flush();
		servletOutputStream.close();
		fileInputStream.close();
		
	}

}
