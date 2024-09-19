package com.myservlet;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.client.ClientDAO;
import com.document.DocumentDAO;
import com.user.UserDTO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class DocumentDeleteAction
 */
@WebServlet("/DocumentDelete")
public class DocumentDeleteAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentDeleteAction() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
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
	    request.getRequestDispatcher("Error.jsp").forward(request, response);
	}

  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String clientName = XSSEscape.changeClientName(request.getParameter("clientName"));
		String clientCode = XSSEscape.isClientCode(request.getParameter("clientCode"));
		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
		String folderPath;
		String categoryRoot = "";
		String[] checkedDocumentCodes = request.getParameterValues("checkedDocumentCode");
        DocumentDAO documentDAO = new DocumentDAO();
		
		//유저의 권한 먼저 확인
		if (user == null || !user.isDocument() ) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
		    documentDAO.documentClose();
			return;
		}
		
		//일괄 삭제
		if (checkedDocumentCodes != null) {
			ClientDAO clientDAO = new ClientDAO();
			String[] rootAndName;
			File file;
			for (String code: checkedDocumentCodes) {
				rootAndName = XSSEscape.changeDocumentDownload(code);
				if (rootAndName != null) {
					categoryCode = XSSEscape.isNumber(rootAndName[0]);
					clientName = XSSEscape.changeClientName(rootAndName[1]);
					clientCode = XSSEscape.isClientCode(clientDAO.getClientCode(clientName));
					fileName = XSSEscape.changeCategoryName(rootAndName[2]);
					categoryRoot = documentDAO.getRoot(categoryCode);
					folderPath = getServletContext().getRealPath(categoryRoot + File.separator + clientName + "/");
					file = new File(folderPath + fileName);
					
					if(file.exists() && documentDAO.documentDelete(fileName, categoryCode, clientCode,user.getUserCode()) == 1) {
						file.delete();
			            file = new File(folderPath);
			            if (isDirectoryEmpty(file)) {
			            	file.delete();
			            }
					}
				}
			}
			
            request.setAttribute("messageDocument", "문서 삭제 성공!");
            request.getRequestDispatcher("Message.jsp").forward(request, response);
		    documentDAO.documentClose();
			return;
		}
		
		
		//단일 삭제
		if (user == null || !user.isDocument() || categoryCode == null || fileName == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
		    documentDAO.documentClose();
			return;
		}

		folderPath = getServletContext().getRealPath(File.separator + documentDAO.getRoot(categoryCode) + File.separator + clientName + "/");
        File file = new File(folderPath + fileName);
		
        if (file.exists() && documentDAO.documentDelete(fileName, categoryCode, clientCode,user.getUserCode()) == 1) {
            file.delete();
            file = new File(folderPath);
            if (isDirectoryEmpty(file)) {
            	file.delete();
            }
            request.setAttribute("messageDocument", "문서 삭제 성공!");
            request.getRequestDispatcher("Message.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
        }
	    documentDAO.documentClose();

	}
	
   private static boolean isDirectoryEmpty(File directory) {
        String[] files = directory.list();
        return files == null || files.length == 0;
    }

}
