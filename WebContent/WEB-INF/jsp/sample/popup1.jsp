
<div class="popup" id="pop01">
    <div class="tit">
        <p>시행계획 조회</p>
        <button type="button" class="btn-close"><span class="sr-only">팝업창 닫기</span></button>
    </div>
    
    <div class="sch">
        <div>
            <p>소관부처</p>
            <div class="selectBox">
                <select name="" id="select02">
                    <option value="">- 선택 -</option>
                </select>
            </div>
        </div>
        
        <div class="flex1">
            <p>사업명</p>
            <input type="text" class="w100">
            <div class="btn-wrap">
                <button class="btn-reset"><span class="sr-only">초기화버튼</span></button>
                <button type="button" class="btn-sch">조회</button>
            </div>
        </div>
    </div>
    
    <div class="cont">
        <div class="schList">
            <div class="tblBox">
                <table class="tbl col">
                    <caption></caption>
                    <colgroup>
                        <col style="width: 5%;">
                        <col style="width: 12%;">
                        <col style="width: 35%;">
                        <col style="width: 15%;">
                        <col style="width: 20%;">
                        <col style="width: 13%;">
                    </colgroup>
                    <thead>
                        <tr>
                            <th scope="col">
                                <div class="tblChk">
                                    <input type="checkbox" id="chk00"><label for="chk00"></label>
                                </div>
                            </th>
                            <th scope="col">사업번호</th>
                            <th scope="col">사업명</th>
                            <th scope="col">소관부처</th>
                            <th scope="col">국가</span></th>
                            <th scope="col">사업분야</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="tC">
                                <div class="tblChk">
                                    <input type="checkbox" id="chk01"><label for="chk01"></label>
                                </div>
                            </td>
                            <td class="tC">100511</td>
                            <td>
                                <a href="#">아태지역 개도국 OECD 선진 경쟁정책 및 모험사례 교육 연수</a>
                            </td>
                            <td class="tC"><a href="#">국무조정실</a></td>
                            <td class="tC">인도네시아</td>
                            <td class="tC">행정비용</td>
                        </tr>
                        <!-- 추가 행들... -->
                    </tbody>
                </table>
            </div>

            <!-- pagination -->
            <div class="pagination">
                <button type="button" class="page-navi prev" disabled=""><span class="sr-only">이전</span></button>
                <div class="page-links">
                    <a class="page-link active" href="#"><span class="sr-only">현재페이지</span>1</a>
                    <a class="page-link" href="#">2</a>
                    <a class="page-link" href="#">3</a>
                    <a class="page-link" href="#">4</a>
                    <a class="page-link" href="#">5</a>
                </div>
                <button type="button" class="page-navi next"><span class="sr-2020">다음</span></button>

                <p>총 949건 (1/95)</p>
                <select name="" id="select01">
                    <option value="">5</option>
                </select>
            </div>
            <!-- //pagination -->
        </div>
    </div>
    
    <div class="btn-wrap">
        <button type="button" class="btn close">취소</button>
        <button type="button" class="btn navy">선택</button>
    </div>
</div>
