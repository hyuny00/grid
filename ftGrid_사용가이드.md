# ftGrid.js 사용 가이드

## 목차
1. [개요](#개요)
2. [시작하기](#시작하기)
3. [초기화 옵션](#초기화-옵션)
4. [기본 사용법](#기본-사용법)
5. [데이터 조작](#데이터-조작)
6. [정렬 (Sort)](#정렬-sort)
7. [컬럼 리사이저 (Column Resizer)](#컬럼-리사이저-column-resizer)
8. [템플릿 문법](#템플릿-문법)
9. [포맷팅 헬퍼](#포맷팅-헬퍼)
10. [이벤트 콜백](#이벤트-콜백)
11. [트리 그리드](#트리-그리드)
12. [엑셀 기능](#엑셀-기능)
13. [API 레퍼런스](#api-레퍼런스)

---

## 개요

ftGrid.js는 jQuery 기반의 강력한 그리드 컴포넌트입니다. 다음과 같은 기능을 제공합니다:

- 페이징 처리
- 트리 구조 지원 (계층형 데이터)
- CRUD 작업 (추가, 수정, 삭제, 저장)
- 정렬 기능
- 체크박스 선택 (개수 제한 가능)
- 엑셀 데이터 업로드 및 유효성 검사
- 셀 병합
- 다양한 포맷팅 헬퍼 (날짜, 금액, 전화번호 등)
- 컨텍스트 메뉴

---

## 시작하기

### 기본 초기화

```javascript
const gridInstance = initTreeGrid({
    gridId: 'grid1',
    searchFormId: 'searchForm1',
    templateId: 'node-row-template-1',
    urls: {
        mainUrl: '/api/data/list'
    }
});
```

### HTML 구조

```html
<!-- 검색 폼 -->
<form id="searchForm1">
    <input type="text" name="keyword" />
    <button type="button" class="btn-search">검색</button>
    <button type="button" class="btn-reset">초기화</button>
</form>

<!-- 그리드 컨테이너 -->
<div id="grid1-container">
    <table id="grid1">
        <thead>
            <tr>
                <th><input type="checkbox" class="check-all" /></th>
                <th class="sortable" data-sort="name">이름</th>
                <th class="sortable" data-sort="date">날짜</th>
            </tr>
        </thead>
        <tbody id="grid1-body"></tbody>
    </table>
    <div id="grid1-pagination"></div>

    <!-- 액션 버튼 -->
    <button class="btn-add-row">행 추가</button>
    <button class="btn-delete">삭제</button>
    <button class="btn-save">저장</button>
</div>

<!-- 행 템플릿 -->
<script type="text/template" id="node-row-template-1">
    <tr class="{{displayClass}}">
        <td><input type="checkbox" class="row-check tblChk" /></td>
        <td><input type="text" data-field="name" data-value="{{name}}" /></td>
        <td>{{format regDate "date"}}</td>
    </tr>
</script>
```

---

## 초기화 옵션

### 필수 옵션

| 옵션 | 타입 | 설명 |
|------|------|------|
| `gridId` | String | 그리드 테이블의 ID |
| `templateId` | String | 행 템플릿의 ID (renderRowFunction 사용시 생략 가능) |
| `urls.mainUrl` | String | 데이터 조회 URL |

### 선택 옵션

| 옵션 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| `searchFormId` | String | - | 검색 폼 ID |
| `pageSize` | Number | 10 | 페이지당 표시 건수 |
| `isLoading` | String | 'Y' | 자동 로딩 여부 ('Y' 또는 'N') |
| `addRowPosition` | String | 'top' | 행 추가 위치 ('top', 'bottom', 'above', 'below') |
| `addChildPosition` | String | 'bottom' | 자식행 추가 위치 ('top', 'bottom') |
| `urlParamKey` | String | - | 목록의 고유 키 필드명 |
| `checkCount` | Number | - | 체크박스 최대 선택 개수 |
| `defaultFields` | Object | {} | 새 행 추가시 기본값 |
| `gridData` | Object | {} | 초기 로딩시 추가 조건 |
| `codeMap` | Object | {} | 코드 매핑 데이터 |
| `selectOption` | Object | {} | select 옵션 데이터 |
| `effectCheckBox` | String | 'Y' | 체크박스 수정 추적 여부 |
| `sortNoSearch` | Boolean | false | 정렬시 재조회 없이 현재 데이터 정렬 |
| `enableSelect2` | Boolean | false | Select2 적용 여부 |

### URL 옵션

```javascript
urls: {
    mainUrl: '/api/data/list',        // 필수: 데이터 조회 URL
    childrenUrl: '/api/data/children/{no}', // 선택: 트리 자식 로딩 URL
    saveUrl: '/api/data/save',        // 선택: 저장 URL
    deleteUrl: '/api/data/delete'     // 선택: 삭제 URL (없으면 저장시 함께 처리)
}
```

### 전체 옵션 예시

```javascript
const gridInstance = initTreeGrid({
    gridId: 'grid1',
    searchFormId: 'searchForm1',
    templateId: 'node-row-template-1',

    // URL 설정
    urls: {
        mainUrl: '/sample/grid/parent',
        childrenUrl: '/sample/grid/children/{no}',
        saveUrl: '/sample/grid/save',
        deleteUrl: '/sample/grid/delete'
    },

    // 페이징
    pageSize: 10,

    // 트리 설정
    treeLoadMode: 'lazy',  // 'full' 또는 'lazy'
    urlParamKey: 'no',
    urlParentParamKey: 'parentNo',

    // 행 추가 위치
    addRowPosition: 'top',
    addChildPosition: 'bottom',

    // 체크박스
    checkCount: 5,
    effectCheckBox: 'N',

    // 기본값
    defaultFields: {
        name: '',
        status: 'active'
    },

    // 초기 데이터
    gridData: { category: 'A' },

    // 자동 로딩
    isLoading: 'Y',

    // 셀 병합
    mergeCells: {
        category: true,
        group: true
    },

    // 이벤트 콜백
    onRowClick: function(rowData, $row, event) {
        console.log('클릭된 행:', rowData);
    },
    onRowDoubleClick: function(rowData, $row) {
        console.log('더블클릭된 행:', rowData);
    },
    onInitComplete: function() {
        console.log('그리드 초기화 완료');
    }
});
```

---

## 기본 사용법

### 데이터 조회

```javascript
// 검색 실행 (페이지 1로 이동)
gridInstance.searchData();

// 특정 페이지로 이동
gridInstance.searchData(3);

// 데이터 새로고침 (현재 페이지 유지)
gridInstance.fetchData(gridInstance.currentPage);
```

### 검색 조건 설정

```javascript
// 추가 검색 조건 설정
gridInstance.setGridData({ category: 'B', status: 'active' });

// 검색 실행
gridInstance.searchData();
```

### 외부 데이터 설정

```javascript
// 서버 응답 형식
const responseData = {
    total: 100,
    data: [
        { id: 1, name: '항목1', childYn: 'Y' },
        { id: 2, name: '항목2', childYn: 'N' }
    ]
};

// 데이터 설정
gridInstance.setData(responseData, 1);  // 두 번째 파라미터는 현재 페이지
```

### 저장

```javascript
// 내장 저장 기능 사용 (saveUrl 설정 필요)
gridInstance.saveChanges();

// 또는 수동으로 데이터 가져와서 저장
const modifiedRows = gridInstance.getModifiedRows();
const deletedRows = gridInstance.getDeletedRows();

$.ajax({
    url: '/api/save',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
        updatedNodes: modifiedRows,
        deletedNodes: deletedRows
    }),
    success: function(res) {
        gridInstance.modifiedRows.clear();
        gridInstance.fetchData(gridInstance.currentPage);
    }
});
```

---

## 데이터 조작

### 행 추가

```javascript
// 맨 위에 행 추가
gridInstance.addRow('top');

// 맨 아래에 행 추가
gridInstance.addRow('bottom');

// 선택된 행 위에 추가
gridInstance.addRow('above');

// 선택된 행 아래에 추가
gridInstance.addRow('below');
```

### 자식 행 추가 (트리 모드)

```javascript
// 선택된 행의 자식으로 추가
gridInstance.addChildToSelected();
```

### 행 삭제

```javascript
// 체크된 행 삭제 (deleteUrl 없으면 화면에서만 삭제, 저장시 서버 전송)
gridInstance.deleteSelected();

// deleteUrl 없이 화면에서만 삭제
gridInstance.deleteSelected2();
```

### 행 데이터 조회

```javascript
// ID로 행 데이터 조회
const rowData = gridInstance.getRowData('row-id-123');

// 인덱스로 행 데이터 조회 (0부터 시작)
const firstRowData = gridInstance.getRowDataByIndex(0);

// 전체 데이터 조회
const allData = gridInstance.getAllData();

// 현재 페이지 데이터 조회
const currentData = gridInstance.getData();

// 데이터 건수
const count = gridInstance.getCount();
```

### 행 데이터 수정

```javascript
// 여러 필드 한번에 수정
gridInstance.updateRowData('row-id-123', {
    name: '새로운 이름',
    email: 'new@email.com',
    status: 'active'
});

// 단일 필드 수정
gridInstance.updateRowField('row-id-123', 'name', '수정된 이름');

// 인덱스로 수정
gridInstance.updateRowDataByIndex(0, { name: '첫 번째 행' });
gridInstance.updateRowFieldByIndex(1, 'status', 'inactive');
```

### 체크박스 관련

```javascript
// 체크된 행 개수
const checkedCount = gridInstance.getCheckedCount();

// 체크된 행 전체 데이터
const checkedRows = gridInstance.getCheckedRowsData();

// 체크된 행의 특정 필드만 가져오기
const checkedIds = gridInstance.getCheckedRowsField('id');
const checkedNames = gridInstance.getCheckedRowsField('name');

// 체크된 행의 ID 목록
const checkedRowIds = gridInstance.getCheckedRowIds();

// 전체 선택/해제
gridInstance.toggleAll(true);   // 전체 선택
gridInstance.toggleAll(false);  // 전체 해제

// 체크박스 활성화/비활성화
gridInstance.setCheckboxEnabled(true);   // 활성화
gridInstance.setCheckboxEnabled(false);  // 비활성화
```

### 인덱스와 ID 변환

```javascript
// 인덱스로 nodeId 가져오기
const nodeId = gridInstance.getNodeIdByIndex(2);

// nodeId로 인덱스 가져오기
const index = gridInstance.getIndexByNodeId('some-node-id');
```

---

## 정렬 (Sort)

그리드 컬럼 헤더를 클릭하여 데이터를 정렬할 수 있습니다.

### 정렬 가능한 컬럼 설정

테이블 헤더(`<th>`)에 `sortable` 클래스와 `data-sort` 속성을 추가합니다.

```html
<table id="grid1">
    <thead>
        <tr>
            <th><input type="checkbox" class="check-all" /></th>
            <th class="sortable" data-sort="name">이름 <span class="sort-icon"></span></th>
            <th class="sortable" data-sort="regDate">등록일 <span class="sort-icon"></span></th>
            <th class="sortable" data-sort="amount">금액 <span class="sort-icon"></span></th>
            <th>비고</th>  <!-- 정렬 불가 -->
        </tr>
    </thead>
    <tbody id="grid1-body"></tbody>
</table>
```

> **참고**: `sort-icon` span이 없어도 그리드 초기화시 자동으로 추가됩니다.

### 정렬 동작 방식

1. **컬럼 헤더 클릭**: 해당 필드로 오름차순(ASC) 정렬
2. **같은 컬럼 재클릭**: 내림차순(DESC)으로 변경
3. **다른 컬럼 클릭**: 새로운 필드로 오름차순 정렬

### 정렬 모드 설정

| 옵션 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| `sortNoSearch` | Boolean | false | 정렬시 서버 재조회 여부 |

```javascript
const gridInstance = initTreeGrid({
    gridId: 'grid1',
    templateId: 'row-template',
    urls: { mainUrl: '/api/data' },

    // 정렬시 서버 재조회 (기본값)
    sortNoSearch: false,

    // 또는: 현재 데이터만 정렬 (서버 재조회 없음)
    sortNoSearch: true
});
```

#### 서버 재조회 모드 (`sortNoSearch: false`)

- 정렬 클릭시 서버에 `sortField`와 `sortDirection` 파라미터가 전송됩니다.
- 서버에서 정렬된 데이터를 반환해야 합니다.
- 대용량 데이터에 적합합니다.

**서버로 전송되는 파라미터:**
```javascript
{
    sortField: 'name',        // 정렬 필드명
    sortDirection: 'asc',     // 'asc' 또는 'desc'
    page: 1,
    pageSize: 10,
    // ... 기타 검색 조건
}
```

#### 클라이언트 정렬 모드 (`sortNoSearch: true`)

- 현재 로드된 데이터만 클라이언트에서 정렬합니다.
- 서버 재조회 없이 빠르게 정렬됩니다.
- 전체 데이터가 이미 로드된 경우에 적합합니다.

### 정렬 아이콘 CSS 예시

```css
/* 정렬 아이콘 기본 스타일 */
th.sortable {
    cursor: pointer;
}

th.sortable .sort-icon {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-left: 4px;
    vertical-align: middle;
}

/* 오름차순 아이콘 */
th.sortable .sort-icon.asc::after {
    content: '▲';
    font-size: 10px;
}

/* 내림차순 아이콘 */
th.sortable .sort-icon.desc::after {
    content: '▼';
    font-size: 10px;
}
```

### 현재 정렬 상태 확인

```javascript
// 현재 정렬 필드
const sortField = gridInstance.currentSortField;

// 현재 정렬 방향 ('asc' 또는 'desc')
const sortDirection = gridInstance.currentSortDirection;

console.log(`현재 정렬: ${sortField} ${sortDirection}`);
```

---

## 컬럼 리사이저 (Column Resizer)

그리드 컬럼의 너비를 마우스 드래그로 조절할 수 있습니다.

### 리사이저 설정

| 옵션 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| `resizableYn` | String | 'Y' | 컬럼 리사이저 활성화 여부 |

```javascript
const gridInstance = initTreeGrid({
    gridId: 'grid1',
    templateId: 'row-template',
    urls: { mainUrl: '/api/data' },

    // 리사이저 활성화 (기본값)
    resizableYn: 'Y',

    // 리사이저 비활성화
    // resizableYn: 'N'
});
```

### 리사이저 동작 방식

1. 그리드 초기화시 각 `<th>` 요소에 `.resizer` div가 자동으로 추가됩니다.
2. 컬럼 경계선에 마우스를 올리면 리사이즈 커서가 표시됩니다.
3. 드래그하여 컬럼 너비를 조절합니다.

### 필수 의존성

리사이저 기능은 `gridResizer.js` 파일이 필요합니다.

```html
<script src="ftGrid.js"></script>
<script src="gridResizer.js"></script>
```

### 리사이저 CSS 예시

```css
/* 리사이저 핸들 */
th .resizer {
    position: absolute;
    top: 0;
    right: 0;
    width: 5px;
    height: 100%;
    cursor: col-resize;
    user-select: none;
}

/* 리사이저 호버 효과 */
th .resizer:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

/* 테이블 헤더 상대 위치 설정 */
th {
    position: relative;
}

/* 리사이즈 적용된 테이블 */
table.resizable-table {
    table-layout: fixed;
}
```

### HTML 구조

리사이저가 적용되면 각 `<th>`에 자동으로 resizer div가 추가됩니다:

```html
<!-- 초기화 전 -->
<th class="sortable" data-sort="name">이름</th>

<!-- 초기화 후 (자동 추가됨) -->
<th class="sortable" data-sort="name">
    이름
    <span class="sort-icon"></span>
    <div class="resizer"></div>
</th>
```

### 특정 컬럼 리사이즈 제외

특정 컬럼의 리사이즈를 제외하려면 CSS로 처리합니다:

```css
/* 체크박스 컬럼 리사이즈 제외 */
th:first-child .resizer {
    display: none;
}

/* 특정 클래스의 컬럼 리사이즈 제외 */
th.no-resize .resizer {
    display: none;
    pointer-events: none;
}
```

---

## 템플릿 문법

### 기본 변수 출력

```html
{{name}}
{{user.email}}
{{reverseIndex}}  <!-- 역순 인덱스 -->
```

### 조건문

#### equals (같음)
```html
{{#if status equals 'active'}}
    활성 상태입니다.
{{else}}
    비활성 상태입니다.
{{/if}}
```

#### not equals (다름)
```html
{{#if type not equals 'premium'}}
    일반 사용자입니다.
{{/if}}
```

#### empty (비어있음)
```html
{{#if description empty}}
    설명을 입력하세요
{{else}}
    {{description}}
{{/if}}
```

#### not empty (비어있지 않음)
```html
{{#if name not empty}}
    {{name}}
{{/if}}
```

### 복합 조건문 (AND, OR)

```html
<!-- AND 조건 -->
{{#if status equals 'active' and type equals 'premium'}}
    프리미엄 활성 사용자입니다.
{{/if}}

<!-- OR 조건 -->
{{#if status equals 'active' or status equals 'pending'}}
    처리 가능한 상태입니다.
{{/if}}

<!-- 복합 조건 -->
{{#if level equals '1' and category equals 'A'}}
    레벨 1 카테고리 A 항목입니다.
{{/if}}

<!-- codeMap 사용 -->
{{#if type not equals 'premium' and code['aa'] equals '01'}}
    조건 충족
{{/if}}
```

> **연산자 우선순위**: AND가 OR보다 높은 우선순위
> `A and B or C and D` → `(A and B) or (C and D)`

### 셀 병합 조건

```html
{{#if mergeFirst fieldName}}
    <!-- 병합된 첫 번째 셀에만 표시 -->
    <span>{{fieldName}}</span>
{{else}}
    <!-- 병합된 나머지 셀은 숨김 -->
{{/if}}
```

### 반복문 (each)

```html
{{#each options}}
    <option value="{{this.value}}">{{this.label}}</option>
{{/each}}

<!-- 인덱스 사용 -->
{{#each items}}
    <div>{{@index}}: {{this.name}}</div>
{{/each}}
```

---

## 포맷팅 헬퍼

### 날짜 포맷팅

```html
<!-- 기본 포맷 (YYYY-MM-DD) -->
{{format regDate "date"}}

<!-- 커스텀 패턴 -->
{{format regDate "date" pattern="YYYY/MM/DD"}}
{{format regDate "date" pattern="MM-DD-YYYY"}}
{{format regDate "date" pattern="YYYY년 MM월 DD일"}}
{{format regDate "date" pattern="YYYY-MM-DD HH:mm:ss"}}
```

**지원 패턴:**
- `YYYY` - 4자리 연도
- `YY` - 2자리 연도
- `MM` - 2자리 월 (01-12)
- `M` - 월 (1-12)
- `DD` - 2자리 일 (01-31)
- `D` - 일 (1-31)
- `HH` - 2자리 시간 (00-23)
- `H` - 시간 (0-23)
- `mm` - 2자리 분 (00-59)
- `ss` - 2자리 초 (00-59)

### 금액 포맷팅

```html
<!-- 기본 (한국 원화) -->
{{format amount "currency"}}
<!-- 결과: ₩1,234,567 -->

<!-- 로케일 및 통화 지정 -->
{{format amount "currency" locale="ko-KR" currency="KRW"}}
{{format amount "currency" locale="en-US" currency="USD"}}

<!-- 소수점 자릿수 지정 -->
{{format amount "currency" minimumFractionDigits="2" maximumFractionDigits="2"}}
```

### 숫자 포맷팅 (천단위 콤마)

```html
<!-- 정수 -->
{{format count "number"}}
<!-- 결과: 1,234,567 -->

<!-- 소수점 -->
{{format price "number" decimals="2"}}
<!-- 결과: 1,234.56 -->
```

### 퍼센트 포맷팅

```html
{{format rate "percent"}}
<!-- 입력: 85 → 결과: 85% -->

{{format rate "percent" decimals="2"}}
<!-- 입력: 85.567 → 결과: 85.57% -->
```

### 문자열 포맷팅

```html
<!-- 대문자 -->
{{format name "string" case="upper"}}

<!-- 소문자 -->
{{format name "string" case="lower"}}

<!-- 타이틀 케이스 (첫 글자만 대문자) -->
{{format name "string" case="title"}}
```

### 전화번호 포맷팅

```html
{{format userTelno "phone"}}
<!-- 입력: 01012345678 → 결과: 010-1234-5678 -->
<!-- 입력: 0212345678 → 결과: 02-1234-5678 -->
<!-- 입력: 0311234567 → 결과: 031-123-4567 -->
```

---

## 이벤트 콜백

### 행 클릭

```javascript
onRowClick: function(rowData, $row, event) {
    // 체크박스 클릭시 이벤트 무시
    if (event && $(event.target).closest('.tblChk').length > 0) {
        return false;
    }

    console.log('선택된 행:', rowData);
    // rowData: 행의 전체 데이터 객체
    // $row: jQuery 행 요소
    // event: 클릭 이벤트 객체
}
```

### 행 더블클릭

```javascript
onRowDoubleClick: function(rowData, $row) {
    console.log('더블클릭된 행:', rowData);
    // 상세 페이지 이동 등
    location.href = '/detail/' + rowData.id;
}
```

### 초기화 완료

```javascript
onInitComplete: function() {
    console.log('그리드 초기화 완료!');
    // 초기화 후 실행할 작업
    doSomethingAfterInit();
}
```

### 행 선택 변경

```javascript
onRowSelectionChange: function(info) {
    console.log('선택된 행들:', info.selectedRows);
    console.log('마지막 선택된 행:', info.lastSelectedRowId);
    console.log('현재 변경된 행:', info.currentRowId);
    console.log('체크 상태:', info.isChecked);
}
```

---

## 트리 그리드

### 트리 모드 설정

```javascript
const gridInstance = initTreeGrid({
    gridId: 'treeGrid',
    templateId: 'tree-row-template',
    treeLoadMode: 'lazy',  // 'full' 또는 'lazy'
    urlParamKey: 'projectId',
    urlParentParamKey: 'parentProjectId',
    urls: {
        mainUrl: '/api/projects',
        childrenUrl: '/api/projects/children/{projectId}'
    }
});
```

#### 트리 로드 모드

| 모드 | 설명 |
|------|------|
| `lazy` | 부모 노드 클릭시 자식 데이터 로딩 (기본값) |
| `full` | 초기 로딩시 전체 계층 구조 로딩 |

### 트리 템플릿

```html
<script type="text/template" id="tree-row-template">
    <tr class="{{displayClass}}" data-id="{{id}}" data-level="{{level}}">
        <td><input type="checkbox" class="row-check tblChk" /></td>
        <td>
            {{{toggleSymbol}}}  <!-- 트리 토글 버튼 -->
            {{name}}
        </td>
        <td>{{description}}</td>
    </tr>
</script>
```

### 자식 행 추가

```javascript
// 체크된 행의 자식으로 추가
const result = gridInstance.addChildToSelected();
// result: { newId: '새 행 ID', parentId: '부모 ID' }
```

---

## 엑셀 기능

### 엑셀 업로드 설정

```javascript
const gridInstance = initTreeGrid({
    gridId: 'excelGrid',
    templateId: 'excel-row-template',
    isExcelMode: true,
    excelUploadEnabled: true,
    excelFileInputId: 'excelFileInput',
    excelUploadBtnId: 'excelUploadBtn',
    urls: {
        mainUrl: '/api/excel/data',
        saveUrl: '/api/excel/save'
    },
    excelValidationRules: {
        name: { required: true, maxLength: 50 },
        email: { required: true, pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ },
        amount: { required: true, type: 'number', min: 0 }
    }
});
```

### 엑셀 업로드 HTML

```html
<input type="file" id="excelFileInput" accept=".xlsx,.xls" style="display:none" />
<button type="button" id="excelUploadBtn">엑셀 업로드</button>
```

### 유효성 검사

엑셀 데이터 업로드시 자동으로 유효성 검사가 실행됩니다:
- 오류가 있는 셀은 빨간색으로 강조 표시
- 토스트 메시지로 오류 내용 표시
- 실시간 수정시 자동으로 재검사

---

## API 레퍼런스

### 데이터 조회 메서드

| 메서드 | 설명 | 반환값 |
|--------|------|--------|
| `getData()` | 현재 페이지 데이터 | Array |
| `getAllData()` | 서버 응답 전체 데이터 | Object |
| `getCount()` | 현재 데이터 건수 | Number |
| `getRowData(nodeId)` | 특정 행 데이터 | Object |
| `getRowDataByIndex(index)` | 인덱스로 행 데이터 조회 | Object |
| `getFlatDataList()` | 평면화된 전체 데이터 리스트 | Array |

### 데이터 조작 메서드

| 메서드 | 설명 | 반환값 |
|--------|------|--------|
| `searchData(page)` | 검색 실행 | void |
| `fetchData(page)` | 데이터 로드 | void |
| `setData(responseData, page)` | 외부 데이터 설정 | void |
| `addRow(position)` | 행 추가 | Object |
| `addChildToSelected()` | 자식 행 추가 | Object |
| `deleteSelected()` | 선택된 행 삭제 | void |
| `saveChanges()` | 변경사항 저장 | void |

### 행 수정 메서드

| 메서드 | 설명 | 반환값 |
|--------|------|--------|
| `updateRowData(nodeId, fields)` | 여러 필드 수정 | Boolean |
| `updateRowField(nodeId, field, value)` | 단일 필드 수정 | Boolean |
| `updateRowDataByIndex(index, fields)` | 인덱스로 여러 필드 수정 | Boolean |
| `updateRowFieldByIndex(index, field, value)` | 인덱스로 단일 필드 수정 | Boolean |

### 체크박스 메서드

| 메서드 | 설명 | 반환값 |
|--------|------|--------|
| `getCheckedCount()` | 체크된 행 개수 | Number |
| `getCheckedRowsData()` | 체크된 행 전체 데이터 | Array |
| `getCheckedRowsField(fieldName)` | 체크된 행의 특정 필드값 | Array |
| `getCheckedRowIds()` | 체크된 행 ID 목록 | Array |
| `toggleAll(checked)` | 전체 선택/해제 | void |
| `setCheckboxEnabled(enabled)` | 체크박스 활성화/비활성화 | void |

### 설정 메서드

| 메서드 | 설명 |
|--------|------|
| `setGridData(data)` | 검색 조건 데이터 설정 |
| `setPageSize(size)` | 페이지 사이즈 변경 |
| `setMainUrl(url)` | 메인 URL 변경 |
| `setCodeMap(codeMap)` | 코드 매핑 데이터 설정 |
| `setDefaultFields(fields)` | 기본 필드값 설정 |
| `setEffectCheckBox(effect)` | 체크박스 수정 추적 설정 |

### 유틸리티 메서드

| 메서드 | 설명 | 반환값 |
|--------|------|--------|
| `findNodeById(id)` | ID로 노드 검색 | Object |
| `getNodeIdByIndex(index)` | 인덱스로 nodeId 조회 | String |
| `getModifiedRows()` | 수정된 행 목록 | Array |
| `getDeletedRows()` | 삭제된 행 목록 | Array |
| `getCodeMap()` | 코드 매핑 데이터 조회 | Object |
| `selectRow(rowId, isMultiple)` | 행 선택 | void |

---

## 전역 함수

그리드 인스턴스는 `gridManagers` 객체에 저장됩니다:

```javascript
// 그리드 매니저 접근
const gridManager = gridManagers['gridId'];

// 전역 함수 사용
const rowData = getGridRowDataByIndex('gridId', 0);
updateGridRowFieldByIndex('gridId', 1, 'status', 'active');
```

---

## 커스텀 렌더링

템플릿 대신 JavaScript 함수로 직접 행을 렌더링할 수 있습니다:

```javascript
const gridInstance = initTreeGrid({
    gridId: 'customGrid',
    urls: { mainUrl: '/api/data' },
    renderRowFunction: function(rowData, reverseIndex, codeMap, selectOption) {
        return `
            <tr data-id="${rowData.id}">
                <td><input type="checkbox" class="row-check tblChk" /></td>
                <td>${rowData.name}</td>
                <td>${reverseIndex}</td>
                <td>${codeMap.status[rowData.statusCode] || ''}</td>
            </tr>
        `;
    }
});
```

---

## 주의사항

1. **ID 필드**: 각 행은 고유한 `id` 필드가 필요합니다. 없으면 자동 생성됩니다.

2. **서버 응답 형식**:
   ```json
   {
       "total": 100,
       "data": [
           { "id": 1, "name": "항목1" },
           { "id": 2, "name": "항목2" }
       ]
   }
   ```

3. **트리 구조시 필수 필드**:
   - `childYn`: 자식 존재 여부 ('Y' 또는 'N')
   - `urlParamKey`로 지정한 필드: 고유 키
   - `urlParentParamKey`로 지정한 필드: 부모 키

4. **data-field 속성**: 수정 추적을 위해 input 요소에 `data-field` 속성 필요

5. **data-value 속성**: 초기값 설정을 위해 `data-value` 속성 사용

---

## 문의 및 지원

추가 문의사항이 있으시면 개발팀에 연락해 주세요.
