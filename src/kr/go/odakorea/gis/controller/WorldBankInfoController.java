package kr.go.odakorea.gis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.go.odakorea.gis.service.WorldBankService;

@RestController
public class WorldBankInfoController {



	@Value("${indicator.template.path}")
	private String indicatorTemplatePath;

	@Autowired
	WorldBankService worldBankService;


    @GetMapping("/api/indicators/flat")
    public ResponseEntity<Map<String, Object>> getFlatIndicatorMap() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> flatMap = new LinkedHashMap<>();

        try {
            // JSON 파일 경로 지정
            File jsonFile = new File(indicatorTemplatePath+File.separator+"indicators.json");

            // JSON을 읽고 Map<String, List<Map<String, Object>>> 형식으로 파싱
            TypeReference<Map<String, List<Map<String, Object>>>> typeRef =
                new TypeReference<Map<String, List<Map<String, Object>>>>() {};

            Map<String, List<Map<String, Object>>> categoryMap = mapper.readValue(jsonFile, typeRef);

            // 모든 항목을 평탄화
            for (List<Map<String, Object>> indicatorList : categoryMap.values()) {
                for (Map<String, Object> indicator : indicatorList) {
                    String code = (String) indicator.get("code");
                    Map<String, Object> indicatorData = new LinkedHashMap<>(indicator);
                    indicatorData.remove("code"); // 코드 키는 제거
                    flatMap.put(code, indicatorData);
                }
            }

            return ResponseEntity.ok(flatMap);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/api/indicators/{indicatorCode}")
    public  ResponseEntity<List<Map<String, Object>>> getIndicator(@PathVariable String indicatorCode) {





        List< Map<String, Object> > resultList= new ArrayList<Map<String, Object>>();

        Map<String, Object> resultMap = new LinkedHashMap<>();

        resultMap.put("국가", "AF");
        resultMap.put("지표명", "극빈곤율");
        resultMap.put("지표코드", "SI.POV.DDAY");
        resultMap.put("단위", "%");
        resultMap.put("연도", "2022");
        resultMap.put("값", "82.18");

        resultList.add(resultMap);

        resultMap = new LinkedHashMap<>();

        resultMap.put("국가", "ET");
        resultMap.put("지표명", "극빈곤율");
        resultMap.put("지표코드", "SI.POV.DDAY");
        resultMap.put("단위", "%");
        resultMap.put("연도", "2022");
        resultMap.put("값", "51.51");

        resultList.add(resultMap);

        return ResponseEntity.ok(resultList);

    }

    @GetMapping("/api/countries/{countryCode}/indicators")
    public  ResponseEntity<List<Map<String, Object>>> getALlIndicator(@PathVariable String countryCode) {


        List< Map<String, Object> > resultList= new ArrayList<Map<String, Object>>();


        if(countryCode.equals("AF")) {
        	 Map<String, Object> resultMap = new LinkedHashMap<>();

	          resultMap.put("국가", "AF");
	          resultMap.put("지표명", "극빈곤율");
	          resultMap.put("지표코드", "SI.POV.DDAY");
	          resultMap.put("단위", "%");
	          resultMap.put("연도", "2022");
	          resultMap.put("값", "82.18");

	          resultList.add(resultMap);

	          resultMap = new LinkedHashMap<>();

     	    resultMap.put("국가", "AF");
     	    resultMap.put("지표명", "안전한 식수 접근률");
     	    resultMap.put("지표코드", "SH.H2O.BASW.ZS");
     	    resultMap.put("단위", "%");
     	    resultMap.put("연도", "2022");
     	    resultMap.put("값", "82.18");

     	    resultList.add(resultMap);

     	    resultMap = new LinkedHashMap<>();

             resultMap.put("국가", "AF");
             resultMap.put("지표명", "전력 접근률");
             resultMap.put("지표코드", "EG.ELC.ACCS.ZS");
             resultMap.put("단위", "%");
             resultMap.put("연도", "2023");
             resultMap.put("값", "85.30");

             resultList.add(resultMap);

             resultMap = new LinkedHashMap<>();

             resultMap.put("국가", "AF");
             resultMap.put("지표명", "초등교육 순등록률");
             resultMap.put("지표코드", "SE.PRM.NENR");
             resultMap.put("단위", "%");
             resultMap.put("연도", "1993");
             resultMap.put("값", "26.77");

             resultList.add(resultMap);
        }


        return ResponseEntity.ok(resultList);

    }




    @GetMapping("/api/test")
    public ResponseEntity<Map<String, Object>> test() {

    	try {
			worldBankService.fetchWorldBankData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;


    }



}


