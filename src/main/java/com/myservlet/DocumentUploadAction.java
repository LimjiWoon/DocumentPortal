package com.myservlet;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.MultipartRequest;
import com.user.UserDTO;
import com.category.*;
import com.client.*;
import com.document.*;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class DocumentUploadAction
 */
@WebServlet("/DocumentUpload")
public class DocumentUploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentUploadAction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
 // 허용된 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "pdf", "ppt", "pptx", "xls", "xlsx", "xml", 
        "csv", "hwp", "hwpx", "docx", "txt", "zip"
    );

    public boolean isAllowedExtension(String fileName) {
        // 파일 확장자 추출
        String extension = "";

        // 파일명에서 마지막 '.' 이후의 확장자 추출
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        // 확장자가 허용된 목록에 있는지 확인
        return ALLOWED_EXTENSIONS.contains(extension);
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
		
        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
        
        // 요청의 Content-Type 확인
        String contentType = request.getContentType();

        // multipart/form-data 여부 확인
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/form-data")) {
            // multipart/form-data 처리
    		int maxSize = 1024 * 1024 * 100;
    		String encoding = "UTF-8";
    		String folderPath = getServletContext().getRealPath("/upload/");

            // 1. 업로드용 폴더가 실제로 있는지 확인하고 내부 파일을 전부 삭제하는 과정
    		// 만약 이 과정에서 폴더가 없으면 누군가 서버 내부 파일을 건들인 것으로 종료한다.
    		try {
        		File folder = new File(folderPath);

                if (folder.exists() && folder.isDirectory()) {
                    if (folder.listFiles() != null) {
                    	for (File folderFile : folder.listFiles()) {
                    		folderFile.delete();
                    	}
                    }
                }
    		} catch (Exception e) {
		        request.setAttribute("errorMessage", "서버 폴더 에러");
			    request.getRequestDispatcher("Error.jsp").forward(request, response);
				return;
    		}
                
            // 2. 실제로 파일을 업로드하고 결과에 따라 처리하는 과정
    		try {
    			MultipartRequest multipartRequest
    				= new MultipartRequest(request, folderPath, maxSize, encoding,
    					new DefaultFileRenamePolicy());
    	        DocumentDAO documentDAO = new DocumentDAO();
        		ClientDAO clientDAO= new ClientDAO();
    			String documentName = XSSEscape.changeCategoryName(multipartRequest.getParameter("documentName"));
    			String categoryCode = XSSEscape.isNumber(multipartRequest.getParameter("categoryCode"));
    			String clientCode = XSSEscape.isClientCode(multipartRequest.getParameter("clientCode"));
    			String clientName = null;
    			String fileContent = XSSEscape.escapeHtml(multipartRequest.getParameter("fileContent"));
    			String categoryRoot = documentDAO.getRoot(categoryCode);
    			int result;
    			
    			//만약 파일 외 같이 받은 값이 이상하다면 파일을 삭제하고 동작을 중단시킨다.
    			//또한 업로드할 경로가 존재하지 않는다면 마찬가지로 동작을 중단시킨다.
    			if (documentName == null || categoryCode  == null || categoryRoot == null) {
    				deleteFile(multipartRequest.getFile("fileName"));
    		        request.setAttribute("errorMessage", "비정상적인 접근");
    			    request.getRequestDispatcher("Error.jsp").forward(request, response);
            		clientDAO.clientClose();
            		documentDAO.documentClose();
    				return;
    			}
    			
    			//clientCode에 맞는 정보가 있는지 확인하고 지정한다.
    			//없으면 기타에 넣는다.
    			if (clientCode != null) {
    				ClientDTO client = clientDAO.getClientInfo(Integer.parseInt(clientCode));
    				if (client != null) {
    					clientName = client.getClientName();
    				}
    			}
    			
    			//업로드한 파일의 정보를 가져온다
    			Enumeration<?> files = multipartRequest.getFileNames();
    			String fileName = "";
    			String orgFileName = "";
    			
    			while(files.hasMoreElements()) {
    				String file = (String)files.nextElement();
    				fileName =  multipartRequest.getFilesystemName(file);			
    				orgFileName =  multipartRequest.getOriginalFileName(file);
    			}

    			//허용되지 않은 확장자가 올라왔을 경우
    			if (!isAllowedExtension(fileName)) {
    				deleteFile(multipartRequest.getFile("fileName"));
    		        request.setAttribute("errorMessage", "허용되지 않은 확장자가 업로드 되었습니다.");
    			    request.getRequestDispatcher("Error.jsp").forward(request, response);
            		clientDAO.clientClose();
            		documentDAO.documentClose();
    				return;
    			}
    			
    			//만에 하나 파일명과 실제 업로드된 파일 명이 다르면 error
    			if (!fileName.equals(orgFileName)) {
    				deleteFile(multipartRequest.getFile("fileName"));
    		        request.setAttribute("errorMessage", "업로드에 문제가 생겼습니다.");
    			    request.getRequestDispatcher("Error.jsp").forward(request, response);
            		clientDAO.clientClose();
            		documentDAO.documentClose();
    				return;
    			}

    			
    			//이제 실제 경로에 파일 같은 이름의 파일이 있는지 확인한다.
    			String movePath = getServletContext().getRealPath(File.separator + categoryRoot + File.separator + clientName + "/");
    			File folder = new File(movePath);
    			
    			//고객사명의 폴더가 없을 경우 만든다 -> 못만들었을 경우 에러
    			if(!folder.exists()) {
    				if(!folder.mkdir()) {
        				deleteFile(multipartRequest.getFile("fileName"));
        		        request.setAttribute("errorMessage", "비정상적인 접근");
        			    request.getRequestDispatcher("Error.jsp").forward(request, response);
                		clientDAO.clientClose();
                		documentDAO.documentClose();
    					return;
    				}
    			}
    			
    			//기존 업로드 경로에 파일명을 붙여 파일 경로로 만든다.
    			folderPath += fileName;
    			movePath += fileName;
    			File moveFile = new File(movePath);
    			boolean isFile = moveFile.exists();
    			if (isFile) {
    				result = documentDAO.documentUpdate(documentName, fileName, categoryCode, user.getUserCode(), clientCode, fileContent);
    			} else {
    				result = documentDAO.documentUpload(documentName, fileName, categoryCode, user.getUserCode(), clientCode, fileContent);
    			}
    			if (isFile && result == 1) {
    				deleteFile(moveFile);
    				Files.move(Paths.get(folderPath), Paths.get(movePath));
                    request.setAttribute("messageDocument", "문서 덮어쓰기 성공!");
            	    request.getRequestDispatcher("Message.jsp").forward(request, response);
            		clientDAO.clientClose();
            		documentDAO.documentClose();
            	    return;
    			} else if (result == 1){
    				Files.move(Paths.get(folderPath), Paths.get(movePath));
                    request.setAttribute("messageDocument", "문서 등록 성공!");
            	    request.getRequestDispatcher("Message.jsp").forward(request, response);
            		clientDAO.clientClose();
            		documentDAO.documentClose();
            	    return;
    			} else {
    				deleteFile(multipartRequest.getFile("fileName"));
                    request.setAttribute("messageDocument", "문서 등록 실패!");
            	    request.getRequestDispatcher("Message.jsp").forward(request, response);
            		clientDAO.clientClose();
            		documentDAO.documentClose();
            	    return;
    			}

        		
    		} catch(Exception e) {
    			e.printStackTrace();
		        request.setAttribute("errorMessage", "비정상적인 접근");
			    request.getRequestDispatcher("Error.jsp").forward(request, response);
				return;
    		}
    		
        } else {
            // 일반 form-data 처리
    	    ArrayList<CategoryDTO> category = new ArrayList<CategoryDTO>();
    	    ArrayList<ClientDTO> client = new ArrayList<ClientDTO>();
    		CategoryDAO categoryDAO= new CategoryDAO();
    		ClientDAO clientDAO= new ClientDAO();
    		
        	category = categoryDAO.getList();
        	client = clientDAO.getList();
        	
        	categoryDAO.categoryClose();
        	clientDAO.clientClose();

            request.setAttribute("category", category);
            request.setAttribute("client", client);
    	    request.getRequestDispatcher("DocumentUpload.jsp").forward(request, response);
        	
        	return;
        }
		
	}
	
	private void deleteFile(File file) {
        if (file != null)
        	file.delete();
	}

}
