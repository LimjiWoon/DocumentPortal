package com.client;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;



public class ClientDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private ClientDTO client;
	
	public ClientDAO() {
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
	

	public int maxPage() { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.CLIENTS;";
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1) - 1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	public int maxPage(String searchField, String searchText) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt "
				+ "FROM  dbo.CLIENTS c "
				+ "LEFT JOIN dbo.USERS u ON c.userCode = u.userCode "
				+ "LEFT JOIN dbo.CATEGORIES cat ON c.categoryCode = cat.categoryCode ";
		if (searchText.trim() != "")
				SQL += "WHERE " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1) - 1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	

	

	public ArrayList<ClientDTO> getList(){
		String SQL = "SELECT clientCode, clientName FROM  dbo.CLIENTS ORDER BY clientName ASC;";
		ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				client = new ClientDTO();
				client.setClientCode(rs.getInt(1));
				client.setClientName(rs.getString(2));
				list.add(client);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

	public ArrayList<ClientDTO> getList(String nowPage){
		String SQL = "SELECT c.clientCode, c.clientName, cat.categoryName, u.userName, c.dateOfUpdate, c.isUse "
				+ "FROM  dbo.CLIENTS c "
				+ "LEFT JOIN dbo.USERS u ON c.userCode = u.userCode "
				+ "LEFT JOIN dbo.CATEGORIES cat ON c.categoryCode = cat.categoryCode "
				+ "ORDER BY c.clientCode DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				client = new ClientDTO();
				client.setClientCode(rs.getInt(1));
				client.setClientName(rs.getString(2));
				client.setCategoryName(rs.getString(3));
				client.setUserName(rs.getString(4));
				client.setDateOfUpdate(rs.getString(5));
				client.setUse(rs.getInt(6));
				list.add(client);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public ArrayList<ClientDTO> getSearch(String nowPage, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT c.clientCode, c.clientName, cat.categoryName, u.userName, c.dateOfUpdate, c.isUse "
				+ "FROM  dbo.CLIENTS c "
				+ "LEFT JOIN dbo.USERS u ON c.userCode = u.userCode "
				+ "LEFT JOIN dbo.CATEGORIES cat ON c.categoryCode = cat.categoryCode ";
		if (searchText.trim() != "")
			SQL += " WHERE " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				client = new ClientDTO();
				client.setClientCode(rs.getInt(1));
				client.setClientName(rs.getString(2));
				client.setCategoryName(rs.getString(3));
				client.setUserName(rs.getString(4));
				client.setDateOfUpdate(rs.getString(5));
				client.setUse(rs.getInt(6));
				list.add(client);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	public void updateUse(int clientCode, String status, int userCode, String clientName) {
		String SQL = "UPDATE CLIENTS SET isUSE=? FROM CLIENTS WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (status.equals("O")) {
				pstmt.setInt(1, 1);
			} else {
				pstmt.setInt(1, 0);
			}
			pstmt.setInt(2, clientCode);
			pstmt.executeUpdate();
			logUpload(userCode, clientName, "client", "update", "사용유무 변경");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int clientUpload(String clientName, int categoryCode, String clientContent, int userCode) {
		String SQL = "INSERT INTO dbo.CLIENTS (clientName, categoryCode, clientContent, userCode) "
				+ "VALUES (?, ?, ?, ?);";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, clientName);
			pstmt.setInt(2, categoryCode);
			pstmt.setString(3, clientContent);
			pstmt.setInt(4, userCode);

			pstmt.executeUpdate();
			logUpload(userCode, clientName, "client", "create", "신규 고객사 생성");
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	} 
	
	
	public boolean clientUniqueName(String clientName) {
		String SQL = "SELECT clientName FROM dbo.CLIENTS WHERE clientName=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, clientName);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				return true;
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}

		
		return false;
	}
	
	public ClientDTO getClientInfo(int clientCode) {
		ClientDTO client = new ClientDTO();
		String SQL = "SELECT clientName, clientContent, categoryCode FROM dbo.CLIENTS WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, clientCode);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				client.setClientName(rs.getString(1));
				client.setClientContent(rs.getString(2));;
				client.setCategoryCode(rs.getInt(3));
				client.setClientCode(clientCode);
				return client;
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int clientUpdate(int clientCode, String categoryCode, String hiddenCategoryCode, String clientName, 
			String hiddenClientName, String clientContent, String hiddenClientContent, int userCode) {
		String SQL = "UPDATE dbo.CLIENTS SET dateOfUpdate=GETDATE()";
		boolean isCode = categoryCode.equals(hiddenCategoryCode);
		boolean isName = clientName.equals(hiddenClientName);
		boolean isContent = clientContent.equals(hiddenClientContent);
		int cnt = 1;
		String logContent = "고객사 수정: ";
		
		if (isCode && isName && isContent)
			return 0;
		
		
		if (!isCode) 
			SQL += ", categoryCode=?";
		if (!isName) 
			SQL += ", clientName=?";
		if (!isContent) 
			SQL += ", clientContent=?";
		
		SQL += " WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (!isCode) {
				pstmt.setInt(cnt, Integer.parseInt(categoryCode));
				logContent += "<br> 폴더 위치 변겅: (전) " + hiddenCategoryCode + " (후)" + categoryCode;
				cnt += 1;
			}
			if (!isName) {
				pstmt.setString(cnt, clientName);
				logContent += "<br> 이름 변경: (전) " + hiddenClientName + " (후)" + clientName;
				cnt += 1;
			}
			if (!isContent) {
				pstmt.setString(cnt, clientContent);
				logContent += "<br> 고객사 설명 변경";
				cnt += 1;
			}
			
			pstmt.setInt(cnt, clientCode);
			pstmt.executeUpdate();
			logUpload(userCode, hiddenClientName, "client", "update", logContent);
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public String getClientName(int clientCode) {
		String SQL = "SELECT clientName FROM dbo.CLIENTS WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, clientCode);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1);
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
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
	
	
	public void clientClose() {
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
