package kr.go.odakorea.file.mapper;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

/**
 * 파일을 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.file.mapper
 * @fileName       : FileMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Mapper("file.mapper.FileMapper")
public interface FileMapper {

	/**
   	 * 파일 자료를 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listFileData(FtMap params);
    
    
    /**
	 * 파일을 업로드 한다
	 * @param params
	 */
    void insertFileUld(FtMap params);
    
    /**
   	 * 한글서식을 다운로드 한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listKornTmpltDwnld(FtMap params);
    
    /**
   	 * 엑셀양식을 다운로드 한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listExclFormDwnld(FtMap params);
    
	
    /**
   	 * 파일 저장 및 검증 
   	 * @param params
   	 */
    void updateFileVrfc(FtMap params);

    
    /**
   	 * 엑셀 다운로드 한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listFileExcl(FtMap params);

	   	
}
