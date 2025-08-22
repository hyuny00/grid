class TreeGridManager {
    constructor(config) {
        this.gridId = config.gridId;
        this.searchFormId = config.searchFormId;
        this.urls = config.urls;
        this.templateId = config.templateId;
        this.pageSize = config.pageSize; // undefined면 페이징 비활성화
        this.defaultFields = config.defaultFields || {};
        this.urlParamKey = config.urlParamKey;
        this.urlParentParamKey = config.urlParentParamKey;

        this.addRowPosition = config.addRowPosition || 'top'; // 행 추가 위치: 'top' 또는 'bottom'
        this.addChildPosition = config.addChildPosition || 'bottom'; // 자식행 추가 위치: 'top' 또는 'bottom'


        // 엑셀 유효성 검사 설정 추가
        this.excelValidationRules = config.excelValidationRules || {};
        // ★ 추가: 엑셀 오류 관련 초기화
        this.excelValidationErrors = [];
        this.excelErrorCells = [];

        // 기능 활성화 여부 체크
        this.treeLoadMode = config.treeLoadMode || 'lazy'; // 'full' 또는 'lazy'
        this.isTreeMode = !!(this.urls.childrenUrl) || this.treeLoadMode === 'full';


       // this.isSaveEnabled = !!(this.urls.saveUrl);
       // this.isSaveEnabled = !!(this.urls.saveExlUrl || this.urls.saveUrl);
        this.isSaveEnabled=true;

        this.isPagingEnabled = !!(this.pageSize); // pageSize가 있으면 페이징 활성화


        // 컨텍스트 메뉴 설정 추가
        this.contextMenuEnabled = config.contextMenuEnabled || false;
        this.contextMenuItems = config.contextMenuItems || [];
        this.onContextMenuClick = config.onContextMenuClick; // 콜백 함수


        this.gridData= config.gridData || {};

        this.checkCount = config.checkCount; // undefined면 제한 없음

        // 그리드별 독립적인 데이터
        this.currentPage = 1;
        this.data = [];
        this.addedRows = new Set();
        this.modifiedRows = new Set();
        this.deletedRows = new Set();
        this.totalCount = 0;
        this.totalPages = 0;

        // 행 클릭 콜백 함수
        this.onRowClick = config.onRowClick;

     // 행 더블클릭 콜백 함수
        this.onRowDoubleClick = config.onRowDoubleClick;


        // 엑셀 업로드 관련 설정 추가
        this.excelUploadEnabled = config.excelUploadEnabled || false;
        this.excelFileInputId = config.excelFileInputId; // 파일 input ID
        this.excelUploadBtnId = config.excelUploadBtnId; // 업로드 버튼 ID

        // 엑셀 데이터 저장용 (원본 엑셀 데이터를 모두 보관)
        this.excelData = [];
        this.isExcelMode = config.isExcelMode || false; // 엑셀 모드인지 일반 모드인지 구분
        //엑셀 업로드 관련 설정 추가end

        //자동조회
        this.isLoading =  config.isLoading || 'Y';


        this.lastToastrMessage = null;
        this.codeMap =  config.codeMap || {};

        this.selectOption=  config.selectOption || {};

        this.currentSortDirection = 'asc'; // 'asc' 또는 'desc'

        this.onInitComplete = config.onInitComplete;

        // 셀 병합 설정 추가
        this.mergeCells = config.mergeCells || {}; // { field1: true, field2: true }
        this.mergeFields = Object.keys(this.mergeCells).filter(field => this.mergeCells[field]);

        this.renderRowFunction = config.renderRowFunction;  // 추가


        this.responseData={};


        this.selectedRows = new Set(); // 선택된 행들의 ID 저장
        this.lastSelectedRowId = null; // 마지막으로 선택된 행 ID


        this.init();
    }

    // 행 선택 상태 관리
    selectRow(rowId, isMultiple = false) {
        if (!isMultiple) {
            this.selectedRows.clear();
            // 기존 선택 표시 제거
            $(`#${this.gridId}-body tr`).removeClass('selected');
        }

        this.selectedRows.add(rowId);
        this.lastSelectedRowId = rowId;

        // 선택 표시 추가
        $(`#${this.gridId}-body tr[data-id="${rowId}"]`).addClass('selected');

        console.log('선택된 행들:', Array.from(this.selectedRows));
    }

    setMainUrl(url) {
    	this.urls.mainUrl=url;
    }




    setPageSize(newPageSize) {
        this.pageSize = newPageSize;
        this.searchData();
    }

    setGridData(gridData) {
        this.gridData = gridData;
    }

    init() {

    	if( this.isExcelMode){
    		 this.fetchExcelData();
    		 this.bindEvents();
    	}else{
    	 	 if( this.isLoading=='Y'){
    	 		this.fetchData();
    	 	 }

    		 this.bindEvents();
    	}



    }

    bindEvents() {
        const self = this;
        // 컨텍스트 메뉴 이벤트 바인딩
        if (this.contextMenuEnabled) {
            this.bindContextMenuEvents();
        }
        // 검색 버튼 이벤트
        $(`#${this.searchFormId} .tblChk`).on('click', function(e) {
            e.stopPropagation();
        });
        // 검색 버튼 이벤트
        $(`#${this.searchFormId} .btn-search`).on('click', function() {
            self.searchData();
        });
        // 초기화 버튼 이벤트
        $(`#${this.searchFormId} .btn-reset`).on('click', function() {
            self.resetSearch();
        });
        // 그리드 액션 버튼들
        $(`#${this.gridId}-container .btn-add-row`).on('click', function() {
            self.addRow();
        });
        // 트리 모드일 때만 자식 추가 버튼 활성화
        if (this.isTreeMode) {
            $(`#${this.gridId}-container .btn-add-child`).on('click', function() {
                self.addChildToSelected();
            });
        }
        if(!this.urls.deleteUrl){
            $(`#${this.gridId}-container .btn-delete`).on('click', function() {
                 self.deleteSelected2();
            });
        } else{
             $(`#${this.gridId}-container .btn-delete`).on('click', function() {
                 self.deleteSelected();
             });
        }
        $(`#${this.gridId}-container`).on('change', '.row-check', function(e) {
            const $checkbox = $(this);
            const $row = $checkbox.closest('tr');
            const nodeId = String($row.data('id'));

            // 체크하려고 할 때만 개수 제한 확인
            if ($checkbox.is(':checked') && self.checkCount) {
                const currentCheckedCount = $(`#${self.gridId}-container .row-check:checked`).length;
                if (currentCheckedCount > self.checkCount) {
                    // 제한 개수 초과시 체크 해제하고 경고
                    $checkbox.prop('checked', false);
                    alert(`최대 ${self.checkCount}개까지만 선택할 수 있습니다.`);
                    return;
                }
            }

            // 체크박스 상태 변경을 데이터에 반영
            console.log('Node ID:', nodeId);
            if (nodeId) {
                // checked 필드로 체크 상태 추적
                self.trackEdit(nodeId, this, 'checked');
                console.log('trackEdit 호출 완료');

                // ★ 새로 추가: 행 선택 상태 관리
                if ($checkbox.prop('checked')) {
                    // 체크된 경우 - 선택 상태에 추가 (다중 선택 허용)
                    if (!self.selectedRows) self.selectedRows = new Set();
                    self.selectedRows.add(nodeId);
                    self.lastSelectedRowId = nodeId;
                    $row.addClass('selected');

                    console.log('행 선택됨:', nodeId);
                } else {
                    // 체크 해제된 경우 - 선택 상태에서 제거
                    if (self.selectedRows) {
                        self.selectedRows.delete(nodeId);
                        $row.removeClass('selected');

                        // 마지막 선택이 해제된 경우 업데이트
                        if (self.lastSelectedRowId === nodeId) {
                            self.lastSelectedRowId = self.selectedRows.size > 0 ?
                                Array.from(self.selectedRows).pop() : null;
                        }
                    }

                    console.log('행 선택 해제됨:', nodeId);
                }

                console.log('현재 선택된 행들:', self.selectedRows ? Array.from(self.selectedRows) : []);
                console.log('마지막 선택된 행:', self.lastSelectedRowId);

                // ★ 선택 상태 변경 이벤트 발생 (필요한 경우)
                if (self.onRowSelectionChange) {
                    self.onRowSelectionChange({
                        selectedRows: Array.from(self.selectedRows || []),
                        lastSelectedRowId: self.lastSelectedRowId,
                        currentRowId: nodeId,
                        isChecked: $checkbox.prop('checked')
                    });
                }
            }

            self.updateHeaderCheckbox();
        });

        // ★ 추가: click 이벤트도 바인딩 (브라우저 호환성을 위해)
        $(`#${this.gridId}-container`).on('click', '.row-check', function(e) {
            console.log('체크박스 click 이벤트 발생:', this, e);
            // click 이벤트에서는 change 이벤트 중복 방지를 위해 별도 처리하지 않음
        });


        // 헤더 정렬 클릭 이벤트 추가
        $(`#${this.gridId} th.sortable`).on('click', function() {
            const sortField = $(this).data('sort');
            self.handleSort(sortField);
        });
        // 저장 기능이 활성화된 경우만 저장 버튼 바인딩
        if (this.isSaveEnabled) {
            $(`#${this.gridId}-container .btn-save`).on('click', function() {
                self.saveChanges();
            });
        }
        // 전체 선택 체크박스
        $(`#${this.gridId}-container .check-all`).on('change', function() {
            self.toggleAll(this.checked);
        });
        // 엑셀 업로드 기능이 활성화된 경우만 이벤트 바인딩 start
        if (this.excelUploadEnabled) {
            // 엑셀 업로드 버튼 이벤트
            $(`#${this.excelUploadBtnId}`).on('click', function() {
                $(`#${self.excelFileInputId}`).val('');
                $(`#${self.excelFileInputId}`).click();
            });
            // 파일 선택 이벤트
            $(`#${self.excelFileInputId}`).on('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    self.handleExcelUpload(file);
                }
            });
        }//엑셀 업로드 기능이 활성화된 경우만 이벤트 바인딩 end
        // 행 클릭 이벤트 바인딩
        this.bindRowClickEvents();
    }

    handleSort(sortField) {
        // 같은 필드를 다시 클릭하면 방향 변경, 다른 필드면 ASC로 초기화
        if (this.currentSortField === sortField) {
            this.currentSortDirection = this.currentSortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            this.currentSortField = sortField;
            this.currentSortDirection = 'asc';
        }

        // 정렬 아이콘 업데이트
        this.updateSortIcons();

        // 데이터 다시 조회
        this.searchData();
    }

    updateSortIcons() {
        // 모든 정렬 아이콘 초기화
        $(`#${this.gridId} th .sort-icon`).removeClass('asc desc');

        // 현재 정렬 필드의 아이콘만 활성화
        if (this.currentSortField) {
            $(`#${this.gridId} th[data-sort="${this.currentSortField}"] .sort-icon`)
                .addClass(this.currentSortDirection);
        }
    }


    getSearchParams() {
        const params = {};


        const excludeParams = ['topMenuSeq','menuSeq','upMenuSeq','_csrf'];

        $(`#${this.searchFormId}`).find('input, select, textarea').each(function() {


        	const $this = $(this);
            const name = $this.attr('name');
            let value;

            if(!name || excludeParams.includes(name) ){
            	return;
            }

            // Select2가 적용된 select 요소인지 확인
            if ($this.hasClass('select2-hidden-accessible') || $this.data('select2')) {
                // Select2에서 값 가져오기
                value = $this.val();

                // 다중 선택인 경우 배열로 반환됨
                if (Array.isArray(value)) {
                    // 빈 값들 제거하고 조인
                    value = value.filter(v => v && v.trim() !== '').join(',');
                }
            } else {
                // 일반 input/textarea/select에서 값 가져오기
                value = $this.val();
            }

            if (name && value && value.toString().trim() !== '') {
                params[name] = value.toString().trim();
            }
        });

        Object.keys(this.gridData).forEach(key => {
        	const value = this.gridData[key];
        	if(value !== null && value !=='undefined' && value.toString().trim()!==''){
        		params[key] = value.toString().trim();
        	}
        });

        // 정렬 파라미터 추가
        if (this.currentSortField) {
            params.sortField = this.currentSortField;
            params.sortDirection = this.currentSortDirection;
        }

        return params;
    }



    searchData() {

        this.currentPage = 1;
        this.toggleAll(false);
        if( this.isExcelMode){
        	 this.fetchExcelData();
        }else{
        	this.fetchData(this.currentPage);
        }

    }

    resetSearch() {
      //  $(`#${this.searchFormId}`)[0].reset();
       // this.searchData();

        const $form = $(`#${this.searchFormId}`);

        // 폼 초기화
        $form[0].reset();

        // 모든 Select2 초기화
       // $form.find('.select2-hidden-accessible').val(null).trigger('change');

        // 대부분의 경우 첫 번째 옵션이 value=""
        $form.find('.select2-hidden-accessible').val('').trigger('change');

    }


    fetchData(page = 1) {

    	if(!this.urls.mainUrl) return;


        this.currentPage = page;

        const searchParams = this.getSearchParams();
        const requestParams = Object.assign({}, searchParams);

        // 페이징이 활성화된 경우만 페이징 파라미터 추가
        if (this.isPagingEnabled) {
            requestParams.page = this.currentPage;
            requestParams.pageSize = this.pageSize;
        }

        console.log(`[${this.gridId}] Request params:`, JSON.stringify(requestParams));

        $.ajax({
        	url: this.urls.mainUrl,
        	method: "POST",
        	contentType: "application/json;charset=utf-8",
        	data: JSON.stringify(requestParams),
            success: (res) => {
            	// setData 메서드 사용
                this.setData(res, page);
            },
            error: (xhr, status, error) => {
                console.log(`[${this.gridId}] 서버 연결 실패:`, error);
                alert("데이터를 불러오는데 실패했습니다.");
            }
        });
    }

    getData(){
    	return  this.data;
    }

    getAllData(){
    	return  this.responseData;
    }

    getCount(){
    	return  this.data.length;
    }

    setData(responseData, page = 1) {

    	this.responseData = responseData;

        this.currentPage = page;

        this.data = [];

        // 페이징이 활성화된 경우만 페이징 정보 설정
        if (this.isPagingEnabled) {
            this.totalCount = responseData.total; // 전체 건수
            this.totalPages = Math.ceil(this.totalCount / this.pageSize);
        } else {
           // this.totalCount = responseData.data.length;
            this.totalCount = responseData.total; // 전체 건수
            this.totalPages = 1;
        }

       // console.log(responseData);
        // 데이터를 responseData.data에서 꺼냄
        const rows = responseData.data || [];  // responseData.data가 undefined/null이면 빈 배열로 대체


        if (this.isTreeMode && this.treeLoadMode === 'full') {
            // 전체로딩 모드: 계층구조 구성
            this.data = this.buildTreeStructure(rows);
        } else {
            for (let i = 0; i < rows.length; i++) {
                const item = rows[i];

                const nodeData = {
                    ...item,
                    id: item.id ? String(item.id) : 'auto_' + Date.now().toString() + '_' + i + '_' + Math.floor(Math.random() * 10000).toString(),
                    level: 0,
                    parentPath: ''
                };

                // 트리 모드일 때만 트리 관련 속성 추가
                if (this.isTreeMode) {
                    nodeData.childrenLoaded = false;
                    nodeData.children = [];
                    nodeData.treeExpanded = false;
                    nodeData.childYn = item.childYn || 'N';
                }

                this.data.push(nodeData);
            }
        }

        this.renderTable();

        // 페이징이 활성화된 경우만 페이지네이션 렌더링
        if (this.isPagingEnabled) {
            this.renderPagination();
            this.updatePageInfo();
        }

		 if (this.onInitComplete && typeof this.onInitComplete === 'function' ) {
            this.onInitComplete();
        }


    }

    buildTreeStructure(flatData) {


        const nodeMap = new Map();
        const rootNodes = [];

        // 1단계: 모든 노드를 맵에 저장하면서 기본 구조 생성
        flatData.forEach((item, index) => {
            const nodeData = {
                ...item,
                id: item.id ? String(item.id) : 'auto_' + Date.now().toString() + '_' + index + '_' + Math.floor(Math.random() * 10000).toString(),
                level: 0, // 임시로 0 설정, 나중에 계산
                parentPath: '',
                childrenLoaded: true, // 전체로딩이므로 true
                children: [],
                treeExpanded: true, // 전체로딩시 모두 펼친 상태
                childYn: item.childYn || 'N'
            };
            const nodeKey = item[this.urlParamKey]; // 예: projectId
            nodeMap.set(nodeKey, nodeData);
        });

        // 2단계: 부모-자식 관계 설정 및 레벨 계산
        nodeMap.forEach((node, nodeKey) => {
            const parentKey = node[this.urlParentParamKey]; // 예: parentProjectId

            if (parentKey && nodeMap.has(parentKey)) {
                const parent = nodeMap.get(parentKey);
                parent.children.push(node);
                node.level = parent.level + 1;
                node.parentPath = parent.parentPath + parentKey + '/';
            } else {
                rootNodes.push(node);
                node.level = 0;
                node.parentPath = '';
            }
        });

        return rootNodes;
    }

    fetchExcelData() {

    	if(!this.urls.mainUrl) return;

        $.ajax({
            url: this.urls.mainUrl,
            method: "POST",
            contentType: "application/json",
            success: (res) => {
                const rows = res.data || [];

                // ★ 서버 데이터를 엑셀 형태로 변환하여 processExcelData 호출
                if (rows.length > 0) {
                    // 객체 배열을 2차원 배열로 변환 (엑셀 형태)
                    const headers = Object.keys(rows[0]);
                    const excelFormatData = [
                        headers, // 첫 번째 행은 헤더
                        ...rows.map(item => headers.map(header => item[header] || '')) // 데이터 행들
                    ];

                    // 엑셀 업로드와 동일하게 처리 (유효성 검사 포함)
                    this.processExcelData(excelFormatData); // true = 엑셀처럼 처리
                } else {
                    // 데이터가 없는 경우
                    this.data = [];
                    this.excelData = [];
                    this.isExcelMode = true; // 엑셀 모드로 설정
                    this.renderTable();
                }


            },
            error: (xhr, status, error) => {
                console.log(`[${this.gridId}] 서버 연결 실패:`, error);
                alert("데이터를 불러오는데 실패했습니다.");
            }
        });
    }

 // 2. renderTable 메서드 수정 - 병합 로직 추가
 // 2. renderTable 메서드 수정 - 병합 로직 추가
    renderTable() {
        const tbody = $(`#${this.gridId}-body`);
        tbody.empty();

        let globalIndex = 0;
        const totalCount = this.totalCount || this.data.length;

        // 평면화된 데이터 생성 (병합 계산을 위해)
        const flatData = [];
        const collectNodes = (nodes, isVisible = true) => {
            nodes.forEach(node => {
                flatData.push({
                    node: node,
                    isVisible: isVisible,
                    globalIndex: globalIndex++
                });

                if (this.isTreeMode && node.children && node.children.length > 0) {
                    const childVisible = isVisible && node.treeExpanded;
                    collectNodes(node.children, childVisible);
                }
            });
        };

        // 전체 노드 수집
        globalIndex = 0;
        collectNodes(this.data);

        // 병합 그룹 계산
        const mergeGroups = this.calculateMergeGroups(flatData);


        // 테이블 렌더링
        globalIndex = 0;
        flatData.forEach(item => {
            const reverseIndex = this.totalCount - (this.currentPage - 1) * this.pageSize - item.globalIndex;
            const rowHtml = this.createNodeRow(
                item.node,
                item.isVisible,
                item.globalIndex,
                reverseIndex,
                mergeGroups[item.globalIndex] || {}
            );
            tbody.append(rowHtml);
        });

        // 병합 스타일 적용
        this.applyMergeStyles();

        // 기존 코드 (날짜 입력 필드 초기화 등)...
        this.initializeDatePickers();

        // 모든 폼 요소의 값을 data-value 속성으로 설정
        $(`#${this.gridId}-container [data-value]`).each(function() {
            const $element = $(this);
            const value = $element.data('value');

            if (value !== undefined && value !== null) {
                if ($element.is('select')) {
                    $element.val(value);
                } else if ($element.is('input[type="checkbox"]')) {
                    if ($element.hasClass('row-check')) {
                        const nodeId = $element.closest('tr').data('id');
                        const result = this.findNodeById(nodeId);
                        if (result && result.node) {
                            $element.prop('checked', result.node.checked === true || result.node.checked === 'true' || result.node.checked === 'Y');
                        }
                    } else {
                        $element.prop('checked', value === true || value === 'true' || value === 'Y');
                    }
                } else if ($element.is('input[type="radio"]')) {
                    const name = $element.attr('name');
                    $(`#${this.gridId}-container input[type="radio"][name="${name}"][value="${value}"]`).prop('checked', true);
                } else if ($element.is('input, textarea')) {
                    $element.val(value);
                }
            }
        });

        /*
        // 체크박스 상태를 별도로 설정 (병합된 셀 포함)
        $(`#${this.gridId}-body input.row-check`).each((index, checkbox) => {
            const $checkbox = $(checkbox);
            const nodeId = $checkbox.closest('tr').data('id');
            const result = this.findNodeById(nodeId);
            if (result && result.node) {
                $checkbox.prop('checked', result.node.checked === true || result.node.checked === 'true' || result.node.checked === 'Y');
            }
        });
        */

        this.bindEditEvents();
    }


    initializeDatePickers() {

    	const $dateInputs = $(`#${this.gridId}-container .date-input`);
    	 if ($dateInputs.length === 0) {
    		 return;
    	 }

        // flatpickr가 존재하고 date-input 요소가 있을 때만 실행
        if (typeof $.fn.flatpickr === 'function') {
	            $dateInputs.flatpickr({
	                locale: 'ko',
	                dateFormat: 'Y-m-d',
	                onReady: function(selectedDates, dateStr, instance) {
	                    if (typeof replaceYearWithSelect === 'function') {
	                        replaceYearWithSelect(instance);
	                    }
	                },
	                onOpen: function(selectedDates, dateStr, instance) {
	                    if (typeof replaceYearWithSelect === 'function') {
	                        replaceYearWithSelect(instance);
	                    }
	                }
	            });
        } else {
            console.warn('flatpickr library is not loaded yet');
        }
    }

    // 3. 병합 그룹 계산 메서드 추가
    calculateMergeGroups(flatData) {
        const mergeGroups = {};

        if (this.mergeFields.length === 0) return mergeGroups;

        this.mergeFields.forEach(field => {
            let currentGroup = [];
            let currentValue = null;

            for (let i = 0; i < flatData.length; i++) {
                const item = flatData[i];
                const value = item.node[field];

                if (value === currentValue) {
                    // 같은 값이면 현재 그룹에 추가
                    currentGroup.push(i);
                } else {
                    // 다른 값이면 이전 그룹 처리하고 새 그룹 시작
                    this.processMergeGroup(field, currentGroup, mergeGroups);
                    currentGroup = [i];
                    currentValue = value;
                }
            }

            // 마지막 그룹 처리
            this.processMergeGroup(field, currentGroup, mergeGroups);
        });

        return mergeGroups;
    }

    // 4. 병합 그룹 처리 메서드
    processMergeGroup(field, group, mergeGroups) {
        if (group.length <= 1) return;

        group.forEach((index, groupIndex) => {
            if (!mergeGroups[index]) mergeGroups[index] = {};

            mergeGroups[index][field] = {
                isFirst: groupIndex === 0,
                isLast: groupIndex === group.length - 1,
                groupSize: group.length,
                groupIndex: groupIndex
            };
        });
    }


    // 편집 이벤트 바인딩 메서드 수정
 // 원본 TreeGridManager의 bindEditEvents 메서드 완성
    bindEditEvents() {
        const self = this;

        // 1. 일반 편집 가능한 요소들의 이벤트 바인딩 (라디오버튼 제외)
        $(`#${this.gridId}-body`)
            .off('input change blur keyup') // 기존 이벤트 제거
            .on('input change blur keyup',
                'input[data-field]:not([type="radio"]):not(.row-check), textarea[data-field], select[data-field], [contenteditable][data-field]',
                function() {
                    const $this = $(this);
                    const nodeId = String($this.closest('tr').data('id'));
                    const field = $this.data('field');

                    if (nodeId && field) {
                        self.trackEdit(nodeId, this, field);
                    }
                }
            );

        // 2. 라디오버튼 전용 처리 (change 이벤트만)
        $(`#${this.gridId}-body`)
            .off('change.radio')
            .on('change.radio', 'input[type="radio"][data-field]', function() {
                const $this = $(this);
                const nodeId = String($this.closest('tr').data('id'));
                const field = $this.data('field');
                if (nodeId && field) {
                    self.trackEdit(nodeId, this, field);
                }
            });

        // 3. 트리 토글 이벤트 추가
        $(`#${this.gridId}-body`)
            .off('click.treeToggle')
            .on('click.treeToggle', '.tree-toggle', function(e) {
                e.preventDefault();
                const nodeId = String($(this).closest('tr').data('id'));
                if (nodeId) {
                    self.toggleTree(nodeId, e);
                }
            });

        // 4. contenteditable 요소에 대한 추가 이벤트
        $(`#${this.gridId}-body`)
            .off('paste')
            .on('paste', '[contenteditable][data-field]', function(e) {
                const $this = $(this);
                const nodeId = String($this.closest('tr').data('id'));
                const field = $this.data('field');

                setTimeout(() => {
                    if (nodeId && field) {
                        self.trackEdit(nodeId, this, field);
                    }
                }, 10);
            });
    }

 // 5. createNodeRow 메서드 수정 - 병합 정보 포함
    createNodeRow(node, isVisible, index, reverseIndex = null, mergeInfo = {}) {
        let hasChildren = false;
        let toggleSymbol = '';
        let toggleClass = 'tree-toggle no-children';

        if (this.isTreeMode) {
            hasChildren = (node.childYn === 'Y') || (node.children && node.children.length > 0);
            toggleSymbol = hasChildren ? (node.treeExpanded ? '[-]' : '[+]') : '';
            toggleClass = hasChildren ? 'tree-toggle' : 'tree-toggle no-children';
        }

        const displayClass = node.level === 0 ? '' : (isVisible ? 'tree-row' : 'tree-row hidden');
        const indentStyle = 'padding-left: ' + (node.level * 20) + 'px;';

        if (!node.id) {
            node.id = 'auto_' + Date.now().toString() + '_' + Math.floor(Math.random() * 10000).toString();
        }

        const rowData = {
            ...node,
            checkedAttr: (node.checked === true || node.checked === 'true' || node.checked === 'Y') ? 'checked' : '',
            gridId: this.gridId,
            displayClass: displayClass,
            indentStyle: indentStyle,
            toggleSymbol: toggleSymbol,
            toggleClass: toggleClass,
            isTreeMode: this.isTreeMode,
            mergeInfo: mergeInfo // 병합 정보 추가
        };


        // 템플릿 렌더링에서 병합 처리
       // const rowHtml = this.renderTemplate(this.templateId, rowData, this.codeMap, this.selectOption, reverseIndex);

        const rowHtml = this.renderRowFunction ?
                this.renderRowFunction(rowData, reverseIndex, this.codeMap, this.selectOption) :
                this.renderTemplate(this.templateId, rowData, this.codeMap, this.selectOption, reverseIndex); // fallback


        const $row = $(rowHtml);
        $row.attr('data-id', node.id);
        $row.attr('data-level', node.level);
        $row.attr('data-parent-path', node.parentPath);

        // 병합된 셀에 클래스 추가
        this.mergeFields.forEach(field => {
            if (mergeInfo[field]) {
                const $cell = $row.find(`[data-field="${field}"]`).closest('td');
                if ($cell.length > 0) {
                    $cell.addClass('merge-cell');
                    $cell.attr('data-merge-field', field);
                    $cell.attr('data-merge-first', mergeInfo[field].isFirst);
                    $cell.attr('data-merge-last', mergeInfo[field].isLast);
                    $cell.attr('data-merge-size', mergeInfo[field].groupSize);

                    // 첫 번째가 아닌 셀은 내용 숨김
                    if (!mergeInfo[field].isFirst) {
                        $cell.addClass('merge-hidden');
                    }
                }
            }
        });

        return $row[0].outerHTML;
    }

 // 6. 병합 스타일 적용 메서드
    applyMergeStyles() {
        // 동적으로 스타일 추가
        const styleId = `${this.gridId}-merge-styles`;
        let $style = $(`#${styleId}`);
        if ($style.length === 0) {
            $style = $('<style>').attr('id', styleId).appendTo('head');
        }
        const css = `
            #${this.gridId}-body .merge-cell.merge-hidden {
                color: transparent !important;
                background-color: transparent !important;
                position: relative !important;
            }
            #${this.gridId}-body .merge-cell.merge-hidden input,
            #${this.gridId}-body .merge-cell.merge-hidden select,
            #${this.gridId}-body .merge-cell.merge-hidden textarea {
                opacity: 0 !important;
                pointer-events: none !important;
            }
            /* 체크박스는 보이게 하고 클릭 가능하게 */
            #${this.gridId}-body .merge-cell.merge-hidden input[type="checkbox"] {
                opacity: 1 !important;
                pointer-events: auto !important;
                visibility: visible !important;
                position: relative !important;
                z-index: 10 !important;
            }
            /* 모든 병합 셀의 아래쪽 테두리 강제 제거 */
            #${this.gridId}-body .merge-cell:not([data-merge-last="true"]),
            #${this.gridId}-body td.merge-cell:not([data-merge-last="true"]),
            #${this.gridId}-body .merge-cell.merge-hidden:not([data-merge-last="true"]) {
                border-bottom: none !important;
                border-bottom-width: 0 !important;
                border-bottom-style: none !important;
            }
            /* 체크박스가 있는 셀도 명시적으로 처리 */
            #${this.gridId}-body .merge-cell:has(input[type="checkbox"]):not([data-merge-last="true"]),
            #${this.gridId}-body td:has(input[type="checkbox"]).merge-cell:not([data-merge-last="true"]) {
                border-bottom: none !important;
                border-bottom-width: 0 !important;
                border-bottom-style: none !important;
            }
            /* ::before 요소 */
            #${this.gridId}-body .merge-cell.merge-hidden:not(.has-checkbox)::before {
                content: '';
                display: block;
                width: 100%;
                height: 100%;
                position: absolute;
                top: 0;
                left: 0;
                background-color: transparent;
                z-index: 1 !important;
                pointer-events: none !important;
            }
        `;
        $style.html(css);

    }



    findNodeById(id, nodes = this.data, parentPath = '') {
        // ID를 문자열로 변환하여 비교
        const targetId = String(id);

        for (let i = 0; i < nodes.length; i++) {
            const node = nodes[i];
            if (String(node.id) === targetId) {
                return { node: node, parent: null, parentPath: parentPath };
            }
            if (this.isTreeMode && node.children && node.children.length > 0) {
                const found = this.findNodeById(targetId, node.children, parentPath + node.id + '/');
                if (found) {
                    if (found.parent === null) {
                        found.parent = node;
                    }
                    return found;
                }
            }
        }
        return null;
    }

    toggleTree(nodeId, e) {
        if (!this.isTreeMode) return;

        e.preventDefault();
        const result = this.findNodeById(nodeId);

        if (!result || !result.node) return;

        const node = result.node;

        if (this.treeLoadMode === 'full') {
            // 전체로딩 모드: 단순히 펼치기/접기만 토글
            node.treeExpanded = !node.treeExpanded;
            this.renderTable();
        } else {
            // 지연로딩 모드: 기존 로직 유지
            if (!node.treeExpanded) {
                this.loadChildren(nodeId);
            } else {
                node.treeExpanded = !node.treeExpanded;
                this.renderTable();
            }
        }
    }

    loadChildren(nodeId) {
        // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;

        const result = this.findNodeById(nodeId);
        if (!result || !result.node) return;

        const node = result.node;
        const paramValue = node[this.urlParamKey]; // ← no 값을 가져옴

        $.ajax({
        	url: this.urls.childrenUrl.replace(`{${this.urlParamKey}}`, paramValue),
            method: "GET",
            success: (response) => {
                // 서버 응답에서 data 배열 추출
                const children = response.data || [];
                const result = this.findNodeById(nodeId);
                if (result && result.node) {
                    const node = result.node;
                    node.children = [];
                    for (let i = 0; i < children.length; i++) {
                        const child = children[i];
                        node.children.push({
                            ...child, // 서버에서 온 모든 필드를 그대로 사용
                            level: node.level + 1,
                            //parentPath: node.parentPath + node.id + '/',
                            parentPath: node.parentPath + (node[this.urlParamKey] || node.id) + '/', // ← 수정
                            childrenLoaded: false,
                            children: [],
                            treeExpanded: false,
                            childYn: child.childYn || 'N'
                        });
                    }
                    node.childrenLoaded = true;
                    node.treeExpanded = true;
                    this.renderTable();
                }
            },
            error: (xhr, status, error) => {
                console.log(`[${this.gridId}] 자식 데이터 로드 실패:`, error);
                alert("자식 데이터를 불러오는데 실패했습니다.");
            }
        });
    }


    setDefaultFields(defaultFields){
    	this.defaultFields=defaultFields;
    }

    addRow(position = 'top', targetRowId = null) {
        const newId = 'N_' + Date.now().toString() + Math.floor(Math.random() * 1000).toString();
        const newRow = {
            id: newId,
            checked: false,
            ...this.defaultFields,
            level: 0,
            parentPath: '',
            isNew: true
        };

        // 트리 모드일 때만 트리 관련 속성 추가
        if (this.isTreeMode) {
            newRow.childrenLoaded = true;
            newRow.children = [];
            newRow.treeExpanded = false;
            newRow.childYn = 'N';
        }

        let insertIndex = -1;
        let parentId = null;

        // 위치별 처리
        switch (position) {
            case 'top':
                insertIndex = 0;
                break;

            case 'bottom':
                insertIndex = this.data.length;
                break;

            case 'above': // 선택된 행 위에
                if (targetRowId || this.lastSelectedRowId) {
                    const targetId = targetRowId || this.lastSelectedRowId;
                    insertIndex = this.data.findIndex(row => String(row.id) === String(targetId));

                    if (insertIndex !== -1) {
                        // 같은 레벨로 설정
                        const targetRow = this.data[insertIndex];
                        newRow.level = targetRow.level || 0;
                        newRow.parentPath = targetRow.parentPath || '';
                        parentId = this.getParentId(targetRow);
                    } else {
                        insertIndex = 0; // 못찾으면 맨 위에
                    }
                } else {
                    insertIndex = 0; // 선택된 행이 없으면 맨 위에
                }
                break;

            case 'below': // 선택된 행 아래에
                if (targetRowId || this.lastSelectedRowId) {
                    const targetId = targetRowId || this.lastSelectedRowId;
                    insertIndex = this.data.findIndex(row => String(row.id) === String(targetId));


                    if (insertIndex !== -1) {
                        insertIndex += 1; // 다음 위치에 삽입

                        // 같은 레벨로 설정
                        const targetRow = this.data[insertIndex - 1];
                        newRow.level = targetRow.level || 0;
                        newRow.parentPath = targetRow.parentPath || '';
                        parentId = this.getParentId(targetRow);
                    } else {
                        insertIndex = this.data.length; // 못찾으면 맨 아래에
                    }
                } else {
                    insertIndex = this.data.length; // 선택된 행이 없으면 맨 아래에
                }
                break;

            case 'child': // 선택된 행의 자식으로
                if (targetRowId || this.lastSelectedRowId) {
                    const targetId = targetRowId || this.lastSelectedRowId;
                    const targetIndex = this.data.findIndex(row => this.getNodeKey(row) === targetId);
                    if (targetIndex !== -1) {
                        const targetRow = this.data[targetIndex];

                        // 자식 레벨로 설정
                        newRow.level = (targetRow.level || 0) + 1;
                        newRow.parentPath = targetRow.parentPath ?
                            `${targetRow.parentPath}/${this.getNodeKey(targetRow)}` :
                            this.getNodeKey(targetRow);
                        parentId = this.getNodeKey(targetRow);

                        // 자식이 있는 마지막 위치 찾기
                        insertIndex = this.findLastChildIndex(targetIndex) + 1;

                        // 부모 행의 childYn을 'Y'로 변경
                        if (this.isTreeMode) {
                            targetRow.childYn = 'Y';
                            if (!targetRow.children) targetRow.children = [];
                        }
                    } else {
                        insertIndex = this.data.length;
                    }
                } else {
                    insertIndex = this.data.length;
                }
                break;

            default:
                insertIndex = this.data.length;
        }

        // 배열에 삽입
        if (insertIndex >= 0 && insertIndex <= this.data.length) {
            this.data.splice(insertIndex, 0, newRow);
        } else {
            this.data.push(newRow);
        }

        this.renderTable();

        // 저장 기능이 활성화된 경우만 추가된 행 추적
        if (this.isSaveEnabled) {
            this.modifiedRows.add(this.getNodeKey(newRow));
        }

        // 새로 추가된 행에 포커스
        setTimeout(() => {
            const $newRow = $(`#${this.gridId}-body tr[data-id="${newId}"]`);
            const $firstEditableField = $newRow.find('input[data-field]:not(.pickr), textarea[data-field], [contenteditable][data-field]').first();
            if ($firstEditableField.length) {
                $firstEditableField.focus();
                if ($firstEditableField.is('input, textarea')) {
                    $firstEditableField.select();
                } else if ($firstEditableField.is('[contenteditable]')) {
                    const element = $firstEditableField[0];
                    if (element.textContent) {
                        const range = document.createRange();
                        range.selectNodeContents(element);
                        const selection = window.getSelection();
                        selection.removeAllRanges();
                        selection.addRange(range);
                    }
                }
            }

            // 새로 추가된 행을 선택 상태로 만들기
            this.selectRow(newId, false);

        }, 100);

        return {
            newId: newId,
            parentId: parentId
        };
    }


 // 헬퍼 함수들
 getParentId(row) {
     if (!row.parentPath) return null;
     const pathParts = row.parentPath.split('/');
     return pathParts[pathParts.length - 1];
 }

 findLastChildIndex(parentIndex) {
     const parentRow = this.data[parentIndex];
     const parentLevel = parentRow.level || 0;

     for (let i = parentIndex + 1; i < this.data.length; i++) {
         const currentLevel = this.data[i].level || 0;
         if (currentLevel <= parentLevel) {
             return i - 1; // 이전 인덱스가 마지막 자식
         }
     }
     return this.data.length - 1; // 배열 끝까지 자식
 }

    addChildToSelected() {
        // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;

        const checkedRows = $(`#${this.gridId}-body input.row-check:checked`);

        if (checkedRows.length === 0) {
            alert("자식을 추가할 행을 선택해주세요.");
            return;
        }

        if (checkedRows.length > 1) {
            alert("하나의 행만 선택해주세요.");
            return;
        }

        const selectedRow = checkedRows.closest("tr");
        const parentId = String(selectedRow.data("id")); // 문자열로 변환

      //  this.addChildRow(parentId);

        // ★ 수정: addChildRow의 반환값을 그대로 반환 (이미 parentId 포함)
        return this.addChildRow(parentId);
    }

    addChildRow(parentId) {
    	 // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;

        const result = this.findNodeById(parentId);
        if (!result || !result.node) return;

        const parent = result.node;
        //const newChildId = Date.now().toString() + Math.floor(Math.random() * 1000).toString();
        const newChildId = 'N_' + Date.now().toString() + Math.floor(Math.random() * 1000).toString();


        const newChild = {
            id: newChildId,
            checked: false,
            ...this.defaultFields, // 기본 필드들 적용

            level: parent.level + 1,
            //parentPath: parent.parentPath + parent.id + '/',
            parentPath: parent.parentPath + (parent[this.urlParamKey] || parent.id) + '/',
            childrenLoaded: true,
            children: [],
            treeExpanded: false,
            isNew: true,
            childYn: 'N'
        };

        if (!parent.children) {
            parent.children = [];
        }

        // 자식행 추가 위치에 따라 처리
        if (this.addChildPosition === 'bottom') {
            parent.children.push(newChild);
        } else {
            parent.children.unshift(newChild);
        }

        parent.childrenLoaded = true;
        parent.treeExpanded = true;

        this.renderTable();

        // 저장 기능이 활성화된 경우만 추가된 행 추적
        if (this.isSaveEnabled) {
           // this.addedRows.add(this.getNodeKey(newChild));
            this.modifiedRows.add(this.getNodeKey(newChild));
        }

        // 새로 추가된 자식 행의 첫 번째 편집 가능한 필드에 포커스
        setTimeout(() => {
            const $newRow = $(`#${this.gridId}-body tr[data-id="${newChildId}"]`);
            const $firstEditableField = $newRow.find('input[data-field], textarea[data-field], [contenteditable][data-field]').first();

            if ($firstEditableField.length) {
                $firstEditableField.focus();

                // input이나 textarea의 경우 텍스트 선택
                if ($firstEditableField.is('input, textarea')) {
                    $firstEditableField.select();
                }
                // contenteditable의 경우 텍스트 선택
                else if ($firstEditableField.is('[contenteditable]')) {
                    const element = $firstEditableField[0];
                    if (element.textContent) {
                        const range = document.createRange();
                        range.selectNodeContents(element);
                        const selection = window.getSelection();
                        selection.removeAllRanges();
                        selection.addRange(range);
                    }
                }
            }
        }, 100);

        // ★ 수정: 새 자식 ID와 부모 ID 반환
        return {
            newId: newChildId,
            parentId: String(parent.id), // 부모 ID도 문자열로 반환
            [this.urlParamKey]: parent[this.urlParamKey]
        };
    }

    getNodeKey(node) {
        return node.level === 0 ? "p-" + node.id : "c-" + node.id;
    }

    deleteSelected2() {
        const checkedRows = $(`#${this.gridId}-body input.row-check:checked`).closest("tr");

        if (checkedRows.length === 0) {
            alert("삭제할 행을 선택해주세요.");
            return;
        }

        const nodesToDelete = [];

        checkedRows.each((index, element) => {  // arrow function으로 변경
            const $row = $(element);
            const id = String($row.data("id"));
            const level = parseInt($row.data("level"));

            // findNodeById로 실제 노드 데이터에서 urlParamKey 값 가져오기
            const result = this.findNodeById(id);
            const paramValue = result && result.node ? result.node[this.urlParamKey] : null;


            nodesToDelete.push({
                id: id,
                level: level,
                [this.urlParamKey]: paramValue
            });
        });


        for (let i = 0; i < nodesToDelete.length; i++) {
            const nodeToDelete = nodesToDelete[i];
            this.deleteNodeRecursively(nodeToDelete.id);
        }

        this.renderTable();
       if (this.isPagingEnabled) {
	        this.totalCount -= nodesToDelete.length;
	        this.updatePageInfo();
       }

       this.toggleAll(false);
    }


    deleteSelected() {

    	if(!confirm("삭제하시겠습니까?")) return;

        const checkedRows = $(`#${this.gridId}-body input.row-check:checked`).closest("tr");

        if (checkedRows.length === 0) {
            alert("삭제할 행을 선택해주세요.");
            return;
        }

        // 엑셀 모드인지 확인
        if (this.isExcelMode) {
            // 엑셀 모드에서의 삭제 처리
            this.deleteExcelRows(checkedRows);
        } else {
            // 일반 모드에서의 삭제 처리 (기존 로직)
            const nodesToDelete = [];
            /*
            checkedRows.each(function () {
                const id = $(this).data("id").toString();
                const level = parseInt($(this).data("level"));
                nodesToDelete.push({ id: id, level: level });
            });
            */

            checkedRows.each((index, element) => {  // arrow function으로 변경
                const $row = $(element);
                const id = $row.data("id").toString();
                const level = parseInt($row.data("level"));

                // findNodeById로 실제 노드 데이터에서 urlParamKey 값 가져오기
                const result = this.findNodeById(id);
                const paramValue = result && result.node ? result.node[this.urlParamKey] : null;

                nodesToDelete.push({
                    id: id,
                    level: level,
                    [this.urlParamKey]: paramValue
                });

            });


            console.log("nodesToDelete..."+JSON.stringify(nodesToDelete));


            $.ajax({
                url: this.urls.deleteUrl,
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify({deleteNodes : nodesToDelete}),
                success: (res) => {

                	toastr["success"](`${checkedRows.length}개 행이 삭제되었습니다.`);

                	this.toggleAll(false);


                    this.fetchData(this.currentPage);
                },
                error: (xhr, status, error) => {
                    alert("삭제에 실패했습니다.");
                }
            });
        }
    }

    // 엑셀 데이터 삭제를 위한 새로운 메서드 추가
    deleteExcelRows(checkedRows) {
        const rowsToDelete = [];
        const deletedIndices = [];

        // 삭제할 행들의 인덱스 수집
        checkedRows.each((index, row) => {
            const $row = $(row);
            const nodeId = $row.data("id").toString();

            // 현재 그리드 데이터에서 해당 행 찾기
            const dataIndex = this.data.findIndex(item => item.id === nodeId);
            if (dataIndex !== -1) {
                deletedIndices.push(dataIndex);
                rowsToDelete.push(this.data[dataIndex]);
            }
        });

        // 인덱스를 내림차순으로 정렬하여 뒤에서부터 삭제 (인덱스 변경 방지)
        deletedIndices.sort((a, b) => b - a);

        // 그리드 데이터에서 삭제
        deletedIndices.forEach(index => {
            this.data.splice(index, 1);
        });

        // 엑셀 원본 데이터에서도 삭제
        deletedIndices.forEach(index => {
            if (this.excelData[index]) {
                this.excelData.splice(index, 1);
            }
        });

        // 삭제된 행과 관련된 밸리데이션 오류 제거
        this.removeValidationErrorsForDeletedRows(deletedIndices);

        // 테이블 다시 렌더링
        this.renderTable();

        // 페이징 정보 업데이트
        if (this.isPagingEnabled) {
            this.totalCount = this.data.length;
            this.totalPages = Math.ceil(this.totalCount / this.pageSize);
            this.updatePageInfo();
        }

        // 오류 셀 강조 다시 적용
        if (this.excelValidationErrors.length > 0) {
            this.highlightErrorCells();
        }
        toastr["success"](`${rowsToDelete.length}개 행이 삭제되었습니다.`);
    }

    // 삭제된 행의 밸리데이션 오류를 제거하는 메서드
    removeValidationErrorsForDeletedRows(deletedIndices) {
        if (!this.excelErrorCells || this.excelErrorCells.length === 0) {
            return;
        }

        // 삭제된 행의 오류 제거
        this.excelErrorCells = this.excelErrorCells.filter(errorCell => {
            return !deletedIndices.includes(errorCell.rowIndex);
        });

        // 남은 행들의 인덱스 재정렬
        // 삭제된 인덱스보다 큰 인덱스들을 조정
        deletedIndices.sort((a, b) => a - b); // 오름차순 정렬

        this.excelErrorCells.forEach(errorCell => {
            let adjustment = 0;
            deletedIndices.forEach(deletedIndex => {
                if (errorCell.rowIndex > deletedIndex) {
                    adjustment++;
                }
            });
            errorCell.rowIndex -= adjustment;
        });

        // 전체 유효성 검사 오류 목록 업데이트
        this.updateValidationErrors();


    }


    deleteNodeRecursively(nodeId) {
        // ID를 문자열로 변환하여 처리
        const targetId = String(nodeId);

        const deleteFromArray = (nodes, parentArray = null, parentIndex = -1) => {
            for (let i = nodes.length - 1; i >= 0; i--) {
                const node = nodes[i];
                if (String(node.id) === targetId) {
                    // 저장 기능이 활성화된 경우만 삭제된 행 추적

                    if (this.isSaveEnabled) {
                        const nodeKey = this.getNodeKey(node);
                        if (!this.modifiedRows.has(nodeKey)) {
                            // nodeKey에 urlParamKey 값을 함께 저장
                            this.deletedRows.add(`${nodeKey}|${node[this.urlParamKey]}`);
                        } else {
                            this.modifiedRows.delete(nodeKey);
                        }
                    }



                    if (this.isTreeMode && node.children && node.children.length > 0) {
                        this.markChildrenAsDeleted(node.children);
                    }

                    if (parentArray === null) {
                        this.data.splice(i, 1);
                    } else {
                        nodes.splice(i, 1);
                    }
                    return true;
                }

                if (this.isTreeMode && node.children && node.children.length > 0) {
                    if (deleteFromArray(node.children, nodes, i)) {
                        return true;
                    }
                }
            }
            return false;
        };

        deleteFromArray(this.data);
    }

    markChildrenAsDeleted(children) {
        // 저장 기능이 활성화된 경우만 실행
        if (!this.isSaveEnabled) return;

        for (let i = 0; i < children.length; i++) {
            const child = children[i];
            const childKey = this.getNodeKey(child);
            if (!this.modifiedRows.has(childKey)) {
                //this.deletedRows.add(childKey);
                // childKey에 urlParamKey 값을 함께 저장
                this.deletedRows.add(`${childKey}|${child[this.urlParamKey]}`);
            } else {
                this.modifiedRows.delete(childKey);
            }

            if (this.isTreeMode && child.children && child.children.length > 0) {
                this.markChildrenAsDeleted(child.children);
            }
        }
    }

    /*
    toggleAll2(checked) {
        $(`#${this.gridId}-body .row-check`).prop("checked", checked);

        // ★ 헤더 체크박스도 함께 업데이트
        $(`#${this.gridId}-container .check-all`).prop('checked', checked);

        // ★ 추가: 데이터에서도 체크 상태 업데이트
        const updateNodeChecked = (nodes) => {
            nodes.forEach(node => {
                node.checked = checked;
                if (this.isTreeMode && node.children && node.children.length > 0) {
                    updateNodeChecked(node.children);
                }
            });
        };

        updateNodeChecked(this.data);


    }
    */
    toggleAll(checked) {
        if (checked && this.checkCount) {
            // 전체 선택시 개수 제한이 있으면 제한된 개수만 체크
            const $allCheckboxes = $(`#${this.gridId}-body .row-check`);

            if ($allCheckboxes.length <= this.checkCount) {
                // 전체 개수가 제한 개수 이하면 모두 체크
                $allCheckboxes.prop('checked', true);

                // ★ 데이터에서도 체크 상태 업데이트
                const updateNodeChecked = (nodes) => {
                    nodes.forEach(node => {
                        node.checked = true;
                        if (this.isTreeMode && node.children && node.children.length > 0) {
                            updateNodeChecked(node.children);
                        }
                    });
                };
                updateNodeChecked(this.data);

            } else {
                // 제한 개수만큼만 체크
                $allCheckboxes.slice(0, this.checkCount).prop('checked', true);

                // ★ 데이터에서도 제한된 개수만 체크 상태 업데이트
                let checkedCount = 0;
                const updateLimitedNodeChecked = (nodes) => {
                    nodes.forEach(node => {
                        if (checkedCount < this.checkCount) {
                            node.checked = true;
                            checkedCount++;

                            if (this.isTreeMode && node.children && node.children.length > 0) {
                                updateLimitedNodeChecked(node.children);
                            }
                        } else {
                            node.checked = false;
                            if (this.isTreeMode && node.children && node.children.length > 0) {
                                const setChildrenUnchecked = (childNodes) => {
                                    childNodes.forEach(child => {
                                        child.checked = false;
                                        if (child.children && child.children.length > 0) {
                                            setChildrenUnchecked(child.children);
                                        }
                                    });
                                };
                                setChildrenUnchecked(node.children);
                            }
                        }
                    });
                };
                updateLimitedNodeChecked(this.data);

                //alert(`최대 ${this.checkCount}개까지만 선택할 수 있어 처음 ${this.checkCount}개 항목을 선택했습니다.`);
            }
        } else if (!checked) {
            // 전체 해제
            $(`#${this.gridId}-body .row-check`).prop('checked', false);

            // ★ 데이터에서도 체크 해제
            const updateNodeChecked = (nodes) => {
                nodes.forEach(node => {
                    node.checked = false;
                    if (this.isTreeMode && node.children && node.children.length > 0) {
                        updateNodeChecked(node.children);
                    }
                });
            };
            updateNodeChecked(this.data);

        } else {
            // 개수 제한이 없으면 기존대로
            $(`#${this.gridId}-body .row-check`).prop('checked', checked);

            // ★ 데이터에서도 체크 상태 업데이트
            const updateNodeChecked = (nodes) => {
                nodes.forEach(node => {
                    node.checked = checked;
                    if (this.isTreeMode && node.children && node.children.length > 0) {
                        updateNodeChecked(node.children);
                    }
                });
            };
            updateNodeChecked(this.data);
        }

        // ★ 헤더 체크박스도 함께 업데이트
        this.updateHeaderCheckbox();
    }




    /*
    // ★ 새로 추가: 개별 체크박스 상태 변경 시 헤더 체크박스 업데이트
    updateHeaderCheckbox2() {
        const totalCheckboxes = $(`#${this.gridId}-body .row-check`).length;
        const checkedCheckboxes = $(`#${this.gridId}-body .row-check:checked`).length;

        // 모든 체크박스가 체크되어 있으면 헤더도 체크, 하나라도 해제되어 있으면 헤더 해제
        const allChecked = totalCheckboxes > 0 && totalCheckboxes === checkedCheckboxes;
        $(`#${this.gridId}-container .check-all`).prop('checked', allChecked);
    }
    */

    updateHeaderCheckbox() {
        const $container = $(`#${this.gridId}-container`);
        const $allCheckboxes = $container.find('.row-check');
        const $checkedCheckboxes = $container.find('.row-check:checked');
        const $headerCheckbox = $container.find('.check-all');

        const checkedCount = $checkedCheckboxes.length;
        const totalCount = $allCheckboxes.length;

        // 개수 제한이 있는 경우
        if (this.checkCount) {
            const maxSelectableCount = Math.min(this.checkCount, totalCount);

            /*
            if (checkedCount === 0) {
                $headerCheckbox.prop('checked', false).prop('indeterminate', false);
            } else if (checkedCount === maxSelectableCount) {
               // $headerCheckbox.prop('checked', true).prop('indeterminate', false);
            } else {
               // $headerCheckbox.prop('checked', false).prop('indeterminate', true);
            }
            */

            // 개수 제한 정보 표시
         //   console.log(`체크된 항목: ${checkedCount}개 / 최대 ${this.checkCount}개`);
           // $(`#${this.gridId}-checked-count`).text(`선택: ${checkedCount}/${this.checkCount}`);
        } else {
            // 기존 로직 (개수 제한 없음)
            if (checkedCount === 0) {
                $headerCheckbox.prop('checked', false).prop('indeterminate', false);

                // ★ 모든 선택 해제시 selectedRows도 초기화
                if (this.selectedRows) {
                    this.selectedRows.clear();
                }
                this.lastSelectedRowId = null;
                $(`#${this.gridId}-container tr`).removeClass('selected');

            } else if (checkedCount === totalCount) {
                $headerCheckbox.prop('checked', true).prop('indeterminate', false);
            } else {
                $headerCheckbox.prop('checked', false).prop('indeterminate', true);
            }

           // console.log(`체크된 항목: ${checkedCount}개 / 전체 ${totalCount}개`);
           // $(`#${this.gridId}-checked-count`).text(`선택된 항목: ${checkedCount}개`);
        }
    }



    trackEdit(nodeId, el, field) {
        let value;

        // 요소 타입에 따라 값 추출
        if (el.type === 'checkbox') {
            value = el.checked;
        } else if (el.type === 'radio') {
            // 라디오버튼이 선택되지 않았다면 처리하지 않음
            if (!el.checked) {
                return;
            }
            value = el.value;
        } else if (el.value !== undefined) {
            value = el.value;
        } else {
            value = (el.innerText || el.textContent || "").trim();
        }


        const result = this.findNodeById(nodeId);

        if (result && result.node) {
            result.node[field] = value;

            // 엑셀 모드인 경우 원본 엑셀 데이터도 업데이트
            if (this.isExcelMode && result.node.isExcel) {
                const nodeIndex = this.data.indexOf(result.node);
                if (nodeIndex >= 0 && nodeIndex < this.excelData.length) {
                    this.excelData[nodeIndex][field] = value;
                }

                // ★ 수정: 이전 오류 상태 먼저 확인
                const hadError = this.excelErrorCells.some(errorCell =>
                    errorCell.rowIndex === nodeIndex && errorCell.field === field
                );

                // 기존 오류를 먼저 제거
                this.excelErrorCells = this.excelErrorCells.filter(errorCell => {
                    return !(errorCell.rowIndex === nodeIndex && errorCell.field === field);
                });

                // 실시간 유효성 검사
                const validationError = this.validateSingleCell(nodeId, field, value);
                const $element = $(el);
                const $td = $element.closest('td');

                if (validationError) {
                    // 오류가 있으면 td에만 스타일 추가하고 오류 목록에 추가
                    $td.addClass('excel-error-cell');

                    // 새 오류 추가
                    this.excelErrorCells.push({
                        rowIndex: nodeIndex,
                        field: field,
                        error: validationError
                    });


                } else {
                    // 오류가 없으면 스타일 제거
                    $td.removeClass('excel-error-cell');
                    $element.removeClass('excel-error-cell');

                }

                // 전체 오류 목록 업데이트
                this.updateValidationErrors();
            }




            // 저장 기능이 활성화된 경우만 수정된 행 추적
            if (this.isSaveEnabled) {
                const nodeKey = this.getNodeKey(result.node);
                this.modifiedRows.add(nodeKey);
            }
        }
    }





    // ★  전체 유효성 검사 오류 목록 업데이트
    /*
    updateValidationErrors() {
        this.excelValidationErrors = [...new Set(this.excelErrorCells.map(errorCell => errorCell.error))];


        // ★ 수정: 오류가 있을 때만 토스트 표시

        if (this.excelValidationErrors.length > 0) {

            let errorMessage = "엑셀 데이터에 오류가 있습니다.<br><br>";
            this.excelValidationErrors.forEach(error => {
                errorMessage += `${error}\n`;
            });

            let type  = 'info';
            let timeOut  = 0;
            this.showToast(errorMessage,  type = type, timeOut = timeOut);

        }else{
        	 let errorMessage="모든 밸리데이션 오류가 해결되었습니다."
        	 let type  = 'success';
             let timeOut  = 10;
             this.showToast(errorMessage,  type = type, timeOut = timeOut);
        }

    }*/

 // ★  전체 유효성 검사 오류 목록 업데이트
    updateValidationErrors() {
        // ★ 수정: 셀 오류만이 아니라 전체 유효성 검사를 다시 실행
        if (this.isExcelMode && this.excelData.length > 0) {
            // 현재 엑셀 데이터로 전체 유효성 검사 재실행
            const headers = Object.keys(this.excelData[0]);
            const rows = this.excelData.map(rowData => headers.map(header => rowData[header] || ''));

            const validationResult = this.validateExcelData(headers, rows);
            this.excelValidationErrors = validationResult.errors || [];
            this.excelErrorCells = validationResult.errorCells || [];
        } else {
            // 기존 방식 (셀 오류만 업데이트)
            this.excelValidationErrors = [...new Set(this.excelErrorCells.map(errorCell => errorCell.error))];
        }

        // ★ 수정: 오류가 있을 때만 토스트 표시
        if (this.excelValidationErrors.length > 0) {
            let errorMessage = "엑셀 데이터에 오류가 있습니다.<br><br>";
            this.excelValidationErrors.forEach(error => {
                errorMessage += `${error}`;
            });

            let type = 'info';
            let timeOut = 0;
            this.showToast(errorMessage, type = type, timeOut = timeOut);
        } else {
            let errorMessage = "모든 밸리데이션 오류가 해결되었습니다."
            let type = 'success';
            let timeOut = 10;
            this.showToast(errorMessage, type = type, timeOut = timeOut);
        }
    }


    showToast(message, type = 'info', timeOut = 0) {
        if (message === this.lastToastrMessage) {
            return;
        }
        toastr.remove();
        this.lastToastrMessage = message;

        this.errorToastId = toastr[type](message, null, {
            closeButton: true,
            timeOut: timeOut,
            extendedTimeOut: 0,
            tapToDismiss: false,
        });

        this.highlightErrorCells();


    }

    getModifiedRows(){

    	const excludeFields = ['children', 'childrenLoaded', 'treeExpanded', 'isNew'];

	   const nodesToUpdate = [];

	   this.modifiedRows.forEach((key) => {
           const id = key.slice(2); // 이미 문자열이므로 그대로 사용
           const result = this.findNodeById(id);
           if (result && result.node) {
               const node = result.node;
               const nodeData = {};

               // 모든 필드를 동적으로 포함 (제외 필드 제외)
               Object.keys(node).forEach(field => {
                   if (!excludeFields.includes(field)) {
                       nodeData[field] = node[field];
                   }
               });

               // urlParamKey가 누락되지 않도록 명시적으로 확인
               if (this.urlParamKey && node[this.urlParamKey]) {
                   nodeData[this.urlParamKey] = node[this.urlParamKey];
               }

               nodesToUpdate.push(nodeData);
           }
       });

    	return nodesToUpdate;
    }

    getDeletedRows(){
    	  const nodesToDelete = [];
    	  this.deletedRows.forEach((key) => {
              const [nodeKey, paramValue] = key.split('|'); // '|'로 분리
              nodesToDelete.push(paramValue);
          });

    	  return nodesToDelete;
    }

    saveChanges() {
        // 저장 기능이 비활성화된 경우 실행하지 않음
        if (!this.isSaveEnabled) {
            alert("저장 기능이 비활성화되어 있습니다.");
            return;
        }

        // 엑셀 모드인 경우 모든 엑셀 데이터를 저장 start
        if (this.isExcelMode) {
            this.saveExcelData();
            return;
        } //엑셀 모드인 경우 모든 엑셀 데이터를 저장 end

        if (this.addedRows.size === 0 && this.modifiedRows.size === 0 && this.deletedRows.size === 0) {
            alert("저장할 변경사항이 없습니다.");
            return;
        }

        const nodesToAdd = [];
        const nodesToUpdate = [];
        const nodesToDelete = [];

        // 트리 구조 관련 내부 필드들 (저장 시 제외)
        const excludeFields = ['children', 'childrenLoaded', 'treeExpanded', 'isNew'];

        this.addedRows.forEach((key) => {
            const id = key.slice(2); // 이미 문자열이므로 그대로 사용
            const result = this.findNodeById(id);
            if (result && result.node) {
                const node = result.node;
                const nodeData = {};

                // 모든 필드를 동적으로 포함 (제외 필드 제외)
                Object.keys(node).forEach(field => {
                    if (!excludeFields.includes(field)) {
                        nodeData[field] = node[field];
                    }
                });

                nodesToAdd.push(nodeData);
            }
        });

        this.modifiedRows.forEach((key) => {
            const id = key.slice(2); // 이미 문자열이므로 그대로 사용
            const result = this.findNodeById(id);
            if (result && result.node) {
                const node = result.node;
                const nodeData = {};

                // 모든 필드를 동적으로 포함 (제외 필드 제외)
                Object.keys(node).forEach(field => {
                    if (!excludeFields.includes(field)) {
                        nodeData[field] = node[field];
                    }
                });

                nodesToUpdate.push(nodeData);
            }
        });

        /*
        this.deletedRows.forEach((key) => {
            const id = key.slice(2); // 이미 문자열이므로 그대로 사용
            nodesToDelete.push(id);
        });
        */
        this.deletedRows.forEach((key) => {
            const [nodeKey, paramValue] = key.split('|'); // '|'로 분리
            nodesToDelete.push(paramValue);
        });

       // console.log(`[${this.gridId}] 추가된 노드:`, nodesToAdd);
       // console.log(`[${this.gridId}] 수정된 노드:`, nodesToUpdate);
       // console.log(`[${this.gridId}] 삭제된 노드:`, nodesToDelete);

        // 실제 서버 전송
        const payload = {
          //  addedNodes: nodesToAdd,
            updatedNodes: nodesToUpdate,
            deletedNodes: nodesToDelete
        };

        $.ajax({
        	url: this.urls.saveUrl,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: (res) => {

               	toastr["success"]("저장 성공");

                this.addedRows.clear();
                this.modifiedRows.clear();
                this.deletedRows.clear();
                this.fetchData(this.currentPage);
            },
            error: (xhr, status, error) => {
                console.log(`[${this.gridId}] 저장 실패:`, error);
                toastr["error"]("저장에 실패했습니다.");
            }
        });

    }

    renderTemplate(templateId, data, mapData, selectOption, reverseIndex) {
        const template = document.getElementById(templateId).innerHTML;

        // Format 헬퍼 함수들 (기존과 동일)
        const formatHelpers = {
            // 날짜 포맷팅
            date: function(value, format = 'YYYY-MM-DD') {
                if (!value) return '';
                let date;
                // 숫자 형태의 날짜 처리 (YYYYMMDD 또는 YYYYMMDDHHMISS 등)
                const numStr = String(value);
                if (/^\d{8,}$/.test(numStr)) {
                    // YYYYMMDD 또는 YYYYMMDDHHMISS 형태로 파싱
                    const year = parseInt(numStr.substring(0, 4));
                    const month = parseInt(numStr.substring(4, 6));
                    const day = parseInt(numStr.substring(6, 8));

                    // 시간 정보 추출 (있는 경우)
                    let hours = 0, minutes = 0, seconds = 0;
                    if (numStr.length >= 10) {
                        hours = parseInt(numStr.substring(8, 10)) || 0;
                    }
                    if (numStr.length >= 12) {
                        minutes = parseInt(numStr.substring(10, 12)) || 0;
                    }
                    if (numStr.length >= 14) {
                        seconds = parseInt(numStr.substring(12, 14)) || 0;
                    }

                    // 유효한 날짜인지 검증
                    if (year >= 1900 && year <= 2100 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                        date = new Date(year, month - 1, day, hours, minutes, seconds);
                    } else {
                        date = new Date(value);
                    }
                } else {
                    date = new Date(value);
                }

                if (isNaN(date.getTime())) return value; // 유효하지 않은 날짜면 원본 반환

                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                const hours = String(date.getHours()).padStart(2, '0');
                const minutes = String(date.getMinutes()).padStart(2, '0');
                const seconds = String(date.getSeconds()).padStart(2, '0');

                return format
                    .replace('YYYY', year)
                    .replace('YY', String(year).slice(-2))
                    .replace('MM', month)
                    .replace('M', String(date.getMonth() + 1))
                    .replace('DD', day)
                    .replace('D', String(date.getDate()))
                    .replace('HH', hours)
                    .replace('H', String(date.getHours()))
                    .replace('mm', minutes)
                    .replace('m', String(date.getMinutes()))
                    .replace('ss', seconds)
                    .replace('s', String(date.getSeconds()));
            },

            // 금액 포맷팅
            currency: function(value, options = {}) {
                if (value === null || value === undefined || value === '') return '';

                const num = parseFloat(value);
                if (isNaN(num)) return value;

                const {
                    locale = 'ko-KR',
                    currency = 'KRW',
                    minimumFractionDigits = 0,
                    maximumFractionDigits = 0
                } = options;

                return new Intl.NumberFormat(locale, {
                    style: 'currency',
                    currency: currency,
                    minimumFractionDigits: minimumFractionDigits,
                    maximumFractionDigits: maximumFractionDigits
                }).format(num);
            },

            // 숫자 포맷팅 (천단위 콤마)
            number: function(value, decimals = 0) {
                if (value === null || value === undefined || value === '') return '';

                const num = parseFloat(value);
                if (isNaN(num)) return value;

                return new Intl.NumberFormat('ko-KR', {
                    minimumFractionDigits: decimals,
                    maximumFractionDigits: decimals
                }).format(num);
            },

            // 퍼센트 포맷팅
            percent: function(value, decimals = 1) {
                if (value === null || value === undefined || value === '') return '';

                const num = parseFloat(value);
                if (isNaN(num)) return value;

                return new Intl.NumberFormat('ko-KR', {
                    style: 'percent',
                    minimumFractionDigits: decimals,
                    maximumFractionDigits: decimals
                }).format(num / 100);
            },

            // 문자열 포맷팅
            string: function(value, format) {
                if (!value) return '';

                switch(format) {
                    case 'upper':
                        return String(value).toUpperCase();
                    case 'lower':
                        return String(value).toLowerCase();
                    case 'title':
                        return String(value).replace(/\w\S*/g, (txt) =>
                            txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());
                    default:
                        return value;
                }
            }
        };

        // 값을 파싱하는 공통 헬퍼 함수
        function parseValue(val) {
            val = val.trim();

            // 문자열 리터럴 처리 ('...' 또는 "...")
            if ((val.startsWith("'") && val.endsWith("'")) ||
                (val.startsWith('"') && val.endsWith('"'))) {
                let content = val.slice(1, -1);

                // 템플릿 리터럴 처리 ${variable}
                content = content.replace(/\$\{(\w+)\}/g, function(match, varName) {
                    return data[varName] !== undefined ? data[varName] : '';
                });

                return content;
            } else {
                // 복잡한 객체 접근 처리 (점 표기법, 대괄호 표기법)
                if (/[\.\[\]]/.test(val)) {
                    try {
                        // 점과 대괄호를 모두 처리
                        const tokens = val.match(/\w+|\[(\w+|['"][^'"]+['"])\]|\.\w+/g);
                        if (!tokens) return '';

                        let value = mapData; // mapData에서 시작
                        for (let token of tokens) {
                            if (token.startsWith('.')) {
                                // 점 표기법
                                const key = token.slice(1);
                                value = value[key];
                            } else if (token.startsWith('[')) {
                                // 대괄호 표기법
                                let key = token.slice(1, -1);
                                if (key.startsWith('"') || key.startsWith("'")) {
                                    key = key.slice(1, -1); // 정적 키
                                } else {
                                    key = data[key]; // 동적 키
                                }
                                value = value[key];
                            } else {
                                // 첫 번째 객체명 또는 일반 프로퍼티
                                value = value[token];
                            }

                            if (value === undefined) break;
                        }

                        return value !== undefined ? value : val;
                    } catch (e) {
                        console.error('객체 접근 오류:', e);
                        return val;
                    }
                }

                // 일반 변수 - data에서 먼저 찾고, 없으면 mapData에서 찾기
                if (data[val] !== undefined) {
                    return data[val];
                } else if (mapData && mapData[val] !== undefined) {
                    return mapData[val];
                }

                return val;
            }
        }


        // 1단계: each 반복문 처리
        let result = template.replace(/\{\{#each\s+(\[?\w+\]?)\}\}([\s\S]*?)\{\{\/each\}\}/g,
            function(match, arrayKey, content) {
                let array;

                // 대괄호 표기법 처리 [variableName]
                if (arrayKey.startsWith('[') && arrayKey.endsWith(']')) {
                    const variableName = arrayKey.slice(1, -1);
                    const actualKey = data[variableName];
                    array = selectOption[actualKey];
                } else {
                    // 일반 변수명 또는 selectOption에서 직접 접근
                    array = data[arrayKey] || selectOption[arrayKey];
                }

                if (!Array.isArray(array)) return '';

                return array.map(item => {
                    // 각 배열 항목에 대해 템플릿 처리
                    let itemResult = content;

                    // 항목의 속성들 처리 (this.property)
                    itemResult = itemResult.replace(/\{\{this\.(\w+)\}\}/g, function(match, prop) {
                        return item[prop] !== undefined ? item[prop] : '';
                    });

                    // 현재 항목 자체 처리 ({{this}})
                    itemResult = itemResult.replace(/\{\{this\}\}/g, item);

                    // 인덱스 처리 ({{@index}})
                    itemResult = itemResult.replace(/\{\{@index\}\}/g, array.indexOf(item));

                    return itemResult;
                }).join('');
            });

        // 2단계: 복합 조건문 파서 (AND, OR 모두 지원)
        // 복합 조건문 파싱 함수 (and, or 모두 지원)
        function parseComplexCondition(conditionStr) {
            // 'or'로 먼저 분리 (or가 and보다 우선순위가 낮음)
            const orGroups = conditionStr.split(/\s+or\s+/);

            return orGroups.map(orGroup => {
                // 각 or 그룹 내에서 'and'로 분리
                const andConditions = orGroup.trim().split(/\s+and\s+/);

                return andConditions.map(condition => {
                    condition = condition.trim();

                    // 'not equals' 패턴 확인
                    const notEqualsMatch = condition.match(/^(.+?)\s+not\s+equals\s+(.+)$/);
                    if (notEqualsMatch) {
                        return {
                            left: notEqualsMatch[1].trim(),
                            operator: 'not equals',
                            right: notEqualsMatch[2].trim()
                        };
                    }

                    // 'equals' 패턴 확인
                    const equalsMatch = condition.match(/^(.+?)\s+equals\s+(.+)$/);
                    if (equalsMatch) {
                        return {
                            left: equalsMatch[1].trim(),
                            operator: 'equals',
                            right: equalsMatch[2].trim()
                        };
                    }

                    return null;
                }).filter(Boolean);
            });
        }

        // 조건 평가 함수 (and, or 지원)
        function evaluateConditions(conditionGroups) {
            // OR 조건: 하나의 그룹이라도 true이면 전체가 true
            return conditionGroups.some(andGroup => {
                // AND 조건: 그룹 내 모든 조건이 true여야 함
                return andGroup.every(condition => {
                    const leftValue = parseValue(condition.left);
                    const rightValue = parseValue(condition.right);

                    if (condition.operator === 'equals') {
                        return leftValue == rightValue;
                    } else if (condition.operator === 'not equals') {
                        return leftValue != rightValue;
                    }

                    return false;
                });
            });
        }

        // 복합 조건문 처리 (and, or 모두 포함된 경우) - else 있는 버전
        result = result.replace(
            /\{\{#if\s+([^}]+?)\s+(and|or)\s+([^}]+?)\}\}([\s\S]*?)\{\{else\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, firstPart, operator, restPart, trueContent, falseContent) {
                const conditionStr = firstPart + ' ' + operator + ' ' + restPart;
                const conditions = parseComplexCondition(conditionStr);
                if (conditions.length > 0) {
                    const result = evaluateConditions(conditions);
                    return result ? trueContent : falseContent;
                }
                return match;
            });

        // 복합 조건문 처리 (and, or 모두 포함된 경우) - else 없는 버전
        result = result.replace(
            /\{\{#if\s+([^}]+?)\s+(and|or)\s+([^}]+?)\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, firstPart, operator, restPart, content) {
                const conditionStr = firstPart + ' ' + operator + ' ' + restPart;
                const conditions = parseComplexCondition(conditionStr);
                if (conditions.length > 0) {
                    const result = evaluateConditions(conditions);
                    return result ? content : '';
                }
                return match;
            });

        // 3단계: NOT EQUALS 조건 처리
        // {{#if key not equals 'value'}}...{{else}}...{{/if}}
        result = result.replace(
            /\{\{#if\s+([^}]+?)\s+not\s+equals\s+([^}]+?)\}\}([\s\S]*?)\{\{else\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, key, value, trueContent, falseContent) {
                const leftValue = parseValue(key);
                const rightValue = parseValue(value);
                return leftValue != rightValue ? trueContent : falseContent;
            });

        // {{#if key not equals 'value'}}...{{/if}} (else 없는 버전)
        result = result.replace(
            /\{\{#if\s+([^}]+?)\s+not\s+equals\s+([^}]+?)\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, key, value, content) {
                const leftValue = parseValue(key);
                const rightValue = parseValue(value);
                return leftValue != rightValue ? content : '';
            });

        // 4단계: 기존 EQUALS 조건 처리
        // {{#if key equals 'value'}}...{{else}}...{{/if}}
        result = result.replace(/\{\{#if\s+([^}]+?)\s+equals\s+([^}]+?)\}\}([\s\S]*?)\{\{else\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, key, value, trueContent, falseContent) {
                const leftValue = parseValue(key);
                const rightValue = parseValue(value);
                return leftValue == rightValue ? trueContent : falseContent;
            });

        // merge 조건 처리 (기존)
        result = result.replace(/\{\{#if\s+mergeFirst\s+(\w+)\}\}([\s\S]*?)\{\{else\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, field, trueContent, falseContent) {
                const mergeInfo = data.mergeInfo && data.mergeInfo[field];
                return (!mergeInfo || mergeInfo.isFirst) ? trueContent : falseContent;
            });

        // merge first 단독 조건 (기존)
        result = result.replace(/\{\{#if\s+mergeFirst\s+(\w+)\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, field, content) {
                const mergeInfo = data.mergeInfo && data.mergeInfo[field];
                return (!mergeInfo || mergeInfo.isFirst) ? content : '';
            });

        // {{#if key equals 'value'}}...{{/if}} (else 없는 버전)
        result = result.replace(/\{\{#if\s+([^}]+?)\s+equals\s+([^}]+?)\}\}([\s\S]*?)\{\{\/if\}\}/g,
            function(match, key, value, content) {
                const leftValue = parseValue(key);
                const rightValue = parseValue(value);
                return leftValue == rightValue ? content : '';
            });

        // 5단계: format 함수 처리 (기존과 동일)
        result = result.replace(/\{\{format\s+([a-zA-Z_$][a-zA-Z0-9_$.]*)\s+["']([a-zA-Z]+)["'](?:\s+([^}]+))?\}\}/g,
            function(match, variable, formatType, formatOptions) {
                // 변수 값 가져오기
                let value = data[variable];

                // 복잡한 객체 접근 처리 (점 표기법)
                if (variable.includes('.')) {
                    const keys = variable.split('.');
                    value = data;
                    for (let key of keys) {
                        if (value && typeof value === 'object') {
                            value = value[key];
                        } else {
                            value = undefined;
                            break;
                        }
                    }
                }

                if (value === undefined || value === null) return '';

                // 포맷 옵션 파싱
                let options = {};
                if (formatOptions) {
                    try {
                        formatOptions.replace(/(\w+)=["']([^"']+)["']/g, function(match, key, val) {
                            options[key] = val;
                        });
                    } catch (e) {
                        console.warn('포맷 옵션 파싱 오류:', formatOptions, e);
                    }
                }

                // 포맷 헬퍼 호출
                if (formatHelpers[formatType]) {
                    let result;
                    if (formatType === 'date') {
                        result = formatHelpers[formatType](value, options.pattern || 'YYYY-MM-DD');
                    } else if (formatType === 'currency') {
                        result = formatHelpers[formatType](value, options);
                    } else if (formatType === 'number') {
                        result = formatHelpers[formatType](value, parseInt(options.decimals) || 0);
                    } else if (formatType === 'percent') {
                        result = formatHelpers[formatType](value, parseInt(options.decimals) || 1);
                    } else if (formatType === 'string') {
                        result = formatHelpers[formatType](value, options.case || 'default');
                    }
                    return result;
                }

                return value;
            });

        // 6단계: 일반 변수 처리 (기존과 동일)
        result = result.replace(/\{\{([^#\/][^}]*?)\}\}/g, function(match, expression) {
            // 공백 제거
            expression = expression.trim();

            // 빈 문자열 체크
            if (!expression) return '';

            // format 처리는 이미 완료되었으므로 건너뛰기
            if (expression.startsWith('format')) {
                return match;
            }

            // 단순 변수인 경우 (연산자가 없으면)
            if (!/[\+\-\*\/\%\(\)\<\>\=\!]/.test(expression)) {
                // reverseIndex 처리
                if (expression === 'reverseIndex') {
                    return reverseIndex !== undefined ? reverseIndex : '';
                }

                // 복잡한 객체 접근 처리 (점 표기법, 대괄호 표기법)
                if (/[\.\[\]]/.test(expression)) {
                    try {
                        // 점과 대괄호를 모두 처리
                        const tokens = expression.match(/\w+|\[(\w+|['"][^'"]+['"])\]|\.\w+/g);
                        if (!tokens) return '';

                        let value = mapData;
                        for (let token of tokens) {
                            if (token.startsWith('.')) {
                                // 점 표기법
                                const key = token.slice(1);
                                value = value[key];
                            } else if (token.startsWith('[')) {
                                // 대괄호 표기법
                                let key = token.slice(1, -1);
                                if (key.startsWith('"') || key.startsWith("'")) {
                                    key = key.slice(1, -1); // 정적 키
                                } else {
                                    key = data[key]; // 동적 키
                                }
                                value = value[key];
                            } else {
                                // 첫 번째 객체명 또는 일반 프로퍼티
                                value = value[token];
                            }

                            if (value === undefined) break;
                        }

                        return value !== undefined ? value : '';
                    } catch (e) {
                        console.error('객체 접근 오류:', e);
                        return '';
                    }
                }

                // 단순 변수 처리
                return data[expression] !== undefined ? data[expression] : '';
            }

            // 계산식 처리
            try {
                // 변수를 실제 값으로 치환
                let processedExpression = expression.replace(/\b(\w+)\b/g, function(match, varName) {
                    // reverseIndex 처리
                    if (varName === 'reverseIndex') {
                        return reverseIndex !== undefined ? reverseIndex : 0;
                    }

                    if (data[varName] !== undefined) {
                        const value = data[varName];
                        // 숫자인 경우만 그대로 반환, 나머지는 문자열로 처리
                        return typeof value === 'number' ? value : `"${value}"`;
                    }
                    return match;
                });

                // 위험한 연산자들 체크
                if (processedExpression.includes('++') ||
                    processedExpression.includes('--') ||
                    processedExpression.includes('=') ||
                    /[^0-9\+\-\*\/\%\(\)\.\s"']/.test(processedExpression.replace(/"/g, '').replace(/'/g, ''))) {
                    console.warn('안전하지 않은 수식:', processedExpression);
                    return '';
                }

                // 계산 실행
                const result = eval(processedExpression);
                return result !== undefined ? result : '';
            } catch (e) {
                console.error('계산식 오류:', e, '표현식:', expression);
                return '';
            }
        });

        return result;
    }

    getCheckedCount() {
        const $checkedCheckboxes = $(`#${this.gridId}-container .row-check:checked`);
        return $checkedCheckboxes.length;
    }

    getCheckedRowsData() {
        const checkedData = [];
        const $checkedCheckboxes = $(`#${this.gridId}-container .row-check:checked`);

        $checkedCheckboxes.each((index, checkbox) => {
            const $row = $(checkbox).closest('tr');
            const nodeId = $row.data('id');

            // findNodeById를 사용해서 실제 데이터 객체 찾기
            const result = this.findNodeById(nodeId);
            if (result && result.node) {
                checkedData.push(result.node);
            }
        });

        return checkedData;
    }

    // 체크된 행의 특정 필드값만 가져오는 메서드
    getCheckedRowsField(fieldName) {
        const checkedData = this.getCheckedRowsData();
        return checkedData.map(row => row[fieldName]);
    }

    // 체크된 행의 ID만 가져오는 메서드 (기존)
    getCheckedRowIds() {
        const checkedIds = [];
        $(`#${this.gridId}-container .row-check:checked`).each(function() {
            const rowId = $(this).closest('tr').data('id');
            checkedIds.push(rowId);
        });
        return checkedIds;
    }

    updatePageInfo() {
        // 페이지 정보 업데이트
        $(`#${this.gridId}-page-info`).text(`총 ${this.totalCount}건`);
    }


    renderPagination2() {
        if (!this.isPagingEnabled) return;
        const pagination = $(`#${this.gridId}-pagination`);
        pagination.empty();

        // 유틸리티 함수: 페이지 버튼 생성
        const createButton = (label, page, disabled = false, isActive = false) => {
            const button = $(`
                <span class="pagination-btn ${isActive ? 'active' : ''} ${disabled ? 'disabled' : ''}" data-page="${page}">${label}</span>
            `);
            if (!disabled && !isActive) {
                button.on('click', (e) => {
                    e.preventDefault();
                    this.fetchData(page);
                });
            }
            return button;
        };

        // 첫 페이지, 이전 페이지
        pagination.append(createButton('처음', 1, this.currentPage === 1));
        pagination.append(createButton('이전', this.currentPage - 1, this.currentPage === 1));

        // 페이지 번호 범위 계산
        const maxVisiblePages = 5; // 보여줄 최대 페이지 수
        let startPage, endPage;

        if (this.totalPages <= maxVisiblePages) {
            // 전체 페이지가 적으면 모두 표시
            startPage = 1;
            endPage = this.totalPages;
        } else {
            // 현재 페이지를 중심으로 범위 계산
            const halfVisible = Math.floor(maxVisiblePages / 2);
            startPage = Math.max(1, this.currentPage - halfVisible);
            endPage = Math.min(this.totalPages, this.currentPage + halfVisible);

            // 범위 조정 (시작이나 끝에 치우쳤을 때)
            if (startPage === 1) {
                endPage = Math.min(maxVisiblePages, this.totalPages);
            } else if (endPage === this.totalPages) {
                startPage = Math.max(1, this.totalPages - maxVisiblePages + 1);
            }
        }

        // 첫 페이지와 ... 표시
        if (startPage > 1) {
            pagination.append(createButton(1, 1));
            if (startPage > 2) {
                pagination.append($('<span class="pagination-ellipsis">...</span>'));
            }
        }

        // 페이지 번호들
        for (let i = startPage; i <= endPage; i++) {
            pagination.append(createButton(i, i, false, i === this.currentPage));
        }

        // ... 과 마지막 페이지 표시
        if (endPage < this.totalPages) {
            if (endPage < this.totalPages - 1) {
                pagination.append($('<span class="pagination-ellipsis">...</span>'));
            }
            pagination.append(createButton(this.totalPages, this.totalPages));
        }

        // 다음 페이지, 마지막 페이지
        pagination.append(createButton('다음', this.currentPage + 1, this.currentPage === this.totalPages));
        pagination.append(createButton('마지막', this.totalPages, this.currentPage === this.totalPages));
    }



    renderPagination() {
        if (!this.isPagingEnabled) return;
        const pagination = $(`#${this.gridId}-pagination`);
        pagination.empty();

        // 페이지 정보 계산
        const maxVisiblePages = 5; // 보여줄 최대 페이지 수
        let startPage, endPage;

        if (this.totalPages <= maxVisiblePages) {
            startPage = 1;
            endPage = this.totalPages;
        } else {
            const halfVisible = Math.floor(maxVisiblePages / 2);
            startPage = Math.max(1, this.currentPage - halfVisible);
            endPage = Math.min(this.totalPages, this.currentPage + halfVisible);

            if (startPage === 1) {
                endPage = Math.min(maxVisiblePages, this.totalPages);
            } else if (endPage === this.totalPages) {
                startPage = Math.max(1, this.totalPages - maxVisiblePages + 1);
            }
        }

        // 이전 버튼
        const prevDisabled = this.currentPage === 1;
        const prevButton = $(`
            <button type="button" class="page-navi prev" ${prevDisabled ? 'disabled' : ''}>
                <span class="sr-only">이전</span>
            </button>
        `);

        if (!prevDisabled) {
            prevButton.on('click', (e) => {
                e.preventDefault();
                this.fetchData(this.currentPage - 1);
            });
        }
        pagination.append(prevButton);

        // 페이지 링크들 컨테이너
        const pageLinksContainer = $('<div class="page-links"></div>');

        // 첫 페이지 표시 (startPage가 1이 아니고 총 페이지가 많을 때)
        if (startPage > 1) {
            const firstPageLink = $(`<a class="page-link" href="#" data-page="1">1</a>`);
            firstPageLink.on('click', (e) => {
                e.preventDefault();
                this.fetchData(1);
            });
            pageLinksContainer.append(firstPageLink);

            if (startPage > 2) {
                pageLinksContainer.append('<span class="page-ellipsis">...</span>');
            }
        }

        // 페이지 번호들
        for (let i = startPage; i <= endPage; i++) {
            const isActive = i === this.currentPage;
            const pageLink = $(`
                <a class="page-link ${isActive ? 'active' : ''}" href="#" data-page="${i}">
                    ${isActive ? '<span class="sr-only">현재페이지</span>' : ''}${i}
                </a>
            `);

            if (!isActive) {
                pageLink.on('click', (e) => {
                    e.preventDefault();
                    this.fetchData(i);
                });
            }
            pageLinksContainer.append(pageLink);
        }

        // 마지막 페이지 표시 (endPage가 totalPages가 아니고 총 페이지가 많을 때)
        if (endPage < this.totalPages) {
            if (endPage < this.totalPages - 1) {
                pageLinksContainer.append('<span class="page-ellipsis">...</span>');
            }

            const lastPageLink = $(`<a class="page-link" href="#" data-page="${this.totalPages}">${this.totalPages}</a>`);
            lastPageLink.on('click', (e) => {
                e.preventDefault();
                this.fetchData(this.totalPages);
            });
            pageLinksContainer.append(lastPageLink);
        }

        pagination.append(pageLinksContainer);

        // 다음 버튼
        const nextDisabled = this.currentPage === this.totalPages;
        const nextButton = $(`
            <button type="button" class="page-navi next" ${nextDisabled ? 'disabled' : ''}>
                <span class="sr-only">다음</span>
            </button>
        `);

        if (!nextDisabled) {
            nextButton.on('click', (e) => {
                e.preventDefault();
                this.fetchData(this.currentPage + 1);
            });
        }
        pagination.append(nextButton);

        // 페이지 정보 (총 건수 및 현재 페이지 정보)
        const pageInfo = $(`<p>총 ${this.totalCount}건 (${this.currentPage}/${this.totalPages})</p>`);
        pagination.append(pageInfo);

        // 페이지 사이즈 선택 드롭다운 (선택사항)
        const pageSizeOptions = [5, 10, 20, 50, 100];
        const pageSizeSelect = $(`<select name="pageSize" id="${this.gridId}-pageSize"></select>`);

        pageSizeOptions.forEach(size => {
            const option = $(`<option value="${size}" ${size === this.pageSize ? 'selected' : ''}>${size}</option>`);
            pageSizeSelect.append(option);
        });

        // 페이지 사이즈 변경 이벤트
        pageSizeSelect.on('change', (e) => {
            this.pageSize = parseInt(e.target.value);
            this.currentPage = 1; // 페이지 사이즈 변경 시 첫 페이지로
            this.fetchData(1);
        });

        pagination.append(pageSizeSelect);
    }


    bindRowClickEvents() {
        const self = this;
        let clickTimer = null;
        const clickDelay = 300; // 300ms 지연

        // 행 클릭 이벤트 (체크박스나 버튼 클릭은 제외)
        $(`#${this.gridId}-body`).off('click.rowClick').on('click.rowClick', 'tr', function(e) {
            // 체크박스, 버튼, input 등은 제외
            if ($(e.target).is('input, button, select, .tree-toggle')) {
                return;
            }

            const $row = $(this);
            const nodeId = String($row.data('id'));

            // 행 데이터 가져오기
            const rowData = self.getRowData(nodeId);

            if (rowData) {
                // 기존 타이머가 있다면 취소
                if (clickTimer) {
                    clearTimeout(clickTimer);
                    clickTimer = null;
                }

                // 지연 후 클릭 이벤트 실행
                clickTimer = setTimeout(function() {
                    // console.log('클릭된 행 데이터:', rowData);

                    // 행 선택 표시 (선택사항)
                	 //self.selectRow2($row);
                	self.selectRow(nodeId);


                    // 콜백 함수가 있다면 실행
                    if (self.onRowClick && typeof self.onRowClick === 'function') {
                        self.onRowClick(rowData, $row, e);
                    }

                    clickTimer = null;
                }, clickDelay);
            }
        });

        // 행 더블클릭 이벤트
        $(`#${this.gridId}-body`).off('dblclick.rowDoubleClick').on('dblclick.rowDoubleClick', 'tr', function(e) {
            // 체크박스, 버튼, input 등은 제외
            if ($(e.target).is('input, button, select, .tree-toggle')) {
                return;
            }

            // 클릭 타이머가 있다면 취소 (더블클릭 우선)
            if (clickTimer) {
                clearTimeout(clickTimer);
                clickTimer = null;
            }

            const $row = $(this);
            const nodeId = String($row.data('id'));

            // 행 데이터 가져오기
            const rowData = self.getRowData(nodeId);

            if (rowData) {
                // console.log('더블클릭된 행 데이터:', rowData);

                // 더블클릭 콜백 함수가 있다면 실행
                if (self.onRowDoubleClick && typeof self.onRowDoubleClick === 'function') {
                    self.onRowDoubleClick(rowData, $row);
                }
            }
        });
    }


 // 3. 행 데이터 가져오기 메서드 (가장 간단한 방법)
 getRowData(nodeId) {
     const result = this.findNodeById(nodeId);
     if (result && result.node) {
         // 내부 속성 제외하고 실제 데이터만 반환
         const excludeFields = ['children', 'childrenLoaded', 'treeExpanded', 'isNew', 'level', 'parentPath'];
         const rowData = {};

         Object.keys(result.node).forEach(key => {
             if (!excludeFields.includes(key)) {
                 rowData[key] = result.node[key];
             }
         });

         return rowData;
     }
     return null;
 }


//특정 행의 데이터를 업데이트하는 메서드
 updateRowData(nodeId, fieldUpdates) {
     const result = this.findNodeById(nodeId);
     if (result && result.node) {
         // 필드별 업데이트
         Object.keys(fieldUpdates).forEach(field => {
             result.node[field] = fieldUpdates[field];
         });

         // 저장 기능이 활성화된 경우만 수정된 행 추적
         if (this.isSaveEnabled) {
             const nodeKey = this.getNodeKey(result.node);
             this.modifiedRows.add(nodeKey);
         }

         // 테이블 다시 렌더링
        // this.renderTable();
         // ★ renderTable() 대신 해당 행만 업데이트
         this.updateSingleRow(nodeId);

         return true;
     }
     return false;
 }

 // 특정 행의 특정 필드만 업데이트하는 메서드
 updateRowField(nodeId, field, value) {
     const result = this.findNodeById(nodeId);
     if (result && result.node) {
         result.node[field] = value;

         // 저장 기능이 활성화된 경우만 수정된 행 추적
         if (this.isSaveEnabled) {
             const nodeKey = this.getNodeKey(result.node);
             this.modifiedRows.add(nodeKey);
         }

         // 해당 셀만 업데이트 (전체 테이블 렌더링 대신)
         const $row = $(`#${this.gridId}-body tr[data-id="${nodeId}"]`);
         const $field = $row.find(`[data-field="${field}"]`);

         if ($field.length) {
             if ($field.is('input, textarea, select')) {
                 $field.val(value);
             } else if ($field.is('[contenteditable]')) {
                 $field.text(value);
             }
         }


         // ★ 추가: 자동으로 테이블 다시 렌더링
         //this.renderTable();
         // ★ renderTable() 대신 해당 행만 업데이트
         this.updateSingleRow(nodeId);

         return true;
     }
     return false;
 }

//단일 행만 업데이트하는 메서드
//TreeGridManager 클래스에 추가
 updateSingleRow(nodeId) {

	    const result = this.findNodeById(nodeId);
	    if (!result || !result.node) {
	        console.error('노드를 찾을 수 없습니다:', nodeId);
	        return;
	    }

	    const $existingRow = $(`#${this.gridId}-body tr[data-id="${nodeId}"]`);
	    if (!$existingRow.length) {
	        console.error('기존 행을 찾을 수 없습니다:', nodeId);
	        return;
	    }

	    const node = result.node;
	    const rowIndex = $existingRow.index();
	    const isVisible = $existingRow.is(':visible');

	    //console.log('행 재생성 중... Node data:', node);

	    // 새 행 HTML 생성
	    const reverseIndex = this.totalCount - (this.currentPage - 1) * this.pageSize - rowIndex;
	    const newRowHtml = this.createNodeRow(node, isVisible, rowIndex, reverseIndex);

	  //  console.log("기존 행 HTML (교체 전):", $existingRow.html());
	   // console.log("새 행 HTML:", newRowHtml);

	    // 기존 행을 새 행으로 교체
	    $existingRow.replaceWith(newRowHtml);

	    // ⚠️ 이 시점에서 $existingRow는 더 이상 DOM에 존재하지 않는 요소를 참조함
	   // console.log("$existingRow는 이제 DOM에서 분리됨:", $existingRow.html());

	    // 새로 DOM에 삽입된 행을 다시 선택
	    const $newRowInDOM = $(`#${this.gridId}-body tr[data-id="${nodeId}"]`);
	  //  console.log("DOM에서 새로 선택한 행:", $newRowInDOM.html());

	    // 행 선택 체크박스 상태 설정 (node 데이터의 checkedAttr 기반)
	    const shouldBeChecked = node.checkedAttr === 'checked' || node.checkedAttr === true;
	    $newRowInDOM.find('.row-check').prop('checked', shouldBeChecked);
	  //  console.log(`행 선택 체크박스: checkedAttr="${node.checkedAttr}" -> checked=${shouldBeChecked}`);

	    // data-value 속성과 실제 input 값 비교
	    $newRowInDOM.find('[data-field]').each(function() {
	        const $this = $(this);
	        const field = $this.data('field');
	        const dataValue = $this.data('value');
	        let actualValue;

	        if ($this.is('input[type="checkbox"]')) {
	            actualValue = $this.is(':checked');
	        } else if ($this.is('input[type="radio"]')) {
	            actualValue = $this.is(':checked') ? $this.val() : '';
	        } else if ($this.is('input, textarea')) {
	            actualValue = $this.val();
	        } else if ($this.is('select')) {
	            actualValue = $this.val();
	        } else if ($this.is('[contenteditable]')) {
	            actualValue = $this.html();
	        }

	       // console.log(`필드 ${field}: data-value="${dataValue}", 실제값="${actualValue}", 타입: ${$this.attr('type') || $this.prop('tagName')}`);

	        // data-value가 있으면 실제 input에 설정
	        if (dataValue !== undefined && dataValue !== '') {
	            if ($this.is('input[type="checkbox"]')) {
	                // 체크박스: data-value가 'true', '1', 'checked', 또는 실제 boolean true이면 체크
	                const shouldCheck = dataValue === true || dataValue === 'true' || dataValue === '1' || dataValue === 'checked';
	                $this.prop('checked', shouldCheck);
	             //   console.log(`checkbox ${field} 값 설정: ${dataValue} -> 체크상태: ${shouldCheck}`);
	            } else if ($this.is('input[type="radio"]')) {
	                // 라디오버튼: data-value와 value가 같으면 선택
	                if ($this.val() === String(dataValue)) {
	                    $this.prop('checked', true);
	                    console.log(`radio ${field} 값 설정: ${dataValue} -> 선택됨`);
	                } else {
	                    $this.prop('checked', false);
	                }
	            } else if ($this.is('select')) {
	                $this.val(dataValue);
	               // console.log(`select ${field} 값 설정: ${dataValue} -> 결과: ${$this.val()}`);
	            } else if ($this.is('input, textarea')) {
	                $this.val(dataValue);
	               // console.log(`input ${field} 값 설정: ${dataValue} -> 결과: ${$this.val()}`);
	            } else if ($this.is('[contenteditable]')) {
	                $this.html(dataValue);
	               // console.log(`contenteditable ${field} 값 설정: ${dataValue}`);
	            }
	        }
	    });

	    // 새 행 찾아서 이벤트 바인딩
	    if ($newRowInDOM.length) {
	        // 라디오버튼 그룹 별도 처리 (같은 name의 모든 라디오버튼)
	        const radioGroups = {};
	        $newRowInDOM.find('input[type="radio"][data-field]').each(function() {
	            const field = $(this).data('field');
	            if (!radioGroups[field]) {
	                radioGroups[field] = [];
	            }
	            radioGroups[field].push($(this));
	        });

	        // 각 라디오 그룹에서 data-value에 맞는 것만 선택
	        Object.keys(radioGroups).forEach(field => {
	            const $firstRadio = radioGroups[field][0];
	            const dataValue = $firstRadio.data('value');

	            if (dataValue !== undefined && dataValue !== '') {
	                // 해당 그룹의 모든 라디오버튼 체크 해제
	                radioGroups[field].forEach($radio => $radio.prop('checked', false));

	                // data-value와 일치하는 라디오버튼만 선택
	                const $targetRadio = radioGroups[field].find($radio => $radio.val() === String(dataValue));
	                if ($targetRadio) {
	                    $targetRadio.prop('checked', true);
	                  //  console.log(`라디오 그룹 ${field}: ${dataValue} 선택됨`);
	                }
	            }
	        });

	        this.bindSingleRowEvents($newRowInDOM);
	       // console.log('단일 행 업데이트 완료');
	    } else {
	        console.error('새로 생성된 행을 찾을 수 없습니다');
	    }
	}

 // 단일 행 이벤트 바인딩 (data-value 설정 제외)
 bindSingleRowEvents($row) {
	    const self = this;

	    //console.log('=== bindSingleRowEvents 호출 ===');
	   // console.log('대상 행:', $row.data('id'));

	    // 기존 이벤트 모두 제거
	    $row.off();

	    // 편집 이벤트 바인딩
	    $row.on('input change blur keyup',
	        'input[data-field]:not([type="radio"]), textarea[data-field], select[data-field], [contenteditable][data-field]',
	        function() {
	            const $this = $(this);
	            const nodeId = String($this.closest('tr').data('id'));
	            const field = $this.data('field');

	           // console.log('편집 이벤트 발생:', nodeId, field, $this.val());

	            if (nodeId && field) {
	                self.trackEdit(nodeId, this, field);
	            }
	        }
	    );

	    // 라디오버튼 이벤트
	    $row.on('change', 'input[type="radio"][data-field]', function () {
	        const $this = $(this);
	        const nodeId = String($this.closest('tr').data('id'));
	        const field = $this.data('field');
	        if (nodeId && field) {
	            self.trackEdit(nodeId, this, field);
	        }
	    });

	    // 체크박스 이벤트 (row-check)
	    $row.on('change', '.row-check', function() {
	        self.updateHeaderCheckbox();
	    });

	    // 트리 토글 이벤트
	    $row.on('click', '.tree-toggle', function(e) {
	        e.preventDefault();
	        const nodeId = String($(this).closest('tr').data('id'));
	        if (nodeId) {
	            self.toggleTree(nodeId, e);
	        }
	    });

	    // 날짜 입력 필드 초기화
	    $row.find('.date-input').flatpickr({
	        locale: 'ko',
	        dateFormat: 'Y-m-d'
	    });

	   // console.log('단일 행 이벤트 바인딩 완료');
	}


//인덱스로 행 데이터 가져오기 (평면 인덱스 - 화면에 보이는 순서)
 getRowDataByIndex(index) {
     const flatData = this.getFlatDataList();
     if (index >= 0 && index < flatData.length) {
         return flatData[index];
     }
     return null;
 }

 // 인덱스로 nodeId 가져오기
 getNodeIdByIndex(index) {
     const rowData = this.getRowDataByIndex(index);
     return rowData ? rowData.id : null;
 }

 // 평면화된 데이터 리스트 가져오기 (트리 구조를 순서대로 펼친 리스트)
 getFlatDataList() {
     const flatList = [];

     const flattenNode = (node) => {
         flatList.push(node);
         if (this.isTreeMode && node.children && node.children.length > 0 && node.treeExpanded) {
             node.children.forEach(child => flattenNode(child));
         }
     };

     this.data.forEach(node => flattenNode(node));
     return flatList;
 }

 // 반대로 nodeId로 인덱스 찾기
 getIndexByNodeId(nodeId) {
     const flatData = this.getFlatDataList();
     return flatData.findIndex(node => String(node.id) === String(nodeId));
 }


//인덱스로 행 데이터 업데이트
 updateRowDataByIndex(index, fieldUpdates) {
     const nodeId = this.getNodeIdByIndex(index);
     if (nodeId) {
         return this.updateRowData(nodeId, fieldUpdates);
     }
     return false;
 }

 // 인덱스로 특정 필드 업데이트
 updateRowFieldByIndex(index, field, value) {
     const nodeId = this.getNodeIdByIndex(index);
     if (nodeId) {
         return this.updateRowField(nodeId, field, value);
     }
     return false;
 }


 //  행 선택 표시 메서드 (선택사항)
 /*
 selectRow2($row) {
     // 이전 선택 해제
     $(`#${this.gridId}-body tr`).removeClass('selected');

     // 현재 행 선택
     $row.addClass('selected');
 }
*/



//3. 엑셀 업로드 처리 메서드 추가
 handleExcelUpload(file) {

     const self = this;
     // 파일 확장자 검증
     const fileName = file.name.toLowerCase();
     if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
         toastr["info"]("엑셀 파일(.xlsx, .xls)만 업로드 가능합니다.");
         return;
     }

     const reader = new FileReader();
     reader.onload = function(e) {
         try {
             const data = new Uint8Array(e.target.result);
             const workbook = XLSX.read(data, { type: 'array' });

             // 첫 번째 시트 읽기
             const firstSheetName = workbook.SheetNames[0];
             const worksheet = workbook.Sheets[firstSheetName];

             // 시트를 JSON으로 변환 (헤더 포함)
             const jsonData = XLSX.utils.sheet_to_json(worksheet, {
                 header: 1, // 배열 형태로 반환
                 defval: '' // 빈 셀의 기본값
             });

             if (jsonData.length === 0) {
                 toastr["info"]("엑셀 파일에 데이터가 없습니다.");
                 return;
             }

             self.processExcelData(jsonData);

         } catch (error) {
             console.error('엑셀 파일 읽기 오류:', error);
         	 toastr["error"]("엑셀 파일을 읽는데 실패했습니다.");
         }
     };

     reader.readAsArrayBuffer(file);
 }

 // 4. 엑셀 데이터 처리 메서드 추가
 processExcelData(jsonData) {

     if (jsonData.length < 2) {
         alert('헤더와 데이터가 모두 필요합니다.');
         return;
     }

     const headers = jsonData[0]; // 첫 번째 행은 헤더
     const rows = jsonData.slice(1); // 나머지는 데이터



     // 유효성 검사 실행
     const validationResult = this.validateExcelData(headers, rows);
     // ★ 수정: 유효성 검사 실패해도 데이터는 로드하고 오류 정보만 저장
     this.excelValidationErrors = validationResult.errors || [];
     this.excelErrorCells = validationResult.errorCells || [];

  // 유효성 검사 실행
    // const validationResult = this.validateExcelData(headers, rows);
     // ★ 수정: 헤더 오류도 포함하여 모든 오류 정보 저장
    // this.excelValidationErrors = [...(validationResult.errors || [])];
    // this.excelErrorCells = validationResult.errorCells || [];



     // 엑셀 원본 데이터 저장 (모든 컬럼 포함)
     this.excelData = [];
     this.data = [];

     for (let i = 0; i < rows.length; i++) {
         const row = rows[i];

         // 원본 엑셀 데이터 저장 (모든 컬럼)
         const excelRowData = {};
         for (let j = 0; j < headers.length; j++) {
             const header = headers[j];
             const value = row[j] || '';
             excelRowData[header] = value;
         }
         this.excelData.push(excelRowData);

         // 그리드 표시용 데이터 생성 (템플릿에 있는 필드만)
         const gridRowData = {
             id: 'excel_' + Date.now().toString() + '_' + i,
             level: 0,
             parentPath: '',
             isExcel: true // 엑셀에서 온 데이터임을 표시
         };

         // 템플릿에서 사용하는 필드들만 매핑
         // 예시: no, name, date 필드
         const excludeFields = ['children', 'childrenLoaded', 'treeExpanded', 'isNew', 'level', 'parentPath', 'id'];

         Object.keys(excelRowData).forEach(field => {
             if (!excludeFields.includes(field)) {
                 gridRowData[field] = excelRowData[field];
             }
         });


         // 트리 모드일 때만 트리 관련 속성 추가
         if (this.isTreeMode) {
             gridRowData.childrenLoaded = true;
             gridRowData.children = [];
             gridRowData.treeExpanded = false;
             gridRowData.childYn = 'N';
         }

         this.data.push(gridRowData);
     }

     this.isExcelMode = true; // 엑셀 모드로 설정
     this.renderTable();

     // 페이징 정보 업데이트
     if (this.isPagingEnabled) {
         this.totalCount = this.data.length;
         this.totalPages = Math.ceil(this.totalCount / this.pageSize);
         this.updatePageInfo();
     }

     // ★ 수정: 오류가 있으면 오류 메시지 표시하고 오류 셀 강조
     if (this.excelValidationErrors.length > 0) {
         let errorMessage = "엑셀 데이터에 오류가 있습니다.<br><br>";
         this.excelValidationErrors.forEach(error => {
             errorMessage += `${error}`;
         });
         //errorMessage += "\n오류가 있는 셀은 빨간색으로 표시됩니다.";

        // alert(errorMessage);
         this.highlightErrorCells(); // ★ 추가: 오류 셀 강조

         toastr.remove();

         toastr.info(errorMessage, null, {
        	 closeButton: true,
        	  timeOut: 0,
        	  extendedTimeOut: 0,
        	  tapToDismiss: false,
        });


     }else{
    	 toastr["success"](`엑셀 데이터 ${this.data.length}건이 로드되었습니다.`);
     }


     if (this.onInitComplete && typeof this.onInitComplete === 'function' ) {
         this.onInitComplete();
     }
 }


//7. 엑셀 데이터 저장 메서드 추가
 saveExcelData() {


	 toastr.remove();
     // ★ 수정: 오류가 있을 때만 토스트 표시
     if (this.excelValidationErrors.length > 0) {

         let errorMessage = "엑셀 데이터에 오류가 있습니다.<br><br>";
         this.excelValidationErrors.forEach(error => {
             errorMessage += `${error}\n`;
         });

         this.errorToastId = toastr.info(errorMessage, null, {
             closeButton: true,
             timeOut: 0,
             extendedTimeOut: 0,
             tapToDismiss: false,
         });

         this.highlightErrorCells();
     }



	 const enterLine = '-----------------------------<br>'

     if (!this.excelData || this.excelData.length === 0) {
         alert("저장할 엑셀 데이터가 없습니다.");
         return;
     }


     // ★ 추가: 유효성 검사 오류가 있으면 저장 중단
     if (this.excelValidationErrors && this.excelValidationErrors.length > 0) {

    	 let errorMessage = "오류를 수정한 후 다시 저장해주세요.";

         toastr.error(errorMessage);
         return; // 저장 중단
     }


     console.log(`[${this.gridId}] 엑셀 데이터 저장:`, this.excelData);

     const payload = {
         isExcelData: true,
         excelData: this.excelData // 모든 엑셀 데이터 전송
     };

     $.ajax({
         url: this.urls.saveExlUrl,
         type: "POST",
         contentType: "application/json",
         data: JSON.stringify(payload),
         success: (res) => {

    		 toastr["success"](res.message);
             //this.isExcelMode = false;

             //this.excelData = [];
             //this.fetchData(this.currentPage); // 서버에서 데이터 다시 조회
             this.excelData = [];
             this.data = []; // 현재 그리드 데이터 초기화

             this.renderTable(); // 빈 그리드로 렌더링

         },
         error: (xhr, status, error) => {
             console.log(`[${this.gridId}] 엑셀 데이터 저장 실패:`, error);
             toastr["error"]("엑셀 데이터 저장에 실패했습니다.");
         }
     });
 }

//★ 추가: 실시간 유효성 검사를 위한 메서드
 validateSingleCell(nodeId, field, value) {
	    if (!this.isExcelMode || !this.excelValidationRules[field]) {
	        return null;
	    }

	    const rule = this.excelValidationRules[field];
	    const result = this.findNodeById(nodeId);

	    if (result && result.node && result.node.isExcel) {
	        const nodeIndex = this.data.indexOf(result.node);
	        const rowNumber = nodeIndex + 2; // 엑셀 행 번호 (헤더 제외)

	        return this.validateCellValue(field, value, rule, rowNumber);
	    }

	    return null;
	}

 // 8. 엑셀 데이터 초기화 메서드 추가
 clearExcelData() {
     this.isExcelMode = false;
     this.excelData = [];
     this.data = [];
     this.renderTable();

     if (this.isPagingEnabled) {
         this.totalCount = 0;
         this.totalPages = 0;
         this.updatePageInfo();
     }
 }


//엑셀 데이터 유효성 검사 메서드
//엑셀 데이터 유효성 검사 메서드 수정
 validateExcelData(headers, rows) {
	 const enterLine = '-----------------------------<br>'
     const errors = [];
     const errorCells = []; // ★ 추가: 오류 셀 위치 저장

     // 헤더 검사
     let result = this.validateHeaders(headers)
     if (!result.check) {
         errors.push("필수 헤더 ["+result.header+"] 가 누락되었습니다.<br>"+enterLine);
     }

     // 각 행 데이터 검사
     for (let i = 0; i < rows.length; i++) {
         const row = rows[i];
         const rowNumber = i + 2; // 엑셀 행 번호 (헤더 제외)

         for (let j = 0; j < headers.length; j++) {
             const header = headers[j];
             const value = row[j] || '';

             if (this.excelValidationRules[header]) {
                 const rule = this.excelValidationRules[header];
                 const validationError = this.validateCellValue(header, value, rule, rowNumber);

                 if (validationError) {
                     errors.push(validationError);
                     // ★ 추가: 오류 셀 위치 저장
                     errorCells.push({
                         rowIndex: i,
                         field: header,
                         error: validationError
                     });
                 }
             }
         }
     }

     return {
         isValid: errors.length === 0,
         errors: errors,
         errorCells: errorCells // ★ 추가: 오류 셀 정보 반환
     };
 }

//오류 셀 강조 메서드 - td만 강조
 highlightErrorCells() {
	    if (!this.excelErrorCells || this.excelErrorCells.length === 0) {
	        return;
	    }

	    setTimeout(() => {
	        this.excelErrorCells.forEach(errorCell => {
	            const $row = $(`#${this.gridId}-body tr[data-id*="excel_"]`).eq(errorCell.rowIndex);
	            const $cell = $row.find(`[data-field="${errorCell.field}"], td:contains("${errorCell.field}")`);

	            if ($cell.length > 0) {
	                // td에만 클래스 추가
	                $cell.addClass('excel-error-cell');
	            }
	        });
	    }, 100);
	}

 // 헤더 검사
 validateHeaders(headers) {
     const requiredHeaders = Object.keys(this.excelValidationRules).filter(
         key => this.excelValidationRules[key].required
     );

     for (let i = 0; i < requiredHeaders.length; i++) {
         if (!headers.includes(requiredHeaders[i])) {
             return {check:false, header:requiredHeaders[i]};
         }
     }

     return {check:true, header:''};
 }

 // 셀 값 검사
 validateCellValue(header, value, rule, rowNumber) {

	 const enterLine = '-----------------------------<br>'

	 rowNumber=rowNumber-1;
     // 필수값 검사
     if (rule.required && (!value || value.toString().trim() === '')) {
         return `${rowNumber}행 ${header}: 필수값입니다.`+enterLine;
     }

     // 값이 비어있고 필수가 아니면 다른 검사 생략
     if (!value || value.toString().trim() === '') {
         return null;
     }

     const stringValue = value.toString().trim();


     // 타입별 검사
     switch (rule.type) {
         case 'number':
             if (isNaN(stringValue) || isNaN(parseFloat(stringValue))) {
                 return `${rowNumber}행 ${header}: 숫자만 입력 가능합니다.<br> (입력값: ${stringValue})<br>`+enterLine;
             }
             break;

         case 'integer':
             if (!Number.isInteger(Number(stringValue)) || isNaN(stringValue)) {
                 return `${rowNumber}행 ${header}: 정수만 입력 가능합니다. <br>(입력값: ${stringValue})<br>`+enterLine;
             }
             break;

         case 'email':
             const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
             if (!emailRegex.test(stringValue)) {
                 return `${rowNumber}행 ${header}: 올바른 이메일 형식이 아닙니다.<br> (입력값: ${stringValue})<br>`+enterLine;
             }
             break;

         case 'phone':
             const phoneRegex = /^[\d-]+$/;
             if (!phoneRegex.test(stringValue)) {
                 return `${rowNumber}행 ${header}: 전화번호 형식이 올바르지 않습니다. <br>(입력값: ${stringValue})<br>`+enterLine;
             }
             break;

         case 'date':
        	    const dateRegex = /^(\d{4}-\d{2}-\d{2}|\d{8})$/;

        	    if (!dateRegex.test(stringValue) || !this.isValidDate(stringValue)) {
        	        return `${rowNumber}행 ${header}: 날짜 형식은 YYYY-MM-DD 또는 YYYYMMDD 이고, 실제 존재하는 날짜여야 합니다. <br>(입력값: ${stringValue})<br>` + enterLine;
        	    }
        	    break;

     }

     // 길이 검사
     if (rule.minLength && stringValue.length < rule.minLength) {
         return `${rowNumber}행 ${header}: 최소 ${rule.minLength}자 이상 입력하세요. <br>(입력값: ${stringValue})<br>`+enterLine;
     }

     if (rule.maxLength && stringValue.length > rule.maxLength) {
         return `${rowNumber}행 ${header}: 최대 ${rule.maxLength}자까지 입력 가능합니다. <br>(입력값: ${stringValue})<br>`+enterLine;
     }

     // 정규식 검사
     if (rule.pattern) {
         const regex = new RegExp(rule.pattern);
         if (!regex.test(stringValue)) {
             return `${rowNumber}행 ${header}: ${rule.patternMessage || '형식이 올바르지 않습니다.'}<br> (입력값: ${stringValue})<br>`+enterLine;
         }
     }

     // 범위 검사 (숫자인 경우)
     if (rule.type === 'number' || rule.type === 'integer') {
         const numValue = Number(stringValue);

         if (rule.min !== undefined && numValue < rule.min) {
             return `${rowNumber}행 ${header}: ${rule.min} 이상의 값을 입력하세요. <br>(입력값: ${stringValue})<br>`+enterLine;
         }

         if (rule.max !== undefined && numValue > rule.max) {
             return `${rowNumber}행 ${header}: ${rule.max} 이하의 값을 입력하세요. <br>(입력값: ${stringValue})<br>`+enterLine;
         }
     }

     // 허용값 목록 검사
     if (rule.allowedValues && Array.isArray(rule.allowedValues)) {
         if (!rule.allowedValues.includes(stringValue)) {
             return `${rowNumber}행 ${header}: 허용된 값이 아닙니다.<br> 허용값: [${rule.allowedValues.join(', ')}]<br> (입력값: ${stringValue})<br>`+enterLine;
         }
     }

     return null; // 유효함
 }


  isValidDate(str) {
	    // YYYYMMDD → YYYY-MM-DD로 변환
	    const formatted = str.includes('-')
	        ? str
	        : `${str.substring(0, 4)}-${str.substring(4, 6)}-${str.substring(6, 8)}`;

	    const d = new Date(formatted);

	    // 날짜 유효성 확인 (예: 2025-02-30 → false)
	    const yyyy = formatted.substring(0, 4);
	    const mm = formatted.substring(5, 7);
	    const dd = formatted.substring(8, 10);

	    return (
	        d instanceof Date &&
	        !isNaN(d.getTime()) &&
	        d.getFullYear() === parseInt(yyyy) &&
	        d.getMonth() + 1 === parseInt(mm) &&
	        d.getDate() === parseInt(dd)
	    );
	}


  bindContextMenuEvents() {
      const self = this;

      const gridBody = document.getElementById(`${this.gridId}-body`);

      if (!gridBody) return;

      gridBody.addEventListener('contextmenu', function(e) {
          const row = e.target.closest('tr');
          if (row) {
              e.preventDefault();
              e.stopPropagation();

              const nodeId = row.dataset.id;

              if (nodeId && nodeId !== 'undefined') {
                 // $(`#${self.gridId}-body tr`).removeClass('context-selected');
                 // $(row).addClass('context-selected');

            	  //selectRow 에서 위의 주석처리한부분함
                  self.selectRow(nodeId, false);

                  self.showContextMenu(e.pageX, e.pageY, nodeId, $(row));
              }
          }
      }, true); // true = 캡처링 단계


      /*

      // 우클릭 이벤트
      $(`#${this.gridId}-body`).off('contextmenu.contextMenu').on('contextmenu.contextMenu', 'tr', function(e) {
          e.preventDefault();
          e.stopImmediatePropagation(); // 다른 핸들러 차단


          const $row = $(this);
          const nodeId = String($row.data('id'));

          if (!nodeId || nodeId === 'undefined') return;

          // 다른 모든 행의 선택 해제
          $(`#${self.gridId}-body tr`).removeClass('context-selected');
          // 현재 행 선택 표시
          $row.addClass('context-selected');

          self.showContextMenu(e.pageX, e.pageY, nodeId, $row);
      });
*/
      // 다른 곳 클릭시 컨텍스트 메뉴 숨기기
      $(document).off('click.contextMenu').on('click.contextMenu', function(e) {
          if (!$(e.target).closest('.context-menu').length) {
              self.hideContextMenu();
          }
      });

      // ESC 키로 컨텍스트 메뉴 숨기기
      $(document).off('keydown.contextMenu').on('keydown.contextMenu', function(e) {
          if (e.keyCode === 27) { // ESC
              self.hideContextMenu();
          }
      });
  }

  showContextMenu(x, y, nodeId, $row) {

	    this.hideContextMenu(); // 기존 메뉴 숨기기

	    const result = this.findNodeById(nodeId);
	    console.log('findNodeById 결과:', result);

	    if (!result || !result.node) {
	        console.error('노드를 찾을 수 없습니다:', nodeId);
	        return;
	    }

	    const node = result.node;
	    console.log('찾은 노드:', node);

	    // 컨텍스트 메뉴 HTML 생성
	    const menuId = `${this.gridId}-context-menu`;
	    let menuHtml = `<div id="${menuId}" class="context-menu" style="
	        position: fixed;
	        z-index: 9999;
	        background: white;
	        border: 1px solid #ccc;
	        border-radius: 4px;
	        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
	        padding: 8px 0;
	        min-width: 150px;
	        display: none;
	    ">`;

	    console.log('contextMenuItems:', this.contextMenuItems);

	    if (!this.contextMenuItems || this.contextMenuItems.length === 0) {
	        console.warn('contextMenuItems가 비어있습니다. 기본 메뉴를 생성합니다.');
	        menuHtml += `
	            <div class="context-menu-item" data-action="test" data-node-id="${nodeId}" style="
	                padding: 8px 16px;
	                cursor: pointer;
	                hover: background-color: #f0f0f0;
	            ">
	                <span>테스트 메뉴</span>
	            </div>
	        `;
	    } else {
	        this.contextMenuItems.forEach((item, index) => {
	            console.log(`메뉴 아이템 ${index}:`, item);

	            // 조건부 표시 체크
	            if (item.condition && !item.condition(node, $row)) {
	                console.log(`메뉴 아이템 ${index} 조건 불만족으로 스킵`);
	                return;
	            }

	            const disabled = item.disabled && item.disabled(node, $row) ? 'disabled' : '';
	            const separator = item.separator ? 'separator' : '';

	            if (item.separator) {
	                menuHtml += `<div class="context-menu-separator" style="
	                    height: 1px;
	                    background: #e0e0e0;
	                    margin: 4px 0;
	                "></div>`;
	            } else {
	                menuHtml += `
	                    <div class="context-menu-item ${disabled}" data-action="${item.action}" data-node-id="${nodeId}" style="
	                        padding: 8px 16px;
	                        cursor: pointer;
	                        display: flex;
	                        align-items: center;
	                        gap: 8px;
	                        ${disabled ? 'opacity: 0.5; cursor: not-allowed;' : ''}
	                    ">
	                        ${item.icon ? `<i class="${item.icon}"></i>` : ''}
	                        <span>${item.label}</span>
	                        ${item.shortcut ? `<span class="shortcut" style="margin-left: auto; opacity: 0.7;">${item.shortcut}</span>` : ''}
	                    </div>
	                `;
	            }
	        });
	    }

	    menuHtml += '</div>';
	   // console.log('생성된 HTML:', menuHtml);

	    // 메뉴를 body에 추가
	    $('body').append(menuHtml);

	    const $menu = $(`#${menuId}`);

	    if ($menu.length === 0) {
	        console.error('메뉴 요소가 생성되지 않았습니다!');
	        return;
	    }

	    // 메뉴 위치 조정 (화면 밖으로 나가지 않도록)
	    $menu.show(); // 먼저 보이게 해서 크기 측정

	    const menuWidth = $menu.outerWidth();
	    const menuHeight = $menu.outerHeight();
	    const windowWidth = $(window).width();
	    const windowHeight = $(window).height();


	    let finalX = x;
	    let finalY = y;

	    if (x + menuWidth > windowWidth) {
	        finalX = x - menuWidth;
	    }
	    if (y + menuHeight > windowHeight) {
	        finalY = y - menuHeight;
	    }


	    $menu.css({
	        left: finalX + 'px',
	        top: finalY + 'px',
	        display: 'block' // 확실히 보이도록
	    });


	    // 호버 효과 추가
	    $menu.find('.context-menu-item:not(.disabled)').hover(
	        function() { $(this).css('background-color', '#f0f0f0'); },
	        function() { $(this).css('background-color', 'transparent'); }
	    );

	    // 메뉴 아이템 클릭 이벤트
	    $menu.off('click.contextMenuItem').on('click.contextMenuItem', '.context-menu-item:not(.disabled)', (e) => {

	        const action = $(e.currentTarget).data('action');
	        const targetNodeId = $(e.currentTarget).data('node-id');


	        this.hideContextMenu();

	        // 콜백 함수 호출
	        if (this.onContextMenuClick) {
	            this.onContextMenuClick(action, targetNodeId, node, $row, this);
	        } else {
	            console.warn('onContextMenuClick 콜백이 없습니다');
	        }
	    });


	}

	// 개선된 hideContextMenu
	hideContextMenu() {
	    const $menus = $('.context-menu');
	    $menus.remove();
	    $(`#${this.gridId}-body tr`).removeClass('context-selected');
	}

//행 데이터를 클립보드에 복사하는 메서드
  copyRowToClipboard(nodeId, format = 'json') {
      const result = this.findNodeById(nodeId);
      if (!result || !result.node) {
          console.log('복사할 데이터를 찾을 수 없습니다.');
          return false;
      }

      const node = result.node;
      let copyText = '';

      switch(format) {
          case 'json':
              // JSON 형태로 복사 (내부 속성 제외)
              const excludeFields = ['children', 'childrenLoaded', 'treeExpanded', 'isNew', 'level', 'parentPath'];
              const cleanData = {};
              Object.keys(node).forEach(key => {
                  if (!excludeFields.includes(key)) {
                      cleanData[key] = node[key];
                  }
              });
              copyText = JSON.stringify(cleanData, null, 2);
              break;

          case 'tab':
              // 탭으로 구분된 텍스트 (엑셀 붙여넣기 가능)
              const excludeFieldsTab = ['children', 'childrenLoaded', 'treeExpanded', 'isNew', 'level', 'parentPath'];
              const values = [];
              Object.keys(node).forEach(key => {
                  if (!excludeFieldsTab.includes(key)) {
                      values.push(node[key] || '');
                  }
              });
              copyText = values.join('\t');
              break;

          case 'text':
              // 읽기 쉬운 텍스트 형태
              const excludeFieldsText = ['children', 'childrenLoaded', 'treeExpanded', 'isNew', 'level', 'parentPath'];
              const textLines = [];
              Object.keys(node).forEach(key => {
                  if (!excludeFieldsText.includes(key)) {
                      textLines.push(`${key}: ${node[key] || ''}`);
                  }
              });
              copyText = textLines.join('\n');
              break;

          default:
              copyText = JSON.stringify(node, null, 2);
      }

      return this.copyToClipboard(copyText);
  }

  // 클립보드에 텍스트 복사하는 범용 메서드
  async copyToClipboard(text) {
      try {
          // 최신 브라우저의 Clipboard API 사용
          if (navigator.clipboard && window.isSecureContext) {
              await navigator.clipboard.writeText(text);
              return true;
          } else {
              // 구버전 브라우저 호환성을 위한 fallback
              return this.fallbackCopyToClipboard(text);
          }
      } catch (err) {
          console.error('클립보드 복사 실패:', err);
          return this.fallbackCopyToClipboard(text);
      }
  }

  // fallback 복사 메서드 (구버전 브라우저용)
  fallbackCopyToClipboard(text) {
      try {
          const textArea = document.createElement('textarea');
          textArea.value = text;
          textArea.style.position = 'fixed';
          textArea.style.left = '-9999px';
          textArea.style.top = '-9999px';
          document.body.appendChild(textArea);
          textArea.focus();
          textArea.select();

          const successful = document.execCommand('copy');
          document.body.removeChild(textArea);

          return successful;
      } catch (err) {
          console.error('Fallback 복사 실패:', err);
          return false;
      }
  }




	//사용 예시
	addRowAbove() {
	 if (this.selectedRows.size === 0) {
	     alert('행을 선택해주세요.');
	     return;
	 }
	 return this.addRow('above');
	}

	//선택된 행 아래에 추가
	addRowBelow() {
	 if (this.selectedRows.size === 0) {
	     alert('행을 선택해주세요.');
	     return;
	 }
	 return this.addRow('below');
	}

	//선택된 행의 자식으로 추가
	addChildRow() {
	 if (this.selectedRows.size === 0) {
	     alert('행을 선택해주세요.');
	     return;
	 }
	 if (!this.isTreeMode) {
	     alert('트리 모드에서만 사용 가능합니다.');
	     return;
	 }
	 return this.addRow('child');
	}


}

//

// 전역 그리드 관리자들
const gridManagers = {};

// 트리 토글 전역 함수 (템플릿에서 호출)
function toggleTree(gridId, nodeId, e) {
    if (gridManagers[gridId]) {
        gridManagers[gridId].toggleTree(nodeId, e);
    }
}

// 편집 추적 전역 함수 (템플릿에서 호출)
function trackEdit(gridId, nodeId, el, field) {
    if (gridManagers[gridId]) {
        gridManagers[gridId].trackEdit(nodeId, el, field);
    }
}

// 그리드 초기화 함수
function initTreeGrid(config) {
    const manager = new TreeGridManager(config);
    gridManagers[config.gridId] = manager;
    return manager;
}


//행 데이터 업데이트 전역 함수
function updateGridRowData(gridId, nodeId, fieldUpdates) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].updateRowData(nodeId, fieldUpdates);
    }
    return false;
}

// 행 필드 업데이트 전역 함수
function updateGridRowField(gridId, nodeId, field, value) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].updateRowField(nodeId, field, value);
    }
    return false;
}

// 행 데이터 가져오기 전역 함수
function getGridRowData(gridId, nodeId) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].getRowData(nodeId);
    }
    return null;
}

//인덱스로 행 데이터 가져오기 전역 함수
function getGridRowDataByIndex(gridId, index) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].getRowDataByIndex(index);
    }
    return null;
}

// 인덱스로 행 데이터 업데이트 전역 함수
function updateGridRowDataByIndex(gridId, index, fieldUpdates) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].updateRowDataByIndex(index, fieldUpdates);
    }
    return false;
}

// 인덱스로 행 필드 업데이트 전역 함수
function updateGridRowFieldByIndex(gridId, index, field, value) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].updateRowFieldByIndex(index, field, value);
    }
    return false;
}

// nodeId와 인덱스 상호 변환 전역 함수
function getGridNodeIdByIndex(gridId, index) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].getNodeIdByIndex(index);
    }
    return null;
}

function getGridIndexByNodeId(gridId, nodeId) {
    if (gridManagers[gridId]) {
        return gridManagers[gridId].getIndexByNodeId(nodeId);
    }
    return -1;
}



