package com.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.myclass.XSSEscape;


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
	
	public int maxPage(String logWhere, String logHow) {
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.LOGS ";
		if (logWhere != null && logHow != null) {
			SQL += "WHERE logWhere = '"+logWhere+"' AND logHow='" + logHow +"';";
		} else if (logWhere != null) {
			SQL += "WHERE logWhere = '"+logWhere+"';";
		} else if (logHow != null) {
			SQL += "WHERE logHow='" + logHow +"';";
		} else {
			SQL += ";";
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

	
	public int maxPage(String startDate, String endDate, String logWhere, String logHow) {
		String SQL = "SELECT COUNT(*) AS cnt "
				+ "FROM dbo.LOGS "
				+ "WHERE logWhen BETWEEN ? AND DATEADD(day, 1, ?) ";
		if (logWhere != null && logHow != null) {
			SQL += "AND logWhere = '"+logWhere+"' AND logHow='" + logHow +"';";
		} else if (logWhere != null) {
			SQL += "AND logWhere = '"+logWhere+"';";
		} else if (logHow != null) {
			SQL += "AND logHow='" + logHow +"';";
		} else {
			SQL += ";";
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, startDate);
			pstmt.setString(2, endDate);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}	

	public int maxPage(String searchText, String startDate, String endDate, String logWhere, String logHow) {
		boolean isNumber = XSSEscape.isNumber(searchText) != null;
		String SQL = "SELECT COUNT(*) AS cnt "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";
		
		if (isNumber) {
			SQL += "WHERE l.logWho = ? ";
		} else {
			SQL += "WHERE u.userName = ? ";
		}
		
		SQL += "AND l.logWhen BETWEEN ? AND DATEADD(day, 1, ?) ";
		if (logWhere != null && logHow != null) {
			SQL += "AND logWhere = '"+logWhere+"' AND logHow='" + logHow +"';";
		} else if (logWhere != null) {
			SQL += "AND logWhere = '"+logWhere+"';";
		} else if (logHow != null) {
			SQL += "AND logHow='" + logHow +"';";
		} else {
			SQL += ";";
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (isNumber) {
				pstmt.setInt(1, Integer.parseInt(searchText));
			} else {
				pstmt.setString(1, searchText);
			}
			pstmt.setString(2, startDate);
			pstmt.setString(3, endDate);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}	
	

	public ArrayList<LogDTO> getList(String nowPage, String logWhere, String logHow){
		String SQL = "SELECT l.logWho, u.userName, l.logWhat, l.logWhere, l.logWhen, l.logHow, l.logwhy "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";
				
		if (logWhere != null && logHow != null) {
			SQL += "WHERE l.logWhere = '"+logWhere+"' AND l.logHow='" + logHow +"' ";
		} else if (logWhere != null) {
			SQL += "WHERE l.logWhere = '"+logWhere+"' ";
		} else if (logHow != null) {
			SQL += "WHERE l.logHow='" + logHow +"' ";
		}
				
		SQL += "ORDER BY l.logWhen ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
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

	public ArrayList<LogDTO> getSearch(String nowPage, String startDate, String endDate, String logWhere, String logHow){
		String SQL = "SELECT l.logWho, u.userName, l.logWhat, l.logWhere, l.logWhen, l.logHow, l.logwhy "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho "
				+ "WHERE l.logWhen BETWEEN ? AND DATEADD(day, 1, ?) ";
		
		if (logWhere != null && logHow != null) {
			SQL += "AND logWhere = '"+logWhere+"' AND logHow='" + logHow +"' ";
		} else if (logWhere != null) {
			SQL += "AND logWhere = '"+logWhere+"' ";
		} else if (logHow != null) {
			SQL += "AND logHow='" + logHow +"' ";
		}
		
		SQL += "ORDER BY l.logWhen ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<LogDTO> list = new ArrayList<LogDTO>();
		
		if (startDate == null || endDate == null) {
			return list;
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, startDate);
			pstmt.setString(2, endDate);
			
			if (nowPage == null) {
				pstmt.setInt(3, 0);
			} else {
				pstmt.setInt(3, (Integer.parseInt(nowPage) -1) * 10);
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

	public ArrayList<LogDTO> getSearch(String nowPage, String searchText, String startDate, String endDate, String logWhere, String logHow){
		ArrayList<LogDTO> list = new ArrayList<LogDTO>();
		String SQL = "SELECT l.logWho, u.userName, l.logWhat, l.logWhere, l.logWhen, l.logHow, l.logwhy "
				+ "FROM dbo.LOGS l "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = l.logWho ";
		boolean isNumber = XSSEscape.isNumber(searchText) != null;
		
		if (startDate == null || endDate == null || searchText == null) {
			return list;
		}
		
		if (isNumber) {
			SQL += "WHERE l.logWho = ? ";
		} else {
			SQL += "WHERE u.userName = ? ";
		}
		
		SQL += "AND l.logWhen BETWEEN ? AND DATEADD(day, 1, ?) ";
		if (logWhere != null && logHow != null) {
			SQL += "AND logWhere = '"+logWhere+"' AND logHow='" + logHow +"' ";
		} else if (logWhere != null) {
			SQL += "AND logWhere = '"+logWhere+"' ";
		} else if (logHow != null) {
			SQL += "AND logHow='" + logHow +"' ";
		}
		SQL += "ORDER BY l.logWhen ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (isNumber) {
				pstmt.setInt(1, Integer.parseInt(searchText));
			} else {
				pstmt.setString(1, searchText);
			}
			pstmt.setString(2, startDate);
			pstmt.setString(3, endDate);
			
			if (nowPage == null) {
				pstmt.setInt(4, 0);
			} else {
				pstmt.setInt(4, (Integer.parseInt(nowPage) -1) * 10);
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
