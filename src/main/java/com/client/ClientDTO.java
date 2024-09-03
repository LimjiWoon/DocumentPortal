package com.client;

public class ClientDTO {

	private int clientCode;
	private String clientName;
	private String clientContent;
	private int categoryCode;
	private int userCode;
	private String dateOfUpdate;
	private boolean isUse;
	private String userName;
	private String categoryName;
	
	
	public int getClientCode() {
		return clientCode;
	}
	public void setClientCode(int clientCode) {
		this.clientCode = clientCode;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientContent() {
		if (clientContent == null)
			return "";
		return clientContent;
	}
	public void setClientContent(String clientContent) {
		this.clientContent = clientContent;
	}
	public int getUserCode() {
		return userCode;
	}
	public void setUserCode(int userCode) {
		this.userCode = userCode;
	}
	public String getDateOfUpdate() {
		return dateOfUpdate;
	}
	public void setDateOfUpdate(String dateOfUpdate) {
		this.dateOfUpdate = dateOfUpdate.substring(0, 16);
	}
	public boolean isUse() {
		return isUse;
	}
	public boolean getIsUse() {
		return isUse;
	}
	public void setUse(int isUse) {
		this.isUse = isUse==1;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	
}
