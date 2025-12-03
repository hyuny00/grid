package kr.go.odakorea.gis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.futechsoft.framework.component.RestTemplateFactory;
import com.futechsoft.gis.vo.Coordinates;
import com.futechsoft.gis.vo.Feature;
import com.futechsoft.gis.vo.GeocodingResponse;

@Service("gis.service.GeocodingService")
public class GeocodingService {


	@Autowired
	RestTemplateFactory restTemplateFactory;

	@Value("${maptiler.proxy.baseUrl}")
	private String procyMapTilerBaseUrl;


	@Value("${maptiler.apiKey}")
	private String apiKey;


    /**
     * 지오코딩 API를 호출하여 검색어에 해당하는 위치의 위도/경도 정보를 가져옵니다.
     *
     * @param query 검색어 (주소, 장소명 등)
     * @return 위도와 경도 정보 (위도, 경도)
     */
    public Coordinates geocode(String query) {
        try {
        	RestTemplate  restTemplate = restTemplateFactory.getRestTemplate();

            // API URL 구성
            String url = UriComponentsBuilder
                    .fromHttpUrl(procyMapTilerBaseUrl + "/geocoding/" + query + ".json")
                    .queryParam("key", apiKey)
                    .build()
                    .toString();


            System.out.println("geocoding.................."+url);

            // API 호출
            GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class);

            // 첫 번째 결과의 좌표 반환 (결과가 없으면 null 반환)
            if (response != null && response.getFeatures() != null && !response.getFeatures().isEmpty()) {
                Feature firstFeature = response.getFeatures().get(0);
                if (firstFeature.getGeometry() != null && firstFeature.getGeometry().getCoordinates() != null) {
                    double[] coords = firstFeature.getGeometry().getCoordinates();
                    if (coords.length >= 2) {
                        // Maptiler는 [경도, 위도] 순서로 반환하므로 Coordinates 객체 생성 시 순서에 주의
                        return new Coordinates(coords[1], coords[0]);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("URL 인코딩 오류", e);
        }
    }

}