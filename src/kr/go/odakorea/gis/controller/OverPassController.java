package kr.go.odakorea.gis.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class OverPassController {

}


/**
보건의료 시설 (Healthcare Infrastructure)
의료기관

amenity=hospital → 병원, 종합병원
amenity=clinic → 의원, 클리닉, 진료소
amenity=doctors → 의사 진료실, 개인병원
healthcare=hospital → 의료용 병원 시설
healthcare=clinic → 의료용 클리닉
healthcare=centre → 보건소, 의료센터

전문의료시설

amenity=pharmacy → 약국, 약방
amenity=dentist → 치과, 치과의원

--------------------------------------------------
교육 시설 (Education Infrastructure)
교육기관

amenity=school → 학교 (초/중/고등학교)
amenity=kindergarten → 유치원, 어린이집
amenity=university → 대학교, 대학
amenity=college → 전문대학, 단과대학
building=school → 학교 건물

학습지원시설

amenity=library → 도서관, 공공도서관
--------------------------------------------------
식수 및 위생 시설 (Water & Sanitation)
식수시설

amenity=drinking_water → 음용수 시설, 식수대
man_made=water_well → 우물, 물우물
man_made=water_works → 정수장, 상수도 처리장
man_made=water_tower → 급수탑, 물탱크
natural=spring → 샘, 천연 용천수
pump=manual → 수동 펌프, 손펌프

위생시설

amenity=toilets → 화장실, 공중화장실
amenity=waste_disposal → 쓰레기 처리장, 폐기물 처리시설
man_made=wastewater_plant → 하수처리장, 오수처리시설
--------------------------------------------------
전력 인프라 (Energy Infrastructure)
발전시설

power=plant → 발전소, 전력 발전시설
power=generator → 발전기, 자가발전기

송배전시설

power=substation → 변전소, 전력 변환소
power=tower → 송전탑, 전력탑
power=pole → 전주, 전신주
power=line → 송전선, 전력선

연료공급

amenity=fuel → 주유소, 연료공급소
--------------------------------------------------
교통 인프라 (Transportation Infrastructure)
도로

highway=primary → 국도, 주요도로
highway=secondary → 지방도, 보조간선도로
highway=trunk → 간선도로, 고속화도로
highway=residential → 주거지역 도로, 생활도로
highway=track → 농로, 비포장도로
highway=path → 보행로, 산책로

교통시설

bridge=yes → 교량, 다리
amenity=bus_station → 버스터미널, 버스정류장
public_transport=stop_position → 대중교통 정거장
--------------------------------------------------
통신 인프라 (Communication Infrastructure)
통신서비스

amenity=internet_cafe → PC방, 인터넷 카페
amenity=post_office → 우체국, 우편취급소

통신시설

man_made=mast → 통신 철탑, 안테나 기둥
man_made=tower → 통신탑, 송신탑
office=telecommunication → 통신회사, 통신사업소
--------------------------------------------------
금융 서비스 (Financial Services)
금융기관

amenity=bank → 은행, 금융기관
amenity=atm → 현금자동입출금기, ATM
office=financial → 금융사무소, 금융서비스업체
amenity=money_transfer → 송금소, 환전소
--------------------------------------------------
농업 인프라 (Agricultural Infrastructure)
농업시설

landuse=farmland → 농지, 경작지
man_made=silo → 사일로, 곡물저장고
shop=agrarian → 농업용품점, 농자재상
amenity=marketplace → 시장, 전통시장
--------------------------------------------------
**/