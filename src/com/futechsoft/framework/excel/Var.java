/*
 *------------------------------------------------------------------------------
 *
 * PROJ : 중소기업규제정보시스템
 * Copyright 2009 FUTECHSOFT All rights reserved
 *------------------------------------------------------------------------------
 *                  변         경         사         항
 *------------------------------------------------------------------------------
 *   DATE       AUTHOR                      DESCRIPTION
 * -----------  ------  ---------------------------------------------------------
 * 2009. 03.
 *----------------------------------------------------------------------------
 */


package com.futechsoft.framework.excel;

/**
 * Variable Define
 *
 * @author $Author: pjh $
 * @version $Revision: 1.28 $
 */
public class Var {


	//데이터유형
	public static final String hz_data_type_upload		= "01";  //업로드
	public static final String hz_data_type_download	= "02";  //다운로드

	//실행결과
	public static final String hz_act_succ		= "01";  //성공
	public static final String hz_act_fail		= "02";  //실패

	//분류유형
	public static final String catg_type_base = "00000";	//root
	public static final String catg_type_biz = "00001";	//중소기업연구원
	public static final String catg_type_homin = "00002";	//옴브즈만

	//설문관리 코드
	public static final String dz_qa_main		= "DZ001000";  //설문주최

	//설문유형
	public static final String dz_data_type_biz		= "01";  //설문
	public static final String dz_data_type_homin	= "02";  //투표

	//공통코드
	public static final String cz_sch_org_sb	= "CZ012000";  //발굴기관코드
	public static final String cz_req_type		= "CZ013000";  //요청상태코드
	public static final String cz_prop_type		= "CZ017000";  //건의유형
	public static final String cz_conf_type		= "CZ022000";  //검토유형코드
	public static final String cz_impmt_type	= "CZ018000";  //개선방안유형코드
	public static final String cz_expct_effct	= "CZ019000";  //기대효과코드
	public static final String cz_expct_effct_div	= "CZ028000";  //기대효과구분코드
	public static final String cz_rel_biz_cd	= "CZ036000";  //업종분류
	public static final String cz_sch_keycode	= "CZ037000";  //검색어 코드

	public static final String cz_biz_con_cd	= "OZ020000";  //업태코드

	public static final String cz_catg_cd2		= "CZ023000";  //분류코드2
	public static final String cz_catg_cd3		= "CZ024000";  //분류코드3

	public static final String cz_rqst_div			= "CZ034000";  //요청구분
	public static final String cz_rqst_deal_stats	= "CZ035000";  //요청처리상태

	public static final String cz_consult_type	= "CZ039000";  //협의방식

	public static final String cz_mail_type	= "CZ044000";  //수발신구분

	public static final String cz_send_type	= "CZ040000";  //송수신유형

	public static final String cz_file_type	= "CZ045000";  //파일구분

	//분할구분코드
	public static final String cz_part_div_orgn		= "01";  //원본
	public static final String cz_part_div_part		= "02";  //분할

	//보고서유형코드
	public static final String cz_report_type	= "CZ047000";

	//처리상태코드
	public static final String deal_stats_off_temp			= "21";  //오프라인접수 임시저장
	public static final String deal_stats_temp				= "22";  //규제애로등록 임시저장
	public static final String deal_stats_rcpt_wait			= "02";  //접수대기
	public static final String deal_stats_conf_wait			= "03";  //검토대기
	public static final String deal_stats_conf_ing			= "04";  //검토중
	public static final String deal_stats_deal_wait			= "05";  //처리대기
	public static final String deal_stats_deal_ing			= "06";  //처리중
	public static final String deal_stats_deal_ing_in		= "32";  //처리중(장기과제)->정책건의로수정
	public static final String deal_stats_deal_ing_out		= "31";  //처리중(부처 협의중)
	public static final String deal_stats_conf_first		= "33";  //처리중(1차협의)
	public static final String deal_stats_conf_recom		= "34";  //처리중(개선권고)
	public static final String deal_stats_comp				= "07";  //완료
	public static final String deal_stats_comp_acpt			= "41";  //완료(수용)
	public static final String deal_stats_comp_dont_acpt	= "42";  //완료(수용불가)
	public static final String deal_stats_comp_some_acpt	= "43";  //완료(부분수용)
	public static final String deal_stats_comp_guide		= "44";  //완료(단순안내)
	public static final String deal_stats_comp_long_conf	= "45";  //완료(장기검토)
	public static final String deal_stats_comp_move			= "46";  //완료(이첩)
	public static final String deal_stats_comp_cancel		= "47";  //완료(철회)
	public static final String deal_stats_comp_rule_prop    = "48";  //완료(정책건의)

