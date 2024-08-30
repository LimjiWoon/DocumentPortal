package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.user.UserDAO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class UpdateUserLock
 */
@WebServlet("/UserUpload")
public class UserUploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserUploadAction() {
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
		UserDAO userDAO = new UserDAO();
		
		try {
			String userID = XSSEscape.changeUserID(request.getParameter("userID"));
			String userName = XSSEscape.changeUserName(request.getParameter("userName"));
			String userPassword = XSSEscape.changeUserPassword(request.getParameter("userPassword"));
			String isCategory = XSSEscape.changePermisson(request.getParameter("isCategory"));
			String isClient = XSSEscape.changePermisson(request.getParameter("isClient"));
			String isDocument = XSSEscape.changePermisson(request.getParameter("isDocument"));
			
			//위 XSS를 어느 하나라도 통과하지 못했다면
			if (userID == null || userName == null || userPassword == null 
					|| isCategory == null || isClient == null || isDocument == null){
	            request.setAttribute("errorMessage", "비정상적인 접근");
	            request.getRequestDispatcher("Error.jsp").forward(request, response);
			} //아이디 중복 체크
			else if (userDAO.userIDCheck(userID) == 1) {
				//아이디 등록 성공
				if(userDAO.userUpload(userName, userID, userPassword, isCategory.equals("1"),
						isClient.equals("1"), isDocument.equals("1")) == 1) {
	                request.setAttribute("messageUser", "사용자 등록 성공!");
	                request.getRequestDispatcher("Message.jsp").forward(request, response);
				} //DB 오류 등으로 등록 실패
				else {
	                request.setAttribute("errorMessage", "에러가 발생해 등록하지 못했습니다.");
	                request.getRequestDispatcher("Error.jsp").forward(request, response);
				}
			} //중복된 아이디가 있을 경우 등록 실패
			else {
                request.setAttribute("errorMessage", "아이디가 중복됩니다.");
                request.getRequestDispatcher("Error.jsp").forward(request, response);
			}
		} catch (Exception e) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
		}
		
		userDAO.userClose();
	}
}
