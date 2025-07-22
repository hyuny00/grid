package kr.go.odakorea.gis.mapper;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.util.FtMap;




@Mapper("gis.mapper.IatiMapper")
public interface IatiMapper {
	
	
	/**
	 * 지역정보가 있는지 조회한다
	 * @param params
	 * @return
	 * @throws Exception
	 */
	FtMap getRgnNo(FtMap params) throws Exception;
	
	
	/**
	 * 지역정보를 저장한다
	 * @param params
	 * @throws Exception
	 */
	void inserNtnRgn(FtMap params) throws Exception;
	
	
	/**
	 * 지역정보를 수정한다
	 * @param params
	 * @throws Exception
	 */
	void updateNtnRgn(FtMap params) throws Exception;
	
}


