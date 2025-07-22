package com.futechsoft.framework.excel;



public class ExcelColumn {

	public CellVo[] cellVo;

	public ExcelColumn(CellVo... cellVo) {
		this.cellVo = cellVo;
	}


	public CellVo[] getCellVos() {
		return cellVo;
	}

}
