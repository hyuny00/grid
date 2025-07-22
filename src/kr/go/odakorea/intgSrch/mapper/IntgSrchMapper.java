package kr.go.odakorea.intgSrch.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

/**
 * 통합검색을 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.intgSrch.mapper
 * @fileName       : IntgSrchMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Mapper("intgSrch.mapper.IntgSrchMapper")
public interface IntgSrchMapper {

	/**
	 * 통합검색 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listIntgSrch(Pageable pageable, FtMap params);

	
	/**
	 * 통합검색 목록을 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListIntgSrch(FtMap params);

	
	/**
	 * 통합검색 옵션 설정  조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listIntgSrchOpt(Pageable pageable, FtMap params);

	
	/**
	 * 통합검색 옵션 설정  Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListIntgSrchOpt(FtMap params);
	
	
	/**
	 * 통합검색 상세  조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listIntgSrchDtl(Pageable pageable, FtMap params);

	
	/**
	 * 통합검색 상세  Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListIntgSrchDtl(FtMap params);
	
	
}
