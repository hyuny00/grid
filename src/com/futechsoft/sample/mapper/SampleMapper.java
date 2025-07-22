
package com.futechsoft.sample.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.util.FtMap;


/**
 * 샘플을 관리하는 Mapper 클래스
 * @author futech
 *
 */
@Mapper("sample.mapper.SampleMapper")
public interface SampleMapper {

	/**
	 * 샘플목록을 조회한다
	 * @param pageable 
	 * @param params
	 * @return 샘플목록
	 * @throws Exception
	 */
	List<FtMap> selectSampleList(@Param("pageable") Pageable pageable, @Param("params") FtMap params) throws Exception;

	/**
	 * 샘플목록 카운트를 조회한다
	 * @param params
	 * @return 샘플목록 카운트
	 * @throws Exception
	 */
	long countSampleList(@Param("params") FtMap params) throws Exception;

	/**
	 * 샘플단건을 조회한다
	 * @param params
	 * @return 샘플
	 * @throws Exception
	 */
	FtMap selectSample(FtMap params) throws Exception;

	/**
	 * 샘플을 등록한다
	 * @param params
	 * @throws Exception
	 */
	void insertSample(FtMap params) throws Exception;

	/**
	 * 샘플을 삭제한다
	 * @param params
	 * @throws Exception
	 */
	void deleteSample(FtMap params) throws Exception;

	/**
	 * 샘플을 수정한다
	 * @param params
	 * @throws Exception
	 */
	void updateSample(FtMap params) throws Exception;

	/**
	 * 대용량 엑셀 다운로드용으로 샘플을 조회한다
	 * @param pageable
	 * @param params
	 * @param customResultHandler
	 */
	public void selectSampleList(@Param("pageable")Pageable pageable, @Param("params")FtMap params, ResultHandler<FtMap> customResultHandler);
}
