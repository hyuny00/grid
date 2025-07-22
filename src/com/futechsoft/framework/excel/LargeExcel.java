package com.futechsoft.framework.excel;

import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class LargeExcel {

	private int rowNo;
	private SXSSFWorkbook workbook;
	private SXSSFSheet sheet;


	public LargeExcel(SXSSFWorkbook workbook, SXSSFSheet sheet, int rowNo) {

		this.workbook=workbook;
		this.sheet=sheet;
		this.rowNo=rowNo;

	}


	public int getRowNo() {
		return rowNo;
	}


	public SXSSFWorkbook getWorkbook() {
		return workbook;
	}


	public SXSSFSheet getSheet() {
		return sheet;
	}

}
