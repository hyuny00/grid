package com.futechsoft.framework.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.futechsoft.framework.exception.BizException;
import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.exception.FileDownloadException;
import com.futechsoft.framework.util.FtMap;

@Component
public class ExcelHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelHelper.class);

	private final int ROW_ACCESS_WINDOW_SIZE = 100;
	private SXSSFWorkbook sxssfWorkbook = null;

	private HSSFWorkbook workBook = null;
	private Workbook wb = null;

	private Sheet wSheet = null;
	private Row wRow = null;
	private Cell wCell = null;

	private int curSheetIdx = -1;	//read하고 있는 현재 sheet의 인덱스
	private int curRowIdx = -1;		//read하고 있는 현재 row의 인덱스
	private short curCellIdx = -1;	//read하고 있는 현재 cell의 인덱스

	private int maxRowCnt = 60000;		//read할 최대 row 갯수
	private int maxCellCnt = 0;		//read할 최대 cell 갯수


	public void excelDownload(HttpServletResponse response, File templateFileName, List<FtMap> list, ExcelColumn excelColumn ) throws IOException, InvalidFormatException {



		ZipSecureFile.setMinInflateRatio(0);
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(templateFileName);

	    Sheet originSheet = xssfWorkbook.getSheetAt(0);
	    int rowNo = originSheet.getLastRowNum();

	    // SXSSF 생성
	    sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, ROW_ACCESS_WINDOW_SIZE);
	    Sheet sheet = sxssfWorkbook.getSheetAt(0);

	    CellStyle  defaultStyle =null;
	    CellStyle  cellStyleContent =null;
	    CellStyle style = null;
	    CellVo cellVos[]= excelColumn.getCellVos();

	    List<CellStyle> cellStyleList = new ArrayList<CellStyle>();

		for(CellVo cellVo  : excelColumn.getCellVos()) {
			defaultStyle = getDefaultStyle();
			cellStyleContent = getCellStyleContent( defaultStyle);

		    if(cellVo.getFormat()!= null) {
				  DataFormat dataformat = sxssfWorkbook.createDataFormat();
				  cellStyleContent.setDataFormat(dataformat.getFormat(cellVo.getFormat()));
			 }

		    style = setAign(cellStyleContent, cellVo);

			cellStyleList.add(style);
		}


	    for(FtMap ftMap : list) {


	        Row row = sheet.createRow(++rowNo);

	        int colIndex=0;
	        Cell cell;
			for(CellVo cellVo : cellVos) {


				style=cellStyleList.get(colIndex);
				cell=row.createCell(colIndex++);
				cell.setCellStyle(style);

				if(cellVo.getCellType() == CellVo.CELL_NUMBER) {
					  cell.setCellValue(ftMap.getDouble(cellVo.getCellColumn()));
					  cell.setCellStyle(style);

				}else if(cellVo.getCellType() == CellVo.CELL_CURRENCY) {
					  cell.setCellValue(ftMap.getDouble(cellVo.getCellColumn()));
					  cell.setCellStyle(style);

				}else if(cellVo.getCellType() == CellVo.CELL_STRING) {
					if(cellVo.getCodeMap()!=null) {
						cell.setCellValue( cellVo.getCodeMap().getString(ftMap.getString(cellVo.getCellColumn())));
					}else {
						cell.setCellValue(ftMap.getString(cellVo.getCellColumn()));
					}
					cell.setCellStyle(style);

				}else if(cellVo.getCellType() == CellVo.CELL_ARRAY) {

					if(cellVo.getCodeMap()!=null) {

						Object[] tmpArr= ftMap.getObjectArray(cellVo.getCellColumn());

						String tmpValue="";
						for(Object s : tmpArr) {
							if(s == null) continue;
							tmpValue+= cellVo.getCodeMap().getString((String)s)+",";
						}

						cell.setCellValue(tmpValue);


					}else {
						cell.setCellValue(ftMap.getString(cellVo.getCellColumn()));
					}
					cell.setCellStyle(style);
				}else {
					cell.setCellValue(ftMap.getString(cellVo.getCellColumn()));
					cell.setCellStyle(style);
				}


			}
	    }

	    ((SXSSFSheet)sheet).flushRows(list.size());

	    response.setHeader("Set-Cookie", "fileDownload=true; path=/");

		response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode( templateFileName.getName(), "utf-8") + "\";");

		try (OutputStream out = response.getOutputStream()) {
			sxssfWorkbook.write(out);
			sxssfWorkbook.dispose();
		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
//			e.printStackTrace();
			LOGGER.error(e.toString());
			throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
		}finally {


			if (xssfWorkbook != null) {
				xssfWorkbook.close();
			}

			if (sxssfWorkbook != null) {
				sxssfWorkbook.close();
				sxssfWorkbook.dispose();
			}
		}


	}



	public void excelDownload(HttpServletResponse response, File templateFileName, List<FtMap> list, String[] columnValue) throws IOException, InvalidFormatException {
		ZipSecureFile.setMinInflateRatio(0);
	    XSSFWorkbook xssfWorkbook = new XSSFWorkbook(templateFileName);

	    Sheet originSheet = xssfWorkbook.getSheetAt(0);
	    int rowNo = originSheet.getLastRowNum();

	    // SXSSF 생성
	    sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, ROW_ACCESS_WINDOW_SIZE);
	    Sheet sheet = sxssfWorkbook.getSheetAt(0);

	    CellStyle defaultStyle = getDefaultStyle();
	    CellStyle cellStyleContent = getCellStyleContent( defaultStyle);




	    for(FtMap ftMap : list) {
	        Row row = sheet.createRow(++rowNo);

	        int colIndex=0;
			for(String value : columnValue) {
				Cell cell=row.createCell(colIndex++);
				cell.setCellValue(ftMap.getString(value));
				cell.setCellStyle(cellStyleContent);
			}
	    }

	    ((SXSSFSheet)sheet).flushRows(list.size());

	    response.setHeader("Set-Cookie", "fileDownload=true; path=/");

		response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode( templateFileName.getName(), "utf-8") + "\";");

		try (OutputStream out = response.getOutputStream()) {
			sxssfWorkbook.write(out);
			sxssfWorkbook.dispose();
		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
//			e.printStackTrace();
			LOGGER.error(e.toString());
			throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
		}finally {

			if (xssfWorkbook != null) {
				xssfWorkbook.close();
			}

			if (sxssfWorkbook != null) {
				sxssfWorkbook.close();
				sxssfWorkbook.dispose();
			}
		}


	}


	private CellStyle getDefaultStyle() {

		 CellStyle defaultStyle = sxssfWorkbook.createCellStyle();
		 defaultStyle.setWrapText(true);
		 return defaultStyle;
	}

	private CellStyle getCellStyleContent(CellStyle cellStyle) {

/*
		Font font = sxssfWorkbook.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setBold(Boolean.TRUE);
		font.setFontName("맑은고딕");

		// 제목 스타일에 폰트 적용, 정렬
		styleHd.setFont(font);

*/
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



	public LargeExcel preparedLargeExcel(File templateFileName) throws InvalidFormatException, IOException {
		 ZipSecureFile.setMinInflateRatio(0);
		 XSSFWorkbook xssfWorkbook = new XSSFWorkbook(templateFileName);

		 Sheet originSheet = xssfWorkbook.getSheetAt(0);
		 int rowNo = originSheet.getLastRowNum();

		 SXSSFWorkbook workbook;
		 SXSSFSheet sheet ;

		 workbook =  new SXSSFWorkbook(xssfWorkbook,ROW_ACCESS_WINDOW_SIZE);
		 workbook.setCompressTempFiles(true);

		 sheet = workbook.getSheetAt(0);

		 LargeExcel largeExcel =  new LargeExcel(workbook, sheet, rowNo);
		 return largeExcel;
	}

	public void endLargeExcel(HttpServletResponse response, SXSSFWorkbook workbook, String fileNm) throws Exception {
		response.reset();
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		response.setHeader("Content-Disposition", "attachment; filename=\"" +  java.net.URLEncoder.encode( fileNm, "utf-8") + "\";");



		try (OutputStream out = response.getOutputStream()) {
			workbook.write(out);
			workbook.dispose();
		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
//			e.printStackTrace();
			LOGGER.error(e.toString());
			throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
		}finally {
			if (workbook!= null) {
				workbook.close();
				workbook.dispose();
			}
		}
	}

	private volatile static ExcelHelper excelInstance;

	public static ExcelHelper getInstance(){
		if(excelInstance == null){
			synchronized(ExcelHelper.class){
					excelInstance = new ExcelHelper();
			}
		}
		return excelInstance;
	}

	/**
	 * 엑셀파일을 로딩하여 workbook을 생성한다.
	 * @param fileName String
	 * @throws Exception
	 */
	public void loadWorkbook(String fileName, String file_ext) throws Exception {
		FileInputStream fis = null;
		if("xls".equals(file_ext)){
			 fis = new FileInputStream(fileName);

				POIFSFileSystem fs =null;
			try {
				 fs =new POIFSFileSystem(fis);

				workBook = new HSSFWorkbook(fs);
			} catch (IOException ex) {
				throw new Exception("Workbook load failed ", ex);
			}finally{
				if(fs != null){
					fs.close();
				}

				if(workBook != null){try{	workBook.close();	}catch(Exception e){LOGGER.error(e.toString());}}
				if(fis != null){try{	fis.close();	}catch(Exception e){LOGGER.error(e.toString());}}
			}
		}else if("xlsx".equals(file_ext)){
			try {
				//wb = new XSSFWorkbook(new FileInputStream(fileName));
				wb = new XSSFWorkbook(fileName);
			} catch (Exception e) {

				throw new Exception("Workbook load failed ", e);
				//e.printStackTrace();

			}finally{
				if(wb != null){try{	wb.close();	}catch(Exception e){LOGGER.error(e.toString());}}
				if(fis != null){try{	fis.close();	}catch(Exception e){LOGGER.error(e.toString());}}
			}
		}
	}

	/**
	 * 특정 인덱스의 worksheet를 load한다.
	 * @param sheetIdx int
	 * @throws Exception
	 */
	public Sheet loadWorkSheet(int sheetIdx) throws Exception {

			if(workBook == null){
				throw new Exception("Workbook is null");
			}
			wSheet = workBook.getSheetAt(sheetIdx);
			this.curSheetIdx = sheetIdx;
			curRowIdx = -1;
			curCellIdx = -1;

		return wSheet;
	}

	/**
	 * 특정 workRow의 특정 workCell로 이동한다.
	 * @param rowIdx int
	 * @param cellIdx short
	 * @throws Exception
	 * @return boolean
	 */
	public boolean moveCell(int rowIdx, short cellIdx) throws Exception {

		int tmpRowIdx = getCurRowIdx();

		if(moveRow(rowIdx))
		{
			if(moveCell(cellIdx))
				return true;

			moveRow(tmpRowIdx);
		}
		return false;
	}

	/**
	 * 현재 행의 특정 위치의 workCell로 이동
	 * @param cellIdx short
	 * @throws Exception
	 * @return boolean
	 */

	public boolean moveCell(short cellIdx) throws Exception {
		if (cellIdx < 0) {
			return false;
		}

		if (wRow == null){
			throw new Exception(this.curSheetIdx + "번째 workRow가 없습니다.");
		}

		if (this.maxCellCnt > 0){
			if (cellIdx > this.maxCellCnt){
				return false;
			}
		}

		if (cellIdx == this.curCellIdx){
			return true;
		}

//		if (cellIdx > getLastCellNum() || cellIdx < getFirstCellNum()){
//			return false;
//		}

		wCell = wRow.getCell(cellIdx);
		this.curCellIdx = cellIdx;
		return true;
	}

	/**
	 * 선택한 workCell의 데이타 존재여부
	 * @return boolean
	 */
	public boolean isBlankCell(){
		if (wCell == null){
			return true;
		}
		if (wCell.getCellType() == CellType.BLANK){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 특정 위치의 workRow로 이동한다.
	 * @param rowIdx int
	 * @throws Exception
	 * @return boolean
	 */
	public boolean moveRow(int rowIdx) throws Exception {
		if (rowIdx < 0) {
			return false;
		}

		if (wSheet == null){
			throw new Exception(this.curSheetIdx + "번째 worksheet가 없습니다.");
		}

		if (this.maxRowCnt > 0){
			if (rowIdx > this.maxRowCnt){
				return false;
			}
		}

		if (rowIdx == this.curRowIdx){
			return true;
		}

//		if (rowIdx > getLastRowNum() || rowIdx < getFirstRowNum()){
//			return false;
//		}

		wRow = wSheet.getRow(rowIdx);
		curRowIdx = rowIdx;
		curCellIdx = -1;
		return true;
	}
	/**
	 * 특정 인덱스의 worksheet를 load한다.
	 * @param sheetIdx int
	 * @throws Exception
	 */
	public Sheet loadWSheet(int sheetIdx) throws Exception {

			if(wb == null){
				throw new Exception("Workbook is null");
			}
			wSheet = wb.getSheetAt(sheetIdx);
			this.curSheetIdx = sheetIdx;
			curRowIdx = -1;
			curCellIdx = -1;

		return wSheet;
	}

	/**
	 * 선택한 workCell의 데이타 존재여부
	 * @return boolean
	 */
	public boolean isBlnkCell(){
		if (wCell == null){
			return true;
		}
		if (wCell.getCellType() == CellType.BLANK){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 특정 위치의 workRow로 이동한다.
	 * @param rowIdx int
	 * @throws Exception
	 * @return boolean
	 */
	public boolean mvRow(int rowIdx) throws Exception {
		if (rowIdx < 0) {
			return false;
		}

		if (wSheet == null){
			throw new Exception(this.curSheetIdx + "번째 worksheet가 없습니다.");
		}

		if (this.maxRowCnt > 0){
			if (rowIdx > this.maxRowCnt){
				return false;
			}
		}

		if (rowIdx == this.curRowIdx){
			return true;
		}

//		if (rowIdx > getLastRowNum() || rowIdx < getFirstRowNum()){
//			return false;
//		}

		wRow = wSheet.getRow(rowIdx);
		curRowIdx = rowIdx;
		curCellIdx = -1;
		return true;
	}
	/**
	 * 특정 workRow의 특정 workCell로 이동한다.
	 * @param rowIdx int
	 * @param cellIdx short
	 * @throws Exception
	 * @return boolean
	 */
	public boolean mvCell(int rowIdx, short cellIdx) throws Exception {

		int tmpRowIdx = getCurRowIdx();

		if(mvRow(rowIdx))
		{
			if(mvCell(cellIdx))
				return true;

			mvRow(tmpRowIdx);
		}
		return false;
	}


	/**
	 * 현재 행의 특정 위치의 workCell로 이동
	 * @param cellIdx short
	 * @throws Exception
	 * @return boolean
	 */

	public boolean mvCell(short cellIdx) throws Exception {
		if (cellIdx < 0) {
			return false;
		}

		if (wRow == null){
			throw new Exception(this.curSheetIdx + "번째 workRow가 없습니다.");
		}

		if (this.maxCellCnt > 0){
			if (cellIdx > this.maxCellCnt){
				return false;
			}
		}

		if (cellIdx == this.curCellIdx){
			return true;
		}

//		if (cellIdx > getLastCellNum() || cellIdx < getFirstCellNum()){
//			return false;
//		}

		wCell = wRow.getCell(cellIdx);
		this.curCellIdx = cellIdx;
		return true;
	}

	/**
	 * workCell의 데이타를 무조건 string으로 반환
	 * @throws Exception
	 * @return String
	 */
	public String getValToString() throws Exception
	{
		if(wCell == null){
			return "";
		}
		if (isErrCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}

		String str = null;
		if(wCell.getCellType() == CellType.NUMERIC){
			if(DateUtil.isCellDateFormatted(wCell)){

				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
				str = fmt.format(wCell.getDateCellValue());
			}else{

				str = new Integer(new Double(getNumValue()).intValue()).toString();
			}
		}else if (wCell.getCellType() == CellType.BOOLEAN){
			str = new Boolean(getBoolValue()).toString();
		}else if (wCell.getCellType() == CellType.STRING){
			str = getStrValue();
		}else {
			str = "";
		}

		return str;
	}

	public String getStrValue() throws Exception
	{
		if(wCell == null)
			return "";
		if (isErrCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}
		return wCell.getStringCellValue().trim();
	}

	/**
	 * boolean value 반환
	 * @throws Exception
	 * @return boolean
	 */
	public boolean getBoolValue() throws Exception {
		if(wCell == null){
			throw new Exception("지정된 workCell이 없습니다.");
		}
		if (isErrCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}

		return wCell.getBooleanCellValue();
	}

	/**
	 * 숫자 값 반환(double)
	 * @throws Exception
	 * @return double
	 */
	public double getNumValue() throws Exception
	{
		if(wCell == null)
			throw new Exception("Cell이 지정되지 않았음");
		if (isErrCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}

		return wCell.getNumericCellValue();
	}

	/**
	 * workCell의 error 여부
	 * @return boolean
	 */
	public boolean isErrCell(){
		if (wCell == null){
			return true;
		}

		if (wCell.getCellType() == CellType.ERROR){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * workCell의 데이타를 무조건 string으로 반환
	 * @throws Exception
	 * @return String
	 */
	public String getValueToString() throws Exception
	{
		if(wCell == null){
			return "";
		}
		if (isErrorCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}

		String str = null;
		if(wCell.getCellType() == CellType.NUMERIC){
			if(DateUtil.isCellDateFormatted(wCell)){

				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
				str = fmt.format(wCell.getDateCellValue());
			}else{

				str = new Integer(new Double(getNumericValue()).intValue()).toString();
			}
		}else if (wCell.getCellType() == CellType.BOOLEAN){
			str = new Boolean(getBooleanValue()).toString();
		}else if (wCell.getCellType() == CellType.STRING){
			str = getStringValue();
		}else {
			str = "";
		}

		return str;
	}

	/**
	 * string value 반환
	 * @throws Exception
	 * @return String
	 */
	public String getStringValue() throws Exception
	{
		if(wCell == null)
			return "";
		if (isErrorCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}
		return wCell.getStringCellValue().trim();
	}

	/**
	 * boolean value 반환
	 * @throws Exception
	 * @return boolean
	 */
	public boolean getBooleanValue() throws Exception {
		if(wCell == null){
			throw new Exception("지정된 workCell이 없습니다.");
		}
		if (isErrorCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}

		return wCell.getBooleanCellValue();
	}

	/**
	 * 숫자 값 반환(double)
	 * @throws Exception
	 * @return double
	 */
	public double getNumericValue() throws Exception
	{
		if(wCell == null)
			throw new Exception("Cell이 지정되지 않았음");
		if (isErrorCell()){
			throw new Exception("지정된 workCell에 오류가 있습니다.");
		}

		return wCell.getNumericCellValue();
	}

	/**
	 * workCell의 error 여부
	 * @return boolean
	 */
	public boolean isErrorCell(){
		if (wCell == null){
			return true;
		}

		if (wCell.getCellType() == CellType.BLANK){
			return true;
		}else {
			return false;
		}
	}



	public int getCurRowIdx() {
    	return curRowIdx;
    }
    public void setCurRowIdx(int curRowIdx) {
    	this.curRowIdx = curRowIdx;
    }
    public int getCurSheetIdx() {
    	return curSheetIdx;
    }
    public void setCurSheetIdx(int curSheetIdx) {
    	this.curSheetIdx = curSheetIdx;
    }


}
