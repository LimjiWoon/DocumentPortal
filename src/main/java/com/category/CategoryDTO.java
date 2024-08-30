package com.category;

public class CategoryDTO {
	private int categoryLv;
	private int categoryCode;
	private int categoryCodeUp;
	private int userCode;
	private String userName;
	private String categoryName;
	private String dateOfCreate;
	private String categoryRoot;
	public int getCategoryLv() {
		return categoryLv;
	}
	public void setCategoryLv(int categoryLv) {
		this.categoryLv = categoryLv;
	}
	public int getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(int categoryCode) {
		this.categoryCode = categoryCode;
	}
	public int getCategoryCodeUp() {
		return categoryCodeUp;
	}
	public void setCategoryCodeUp(int categoryCodeUp) {
		this.categoryCodeUp = categoryCodeUp;
	}
	public int getUserCode() {
		return userCode;
	}
	public void setUserCode(int userCode) {
		this.userCode = userCode;
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
	public String getDateOfCreate() {
		return dateOfCreate.substring(0, 19);
	}
	public void setDateOfCreate(String dateOfCreate) {
		this.dateOfCreate = dateOfCreate;
	}
	public String getCategoryRoot() {
		return categoryRoot;
	}
	public void setCategoryRoot(String categoryRoot) {
		this.categoryRoot = categoryRoot;
	}
}
