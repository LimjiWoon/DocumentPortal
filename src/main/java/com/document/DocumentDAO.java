package com.document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class DocumentDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private DocumentDTO document;
	
	public DocumentDAO() {
		try {
			String dbURL = "jdbc:sqlserver://localhost:1433;encrypt=false;DatabaseName=DocumentPortalData;";
			String dbID = "admin";
			String dbPassword = "qwert12345!";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn=DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String filterSQL(String SQL, String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchText) {
		//그 어떠한 값도 들어오지 않은 경우
		if (startDate == null && endDate == null && filterCategory == null && 
				filterClient == null && searchField == null && searchText == null) {
			return SQL;
		}
		
		StringBuilder query = new StringBuilder(SQL);
		ArrayList<String> conditions = new ArrayList<String>();
		
		if (startDate != null && endDate != null) {
			conditions.add(" f.dateOfUpdate BETWEEN '"+startDate+"' AND DATEADD(day, 1, '"+endDate+"') ");
		} else if (startDate != null) {
			conditions.add(" f.dateOfUpdate > '"+startDate+"' ");
		} else if (endDate != null) {
			conditions.add(" f.dateOfUpdate < DATEADD(day, 1, '"+endDate+"') ");
		}

		if (filterCategory != null) {
			conditions.add(" f.categoryCode ="+filterCategory+" ");
		}

		if (filterClient != null) {
			conditions.add(" f.clientCode ="+filterClient+" ");
		}
		
		if (searchField != null && searchText != null) {
			conditions.add(" " + searchField.trim() + " LIKE '%" + searchText.trim() + "%' ");
		}
		
		if (!conditions.isEmpty()) {
		    query.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		
		return query.toString();
	}

	
	public int maxPage(String startDate, String endDate, String filterCategory, String filterClient) {
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.FILES f ";
		SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}	
	
	
	public int maxPage(String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchText) { 
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	public ArrayList<DocumentDTO> getList(String startDate, String endDate, String filterCategory, String filterClient, String nowPage){
		String SQL = "SELECT f.fileTitle, c.clientName, cat.categoryName, u.userName, f.dateOfUpdate, f.fileName, f.categoryCode, f.clientCode "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		
		SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
		SQL += "ORDER BY f.dateOfCreate ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				document = new DocumentDTO();
				document.setFileTitle(rs.getString(1));
				document.setClientName(rs.getString(2));
				document.setCategoryName(rs.getString(3));
				document.setUserName(rs.getString(4));
				document.setDateOfUpdate(rs.getString(5));
				document.setFileName(rs.getString(6));
				document.setCategoryCode(rs.getInt(7));
				document.setClientCode(rs.getInt(8));
				list.add(document);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public ArrayList<DocumentDTO> getSearch(String startDate, String endDate, String filterCategory, String filterClient, 
			String nowPage, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT f.fileTitle, c.clientName, cat.categoryName, u.userName, f.dateOfUpdate, f.fileName, f.categoryCode, f.clientCode "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
		}
		SQL += " ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				document = new DocumentDTO();
				document.setFileTitle(rs.getString(1));
				document.setClientName(rs.getString(2));
				document.setCategoryName(rs.getString(3));
				document.setUserName(rs.getString(4));
				document.setDateOfUpdate(rs.getString(5));
				document.setFileName(rs.getString(6));
				document.setCategoryCode(rs.getInt(7));
				document.setClientCode(rs.getInt(8));
				list.add(document);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public ArrayList<DocumentDTO> getDownload(String startDate, String endDate, String filterCategory, String filterClient, String searchField, String searchText){
		String SQL = "SELECT c.clientName, cat.categoryName, f.fileName, f.categoryCode, f.clientCode "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
		}
		ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				document = new DocumentDTO();
				document.setClientName(rs.getString(1));
				document.setCategoryName(rs.getString(2));
				document.setFileName(rs.getString(3));
				document.setCategoryCode(rs.getInt(4));
				document.setClientCode(rs.getInt(5));
				list.add(document);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public ArrayList<DocumentDTO> getExcel(int userCode, String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT f.fileTitle, c.clientName, cat.categoryName, u.userName, f.dateOfUpdate, f.fileName, f.categoryCode, f.clientCode, f.fileContent "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		
		if (searchField != null && searchText != null && searchOrder != null) {
			if (searchText.trim() != ""){
				SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);
			} else {
				SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
			}
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" ;";
		} else {
			SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, null, null);
		}
		
		ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			logUpload(userCode, "", "file", "download", "문서 리스트 엑셀 다운로드");
			while (rs.next()) {
				document = new DocumentDTO();
				document.setFileTitle(rs.getString(1));
				document.setClientName(rs.getString(2));
				document.setCategoryName(rs.getString(3));
				document.setUserName(rs.getString(4));
				document.setDateOfUpdate(rs.getString(5));
				document.setFileName(rs.getString(6));
				document.setCategoryCode(rs.getInt(7));
				document.setClientCode(rs.getInt(8));
				document.setFileContent(rs.getString(9));
				list.add(document);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	public DocumentDTO getDocumentInfo(String fileName, String categoryCode, String clientCode){
		String SQL = "SELECT f.fileTitle, c.clientName, cat.categoryName, u.userName, f.dateOfUpdate, f.fileName, f.categoryCode, f.fileContent, f.clientCode "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode "
				+ "WHERE f.fileName=? AND f.categoryCode=? AND f.clientCode ";
		
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		document = new DocumentDTO();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (fileName == null || categoryCode == null) {
				return null;
			}
			
			pstmt.setString(1, fileName);
			pstmt.setInt(2, Integer.parseInt(categoryCode));
			if (clientCode != null) 
				pstmt.setInt(3, Integer.parseInt(clientCode));
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				document.setFileTitle(rs.getString(1));
				document.setClientName(rs.getString(2));
				document.setCategoryName(rs.getString(3));
				document.setUserName(rs.getString(4));
				document.setDateOfUpdate(rs.getString(5));
				document.setFileName(rs.getString(6));
				document.setCategoryCode(rs.getInt(7));
				document.setFileContent(rs.getString(8));
				document.setClientCode(rs.getInt(9));
				return document;
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getRoot(String categoryCode) {
		String SQL = "SELECT categoryRoot FROM dbo.CATEGORIES WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			if (categoryCode == null) {
				return null;
			} else {
				pstmt.setInt(1, Integer.parseInt(categoryCode));
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int documentUpload(String fileTitle, String fileName, String categoryCode, 
			int userCode, String clientCode, String fileContent) {
		String SQL = "INSERT INTO dbo.FILES VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE(), ?);";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileTitle);
			pstmt.setString(2, fileName);
			pstmt.setInt(3, Integer.parseInt(categoryCode));
			pstmt.setInt(4, userCode);
			if (clientCode == null){
				pstmt.setNull(5, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(5, Integer.parseInt(clientCode));
			}
			if (fileContent == null){
				pstmt.setNull(6, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(6, fileContent);
			}

			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "create", categoryCode + "/"+ clientCode + ": 위치의 신규 문서 생성");
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int documentUpdateCheck(String categoryCode, String clientCode, String fileName) {
		String SQL = "SELECT * FROM dbo.FILES "
				+ "WHERE fileName=? AND categoryCode=? AND clientCode ";
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileName);
			pstmt.setInt(2, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(3, Integer.parseInt(clientCode));
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return 1;
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int documentUpdate(String fileTitle, String fileName, String categoryCode, 
			int userCode, String clientCode, String fileContent) {
		String SQL = "UPDATE FILES SET fileTitle=?, fileContent=?, dateOfUpdate=GETDATE() "
				+ "WHERE fileName=? AND categoryCode=? AND clientCode ";
		
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileTitle);
			if (fileContent == null){
				pstmt.setNull(2, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(2, fileContent);
			}
			pstmt.setString(3, fileName);
			pstmt.setInt(4, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(5, Integer.parseInt(clientCode));

			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "update", categoryCode + "/" + clientCode + ": 위치의 문서 갱신");
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	public int documentUpdate(String fileTitle, String fileName, String categoryCode, String originCategoryCode, 
			int userCode, String clientCode, String originClientCode, String fileContent) {
		String SQL = "UPDATE FILES SET fileTitle=?, clientCode=?, fileContent=?, dateOfUpdate=GETDATE(), categoryCode=? "
				+ "WHERE fileName=? AND categoryCode=? AND clientCode ";
		String logContent = "";
		if (!categoryCode.equals(originCategoryCode)) 
			logContent += " 문서 위치: " + originCategoryCode + "-&gt;" + categoryCode;
		if (originClientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
			if (clientCode != null && !clientCode.equals(originClientCode)) {
				logContent += "  고객사: " + originClientCode + "-&gt;" + clientCode;
			} else if (clientCode == null && originClientCode != null){
				logContent += "  고객사: " + originClientCode + "-&gt;" + clientCode;
			}
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileTitle);
			if (clientCode == null){
				pstmt.setNull(2, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(2, Integer.parseInt(clientCode));
			}
			if (fileContent == null){
				pstmt.setNull(3, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(3, fileContent);
			}
			pstmt.setInt(4, Integer.parseInt(categoryCode));
			pstmt.setString(5, fileName);
			pstmt.setInt(6, Integer.parseInt(originCategoryCode));
			if (originClientCode != null)
				pstmt.setInt(7, Integer.parseInt(originClientCode));
			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "update", logContent);
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}	
	
	public int documentUpdate(String fileTitle, String fileName, String originFileName, String categoryCode, 
			String originCategoryCode, int userCode, String clientCode, String originClientCode, String fileContent) {
		String SQL = "UPDATE FILES SET fileTitle=?, fileName=?, clientCode=?, fileContent=?, dateOfUpdate=GETDATE(), categoryCode=? "
				+ "WHERE fileName=? AND categoryCode=? AND clientCode ";
		String logContent = "";
		if (!fileName.equals(originFileName)) 
			logContent += "파일명: " + originFileName + "-&gt;" + fileName;
		if (!categoryCode.equals(originCategoryCode)) 
			logContent += "  문서 위치: " + originCategoryCode + "-&gt;" + categoryCode;
		if (originClientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
			if (clientCode != null && !clientCode.equals(originClientCode)) {
				logContent += "  고객사: " + originClientCode + "-&gt;" + clientCode;
			} else if (clientCode == null && originClientCode != null){
				logContent += "  고객사: " + originClientCode + "-&gt;" + clientCode;
			}
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileTitle);
			pstmt.setString(2, fileName);
			if (clientCode == null){
				pstmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(3, Integer.parseInt(clientCode));
			}
			if (fileContent == null){
				pstmt.setNull(4, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(4, fileContent);
			}
			pstmt.setInt(5, Integer.parseInt(categoryCode));
			pstmt.setString(6, originFileName);
			pstmt.setInt(7, Integer.parseInt(originCategoryCode));
			if (originClientCode != null)
				pstmt.setInt(8, Integer.parseInt(originClientCode));
			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "update", logContent);
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public DocumentDTO getInfo(String fileName, String categoryCode, String clientCode) {
		String SQL = "SELECT fileTitle, clientCode, fileContent FROM dbo.FILES "
				+ "WHERE fileName=? AND categoryCode=? AND clientCode ";
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		document = new DocumentDTO();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (fileName == null || categoryCode == null) {
				return null;
			}
			
			pstmt.setString(1, fileName);
			pstmt.setInt(2, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(3, Integer.parseInt(clientCode));
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				document.setFileTitle(rs.getString(1));
				document.setClientCode(rs.getInt(2));
				document.setFileContent(rs.getString(3));
				document.setFileName(fileName);
				document.setCategoryCode(Integer.parseInt(categoryCode));
				return document;
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public int documentDelete(String fileName, String categoryCode, String clientCode, int userCode) {
		String SQL = "DELETE FILES WHERE fileName=? AND categoryCode=?  AND clientCode ";
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		if(fileName == null || categoryCode == null) {
			return -1;
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileName);
			pstmt.setInt(2, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(3, Integer.parseInt(clientCode));
			
			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "delete", categoryCode + "/" + clientCode + ": 위치의 문서 삭제");
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int logUpload(int logWho, String logWhat, String logWhere, String logHow, String logWhy) {
		String SQL = "INSERT INTO dbo.LOGS (logWho, logWhat, logWhere, logHow, logWhy) "
				+ " VALUES (?, ?, ?, ?, ?);";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, logWho);
			pstmt.setString(2, logWhat);
			pstmt.setString(3, logWhere);
			pstmt.setString(4, logHow);
			if (logWhy == null){
				pstmt.setNull(5, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(5, logWhy);
			}
			pstmt.executeUpdate();
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void documentClose() {
	    try {
	        if (rs != null) {
	            rs.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	    }

	    try {
	        if (pstmt != null) {
	            pstmt.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	    }

	    try {
	        if (conn != null) {
	            conn.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	    }
	}
	
}
