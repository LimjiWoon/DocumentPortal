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
 * Servlet implementation class UpdateUserLock
 */
@WebServlet("/UserUpdate")
public class UserUpdateAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserUpdateAction() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 직접 url을 타이핑하여 접근하는 것을 차단한다 -> 오로지 동작을 위한 servlet은 아니지만 사용자 정보가 필요하므로 doPost만 가능
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
		UserDTO updateUser = new UserDTO();
		
		try {
			String userCode = XSSEscape.changeUserCode(request.getParameter("userCode"));
			String userID = XSSEscape.changeUserID(request.getParameter("userID"));
			String userName = XSSEscape.changeUserName(request.getParameter("userName"));
			String userPassword = XSSEscape.changeUserPassword(request.getParameter("userPassword"));
			String isCategory = XSSEscape.changePermisson(request.getParameter("isCategory"));
			String isClient = XSSEscape.changePermisson(request.getParameter("isClient"));
			String isDocument = XSSEscape.changePermisson(request.getParameter("isDocument"));


        	//userCode가 없는 경우
			if (userCode == null) {
                request.setAttribute("errorMessage", "비정상적인 접근");
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			} //페이지를 로딩하는 경우
			else if (userID == null && userName == null && userPassword == null 
					&& isCategory == null && isClient == null  && isDocument == null ) {
				updateUser = userDAO.getInfo(Integer.parseInt(userCode)); 

	            request.setAttribute("updateUser", updateUser);
                request.getRequestDispatcher("WEB-INF/UserUpdate.jsp").forward(request, response);
			} //잘못된 값이 존재할 때
			else if (userID == null || userName == null || userPassword == null 
					|| isCategory == null || isClient == null || isDocument == null ) {
				updateUser = userDAO.getInfo(Integer.parseInt(userCode)); 

                request.setAttribute("errorMessage", "변경 실패");
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			} //수정을 시도할 때
			else {
				updateUser = userDAO.getInfo(Integer.parseInt(userCode)); 
				//ID가 변경 시도가 감지 되었을 때
				if (!userID.equals(updateUser.getUserID())) {
	                request.setAttribute("errorMessage", "에러");
	                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
				} //입력한 값들 중 그 어떠한 값도 바뀌지 않았을 떄
				else if (userName.equals(updateUser.getUserName())
						&& userPassword.equals(updateUser.getUserPassword())
						&& isCategory.equals("1")==updateUser.getIsCategory()
						&& isClient.equals("1")==updateUser.getIsClient()
						&& isDocument.equals("1")==updateUser.getIsDocument()) {
	                request.setAttribute("errorMessage", "변경된 값이 없습니다.");
	                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
				} //사용자 수정 시도 -> 에러 시 아래 오류로 넘어감
				else if (userDAO.userUpdate(request.getParameter("userID"),
							userName, updateUser.getUserName(),
							userPassword, updateUser.getUserPassword(),
							isCategory, (updateUser.getIsCategory()? "1": "0"),
							isClient, (updateUser.getIsClient()? "1": "0"),
							isDocument, (updateUser.getIsDocument()? "1": "0"))  == 1) {
	                request.setAttribute("messageUser", "사용자 수정 성공!");
	                request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
				} //DB 오류
				else {
	                request.setAttribute("errorMessage", "데이터 베이스 오류.");
	                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
				}
			}
		} catch (Exception e) {
	    	userDAO.errorLogUpload(e);
            request.setAttribute("errorMessage", "오류");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		}
		
		userDAO.userClose();
	}

}
