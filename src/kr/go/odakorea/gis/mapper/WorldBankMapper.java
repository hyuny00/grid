package kr.go.odakorea.gis.mapper;

import java.util.List;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.util.FtMap;

@Mapper("gis.mapper.WorldBankMapper")
public interface WorldBankMapper {
	
	
	/**
	 * 국가별 지표를 등록한다
	 * @param params
	 * @throws Exception
	 */
	void insertNtnIdct(FtMap params) throws Exception;
	
	
	/**
	 * 국가별 지표를 삭제한다
	 * @param params
	 * @return
	 * @throws Exception
	 */
	void deletetNtnIdct(FtMap params) throws Exception;
	
	
	/**
	 * 국가목록을 가져온다
	 * @param params
	 * @return
	 * @throws Exception
	 */
	List<String> getNtnCdList() throws Exception;

}
