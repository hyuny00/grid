class TreeGridManager {
    constructor(config) {
        this.gridId = config.gridId;
        this.searchFormId = config.searchFormId;
        this.urls = config.urls;
        this.templateId = config.templateId;
        this.pageSize = config.pageSize; // undefined면 페이징 비활성화
        this.defaultFields = config.defaultFields || {};
        
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
                
                
                /*
                // 페이징이 활성화된 경우만 페이징 정보 설정
                if (this.isPagingEnabled) {
                    this.totalCount = res.total; // 실제로는 서버에서 받아와야 함
                    this.totalPages = Math.ceil(this.totalCount / this.pageSize); // pageSize = 10이라면, totalPages = 10
                } else {
                    this.totalCount = res.length;
                    this.totalPages = 1;
                }
                */
                
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
                
                //for (let i = 0; i < res.length; i++) {
                   // const item = res[i];
                
                for (let i = 0; i < rows.length; i++) {
                    const item = rows[i];
                    
                    const nodeData = {
                        ...item, // 서버에서 온 모든 필드를 그대로 사용
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
                const nodeId = parseInt($this.closest('tr').data('id'));
                const field = $this.data('field');
                
                if (nodeId && field) {
                    self.trackEdit(nodeId, this, field);
                }
            }
        );
        
        // contenteditable 요소에 대한 추가 이벤트
        $(`#${this.gridId}-body`).off('paste').on('paste', '[contenteditable][data-field]', function(e) {
            const $this = $(this);
            const nodeId = parseInt($this.closest('tr').data('id'));
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
        
        const rowData = {
            ...node, // 노드의 모든 데이터를 포함
            gridId: this.gridId,
            displayClass: displayClass,
            indentStyle: indentStyle,
            toggleSymbol: toggleSymbol,
            toggleClass: toggleClass,
            isTreeMode: this.isTreeMode // 템플릿에서 트리 모드 여부 확인 가능
        };
        
        return this.renderTemplate(this.templateId, rowData);
    }
    
    findNodeById(id, nodes = this.data, parentPath = '') {
        for (let i = 0; i < nodes.length; i++) {
            const node = nodes[i];
            if (node.id === id) {
                return { node: node, parent: null, parentPath: parentPath };
            }
            if (this.isTreeMode && node.children && node.children.length > 0) {
                const found = this.findNodeById(id, node.children, parentPath + node.id + '/');
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
            node.treeExpanded = false;
            this.renderTable();
        }
    }
    
    loadChildren(nodeId) {
        // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;
        
        $.ajax({
        	url: this.urls.childrenUrl.replace('{id}', nodeId),
            method: "GET",
            success: (children) => {
                const result = this.findNodeById(nodeId);
                if (result && result.node) {
                    const node = result.node;
                    node.children = [];
                    for (let i = 0; i < children.length; i++) {
                        const child = children[i];
                        node.children.push({
                            ...child, // 서버에서 온 모든 필드를 그대로 사용
                            level: node.level + 1,
                            parentPath: node.parentPath + node.id + '/',
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
        const newId = Date.now() + Math.floor(Math.random() * 1000);
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
        
        this.data.unshift(newRow);
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
        const parentId = parseInt(selectedRow.data("id"));
        
        this.addChildRow(parentId);
    }
    
    addChildRow(parentId) {
        // 트리 모드가 아니면 실행하지 않음
        if (!this.isTreeMode) return;
        
        const result = this.findNodeById(parentId);
        if (!result || !result.node) return;
        
        const parent = result.node;
        const newChildId = Date.now() + Math.floor(Math.random() * 1000);
        const newChild = {
            id: newChildId,
            ...this.defaultFields, // 기본 필드들 적용
         
            level: parent.level + 1,
            parentPath: parent.parentPath + parent.id + '/',
            childrenLoaded: true,
            children: [],
            treeExpanded: false,
            isNew: true,
            childYn: 'N'
        };
        
        if (!parent.children) {
            parent.children = [];
        }
        
        parent.children.unshift(newChild);
        parent.childrenLoaded = true;
        parent.treeExpanded = true;
        
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
    
    deleteSelected() {
        const checkedRows = $(`#${this.gridId}-body input.row-check:checked`).closest("tr");
        
        if (checkedRows.length === 0) {
            alert("삭제할 행을 선택해주세요.");
            return;
        }
        
        const nodesToDelete = [];
        
        checkedRows.each(function () {
            const id = parseInt($(this).data("id"));
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
    
    deleteNodeRecursively(nodeId) {
        const deleteFromArray = (nodes, parentArray = null, parentIndex = -1) => {
            for (let i = nodes.length - 1; i >= 0; i--) {
                const node = nodes[i];
                if (node.id === nodeId) {
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
            
          //  console.log(`[${this.gridId}] 필드 수정: ID=${nodeId}, 필드=${field}, 값=${value}`);
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
            const id = parseInt(key.slice(2));
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
            const id = parseInt(key.slice(2));
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
            const id = parseInt(key.slice(2));
            nodesToDelete.push(id);
        });
        
        console.log(`[${this.gridId}] 추가된 노드:`, nodesToAdd);
        console.log(`[${this.gridId}] 수정된 노드:`, nodesToUpdate);
        console.log(`[${this.gridId}] 삭제된 노드:`, nodesToDelete);
        /*
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
        */
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
    
    renderPagination2() {
        // 페이징이 비활성화된 경우 실행하지 않음
        if (!this.isPagingEnabled) return;
        
        // 페이지네이션 렌더링
        const pagination = $(`#${this.gridId}-pagination`);
        pagination.empty();
        
        if (this.totalPages > 1) {
            for (let i = 1; i <= this.totalPages; i++) {
                const isActive = i === this.currentPage ? 'active' : '';
                const pageItem = $(`<li class="page-item ${isActive}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>`);
                
                pageItem.find('a').on('click', (e) => {
                    e.preventDefault();
                    this.fetchData(i);
                });
                
                pagination.append(pageItem);
            }
        }
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


}

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