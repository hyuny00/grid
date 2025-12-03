class ColgroupTableResizer {
    constructor() {
        this.currentResizer = null;
        this.currentTable = null;
        this.currentColumn = null;
        this.startX = 0;
        this.startWidth = 0;
        this.indicator = null;
        this.initializeAllTables();
        this.bindGlobalEvents();
        this.createIndicator(); // 동적으로 인디케이터 생성
    }

    createIndicator() {
        // 기존 인디케이터가 있으면 제거
        const existingIndicator = document.getElementById('dynamic-resize-indicator');
        if (existingIndicator) {
            existingIndicator.remove();
        }

        this.indicator = document.createElement('div');
        this.indicator.id = 'dynamic-resize-indicator';
        this.indicator.className = 'resize-indicator';
        this.indicator.style.cssText = `
            position: fixed;
            top: 10px;
            right: 10px;
            background: rgba(0, 0, 0, 0.8);
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            z-index: 9999;
            display: none;
            pointer-events: none;
        `;
        document.body.appendChild(this.indicator);
    }

    initializeAllTables() {
        const tables = document.querySelectorAll('.resizable-table');
        tables.forEach(table => this.initializeTable(table));
    }

    initializeTable(table) {
        const resizers = table.querySelectorAll('.resizer');

        resizers.forEach((resizer, columnIndex) => {
            // 기존 핸들러 제거
            if (resizer._resizeHandler) {
                resizer.removeEventListener('mousedown', resizer._resizeHandler);
            }

            // 새 핸들러 추가
            resizer._resizeHandler = (e) => this.startResize(e, resizer, table, columnIndex);
            resizer.addEventListener('mousedown', resizer._resizeHandler);
        });
    }

    bindGlobalEvents() {
        document.addEventListener('mousemove', (e) => this.doResize(e));
        document.addEventListener('mouseup', () => this.stopResize());

        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === 1) {
                        const newTables = node.querySelectorAll ?
                            node.querySelectorAll('.resizable-table') : [];
                        newTables.forEach(table => this.initializeTable(table));

                        if (node.classList && node.classList.contains('resizable-table')) {
                            this.initializeTable(node);
                        }
                    }
                });
            });
        });

        observer.observe(document.body, { childList: true, subtree: true });
    }

    startResize(e, resizer, table, columnIndex) {
        e.preventDefault();
        e.stopPropagation();

        this.currentResizer = resizer;
        this.currentTable = table;
        this.currentColumn = columnIndex;
        this.startX = e.pageX;

        // colgroup에서 현재 넓이 가져오기
        const colgroup = table.querySelector('colgroup');
        const cols = colgroup.querySelectorAll('col');
        const currentCol = cols[columnIndex];

        if (currentCol) {
            const currentWidth = currentCol.style.width;
            this.startWidth = parseFloat(currentWidth) || 10; // 퍼센트 값
        }

        resizer.classList.add('resizing');
        document.body.style.cursor = 'col-resize';
        document.body.style.userSelect = 'none';

        this.setOtherResizersState(table, false);
        this.showIndicator();
    }

    doResize(e) {
        if (!this.currentResizer || !this.currentTable) return;

        const deltaX = e.pageX - this.startX;
        const tableWidth = this.currentTable.offsetWidth;

        // 픽셀 변화를 퍼센트로 변환
        const deltaPercent = (deltaX / tableWidth) * 100;
        let newWidth = this.startWidth + deltaPercent;

        // 최소/최대 넓이 제한
        const minWidth = 3;  // 최소 3%
        const maxWidth = 60; // 최대 60%

        newWidth = Math.max(minWidth, Math.min(maxWidth, newWidth));

        // colgroup 업데이트
        const colgroup = this.currentTable.querySelector('colgroup');
        const cols = colgroup.querySelectorAll('col');
        const currentCol = cols[this.currentColumn];


        const tableId = this.currentTable.id;



        if (currentCol) {
            currentCol.style.width = newWidth.toFixed(1) + '%';
            this.updateIndicator(newWidth,tableId);
        }
    }

    stopResize() {
        if (this.currentResizer) {
            this.currentResizer.classList.remove('resizing');
            this.setOtherResizersState(null, true);
            this.hideIndicator();

            // 전체 넓이 정규화 (선택사항)
            this.normalizeColumnWidths();

            this.currentResizer = null;
            this.currentTable = null;
            this.currentColumn = null;

            document.body.style.cursor = '';
            document.body.style.userSelect = '';
        }
    }

    normalizeColumnWidths() {
        if (!this.currentTable) return;

        const colgroup = this.currentTable.querySelector('colgroup');
        const cols = colgroup.querySelectorAll('col');

        // 현재 총 넓이 계산
        let totalWidth = 0;
        const widths = [];

        cols.forEach(col => {
            const width = parseFloat(col.style.width) || 10;
            widths.push(width);
            totalWidth += width;
        });

        // 100%로 정규화 (선택사항 - 주석 해제하면 활성화)
        /*
        if (totalWidth !== 100) {
            const ratio = 100 / totalWidth;
            cols.forEach((col, index) => {
                col.style.width = (widths[index] * ratio).toFixed(1) + '%';
            });
        }
        */
    }

    setOtherResizersState(activeTable, enabled) {
        const allTables = document.querySelectorAll('.resizable-table');

        allTables.forEach(table => {
            if (table !== activeTable || activeTable === null) {
                const resizers = table.querySelectorAll('.resizer');
                resizers.forEach(resizer => {
                    resizer.style.pointerEvents = enabled ? 'auto' : 'none';
                    resizer.style.opacity = enabled ? '1' : '0.3';
                });
            }
        });
    }

    showIndicator() {
        if (this.indicator) {
            this.indicator.style.display = 'block';
        }
    }

    hideIndicator() {
        if (this.indicator) {
            this.indicator.style.display = 'none';
        }
    }

    updateIndicator(width,tableId) {
        if (this.indicator) {
            this.indicator.textContent = `${tableId} - 넓이: ${width.toFixed(1)}%`;
        }
    }

    // API 메서드들
    addTable(table) {
        this.initializeTable(table);
    }

    removeTable(table) {
        const resizers = table.querySelectorAll('.resizer');
        resizers.forEach(resizer => {
            if (resizer._resizeHandler) {
                resizer.removeEventListener('mousedown', resizer._resizeHandler);
                delete resizer._resizeHandler;
            }
        });
    }

    // 특정 테이블의 열 넓이를 프로그래밍 방식으로 설정
    setColumnWidth(tableId, columnIndex, widthPercent) {
        const table = document.getElementById(tableId);
        if (!table) return;

        const colgroup = table.querySelector('colgroup');
        const cols = colgroup.querySelectorAll('col');
        const targetCol = cols[columnIndex];

        if (targetCol) {
            targetCol.style.width = widthPercent + '%';
        }
    }

    // 특정 테이블의 모든 열 넓이 가져오기
    getColumnWidths(tableId) {
        const table = document.getElementById(tableId);
        if (!table) return [];

        const colgroup = table.querySelector('colgroup');
        const cols = colgroup.querySelectorAll('col');

        return Array.from(cols).map(col => parseFloat(col.style.width) || 0);
    }

    // 인디케이터 위치를 마우스 따라다니게 하는 옵션
    enableFollowMouse() {
        document.addEventListener('mousemove', (e) => {
            if (this.indicator && this.indicator.style.display === 'block') {
                this.indicator.style.left = (e.pageX + 10) + 'px';
                this.indicator.style.top = (e.pageY - 30) + 'px';
                this.indicator.style.position = 'absolute';
            }
        });
    }
}