	public static final String deal_stats_recall    		= "97";  //회수
	public static final String deal_stats_return    		= "98";  //반송
	public static final String deal_stats_del				= "99";  //삭제

	//협업기관
	public static final String help_org_01				= "701";  //경기도 SOS
	public static final String help_org_02				= "702";  //충남 SOS
	public static final String help_org_03				= "703";  //한국산업단지공단
	public static final String help_org_04				= "704";  //G4B
	public static final String help_org_05				= "705";  //소상공인 진흥원

	//처리이력코드
	public static final String deal_hstr_off_temp				= "00";  //오프라인접수 임시저장
	public static final String deal_hstr_temp					= "01";  //규제애로등록 임시저장
	public static final String deal_hstr_prop					= "02";  //등록
	public static final String deal_hstr_rcpt					= "03";  //접수
	public static final String deal_hstr_conf_ing				= "04";  //검토중
	public static final String deal_hstr_conf_comp				= "05";  //검토완료
	public static final String deal_hstr_deal_ing				= "06";  //처리중
	public static final String deal_hstr_deal_comp				= "07";  //처리완료
	public static final String deal_hstr_admin_modify			= "08";  //관리자수정
	public static final String deal_hstr_rqst_chrgp_chng		= "09";  //담당자 변경요청
	public static final String deal_hstr_rqst_ext_limit 		= "10";  //기한연장요청
	public static final String deal_hstr_rqst_chrgp_chng_appr 	= "11";  //담당자 변경요청승인
	public static final String deal_hstr_rqst_ext_limit_appr 	= "12";  //기한연장요청승인
	public static final String deal_hstr_rqst_chrgp_chng_reject	= "13";  //담당자 변경요청반려
	public static final String deal_hstr_rqst_ext_limit_reject 	= "14";  //기한연장요청반려
	public static final String deal_hstr_chrgp_chng 			= "15";  //담당자 변경
	public static final String deal_hstr_recall    				= "97";  //회수
	public static final String deal_hstr_return    				= "98";  //반송
	public static final String deal_hstr_del					= "99";  //삭제

	//접수유형
	public static final String recpt_type_out		= "01";  //외부
	public static final String recpt_type_in		= "02";  //내부


	//운영관리자 업무권한코드
	public static final String administrator		= "HZ001000";   //최상위 권한코드

	//민원관리 담당자 코드
	public static final String chrg_div_cd = "CZ015000"; //담당자 구분 코드
	public static final String chrg_div_schp  = "01" ; //발굴자
	public static final String chrg_div_confp = "02"; //분석자
	public static final String chrg_div_dealp = "03"; //해소자

	//민원관리 담당자 명
	public static final String chrg_div_schp_nm  = "발굴자"; //발굴자
	public static final String chrg_div_confp_nm = "분석자"; //분석자
	public static final String chrg_div_dealp_nm = "해소자"; //해소자

	//기본처리기한일
	public static final int base_deal_limit_days = 7; //기본 처리기한일

	//권한코드
	public static final String auth_super_admin = "HZ001000"; //운영관리자
	public static final String auth_regbn_admin = "CZ003000"; //규제애로관리자
	public static final String auth_regbn_user 	= "CZ002000"; //규제애로사용자
	public static final String auth_apprp 	= "CZ001000"; //요청승인권자

	//요청구분코드
	public static final String rqst_div_chrgp_chng = "01"; //담당자 변경요청
	public static final String rqst_div_ext_limit_ymd = "02"; //기한연장요청


	//승인결과코드
	public static final String appr_rslt_appr = "01"; //승인
	public static final String appr_rslt_reject = "02"; //반려

	//호민관시스템 상태코드
	public static final String homin_sys_stats_appl_rcpt= "10"; //신청접수
	public static final String homin_sys_stats_conf 	= "30"; //검토
	public static final String homin_sys_stats_send 	= "40"; //발송
	public static final String homin_sys_stats_del 	 	= "98"; //삭제
	public static final String homin_sys_stats_comp 	= "99"; //완료

