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
import com.myclass.PasswordLogic;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class UpdatePassword
 */
@WebServlet("/Login")
public class LoginAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginAction() {
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
	    request.getRequestDispatcher("WEB-INF/Login.jsp").forward(request, response);
	}
    
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String userID = XSSEscape.checkID(request.getParameter("userID"));
        String userPassword = XSSEscape.checkPassword(request.getParameter("userPassword"));
        String message = "";
        UserDAO userDAO = new UserDAO();
        PasswordLogic logic = new PasswordLogic();

        //user는 아이디와 비밀번호만 저장함
        UserDTO user = new UserDTO();
        //realUser는 모든 데이터를 저장 -> 정말 중요한 정보
        UserDTO realUser = new UserDTO();
        user.setUserID(userID);
        
        //DB로 먼저 로그인 시도 -> 그에 따른 결과 반환
        int result = userDAO.login(userID, userPassword);
        
        try {
        	//입력된 ID와 비밀번호가 XSS 공격일 경우
            if(userID == null || userPassword == null) {
            	message += "비정상적인 접근";
                request.setAttribute("errorMessage", message);
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
            } //로그인 시도 - 일치하는 아이디가 있을 경우
            else if (result == 1) {
            	realUser = userDAO.getInfo(userID);
            	//로그인 실패 1- 퇴사자
                if (realUser.isRetire()) {
                	message += "퇴사한 사원입니다.";
                    request.setAttribute("errorMessage", message);
                    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
                } //로그인 실패 2- 계정 잠금 
                else if (realUser.isLock()) {
                	message += "계정이 잠겼습니다.";
                    request.setAttribute("errorMessage", message);
                    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
                } //로그인 실패 3- 장기 미접속 시
                else if (logic.getPasswordChangeDay(realUser.getDateOfPassword()) > 180) {
                	message += "장기간 미접속한 사람입니다. \\n계정이 잠겼습니다.";
                    request.setAttribute("errorMessage", message);
                    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
                } //로그인 성공 1 - 비밀번호 변경 필요
                else if (logic.getPasswordChangeDay(realUser.getDateOfPassword()) > 90) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    if (realUser.getDateOfPassword() == null) {
                    	message += "최초 로그인입니다. \\n";
                    } else {
                    	message += "비밀번호를 변경한지 3개월이 지났습니다. \\n";
                    }
                	message += "비밀번호를 변경해야합니다.";
                    request.setAttribute("errorMessage", message);
                    request.getRequestDispatcher("WEB-INF/PasswordRenewPage.jsp").forward(request, response);
                } //로그인 성공 2
                else {
                    userDAO.loginSuccess(realUser.getUserCode(), userID);
                    HttpSession session = request.getSession();
                    session.setAttribute("user", realUser);
                    response.sendRedirect("Main");
                }
            } //로그인 실패 4 - 비밀번호 틀림
            else if (result == 0) {
                user = userDAO.getInfo(userID);
                if (!user.isLock()) {
                    userDAO.loginFail(user.getUserCode(), userID, user.getFailOfPassword());
                }
                message = "비밀번호가 틀립니다.";
                if (user.getFailOfPassword() == 4) {
                    message += " 비밀번호를 5회 틀려 계정이 잠겼습니다. 관리자에게 문의해주시길 바랍니다.";
                }
                request.setAttribute("errorMessage", message);
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
            } //로그인 실패 5 - 아이디 틀림
            else if (result == -1) {
                message = "존재하지 않는 아이디입니다.";
                request.setAttribute("errorMessage", message);
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
            } //로그인 실패 6 - DB 자체의 오류
            else if (result == -2) {
                message = "데이터베이스 오류가 발생했습니다.";
                request.setAttribute("errorMessage", message);
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
            }
        }  //중간에 에러 뭔지 모를 에러 발생 시 따로 처리
        catch (Exception e) {
	    	userDAO.errorLogUpload(e);
        	message = "에러";
            request.setAttribute("errorMessage", message);
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
        }
        
        userDAO.userClose();
	

	}

}
