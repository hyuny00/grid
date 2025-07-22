<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf"%>

<script>
    function logout() {
        document.logoutForm.submit();
    }

    function updateUserForm() {
        document.userDetailForm.action="/admin/user/detailUser";
        document.userDetailForm.submit();
    }
</script>


<div id="side" class="side">
    <div class="side_menu">
        <h2>ODA 사업 관리</h2>
        <ul class="gnb">
            <li>
                <a href="javascript:;">사업목록</a>
            </li>
            <li class="hasChild active">
                <a href="javascript:;">연수 사업 관리</a>
                <ul>
                    <li><a href="javascript:;" class="on">연수 사업 목록</a></li>
                    <li><a href="javascript:;">연수 프로그램 관리</a></li>
                    <li><a href="javascript:;">연수생 관리</a></li>
                    <li><a href="javascript:;">체크리스트</a></li>
                </ul>
            </li>
            <li class="hasChild">
                <a href="javascript:;">장학 사업 관리</a>
                <ul>
                    <li><a href="javascript:;">연수 사업 목록</a></li>
                    <li><a href="javascript:;">연수 프로그램 관리</a></li>
                    <li><a href="javascript:;">연수생 관리</a></li>
                    <li><a href="javascript:;">체크리스트</a></li>
                </ul>
            </li>
            <li class="hasChild">
                <a href="javascript:;">패키지 사업 관리</a>
                <ul>
                    <li><a href="javascript:;">연수 사업 목록</a></li>
                    <li><a href="javascript:;">연수 프로그램 관리</a></li>
                    <li><a href="javascript:;">연수생 관리</a></li>
                    <li><a href="javascript:;">체크리스트</a></li>
                </ul>
            </li>
        </ul>
    </div>
    <button class="btnSlide">slide</button>
</div>



<script>

</script>