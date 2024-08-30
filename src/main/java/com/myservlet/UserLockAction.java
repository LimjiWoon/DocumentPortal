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

/**
 * Servlet implementation class UpdateUserLock
 */
@WebServlet("/UserLock")
public class UserLockAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLockAction() {
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
        HttpSession session = request.getSession();
        UserDTO master = (UserDTO) session.getAttribute("user");
        String status = request.getParameter("status");
        String userCode = request.getParameter("userCode");

        // 입력값 확인 및 사용자 검증
        if (master==null || master.getUserCode() != 0 || status == null || userCode == null
        		|| userCode.equals("0") || !status.matches("[OX]") || !userCode.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "입력값 오류");
            return;
        }

        try {
            userDAO.updateLock(Integer.parseInt(userCode), status);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "요청 오류");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 에러");
        }
	}

}