	//호민관시스템 답변상태 코드
	public static final String res_status_acpt 	 			= "A"; //수용
	public static final String res_status_long_conf 		= "B"; //장기검토
	public static final String res_status_dont_acpt 		= "C"; //수용불가
	public static final String res_status_dont_some_acpt 	= "D"; //부분수용
	public static final String res_status_dont_guide 		= "E"; //단순안내
	public static final String res_status_move 				= "F"; //이첩
	public static final String res_status_cancel 			= "G"; //철회
	public static final String res_status_rule_prop			= "H"; //정책건의

	//중기포털, 호민관 어드민 id
	public static final String admin_id_homin 	 = "homin1"; 		//호민관 Admin id
	public static final String admin_id_portal 	 = "refinfo"; 	//중기포털 Admin id


	//발굴기관코드
	public static final String sch_org_sbris 	 = "01"; 		//중소기업포털
	public static final String sch_org_homin 	 = "02"; 		//호민관

	//규제애로등록 공통코드
	public static final String rgst_sys_div_cd 	 	 = "CZ038045"; 		//시스템코드
	public static final String rgst_problem_type_cd  = "CZ038001"; 		//문제유형코드
	public static final String rgst_target_field_cd  = "CZ038006"; 		//대상분야코드
	public static final String rgst_catg_cd1 	 	 = "CZ038021"; 		//대분류
	public static final String rgst_catg_cd2 	 	 = "CZ038028"; 		//소분류
	public static final String rgst_deal_stats_cd 	 = "CZ038013"; 		//처리상태코드
	public static final String rgst_sr_type_cd 	 	 = "CZ040000"; 		//송수신유형코드
	public static final String rgst_deal_case_type_cd 	 = "CZ038052"; 	//처리사례유형코드








	//규제등록관리 코드
	public static final String bz_ref_prgss_state	  = "BZ001000";  //규제진행상태
	public static final String bz_rgst_hstr_div       = "BZ002000";  //등록이력구분
	public static final String bz_deal_cd             = "BZ004000";  //처리코드
	public static final String bz_rgst_chng_resn_cd   = "BZ005000";  //변경사유코드
	public static final String bz_catg_stnd           = "BZ006000";  //분류기준
	public static final String bz_deal_org_cd         = "BZ007000";  //처리기관코드
	public static final String bz_deprt_lrgcatg       = "BZ008000";  //부문대분류
	public static final String bz_deprt_mdmcatg       = "BZ009000";  //부문중분류
	public static final String bz_type_catg           = "BZ010000";  //유형분류
	public static final String bz_char_lrgcatg        = "BZ011000";  //성격별대분류
	public static final String bz_char_mdmcatg        = "BZ012000";  //성격별중분류
	public static final String bz_enact_type          = "BZ013000";  //입법유형
	public static final String bz_law_basis           = "BZ014000";  //법적근거
	public static final String bz_enact_div           = "BZ015000";  //입법구분
	public static final String bz_ref_type           	= "BZ017000";  //규제구분
	public static final String bz_law_state          	= "BZ016000";  //법령상태
	public static final String bz_contn_limit_yn     	= "BZ023000";  //존속기한설정여부

	//규제등록권한 코드
	public static final String bz_auth1		= "BZ001000";  //규제등록관리자
	public static final String bz_auth2		= "BZ002000";  //규제부처담당관(규제개혁실)
	public static final String bz_auth3		= "BZ003000";  //규제법무담당자
	public static final String bz_auth4		= "BZ004000";  //규제부처실무자

	//뉴스레터
	public static final String cz_news_letter_cnts = "CZ042000"; //뉴스레터 컨텐츠

	//과제 요청 메세지
	public static final String cz_msg_regist_exam		= "과제등록을 검토해주십시오.";
	public static final String cz_msg_regist_req		= "과제등록을 승인해주십시오.";
	public static final String cz_msg_date_exten		= "과제의 추진기간을 연장하여 주십시오.";
	public static final String cz_msg_complet_req		= "과제추진이 완료되었으므로 종결처리해 주십시오.";
	public static final String cz_msg_trans_req		= "과제의 이관을 승인해주십시오.";
	public static final String cz_msg_date_exten_req	= "완료예정일이 지나 기한초과되었습니다. 추진기간 연장을 요청하세요.";


	//시스템도메인
	public static final String system_domain		= "gyujis.go.kr";  //시스템 도메인
	/**
	 *
	 *
	 */
	public Var() {
		super();
	}

}