package com.futechsoft.sample.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.futechsoft.framework.common.constant.ViewInfo;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.common.pagination.Page;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.excel.CellVo;
import com.futechsoft.framework.excel.ExcelColumn;
import com.futechsoft.framework.excel.LargeExcel;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;
import com.futechsoft.sample.service.SampleService;

/**
 * 샘플을 관리하는 controller 클래스
 * @author futech
 *
 */
@Controller
public class SampleController extends AbstractController{

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);


	@Resource(name = "sample.service.SampleService")
	SampleService sampleService;

	 @Value("${spring.profiles.active:default}")
     private String activeProfile;




	 @RequestMapping("/sample/newSampleListForm")
	public String newSampleList( HttpServletRequest request) throws Exception {


		FtMap params = super.getFtMap(request);
		params.put("testData", "1234");

		 return "tiles:sample/newSampleListForm";

	 }


	 @RequestMapping("/sample/popup1")
	 public String popup1(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());

		return "sample/popup1";

	}


	 @RequestMapping("/sample/popup2")
	 public String popup2(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());

		return "sample/popup2";

	}



	 @ResponseBody
	 @RequestMapping("/sample/newSampleList2")
	 public Map<String, Object> newGridSample(@RequestBody(required=false) Map<String, Object> map) {

		 System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		 System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+map.get("schNtnCd"));

		 List<Map<String, Object>> dataList = new ArrayList<>();

		    for (int i = 1; i <= 12; i++) {
		        Map<String, Object> row = new HashMap<>();
		        if(i<3) {
		        row.put("projectId", "1092000_2023_00");
		        }else {
		        	 row.put("projectId", "1092000_2023_00" + i);
		        }



		        row.put("projectType", "패키지" + i);
		        row.put("projectTitle", "몽골 날라이흐구/바양주르흐구 지속가능한 '길(Trail)여행' 비즈니스 모델 고도화를 통한 주민 소득증대 사업 2단계('24-'26/" + (1240 + i) + "백만원)");
		        row.put("period", "2023 ~2025");
		        row.put("department", "국무조정실" + i);
		        row.put("country", "방글라데시" + i);
		        row.put("sector", "일반 공공행정 및 시민사회" + i);
		        row.put("regDate", "2025050"+i);
		        row.put("status", "확정" + i);

		        //체크박스
		        if(i<3) {
		        	 row.put("checked", false);
		        	 row.put("statusCd", "01");
		        	  row.put("childYn", "Y");
		        }else {
		        	 row.put("checked", true);
		        	 row.put("statusCd", "02");
		        	  row.put("childYn", "N");
		        }

		        dataList.add(row);
		    }

		Map<String, Object> response = new HashMap<>();
	    response.put("data", dataList);
	    response.put("total", dataList.size());

	    return response;

	 }

	 @ResponseBody
	 @RequestMapping("sample/newSampleList2/children/{projectId}")
	 public Map<String, Object> getChildRows11(@PathVariable String projectId) {

		 List<Map<String, Object>> dataList = new ArrayList<>();

		    for (int i = 1; i <= 3; i++) {
		        Map<String, Object> row = new HashMap<>();
		        row.put("projectId", "1092000_2023_00" + i);
		        row.put("projectType", "패키지" + i);
		        row.put("projectTitle", "몽골 날라이흐구/바양주르흐구 지속가능한 '길(Trail)여행' 비즈니스 모델 고도화를 통한 주민 소득증대 사업 2단계('24-'26/" + (1240 + i) + "백만원)");
		        row.put("period", "2023 ~2025");
		        row.put("department", "국무조정실" + i);
		        row.put("country", "방글라데시" + i);
		        row.put("sector", "일반 공공행정 및 시민사회" + i);
		        row.put("regDate", "2025050"+i);
		        row.put("status", "확정" + i);

		        //체크박스
		        if(i<2) {
		        	 row.put("statusCd", "01");
		        }else {
		        	 row.put("statusCd", "02");
		        }
		        row.put("childYn", "N");


		        dataList.add(row);
		    }

		Map<String, Object> response = new HashMap<>();
	    response.put("data", dataList);
	    response.put("total", dataList.size());

	    return response;

	 }




	 @ResponseBody
	 @RequestMapping("/sample/newSampleList3")
	 public Map<String, Object> newGridSample3() {

	     List<Map<String, Object>> dataList = new ArrayList<>();
	     Map<String, Map<String, Object>> idToNodeMap = new HashMap<>();

	     for (int i = 1; i <= 12; i++) {
	         Map<String, Object> row = new HashMap<>();

	         String projectId = String.format("1092000_2023_%03d", i);
	         row.put("projectId", projectId);
	         row.put("projectType", "패키지" + i);
	         row.put("projectTitle", "몽골 날라이흐구/바양주르흐구 지속가능한 '길(Trail)여행' 비즈니스 모델 고도화를 통한 주민 소득증대 사업 2단계('24-'26/" + (1240 + i) + "백만원)");
	         row.put("period", "2023 ~2025");
	         row.put("department", "국무조정실" + i);
	         row.put("country", "방글라데시" + i);
	         row.put("sector", "일반 공공행정 및 시민사회" + i);
	         row.put("regDate", "2025050" + i);
	         row.put("status", "확정" + i);

	         if (i < 3) {
	             row.put("checked", false);
	             row.put("statusCd", "01");
	         } else {
	             row.put("checked", true);
	             row.put("statusCd", "02");
	         }

	         // 계층 구조 설정: 3개 단위로 루트/1단/2단 구성
	         String parentProjectId = null;
	         if ((i - 1) % 3 == 1) {
	             // 두 번째 항목: 첫 번째 항목이 부모
	             parentProjectId = String.format("1092000_2023_%03d", i - 1);
	         } else if ((i - 1) % 3 == 2) {
	             // 세 번째 항목: 두 번째 항목이 부모 → 3단계
	             parentProjectId = String.format("1092000_2023_%03d", i - 1);
	         }

	         row.put("parentProjectId", parentProjectId);
	         row.put("childYn", "N");  // 나중에 자식 여부 갱신

	         dataList.add(row);
	         idToNodeMap.put(projectId, row);
	     }

	     // 자식이 존재하는 부모에 childYn = "Y" 설정
	     for (Map<String, Object> row : dataList) {
	         String parentId = (String) row.get("parentProjectId");
	         if (parentId != null) {
	             Map<String, Object> parent = idToNodeMap.get(parentId);
	             if (parent != null) {
	                 parent.put("childYn", "Y");
	             }
	         }
	     }

	     Map<String, Object> response = new HashMap<>();
	     response.put("data", dataList);
	     response.put("total", dataList.size());

	     return response;
	 }




	 @RequestMapping("/sample/gridSample0")
		public String gridSample0( HttpServletRequest request) throws Exception {

			 return "tiles:sample/gridSample0";

		 }

	 @RequestMapping("/sample/gridSample1")
	public String gridSample1( HttpServletRequest request) throws Exception {

		 return "tiles:sample/gridSample1";

	 }

	 @RequestMapping("/sample/gridSample2")
		public String gridSample2( HttpServletRequest request) throws Exception {

			 return "tiles:sample/gridSample2";

		 }

	 @RequestMapping("/sample/gridSample3")
		public String gridSample3( HttpServletRequest request) throws Exception {

			 return "tiles:sample/gridSample3";

		 }

	 @RequestMapping("/sample/gridSample4")
		public String gridSample4( HttpServletRequest request) throws Exception {

			 return "tiles:sample/gridSample4";

		 }

	 @RequestMapping("/sample/gridSample5")
		public String gridSample5( HttpServletRequest request) throws Exception {

			 return "tiles:sample/gridSample5";

		 }

	 @RequestMapping("/sample/gridSample6")
		public String gridSample6( HttpServletRequest request) throws Exception {

			FtMap params = super.getFtMap(request);
			request.setAttribute("testData", "1234");

			 return "tiles:sample/gridSample6";

		 }


	 @ResponseBody
	 @RequestMapping("/sample/grid/parent")
	 public Map<String, Object> getParentRows() {
	     List<Map<String, Object>> parents = Arrays.asList(
	         createMap( "no","1", "name", "부서1", "date", "2025-06-01","childYn","Y", "parentNo",""),
	         createMap( "no","2","name", "부서2", "date", "2025-06-05","childYn","N", "parentNo","")

	     );

		Map<String, Object> response = new HashMap<>();
	    response.put("data", parents);
	    response.put("total", 10);

	    return response;

	 }

	 @ResponseBody
	 @RequestMapping("/sample/grid/parent2")
	 public Map<String, Object> getParentRows22() {
	     List<Map<String, Object>> parents = Arrays.asList(
	         createMap( "no","1", "name", "부서1", "date", "2025-06-01","childYn","Y", "parentNo",""),
	         createMap( "no","101", "name", "팀1-1", "date", "2025-06-10","childYn","N", "parentNo","1"),
             createMap( "no","102",  "name", "팀1-2", "date", "2025-06-12","childYn","N", "parentNo","1"),
	         createMap( "no","2","name", "부서2", "date", "2025-06-05","childYn","N", "parentNo","")

	     );

		Map<String, Object> response = new HashMap<>();
	    response.put("data", parents);
	    response.put("total", 10);

	    return response;

	 }

	 @ResponseBody
	 @RequestMapping("/sample/grid/children/{parentId}")
	 public Map<String, Object> getChildRows(@PathVariable String parentId) {
	     List<Map<String, Object>> children;
	     if (parentId .equals("1")) {
	         children = Arrays.asList(
	             createMap( "no","101", "name", "팀1-1", "date", "2025-06-10","childYn","N"),
	             createMap( "no","102",  "name", "팀1-2", "date", "2025-06-12","childYn","N")
	         );
	     } else {
	         children = Collections.emptyList();
	     }

	 	Map<String, Object> response = new HashMap<>();
	    response.put("data", children);
	    response.put("total", 10);

	    return response;


	 }



	 @ResponseBody
	 @RequestMapping("/sample/grid2/parent")
	 public  Map<String, Object>  getParentRows2() {
	     List<Map<String, Object>> parents = Arrays.asList(
	         createMap( "no","1", "name", "부서1", "category", "A","childYn","Y"),
	         createMap( "no","2", "name", "부서2", "category", "B","childYn","N")
	     );
	     Map<String, Object> response = new HashMap<>();
		    response.put("data", parents);
		    response.put("total", 10);

		    return response;
	 }

	 @ResponseBody
	 @RequestMapping("/sample/grid2/children/{parentId}")
	 public  Map<String, Object>  getChildRows2(@PathVariable String parentId) {
	     List<Map<String, Object>> children;
	     if (parentId .equals("1")) {
	         children = Arrays.asList(
	             createMap( "no","101", "name", "팀1-1", "category", "B","childYn","N"),
	             createMap( "no","102","name", "팀1-2", "category", "B","childYn","N")
	         );
	     } else {
	         children = Collections.emptyList();
	     }
	     Map<String, Object> response = new HashMap<>();
		    response.put("data", children);
		    response.put("total", 10);

		    return response;


	 }

	 // Helper method to create Map
	 private Map<String, Object> createMap(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
	     Map<String, Object> map = new HashMap<>();
	     map.put(key1, value1);
	     map.put(key2, value2);
	     map.put(key3, value3);
	     map.put(key4, value4);

	     return map;
	 }

	 private Map<String, Object> createMap(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5) {
	     Map<String, Object> map = new HashMap<>();
	     map.put(key1, value1);
	     map.put(key2, value2);
	     map.put(key3, value3);
	     map.put(key4, value4);
	     map.put(key5, value5);

	     return map;
	 }

	@ResponseBody
	@PostMapping("/sample/grid/delete")
	public Map<String, Object> deleteGrid(@RequestBody Map<String, Object> requestData) throws Exception {

		 Map<String, Object> response = new HashMap<>();

		 FtMap params = getFtMap(requestData);

		sampleService.deleteGridSample(params);

		response.put("success", true);
		response.put("message", "저장 완료");
		response.put("receivedData", requestData); // 받은 데이터를 그대로 응답에 포함

		return response;

	 }


	@ResponseBody
	@PostMapping("/sample/grid/save")
	public  Map<String, Object>  saveGrid(@RequestBody Map<String, Object> requestData) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 받은 데이터 로그 출력
			System.out.println("=== 받은 전체 데이터 ===");
			System.out.println(requestData);

			FtMap params = getFtMap(requestData);

			// 여기서 실제 저장 로직을 구현하면 됩니다
			sampleService.saveGridSample(params);

			response.put("success", true);
			response.put("message", "저장 완료");
			response.put("receivedData", requestData); // 받은 데이터를 그대로 응답에 포함

			return response;

		} catch (Exception e) {
			System.err.println("저장 중 오류 발생: " + e.getMessage());
			e.printStackTrace();

			response.put("success", false);
			response.put("message", "저장 실패: " + e.getMessage());

			return response;
		}
	}

	@ResponseBody
	@PostMapping("/sample/grid/saveExl")
	public  Map<String, Object>  saveExl(@RequestBody Map<String, Object> requestData) throws Exception {

		Map<String, Object> response = new HashMap<>();
		FtMap params = getFtMap(requestData);

		 int savedCount=0;
        // 엑셀 데이터 저장인지 확인
        if (Boolean.TRUE.equals(params.getBoolean("isExcelData"))) {
        	   List<Map<String, Object>> excelData = (List<Map<String, Object>>) params.get("excelData");
        	   savedCount = sampleService.saveGridExcelSample(excelData);
        }

    	response.put("success", true);
		response.put("message", "총 "+savedCount+"건 저장 완료");

		return response;

	}


	@RequestMapping("/sample/selectSampleListForm")
	public String selectSampleListForm( HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());

		/* 공통코드목록 가져오는 방법*/
		params.put("cdGroupSn", 1);
		List<FtMap> codeList = super.getCommonService().selectCommonCodeList(params);
		request.setAttribute("codeList", codeList);


		params.put("cdGroupSn", 1);
		List<FtMap> sampleCodeList = getCommonService().selectCommonCodeList(params);
		request.setAttribute("sampleCodeList", sampleCodeList);


		/*공통코드 목록을 map형식으로 변환 필요시*/
		FtMap etcCode = super.getCommonService().selectCommonCodeMap(codeList);
		request.setAttribute("etcCode", etcCode);

		return "tiles:sample/sampleList";

	}

	@ResponseBody
	@RequestMapping("/sample/selectSampleList1")
	public  Map<String, Object> selectSampleList1(@RequestBody Map<String, Object> map) throws Exception {


		FtMap params = getFtMap(map);

		Pageable pageable = new Pageable();
		pageable.setParam(params);


		params.put("userId", SecurityUtil.getUserId());



		Page<FtMap> page = sampleService.selectSampleList(pageable, params);

		List resultList= page.getList();
		Map<String, Object> response = new HashMap<>();
	    response.put("data", resultList);
	    response.put("total",  page.getPageable().getTotalCount());


	    return response;
	}



	/**
	 * 샘플목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/sample/selectSampleList")
	public  Map<String, Object> selectSampleList(@RequestBody(required = false) Map<String, Object> map) throws Exception {



		FtMap params = getFtMap(map);

		Pageable pageable = new Pageable();
		pageable.setParam(params);


		params.put("userId", SecurityUtil.getUserId());


	System.out.println("schCondition.."+params.getString("schCondition"));
	System.out.println("schKeyword.."+params.getString("schKeyword"));


		Page<FtMap> page = sampleService.selectSampleList(pageable, params);

		List resultList= page.getList();
		Map<String, Object> response = new HashMap<>();
	    response.put("data", resultList);
	    response.put("total",  page.getPageable().getTotalCount());


	    return response;
	}
	/**
	 * 샘플폼으로 이동한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/sample/sampleForm")
	public String sampleForm(HttpServletRequest request) throws Exception {

		return "tiles:sample/sampleForm";
	}

	/**
	 * 샘플을 등록한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/sample/insertSample")
	public String insertSample(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());

		sampleService.insertSample(params);

		request.setAttribute(ViewInfo.REDIRECT_URL, "/sample/selectSampleListForm");
		return ViewInfo.REDIRECT_PAGE;

	}

	/**
	 * 샘플단건을 조회한다
	 * @param request
	 * @return  jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/sample/selectSample")
	public String selectSample(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());
		FtMap result = sampleService.selectSample(params);

		request.setAttribute("result", result);


		return "tiles:sample/sampleForm";
	}

	/**
	 * 샘플을 수정한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/sample/updateSample")
	public String updateSample(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());

		sampleService.updateSample(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		//업데이트를 하고 상세조회화면으로 넘어갈때  상세조회를 할 조건을 세팅한다
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/sample/selectSample");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 샘플을 삭제한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/sample/deleteSample")
	public String deleteSample(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());


		System.out.println(params);

		sampleService.deleteSample(params);

		request.setAttribute("message", "삭제되었습니다.");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/sample/selectSampleList");
		return ViewInfo.REDIRECT_PAGE;

	}

	/**
	 * 샘플 팝업을 호출한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/sample/samplePopup")
	public String samplePopup(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		LOGGER.debug("testId.." + params.getString("testId"));

		params.put("userId", SecurityUtil.getUserId());

		request.setAttribute("contents", "hello....");

		return "sample/samplePopup";
	}

	@RequestMapping(value = "/dashboard")
	public String getChatViewPage(ModelAndView mav) {
		return "tiles:dashboard/dashboard";
	}


	/**
	 * 엑셀을 다운로드한다
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/file/excelDown")
	public void excelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FtMap params = getFtMap(request);

		Pageable pageable = new Pageable();
		pageable.setPaged(false);
		Page<FtMap> page = sampleService.selectSampleList(pageable, params);

		String templateFilePath = getExceltemplatePath(request);
		File templateFile=new File(templateFilePath, "test_1.xlsx");

		//단순 엑셀다운로드
		//String[] columnValue= {"id", "name", "regUser"};
		//getExcelHelper().excelDownload(response, templateFile, page.getList(), columnValue);


		// 포맷 , 코드 ,정렬 필요시
		params.put("upCdSeq", 800);
		FtMap codeMap = getCommonService().selectCommonCodeMap(params);
		ExcelColumn excelColumn  = new ExcelColumn(
													new CellVo(CellVo.CELL_STRING, "id", 			CellVo.ALIGN_RIGHT),
													new CellVo(CellVo.CELL_STRING, "name", 	CellVo.ALIGN_CENTER),
													new CellVo(CellVo.CELL_STRING, "etc_code",	codeMap, CellVo.ALIGN_LEFT)
							);
		getExcelHelper().excelDownload(response, templateFile, page.getList(), excelColumn);


	}


	/**
	 * 대용량 엑셀을 다운로드한다
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/file/largeExcelDown")
	public void largeExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FtMap params = getFtMap(request);
		params.put("userId", SecurityUtil.getUserId());

		String templateFilePath = getExceltemplatePath(request);
		File templateFile=new File(templateFilePath, "test_1.xlsx");

		LargeExcel largeExcel = getExcelHelper().preparedLargeExcel(templateFile);

		//String[] columnValue= {"id", "name", "etcCode"};
		params.put("upCdSeq", 800);
		FtMap codeMap = getCommonService().selectCommonCodeMap(params);
		ExcelColumn excelColumn  = new ExcelColumn(
													new CellVo(CellVo.CELL_STRING, "id", 			CellVo.ALIGN_CENTER),
													new CellVo(CellVo.CELL_STRING, "name", 	CellVo.ALIGN_RIGHT),
													new CellVo(CellVo.CELL_STRING, "etc_code",	codeMap, CellVo.ALIGN_CENTER)
							);

		sampleService.selectExlSampleList(params, largeExcel.getSheet(), excelColumn, largeExcel);

		getExcelHelper().endLargeExcel( response,  largeExcel.getWorkbook(), templateFile.getName());

	}


	@RequestMapping(value = "/file/jexcelDown")
	public void jexcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {


		try ( InputStream io = new FileInputStream(new File(getExceltemplatePath(request), "test.xlsx"));
				OutputStream os = response.getOutputStream();) {

			List<FtMap> dataList = new ArrayList<FtMap>();
			FtMap ftMap= new FtMap();
			ftMap.put("seq", "1");
			ftMap.put("name", "122");
			ftMap.put("phone", "00-22-22");

			dataList.add(ftMap);


			ftMap= new FtMap();
			ftMap.put("seq", "11");
			ftMap.put("name", "1221");
			ftMap.put("phone", "00-212-22");

			dataList.add(ftMap);

			Context context = new Context();
			context.putVar("dataList", dataList);

			response.setContentType("application/msexcel");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			response.setHeader("Content-Disposition", "attachment; filename=\"" +  java.net.URLEncoder.encode( "테스트.xlsx", "utf-8") + "\";");


			JxlsHelper.getInstance().processTemplate(io, os, context);
		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			LOGGER.error(e.toString());
		}

	}

}
