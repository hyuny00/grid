package com.futechsoft.framework.common.sqlHelper;

public class Column {

	public String[] columns;

	public Column(String... column) {
		this.columns = column;
	}

	public String[] getColumns() {
		return columns;
	}

	public static final String RGSTP_NO = "rgstp_no";
	public static final String RGST_DTTM = "rgst_dttm";
	public static final String UPDTP_NO = "updtp_no";
	public static final String UPDT_DTTM = "updt_dttm";

	public static final String DATE = "sysdate";

}
