package com.myservlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.category.*;
import com.client.*;
import com.document.*;
import com.myclass.XSSEscape;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.user.UserDTO;


/**
 * Servlet implementation class DocumentUpdateAction
 */
@WebServlet("/DocumentUpdate")
public class DocumentUpdateAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentUpdateAction() {
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
		

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || !user.isDocument()) {
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
    		
    		// 2. 파일이 있는지 확인해본다. 없으면 기타 사항만 수정한다.
    		try {
    			MultipartRequest multipartRequest
    				= new MultipartRequest(request, folderPath, maxSize, encoding,
    					new DefaultFileRenamePolicy());
        		DocumentDAO documentDAO= new DocumentDAO();
        		ClientDAO clientDAO = new ClientDAO();
    			String fileTitle = XSSEscape.escapeHtml(multipartRequest.getParameter("documentName"));
                String fileName = multipartRequest.getFilesystemName("fileName");
    			String fileContent = XSSEscape.escapeHtml(multipartRequest.getParameter("fileContent"));
    			String clientCode = XSSEscape.isClientCode(multipartRequest.getParameter("clientCode"));
    			String categoryCode = XSSEscape.isNumber(multipartRequest.getParameter("categoryCode"));
    			String clientName = clientDAO.getClientName(clientCode);
    			String originFileTitle = XSSEscape.escapeHtml(multipartRequest.getParameter("originFileTitle"));
    			String originFileName = XSSEscape.changeCategoryName(multipartRequest.getParameter("originFileName"));
    			String originClientCode = XSSEscape.isClientCode(multipartRequest.getParameter("originClientCode"));
    			String originCategoryCode = XSSEscape.isNumber(multipartRequest.getParameter("originCategoryCode"));
    			String originFileContent = XSSEscape.escapeHtml(multipartRequest.getParameter("originFileContent"));
    			String originClientName = clientDAO.getClientName(originClientCode);
    			String categoryRoot = documentDAO.getRoot(categoryCode);
    			
    			
    			//입력된 값이 이상할 경우 종료
    			if(fileTitle == null || categoryCode == null || originFileName == null || 
    					originCategoryCode == null || categoryRoot == null) {
    				deleteFile(multipartRequest.getFile("fileName"));
    		        request.setAttribute("errorMessage", "비정상적인 접근");
    			    request.getRequestDispatcher("Error.jsp").forward(request, response);
    				return;
    			}

    			
                // 파일이 업로드되지 않은 경우의 처리 로직
                if (fileName == null) {
                	//변경된 값 아예 X
                	if (originFileTitle.equals(fileTitle) && isClientSame(originClientCode, clientCode) &&
                			originCategoryCode.equals(categoryCode) && originFileContent.equals(fileContent)) {
        		        request.setAttribute("errorMessage", "변경된 사항이 없습니다.");
        			    request.getRequestDispatcher("Error.jsp").forward(request, response);
        				return;
                	}
                	//위치가 변경이 없을 경우
                	if(isClientSame(originClientCode, clientCode) && originCategoryCode.equals(categoryCode)) {
                		if (documentDAO.documentUpdate(fileTitle, originFileName, categoryCode, user.getUserCode(), clientCode, fileContent) == 1) {
                            request.setAttribute("messageDocument", "문서 수정 성공!");
                    	    request.getRequestDispatcher("Message.jsp").forward(request, response);
                		} else {
            		        request.setAttribute("errorMessage", "문서 수정 실패!");
            			    request.getRequestDispatcher("Error.jsp").forward(request, response);
                		}
        				return;
                	}

                	//위치가 변경되었을 경우
                	String movePath = getServletContext().getRealPath(File.separator + categoryRoot + File.separator + clientName + "/");
                	folderPath = getServletContext().getRealPath(File.separator + documentDAO.getRoot(originCategoryCode) + File.separator + originClientName + File.separator + "/");
                	File uploadedFile = new File(folderPath + originFileName);
                	File moveFile = new File(movePath);
                	File deleteFile = new File(folderPath);
                		
                	
                	//만약 파일이 제대로 올라가지 않았을 경우, 옮길 위치에 폴더가 없으면 폴더를 만든다.
                	if (!uploadedFile.exists() || (!moveFile.exists() && !moveFile.mkdir())) {
        		        request.setAttribute("errorMessage", "비정상적인 접근");
        			    request.getRequestDispatcher("Error.jsp").forward(request, response);
                		return;
                	}
                	
                	//다시 제대로된 파일을 지정
                	moveFile = new File(movePath + originFileName);
                	
                	//1. 이동할 위치에 파일이 있는경우 -> 삭제 처리 (이미 클라이언트 측에 물어봄)
                	if (moveFile.exists()) {
                		if(documentDAO.documentDelete(originFileName, categoryCode, clientCode, user.getUserCode()) == 1) {
                			deleteFile(moveFile);
                		} else {
            		        request.setAttribute("errorMessage", "문서 수정 실패!");
            			    request.getRequestDispatcher("Error.jsp").forward(request, response);
                    		return;
                		}
                	}
                	
                	//2. 파일 이동시키고 DB 수정하기
        			if (documentDAO.documentUpdate(fileTitle, originFileName, categoryCode, originCategoryCode,
                			user.getUserCode(), clientCode, originClientCode, fileContent) == 1){
        				Files.move(Paths.get(folderPath + originFileName), Paths.get(movePath + originFileName));
        				if (deleteFile.isDirectory() && deleteFile.listFiles().length == 0) {
        				    deleteFile.delete();
        				}
                        request.setAttribute("messageDocument", "문서 수정 성공!");
                	    request.getRequestDispatcher("Message.jsp").forward(request, response);
                	    return;
        			} else {
                        request.setAttribute("messageDocument", "문서 수정 실패!");
                	    request.getRequestDispatcher("Message.jsp").forward(request, response);
                	    return;
        			}
                }


                // 파일이 정상적으로 업로드된 경우의 처리 로직

                //문서 업로드 시 절차 과정
                //1. 카테고리 코드, 고객사 변경 확인
                //2. 파일명 변경 확인 -> 1,2번 둘중 하나라도 변경됬으면 3번 과정 진행
                //3. 이동할 위치의 파일(move) 확인하고 DB 및 파일 삭제
                //4. 원본(origin) 파일 삭제
                //5. 올린(uploaded) 파일 이동
            	String moveFolderPath = getServletContext().getRealPath(File.separator + categoryRoot + File.separator + clientName + "/");
                String originFolderPath = getServletContext().getRealPath(File.separator + documentDAO.getRoot(originCategoryCode) + File.separator + originClientName + File.separator + "/");
                
            	
                
                //1,2번 확인 후 3번 진행 코드 
                if (!categoryCode.equals(originCategoryCode) || !isClientSame(clientCode, originClientCode) || !fileName.equals(originFileName) ) {
                	File moveFile = new File(moveFolderPath + fileName);
                	if (moveFile.exists()) {
                		if(documentDAO.documentDelete(fileName, categoryCode, clientCode, user.getUserCode()) == 1) {
                			deleteFile(moveFile);
                		} else {
            		        request.setAttribute("errorMessage", "문서 수정 실패!");
            			    request.getRequestDispatcher("Error.jsp").forward(request, response);
                    		return;
                		}
                	}
                	moveFile = new File(moveFolderPath);
                	if (!moveFile.exists() && !moveFile.mkdir()) {
        		        request.setAttribute("errorMessage", "문서 수정 실패!");
        			    request.getRequestDispatcher("Error.jsp").forward(request, response);
                		return;
                	}
                }
                

                //4,5번 진행 코드
                File originFile = new File(originFolderPath, originFileName);
                File uploadedFile = new File(folderPath, fileName);
                File deleteFile = new File(originFolderPath);
                
                
                if (originFile.exists() && uploadedFile.exists()) {
                	if (documentDAO.documentUpdate(fileTitle, fileName, originFileName, categoryCode, 
                			originCategoryCode, user.getUserCode(), clientCode, originClientCode, fileContent) == 1) {
                    	deleteFile(originFile);
                    	Files.move(Paths.get(folderPath + fileName), Paths.get(moveFolderPath + fileName));
        				if (deleteFile.isDirectory() && deleteFile.listFiles().length == 0) {
        				    deleteFile.delete();
        				}
                        request.setAttribute("messageDocument", "문서 수정 성공!");
                	    request.getRequestDispatcher("Message.jsp").forward(request, response);
                	    return;
                	}
                }

		        request.setAttribute("errorMessage", "문서 수정 실패!");
			    request.getRequestDispatcher("Error.jsp").forward(request, response);
        		return;
                
    		} catch(Exception e) {
    			e.printStackTrace();
		        request.setAttribute("errorMessage", "그냥 에러");
			    request.getRequestDispatcher("Error.jsp").forward(request, response);
				return;
    		}
    		

        } else {
            // 일반 form-data 처리
    	    ArrayList<CategoryDTO> categoryList = new ArrayList<CategoryDTO>();
    	    ArrayList<ClientDTO> client = new ArrayList<ClientDTO>();
    	    DocumentDTO document = new DocumentDTO();
    		CategoryDAO categoryDAO= new CategoryDAO();
    		ClientDAO clientDAO= new ClientDAO();
    		DocumentDAO documentDAO= new DocumentDAO();
    		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
    		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
    		String clientCode = XSSEscape.isClientCode(request.getParameter("clientCode"));
    		
    		if (fileName == null || categoryCode == null) {
    	        request.setAttribute("errorMessage", "비정상적인 접근");
    		    request.getRequestDispatcher("Error.jsp").forward(request, response);
    			return;
    		}
    		
    		categoryList = categoryDAO.getList();
        	client = clientDAO.getList();
        	document = documentDAO.getDocumentInfo(fileName, categoryCode, clientCode);
        	
        	clientDAO.clientClose();
        	categoryDAO.categoryClose();
        	documentDAO.documentClose();
        	
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("client", client);
            request.setAttribute("document", document);
    		
    	    request.getRequestDispatcher("DocumentUpdate.jsp").forward(request, response);
        }
		
		
	}
	

	private void deleteFile(File file) {
        if (file != null)
        	file.delete();
	}
	
	private boolean isClientSame(String client1, String client2) {
		if (client1 == null) {
			if (client2 == null)
				return true;
		} else {
			return client1.equals(client2);
		}
		return false;
	}

}
