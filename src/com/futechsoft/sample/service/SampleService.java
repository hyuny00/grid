package com.futechsoft.sample.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futechsoft.framework.annotation.SimpleCacheAccess;
import com.futechsoft.framework.common.pagination.Page;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.excel.CustomResultHandler;
import com.futechsoft.framework.excel.ExcelColumn;
import com.futechsoft.framework.excel.LargeExcel;
import com.futechsoft.framework.file.service.FileUploadService;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FileUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.sample.mapper.SampleMapper;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.gis.service.IatiService;


/**
 * 샘플을 관리하는 Service
 * @author futech
 *
 */
@Service("sample.service.SampleService")
public class SampleService extends EgovAbstractServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleService.class);

	@Resource(name = "sample.mapper.SampleMapper")
	private SampleMapper sampleMapper;

	@Resource(name = "framework.file.service.FileUploadService")
	FileUploadService fileUploadService;


	@Resource(name = "gis.service.IatiService")
	IatiService iatiService;


	/**
	 * 샘플목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return 샘플목록
	 * @throws Exception
	 */
	//@CacheAccess(value = "userCache")
	 @SimpleCacheAccess(value = "realtimeDataCache", ttl = 30)
	public Page<FtMap> selectSampleList(Pageable pageable, FtMap params) throws Exception {



		List<FtMap> list = sampleMapper.selectSampleList(pageable, params);
		long count = sampleMapper.countSampleList(params);

		Page<FtMap> page = new Page<FtMap>(pageable, list, count);


		return page;
	}

	/**
	 * 엑셀다운로드용 샘플목록을 조회한다
	 * @param params
	 * @param sheet
	 * @param excelColumn
	 * @param largeExcel
	 * @throws Exception
	 */
	public void selectExlSampleList(FtMap params,  SXSSFSheet sheet,  ExcelColumn excelColumn,LargeExcel largeExcel) throws Exception {

		Pageable pageable = new Pageable();
		pageable.setPaged(false);
		sampleMapper.selectSampleList(pageable, params, new CustomResultHandler(sheet, excelColumn, largeExcel));

	}

	/**
	 * 샘플단건을 조회한다
	 * @param params
	 * @return 샘플단건
	 * @throws Exception
	 */
	public FtMap selectSample(FtMap params) throws Exception {
		return sampleMapper.selectSample(params);
	}

	/**
	 * 샘플을 등록한다
	 * @param params
	 * @throws Exception
	 */
	@Transactional
	public void insertSample(FtMap params) throws Exception {


		iatiService.saveIatiInfo(params);

		/*

		params.put("userId", SecurityUtil.getUserId());
		//업무구분코드에 맞게 세팅
		params.put("taskSecd", "0000");

		//파일저장 및 경로 세팅(한글경로 불가). vvvv/bbbb/yyyymm 폴더 아래 파일 저장
		String attcDocId=fileUploadService.saveFile(params, "vvvv/bbbb","attcDocId");
		params.put("attcDocId", attcDocId);

		String attcDocId2=fileUploadService.saveFile(params, "AAAA","attcDocId2");
		params.put("attcDocId2", attcDocId2);


		sampleMapper.insertSample(params);
*/
	}


	/**
	 * 샘플을 수정한다
	 * @param params
	 * @throws Exception
	 */
	@Transactional
	public void updateSample(FtMap params) throws Exception {


		//업무구분코드에 맞게 세팅
		params.put("taskSecd", "0000");

		//파일저장 및 경로 세팅(한글경로 불가). AAAA디렉토리/yyyymm 폴더 아래 파일 저장
		String attcDocId=fileUploadService.saveFile(params, "AAAA","attcDocId");
		params.put("attcDocId", attcDocId);

		String attcDocId2=fileUploadService.saveFile(params, "AAAA","attcDocId2");
		params.put("attcDocId2", attcDocId2);



		//ZIP 파일 압축해제필요시..........
		List<String> zipFileIdList= (ArrayList<String>)params.get("zipFileIdList");
		if(zipFileIdList!=null && zipFileIdList.size()>0) {
			for(String value : zipFileIdList) {
				System.out.println("zipFileId: "+value);

				FileInfoVo fileInfoVo = fileUploadService.getFileInfo(value);

				String uploadPath= fileUploadService.getRealUploadPath();

				String tempZipPath = fileUploadService.getTempZipPath();
				FileUtil.extractZip(fileInfoVo,uploadPath, tempZipPath);


				Path target = Paths.get(tempZipPath, fileInfoVo.getFileId());

				String unZipPath = target.toString();
				System.out.println("unZipPath..."+unZipPath);
				//이후작업 계속


			}
		}




		sampleMapper.updateSample(params);
	}

	/**
	 * 샘플을 삭제한다
	 * @param params
	 * @throws Exception
	 */
	//@CacheEvict(value = "userCache", allEntries = true)
	@Transactional
	public void deleteSample(FtMap params) throws Exception {

		//샘플삭제시 파일삭제 업데이트
		String delFileGroup= params.getString("deletefileGroupId");
		if(!CommonUtil.nvl(delFileGroup).equals("")) {
			String[] delFileGroups=StringUtils.split(delFileGroup, "|");
			for(String docId: delFileGroups) {
				fileUploadService.deleteFileGroup(docId);
			}
		}


		sampleMapper.deleteSample(params);

	}


	@Transactional
	public void saveGridSample(FtMap params) throws Exception {

		 if (params.containsKey("updatedNodes")) {
             List<Map<String, Object>> rowMaps = (List<Map<String, Object>>) params.get("updatedNodes");

             if (rowMaps != null) {
                 for (int i = 0; i < rowMaps.size(); i++) {
                     Map<String, Object> rowMap = rowMaps.get(i);
                     // Map을 FtMap으로 변환
                     FtMap row = new FtMap();
                     row.setFtMap(rowMap);

                     LOGGER.debug("행 " + (i+1) + ":");
                     LOGGER.debug("  ID: " + row.get("id"));
                     LOGGER.debug("  이름: " + row.get("name"));
                     LOGGER.debug("  날짜: " + row.get("date"));

                     if(row.getString("id").startsWith("N_")){
                    	 //추가
                    	//sampleMapper.aaa(row);
                     }else{
                    	 //수정
                       //sampleMapper.bbb(row);
                     }
                 }
             }
         }

	}

	@Transactional
	public int saveGridExcelSample(List<Map<String, Object>> excelData) throws Exception {

		int savedCount = 0;

		for (int i = 0; i < excelData.size(); i++) {
            Map<String, Object> rowMap = excelData.get(i);
            FtMap row = new FtMap();
            row.setFtMap(rowMap);

            LOGGER.debug("행 " + (i+1) + ":");
            LOGGER.debug("  no: " + row.get("no"));
            LOGGER.debug("  이름: " + row.get("name"));
            LOGGER.debug("  날짜: " + row.get("date"));

            //저장 mapper호출


            savedCount++;
        }

		return savedCount;

	}


	@Transactional
	public void deleteGridSample(FtMap params) throws Exception {

		String[] deletedRows = params.getStringArray("deleteNodes");

        System.out.println("\n=== 삭제된 행들 ===");
        System.out.println("삭제 행 개수: " + (deletedRows != null ? deletedRows.length : 0));

        if (deletedRows != null) {
            for (int i = 0; i < deletedRows.length; i++) {
           	 LOGGER.debug("삭제 ID " + (i+1) + ": " + deletedRows[i]);

           	 //삭제 mapper호출

            }
        }

	}

}
