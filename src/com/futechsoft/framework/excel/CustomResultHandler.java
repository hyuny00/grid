package com.futechsoft.framework.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import com.futechsoft.framework.util.FtMap;


public class CustomResultHandler implements ResultHandler<FtMap> {

	private SXSSFSheet sheet ;
	private int rowNo;
	private ExcelColumn excelColumn;
	private LargeExcel largeExcel;

	CellStyle  defaultStyle =null;
    CellStyle  cellStyleContent =null;
    CellStyle  style = null;

    List<CellStyle> cellStyleList = new ArrayList<CellStyle>();

    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
    private Matcher m =null;
    private String noHtmlValue="";

	public CustomResultHandler(SXSSFSheet sheet,  ExcelColumn excelColumn, LargeExcel largeExcel) {
		this.sheet=sheet;
		this.excelColumn=excelColumn;
		this.largeExcel=largeExcel;
		this.rowNo=largeExcel.getRowNo();

		for(CellVo cellVo  : excelColumn.getCellVos()) {
			defaultStyle = getDefaultStyle();
			cellStyleContent = getCellStyleContent( defaultStyle);
			style = setAign(cellStyleContent, cellVo);

			cellStyleList.add(style);
		}

	}

	@Override
    public void handleResult(ResultContext<? extends FtMap> resultContext) {
    	 setValue(resultContext);
    }


	private void setValue(ResultContext<? extends FtMap>  resultContext) {

		FtMap ftMap =  resultContext.getResultObject();
		Row row= sheet.createRow(++rowNo);

		int colIndex=0;
		for(CellVo cellVo  : excelColumn.getCellVos()) {

			style=cellStyleList.get(colIndex);
			Cell cell=row.createCell(colIndex++);
			cell.setCellStyle(style);

			if(cellVo.getCellType() == CellVo.CELL_NUMBER) {
				  cell.setCellValue(ftMap.getDouble(cellVo.getCellColumn()));

			}else if(cellVo.getCellType() == CellVo.CELL_CURRENCY) {

				  cell.setCellValue(ftMap.getDouble(cellVo.getCellColumn()));

			}else if(cellVo.getCellType() == CellVo.CELL_STRING) {
				noHtmlValue="";

			    m = REMOVE_TAGS.matcher(ftMap.getString(cellVo.getCellColumn()));
			    noHtmlValue= m.replaceAll("");

				if(cellVo.getCodeMap()!=null) {
					cell.setCellValue( cellVo.getCodeMap().getString(noHtmlValue));
				}else {
					cell.setCellValue(noHtmlValue);
				}
			}else {
				cell.setCellValue(ftMap.getString(cellVo.getCellColumn()));

			}

		}

	}
/*
	 public CellStyle getPreferredCellStyle(Cell cell) {
		  // a method to get the preferred cell style for a cell
		  // this is either the already applied cell style
		  // or if that not present, then the row style (default cell style for this row)
		  // or if that not present, then the column style (default cell style for this column)
		  CellStyle cellStyle = cell.getCellStyle();
		  if (cellStyle.getIndex() == 0) cellStyle = cell.getRow().getRowStyle();
		  if (cellStyle == null) cellStyle = cell.getSheet().getColumnStyle(cell.getColumnIndex());
		  if (cellStyle == null) cellStyle = cell.getCellStyle();
		  return cellStyle;
		 }
*/
	private CellStyle getDefaultStyle() {

		 CellStyle defaultStyle = largeExcel.getWorkbook().createCellStyle();
		 defaultStyle.setWrapText(true);
		 return defaultStyle;
	}

	private CellStyle getCellStyleContent(CellStyle cellStyle) {

		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);

		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

		 return cellStyle;
	}

	private CellStyle setAign(CellStyle cellStyle,CellVo cellVo) {

		if(cellVo.getAlign() ==CellVo.ALIGN_CENTER ) {
			cellStyle.setAlignment(HorizontalAlignment.CENTER);

		}else if (cellVo.getAlign() ==CellVo.ALIGN_LEFT) {
			cellStyle.setAlignment(HorizontalAlignment.LEFT);

		}else if (cellVo.getAlign() ==CellVo.ALIGN_RIGHT) {
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);

		}else {
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
		}

		return cellStyle;
	}

}
