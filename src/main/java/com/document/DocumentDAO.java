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

	//SQL query문의 Where절 조합기
	private String filterSQL(String SQL, String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchText) {
		if (startDate == null && endDate == null && filterCategory == null && 
				filterClient == null && searchField == null && searchText == null) {
			return SQL + " WHERE c.isUse=1 "; //통상적인 Where절 반환
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
		conditions.add(" c.isUse=1 ");
		
		if (!conditions.isEmpty()) {
		    query.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		
		return query.toString(); //조합된 Where절을 반환
	}

	//최대 페이지 수 구하기
	public int maxPage(String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchText) { 
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1; //최대 페이지 반환
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	//문서들의 정보 리스트를 반환하는 메소드
	public ArrayList<DocumentDTO> getList(String startDate, String endDate, String filterCategory, String filterClient, 
			String nowPage, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT f.fileTitle, c.clientName, cat.categoryName, u.userName, f.dateOfUpdate, f.fileName, f.categoryCode, f.clientCode "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);

		if (searchOrder != null && searchField != null) {
			SQL += " ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		} else {
			SQL += "ORDER BY f.dateOfCreate ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		}
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
		
		return list; //리스트 반환 -> 결과 없을 시 빈 리스트
	}
	
	//원하는 문서들을 한번에 다운로드 위해 정보들 가져오는 메소드
	public ArrayList<DocumentDTO> getDownload(String startDate, String endDate, String filterCategory, String filterClient, String searchField, String searchText){
		String SQL = "SELECT c.clientName, cat.categoryName, f.fileName, f.categoryCode, f.clientCode "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);

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
		
		return list; //리스트 반환 -> 결과 없을 시 빈 리스트
	}

	//엑셀 시트를 다운로드 하기 위한 정보를 반환하는 메소드
	public ArrayList<DocumentDTO> getExcel(int userCode, String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT f.fileTitle, c.clientName, cat.categoryName, u.userName, f.dateOfUpdate, f.fileName, f.categoryCode, f.clientCode, f.fileContent "
				+ "FROM dbo.FILES f "
				+ "LEFT JOIN dbo.CATEGORIES cat ON cat.categoryCode = f.categoryCode "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = f.userCode "
				+ "LEFT JOIN dbo.CLIENTS c ON c.clientCode = f.clientCode ";
		
		SQL = filterSQL(SQL, startDate, endDate, filterCategory, filterClient, searchField, searchText);
		
		if (searchField != null && searchText != null && searchOrder != null) {
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" ;";
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
		
		return list; //리스트 반환 -> 결과 없을 시 빈 리스트
	}
	
	//특정 문서의 정보를 가져오는 메소드
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
				return null; //입력값 오류
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
				return document; //문서 정보 반환
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null; //데이터 베이스 오류
	}
	
	//해당 문서 목록 경로를 가져오는 메소드
	public String getRoot(String categoryCode) {
		String SQL = "SELECT categoryRoot FROM dbo.CATEGORIES WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			if (categoryCode == null) {
				return null; //입력값 오류
			} else {
				pstmt.setInt(1, Integer.parseInt(categoryCode));
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1); //문서 경로 반환
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null; //데이터베이스 오류
	}
	
	//새로운 문서를 등록하는 메소드
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
			
			return 1; //등록 성공
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; //등록 실패
	}
	
	//문서를 등록하거나 갱신할 때 해당 위치에 같은 이름의 문서가 존재하는지 확인하는 메소드
	public int documentUpdateCheck(String categoryCode, String clientCode, String fileName) {
		String SQL = "SELECT * FROM dbo.FILES "
				+ "WHERE fileName=? AND categoryCode=? AND clientCode ";
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		if (categoryCode == null || fileName == null)
			return -1; //입력값 오류
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileName);
			pstmt.setInt(2, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(3, Integer.parseInt(clientCode));
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return 1; //문서 있음
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; //문서 없음
	}
	
	//문서를 갱신하는 메소드 - 위치가 변하지 않았을 경우
	public int documentUpdate(String fileName, String categoryCode, int userCode, String clientCode, String fileContent) {
		String SQL = "UPDATE FILES SET fileContent=?, dateOfUpdate=GETDATE() WHERE fileName=? AND categoryCode=? AND clientCode ";
		
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (fileContent == null){
				pstmt.setNull(1, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(1, fileContent);
			}
			pstmt.setString(2, fileName);
			pstmt.setInt(3, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(4, Integer.parseInt(clientCode));

			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "update", categoryCode + "/" + clientCode + ": 위치의 문서 갱신");
			
			return 1; //문서 갱신 성공
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; //문서 갱신 실패
	}
	
	//문서를 갱신하는 메소드 - 위치가 변했을 경우
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
			
			return 1; //문서 갱신 성공
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; //문서 갱신 실패
	}
	
	//특정 문서의 정보를 가져오는 메소드
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
				return null; //입력값 오류
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
				return document; //문서 정보 반환
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null; //해당 문서 없음 or 데이터베이스 오류
	}
	
	//문서를 삭제하는 메소드
	public int documentDelete(String fileName, String categoryCode, String clientCode, int userCode) {
		String SQL = "DELETE FILES WHERE fileName=? AND categoryCode=?  AND clientCode ";
		if (clientCode == null) {
			SQL += "IS NULL;";
		} else {
			SQL += "=?;";
		}
		
		if(fileName == null || categoryCode == null) {
			return -1; //입력값 오류
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fileName);
			pstmt.setInt(2, Integer.parseInt(categoryCode));
			if (clientCode != null)
				pstmt.setInt(3, Integer.parseInt(clientCode));
			
			pstmt.executeUpdate();
			logUpload(userCode, fileName, "file", "delete", categoryCode + "/" + clientCode + ": 위치의 문서 삭제");
			return 1; //문서 삭제 성공
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //문서 삭제 실패
	}

	//로그를 기록하는 메소드
	public void logUpload(int logWho, String logWhat, String logWhere, String logHow, String logWhy) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//접속한 DB의 연결을 끝는 메소드
	public void documentClose() {
	    try {
	        if (rs != null) {
	            rs.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); 
	    }

	    try {
	        if (pstmt != null) {
	            pstmt.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try {
	        if (conn != null) {
	            conn.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); 
	    }
	}
	
}
