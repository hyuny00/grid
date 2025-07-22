package kr.go.odakorea.gis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.futechsoft.framework.component.RestTemplateFactory;


/**
 * 지도서비스를 관리하는 Controller  클래스
* @packageName    : kr.go.odakorea.gis.controller
* @fileName       : MapProxyController.java
* @description    :
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.22        pdh       최초 생성
 */
@RestController
@RequestMapping("/maps/{mapId}")
public class MapProxyController {
	
	
	@Value("${maptiler.proxy.baseUrl}")
	private String procyMapTilerBaseUrl;
	
	
	@Value("${maptiler.apiKey}")
	private String apiKey;
	
	@Autowired
	RestTemplateFactory restTemplateFactory;
	
	/**
	 * 지도 스타일 정보를 가져온다
	 * @param mapId
	 * @return
	 */
    @GetMapping("/style.json")
    public ResponseEntity<String> proxyStyleJson(@PathVariable String mapId) {
    	
    	
    	RestTemplate  restTemplate = restTemplateFactory.getRestTemplate();
    	
        String originUrl = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/style.json")
                .queryParam("key", apiKey)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(originUrl, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to load style.json");
        }

        String originalJson = response.getBody();
        if (originalJson == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Empty style.json");
        }

        String proxyBaseUrl = "http://localhost:8080/maps/" + mapId;

        String modifiedJson = originalJson
                .replaceAll("https://api\\.maptiler\\.com/tiles/[^\"\\s]+",
                        proxyBaseUrl + "/tiles.json")
                .replaceAll("https://api\\.maptiler\\.com/fonts/[^\"\\s]+",
                        proxyBaseUrl + "/fonts/{fontstack}/{range}.pbf")
                .replaceAll("https://api\\.maptiler\\.com/maps/" + mapId + "/sprite\\.png",
                        proxyBaseUrl + "/sprite.png")
                .replaceAll("https://api\\.maptiler\\.com/maps/" + mapId + "/sprite\\.json",
                        proxyBaseUrl + "/sprite.json");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(modifiedJson);
    }
    

    /**
     * 지도 Tiles를 가져온다
     * @param mapId
     * @return
     */
    @GetMapping("/tiles.json")
    public ResponseEntity<Resource> proxyTiles(@PathVariable String mapId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/tiles/v3/tiles.json")
                .queryParam("key", apiKey)
                .toUriString();
        return forwardResource(url);
    }

    /**
     *  스타일 사양에서 사용되는 URL 템플릿으로, 벡터 맵에서 텍스트를 렌더링하기 위한 글리프(glyph) 데이터를 요청한다
     * @param mapId
     * @param fontstack
     * @param range
     * @return
     */
    @GetMapping("/fonts/{fontstack}/{range}.pbf")
    public ResponseEntity<Resource> proxyFont(@PathVariable String mapId,
                                              @PathVariable String fontstack,
                                              @PathVariable String range) {
        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/fonts/" + fontstack + "/" + range + ".pbf")
                .queryParam("key", apiKey)
                .toUriString();
        return forwardResource(url);
    }

    
    /**
     * MapTiler 에서 사용되는 스프라이트 이미지 파일을 반환한다
     * @param mapId
     * @return
     */
    @GetMapping("/sprite.png")
    public ResponseEntity<Resource> proxySpritePng(@PathVariable String mapId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/sprite.png")
                .queryParam("key", apiKey)
                .toUriString();
        return forwardResource(url);
    }

    
    /**
     *  MapTiler에서 사용하는 스프라이트의 메타데이터를 반환한다
     * @param mapId
     * @return
     */
    @GetMapping("/sprite.json")
    public ResponseEntity<Resource> proxySpriteJson(@PathVariable String mapId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/sprite.json")
                .queryParam("key", apiKey)
                .toUriString();
        return forwardResource(url);
    }


    /**
     * 프록시서버로 연결하는 공동 메소드이다
     * @param url
     * @return
     */
    private ResponseEntity<Resource> forwardResource(String url) {
    	
    	RestTemplate  restTemplate = restTemplateFactory.getRestTemplate();
    	
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Resource> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Resource.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }
}