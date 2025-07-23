package com.futechsoft.framework.common.mapper;

import java.util.List;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.util.FtMap;

@Mapper("framework.common.mapper.CommonMapper")
public interface CommonMapper{

	List<FtMap> selectCommonCodeList(FtMap params) throws Exception;
	
	
	
	
	List<FtMap>  selectNtcCodeList(FtMap params) throws Exception;
	
	List<FtMap> selectBizFldCdList(FtMap params) throws Exception;
	
	
	void insertUserAuth(FtMap params) throws Exception;
	void deleteUserAuth(FtMap params) throws Exception;
}
