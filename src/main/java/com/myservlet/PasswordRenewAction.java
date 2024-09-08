package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.user.UserDAO;
import com.user.UserDTO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class UpdatePassword
 */
@WebServlet("/PasswordRenew")
public class PasswordRenewAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PasswordRenewAction() {
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

		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		
		if(user == null || user.getUserID() == null) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
            session.invalidate();
		} else {
            if (user.getUserName() == null) {
                session.invalidate();
            }
			request.setAttribute("ID", user.getUserID());
            request.getRequestDispatcher("PasswordRenewPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        UserDAO userDAO = new UserDAO();
        String userID = request.getParameter("userID");
        String nowPassword = XSSEscape.escapeHtml(request.getParameter("nowPassword"));
        String newPassword = XSSEscape.escapeHtml(request.getParameter("newPassword"));
        String confirmPassword = XSSEscape.escapeHtml(request.getParameter("confirmPassword"));
        String message = "";
        int result = -3;
        
        
        
        try {
            if(userID.length() < 21 || userID.matches("[A-Za-z0-9]*")
            		|| request.getParameter("nowPassword").length() < 21
            		|| request.getParameter("newPassword").length() < 21) {
                if (!newPassword.equals(confirmPassword)) {
                	message = "변경할 비밀번호와 비밀번호 확인이 일치하지 않습니다.";
                } else {
                	result = userDAO.passwordRenew(userID, nowPassword, newPassword);
                	if (result == -1) {
                    	message = "변경할 비밀번호가 현재 비밀번호와 같습니다.";
                    } else if (result == 1) {
                    	message = "변경 성공! \\n 다시 로그인하여주십시오";
                        request.setAttribute("LoginSuccess", "1");
                        request.setAttribute("errorMessage", message);
                        request.getRequestDispatcher("PasswordRenewPage.jsp").forward(request, response);
                        return;
                    } else if (result == 0) {
                    	message = "기존 비밀번호가 일치하지 않습니다.";
                    } else if (result == -2) {
                    	message = "데이터베이스 오류가 발생했습니다.";
                    } else if (result == -3) {
                    	message = "비정상적인 접근";
                    }
                }
            } else {
            	message = "비정상적인 접근";
            }

        } catch (Exception e) {
        	message = "에러";
        }
        request.setAttribute("errorMessage", message);
        request.setAttribute("user", message);
        request.getRequestDispatcher("Error.jsp").forward(request, response);

		
	}

}
