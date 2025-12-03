package kr.go.odakorea.gis.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.go.odakorea.gis.service.GeocodeUpdateService;

@RestController
public class TestController {


	  private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	    @Autowired
	    private GeocodeUpdateService geocodeUpdateService;



		    /**
		     * CSV 파일을 처리하여 UPDATE SQL을 생성합니다.
		     *
		     * @param csvFilePath CSV 파일 경로
		     * @return 처리 결과
		     */
		    @GetMapping("/process-csv")
		    public ResponseEntity<Map<String, Object>> processCSV() {

		    	/*
		        try {
		            List<String> sqlQueries = geocodeUpdateService.processCSVAndGenerateSQL("C:/test/rgn2.csv");

		            Map<String, Object> result = new HashMap<>();
		            result.put("success", true);
		            result.put("message", "CSV 처리 완료");
		            result.put("sqlCount", sqlQueries.size());
		            result.put("sqlQueries", sqlQueries);


		            logger.info("========== 생성된 UPDATE SQL ==========");
		            for (String sql : sqlQueries) {
		                System.out.println(sql);
		            }
		            logger.info("총 {}개의 SQL 쿼리가 생성되었습니다.", sqlQueries.size());

		            return ResponseEntity.ok(result);

		        } catch (Exception e) {
		            logger.error("CSV 처리 중 오류 발생", e);

		            Map<String, Object> result = new HashMap<>();
		            result.put("success", false);
		            result.put("message", "CSV 처리 실패: " + e.getMessage());

		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		        }
		        */
		    	 String result =geocodeUpdateService.processCSVAndUpdateCoordinates("C:/test/rgn3.csv", "C:/test/result2.csv");

		    	 Map<String, Object> result1 = new HashMap<>();
		    	 result1.put("success", true);
		    	 result1.put("message", "CSV 처리 완료");

		    	 return ResponseEntity.ok(result1);
		    }






}
