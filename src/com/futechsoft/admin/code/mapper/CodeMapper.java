package com.futechsoft.admin.code.mapper;

import java.util.List;

import com.futechsoft.admin.code.vo.Code;
import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.util.FtMap;

@Mapper("code.mapper.CodeMapper")
public interface CodeMapper {

	List<Code> getCodeList(FtMap params) throws Exception;

	void insertCode(FtMap params) throws Exception;
	
	void updateCode(FtMap params)  throws Exception;


	void updateSrtOrd(FtMap params)  throws Exception;

	void updateUseYn(FtMap params)  throws Exception;


	// Code selectUntWorkDivNm(FtMap params) throws Exception;

	// void updateUntWorkCd(FtMap params) throws Exception;

	// void deleteUntWorkCd(FtMap params) throws Exception;

}
