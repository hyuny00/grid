package kr.go.odakorea.gis.mapper;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.FtMap;

@Mapper("gis.mapper.KoicaInfoMapper")
public interface KoicaScrapingMapper {
	
	void insertKoicaScrpInfo(FtMap params) throws Exception;
	
	
	String getEnfcInstUnqBizNo() throws Exception;
	
	FileInfoVo selectFileInfo(FtMap params) throws Exception;

}
