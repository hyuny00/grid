package kr.go.odakorea.nice.controller;


import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.futechsoft.framework.component.RestTemplateFactory;


@Controller
public class NiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(NiceController.class);

	@Autowired
	RestTemplateFactory restTemplateFactory;


	@Value("${nice.clientId}")
	private String clientId;

	@Value("${nice.clientSecret}")
	private String clientSecret;


	@Value("${nice.accessToken}")
	private String accessToken;


	@Value("${nice.returnUrl}")
	private String returnUrl;


	@Value("${nice.productId}")
	private String productId;


    /**
     * 기관 토큰 발급 API 예제
     * API_본인확인(통합형)개발가이드.xlsx -> 2.API 규격 탭 -> 2-2 기관토큰(accessToken) 요청
     * */
	/*
    @GetMapping("/api/v1/getAccessToken")
    public String getAccessToken(Model model, ModelMap modelMap) {
        String access_auth = clientId + ":" + clientSecret;
        String ACCESS_BASE64_AUTH = "Basic " + Base64.getEncoder().encodeToString(access_auth.getBytes());
        String data = "grant_type=client_credentials&scope=default";

        try {
            RestTemplate restTemplate =  restTemplateFactory.getRestTemplate();


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", ACCESS_BASE64_AUTH);

            HttpEntity<String> request = new HttpEntity<>(data, headers);

            ResponseEntity<String> ACCESS_TOKEN_RESPONSE = restTemplate.postForEntity(
                    "https://svc.niceapi.co.kr:22001/digital/niceid/oauth/oauth/token",
                    request,
                    String.class
            );

            LOGGER.info("ACCESS_TOKEN_RESPONSE:{}", ACCESS_TOKEN_RESPONSE.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(ACCESS_TOKEN_RESPONSE.getBody(), HashMap.class);

            Map<String, String> dataHeader = (Map<String, String>) responseMap.get("dataHeader");
            if(!(dataHeader.get("GW_RSLT_CD").equals("1200"))) {
                makeGW_RSLT_MSG(dataHeader.get("GW_RSLT_CD"));
            } else {
                Map<String, Object> dataBody = (Map<String, Object>) responseMap.get("dataBody");
                Integer expires_in = (Integer) dataBody.get("expires_in"); // 남은시간 (초)
                String access_token = (String) dataBody.get("access_token");

                model.addAttribute("expires_in", expires_in);
                model.addAttribute("access_token", access_token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "nice/access_token";
    }

*/

    /**
     * 암호화토큰 요청/응답값을 활용한 데이터 생성 및 본인인증 창이 호출되는지 확인하는 예제입니다.
     * (1) API_본인확인(통합형)개발가이드.xlsx -> 2.API 규격 탭 -> 2-3 암호화 토큰(CRYPTO_TOKEN) 요청과
     * (2) API_본인확인(통합형)개발가이드.xlsx -> 3.API 호출 탭 전체 프로세스를 확인해 주셔야 합니다.
     * (3) 예제 코드에서 테스트 시 수정해주셔야 할 부분은 returnurl (콜백받을 URL 주소) 이며,
     *     커스텀 값들은 엑셀파일 3.API 호출 탭의 요청 데이터 부분을 확인해주세요
     * */
    @GetMapping("/api/v1/checkPlusMain")
    public String checkPlusMain(Model model, HttpSession session) {

        // Authorization 헤더를 위한 인증 문자열 생성 및 Base64 인코딩
        Date date = new Date();
        long CURRENT_TIMESTAMP = date.getTime()/1000;
        String crypto_auth = accessToken + ":" + CURRENT_TIMESTAMP + ":" + clientId;
        String CRYPTO_BASE64_AUTH = Base64.getEncoder().encodeToString(crypto_auth.getBytes());

        ObjectMapper objectMapper = new ObjectMapper();

        // 날짜 및 시간 (YYYYMMDDHHiiss 형식)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String req_dtim = sdf.format(new Date());

        StringBuilder stringBuilder = new StringBuilder(13);
        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            int digit = random.nextInt(10); // 0에서 9 사이의 숫자 생성
            stringBuilder.append(digit);
        }
        String randomNumber = stringBuilder.toString();
        String req_no = "REQ" + req_dtim + randomNumber;


        // 요청 body 구성
        ObjectNode requestDataHeader = objectMapper.createObjectNode();
        requestDataHeader.put("CNTY_CD", "ko");
        ObjectNode requestDataBody = objectMapper.createObjectNode();

        requestDataBody.put("req_dtim", req_dtim);
        requestDataBody.put("req_no", req_no);
        requestDataBody.put("enc_mode", "1");

        ObjectNode CRYPTO_DATA = objectMapper.createObjectNode();
        CRYPTO_DATA.set("dataHeader", requestDataHeader);
        CRYPTO_DATA.set("dataBody", requestDataBody);

        try {
        	RestTemplate restTemplate =  restTemplateFactory.getRestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "bearer " + CRYPTO_BASE64_AUTH);
            headers.set("ProductID", productId);

            HttpEntity<ObjectNode> request = new HttpEntity<>(CRYPTO_DATA, headers);

            ResponseEntity<String> CRYPTO_RESPONSE = restTemplate.postForEntity(
                    "https://svc.niceapi.co.kr:22001/digital/niceid/api/v1.0/common/crypto/token",
                    request,
                    String.class
            );

            Map<String, Object> CRYPTO_RESPONSE_DATA = objectMapper.readValue(CRYPTO_RESPONSE.getBody(), HashMap.class);
            Map<String, String> dataHeader = (Map<String, String>) CRYPTO_RESPONSE_DATA.get("dataHeader");

            // 상세응답코드
            // (1) 게이트웨이 결과 코드 확인
            if (!"1200".equals(dataHeader.get("GW_RSLT_CD"))) {
                makeGW_RSLT_MSG(dataHeader.get("GW_RSLT_CD"));
            }

            // (2) 응답 코드 확인
            Map<String, String> dataBody = (Map<String, String>) CRYPTO_RESPONSE_DATA.get("dataBody");
            if (!"P000".equals(dataBody.get("rsp_cd"))) {
                make_res_msg(dataBody.get("rsp_cd"));
            }

            // (3) 최종 응답값 검증
            if (!"0000".equals(dataBody.get("result_cd"))) {
                make_result_msg(dataBody.get("result_cd"));
            }

            // 응답 데이터에서 필요한 값 추출
            String token_version_id = dataBody.get("token_version_id");
            String site_code = dataBody.get("site_code");
            String token_val = dataBody.get("token_val");


            /*
            * 가이드 3. API 호출 > Key 생성 부분 확인
            * 반드시 암호화 토큰 요청 - 응답값을 사용해 주세요
            * 암호화 토큰 요청값(req_dtim, req_no)과  응답값을 조합하여 originalString 생성
            * */

            String originalString = req_dtim.trim() + req_no.trim() + token_val.trim();

            /*
             * 대칭키(key, iv), 무결성키(hmac_key) 생성
             * (1) CRYPTO_RESULT_VALUE : original-string 을 SHA-256 암호화 후 base64 인코딩
             * (2) key, iv, hmac_key 규격에 맞게 substring
             */

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(originalString.getBytes());
            byte[] encodedOriginal = messageDigest.digest();
            String CRYPTO_RESULT_VALUE = Base64.getEncoder().encodeToString(encodedOriginal);

            String key = CRYPTO_RESULT_VALUE.substring(0, 16);
            String iv = CRYPTO_RESULT_VALUE.substring(CRYPTO_RESULT_VALUE.length() - 16);
            String hmac_key = CRYPTO_RESULT_VALUE.substring(0, 32);

            // 복호화 시 필요하므로 저장 (예제에서는 세션 사용)
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("key", key);
            sessionMap.put("iv", iv);
            sessionMap.put("hmac_key", hmac_key);

            session.setAttribute("sessionMap", sessionMap);

            /*
            * 개발가이드 3. API 호출 > 요청데이터(plain_data) 생성
            * 예제에서는 필수값인 sitecode, requestno, returnurl 로 작성했으나
            * 필요하신 경우 가이드를 활용하여 커스텀 해 주세요
            * */
            ObjectNode plain_data = objectMapper.createObjectNode();
            plain_data.put("sitecode", site_code);
            plain_data.put("requestno", req_no);
            /*
             * returnurl : 프로토콜, 도메인 (포트번호 있다면 포트번호) 입력 => 반드시 부모창과 일치하게 설정 부탁드립니다.
             * 예 1) https://회원사도메인/api/v1/checkPlusSuccess
             * 예 2) http://localhost:8080/api/v1/checkPlusSuccess
             * 예 3) 서버 / 클라이언트와 분리되어 있는 상황이라면 클라이언트의 주소(혹은 앱의 schema로 작성 요청)
             *      => methodtype ; "get" 으로 필수 설정, 클라이언트에서 파라미터로 전달 된 결과데이터(암호화 되어 있음) 을 받아
             *                       서버로 복호화 요청하는 프로세스로 진행
             * */
            plain_data.put("returnurl", returnUrl);
            // plain_data.put("methodtype","get");

            // enc_data 생성 => json-string 데이터를 aes 암호화 후 base64 인코딩
            String json_string = plain_data.toString();
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = c.doFinal(json_string.trim().getBytes());
            String enc_data = Base64.getEncoder().encodeToString(encrypted);

            // integrity_value 생성 => enc_data를 hmac 무결성 처리
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec sks = new SecretKeySpec(hmac_key.getBytes(), "HmacSHA256");
            mac.init(sks);
            byte[] hmac256 = mac.doFinal(enc_data.getBytes());
            String integrity_value = Base64.getEncoder().encodeToString(hmac256);

            // 최종적으로 본인인증 시 필요한 값
            model.addAttribute("token_version_id", token_version_id);
            model.addAttribute("enc_data", enc_data);
            model.addAttribute("integrity_value", integrity_value);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return "nice/checkplus_main";
    }

    /**
     * 인증이 완료된 후 전달된 데이터를 복호화 하는 예제입니다.
     * checkPlusMain 에서 지정한 returnurl의 주소로 맵핑하는 작업이 필요합니다.
     * 복호화 작업은 복호화 할 key 확인 > 복호화 할 데이터가 있는지 확인 > 무결성검증 > 복호화 순서로 진행됩니다.
     * 복호화 시 확인할 수 있는 데이터들은 API_본인확인(통합형)개발가이드.xlsx -> 4.API 응답 탭 > 4-3 을 확인해 주세요
     * 일부 데이터가 보이지 않는 것은 계약이 되지 않아서 발생하는 현상이므로,필요하신 경우 계약담당자에게 요청해주세요
     * */

    @GetMapping("/api/v1/checkPlusSuccess")
    public String checkPlusSuccess(@RequestParam String integrity_value,
                                              @RequestParam String token_version_id,
                                              @RequestParam String enc_data,
                                              HttpSession session, Model model) {
        try{

            Map<String, String > sessionMap = (Map<String, String>) session.getAttribute("sessionMap");
            // checkplus_main 에서 저장 된 key, iv, hmac_key 확인 key 없을 시 예외처리
            if(sessionMap == null) {
                throw new RuntimeException("세션에 저장된 key가 없습니다. => 복호화 시 에러 발생");
            }

            String key = sessionMap.get("key") == null ? "" : sessionMap.get("key");
            String iv = sessionMap.get("iv") == null ? "" : sessionMap.get("iv");
            String hmac_key = sessionMap.get("hmac_key") == null ? "" : sessionMap.get("hmac_key");

            LOGGER.info("세션에 저장되어 있는 key : {}, iv : {}, hmac_key : {}", key, iv, hmac_key);

            /*
             * 복호화 할 파라미터가 있는지 확인합니다.
             * 리턴 받은 파라미터명이 동일하여 구분하기 위해 변경하였습니다.
             * 값은 표준창 호출 전(checkPlusMain)에서 생성한 값과 다릅니다.
             * */
            String RETURN_INTEGRITY_VALUE = integrity_value;
            String RETURN_ENC_DATA = enc_data;

            /*
             * 파라미터 없을 시 예외처리
             * 예제에서는 GET 방식으로 처리되었지만, 브라우저정책, 디바이스에 따라 POST 로 전달될 수 있습니다.
             * 항상 GET 방식으로 전달받기 원하시는 경우 checkPlusMain 의 "methodtype", "get" 을 추가하여 테스트 요청드립니다.
             * */

            if(RETURN_ENC_DATA == null || RETURN_ENC_DATA.isEmpty() || RETURN_INTEGRITY_VALUE == null || RETURN_INTEGRITY_VALUE.isEmpty()) {
                throw new RuntimeException("복호화 할 파라미터가 없습니다. => 복호화 시 에러 발생");
            }

            /*
             * 무결성 검증 : 리턴 받은 enc_data 가 변조되었는지 검증합니다.
             * 리턴 받은 enc_data(RETURN_ENC_DATA) 를 checkPlusMain 에서 생성한 hmac_key 로 무결성 처리 => MAKE_INTEGRITY_VALUE 생성
             * 리턴 받은 integrity_value(RETURN_INTEGRITY_VALUE)와 비교합니다.
             * */
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec sks = new SecretKeySpec(hmac_key.getBytes(), "HmacSHA256");
            mac.init(sks);
            byte[] hmac256 = mac.doFinal(RETURN_ENC_DATA.getBytes());
            String MAKE_INTEGRITY_VALUE = Base64.getEncoder().encodeToString(hmac256);

            if(!(RETURN_INTEGRITY_VALUE.equals(MAKE_INTEGRITY_VALUE))) {
                throw new RuntimeException("복호화 중 오류 발생: 무결성 검증 실패. 데이터가 변조되었거나 HmacKey가 일치하지 않습니다.");
            }

            // 복호화 진행
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
            byte[] cipherEnc = Base64.getDecoder().decode(RETURN_ENC_DATA);
            String dec_data = new String(c.doFinal(cipherEnc), "EUC-KR");


            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, String> mapResult = objectMapper.readValue(dec_data, Map.class);
            // utf-8 name URL 디코딩
            String utf8NameEncoded = mapResult.get("utf8_name");
            if (utf8NameEncoded != null) {
                String decodedUtf8Name = URLDecoder.decode(utf8NameEncoded, "UTF-8");
                mapResult.put("utf8_name", decodedUtf8Name); // 디코딩된 값으로 업데이트
            }
            model.addAttribute("mapResult", mapResult);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "nice/checkplus_success";
    }




    /**
     * NICE API 응답의 GW_RSLT_CD 에 따라 상세 오류 메시지를 구성하고 RuntimeException 을 발생시킵니다.
     * 예제이므로, 귀사 비지니스에 따라 수정 가능합니다.
     * @param GW_RSLT_CD NICE API 응답의 GW_RSLT_CD 코드
     * @throws RuntimeException GW_RSLT_CD 에 해당하는 오류 메시지를 포함하는 예외
     */
    public void makeGW_RSLT_MSG(String GW_RSLT_CD) {
        String detail_message;

        switch (GW_RSLT_CD) {
            case "1300":
                detail_message = "request body 가 비었습니다.";
                break;
            case "1400":
                detail_message = "잘못된 요청";
                break;
            case "1401":
                detail_message = "인증 필요";
                break;
            case "1402":
                detail_message = "권한 없음";
                break;
            case "1403":
                detail_message = "서비스 사용 중지됨";
                break;
            case "1404":
                detail_message = "서비스를 찾을 수 없음";
                break;
            case "1500":
                detail_message = "서버 내부 오류 : 데이터 형식 오류입니다. 요청 body 가 dataHeader/dataBody 로 나누어져있는지 확인";
                break;
            case "1501":
                detail_message = "보호된 서비스에서 엑세스가 거부되었습니다.";
                break;
            case "1502":
                detail_message = "보호된 서비스에서 응답이 잘못되었습니다.";
                break;
            case "1503":
                detail_message = "일시적으로 사용할 수 없는 서비스";
                break;
            case "1700":
                detail_message = "엑세스가 허용되지 않습니다 : CLient ID";
                break;
            case "1701":
                detail_message = "엑세스가 허용되지 않습니다 : Service URI";
                break;
            case "1702":
                detail_message = "엑세스가 허용되지 않습니다 : CLient ID + Client_IP";
                break;
            case "1703":
                detail_message = "엑세스가 허용되지 않습니다 : CLient ID + Service URI";
                break;
            case "1705":
                detail_message = "엑세스가 허용되지 않습니다 : CLient ID + Black List Client IP";
                break;
            case "1706":
                detail_message = "엑세스가 허용되지 않습니다 : CLient ID + Product Code";
                break;
            case "1707":
                detail_message = "엑세스가 허용되지 않습니다 : Product Code + Service URI";
                break;
            case "1711":
                detail_message = "거래 제한된 요일입니다.";
                break;
            case "1712":
                detail_message = "거래 제한된 시간입니다.";
                break;
            case "1713":
                detail_message = "거래 제한된 요일/시간입니다.";
                break;
            case "1714":
                detail_message = "거래 제한된 일자입니다.";
                break;
            case "1715":
                detail_message = "거래 제한된 일자/시간입니다.";
                break;
            case "1716":
                detail_message = "공휴일 거래가 제한된 서비스입니다.";
                break;
            case "1717":
                detail_message = "SQL 인젝션, XSS 방어";
                break;
            case "1800":
                detail_message = "잘못된 토큰 => 기관토큰 재발급 후 확인,요청 Header 의 current_time_stamp(UTC) 가 실제 시간과 3분 이상 차이날 시 발생";
                break;
            case "1801":
                detail_message = "잘못된 클라이언트 정보";
                break;
            case "1900":
                detail_message = "초과된 연결횟수";
                break;
            case "1901":
                detail_message = "초과 된 토큰 조회 실패";
                break;
            case "1902":
                detail_message = "초과된 토근 체크 실패";
                break;
            case "1903":
                detail_message = "초과된 접속자 수";
                break;
            case "1904":
                detail_message = "전송 크기 초과";
                break;
            case "1905":
                detail_message = "접속량이 너무 많음";
                break;
            case "1906":
                detail_message = "상품이용 한도초과";
                break;
            case "1907":
                detail_message = "API 이용 주기 초과";
                break;
            default:
                detail_message = "알 수 없는 오류코드(GW_RSLT_CD) : " + GW_RSLT_CD + ", 요청 시각, client_id 를 niceid_support@nice.co.kr 로 문의";
                break;
        }

        String error_message = String.format("GW_RSLT_CD 오류 발생: %s %s , 개발가이드 > FAQ 를 참고해 주세요", GW_RSLT_CD, detail_message);
        System.err.println("NICE_API 에러 : " + error_message);
        throw new RuntimeException(error_message);
    }

    /**
     * 응답 코드(rsp_cd)에 따른 오류 메시지를 생성하고 RuntimeException을 발생시킵니다.
     * 예제이므로, 귀사 비즈니스에 따라 수정 가능합니다.
     * @param rsp_cd 응답 코드
     * @throws RuntimeException rsp_cd 에 해당하는 오류 메시지를 포함하는 예외
     */
    public void make_res_msg(String rsp_cd) {
        String detail_message;

        switch (rsp_cd) {
            case "EAPI2500":
                detail_message = "맵핑정보 없음";
                break;
            case "EAPI2510":
                detail_message = "잘못된 요청";
                break;
            case "EAPI2530":
                detail_message = "응답전문 맵핑 오류";
                break;
            case "EAPI2540":
                detail_message = "대응답 정보 없음";
                break;
            case "EAPI2550":
                detail_message = "숫자타입 입력 오류";
                break;
            case "EAPI2560":
                detail_message = "실수타입 입력 오류";
                break;
            case "EAPI2561":
                detail_message = "실수형 타입 길이정보 문법 에러( 형식 : [전체길이,실수부길이])";
                break;
            case "EAPI2562":
                detail_message = "실수형 타입 논리 에러(전체 길이는 소수부 길이보다 커야합니다.";
                break;
            case "EAPI2563":
                detail_message = "실수형 타입 파싱 에러(입력값을 실수값으로 변환할 수 없습니다.)";
                break;
            case "EAPI2564":
                detail_message = "실수형 타입 정수부 길이 에러";
                break;
            case "EAPI2565":
                detail_message = "실수형 타입 소수부 길이 에러";
                break;
            case "EAPI2600":
                detail_message = "내부 시스템 오류";
                break;
            case "EAPI2700":
                detail_message = "외부 시스템 연동 오류";
                break;
            case "EAPI2701":
                detail_message = "타임아웃 발생";
                break;
            case "EAPI2702":
                detail_message = "DISCONNECTION_OK";
                break;
            case "EAPI2703":
                detail_message = "DISCONNECTION_FAIL";
                break;
            case "EAPI2704":
                detail_message = "RESULT_OK";
                break;
            case "EAPI2705":
                detail_message = "RESULT_FAIL";
                break;
            case "EAPI2892":
                detail_message = "반복부 카운터 에러(지정된 건수보다 크거나 작습니다.";
                break;
            case "EAPI5001":
                detail_message = "schema 검증정보가 없습니다.";
                break;
            case "EAPI5002":
                detail_message = "schema 검증 실패";
                break;
            case "S603":
                detail_message = "내부 DB오류.";
                break;
            case "E998":
                detail_message = "서비스 권한 오류 : 정지된 계약인지 확인.";
                break;
            case "E999":
                detail_message = "내부 시스템 오류 : 사용할 수 있는 API 목록인지 확인.";
                break;
            default:
                detail_message = "알 수 없는 오류코드(rsp_cd) : " + rsp_cd + ", 요청 시각, client_id 를 niceid_support@nice.co.kr 로 문의";
                break;
        }

        String error_message = String.format("rsp_cd 오류 발생: %s %s: FAQ 참고해 주세요", rsp_cd, detail_message);
        System.err.println("NICE_API 에러 : " + error_message);
        throw new RuntimeException(error_message);
    }

    /**
     * result_cd 결과 코드에 따른 오류 메시지를 생성하고 RuntimeException을 발생시킵니다.
     * 예제이므로, 귀사 비즈니스에 따라 수정 가능합니다.
     * @param result_cd 결과 코드
     * @throws RuntimeException result_cd 에 해당하는 오류 메시지를 포함하는 예외
     */
    public void make_result_msg(String result_cd) {
        String detail_message;

        switch (result_cd) {
            case "0001":
                detail_message = "필수 입력값 오류 : 요청한 dataBody 확인, req_dtim, req_no, enc_mode 비어있는 값 없는지 확인, 규격 확인";
                break;
            case "0003":
                detail_message = "OTP 발급 회원사 아님 [계약건 확인이 필요함] => 요청 시각, client_id 를 niceid_support@nice.co.kr 로 문의";
                break;
            case "0099":
                detail_message = "인증 필요";
                break;
            default:
                detail_message = "알 수 없는 오류코드(result_cd) : " + result_cd + ", 요청 시각, client_id 를 niceid_support@nice.co.kr 로 문의";
                break;
        }

        String error_message = String.format("result_cd 오류 발생: %s %s : FAQ 참고해 주세요", result_cd, detail_message);
        System.err.println("NICE_API 에러 : " + error_message);
        throw new RuntimeException(error_message);
    }


}
