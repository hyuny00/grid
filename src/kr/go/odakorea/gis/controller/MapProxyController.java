package kr.go.odakorea.gis.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class MapProxyController {


	@Value("${maptiler.proxy.baseUrl}")
	private String procyMapTilerBaseUrl;


	@Value("${maptiler.apiKey}")
	private String apiKey;

	@Value("${maptiler.client.baseUrl}")
	private String clientBaseUrl;

	@Autowired
	RestTemplateFactory restTemplateFactory;

	/**
	 * 지도 스타일 정보를 가져온다
	 * @param mapId
	 * @return
	 */
    @GetMapping("/style.json")
    public ResponseEntity<String> proxyStyleJson(@PathVariable String mapId) {

    	RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

        String originUrl = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/style.json")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Style.json Request =====");
        System.out.println("URL: " + originUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(originUrl, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to load style.json");
        }

        String originalJson = response.getBody();
        if (originalJson == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Empty style.json");
        }

        String proxyBaseUrl = clientBaseUrl + "/maps/" + mapId;

        String modifiedJson = originalJson
                .replaceAll("https://api\\.maptiler\\.com/tiles/[^\"]*",
                        proxyBaseUrl + "/tiles.json")
                .replaceAll("https://api\\.maptiler\\.com/fonts/[^\"]*",
                        proxyBaseUrl + "/fonts/{fontstack}/{range}.pbf")
                .replaceAll("https://api\\.maptiler\\.com/maps/" + mapId + "/sprite\\.png[^\"]*",
                        proxyBaseUrl + "/sprite.png")
                .replaceAll("https://api\\.maptiler\\.com/maps/" + mapId + "/sprite\\.json[^\"]*",
                        proxyBaseUrl + "/sprite.json");

        System.out.println("===== Style.json Modified =====");

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
    public ResponseEntity<String> proxyTiles(@PathVariable String mapId) {
        RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/tiles/v3/tiles.json")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Tiles.json Request =====");
        System.out.println("URL: " + url);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Failed to load tiles.json");
        }

        String tilesJson = response.getBody();
        if (tilesJson == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Empty tiles.json");
        }

        System.out.println("===== Original tiles.json =====");
        System.out.println(tilesJson);

        String proxyBaseUrl = clientBaseUrl + "/maps/" + mapId;

        // 타일 URL만 정확하게 치환 (경로 유지)
        String modifiedTilesJson = tilesJson
                .replaceAll(
                    "https://api\\.maptiler\\.com/tiles/v3/",
                    proxyBaseUrl + "/tiles/"
                )
                .replaceAll(
                    "https://api\\.maptiler\\.com/tiles/",
                    proxyBaseUrl + "/tiles/"
                );

        System.out.println("===== Modified tiles.json =====");
        System.out.println(modifiedTilesJson);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(modifiedTilesJson);
    }

    /**
     * 벡터 타일 데이터 요청 처리 (.pbf)
     * @param mapId
     * @param z 줌 레벨
     * @param x 타일 X 좌표
     * @param y 타일 Y 좌표
     * @return
     */

    @GetMapping("/tiles/{z}/{x}/{y}.pbf")
    public ResponseEntity<byte[]> proxyVectorTile(
            @PathVariable String mapId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y) {

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/tiles/v3/" + z + "/" + x + "/" + y + ".pbf")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Vector Tile Request =====");
        System.out.println("z=" + z + ", x=" + x + ", y=" + y);
        System.out.println("URL: " + url);

        return forwardBinaryResource(url, "application/x-protobuf");
    }

    /**
     * 래스터 타일 데이터 요청 처리 (.png)
     * @param mapId
     * @param z
     * @param x
     * @param y
     * @return
     */
    @GetMapping("/tiles/{z}/{x}/{y}.png")
    public ResponseEntity<byte[]> proxyRasterTile(
            @PathVariable String mapId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y) {

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/tiles/v3/" + z + "/" + x + "/" + y + ".png")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Raster Tile Request =====");
        System.out.println("z=" + z + ", x=" + x + ", y=" + y);

        return forwardBinaryResource(url, "image/png");
    }

    /**
     * WebP 타일 데이터 요청 처리
     * @param mapId
     * @param z
     * @param x
     * @param y
     * @return
     */
    @GetMapping("/tiles/{z}/{x}/{y}.webp")
    public ResponseEntity<byte[]> proxyWebpTile(
            @PathVariable String mapId,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y) {

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/tiles/v3/" + z + "/" + x + "/" + y + ".webp")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== WebP Tile Request =====");

        return forwardBinaryResource(url, "image/webp");
    }

    /**
     *  스타일 사양에서 사용되는 URL 템플릿으로, 벡터 맵에서 텍스트를 렌더링하기 위한 글리프(glyph) 데이터를 요청한다
     * @param mapId
     * @param fontstack
     * @param range
     * @return
     */
    @GetMapping("/fonts/{fontstack}/{range}.pbf")
    public ResponseEntity<byte[]> proxyFont(
            @PathVariable String mapId,
            @PathVariable String fontstack,
            @PathVariable String range) {

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/fonts/" + fontstack + "/" + range + ".pbf")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Font Request =====");
        System.out.println("Font: " + fontstack + ", Range: " + range);

        return forwardBinaryResource(url, "application/x-protobuf");
    }

    /**
     * 바이너리 리소스 전달 공통 메소드
     */
    private ResponseEntity<byte[]> forwardBinaryResource(String url, String contentType) {
        RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class
            );

            // 깨끗한 응답 헤더 생성
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.parseMediaType(contentType));

            // Content-Length 설정
            if (response.getBody() != null) {
                responseHeaders.setContentLength(response.getBody().length);
            }

            // CORS 헤더 추가
            responseHeaders.set("Access-Control-Allow-Origin", "*");

            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(responseHeaders)
                    .body(response.getBody());

        } catch (Exception e) {
            System.err.println("Error forwarding binary resource: " + url);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * MapTiler 에서 사용되는 스프라이트 이미지 파일을 반환한다
     * @param mapId
     * @return
     */
    @GetMapping("/sprite.png")
    public ResponseEntity<byte[]> proxySpritePng(@PathVariable String mapId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/sprite.png")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Sprite PNG Request =====");

        return forwardBinaryResource(url, "image/png");
    }

    /**
     * 고해상도 스프라이트 이미지
     * @param mapId
     * @return
     */
    @GetMapping("/sprite@2x.png")
    public ResponseEntity<byte[]> proxySpritePng2x(@PathVariable String mapId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/sprite@2x.png")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Sprite @2x PNG Request =====");

        return forwardBinaryResource(url, "image/png");
    }


    /**
     *  MapTiler에서 사용하는 스프라이트의 메타데이터를 반환한다
     * @param mapId
     * @return
     */
    @GetMapping("/sprite.json")
    public ResponseEntity<String> proxySpriteJson(@PathVariable String mapId) {
        RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/sprite.json")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Sprite JSON Request =====");
        System.out.println("URL: " + url);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Failed to load sprite.json");
        }

        String spriteJson = response.getBody();
        if (spriteJson == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Empty sprite.json");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(spriteJson);
    }

    /**
     * 고해상도 스프라이트 JSON
     * @param mapId
     * @return
     */
    @GetMapping("/sprite@2x.json")
    public ResponseEntity<String> proxySpriteJson2x(@PathVariable String mapId) {
        RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

        String url = UriComponentsBuilder
                .fromHttpUrl(procyMapTilerBaseUrl + "/maps/" + mapId + "/sprite@2x.json")
                .queryParam("key", apiKey)
                .toUriString();

        System.out.println("===== Sprite @2x JSON Request =====");
        System.out.println("URL: " + url);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Failed to load sprite@2x.json");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }


    /**
     * 프록시서버로 연결하는 공동 메소드이다
     * @param url
     * @return
     */
    private ResponseEntity<Resource> forwardResource(String url) {

    	RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
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

        } catch (Exception e) {
            System.err.println("Error forwarding resource: " + url);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/image")
    public ResponseEntity<byte[]> getStaticMapImage(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "14") int zoom,
            @RequestParam(defaultValue = "600") int width,
            @RequestParam(defaultValue = "400") int height,
            @PathVariable String mapId
    ) {

    	RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

    	String format = "png";
	    String url = String.format(
	            "https://api.maptiler.com/maps/%s/static/%f,%f,%d/%dx%d.%s?key=%s",
	            mapId, lon, lat, zoom, width, height, format, apiKey
	    );


	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));

	    HttpEntity<Void> entity = new HttpEntity<>(headers);

	    ResponseEntity<byte[]> response = restTemplate.exchange(
	            url,
	            HttpMethod.GET,
	            entity,
	            byte[].class
	    );

	    return ResponseEntity
	            .status(response.getStatusCode())
	            .contentType(MediaType.IMAGE_PNG)
	            .body(response.getBody());
    }

}