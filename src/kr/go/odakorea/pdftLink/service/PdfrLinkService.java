package kr.go.odakorea.pdftLink.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futechsoft.framework.common.pagination.Page;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.pdftLink.mapper.PdfrLinkMapper;


/**
 * 유무상연계를 관리하는 Service 크래스
* @packageName    : kr.go.odakorea.pdftLink.service
* @fileName       : PdfrLinkService.java
* @description    : 유무상연계를 관리하는 Service 크래스
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.22        pdh       최초 생성
 */
@Service("pdftLink.service.PdfrLinkService")
public class PdfrLinkService extends EgovAbstractServiceImpl {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(PdfrLinkService.class);

	@Resource(name = "pdftLink.mapper.PdfrLinkMapper")
	private PdfrLinkMapper pdfrLinkMapper;
	
	
	/**
	 * ODA 사업목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Page<FtMap>  listOdaBizInfo(Pageable pageable,  FtMap params) throws Exception{
		
		
		List<FtMap> list = pdfrLinkMapper.listOdaBizInfo(pageable, params);
		long count = pdfrLinkMapper.countOdaBizInfo(params);

		Page<FtMap> page = new Page<FtMap>(pageable, list, count);

		return page;
	}
	
	
	
	
	/**
	 * 사업간 연계를 요청한다
	 * @param params
	 * @throws Exception
	 */
	public void createBizLinkDmnd(FtMap params) throws Exception{
		
		pdfrLinkMapper.createBizLinkDmnd(params);
	}
	
	/**
	 * 사업간 연계를 삭제한다
	 * @param params
	 * @throws Exception
	 */
	public void deleteBizLinkDmnd(FtMap params) throws Exception{
	}
	
	/**
	 * 사업간 연계를 수정한다
	 * @param params
	 * @throws Exception
	 */
	public void updateBizLinkDmnd(FtMap params) throws Exception{
	}
	
	
	/**
	 * 사업관 연계를 삭제한다
	 * @param params
	 * @throws Exception
	 */
	public void deleteBizLink(FtMap params) throws Exception{
	}
	
	
	/**
	 * 검토의견을 저장한다
	 * @param params
	 * @throws Exception
	 */
	@Transactional
	public void saveOpnn(FtMap params) throws Exception{
		
	}
	
	
	/**
	 * 검토의견을 삭제한다
	 * @param params
	 * @throws Exception
	 */
	public void deleteOpnn(FtMap params) throws Exception{
	}
	
	
	
	/**
	 * 사업간 연계를 확정/확정취소한다
	 * @param params
	 * @throws Exception
	 */
	public void  updateBizLinkCfmtn(FtMap params) throws Exception{
	}
	
	
	/**
	 * 사업간 연계확정을 등록한다.
	 * @param params
	 * @throws Exception
	 */
	public void  createBizLinkCfmtn(FtMap params) throws Exception{
	}


}
