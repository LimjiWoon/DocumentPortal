package com.myclass;

import com.user.UserDTO;
import com.category.CategoryDTO;
import com.client.ClientDTO;
import com.document.DocumentDTO;
import com.log.LogDTO;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateExcel {
	
	public static Workbook userExcel(ArrayList<UserDTO> users) {
		int cnt = 1;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();
		Row titleRow = sheet.createRow(0);
		Row row;
		
		titleRow.createCell(0).setCellValue("사번");
		titleRow.createCell(1).setCellValue("이름");
		titleRow.createCell(2).setCellValue("고객사(권한)");
		titleRow.createCell(3).setCellValue("문서 목록(권한)");
		titleRow.createCell(4).setCellValue("문서(권한)");
		titleRow.createCell(5).setCellValue("비밀번호 변경일");
		titleRow.createCell(6).setCellValue("계정 잠금 여부");
		titleRow.createCell(7).setCellValue("퇴사 여부");
		
		for (UserDTO user : users) {
			row = sheet.createRow(cnt);
			
			row.createCell(0).setCellValue(user.getUserCode());
			row.createCell(1).setCellValue(user.getUserName());
			row.createCell(2).setCellValue(user.isClient());
			row.createCell(3).setCellValue(user.isCategory());
			row.createCell(4).setCellValue(user.isDocument());
			if (user.getDateOfPassword() == null) {
				row.createCell(5).setCellValue(user.getDateOfPassword());
			} else {
				row.createCell(5).setCellValue(Integer.parseInt(user.getDateOfPassword()));
			}
			row.createCell(6).setCellValue(user.isLock());
			row.createCell(7).setCellValue(user.isRetire());
			
			cnt += 1;
		}
		
		return workBook;
	}

	
	public static Workbook categoryExcel(ArrayList<CategoryDTO> categories) {
		int cnt = 1;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();
		Row titleRow = sheet.createRow(0);
		Row row;
		
		titleRow.createCell(0).setCellValue("코드");
		titleRow.createCell(1).setCellValue("문서 목록명");
		titleRow.createCell(2).setCellValue("작성자");
		titleRow.createCell(3).setCellValue("생성일");
		
		for (CategoryDTO category : categories) {
			row = sheet.createRow(cnt);

			row.createCell(0).setCellValue(category.getCategoryCode());
			row.createCell(1).setCellValue(category.getCategoryName());
			row.createCell(2).setCellValue(category.getUserName());
			row.createCell(3).setCellValue(category.getDateOfCreate());
			
			cnt += 1;
		}
		
		return workBook;
	}

	
	public static Workbook clientExcel(ArrayList<ClientDTO> clients) {
		int cnt = 1;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();
		Row titleRow = sheet.createRow(0);
		Row row;
		
		titleRow.createCell(0).setCellValue("코드");
		titleRow.createCell(1).setCellValue("고객사명");
		titleRow.createCell(2).setCellValue("작성자");
		titleRow.createCell(3).setCellValue("최근 수정일");
		titleRow.createCell(4).setCellValue("사용 유무");
		
		for (ClientDTO client : clients) {
			row = sheet.createRow(cnt);

			row.createCell(0).setCellValue(client.getClientCode());
			row.createCell(1).setCellValue(client.getClientName());
			row.createCell(2).setCellValue(client.getUserName());
			row.createCell(3).setCellValue(client.getDateOfUpdate());
			row.createCell(4).setCellValue(client.isUse());
			
			cnt += 1;
		}
		
		return workBook;
	}

	
	public static Workbook documentExcel(ArrayList<DocumentDTO> documents) {
		int cnt = 1;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();
		Row titleRow = sheet.createRow(0);
		Row row;
		
		titleRow.createCell(0).setCellValue("문서 제목");
		titleRow.createCell(1).setCellValue("저장된 문서명");
		titleRow.createCell(2).setCellValue("작성자");
		titleRow.createCell(3).setCellValue("고객사");
		titleRow.createCell(4).setCellValue("문서 위치");
		titleRow.createCell(5).setCellValue("최근 수정일");
		titleRow.createCell(6).setCellValue("설명");
		
		for (DocumentDTO document : documents) {
			row = sheet.createRow(cnt);
			
			row.createCell(0).setCellValue(document.getFileTitle());
			row.createCell(1).setCellValue(document.getFileName());
			row.createCell(2).setCellValue(document.getUserName());
			row.createCell(3).setCellValue(document.getClientName());
			row.createCell(4).setCellValue(document.getCategoryName());
			row.createCell(5).setCellValue(document.getDateOfUpdate());
			row.createCell(6).setCellValue(document.getFileContent());
			
			cnt += 1;
		}
		
		return workBook;
	}

	
	public static Workbook logExcel(ArrayList<LogDTO> logs) {
		int cnt = 1;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();
		Row titleRow = sheet.createRow(0);
		Row row;
		
		titleRow.createCell(0).setCellValue("사번");
		titleRow.createCell(1).setCellValue("사용자");
		titleRow.createCell(2).setCellValue("페이지");
		titleRow.createCell(3).setCellValue("시간");
		titleRow.createCell(4).setCellValue("대상");
		titleRow.createCell(5).setCellValue("행동");
		titleRow.createCell(6).setCellValue("설명");
		
		for (LogDTO log : logs) {
			row = sheet.createRow(cnt);

			row.createCell(0).setCellValue(log.getLogWho());
			row.createCell(1).setCellValue(log.getLogWhoName());
			row.createCell(2).setCellValue(log.getLogWhere());
			row.createCell(3).setCellValue(log.getLogWhen());
			row.createCell(4).setCellValue(log.getLogWhat());
			row.createCell(5).setCellValue(log.getLogHow());
			row.createCell(6).setCellValue(log.getLogWhy());
			
			cnt += 1;
		}
		
		return workBook;
	}
	
}
