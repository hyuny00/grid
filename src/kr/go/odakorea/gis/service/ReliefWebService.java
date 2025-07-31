package kr.go.odakorea.gis.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futechsoft.framework.component.RestTemplateFactory;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FtMap;

import kr.go.odakorea.gis.mapper.ReliefWebMapper;
import kr.go.odakorea.gis.util.CountryCodeUtil;
import kr.go.odakorea.gis.util.DisasterCodeUtil;

/**
 * 재난정보 수집서비스
* @packageName    : kr.go.odakorea.gis.service
* @fileName       : ReliefWebService.java
* @description    :
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.06.17        pdh       최초 생성
 */
@Service
public class ReliefWebService {
	
	
	@Autowired
	RestTemplateFactory restTemplateFactory;
    
	@Value("${reliefweb.baseUrl}")
	private String reliefwebBaseUrl;
	
	
	@Value("${ap.reliefWebRelayYn}")
	private String reliefWebRelayYn;
	
	
	@Resource(name = "gis.mapper.ReliefWebMapper")
	private ReliefWebMapper reliefWebMapper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReliefWebService.class);

	
    //@Scheduled(fixedRate = 30000) 
	//3시간 마다 실행
    @Scheduled(cron = "0 0 0/3 * * *")
	public void saveDisaster() throws Exception {
    	
		if(reliefWebRelayYn.equals("N")) return;
    	
    	LOGGER.debug("reliefwebBaseUrl  get: " +reliefwebBaseUrl);
		
		RestTemplate  restTemplate = restTemplateFactory.getRestTemplate();
		
		ResponseEntity<String> response = restTemplate.getForEntity(reliefwebBaseUrl, String.class);
        String json = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> root=null;
        
		try {
			root = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

        @SuppressWarnings("unchecked")
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) root.get("data");

        List<FtMap> resultList = new ArrayList<FtMap>();

        for (Map<String, Object> item : dataList) {
            Map<String, Object> fields = (Map<String, Object>) item.get("fields");
            FtMap flatMap = new FtMap();

            flatMap.put("clmtyIdVl", String.valueOf(item.get("id")));

           // flatMap.put("name", String.valueOf(fields.get("name")));
            flatMap.put("status", String.valueOf(fields.get("status")));

            List<Map<String, Object>> countries = (List<Map<String, Object>>) fields.get("country");
            
            if (countries != null && !countries.isEmpty()) {
                Map<String, Object> country = countries.get(0);
                flatMap.put("country", String.valueOf(country.get("name")));
                flatMap.put("iso3", String.valueOf(country.get("iso3")));  // profile=full이면 대부분 포함됨
                
                Map<String, Object> location = (Map<String, Object>) country.get("location");
                if (location != null) {
                    Object lat = location.get("lat");
                    Object lon = location.get("lon");
                    flatMap.put("latitude", lat != null ? lat.toString() : "");
                    flatMap.put("longitude", lon != null ? lon.toString() : "");
                }
            }
            
            
            
         

            List<Map<String, Object>> types = (List<Map<String, Object>>) fields.get("type");
            if (types != null && !types.isEmpty()) {
                //flatMap.put("type", String.valueOf(types.get(0).get("name")));
                flatMap.put("clmtyNm", String.valueOf(types.get(0).get("name")));
            }

            Map<String, Object> date = (Map<String, Object>) fields.get("date");
            if (date != null) {
                flatMap.put("created", String.valueOf(date.get("event")));
            }

            flatMap.put("url", String.valueOf(fields.get("url")));

            resultList.add(flatMap);
        }


        // OffsetDateTime으로 파싱
        OffsetDateTime utcDateTime = null;

        // Asia/Seoul 시간대로 변환
        ZonedDateTime   kstDateTime =  null;

        // 출력 형식 지정 (선택)
        DateTimeFormatter formatter = null;
        
        
        // 결과 출력
        for (FtMap param : resultList) {
            System.out.println(param); 
            
            int count= reliefWebMapper.getClmtyIdVl(param);
            
            param.put("ntnCd", CountryCodeUtil.toAlpha2(param.getString("iso3")));
            
            //param.put("iso3", CountryCodeUtil.getAlpha2ByName(param.getString("iso3")));
            
            param.put("clmtyNm", DisasterCodeUtil.translate(param.getString("clmtyNm")));
            
            // OffsetDateTime으로 파싱
             utcDateTime = OffsetDateTime.parse(param.getString("created"));

            // Asia/Seoul 시간대로 변환
             kstDateTime = utcDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul"));

            // 출력 형식 지정 (선택)
             formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

             // 포맷 후 파라미터에 넣기
             param.put("clmtyOcrnDt", kstDateTime.format(formatter));
          
            
            if(CommonUtil.nvl(param.getString("status")).equals("past")) {
            	 param.put("clmtyPrgrsStcd", "02");
            }else if (CommonUtil.nvl(param.getString("status")).equals("ongoing")) {
            	 param.put("clmtyPrgrsStcd", "01");
            }else if (CommonUtil.nvl(param.getString("status")).equals("alert")) {
            	 param.put("clmtyPrgrsStcd", "03");
            }
            
            
            param.put("userId", "system");
            
            LOGGER.debug("param...{}", param);
            //{clmtyIdVl=52399, country=Venezuela (Bolivarian Republic of), clmtyNm=홍수, created=2025-07-10T08:26:26+00:00, clmtyOcrnDt=2025-07-10 17:26:26, clmtyPrgrsStcd=01, ntnCd=null, userId=system, url=https://reliefweb.int/taxonomy/term/52399, status=ongoing}
          
            
            try {
	             if(count == 0) {
	            	 reliefWebMapper.createRcntnClmty(param);
		       	 } else {
		       		reliefWebMapper.updateClmtyPrgrsStcd(param);
		       	 }
            }catch(Exception e) {
            	e.printStackTrace();
            }
            
        }
		
	}
	
	
}

/*
 * {country=Armenia, created=2024-06-13T13:08:24+00:00, name=Armenia: Floods - May 2024, id=52036, type=Flash Flood, url=https://reliefweb.int/taxonomy/term/52036, status=past}
{country=Botswana, created=2024-06-12T13:58:10+00:00, name=Botswana: Drought - May 2024, id=52034, type=Drought, url=https://reliefweb.int/taxonomy/term/52034, status=past}
{country=Sri Lanka, created=2024-06-12T03:55:16+00:00, name=Sri Lanka: Floods and Landslides - May 2024, id=52032, type=Flood, url=https://reliefweb.int/taxonomy/term/52032, status=past}
{country=Belize, created=2024-06-11T13:28:31+00:00, name=Belize: Wild Fires - May 2024, id=52030, type=Wild Fire, url=https://reliefweb.int/taxonomy/term/52030, status=past}

*/

