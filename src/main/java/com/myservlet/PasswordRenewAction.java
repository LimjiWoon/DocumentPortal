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
	 * 직접 url을 타이핑하여 접근하는 것을 차단한다
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		
		//사용자 검증
		if(user == null || user.getUserID() == null) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
            session.invalidate();
		} //사용자 검증에 성공했을 때 비밀번호 변경창 띄움 
		else {
			//단, 로그인페이지에서 접속했을 경우 세션에 저장된 사용자 정보를 없앤다.
            if (user.getUserName() == null) {
                session.invalidate();
            }
            //사용자 ID 정보만 비밀번호 변경창에 전달한다.
			request.setAttribute("ID", user.getUserID());
            request.getRequestDispatcher("WEB-INF/PasswordRenewPage.jsp").forward(request, response);
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
        
        //비밀번호 변경 시도
        try {
        	//비밀번호 검증
            if(userID.length() < 21 || userID.matches("[A-Za-z0-9]*")
            		|| request.getParameter("nowPassword").length() < 21
            		|| request.getParameter("newPassword").length() < 21) {
            	//신규 비밀번호와 신규 비밀번호 재입력이 다를 경우
                if (!newPassword.equals(confirmPassword)) {
                	message = "변경할 비밀번호와 비밀번호 확인이 일치하지 않습니다.";
                } else {
                	result = userDAO.passwordRenew(userID, nowPassword, newPassword);
                	//변경할 비밀번호와 현재 비밀번호가 같을 경우
                	if (result == -1) {
                    	message = "변경할 비밀번호가 현재 비밀번호와 같습니다.";
                    } //변경 성공
                	else if (result == 1) {
                    	message = "변경 성공! \\n 다시 로그인하여주십시오";
                        request.setAttribute("LoginSuccess", "1");
                        request.setAttribute("errorMessage", message);
                        request.getRequestDispatcher("WEB-INF/PasswordRenewPage.jsp").forward(request, response);
                        return;
                    } //가존 비밀번호가 틀릴 경우
                	else if (result == 0) {
                    	message = "기존 비밀번호가 일치하지 않습니다.";
                    } //DB 오류
                	else if (result == -2) {
                    	message = "데이터베이스 오류가 발생했습니다.";
                    } //비정상적인 오류 
                	else if (result == -3) {
                    	message = "비정상적인 접근";
                    }
                }
            } else {
            	message = "비정상적인 접근";
            }

        } catch (Exception e) {
	    	userDAO.errorLogUpload(e);
        	message = "에러";
        }

        userDAO.userClose();
        
        //변경 실패 시 다시 비밀번호 변경창으로 돌아간다.
        request.setAttribute("errorMessage", message);
        request.setAttribute("user", message);
        request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);

		
	}

}
