package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.user.UserDAO;
import com.user.UserDTO;

/**
 * Servlet implementation class UpdateUserLock
 */
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 직접 url을 타이핑하여 접근하는 것을 차단한다 -> 오로지 동작을 위한 servlet임
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
	    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
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
        
        // 입력된 값에 따른 사용자 퇴사 여부 변경 시도
        try {
            userDAO.updateLock(Integer.parseInt(userCode), status);
        } catch (NumberFormatException e) {
	    	userDAO.errorLogUpload(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "요청 오류");
        } catch (Exception e) {
	    	userDAO.errorLogUpload(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 에러");
        }
        userDAO.userClose();
	}

}
