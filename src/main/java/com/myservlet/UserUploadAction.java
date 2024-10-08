package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.user.UserDAO;
import com.user.UserDTO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class UpdateUserLock
 */
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 페이지 로딩을 위한 doGet()
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        //세션에 로그인 정보가 있는지 확인부터 한다.
        //관리자 계정이라면 upload페이지로 이동시킨다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || user.getUserCode() != 0) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
	        request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		} else {
			request.getRequestDispatcher("WEB-INF/UserUpload.jsp").forward(request, response);
		}
		
	}

  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 사용자 등록을 위한 doPost()
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
		        request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			} //아이디 중복 체크
			else if (userDAO.userIDCheck(userID) == 1) {
				//아이디 등록 성공
				if(userDAO.userUpload(userName, userID, userPassword, isCategory.equals("1"),
						isClient.equals("1"), isDocument.equals("1")) == 1) {
	                request.setAttribute("messageUser", "사용자 등록 성공!");
	                request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
				} //DB 오류 등으로 등록 실패
				else {
	                request.setAttribute("errorMessage", "에러가 발생해 등록하지 못했습니다.");
	                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
				}
			} //중복된 아이디가 있을 경우 등록 실패
			else {
                request.setAttribute("errorMessage", "아이디가 중복됩니다.");
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			}
		} catch (Exception e) {
	    	userDAO.errorLogUpload(e);
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		}
		
		userDAO.userClose();
	}
}
