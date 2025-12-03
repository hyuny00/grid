<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>NICE 평가정보 JAVA 예제파일 본인인증 표준창 호출</title>
</head>
<body>
<h2>본인인증 창을 호출하기 위한 form 입니다.</h2>
<form name="form_chk" method="post">
    <label for="m">m:
        <input type="text" id="m" name="m" value="service">
    </label><br>
    <label for="token_version_id">token_version_id :
        <input type="text"  id="token_version_id" name="token_version_id" value="${token_version_id}">
    </label><br>
    <label for="enc_data"> enc_data :
        <input type="text" id="enc_data" name="enc_data" value="${enc_data}">
    </label><br>
    <label for="integrity_value"> integrity_value :
        <input type="text" id="integrity_value" name="integrity_value" value="${integrity_value}">
    </label><br>
</form>

<button type="button" id="btn_submit">CheckPlus 안심본인인증 Click</button>

</body>
<script>
    document.getElementById('btn_submit').addEventListener('click', function () {
        window.open('', 'popupChk', 'width=500, height=550, top=100, left=100, fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no');
        document.form_chk.action = "https://nice.checkplus.co.kr/cert/request";
        document.form_chk.target = "popupChk";
        document.form_chk.submit();
    })
</script>
</html>