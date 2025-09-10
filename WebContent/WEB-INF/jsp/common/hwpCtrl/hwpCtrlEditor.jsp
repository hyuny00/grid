<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<c:if test="${serverName eq 'stats.odakorea.go.kr' }">
    <c:set var="hwpctrlUrl" value="https://stats.odakorea.go.kr/webhwpctrl" />
</c:if>
<c:if test="${serverName ne 'stats.odakorea.go.kr' }">
    <c:set var="hwpctrlUrl" value="http://10.47.25.178:33000/webhwpctrl" />
</c:if>

<!-- 한글로폼 -->
<script type="text/javascript" src="${basePath}/js/hangulroForm/HangulroFormManager.min.js?version=1.0.0.30"></script>
<!-- 웹한글 기안기 관련 lib -->
<script type="text/javascript" src="${hwpctrlUrl}/js/hwpctrlapp/utils/util.js"></script>
<script type="text/javascript" src="${hwpctrlUrl}/js/hwpctrlapp/hwpCtrlApp.js"></script>
<script type="text/javascript" src="${hwpctrlUrl}/js/webhwpctrl.js"></script>

<script>
var token;
var HwpCtrl; // 전역 변수로 선언

$(document).ready(function(){
    token = $('input[name="_csrf"]').attr('value');
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            xhr.setRequestHeader("AJAX", true);
            if (!csrfSafeMethod(settings.type) && !this.crossDomain) {
                xhr.setRequestHeader("${_csrf.headerName}", token);
            }
        }
    });

    console.log('Document ready, starting HWP initialization...');

    // 1초 후에 초기화 시작 (스크립트 로딩 시간 확보)
    setTimeout(function() {
        waitForScriptsAndInit();
    }, 1000);
});

function csrfSafeMethod(method) {
    return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
}

// 스크립트가 로드될 때까지 기다리고 초기화
function waitForScriptsAndInit() {
    var maxAttempts = 50; // 최대 5초간 대기 (50 * 100ms)
    var attempts = 0;

    function checkScripts() {
        attempts++;
        console.log('Checking scripts... attempt:', attempts);

        // HangulroFormManager 체크
        if (typeof HangulroFormManager !== 'undefined' &&
            HangulroFormManager.hwp &&
            typeof HangulroFormManager.hwp.BuildWebHwpCtrl === 'function') {

            console.log('All scripts loaded, initializing HWP...');
            initHwpControl();
            return;
        }

        // 아직 로드되지 않았으면 계속 시도
        if (attempts < maxAttempts) {
            setTimeout(checkScripts, 100);
        } else {
            console.error('Scripts failed to load within 5 seconds');
            console.log('HangulroFormManager status:', typeof HangulroFormManager);
            if (typeof HangulroFormManager !== 'undefined') {
                console.log('HangulroFormManager.hwp:', HangulroFormManager.hwp);
            }
            alert('HWP 스크립트 로딩에 실패했습니다.');
        }
    }

    checkScripts();
}

function initHwpControl() {
    try {
        console.log('Starting HWP control initialization...');

        // 전역 변수에 할당
        HwpCtrl = HangulroFormManager.hwp.BuildWebHwpCtrl(
            "hwpctrl",
            "${hwpctrlUrl}/",
            "../../common",
            "jsp",
            function() {
                console.log('HWP Control created successfully');
                try {
                    HangulroFormManager.openDoc(HwpCtrl, "${base64Str}", "HWP", function(res){
                        console.log('HWP Document opened:', res);

                        // HWP 초기화 완료 후 이벤트 바인딩
                        bindSaveButtonEvent();
                    });
                } catch (docError) {
                    console.error('Document open error:', docError);
                }
            }
        );
    } catch (error) {
        console.error('HWP Control initialization error:', error);
        alert('HWP 컨트롤 초기화 중 오류가 발생했습니다: ' + error.message);
    }
}

// 저장 버튼 이벤트 바인딩 함수
function saveHwp() {

      if(!confirm("수정하시겠습니까?")) return;

      if (!HwpCtrl) {
          alert("HWP 컨트롤이 초기화되지 않았습니다.");
          return;
      }

      try {
          HangulroFormManager.saveDoc(HwpCtrl, "HWP", function(data) {

              var str = atob(data);
              if(str.indexOf("<!DOCTYPE html>") != -1) {
                  alert("저장에 실패했습니다.");
                  return;
              }
              if(str.indexOf("error") != -1) {
                  alert("저장에 실패했습니다.");
                  return;
              }

              var saveHwpUrl = "/file/saveHwp";

              const payload = {
            		 fileId:$("#hwpFileId").val(),
		             fileNm :$("#hwpFileNm").val(),
		             temp :$("#hwpTemp").val(),
		             base64Data:data
  				};

              $.ajax({
                  url: saveHwpUrl,
                  type: 'post',
                  contentType: "application/json; charset=UTF-8",
                  data: JSON.stringify(payload),
                  success: function(data) {

                	  console.log(data);

                      if(data == "OK") {
                          alert("저장되었습니다.");
                      } else {
                          alert("저장에 실패했습니다1.");
                      }
                  },
                  error: function(err) {
                      console.error('Document save error:', err);
                      alert("저장 중 오류가 발생했습니다.");
                  }
              });
          });

      } catch (saveError) {
          console.error('Save function error:', saveError);
          alert("저장 기능을 실행할 수 없습니다.");
      }
}
</script>

<style type="text/css">
.loading_progress,
.loading_progress:after {position:fixed; top:50%; left:50%; width:2em; height:2em; border-radius: 50%;}
.loading_progress {
    position:fixed;
    font-size: 10px;
    text-indent: -9999em;
    border-top: 6px solid rgba(76, 115, 213, 0.9);
    border-right: 6px solid rgba(76, 115, 213, 0.9);
    border-bottom: 6px solid rgba(76, 115, 213, 0.9);
    border-left: 6px solid #ffffff;
    -webkit-transform: translateZ(0);
    -ms-transform: translateZ(0);
    transform: translateZ(0);
    -webkit-animation: load8 1.1s infinite linear;
    animation: load8 1.1s infinite linear;
}
@-webkit-keyframes load8 {
    0% {-webkit-transform: rotate(0deg); transform: rotate(0deg);}
    100% {-webkit-transform: rotate(360deg); transform: rotate(360deg);}
}
@keyframes load8 {
    0% {-webkit-transform: rotate(0deg); transform: rotate(0deg);}
    100% {-webkit-transform: rotate(360deg); transform: rotate(360deg);}
}
.btnsize { width:230px;text-align:left }
</style>

<div class="popup" id="pop01">
    <div class="tit">
        <p>한글에디터</p>
        <button type="button" class="btn-close"><span class="sr-only">팝업창 닫기</span></button>
    </div>
    <form name="hwpForm">
       <jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
        <!-- 파일 수정용 -->
        <input type="hidden" id="base64Data" name="base64Data" value="${base64Str}">
        <input type="hidden" id="hwpFileId" name="hwpFileId" value="${hwpFileId}">
        <input type="hidden" id="hwpFileNm" name="hwpFileNm" value="${hwpFileNm}">
        <input type="hidden" id="hwpTemp" name="hwpTemp" value="${hwpTemp}">
    </form>


    <div id="hwpctrl" style="width:100%;height:700px;overflow:hidden;"></div>
    <div class="btn-wrap">
        <button type="button" class="btn close">취소</button>
        <button type="button" class="btn navy" onclick="saveHwp();">문서저장</button>
    </div>
</div>