<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>로그인화면</title>
<link rel="stylesheet" href="../css/reset.css">
<link rel="stylesheet" href="../css/style.css">
<script src="../js/jquery-3.3.1.min.js"></script>
</head>
<body>
<div id="wrap">
    <form id="" name="" action="" method="post">
        <div class="login-wrap">
            <div class="id-pw-wrap">
                <div class="logo"><span><img src="../img/logo.png" alt="로고">ODA통합정보포털</span></div>
                <div class="id-pw-list-wrap">
                    <div class="id-pw-list">
                        <span class="icon"><img src="../img/ico_login_id.svg" alt=""></span>
                        <input type="text" class="input" placeholder="아이디" title="아이디를 입력하세요">
                    </div>
                    <div class="id-pw-list">
                        <span class="icon"><img src="../img/ico_login_pw.svg" alt=""></span>
                        <input type="password" class="input" placeholder="비밀번호" title="비밀번호를 입력하세요">
                    </div>
                </div>
                <div class="login-keep">
                    <input type="checkbox" class="check" id="loginKeep">
                    <label for="loginKeep" class="label">로그인 상태 유지</label>
                </div>
                <div class="error-message">
                    <p>아이디 또는 비밀번호가 잘못 입력 되었습니다. <br> <strong>아이디</strong>와 <strong>비밀번호</strong>를 정확히 입력해 주세요.</p>
                </div>
                <div class="btn-login-wrap">
                    <a href="#" class="btn-login">로그인</a>
                    <a href="#" class="btn-gpki">GPKI 인증서 로그인</a>
                </div>
                <div class="btn-etc">
                    <a href="#" title="사용자등록">사용자등록</a>
                    <a href="#" title="GPKI 모듈 수동설치">GPKI 모듈 수동설치</a>
                </div>
                <span class="question">☎ 사용자 관련 문의 02-730-2476</span>
            </div>
        </div>
    </form>
</div>
</body>
</html>