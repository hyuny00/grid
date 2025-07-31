package kr.go.odakorea.gis.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futechsoft.framework.component.RestTemplateFactory;
import com.futechsoft.framework.util.FtMap;

import kr.go.odakorea.gis.mapper.WorldBankMapper;
import kr.go.odakorea.gis.util.CountryCodeUtil;
import kr.go.odakorea.gis.vo.IndicatorInfoVo;

@Service
public class WorldBankService {

	@Autowired
	RestTemplateFactory restTemplateFactory;

	@Value("${worldbank.baseUrl}")
	private String worldbankBaseUrl;
	
	
	@Value("${indicator.template.path}")
	private String indicatorTemplatePath;
	
	
	@Value("${ap.worldBankRelayYn}")
	private String worldBankRelayYn;
	

	@Resource(name = "gis.mapper.WorldBankMapper")
	private WorldBankMapper worldBankMapper;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WorldBankService.class);
	
	
	private  Map<String, List<IndicatorInfoVo>> comprehensiveIndicators = new ConcurrentHashMap<>();
	
	/*
	private final List<String> odaCountries = Arrays.asList(
	    "AFG", // 아프가니스탄
	    "ETH"
	);
*/

	@PostConstruct
	public void init() throws Exception {
		comprehensiveIndicators = loadIndicators("indicators.json");
	}
	
	
	// 테스트용 - 실제 운영에서는 cron으로 변경
	// @Scheduled(cron = "0 0 3 * * *")
	//@Scheduled(fixedRate = 60000) // 1분마다 실행 (테스트용)
	
	//매년 1월, 4월, 7월, 10월의 1일 오전 3시 정각
	@Scheduled(cron = "0 0 3 1 1,4,7,10 *") 
	public void fetchWorldBankData() throws Exception {
		
		if(worldBankRelayYn.equals("N")) return;
		
		List<String> odaCountries = worldBankMapper.listNtnCd(); 
		
		
		File file = new File(indicatorTemplatePath+File.separator+"indicator-result.json");

	    if (file.exists()) {
	    	file.delete();
	    }
		
		LOGGER.info("=== World Bank 데이터 수집 시작 ===");
		
		// 기본 설정 확인
		if (worldbankBaseUrl == null || worldbankBaseUrl.trim().isEmpty()) {
			LOGGER.error("worldbank.baseUrl이 설정되지 않았습니다.");
			return;
		}
		
		LOGGER.info("worldbank.baseUrl: {}", worldbankBaseUrl);
		LOGGER.info("처리 대상 국가 수: {}", odaCountries.size());
		LOGGER.info("지표 카테고리 수: {}", comprehensiveIndicators.size());
		
		String BASE_URL = worldbankBaseUrl + "/v2/country/{country}/indicator/{indicator}?format=json&MRV=1";
		
		RestTemplate restTemplate = null;
		try {
			restTemplate = restTemplateFactory.getRestTemplate();
		} catch (Exception e) {
			return;
		}
		
	    // 중복 방지용 Set - 메소드 범위로 이동
	    Set<String> processedKeys = new HashSet<>();
	    int totalRequests = 0;
	    int successfulRequests = 0;
	    
	    // 실패 추적용
	    Map<String, Set<String>> failedByCountry = new HashMap<>(); // 국가별 실패한 지표들
	    Map<String, Set<String>> failedByIndicator = new HashMap<>(); // 지표별 실패한 국가들
	    Set<String> completeFailures = new HashSet<>(); // 완전히 실패한 요청들
	    
	    LOGGER.info("데이터 수집 시작 - 국가별 지표 처리...");
		for (String country : odaCountries) {
			LOGGER.info("국가 처리 중: {}", country);
			
		    for (Map.Entry<String, List<IndicatorInfoVo>> entry : comprehensiveIndicators.entrySet()) {
		        String category = entry.getKey();
		        LOGGER.info("  카테고리: {} (지표 {}개)", category, entry.getValue().size());
		        
		        for (IndicatorInfoVo indicatorInfo : entry.getValue()) {
		        	
		        	totalRequests++;
		        	LOGGER.info("    처리 중: {} - {}", indicatorInfo.getCode(), indicatorInfo.getName());
		        	
		        	// API 호출 속도 제한 (World Bank API는 초당 5회 제한)
		        	try {
		        		Thread.sleep(1000);
		        	} catch (InterruptedException e) {
		        		Thread.currentThread().interrupt();
		        		LOGGER.warn("작업이 중단되었습니다.");
		        		return;
		        	}
		        	
		        	boolean success = false;
		        	int maxRetries = 3;
		        	
		        	for (int retry = 0; retry < maxRetries && !success; retry++) {
			            try {
			                String url = BASE_URL
			                    .replace("{country}", country)
			                    .replace("{indicator}", indicatorInfo.getCode());

			                LOGGER.info("      API 호출 (시도 {}/{}): {}", retry + 1, maxRetries, url);
			                
			                ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
			                
			                LOGGER.info("      응답 상태: {}", response.getStatusCode());
			                
			                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			                    Object[] body = response.getBody();
			                    LOGGER.info("      응답 배열 길이: {}", body.length);
			                    
			                    if (body.length > 1 && body[1] instanceof List) {
			                        @SuppressWarnings("unchecked")
			                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body[1];
			                        
			                        LOGGER.info("      데이터 리스트 크기: {}", dataList.size());
			                        
			                        if (!dataList.isEmpty()) {
			                            success = true;
			                            successfulRequests++;
			                            LOGGER.info("      ✓ 데이터 처리 시작");
			                            processDataList(dataList, indicatorInfo, category, country, processedKeys);
			                            
			                        } else {
			                            LOGGER.info("      ! 데이터 없음: {} - {} ({})", country, indicatorInfo.getName(), category);
			                            
			                            // 데이터 없음도 실패로 간주하여 기록
			                            String noDataKey = String.format("%s-%s (%s) [데이터없음]", country, indicatorInfo.getCode(), category);
			                            completeFailures.add(noDataKey);
			                            
			                            failedByCountry.computeIfAbsent(country, k -> new HashSet<>())
			                                .add(indicatorInfo.getCode() + " (" + indicatorInfo.getName() + ") [데이터없음]");
			                            
			                            String indicatorKey = indicatorInfo.getCode() + " (" + indicatorInfo.getName() + ")";
			                            failedByIndicator.computeIfAbsent(indicatorKey, k -> new HashSet<>())
			                                .add(country + " [데이터없음]");
			                        }
			                    } else {
			                        LOGGER.warn("      ! 응답 형식 오류: body[1]이 List가 아님");
			                    }
			                } else {
			                    LOGGER.warn("      ! API 호출 실패: {} - {} ({}), 상태코드: {}", 
			                    		country, indicatorInfo.getName(), category, 
			                    		response.getStatusCode());
			                    
			                    // HTTP 오류도 실패로 기록
			                    if (retry == maxRetries - 1) {
			                        String httpErrorKey = String.format("%s-%s (%s) [HTTP-%s]", 
			                            country, indicatorInfo.getCode(), category, response.getStatusCode());
			                        completeFailures.add(httpErrorKey);
			                        
			                        failedByCountry.computeIfAbsent(country, k -> new HashSet<>())
			                            .add(indicatorInfo.getCode() + " (" + indicatorInfo.getName() + ") [HTTP-" + response.getStatusCode() + "]");
			                        
			                        String indicatorKey = indicatorInfo.getCode() + " (" + indicatorInfo.getName() + ")";
			                        failedByIndicator.computeIfAbsent(indicatorKey, k -> new HashSet<>())
			                            .add(country + " [HTTP-" + response.getStatusCode() + "]");
			                    }
			                }
			                
			            } catch (Exception e) {
			                if (retry == maxRetries - 1) {
			                    // 최종 실패 시 실패 정보 기록
			                    String failureKey = String.format("%s-%s (%s)", country, indicatorInfo.getCode(), category);
			                    completeFailures.add(failureKey);
			                    
			                    // 국가별 실패 지표 기록
			                    failedByCountry.computeIfAbsent(country, k -> new HashSet<>())
			                        .add(indicatorInfo.getCode() + " (" + indicatorInfo.getName() + ")");
			                    
			                    // 지표별 실패 국가 기록
			                    String indicatorKey = indicatorInfo.getCode() + " (" + indicatorInfo.getName() + ")";
			                    failedByIndicator.computeIfAbsent(indicatorKey, k -> new HashSet<>())
			                        .add(country);
			                    
			                    LOGGER.error("      ✗ API 호출 완전 실패: {} - {} ({}): {}", 
			                    		country, indicatorInfo.getName(), category, e.getMessage());
			                } else {
			                    LOGGER.warn("      ⚠ API 호출 오류 (재시도 {}/{}): {} - {} ({}): {}", 
			                    		retry + 1, maxRetries, country, indicatorInfo.getName(), category, e.getMessage());
			                    
			                    try {
			                        Thread.sleep(2000 * (retry + 1)); // 점진적 백오프
			                    } catch (InterruptedException ie) {
			                        Thread.currentThread().interrupt();
			                        LOGGER.warn("작업이 중단되었습니다.");
			                        return;
			                    }
			                }
			            }
		        	}
		        }
		    }
		    
		/*
		    Path sourcePath = Paths.get(indicatorTemplatePath, "indicator-result.json");
		    Path targetPath = Paths.get(indicatorTemplatePath, "final-indicator-result.json");

		    try {
		        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		        System.out.println("파일 복사 및 이름 변경 완료 (기존 파일 덮어씀)");
		    } catch (IOException e) {
		        e.printStackTrace();
		        System.out.println("파일 복사 실패");
		    }
*/
		    
		    
		}
		
		LOGGER.info("=== World Bank 데이터 수집 완료 ===");
		LOGGER.info("총 요청: {}, 성공: {}, 실패: {}", 
				totalRequests, successfulRequests, totalRequests - successfulRequests);
		
		// 실패 요약 로그 출력
		if (!completeFailures.isEmpty()) {
			LOGGER.warn("=== 실패 요약 ===");
			LOGGER.warn("완전히 실패한 요청 수: {}", completeFailures.size());
			
			// 국가별 실패 현황
			LOGGER.warn("--- 국가별 실패 현황 ---");
			for (Map.Entry<String, Set<String>> entry : failedByCountry.entrySet()) {
				String country = entry.getKey();
				Set<String> failedIndicators = entry.getValue();
				LOGGER.warn("{}: {} 개 지표 실패", country, failedIndicators.size());
				for (String indicator : failedIndicators) {
					LOGGER.warn("  - {}", indicator);
				}
			}
			
			// 지표별 실패 현황
			LOGGER.warn("--- 지표별 실패 현황 ---");
			for (Map.Entry<String, Set<String>> entry : failedByIndicator.entrySet()) {
				String indicator = entry.getKey();
				Set<String> failedCountries = entry.getValue();
				LOGGER.warn("{}: {} 개 국가에서 실패", indicator, failedCountries.size());
				LOGGER.warn("  실패 국가: {}", String.join(", ", failedCountries));
			}
			
			// 가장 문제가 많은 국가/지표 식별
			String mostProblematicCountry = failedByCountry.entrySet().stream()
				.max(Map.Entry.<String, Set<String>>comparingByValue((a, b) -> Integer.compare(a.size(), b.size())))
				.map(Map.Entry::getKey)
				.orElse("없음");
			
			String mostProblematicIndicator = failedByIndicator.entrySet().stream()
				.max(Map.Entry.<String, Set<String>>comparingByValue((a, b) -> Integer.compare(a.size(), b.size())))
				.map(Map.Entry::getKey)
				.orElse("없음");
			
			LOGGER.warn("가장 문제가 많은 국가: {} ({} 개 지표 실패)", 
				mostProblematicCountry, 
				failedByCountry.getOrDefault(mostProblematicCountry, new HashSet<>()).size());
			
			LOGGER.warn("가장 문제가 많은 지표: {} ({} 개 국가에서 실패)", 
				mostProblematicIndicator,
				failedByIndicator.getOrDefault(mostProblematicIndicator, new HashSet<>()).size());
			
		} else {
			LOGGER.info("✓ 모든 요청이 성공했습니다!");
		}
		
	
	}
	
	

	
	@SuppressWarnings("unchecked")
	private void processDataList(List<Map<String, Object>> dataList, IndicatorInfoVo indicatorInfo, 
			String category, String country, Set<String> processedKeys) throws Exception {
		
		
		for (Map<String, Object> data : dataList) {
            Object valueObj = data.get("value");
            Double value = null;

            if (valueObj instanceof Number) {
                value = ((Number) valueObj).doubleValue();
            }

            String date = (String) data.get("date");
            String uniqueKey = country + "-" + indicatorInfo.getCode() + "-" + date;

            // 중복 처리 방지
            if (processedKeys.contains(uniqueKey)) {
                continue;
            }
            processedKeys.add(uniqueKey);

            String evaluation = indicatorInfo.evaluateValue(value);
            String valueStr = value != null ? String.format("%.2f", value) : "N/A";

            // 로그 출력
            LOGGER.info("=====================================");
            LOGGER.info("분류: {}", category);
            LOGGER.info("국가: {}", country);
            LOGGER.info("지표명: {} ({})", indicatorInfo.getName(), indicatorInfo.getCode());
            LOGGER.info("설명: {}", indicatorInfo.getDescription());
            LOGGER.info("단위: {}", indicatorInfo.getUnit());
            LOGGER.info("연도: {}", date);
            LOGGER.info("값: {}", valueStr);
            LOGGER.info("평가: {}", evaluation);
            LOGGER.info("평가기준: {}", indicatorInfo.getCriteriaExplanation());
            LOGGER.info("=====================================");
            
            /*
             *
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 국가: AFG
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 지표명: 초등교육 순등록률 (SE.PRM.NENR)
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 설명: 초등학교 연령대 아동 중 실제로 초등교육에 등록한 비율로, 교육 접근성과 참여도를 보여줍니다. 높을수록 교육기회가 널리 제공됨을 의미합니다.
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 단위: %
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 연도: 1993
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 값: 26.77
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 평가: 나쁨
					2025-07-09 17:24:26,023  INFO [kr.go.odakorea.gis.service.WorldBankService] 평가기준: 기준: 95.0 이상(좋음), 80.0 이하(나쁨), 그 사이(보통)
             */
          
            FtMap param = new FtMap();
            //param.put("edcfNtnCd",  CountryCodeUtil.toAlpha2(country) );
            param.put("edcfNtnCd", country);
            param.put("idctCdVl", indicatorInfo.getCode());
            param.put("crtrYr", date);
            param.put("idctNm", indicatorInfo.getName());
            param.put("idctUnit", indicatorInfo.getUnit());
            param.put("idctVl", valueStr);

            LOGGER.debug("param....{}", param);
            
            
            //DB저장.
            try {
	            worldBankMapper.deletetNtnIdct(param);
	            worldBankMapper.createNtnIdct(param);
            }catch(Exception e) {
            	
            	LOGGER.error(e.toString());
            }
	            
       
            
        }
	}
	
	
	private  Map<String, List<IndicatorInfoVo>> loadIndicators(String fileNm) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, List<IndicatorInfoVo>> indicators = null;
		try {
			indicators = objectMapper.readValue(new File(indicatorTemplatePath+File.separator+fileNm),
					new TypeReference<Map<String, List<IndicatorInfoVo>>>() {
					});
		} catch (StreamReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indicators;
	}
	
	

}