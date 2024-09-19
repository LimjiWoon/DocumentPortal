package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.user.*;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class UserRetireAction
 */
@WebServlet("/UserRetire")
public class UserRetireAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRetireAction() {
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
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
        
        String userCode = XSSEscape.changeUserCode(request.getParameter("userCode"));
        String isRetire = request.getParameter("isRetire");
        HttpSession session = request.getSession();
        UserDTO master = (UserDTO) session.getAttribute("user");
        UserDAO userDAO = new UserDAO();
        
        
        try {
        	// 검증을 위한 if 문이다.
        	// 제대로 된 값을 입력 받았는지 확인하고 master 권한을 가진 유저인지 확인한다.
        	if (userCode == null || "0".equals(userCode)
        			|| (!"0".equals(isRetire) && !"1".equals(isRetire))
        			|| master == null || master.getUserCode() != 0) {
                request.setAttribute("errorMessage", "잘못된 접근입니다.");
                request.getRequestDispatcher("Error.jsp").forward(request, response);
        	} else{
        		// 유저 탈퇴 or 복직 처리
            	userDAO.userRetire(Integer.parseInt(userCode), isRetire);
                request.getRequestDispatcher("User").forward(request, response);
        	}

        } catch(Exception e) {
            request.setAttribute("errorMessage", "오류");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
        }
        userDAO.userClose();

    }

}
