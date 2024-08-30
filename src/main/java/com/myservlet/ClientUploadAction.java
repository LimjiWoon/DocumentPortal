package com.myservlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.category.CategoryDAO;
import com.category.CategoryDTO;
import com.client.*;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class ClientUploadAction
 */
@WebServlet("/ClientUpload")
public class ClientUploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientUploadAction() {
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

		CategoryDAO categoryDAO= new CategoryDAO();
	    ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
        
        if (categoryCode == null) {
			list = categoryDAO.getList();
			categoryDAO.categoryClose();
	        request.setAttribute("list", list);
		    request.getRequestDispatcher("ClientUpload.jsp").forward(request, response);
		    return;
		}

        ClientDAO clientDAO = new ClientDAO();
        
		try {
			boolean newCategory = XSSEscape.isTrue(request.getParameter("newCategory"));
			String clientName = XSSEscape.changeClientName(request.getParameter("clientName"));
			String clientContent = XSSEscape.changeText(request.getParameter("clientContent"));
			HttpSession session = request.getSession();
			UserDTO user = (UserDTO) session.getAttribute("user");
			
			if (clientName == null) {
	            request.setAttribute("errorMessage", "실패");
	            request.getRequestDispatcher("Error.jsp").forward(request, response);
			} else if(clientDAO.clientUniqueName(clientName)){
	            request.setAttribute("errorMessage", "이름이 중복됩니다.");
	            request.getRequestDispatcher("Error.jsp").forward(request, response);
			} else if (newCategory && user.isCategory()) {
				String categoryLv = XSSEscape.isNumber(request.getParameter("categoryLv"));
				String categoryRoot = categoryDAO.getRoot(Integer.parseInt(categoryLv), Integer.parseInt(categoryCode));
				String folderPath = getServletContext().getRealPath("");
				
				
				if (categoryRoot == null) {
		            request.setAttribute("errorMessage", "비정상적인 접근");
		            request.getRequestDispatcher("Error.jsp").forward(request, response);
					return;
				}
				
				File folder = new File(folderPath + File.separator + categoryRoot +"/"+clientName);

				if (folder.exists()) {
	                request.setAttribute("errorMessage", "해당 목록이 이미 존재합니다.");
	                request.getRequestDispatcher("Error.jsp").forward(request, response);
	                return;
				}
				
				if(folder.mkdir()) {
					int result = categoryDAO.categoryUpload(clientName, Integer.parseInt(categoryLv), Integer.parseInt(categoryCode), user.getUserCode(), categoryRoot);
					if(result == 1) {
						result = clientDAO.clientUpload(clientName, categoryDAO.categoryCodeLast(), clientContent, user.getUserCode());
						if(result == 1) {
				            request.setAttribute("messageClient", "객사와 고객사명의 문서 목록을 등록했습니다.");
				            request.getRequestDispatcher("Message.jsp").forward(request, response);
			                return;
						}
					}
		            request.setAttribute("messageClient", "문서 목록을 등록했지만 고객사 등록을 실패 했습니다.");
		            request.getRequestDispatcher("Message.jsp").forward(request, response);
				} else {
	                request.setAttribute("errorMessage", "문서 목록 등록에 실패 했습니다.");
	                request.getRequestDispatcher("Error.jsp").forward(request, response);
				}
				
			}else {
	            clientDAO.clientUpload(clientName, Integer.parseInt(categoryCode), clientContent, user.getUserCode());
	            request.setAttribute("messageClient", "고객사를 등록했습니다.");
	            request.getRequestDispatcher("Message.jsp").forward(request, response);
			}
        } catch(Exception e) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
        }

		clientDAO.clientClose();
		categoryDAO.categoryClose();
		
	}

}
