package com.myservlet;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.category.*;
import com.user.UserDTO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class CategoryUploadAction
 */
@WebServlet("/CategoryUpload")
public class CategoryUploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryUploadAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || !user.isCategory()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
		
		CategoryDAO categoryDAO= new CategoryDAO();
		String categoryName = XSSEscape.changeCategoryName(request.getParameter("categoryName"));
		String categoryRoot = "/root/" + categoryName; 


		if (categoryName == null) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
		
		String folderPath = getServletContext().getRealPath(categoryRoot);
		File folder = new File(folderPath);
		try {

			if (folder.exists()) {
                request.setAttribute("errorMessage", "해당 목록이 이미 존재합니다.");
                request.getRequestDispatcher("Error.jsp").forward(request, response);
                return;
			}
			
			if(folder.mkdir()) {
				if (categoryDAO.categoryUpload(categoryName, 0, user.getUserCode(), categoryRoot) == 1) {
		            request.setAttribute("messageCategory", "신규 문서 목록 생성 성공!");
		            request.getRequestDispatcher("Message.jsp").forward(request, response);
		            categoryDAO.categoryClose();
					return;
				}
				
			}
			
			
		} catch(Exception e) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
		}
		
		if (folder.exists()) {
			folder.delete();
		}

        categoryDAO.categoryClose();
		request.setAttribute("errorMessage", "경로가 잘못 되었습니다..");
        request.getRequestDispatcher("Error.jsp").forward(request, response);
		
	}
		
	

}
