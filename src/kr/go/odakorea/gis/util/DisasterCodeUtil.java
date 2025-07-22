package kr.go.odakorea.gis.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 재난코드를 관리한다
 * @author 박동현-행정망
 *
 */
public class DisasterCodeUtil {

    private static final Map<String, String> disasterTranslationMap = new HashMap<>();

    static {
        disasterTranslationMap.put("Cold Wave", "한파");
        disasterTranslationMap.put("Complex Emergency", "복합 재난");
        disasterTranslationMap.put("Drought", "가뭄");
        disasterTranslationMap.put("Earthquake", "지진");
        disasterTranslationMap.put("Epidemic", "전염병");
        disasterTranslationMap.put("Extratropical Cyclone", "외열대성 사이클론");
        disasterTranslationMap.put("Fire", "화재");
        disasterTranslationMap.put("Flash Flood", "급류 홍수");
        disasterTranslationMap.put("Flood", "홍수");
        disasterTranslationMap.put("Heat Wave", "폭염");
        disasterTranslationMap.put("Insect Infestation", "해충 피해");
        disasterTranslationMap.put("Land Slide", "산사태");
        disasterTranslationMap.put("Mud Slide", "진흙사태");
        disasterTranslationMap.put("Other", "기타");
        disasterTranslationMap.put("Severe Local Storm", "국지성 폭풍");
        disasterTranslationMap.put("Snow Avalanche", "눈사태");
        disasterTranslationMap.put("Storm Surge", "폭풍 해일");
        disasterTranslationMap.put("Technological Disaster", "기술 재난");
        disasterTranslationMap.put("Tropical Cyclone", "열대성 사이클론");
        disasterTranslationMap.put("Tsunami", "쓰나미");
        disasterTranslationMap.put("Volcano", "화산");
        disasterTranslationMap.put("Wild Fire", "산불");
       
    }

    public static String translate(String englishTerm) {
        return disasterTranslationMap.getOrDefault(englishTerm, englishTerm);  // 값 없으면 원문 반환
    }

    public static void main(String[] args) {
        // 예제
        System.out.println(translate("Tsunami"));             // 쓰나미
        System.out.println(translate("Alien Invasion"));      // Alien Invasion
    }
}
