package com.futechsoft.framework.excel;

import com.futechsoft.framework.util.FtMap;

public class CellVo {

	final public static int CELL_STRING= 1;
	final public static int CELL_NUMBER= 2;
	final public static int CELL_CURRENCY= 3;
	final public static int CELL_ARRAY= 4;

	final public static int ALIGN_CENTER= 1;
	final public static int ALIGN_RIGHT= 2;
	final public static int ALIGN_LEFT= 3;

	private int cellType;
	private String cellColumn;
	private int align;
	private FtMap codeMap;
	private String format;



	public CellVo(String cellColumn) {
		 this(CellVo.CELL_STRING,  cellColumn, null, CellVo.ALIGN_CENTER, null);
	}

	public CellVo(int cellType, String cellColumn) {
		this(cellType,  cellColumn, null, CellVo.ALIGN_CENTER, null);
	}


	public CellVo(String cellColumn, int align) {
		 this(CellVo.CELL_STRING,  cellColumn, null, align, null);
	}

	public CellVo(int cellType, String cellColumn, int align) {
		this(cellType,  cellColumn, null, align, null);
	}

	public CellVo(int cellType, String cellColumn, int align,  String format) {
		this(cellType,  cellColumn, null, align, format);
	}

	public CellVo(String cellColumn, FtMap codeMap) {

		this(CellVo.CELL_STRING,  cellColumn, codeMap, CellVo.ALIGN_CENTER, null);
	}

	public CellVo(String cellColumn, FtMap codeMap, int align) {
		this(CellVo.CELL_STRING,  cellColumn, codeMap,align, null);
	}

	public CellVo(String cellColumn, FtMap codeMap, int align,  String format) {
		this(CellVo.CELL_STRING,  cellColumn, codeMap,align, format);
	}

	public CellVo(int cellType, String cellColumn, FtMap codeMap, int align) {
		this(cellType,  cellColumn, codeMap,align, null);
	}

	public CellVo(int cellType, String cellColumn, FtMap codeMap, int align, String format) {
		this.cellType=cellType;
		this.cellColumn=cellColumn;
		this.codeMap=codeMap;
		this.align=align;
		this.format=format;
	}

	public int getCellType() {
		return cellType;
	}

	public String getCellColumn() {
		return cellColumn;
	}

	public FtMap  getCodeMap() {
		return codeMap;
	}

	public int getAlign() {
		return align;
	}

	public String getFormat() {
		return format;
	}

}
