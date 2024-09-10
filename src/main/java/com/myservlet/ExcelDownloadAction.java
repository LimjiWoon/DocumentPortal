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
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
		
		
	    String dateOfPassword = XSSEscape.changeChangeDate(request.getParameter("dateOfPassword"));
	    String searchField = XSSEscape.changeUserField(request.getParameter("searchField"));
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String isRetire = XSSEscape.changePermisson(request.getParameter("isRetire"));
	    String isLock = XSSEscape.changePermisson(request.getParameter("isLock"));
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    String isUse = XSSEscape.changePermisson(request.getParameter("isUse"));
	    

	    
	    
	    try {
	    	Workbook workBook;
	    	//각 권한에 따른 엑셀 생성
	    	if ("hidden1".equals(code) && user.getUserCode() == 0) {
	    	    ArrayList<UserDTO> list = new ArrayList<UserDTO>();
	    	    UserDAO userDAO = new UserDAO();
	    		
	    	    if (searchField != null && searchText != null && searchOrder != null){
	    	    	list = userDAO.getExcel(dateOfPassword, isLock, isRetire, searchField, searchOrder, searchText);
	    	    } else {
	    	    	list = userDAO.getExcel(dateOfPassword, isLock, isRetire, null, null, null);
	    	    }
	    		
	    		workBook = CreateExcel.userExcel(list);
	    		userDAO.userClose();
			} else if ("hidden2".equals(code) && user.isClient()) {
			    ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
			    ClientDAO clientDAO = new ClientDAO();

	    	    if (searchField != null && searchText != null && searchOrder != null){
	    	    	list = clientDAO.getExcel(startDate, endDate, isUse, searchField, searchOrder, searchText);
	    	    } else {
	    	    	list = clientDAO.getExcel(startDate, endDate, isUse, null, null, null);
	    	    }
				
				workBook = CreateExcel.clientExcel(list);
				clientDAO.clientClose();
			} else if ("hidden3".equals(code) && user.isCategory()) {
			    ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
			    CategoryDAO categoryDAO = new CategoryDAO();
			    
			    
				
				workBook = CreateExcel.categoryExcel(list);
				categoryDAO.categoryClose();
			} else if ("hidden4".equals(code) && user.isDocument()) {
			    ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
			    DocumentDAO documentDAO = new DocumentDAO();
				
				workBook = CreateExcel.documentExcel(list);
				documentDAO.documentClose();
			} else if ("hidden5".equals(code) && user.getUserCode() == 0) {
			    ArrayList<LogDTO> list = new ArrayList<LogDTO>();
			    LogDAO logDAO = new LogDAO();
				
				workBook = CreateExcel.logExcel(list);
				logDAO.logClose();
			} else {
				workBook = new XSSFWorkbook();
			}
	    	
	    	
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setHeader("Content-Disposition", "attachment; filename=\"Excel.xlsx\"");
	        
	        // ServletOutputStream을 통해 파일을 클라이언트에게 전송
	        ServletOutputStream servletOutputStream = response.getOutputStream();
	        
	        workBook.write(servletOutputStream);

			servletOutputStream.flush();
			servletOutputStream.close();
			workBook.close();
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	        request.setAttribute("errorMessage", "에러");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
	    }
	    
		
	}
	



}
