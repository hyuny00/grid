package kr.go.odakorea.gis.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.go.odakorea.gis.service.TileService;

@RestController
@RequestMapping("/tiles")
public class TileController {

	@Autowired
    private  TileService tileService;



    @GetMapping("/{zoom}/{x}/{y}.png")
    public ResponseEntity<byte[]> getTile(@PathVariable int zoom, @PathVariable int x, @PathVariable int y) {
        // B 서버에서 타일을 가져옵니다.
        byte[] tile = tileService.getTile(zoom, x, y);


        // 타일 이미지 파일을 클라이언트에게 반환합니다.
        return ResponseEntity.ok()
                             .header("Content-Type", "image/png")
                             .body(tile);
    }


    @GetMapping("/maptiler/{style}/{zoom}/{x}/{y}")
    public ResponseEntity<byte[]> getMapTilerTileWithStyle(
    		 @PathVariable String style,
             @PathVariable int zoom,
             @PathVariable int x,
             @PathVariable int y,
             HttpServletResponse response) {

        try {

        	  // 이미 response가 commit되었는지 확인
            if (response.isCommitted()) {
                return null;
            }


        	System.out.println("Style: " + style + ", Zoom: " + zoom + ", X: " + x + ", Y: " + y);
            byte[] tileData = tileService.getMaptilerTile(zoom, x, y, style);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setCacheControl("max-age=86400"); // 24시간

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(tileData);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }





   /*
    public void test() {

        try {
            // 유효한 위도/경도 (서울역 → 강남역)
            String origin = "126.9723,37.5564";  // 서울역
            String destination = "127.0276,37.4979";  // 강남역

            // Directions API URL (driving mode)
            String urlStr = String.format(
                "https://api.mapbox.com/directions/v5/mapbox/driving/%s;%s?geometries=geojson&overview=full&access_token=%s",
                origin, destination, "pk.eyJ1IjoiZnV0ZWNoIiwiYSI6ImNtOXVtNWYzbTA5eXIyanI4bXJjMTl0bHAifQ.iDR1Jma7ZhntwfF67SJ4kQ"
            );

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder responseContent = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                in.close();

                // JSON 응답 출력
                System.out.println("응답 결과:");
                System.out.println(responseContent.toString());

            } else {
                System.out.println("HTTP 요청 실패: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

*/
}