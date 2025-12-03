package kr.go.odakorea.gis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futechsoft.gis.vo.Coordinates;

import kr.go.odakorea.gis.service.GeocodingService;

/**
 * 지오코딩을 관리하는 Controller
* @packageName    : kr.go.odakorea.gis.controller
* @fileName       : GeocodingController.java
* @description    :
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.22        pdh       최초 생성
 */
@RestController
public class GeocodingController {


	@Autowired
    private  GeocodingService geocodingService;


	/**
	 * 지역명으로 위도 경도 정보를 가져온다
	 * @param query
	 * @return
	 */
    @GetMapping("/api/geocode")
    public Coordinates geocode(@RequestParam String query) {
        return geocodingService.geocode(query);
    }

}
