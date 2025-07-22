package com.futechsoft;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//C:/Users/Wakanda/Documents/iati/iati_act_KR-GOV-051_2023.xml
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.futechsoft.framework.util.FtMap;

public class IatiSaxParser {

    public static void main(String[] args) throws Exception {
    	
    	List<FtMap> list = new ArrayList<FtMap>();
    	
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        parser.parse(new File("C:/Users/Wakanda/Documents/iati/iati_act_KR-GOV-051_2023.xml"), new DefaultHandler() {

            boolean inActivity = false;
            boolean inIdentifier = false;
            boolean inLocation = false;
            boolean inName = false;
            boolean inNarrative = false;
            boolean inPos = false;
            boolean inRecipientCountry = false;

            String currentIdentifier = "";
            String currentNarrative = "";
            String currentPos = "";
            String currentCountryCode = "";
            String currentCountryName = "";

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                switch (qName) {
                    case "iati-activity":
                        inActivity = true;
                        currentIdentifier = "";
                        break;
                    case "iati-identifier":
                        if (inActivity) inIdentifier = true;
                        break;
                    case "location":
                        if (inActivity) inLocation = true;
                        break;
                    case "name":
                        if (inLocation) inName = true;
                        break;
                    case "narrative":
                        if (inName || inRecipientCountry) {
                            inNarrative = true;
                            currentNarrative = "";
                        }
                        break;
                    case "pos":
                        if (inLocation) {
                            inPos = true;
                            currentPos = "";
                        }
                        break;
                    case "recipient-country":
                        inRecipientCountry = true;
                        currentCountryCode = attributes.getValue("code");
                        currentCountryName = "";
                        break;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                String text = new String(ch, start, length).trim();
                if (inIdentifier && !text.isEmpty()) {
                    currentIdentifier += text;
                }
                if (inNarrative && !text.isEmpty()) {
                    currentNarrative += text;
                }
                if (inPos && !text.isEmpty()) {
                    currentPos += text;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
            	
            	 FtMap param= new FtMap();
            	  
                switch (qName) {
                    case "iati-identifier":
                        inIdentifier = false;
                        break;
                    case "narrative":
                        if (inName && inLocation) {
                            // 지역명 끝남
                        } else if (inRecipientCountry) {
                            currentCountryName = currentNarrative;
                        }
                        inNarrative = false;
                        break;
                    case "pos":
                        inPos = false;
                        break;
                    case "name":
                        inName = false;
                        break;
                    case "location":
                        inLocation = false;

                        if (param != null) {
                            param.put("rgnEngNm", currentNarrative);
                            param.put("iatiIdentifier", currentIdentifier);

                            String[] parts = currentPos.split("\\s+");
                            if (parts.length == 2) {
                                double latitude = Double.parseDouble(parts[0]);
                                double longitude = Double.parseDouble(parts[1]);

                                param.put("bizRgnPstnLatVl", latitude);
                                param.put("bizRgnPstnLotVl", longitude); // 기존 오류 수정
                            }

                            // 국가 코드, 국가명 추가
                            param.put("ntn_cd", currentCountryCode);
                            param.put("ntn_nm", currentCountryName);

                            list.add(param);
                        }
                        break;
                    case "recipient-country":
                        inRecipientCountry = false;
                        currentCountryName = currentNarrative;

                        // 로그 출력용
                        System.out.println("국가 코드: " + currentCountryCode);
                        System.out.println("국가명: " + currentCountryName);
                        System.out.println("===========================");

                        // param에는 여기서 넣지 말고 location 끝날 때 넣자
                        break;
                    case "iati-activity":
                        inActivity = false;
                        break;
                }
                
            }

            @Override
            public void endDocument() {
                System.out.println("전체 XML 파싱 완료");
            }
        });
        
        
        for(FtMap map : list) {
        	System.out.println(map);
        }
    }
}

/*
//tbm_biz_ntn_rgn_mt 이 테이블에 
biz_id :  사업 ID: KR-GOV-010-KR-GOV-051-2019090105282
crtr_yr
rcntn_ntn_no :수원국국가번호
rgn_no : 지역번호   국가지역코드의 rgn_no
reg_dt
reg_user_id
mdfcn_dt
mdfcn_user_id
del_yn


//tcm_ntn_rgn_cd 국가지역코드의 rgn_no
 */
