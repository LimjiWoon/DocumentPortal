package com.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class LogDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private LogDTO log;
	
	public LogDAO() {
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

	private String filterSQL(String SQL, String startDate, String endDate, String logWhere, 
			String logHow, String searchField, String searchText) {
		//그 어떠한 값도 들어오지 않은 경우
		if (startDate == null && endDate == null && logWhere == null && 
				logHow == null && searchField == null && searchText == null) {
			return SQL;
		}
		
		StringBuilder query = new StringBuilder(SQL);
		ArrayList<String> conditions = new ArrayList<String>();
		
		if (startDate != null && endDate != null) {
			conditions.add(" l.logWhen BETWEEN '"+startDate+"' AND DATEADD(day, 1, '"+endDate+"') ");
		} else if (startDate != null) {
			conditions.add(" l.logWhen > '"+startDate+"' ");
		} else if (endDate != null) {
			conditions.add(" l.logWhen < DATEADD(day, 1, '"+endDate+"') ");
		}

		if (logWhere != null) {
			conditions.add(" l.logWhere ='"+logWhere+"' ");
		}

		if (logHow != null) {
			conditions.add(" l.logHow ='"+logHow+"' ");
		}
		
		if (searchField != null && searchText != null) {
			conditions.add(" " + searchField.trim() + " LIKE '%" + searchText.trim() + "%' ");
		}
		
		if (!conditions.isEmpty()) {
		    query.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		
		return query.toString();
	}
	
	
	public int maxPage(String startDate, String endDate, String logWhere, String logHow) {
		String SQL = "SELECT COUNT(*) AS cnt "
				+ "FROM dbo.LOGS l ";
		SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, null, null);
		
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

	public int maxPage(String startDate, String endDate, String logWhere, String logHow, String searchField, String searchText) {
		String SQL = "SELECT COUNT(*) AS cnt "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";

		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, null, null);
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
	

	public ArrayList<LogDTO> getList(String startDate, String endDate, String logWhere, String logHow, String nowPage){
		String SQL = "SELECT l.logWho, u.userName, l.logWhat, l.logWhere, l.logWhen, l.logHow, l.logwhy "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";
		SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, null, null);
				
		SQL += " ORDER BY l.logWhen ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<LogDTO> list = new ArrayList<LogDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				log = new LogDTO();
				log.setLogWho(rs.getInt(1));
				log.setLogWhoName(rs.getString(2));
				log.setLogWhat(rs.getString(3));
				log.setLogWhere(rs.getString(4));
				log.setLogWhen(rs.getString(5));
				log.setLogHow(rs.getString(6));
				log.setLogWhy(rs.getString(7));
				list.add(log);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	public ArrayList<LogDTO> getSearch(String startDate, String endDate, String logWhere, String logHow, String nowPage, String searchField, String searchText){
		String SQL = "SELECT l.logWho, u.userName, l.logWhat, l.logWhere, l.logWhen, l.logHow, l.logwhy "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";

		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, null, null);
		}
		
		SQL += " ORDER BY l.logWhen ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<LogDTO> list = new ArrayList<LogDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				log = new LogDTO();
				log.setLogWho(rs.getInt(1));
				log.setLogWhoName(rs.getString(2));
				log.setLogWhat(rs.getString(3));
				log.setLogWhere(rs.getString(4));
				log.setLogWhen(rs.getString(5));
				log.setLogHow(rs.getString(6));
				log.setLogWhy(rs.getString(7));
				list.add(log);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	public ArrayList<LogDTO> getExcel(String startDate, String endDate, String logWhere, String logHow, String searchField, String searchText){
		String SQL = "SELECT l.logWho, u.userName, l.logWhat, l.logWhere, l.logWhen, l.logHow, l.logwhy "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";
		
		if (searchField != null && searchText != null) {
			if (searchText.trim() != ""){
				SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, searchField, searchText);
			} else {
				SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, null, null);
			}
		} else {
			SQL = filterSQL(SQL, startDate, endDate, logWhere, logHow, null, null);
		}
		
		SQL += " ORDER BY l.logWhen ASC;";
		ArrayList<LogDTO> list = new ArrayList<LogDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				log = new LogDTO();
				log.setLogWho(rs.getInt(1));
				log.setLogWhoName(rs.getString(2));
				log.setLogWhat(rs.getString(3));
				log.setLogWhere(rs.getString(4));
				log.setLogWhen(rs.getString(5));
				log.setLogHow(rs.getString(6));
				log.setLogWhy(rs.getString(7));
				list.add(log);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
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
		
	public void logClose() {
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
