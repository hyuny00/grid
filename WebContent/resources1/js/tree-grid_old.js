class TreeGridManager {
    constructor(config) {
        this.gridId = config.gridId;
        this.searchFormId = config.searchFormId;
        this.urls = config.urls;
        this.templateId = config.templateId;
        this.pageSize = config.pageSize || 5;
        this.defaultFields = config.defaultFields || {}; // 추가
        
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
        
        $(`#${this.gridId}-container .btn-add-child`).on('click', function() {
            self.addChildToSelected();
        });
        
        $(`#${this.gridId}-container .btn-delete`).on('click', function() {
            self.deleteSelected();
        });
        
        $(`#${this.gridId}-container .btn-save`).on('click', function() {
            self.saveChanges();
        });
        
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
        const requestParams = Object.assign({}, searchParams, {
            page: this.currentPage,
            size: this.pageSize
        });
        
        console.log(`[${this.gridId}] Request params:`, JSON.stringify(requestParams));
        
        $.ajax({
            url: this.urls.parent,
            method: "GET",
            data: requestParams,
            success: (res) => {
                this.data = [];
                this.totalCount = 50; // 실제로는 서버에서 받아와야 함
                this.totalPages = 3;  // 실제로는 서버에서 받아와야 함
                
                for (let i = 0; i < res.length; i++) {
                    const item = res[i];
                    this.data.push({
                    	...item, // 서버에서 온 모든 필드를 그대로 사용
                    	
                        level: 0,
                        parentPath: '',
                        childrenLoaded: false,
                        children: [],
                        treeExpanded: false,
                        childYn: item.childYn || 'N'
                    });
                }
                this.renderTable();
                this.renderPagination();
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
            
            if (node.children && node.children.length > 0) {
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
        
    }
    
    createNodeRow(node, isVisible) {
        const hasChildren = (node.childYn === 'Y') || (node.children && node.children.length > 0);
        const toggleSymbol = hasChildren ? (node.treeExpanded ? '[-]' : '[+]') : '';
        const toggleClass = hasChildren ? 'tree-toggle' : 'tree-toggle no-children';
        
        const displayClass = node.level === 0 ? '' : (isVisible ? 'tree-row show' : 'tree-row');
        const indentStyle = 'padding-left: ' + (node.level * 20) + 'px;';
        
        const rowData = {
            ...node, // 노드의 모든 데이터를 포함
            gridId: this.gridId,
            displayClass: displayClass,
            indentStyle: indentStyle,
            toggleSymbol: toggleSymbol,
            toggleClass: toggleClass
        };
        
       // console.log(`[${this.gridId}] 템플릿 렌더링 데이터:`, rowData); // 추가
        
        return this.renderTemplate(this.templateId, rowData);
    }
    
    findNodeById(id, nodes = this.data, parentPath = '') {
        for (let i = 0; i < nodes.length; i++) {
            const node = nodes[i];
            if (node.id === id) {
                return { node: node, parent: null, parentPath: parentPath };
            }
            if (node.children && node.children.length > 0) {
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
        e.preventDefault();
        const result = this.findNodeById(nodeId);
        
        if (!result || !result.node) return;
        
        const node = result.node;
        
        if (!node.treeExpanded) {
            this.loadChildren(nodeId);
        } else {
            //node.treeExpanded = false;
            // 이미 로드된 경우 토글
        	node.treeExpanded = !node.treeExpanded;
            this.renderTable();
        }
    }
    
    loadChildren(nodeId) {
        $.ajax({
            url: this.urls.children.replace('{id}', nodeId),
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
                name: "새행",
                ...this.defaultFields, // 기본 필드들 적용
                level: 0,
                parentPath: '',
                childrenLoaded: true, 
                children: [],
                treeExpanded: false,
                isNew: true,
                childYn: 'N'
           };
        
        this.data.unshift(newRow);
        this.renderTable();
        this.addedRows.add(this.getNodeKey(newRow));
        
        this.totalCount++;
        this.updatePageInfo();
    }
    
    addChildToSelected() {
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
        const result = this.findNodeById(parentId);
        if (!result || !result.node) return;
        
        const parent = result.node;
        const newChildId = Date.now() + Math.floor(Math.random() * 1000);
        const newChild = {
                id: newChildId,
                name: "새 자식행",
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
        this.addedRows.add(this.getNodeKey(newChild));
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
        this.totalCount -= nodesToDelete.length;
        this.updatePageInfo();
    }
    
    deleteNodeRecursively(nodeId) {
        const deleteFromArray = (nodes, parentArray = null, parentIndex = -1) => {
            for (let i = nodes.length - 1; i >= 0; i--) {
                const node = nodes[i];
                if (node.id === nodeId) {
                    const nodeKey = this.getNodeKey(node);
                    if (!this.modifiedRows.has(nodeKey)) {
                        this.deletedRows.add(nodeKey);
                    } else {
                        this.modifiedRows.delete(nodeKey);
                    }
                    
                    if (node.children && node.children.length > 0) {
                        this.markChildrenAsDeleted(node.children);
                    }
                    
                    if (parentArray === null) {
                        this.data.splice(i, 1);
                    } else {
                        nodes.splice(i, 1);
                    }
                    return true;
                }
                
                if (node.children && node.children.length > 0) {
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
        for (let i = 0; i < children.length; i++) {
            const child = children[i];
            const childKey = this.getNodeKey(child);
            if (!this.modifiedRows.has(childKey)) {
                this.deletedRows.add(childKey);
            } else {
                this.modifiedRows.delete(childKey);
            }
            
            if (child.children && child.children.length > 0) {
                this.markChildrenAsDeleted(child.children);
            }
        }
    }
    
    toggleAll(checked) {
        $(`#${this.gridId}-body .row-check`).prop("checked", checked);
    }
    
    trackEdit(nodeId, el, field) {
        const value = el.value !== undefined ? el.value : (el.innerText || el.textContent || "").trim();
        
        //console.log(`[${this.gridId}] trackEdit 호출 - nodeId: ${nodeId}, field: ${field}, value: ${value}`);
        
        const result = this.findNodeById(nodeId);
        
        if (result && result.node) {
           // console.log(`[${this.gridId}] 변경 전 값: ${result.node[field]}, 변경 후 값: ${value}`);
            
            result.node[field] = value;
            const nodeKey = this.getNodeKey(result.node);
            
            if (!this.addedRows.has(nodeKey)) {
                this.modifiedRows.add(nodeKey);
               // console.log(`[${this.gridId}] modifiedRows에 추가: ${nodeKey}`);
            }
        }
    }
    
    saveChanges() {
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
        
        // 실제 서버 전송
        const payload = { 
            addedNodes: nodesToAdd,
            updatedNodes: nodesToUpdate,
            deletedNodes: nodesToDelete
        };
        
        /*
        $.ajax({
            url: this.urls.save,
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
    
    renderPagination() {
        // 페이지네이션 렌더링 (필요시 구현)
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
