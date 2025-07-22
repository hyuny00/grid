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
        this.isSaveEnabled = !!(this.urls.saveExlUrl || this.urls.saveUrl);
        
        this.isPagingEnabled = !!(this.pageSize); // pageSize가 있으면 페이징 활성화
        
        
        // 컨텍스트 메뉴 설정 추가
        this.contextMenuEnabled = config.contextMenuEnabled || false;
        this.contextMenuItems = config.contextMenuItems || [];
        this.onContextMenuClick = config.onContextMenuClick; // 콜백 함수

        
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
        
        
        // 엑셀 업로드 관련 설정 추가
        this.excelUploadEnabled = config.excelUploadEnabled || false;
        this.excelFileInputId = config.excelFileInputId; // 파일 input ID
        this.excelUploadBtnId = config.excelUploadBtnId; // 업로드 버튼 ID
        
        // 엑셀 데이터 저장용 (원본 엑셀 데이터를 모두 보관)
        this.excelData = [];
        this.isExcelMode = config.isExcelMode || false; // 엑셀 모드인지 일반 모드인지 구분
        //엑셀 업로드 관련 설정 추가end
        
        this.init();
        
        this.lastToastrMessage = null;
    }
  
    setPageSize(newPageSize) {
        this.pageSize = newPageSize;
        searchData();
    }
    
    init() {
    	if( this.isExcelMode){
    		 this.fetchExcelData();
    		 this.bindEvents();
    	}else{
    		 this.fetchData();
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
        
        $(`#${this.gridId}-container .btn-delete`).on('click', function() {
            self.deleteSelected();
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
            $(`#${this.excelFileInputId}`).on('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    self.handleExcelUpload(file);
                }
            });
        }//엑셀 업로드 기능이 활성화된 경우만 이벤트 바인딩 end
        
        // 행 클릭 이벤트 바인딩
        this.bindRowClickEvents();
    }
    
   
    
    /*
    getSearchParams() {
        const params = {};
        
        $(`#${this.searchFormId}`).find('input, select, textarea').each(function() {
            const $this = $(this);
            const name = $this.attr('name');
            const value = $this.val();
            
            if (name && value && value.trim() !== '') {
                params[name] = value.trim();
            }
        });
        
        return params;
    }*/
    
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
        
        return params;
    }

  
    
    searchData() {
        this.currentPage = 1;
        
        if( this.isExcelMode){
        	 this.fetchExcelData();
        }else{
        	 this.fetchData(currentPage);
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
        	contentType: "application/json",
        	data: JSON.stringify(requestParams), 
            success: (res) => {
            	
                this.data = [];
              
                // 페이징이 활성화된 경우만 페이징 정보 설정
                if (this.isPagingEnabled) {
                    this.totalCount = res.total; // 전체 건수
                    this.totalPages = Math.ceil(this.totalCount / this.pageSize);
                } else {
                    this.totalCount = res.data.length;
                    this.totalPages = 1;
                }

                // 데이터를 res.data에서 꺼냄
                const rows = res.data || [];  // res.data가 undefined/null이면 빈 배열로 대체
                
                
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
            },
            error: (xhr, status, error) => {
                console.log(`[${this.gridId}] 서버 연결 실패:`, error);
                alert("데이터를 불러오는데 실패했습니다.");
            }
        });
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
            nodeMap.set(item.no, nodeData);
        });
        
        // 2단계: 부모-자식 관계 설정 및 레벨 계산
        nodeMap.forEach((node, nodeNo) => {
            //const parentNo =  node.parentNo; // patentNo 또는 parentNo 필드 사용
            
            const parentNo = node[this.urlParentParamKey];
            
            if (parentNo && nodeMap.has(parentNo)) {
                // 부모가 있는 경우
                const parent = nodeMap.get(parentNo);
                parent.children.push(node);
                node.level = parent.level + 1;
                node.parentPath = parent.parentPath + parent.no + '/';
            } else {
                // 루트 노드인 경우
                rootNodes.push(node);
                node.level = 0;
                node.parentPath = '';
            }
        });
        
        return rootNodes;
    }
    
    fetchExcelData() {

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
    
    renderTable() {
        const tbody = $(`#${this.gridId}-body`);
        tbody.empty();
        
        const renderNode = (node, isVisible = true) => {
            tbody.append(this.createNodeRow(node, isVisible));
            
            // 트리 모드일 때만 자식 노드 렌더링
            if (this.isTreeMode && node.children && node.children.length > 0) {
                for (let i = 0; i < node.children.length; i++) {
                    const child = node.children[i];
                    const childVisible = isVisible && node.treeExpanded;
                    renderNode(child, childVisible);
                }
            }
        };
        
        for (let i = 0; i < this.data.length; i++) {
            renderNode(this.data[i]);
        }
        
        // 날짜 입력 필드 초기화
        $(`#${this.gridId}-container .date-input`).datepicker({ 
            dateFormat: "yy-mm-dd",
            showButtonPanel: true
        });
        
        // 모든 폼 요소의 값을 data-value 속성으로 설정
        $(`#${this.gridId}-container [data-value]`).each(function() {
            const $element = $(this);
            const value = $element.data('value');
            
            /*
            if (value !== undefined && value !== null) {
                if ($element.is('select')) {
                    $element.val(value);
                } else if ($element.is('input[type="checkbox"]')) {
                    $element.prop('checked', value === true || value === 'true' || value === 'Y');
                } else if ($element.is('input, textarea')) {
                    $element.val(value);
                }
            }*/
            
            if (value !== undefined && value !== null) {
                if ($element.is('select')) {
                    $element.val(value);
                } else if ($element.is('input[type="checkbox"]')) {
                    $element.prop('checked', value === true || value === 'true' || value === 'Y');
                }  else if ($element.is('input[type="radio"]')) {
                    const name = $element.attr('name');
                    // 직접 해당 값의 라디오버튼만 체크 (브라우저가 자동으로 다른 것들 해제)
                    $(`#${this.gridId}-container input[type="radio"][name="${name}"][value="${value}"]`).prop('checked', true);
                } else if ($element.is('input, textarea')) {
                    $element.val(value);
                }
            }
        });
        
        
        // 편집 이벤트 바인딩
        this.bindEditEvents();
    }
    
    // 편집 이벤트 바인딩 메서드 수정
    bindEditEvents() {
        const self = this;
        
        // 라디오버튼을 제외한 편집 가능한 요소들
        $(`#${this.gridId}-body`).off('input change blur keyup').on('input change blur keyup', 
            'input[data-field]:not([type="radio"]), textarea[data-field], select[data-field], [contenteditable][data-field]', 
            function() {
                const $this = $(this);
                const nodeId = String($this.closest('tr').data('id'));
                const field = $this.data('field');
                
                if (nodeId && field) {
                    self.trackEdit(nodeId, this, field);
                }
            }
        );
        
        // 라디오버튼 전용 처리 (change 이벤트만)
        $(`#${this.gridId}-body`).off('change.radio').on(
            'change.radio',
            'input[type="radio"][data-field]',
            function () {
                const $this = $(this);
                const nodeId = String($this.closest('tr').data('id'));
                const field = $this.data('field');
                if (nodeId && field) {
                    self.trackEdit(nodeId, this, field);
                }
            }
        );
        
        
        // 트리 토글 이벤트 추가
        $(`#${this.gridId}-body`).off('click.treeToggle').on('click.treeToggle', '.tree-toggle', function(e) {
            e.preventDefault();
            const nodeId = String($(this).closest('tr').data('id'));
            if (nodeId) {
                self.toggleTree(nodeId, e);
            }
        });
        
        // contenteditable 요소에 대한 추가 이벤트
        $(`#${this.gridId}-body`).off('paste').on('paste', '[contenteditable][data-field]', function(e) {
            const $this = $(this);
            const nodeId = String($this.closest('tr').data('id')); // 문자열로 변환
            const field = $this.data('field');
            
            setTimeout(() => {
                if (nodeId && field) {
                    self.trackEdit(nodeId, this, field);
                }
            }, 10);
        });
    }
    
    createNodeRow(node, isVisible) {
        let hasChildren = false;
        let toggleSymbol = '';
        let toggleClass = 'tree-toggle no-children';
        
        // 트리 모드일 때만 트리 관련 UI 설정
        if (this.isTreeMode) {
            hasChildren = (node.childYn === 'Y') || (node.children && node.children.length > 0);
            toggleSymbol = hasChildren ? (node.treeExpanded ? '[-]' : '[+]') : '';
            toggleClass = hasChildren ? 'tree-toggle' : 'tree-toggle no-children';
        }
        
        const displayClass = node.level === 0 ? '' : (isVisible ? 'tree-row show' : 'tree-row');
        const indentStyle = 'padding-left: ' + (node.level * 20) + 'px;';
        
        // ID가 없으면 자동 생성
        if (!node.id) {
            node.id = 'auto_' + Date.now().toString() + '_' + Math.floor(Math.random() * 10000).toString();
        }
        
        const rowData = {
            ...node,
            gridId: this.gridId,
            displayClass: displayClass,
            indentStyle: indentStyle,
            toggleSymbol: toggleSymbol,
            toggleClass: toggleClass,
            isTreeMode: this.isTreeMode
        };
        
        // 템플릿 렌더링 후 tr 요소에 data-id 속성 추가
        const rowHtml = this.renderTemplate(this.templateId, rowData);
        const $row = $(rowHtml);
        $row.attr('data-id', node.id);
        $row.attr('data-level', node.level);
        $row.attr('data-parent-path', node.parentPath);
        
        return $row[0].outerHTML;
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
    
    addRow() {
        //const newId = Date.now().toString() + Math.floor(Math.random() * 1000).toString(); // 문자열로 생성
        const newId = 'N_' + Date.now().toString() + Math.floor(Math.random() * 1000).toString();
        const newRow = { 
            id: newId, 
            
            ...this.defaultFields, // 기본 필드들 적용
         
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
        
        // 행 추가 위치에 따라 처리
        if (this.addRowPosition === 'bottom') {
            this.data.push(newRow);
        } else {
            this.data.unshift(newRow);
        }
        
        this.renderTable();
        
        // 저장 기능이 활성화된 경우만 추가된 행 추적
        if (this.isSaveEnabled) {
            //this.addedRows.add(this.getNodeKey(newRow));
            this.modifiedRows.add(this.getNodeKey(newRow));
        }
        
        //this.totalCount++;
        //this.updatePageInfo();
        
        // 새로 추가된 행의 첫 번째 편집 가능한 필드에 포커스
        setTimeout(() => {
            const $newRow = $(`#${this.gridId}-body tr[data-id="${newId}"]`);
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
        
        
        
     // ★ 수정: 새 행 ID와 부모 ID(null) 반환
        return {
            newId: newId,
            parentId: null
        };
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
        
        checkedRows.each(function () {
            const id = String($(this).data("id")); // 문자열로 변환
            const level = parseInt($(this).data("level"));
            nodesToDelete.push({ id: id, level: level });
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
    }
    
    
    deleteSelected() {
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
            
            checkedRows.each(function () {
                const id = $(this).data("id").toString();
                const level = parseInt($(this).data("level"));
                nodesToDelete.push({ id: id, level: level });
            });
            
            $.ajax({
                url: this.urls.deleteUrl,
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify({deleteNodes : nodesToDelete}),
                success: (res) => {
                	
                	toastr["success"](`${checkedRows.length}개 행이 삭제되었습니다.`);
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
                            this.deletedRows.add(nodeKey);
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
                this.deletedRows.add(childKey);
            } else {
                this.modifiedRows.delete(childKey);
            }
            
            if (this.isTreeMode && child.children && child.children.length > 0) {
                this.markChildrenAsDeleted(child.children);
            }
        }
    }
    
    toggleAll(checked) {
        $(`#${this.gridId}-body .row-check`).prop("checked", checked);
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
        
        this.deletedRows.forEach((key) => {
            const id = key.slice(2); // 이미 문자열이므로 그대로 사용
            nodesToDelete.push(id);
        });
        
       // console.log(`[${this.gridId}] 추가된 노드:`, nodesToAdd);
        console.log(`[${this.gridId}] 수정된 노드:`, nodesToUpdate);
        //console.log(`[${this.gridId}] 삭제된 노드:`, nodesToDelete);
        
        // 실제 서버 전송
        const payload = { 
          //  addedNodes: nodesToAdd,
            updatedNodes: nodesToUpdate,
           // deletedNodes: nodesToDelete
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
    
    renderTemplate(templateId, data) {
        const template = document.getElementById(templateId).innerHTML;
        return template.replace(/\{\{(\w+)\}\}/g, function(match, key) {
            return data[key] !== undefined ? data[key] : '';
        });
    }
    
    updatePageInfo() {
        // 페이지 정보 업데이트
        $(`#${this.gridId}-page-info`).text(`총 ${this.totalCount}건`);
    }
    
    
    renderPagination() {
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
    
 // 2. 행 클릭 이벤트 바인딩 메서드 추가
    bindRowClickEvents() {
        const self = this;
        
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
               // console.log('클릭된 행 데이터:', rowData);
                
                // 행 선택 표시 (선택사항)
                self.selectRow($row);
                
                // 콜백 함수가 있다면 실행
                if (self.onRowClick && typeof self.onRowClick === 'function') {
                    self.onRowClick(rowData, $row);
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

 //  행 선택 표시 메서드 (선택사항)
 selectRow($row) {
     // 이전 선택 해제
     $(`#${this.gridId}-body tr`).removeClass('selected');
     
     // 현재 행 선택
     $row.addClass('selected');
 }
 
 
 
 
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
     
    // console.log('엑셀 헤더:', headers);
    // console.log('엑셀 데이터:', rows);
     
     
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
   
	 
	 
	 const enterLine = '---------------------------------------<br>'
		  
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
	 const enterLine = '---------------------------------------<br>'
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
	 
	 const enterLine = '---------------------------------------<br>'
		 
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
      
      // 우클릭 이벤트
      $(`#${this.gridId}-body`).off('contextmenu.contextMenu').on('contextmenu.contextMenu', 'tr', function(e) {
          e.preventDefault();
          
          const $row = $(this);
          const nodeId = String($row.data('id'));
          
          if (!nodeId || nodeId === 'undefined') return;
          
          // 다른 모든 행의 선택 해제
          $(`#${self.gridId}-body tr`).removeClass('context-selected');
          // 현재 행 선택 표시
          $row.addClass('context-selected');
          
          self.showContextMenu(e.pageX, e.pageY, nodeId, $row);
      });
      
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
      if (!result || !result.node) return;
      
      const node = result.node;
      
      // 컨텍스트 메뉴 HTML 생성
      const menuId = `${this.gridId}-context-menu`;
      let menuHtml = `<div id="${menuId}" class="context-menu">`;
      
      this.contextMenuItems.forEach((item, index) => {
          // 조건부 표시 체크
          if (item.condition && !item.condition(node, $row)) {
              return;
          }
          
          const disabled = item.disabled && item.disabled(node, $row) ? 'disabled' : '';
          const separator = item.separator ? 'separator' : '';
          
          if (item.separator) {
              menuHtml += `<div class="context-menu-separator"></div>`;
          } else {
              menuHtml += `
                  <div class="context-menu-item ${disabled}" data-action="${item.action}" data-node-id="${nodeId}">
                      ${item.icon ? `<i class="${item.icon}"></i>` : ''}
                      <span>${item.label}</span>
                      ${item.shortcut ? `<span class="shortcut">${item.shortcut}</span>` : ''}
                  </div>
              `;
          }
      });
      
      menuHtml += '</div>';
      
      // 메뉴를 body에 추가
      $('body').append(menuHtml);
      
      const $menu = $(`#${menuId}`);
      
      // 메뉴 위치 조정 (화면 밖으로 나가지 않도록)
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
          top: finalY + 'px'
      }).show();
      
      // 메뉴 아이템 클릭 이벤트
      $menu.off('click.contextMenuItem').on('click.contextMenuItem', '.context-menu-item:not(.disabled)', (e) => {
          const action = $(e.currentTarget).data('action');
          const targetNodeId = $(e.currentTarget).data('node-id');
          
          this.hideContextMenu();
          
          // 콜백 함수 호출
          if (this.onContextMenuClick) {
              this.onContextMenuClick(action, targetNodeId, node, $row, this);
          }
      });
  }
  
  hideContextMenu() {
      $('.context-menu').remove();
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