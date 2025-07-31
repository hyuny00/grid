package kr.go.odakorea.gis.mapper;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.util.FtMap;

@Mapper("gis.mapper.ReliefWebMapper")
public interface ReliefWebMapper {

	/**
	 * 수원국 재난내역을 등록한다
	 * @param params
	 * @throws Exception
	 */
	void createRcntnClmty(FtMap params) throws Exception;
	
	/**
	 * 수원국 재난상태를 수정한다
	 * @param params
	 * @throws Exception
	 */
	void updateClmtyPrgrsStcd(FtMap params) throws Exception;
	
	
	/**
	 * 재난아이디를 조회한다
	 * @param params
	 * @return
	 * @throws Exception
	 */
	int getClmtyIdVl(FtMap params) throws Exception;
	
	
}
