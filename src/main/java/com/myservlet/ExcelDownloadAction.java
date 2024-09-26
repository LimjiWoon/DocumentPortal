package com.myservlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.myclass.CreateExcel;
import com.myclass.XSSEscape;
import com.user.*;
import com.category.*;
import com.client.*;
import com.document.*;
import com.log.*;

/**
 * Servlet implementation class ExcelDownloadAction
 */
@WebServlet("/Excel")
public class ExcelDownloadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExcelDownloadAction() {
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


        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
	    String code = XSSEscape.changeText(request.getParameter("code"));
		if (user == null || code == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			return;
		}
		
		
	    String dateOfPassword = XSSEscape.changeChangeDate(request.getParameter("dateOfPassword"));
	    String searchField = null;
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String isRetire = XSSEscape.changePermisson(request.getParameter("isRetire"));
	    String isLock = XSSEscape.changePermisson(request.getParameter("isLock"));
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    String filterCategory = XSSEscape.isNumber(request.getParameter("filterCategory"));
	    String filterClient = XSSEscape.isNumber(request.getParameter("filterClient"));
	    String logWhere = XSSEscape.changeLogWhere(request.getParameter("logWhere"));
	    String logHow = XSSEscape.changeLogHow(request.getParameter("logHow"));
	    String isUse = XSSEscape.changePermisson(request.getParameter("isUse"));
	    

	    
	    //엑셀을 만들어 다운로드 한다. code에 따라 다운받는 엑셀 페이지가 달라진다.
	    try {
	    	Workbook workBook;

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    	
	    	//각 권한에 따른 엑셀 생성
			//사용자 페이지
	    	if ("hidden1".equals(code) && user.getUserCode() == 0) {
	    	    ArrayList<UserDTO> list = new ArrayList<UserDTO>();
	    	    UserDAO userDAO = new UserDAO();
	    	    searchField = XSSEscape.changeUserField(request.getParameter("searchField"));
	    		
	    	    if (searchField != null && searchText != null && searchOrder != null){
	    	    	list = userDAO.getExcel(user.getUserCode(), dateOfPassword, isLock, isRetire, searchField, searchOrder, searchText);
	    	    } else {
	    	    	list = userDAO.getExcel(user.getUserCode(), dateOfPassword, isLock, isRetire, null, null, null);
	    	    }
	    		
	    		workBook = CreateExcel.userExcel(list);
		        response.setHeader("Content-Disposition", "attachment; filename=\"user_list.xlsx\"");
	    		userDAO.userClose();
			} //고객사 페이지 
	    	else if ("hidden2".equals(code) && user.isClient()) {
			    ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
			    ClientDAO clientDAO = new ClientDAO();
			    searchField = XSSEscape.changeClientField(request.getParameter("searchField"));

	    	    if (searchField != null && searchText != null && searchOrder != null){
	    	    	list = clientDAO.getExcel(user.getUserCode(), startDate, endDate, isUse, searchField, searchOrder, searchText);
	    	    } else {
	    	    	list = clientDAO.getExcel(user.getUserCode(), startDate, endDate, isUse, null, null, null);
	    	    }
				
				workBook = CreateExcel.clientExcel(list);
		        response.setHeader("Content-Disposition", "attachment; filename=\"client_list.xlsx\"");
				clientDAO.clientClose();
			} //문서 목록 페이지
	    	else if ("hidden3".equals(code) && user.isCategory()) {
			    ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
			    CategoryDAO categoryDAO = new CategoryDAO();
			    searchField = XSSEscape.changeCategoryField(request.getParameter("searchField"));

	    	    if (searchField != null && searchText != null && searchOrder != null){
	    	    	list = categoryDAO.getExcel(user.getUserCode(), startDate, endDate, searchField, searchOrder, searchText);
	    	    } else {
	    	    	list = categoryDAO.getExcel(user.getUserCode(), startDate, endDate, null, null, null);
	    	    }
				
				workBook = CreateExcel.categoryExcel(list);
		        response.setHeader("Content-Disposition", "attachment; filename=\"category_list.xlsx\"");
				categoryDAO.categoryClose();
			} //문서 페이지
	    	else if ("hidden4".equals(code) && user.isDocument()) {
			    ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
			    DocumentDAO documentDAO = new DocumentDAO();
			    searchField = XSSEscape.changeDocumentField(request.getParameter("searchField"));

	    	    if (searchField != null && searchText != null && searchOrder != null){
	    	    	list = documentDAO.getExcel(user.getUserCode(), startDate, endDate, filterCategory, filterClient, searchField, searchOrder, searchText);
	    	    } else {
	    	    	list = documentDAO.getExcel(user.getUserCode(), startDate, endDate, filterCategory, filterClient, null, null, null);
	    	    }
				
				workBook = CreateExcel.documentExcel(list);
		        response.setHeader("Content-Disposition", "attachment; filename=\"document_list.xlsx\"");
				documentDAO.documentClose();
			} //로그 페이지
	    	else if ("hidden5".equals(code) && user.getUserCode() == 0) {
			    ArrayList<LogDTO> list = new ArrayList<LogDTO>();
			    LogDAO logDAO = new LogDAO();
			    searchField = XSSEscape.changeLogField(request.getParameter("searchField"));

	    	    if (searchField != null && searchText != null){
	    	    	list = logDAO.getExcel(user.getUserCode(), startDate, endDate, logWhere, logHow, searchField, searchText);
	    	    } else {
	    	    	list = logDAO.getExcel(user.getUserCode(), startDate, endDate, logWhere, logHow, null, null);
	    	    }
				
				workBook = CreateExcel.logExcel(list);
		        response.setHeader("Content-Disposition", "attachment; filename=\"log_list.xlsx\"");
				logDAO.logClose();
			} //만약 이상한 값이 들어왔을 경우 빈 엑셀을 반환
	    	else {
				workBook = new XSSFWorkbook();
		        response.setHeader("Content-Disposition", "attachment; filename=\"Excel.xlsx\"");
			}
	    	
	    	
	        
	        // ServletOutputStream을 통해 파일을 클라이언트에게 전송
	        ServletOutputStream servletOutputStream = response.getOutputStream();
	        
	        workBook.write(servletOutputStream);

			servletOutputStream.flush();
			servletOutputStream.close();
			workBook.close();
			
	    } catch (Exception e) {
			//사용자 페이지
	    	if ("hidden1".equals(code) && user.getUserCode() == 0) {
	    	    UserDAO userDAO = new UserDAO();
	    	    userDAO.errorLogUpload(e);
	    		userDAO.userClose();
			} //고객사 페이지 
	    	else if ("hidden2".equals(code) && user.isClient()) {
			    ClientDAO clientDAO = new ClientDAO();
			    clientDAO.errorLogUpload(e);
				clientDAO.clientClose();
			} //문서 목록 페이지
	    	else if ("hidden3".equals(code) && user.isCategory()) {
			    CategoryDAO categoryDAO = new CategoryDAO();
			    categoryDAO.errorLogUpload(e);
				categoryDAO.categoryClose();
			} //문서 페이지
	    	else if ("hidden4".equals(code) && user.isDocument()) {
			    DocumentDAO documentDAO = new DocumentDAO();
			    documentDAO.errorLogUpload(e);
				documentDAO.documentClose();
			} //로그 페이지
	    	else if ("hidden5".equals(code) && user.getUserCode() == 0) {
			    LogDAO logDAO = new LogDAO();
			    logDAO.errorLogUpload(e);
				logDAO.logClose();
			}
	    	else {
	    		e.printStackTrace();
			}
	        request.setAttribute("errorMessage", "에러");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			return;
	    }
	    
		
	}
	



}
