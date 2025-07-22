

//향상된 페이지네이션 렌더링
function renderPagination() {
 const pagination = $("#pagination");
 pagination.empty();
 
 if (totalPages <= 1) {
     return; // 페이지가 1개 이하면 페이지네이션 숨김
 }
 
 // 처음 버튼
 var firstDisabled = currentPage === 1 ? "disabled" : "";
 pagination.append(
     '<li class="page-item ' + firstDisabled + '">' +
     '<a class="page-link" href="#" onclick="goFirstPage(); return false;">&laquo;&laquo;</a>' +
     '</li>'
 );
 
 // 이전 버튼
 var prevDisabled = currentPage === 1 ? "disabled" : "";
 pagination.append(
     '<li class="page-item ' + prevDisabled + '">' +
     '<a class="page-link" href="#" onclick="goPrevPage(); return false;">&laquo;</a>' +
     '</li>'
 );
 
 // 페이지 번호들 (현재 페이지 기준으로 앞뒤 2개씩 표시)
 var startPage = Math.max(1, currentPage - 2);
 var endPage = Math.min(totalPages, currentPage + 2);
 
 // 시작 페이지가 1보다 크면 1과 ... 표시
 if (startPage > 1) {
     pagination.append(
         '<li class="page-item">' +
         '<a class="page-link" href="#" onclick="goPage(1); return false;">1</a>' +
         '</li>'
     );
     if (startPage > 2) {
         pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
     }
 }
 
 // 페이지 번호들
 for (var i = startPage; i <= endPage; i++) {
     var activeClass = i === currentPage ? "active" : "";
     pagination.append(
         '<li class="page-item ' + activeClass + '">' +
         '<a class="page-link" href="#" onclick="goPage(' + i + '); return false;">' + i + '</a>' +
         '</li>'
     );
 }
 
 // 끝 페이지가 전체 페이지보다 작으면 ... 과 마지막 페이지 표시
 if (endPage < totalPages) {
     if (endPage < totalPages - 1) {
         pagination.append('<li class="page-item disabled"><span class="page-link">...</span></li>');
     }
     pagination.append(
         '<li class="page-item">' +
         '<a class="page-link" href="#" onclick="goPage(' + totalPages + '); return false;">' + totalPages + '</a>' +
         '</li>'
     );
 }
 
 // 다음 버튼
 var nextDisabled = currentPage === totalPages ? "disabled" : "";
 pagination.append(
     '<li class="page-item ' + nextDisabled + '">' +
     '<a class="page-link" href="#" onclick="goNextPage(); return false;">&raquo;</a>' +
     '</li>'
 );
 
 // 마지막 버튼
 var lastDisabled = currentPage === totalPages ? "disabled" : "";
 pagination.append(
     '<li class="page-item ' + lastDisabled + '">' +
     '<a class="page-link" href="#" onclick="goLastPage(); return false;">&raquo;&raquo;</a>' +
     '</li>'
 );
 
 // 페이지 정보 표시
 updatePageInfo();
}

//페이지 정보 업데이트
function updatePageInfo() {
 var startRecord = (currentPage - 1) * pageSize + 1;
 var endRecord = Math.min(currentPage * pageSize, totalCount);
 
 // 페이지 정보를 표시할 영역이 있다면
 var pageInfo = "총 " + totalCount + "건 중 " + startRecord + "-" + endRecord + "건 (페이지 " + currentPage + "/" + totalPages + ")";
 
 // 기존 페이지 정보 영역이 있으면 업데이트, 없으면 생성
 if ($("#page-info").length === 0) {
     $("#pagination").after('<div id="page-info" class="text-center mt-2 text-muted small">' + pageInfo + '</div>');
 } else {
     $("#page-info").text(pageInfo);
 }
}

//페이지 이동 함수들
function goPage(page) {
 if (page < 1 || page > totalPages || page === currentPage) return;
 fetchData(page);
}

function goFirstPage() {
 if (currentPage > 1) {
     fetchData(1);
 }
}

function goPrevPage() {
 if (currentPage > 1) {
     fetchData(currentPage - 1);
 }
}

function goNextPage() {
 if (currentPage < totalPages) {
     fetchData(currentPage + 1);
 }
}

function goLastPage() {
 if (currentPage < totalPages) {
     fetchData(totalPages);
 }
}

function renderTemplate(templateId, data) {
    let template = document.getElementById(templateId).innerHTML;
    return template.replace(/{{(.*?)}}/g, (_, key) => {
        return data[key.trim()] ?? '';
    });
}



