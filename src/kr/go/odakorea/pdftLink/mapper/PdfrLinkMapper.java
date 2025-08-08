package kr.go.odakorea.pdftLink.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.util.FtMap;


/**
 * 유무상 연계를 관리하는 Mapper
* @packageName    : kr.go.odakorea.pdftLink.mapper
* @fileName       : PdfrLinkMapper.java
* @description    : 유무상 연계를 관리하는 Mapper
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.22        pdh       최초 생성
 */
@Mapper("pdftLink.mapper.PdfrLinkMapper")
public interface PdfrLinkMapper{
	
	
	/**
	 * ODA 사업목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
	List<FtMap> listOdaBizInfo(@Param("pageable") Pageable pageable, @Param("params") FtMap params) throws Exception;
	
	
	
	/**
	 *  ODA 사업목록을 카운트 한다
	 * @param params
	 * @return
	 * @throws Exception
	 */
	long countOdaBizInfo(@Param("params") FtMap params) throws Exception;
	
	
	/**
	 * 사업간 연계를 요청한다
	 * @param params
	 * @throws Exception
	 */
	void createBizLinkDmnd(FtMap params) throws Exception;
	
	/**
	 * 사업간 연계를 삭제한다
	 * @param params
	 * @throws Exception
	 */
	void deleteBizLinkDmnd(FtMap params) throws Exception;
	
	/**
	 * 사업간 연계를 수정한다
	 * @param params
	 * @throws Exception
	 */
	void updateBizLinkDmnd(FtMap params) throws Exception;
	
	
	
	/**
	 * 사업관 연계를 삭제한다
	 * @param params
	 * @throws Exception
	 */
	void deleteBizLink(FtMap params) throws Exception;
	
	
	/**
	 * 검토의견을 등록한다
	 * @param params
	 * @throws Exception
	 */
	void createOpnn(FtMap params) throws Exception;
	
	
	/**
	 * 검토의견을 삭제한다
	 * @param params
	 * @throws Exception
	 */
	void deleteOpnn(FtMap params) throws Exception;
	
	
	/**
	 * 검토의견을 수정한다
	 * @param params
	 * @throws Exception
	 */
	void updateOpnn(FtMap params) throws Exception;
	
	
	/**
	 * 사업간 연계를 확정/확정취소한다
	 * @param params
	 * @throws Exception
	 */
	void  updateBizLinkCfmtn(FtMap params) throws Exception;
	
	
	/**
	 * 사업간 연계를 등록한다.
	 * @param params
	 * @throws Exception
	 */
	void  createBizLinkCfmtn(FtMap params) throws Exception;

}