// 전역 리사이저 인스턴스
let globalResizer;

// 초기화
document.addEventListener('DOMContentLoaded', () => {
    globalResizer = new ColgroupTableResizer();

    // 선택사항: 인디케이터가 마우스를 따라다니게 하려면 주석 해제
    // globalResizer.enableFollowMouse();

    /*
    // 체크박스 전체 선택/해제 기능
    document.querySelectorAll('.check-all').forEach(checkAll => {
        checkAll.addEventListener('change', function() {
            const table = this.closest('table');
            const checkboxes = table.querySelectorAll('tbody input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = this.checked);
        });
    });

    // 사용 예시 함수들
    window.testSetWidth = function() {
        globalResizer.setColumnWidth('grid1', 2, 30); // 세 번째 열을 30%로 설정
        console.log('Grid1 열 넓이:', globalResizer.getColumnWidths('grid1'));
    };

    window.addSampleData = function() {
        const tbody = document.getElementById('grid1-body');
        const newRow = `
            <tr>
                <td><div class="tblChk"><input type="checkbox" id="chk${Date.now()}"><label for="chk${Date.now()}"></label></div></td>
                <td>2025-${String(Math.floor(Math.random() * 900) + 100)}</td>
                <td>새로운 사업 프로젝트 - ${Math.random() > 0.5 ? 'IT 기술 지원' : '환경 개선 사업'}</td>
                <td>${Math.floor(Math.random() * 3) + 1}년</td>
                <td>${['과학기술정보통신부', '환경부', '외교부'][Math.floor(Math.random() * 3)]}</td>
                <td>${['필리핀', '태국', '인도네시아'][Math.floor(Math.random() * 3)]}</td>
                <td>${['IT/통신', '환경', '인프라'][Math.floor(Math.random() * 3)]}</td>
                <td>${['진행중', '계획중', '완료'][Math.floor(Math.random() * 3)]}</td>
            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', newRow);
    };
    */
});