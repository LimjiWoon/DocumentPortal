package com.myservlet;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.category.*;
import com.user.UserDTO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class CategoryUploadAction
 */
@WebServlet("/CategoryUpload")
public class CategoryUploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryUploadAction() {
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
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || !user.isCategory()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			return;
		}
		
		CategoryDAO categoryDAO= new CategoryDAO();
		String categoryName = XSSEscape.changeCategoryName(request.getParameter("categoryName"));
		String categoryRoot = "/root/" + categoryName; 

		//등록시 이름이 비어있다면 에러
		if (categoryName == null) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
            categoryDAO.categoryClose();
			return;
		}
		
		String folderPath = getServletContext().getRealPath(categoryRoot);
		File folder = new File(folderPath);
		try {
			//파일이 이미 존재하고 있다면 에러
			if (folder.exists()) {
                request.setAttribute("errorMessage", "해당 목록이 이미 존재합니다.");
                request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
	            categoryDAO.categoryClose();
                return;
			}
			//파일이 없다면 폴더 생성
			if(folder.mkdir()) {
				if (categoryDAO.categoryUpload(categoryName, 0, user.getUserCode(), categoryRoot) == 1) {
		            request.setAttribute("messageCategory", "신규 문서 목록 생성 성공!");
		            request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
		            categoryDAO.categoryClose();
					return;
				}
			}
		} catch(Exception e) {
	    	categoryDAO.errorLogUpload(e);
		}
		
		//만약에 위 과정에서 에러처리로 빠졌다면 생성했던 폴더를 삭제한다
		if (folder.exists()) {
			folder.delete();
		}

        categoryDAO.categoryClose();
		request.setAttribute("errorMessage", "경로가 잘못 되었습니다..");
        request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		
	}
		
	

}
