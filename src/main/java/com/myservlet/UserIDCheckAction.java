package com.myservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myclass.XSSEscape;
import com.user.UserDAO;
import com.user.UserDTO;

/**
 * Servlet implementation class UserIDCheck
 */
@WebServlet("/UserIDCheck")
public class UserIDCheckAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserIDCheckAction() {
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
        response.setCharacterEncoding("UTF-8");

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || user.getUserCode() != 0) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		    return;
		} else {
            request.getRequestDispatcher("WEB-INF/UserIDCheck.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

		UserDAO userDAO = new UserDAO();
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");
        String userID = XSSEscape.checkID(request.getParameter("userID"));
        int result = 0;
        PrintWriter out = response.getWriter();
        
        // 입력값 확인 및 사용자 검증
        if (user==null || user.getUserCode() != 0 || userID == null || "".equals(userID.trim())) {
        	out.println("0");
            out.close();
            return;
        }
        
        try {
        	result = userDAO.userIDCheck(userID);
        	//result가 1일 경우 해당 아이디 사용 가능
        	if (result == 1) {
            	out.println("1");
        	} //그 외의 경우는 아이디 사용 불가 
        	else {
            	out.println("0");
        	}
        } catch (Exception e) {
        	out.println("0");
            e.printStackTrace();
        }
        
        out.close();
        userDAO.userClose();
	}

}
