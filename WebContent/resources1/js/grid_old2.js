class TreeGridManager {
    constructor(config) {
        this.gridId = config.gridId;
        this.searchFormId = config.searchFormId;
        this.urls = config.urls;
        this.templateId = config.templateId;
        this.pageSize = config.pageSize; // undefined면 페이징 비활성화
        this.defaultFields = config.defaultFields || {};
        this.urlParamKey = config.urlParamKey; 
        
        this.addRowPosition = config.addRowPosition || 'top'; // 행 추가 위치: 'top' 또는 'bottom'
        this.addChildPosition = config.addChildPosition || 'bottom'; // 자식행 추가 위치: 'top' 또는 'bottom'
        
        // 기능 활성화 여부 체크
        this.isTreeMode = !!(this.urls.childrenUrl);
        this.isSaveEnabled = !!(this.urls.saveUrl);
        
        this.isPagingEnabled = !!(this.pageSize); // pageSize가 있으면 페이징 활성화
        
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
        
        this.init();
    }
    
   
    
    init() {
        this.fetchData();
        this.bindEvents();
    }
    
    bindEvents() {
        const self = this;
        
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
        
        // 행 클릭 이벤트 바인딩
        this.bindRowClickEvents();
    }
    
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
    }
    
    searchData() {
        this.currentPage = 1;
        this.fetchData(1);
    }
    
    resetSearch() {
        $(`#${this.searchFormId}`)[0].reset();
        this.searchData();
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
        	method: "POST", // ★ 반드시 명시
        	contentType: "application/json", // JSON으로 보내겠다고 명시
        	data: JSON.stringify(requestParams), // JSON으로 직렬화
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
                this.renderTable();
                
                // 페이징이 활성화된 경우만 페이지네이션 렌더링
                if (this.isPagingEnabled) {
                    this.renderPagination();
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
            if (value !== undefined && value !== null) {
                if ($element.is('select')) {
                    $element.val(value);
                } else if ($element.is('input[type="checkbox"]')) {
                    $element.prop('checked', value === true || value === 'true' || value === 'Y');
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
        
        // 모든 편집 가능한 요소들에 대해 이벤트 바인딩
        $(`#${this.gridId}-body`).off('input change blur keyup').on('input change blur keyup', 
            'input[data-field], textarea[data-field], select[data-field], [contenteditable][data-field]', 
            function() {
                const $this = $(this);
                const nodeId = String($this.closest('tr').data('id')); // 문자열로 변환
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
            // 1. 현재 노드에 urlParamKey 값이 없으면 토글 버튼 숨김
            const paramValue = node[this.urlParamKey];
            if (!paramValue) {
                hasChildren = false;
                toggleSymbol = '';
                toggleClass = 'tree-toggle no-children hidden';
            } else {
                // 2. 자식이 로드되었고, 자식들 중에 urlParamKey 값이 없는 경우도 체크
                if (node.childrenLoaded && node.children && node.children.length > 0) {
                    const hasValidChildren = node.children.some(child => child[this.urlParamKey]);
                    if (!hasValidChildren) {
                        hasChildren = false;
                        toggleSymbol = '';
                        toggleClass = 'tree-toggle no-children hidden';
                    } else {
                        hasChildren = true;
                        toggleSymbol = node.treeExpanded ? '[-]' : '[+]';
                        toggleClass = 'tree-toggle';
                    }
                } else {
                    // 아직 자식이 로드되지 않은 경우, childYn 기준으로 판단
                    hasChildren = (node.childYn === 'Y');
                    toggleSymbol = hasChildren ? (node.treeExpanded ? '[-]' : '[+]') : '';
                    toggleClass = hasChildren ? 'tree-toggle' : 'tree-toggle no-children';
                }
            }
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
        // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;
        
        e.preventDefault();
        const result = this.findNodeById(nodeId);
        
        if (!result || !result.node) return;
        
        const node = result.node;
        
        if (!node.treeExpanded) {
            this.loadChildren(nodeId);
        } else {
            //node.treeExpanded = false;
        	node.treeExpanded = !node.treeExpanded;
            this.renderTable();
        }
    }
    
    loadChildren(nodeId) {
        // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;
        
        const result = this.findNodeById(nodeId);
        if (!result || !result.node) return;
        
        const node = result.node;
        const paramValue = node[this.urlParamKey];
        
        $.ajax({
            url: this.urls.childrenUrl.replace(`{${this.urlParamKey}}`, paramValue),
            method: "GET",
            success: (response) => {
                const children = response.data || [];
                
                if (result && result.node) {
                    const node = result.node;
                    node.children = [];
                    
                    // 자식들 중에 urlParamKey 값이 있는지 확인
                    let hasValidChildren = false;
                    
                    for (let i = 0; i < children.length; i++) {
                        const child = children[i];
                        
                        // 자식에 urlParamKey 값이 있는지 확인
                        if (child[this.urlParamKey]) {
                            hasValidChildren = true;
                        }
                        
                        node.children.push({
                            ...child,
                            level: node.level + 1,
                            parentPath: node.parentPath + (node[this.urlParamKey] || node.id) + '/',
                            childrenLoaded: false,
                            children: [],
                            treeExpanded: false,
                            childYn: child.childYn || 'N'
                        });
                    }
                    
                    // 자식들 중에 유효한 값이 없으면 부모의 childYn을 'N'으로 설정
                    if (!hasValidChildren && children.length > 0) {
                        node.childYn = 'N';
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
        const newId = Date.now().toString() + Math.floor(Math.random() * 1000).toString(); // 문자열로 생성
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
            this.addedRows.add(this.getNodeKey(newRow));
        }
        
        this.totalCount++;
        this.updatePageInfo();
        
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
        
        this.addChildRow(parentId);
    }
    
    addChildRow(parentId) {
    	// 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;
        
        const result = this.findNodeById(parentId);
        if (!result || !result.node) return;
        
        const parent = result.node;
        const newChildId = Date.now().toString() + Math.floor(Math.random() * 1000).toString();
        const newChild = {
            id: newChildId,
            ...this.defaultFields,
            level: parent.level + 1,
            parentPath: parent.parentPath + (parent[this.urlParamKey] || parent.id) + '/',
            childrenLoaded: true,
            children: [],
            treeExpanded: false,
            isNew: true,
            childYn: 'N'
            // urlParamKey 값은 없음 (새로 추가된 행이므로)
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
        
        // ★ 부모의 자식들 중에 유효한 urlParamKey가 없으면 부모 토글 숨김
        const hasValidChildren = parent.children.some(child => child[this.urlParamKey]);
        if (!hasValidChildren) {
            parent.childYn = 'N'; // 토글 버튼이 숨겨지도록 설정
        }
        
        this.renderTable();
        
        this.renderTable();
        
        // 저장 기능이 활성화된 경우만 추가된 행 추적
        if (this.isSaveEnabled) {
            this.addedRows.add(this.getNodeKey(newChild));
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
        
        const nodesToDelete = [];
        
        checkedRows.each(function () {
            const id = $(this).data("id").toString();
            const level = parseInt($(this).data("level"));
            nodesToDelete.push({ id: id, level: level });
        });
        
        
        $.ajax({
        	url: this.urls.deleteUrl,
        	method: "POST", // ★ 반드시 명시
        	contentType: "application/json", // JSON으로 보내겠다고 명시
        	data: JSON.stringify({deleteNodes : nodesToDelete}), // JSON으로 직렬화
            success: (res) => {
            	 this.fetchData(this.currentPage);
            },
            error: (xhr, status, error) => {
                alert("삭제에 실패했습니다.");
            }
        });
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
        } else if (el.value !== undefined) {
            value = el.value;
        } else {
            value = (el.innerText || el.textContent || "").trim();
        }
        
        const result = this.findNodeById(nodeId);
        
        if (result && result.node) {
            result.node[field] = value;
            
            // 저장 기능이 활성화된 경우만 수정된 행 추적
            if (this.isSaveEnabled) {
                const nodeKey = this.getNodeKey(result.node);
                if (!this.addedRows.has(nodeKey)) {
                    this.modifiedRows.add(nodeKey);
                }
            }
        }
    }
    
    saveChanges() {
        // 저장 기능이 비활성화된 경우 실행하지 않음
        if (!this.isSaveEnabled) {
            alert("저장 기능이 비활성화되어 있습니다.");
            return;
        }
        
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
        
        console.log(`[${this.gridId}] 추가된 노드:`, nodesToAdd);
        console.log(`[${this.gridId}] 수정된 노드:`, nodesToUpdate);
        //console.log(`[${this.gridId}] 삭제된 노드:`, nodesToDelete);
        
        // 실제 서버 전송
        const payload = { 
            addedNodes: nodesToAdd,
            updatedNodes: nodesToUpdate,
            deletedNodes: nodesToDelete
        };
        
        $.ajax({
        	url: this.urls.saveUrl,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: (res) => {
                alert("저장 성공");
                this.addedRows.clear();
                this.modifiedRows.clear();
                this.deletedRows.clear();
                this.fetchData(this.currentPage);
            },
            error: (xhr, status, error) => {
                console.log(`[${this.gridId}] 저장 실패:`, error);
                alert("저장에 실패했습니다.");
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

        // 페이지 번호
        for (let i = 1; i <= this.totalPages; i++) {
            pagination.append(createButton(i, i, false, i === this.currentPage));
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

 // 4. 행 선택 표시 메서드 (선택사항)
 selectRow($row) {
     // 이전 선택 해제
     $(`#${this.gridId}-body tr`).removeClass('selected');
     
     // 현재 행 선택
     $row.addClass('selected');
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