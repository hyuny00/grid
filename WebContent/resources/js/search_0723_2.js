class ODAFilterSystem {
	
	constructor(categoryData, gridInstances = [], multiStepCategories = [], categoryTitles = {}, treeData = {}) {
	    this.selectedFilters = new Map();
	    this.activeCategory = null;
	    this.activeSubCategory = null; // 2단계 카테고리 추가
	    this.isFilterOpen = false;
	    this.categoryData = categoryData || {};
	    this.subCategoryData = {}; // 2단계 데이터 저장
	    this.gridInstances = Array.isArray(gridInstances) ? gridInstances : [gridInstances].filter(Boolean);
	    this.multiStepCategories = Array.isArray(multiStepCategories) ? multiStepCategories : []; // 2단계 필터 카테고리 설정
	    this.categoryTitles = categoryTitles || {}; // 카테고리 제목 맵
	    
	    this.treeData = treeData || {}; // 트리 데이터 저장
	    // ✅ 스타일 추가
	    this.addTreeFilterStyles();
	    this.init();
	}
	
	
	
	init() {
		this.bindEvents();
		this.updateSelectedDisplay();
	}
	
	// 트리 데이터가 있는 카테고리인지 확인
	isTreeCategory(category) {
	    return this.treeData[category] && Array.isArray(this.treeData[category]);
	}

	// 그리드 인스턴스 설정 (나중에 설정할 수 있도록)
	setGridInstance(gridInstance) {
	    if (Array.isArray(gridInstance)) {
	        this.gridInstances = gridInstance;
	    } else {
	        this.gridInstances = [gridInstance];
	    }
	}

	// 그리드 인스턴스 추가
	addGridInstance(gridInstance) {
	    this.gridInstances.push(gridInstance);
	}
	
	bindEvents() {
		// DOM 요소 존재 확인 후 이벤트 바인딩
		const filterToggleBtn = document.getElementById('filterToggleBtn');
		if (filterToggleBtn) {
			filterToggleBtn.addEventListener('click', () => {
				this.toggleFilterArea();
			});
		}

		// 카테고리 버튼들
		const categoryBtns = document.querySelectorAll('.filter-category-btn');
		categoryBtns.forEach(btn => {
			btn.addEventListener('click', (e) => {
				e.stopPropagation();
				const category = btn.dataset.category;
				this.toggleCategory(btn, category);
			});
		});

		// 텍스트 검색 버튼
		const textSearchBtn = document.getElementById('textSearchBtn');
		if (textSearchBtn) {
			textSearchBtn.addEventListener('click', () => {
				this.performTextSearch();
			});
		}

		// 조건검색 버튼
		const applyFiltersBtn = document.getElementById('applyFiltersBtn');
		if (applyFiltersBtn) {
			applyFiltersBtn.addEventListener('click', () => {
				this.applyFilters();
			});
		}

		// 초기화 버튼
		const resetFiltersBtn = document.getElementById('resetFiltersBtn');
		if (resetFiltersBtn) {
			resetFiltersBtn.addEventListener('click', () => {
				this.resetFilters();
			});
		}

		// 텍스트 입력 엔터키 처리
		const projectNameInput = document.getElementById('projectNameInput');
		if (projectNameInput) {
			projectNameInput.addEventListener('keypress', (e) => {
				if (e.key === 'Enter') {
					this.performTextSearch();
				}
			});
		}
	}

	toggleFilterArea() {
		const filterArea = document.getElementById('searchFilterArea');
		const toggleBtn = document.getElementById('filterToggleBtn');
		
		if (!filterArea || !toggleBtn) return;
		
		this.isFilterOpen = !this.isFilterOpen;
		
		if (this.isFilterOpen) {
			filterArea.style.display = 'block';
			toggleBtn.textContent = '검색 필터 닫기';
		} else {
			filterArea.style.display = 'none';
			toggleBtn.textContent = '검색 필터 열기';
			this.clearActiveCategory();
		}
	}

	toggleCategory(btn, category) {
	    // 이미 활성화된 카테고리인지 확인
	    if (this.activeCategory === category) {
	        // 토글 동작 대신 그대로 유지하거나, 필요에 따라 새로고침
	        // this.clearActiveCategory();
	        // return;
	        
	        // 옵션 1: 아무것도 하지 않고 그대로 유지
	        return;
	        
	        // 옵션 2: 다시 로드 (필요한 경우)
	        // this.activeCategory = null;
	        // this.activeSubCategory = null;
	    }

	    // 새로운 카테고리 선택 전에 기존 필터 옵션들 모두 정리
	    this.clearAllFilterOptions();

	    // 모든 카테고리 버튼 비활성화
	    document.querySelectorAll('.filter-category-btn').forEach(b => {
	        b.classList.remove('active');
	    });

	    $("#pTxt").text(btn.innerText);
	    
	    // 현재 버튼 활성화
	    btn.classList.add('active');
	    this.activeCategory = category;
	    this.activeSubCategory = null; // 2단계 초기화
	    
	    // 2단계 필터가 필요한 카테고리인지 확인
	    if (this.isMultiStepCategory(category)) {
	        this.updateTwoStepFilterOptions(category);
	    } else {
	        this.updateFilterOptions(category);
	    }
	    
	    if (this.isTreeCategory(category)) {
	        this.updateTreeFilterOptions(category);
	    } else if (this.isMultiStepCategory(category)) {
	        this.updateTwoStepFilterOptions(category);
	    } else {
	        this.updateFilterOptions(category);
	    }
	}
	
	// 트리 필터 옵션 업데이트
	updateTreeFilterOptions(category) {
	    console.log('=== updateTreeFilterOptions 시작 ===');
	    console.log('category:', category);
	    
	    const firstStepContainer = document.querySelector('.first-step-options');
	    if (!firstStepContainer) {
	        return this.updateFilterOptions(category);
	    }
	    
	    // 컨테이너 표시
	    firstStepContainer.style.display = 'flex';
	    
	    const firstStepTitle = firstStepContainer.querySelector('p');
	    if (firstStepTitle) {
	        firstStepTitle.textContent = this.getCategoryTitle(category);
	    }
	    
	    // 2, 3, 4단계 컨테이너 숨기기
	    ['second', 'third', 'fourth'].forEach(step => {
	        const container = document.querySelector(`.${step}-step-options`);
	        if (container) {
	            container.style.display = 'none';
	        }
	    });
	    
	    const firstStepList = firstStepContainer.querySelector('.first-step-list');
	    if (firstStepList) {
	        firstStepList.innerHTML = '';
	    }
	    
	    // 1단계 데이터 (nodeLevel === 1) 가져오기
	    const level1Items = this.treeData[category].filter(item => item.nodeLevel === 1);
	    
	    if (level1Items.length === 0) {
	       // firstStepList.innerHTML = '<li><div class="empty-state">옵션이 없습니다</div></li>';
	        return;
	    }
	    
	    // 1단계 옵션들 추가
	    level1Items.forEach(itemData => {
	        const li = document.createElement('li');
	        
	        const button = document.createElement('button');
	        button.type = 'button';
	        button.className = 'filter-option-item';
	        button.textContent = itemData.fldCdNm;
	        button.dataset.category = category;
	        button.dataset.value = itemData.odaFldCd;
	        button.dataset.nodeLevel = itemData.nodeLevel;
	        button.id = `${category}-${itemData.odaFldCd}`;
	        
	        button.addEventListener('click', () => {
	            this.selectTreeItem(button, itemData, 1);
	        });
	        
	        li.appendChild(button);
	        firstStepList.appendChild(li);
	    });
	    
	    console.log('트리 필터 1단계 옵션 생성 완료');
	}
	
	selectTreeItem(button, itemData, level) {
	    const category = button.dataset.category;
	    const value = button.dataset.value;
	    
	    // 현재 레벨의 모든 버튼 비활성화
	    const currentLevelContainer = document.querySelector(this.getLevelContainerSelector(level));
	    if (currentLevelContainer) {
	        currentLevelContainer.querySelectorAll('.filter-option-item').forEach(btn => {
	            btn.classList.remove('selected');
	        });
	    }
	    
	    // 현재 버튼 활성화
	    button.classList.add('selected');
	    
	    // 다음 레벨 데이터 표시
	    const nextLevel = level + 1;
	    if (nextLevel <= 4) {
	        this.showNextTreeLevel(category, value, nextLevel);
	    } else {
	        // 4단계가 선택된 경우 최종 선택 처리
	        this.selectFinalTreeItem(category, value);
	        
	        // ✅ 추가: 최종 선택된 항목에 'final-selected' 클래스 추가
	        button.classList.add('final-selected');
	    }
	}
	

	// 레벨별 컨테이너 선택자 반환
	getLevelContainerSelector(level) {
	    const levelNames = ['', 'first', 'second', 'third', 'fourth'];
	    return `.${levelNames[level]}-step-options`;
	}

	// 다음 레벨 트리 옵션 표시
	showNextTreeLevel(category, parentValue, level) {
	    const levelNames = ['', 'first', 'second', 'third', 'fourth'];
	    const containerSelector = `.${levelNames[level]}-step-options`;
	    const listSelector = `.${levelNames[level]}-step-list`;
	    
	    const container = document.querySelector(containerSelector);
	    const list = document.querySelector(listSelector);
	    
	    if (!container || !list) return;
	    
	    // 해당 레벨과 그 이후 레벨들 초기화
	    for (let i = level; i <= 4; i++) {
	        const nextContainer = document.querySelector(`.${levelNames[i]}-step-options`);
	        const nextList = document.querySelector(`.${levelNames[i]}-step-list`);
	        if (nextContainer) nextContainer.style.display = 'none';
	        if (nextList) nextList.innerHTML = '';
	    }
	    
	    // 하위 데이터 찾기
	    const childItems = this.treeData[category].filter(item => 
	        item.nodeLevel === level && item.upOdaFldCd === parentValue
	    );
	    
	    if (childItems.length === 0) {
	        // 하위 항목이 없으면 선택 완료 - 최종 선택 처리
	        this.selectFinalTreeItem(category, parentValue);
	        
	        // ✅ 추가: 현재 선택된 버튼에 'final-selected' 클래스 추가
	        const currentButton = document.querySelector(`button[data-value="${parentValue}"]`);
	        if (currentButton) {
	            currentButton.classList.add('final-selected');
	        }
	        return;
	    }
	    
	    // 컨테이너 표시
	    container.style.display = 'flex';
	    
	    // 하위 옵션들 추가
	    childItems.forEach(itemData => {
	        const li = document.createElement('li');
	        
	        const button = document.createElement('button');
	        button.type = 'button';
	        button.className = 'filter-option-item';
	        button.textContent = itemData.fldCdNm;
	        button.dataset.category = category;
	        button.dataset.value = itemData.odaFldCd;
	        button.dataset.nodeLevel = itemData.nodeLevel;
	        button.id = `${category}-${itemData.odaFldCd}`;
	        
	        button.addEventListener('click', () => {
	            this.selectTreeItem(button, itemData, level);
	        });
	        
	        li.appendChild(button);
	        list.appendChild(li);
	    });
	}

	
	
	selectFinalTreeItem(category, value) {
	    // 선택된 항목의 전체 경로 찾기
	    const selectedItem = this.treeData[category].find(item => item.odaFldCd === value);
	    if (!selectedItem) return;
	    
	    let displayText = this.buildTreePath(category, selectedItem);
	    
	    const key = `${category}-${value}`;
	    
	    // ✅ 수정: 이미 선택된 항목이면 알림 후 리턴
	    if (this.selectedFilters.has(key)) {
	        // 이미 선택된 항목 시각적 표시
	        const existingButton = document.querySelector(`#${key}`);
	        if (existingButton) {
	            existingButton.classList.add('already-selected');
	            setTimeout(() => {
	                existingButton.classList.remove('already-selected');
	            }, 1000);
	        }
	        return;
	    }
	    
	    this.selectedFilters.set(key, {
	        category,
	        value,
	        text: displayText,
	        treeData: selectedItem
	    });
	    
	    // ✅ 추가: 선택된 모든 경로의 버튼들을 'in-path' 클래스로 표시
	    this.markSelectedPath(category, selectedItem);
	    
	    this.updateSelectedDisplay();
	    
	    // ✅ 추가: 선택 완료 알림
	    //this.showSelectionFeedback(displayText);
	}
	
	// 4. ✅ 새로운 메서드: 선택된 경로 표시
	markSelectedPath(category, selectedItem) {
	    // 기존 경로 표시 제거
	    document.querySelectorAll('.filter-option-item.in-path').forEach(btn => {
	        btn.classList.remove('in-path');
	    });
	    
	    // 선택된 항목부터 루트까지의 경로 구성
	    const pathItems = [];
	    let currentItem = selectedItem;
	    
	    while (currentItem) {
	        pathItems.unshift(currentItem);
	        
	        if (currentItem.upOdaFldCd) {
	            currentItem = this.treeData[category].find(item => 
	                item.odaFldCd === currentItem.upOdaFldCd
	            );
	        } else {
	            currentItem = null;
	        }
	    }
	    
	    // 경로상의 모든 버튼에 'in-path' 클래스 추가
	    pathItems.forEach(item => {
	        const button = document.querySelector(`#${category}-${item.odaFldCd}`);
	        if (button) {
	            button.classList.add('in-path');
	        }
	    });
	    
	    // 최종 선택된 항목에는 'final-selected' 클래스 추가
	    const finalButton = document.querySelector(`#${category}-${selectedItem.odaFldCd}`);
	    if (finalButton) {
	        finalButton.classList.add('final-selected');
	    }
	}

	// 5. ✅ 새로운 메서드: 선택 완료 피드백
	showSelectionFeedback(displayText) {
	    // 간단한 토스트 알림 또는 상태 표시
	    const feedback = document.createElement('div');
	    feedback.className = 'selection-feedback';
	    feedback.textContent = `선택됨: ${displayText}`;
	    feedback.style.cssText = `
	        position: fixed;
	        top: 20px;
	        right: 20px;
	        background: #28a745;
	        color: white;
	        padding: 10px 15px;
	        border-radius: 4px;
	        z-index: 9999;
	        font-size: 14px;
	        box-shadow: 0 2px 8px rgba(0,0,0,0.2);
	    `;
	    
	    document.body.appendChild(feedback);
	    
	    setTimeout(() => {
	        feedback.remove();
	    }, 2000);
	}
	
	
	// 트리 경로를 텍스트로 구성하는 새로운 메서드
	buildTreePath(category, targetItem) {
	    const pathItems = [];
	    let currentItem = targetItem;
	    
	    // 상위로 올라가면서 경로 구성
	    while (currentItem) {
	        pathItems.unshift(currentItem.fldCdNm); // 배열 앞쪽에 추가
	        
	        // 상위 항목 찾기
	        if (currentItem.upOdaFldCd) {
	            currentItem = this.treeData[category].find(item => 
	                item.odaFldCd === currentItem.upOdaFldCd
	            );
	        } else {
	            currentItem = null;
	        }
	    }
	    
	    return pathItems.join(' > ');
	}
	
	// 모든 필터 옵션 정리하는 새로운 메서드
	clearAllFilterOptions() {
	    console.log('=== clearAllFilterOptions 실행 ===');
	    
	    // 2단계 필터 컨테이너들 초기화
	    const firstStepContainer = document.querySelector('.first-step-options');
	    const secondStepContainer = document.querySelector('.second-step-options');
	    
	    if (firstStepContainer) {
	        firstStepContainer.style.display = 'none';
	        const firstStepList = firstStepContainer.querySelector('.first-step-list');
	        if (firstStepList) {
	            firstStepList.innerHTML = '';
	        }
	    }
	    
	    if (secondStepContainer) {
	        secondStepContainer.style.display = 'none';
	        const secondStepList = secondStepContainer.querySelector('.second-step-list');
	        if (secondStepList) {
	            secondStepList.innerHTML = '';
	        }
	    }
	    
	    // 1단계 필터 컨테이너 초기화
	    const singleStepContainer = document.querySelector('.filter-options-content');
	    if (singleStepContainer) {
	        singleStepContainer.style.display = 'none';
	        singleStepContainer.innerHTML = '';
	    }
	    
	    // 3, 4단계 필터 컨테이너들도 초기화
	    const thirdStepContainer = document.querySelector('.third-step-options');
	    const fourthStepContainer = document.querySelector('.fourth-step-options');
	    
	    if (thirdStepContainer) {
	        thirdStepContainer.style.display = 'none';
	        const thirdStepList = thirdStepContainer.querySelector('.third-step-list');
	        if (thirdStepList) {
	            thirdStepList.innerHTML = '';
	        }
	    }
	    
	    if (fourthStepContainer) {
	        fourthStepContainer.style.display = 'none';
	        const fourthStepList = fourthStepContainer.querySelector('.fourth-step-list');
	        if (fourthStepList) {
	            fourthStepList.innerHTML = '';
	        }
	    }
	    
	    
	  
	    
	  // ✅ 수정: 모든 선택 상태 클래스 제거 부분을 더 강화
	    document.querySelectorAll('.filter-option-item').forEach(btn => {
	        btn.classList.remove('selected', 'on', 'final-selected', 'in-path', 'already-selected');
	    });

	    // ✅ 추가: 트리 필터의 경로 표시도 완전히 초기화
	    document.querySelectorAll('.first-step-list .filter-option-item, .second-step-list .filter-option-item, .third-step-list .filter-option-item, .fourth-step-list .filter-option-item').forEach(btn => {
	        btn.classList.remove('selected', 'final-selected', 'in-path');
	    });
	    
	    
	}

	// 7. ✅ CSS 스타일 추가 (별도 CSS 파일에 추가하거나 동적으로 추가)
	addTreeFilterStyles() {
	    const style = document.createElement('style');
	    style.textContent = `
	        /* 트리 필터 선택 상태 스타일 */
	        .filter-option-item.selected {
	            background-color: #007bff !important;
	            color: white !important;
	            border-color: #007bff !important;
	        }
	        
	        .filter-option-item.final-selected {
	            background-color: #28a745 !important;
	            color: white !important;
	            border-color: #28a745 !important;
	            font-weight: bold;
	        }
	        
	        .filter-option-item.in-path {
	            background-color: #e3f2fd !important;
	            border-color: #2196f3 !important;
	        }
	        
	        .filter-option-item.already-selected {
	            background-color: #ffc107 !important;
	            color: #212529 !important;
	            animation: pulse 0.5s ease-in-out;
	        }
	        
	        @keyframes pulse {
	            0% { transform: scale(1); }
	            50% { transform: scale(1.05); }
	            100% { transform: scale(1); }
	        }
	        
	        /* 선택 피드백 애니메이션 */
	        .selection-feedback {
	            animation: slideInRight 0.3s ease-out;
	        }
	        
	        @keyframes slideInRight {
	            from {
	                transform: translateX(100%);
	                opacity: 0;
	            }
	            to {
	                transform: translateX(0);
	                opacity: 1;
	            }
	        }
	    `;
	    document.head.appendChild(style);
	}
	

	// 2단계 필터가 필요한 카테고리인지 확인
	isMultiStepCategory(category) {
		return this.multiStepCategories.includes(category);
	}

	clearActiveCategory() {
		document.querySelectorAll('.filter-category-btn').forEach(btn => {
			btn.classList.remove('active');
		});
		this.activeCategory = null;
		this.activeSubCategory = null;
		this.clearFilterOptions();
	}

	// 2단계 필터 옵션 업데이트 (기존 구조 활용)
	updateTwoStepFilterOptions(category) {
	    console.log('=== updateTwoStepFilterOptions 시작 ===');
	    console.log('category:', category);
	    
	    // DOM 요소 확인
	    const firstStepContainer = document.querySelector('.first-step-options');
	    const secondStepContainer = document.querySelector('.second-step-options');
	    
	    // 만약 2단계 필터 컨테이너가 없다면, 1단계 필터로 대체
	    if (!firstStepContainer) {
	        console.log('2단계 필터 컨테이너가 없음, 1단계 필터로 대체');
	        return this.updateFilterOptions(category);
	    }
	    
	    // 컨테이너 표시
	    firstStepContainer.style.display = 'flex';
	    
	    // 여기에 추가
	    const firstStepTitle = firstStepContainer.querySelector('p');
	    if (firstStepTitle) {
	        firstStepTitle.textContent = this.getCategoryTitle(category);
	    }
	    
	    
	    if (secondStepContainer) {
	        secondStepContainer.style.display = 'none';
	    }
	    
	    // 기존 내용 찾기
	    const firstStepList = firstStepContainer.querySelector('.first-step-list');
	    if (firstStepList) {
	        firstStepList.innerHTML = '';
	    }
	    
	    const items = this.categoryData[category];
	    console.log('items:', items);
	    
	    if (!items || !Array.isArray(items)) {
	        if (firstStepList) {
	          //  firstStepList.innerHTML = '<li><div class="empty-state">옵션이 없습니다</div></li>';
	        }
	        return;
	    }

	    // 1단계 옵션들 추가
	    items.forEach(itemData => {
	        const li = document.createElement('li');
	        
	        const button = document.createElement('button');
	        button.type = 'button';
	        button.className = 'filter-option-item';
	        button.textContent = itemData.text;
	        button.dataset.category = category;
	        button.dataset.value = itemData.value;
	        button.id = category + '-' + itemData.value;
	        
	        button.addEventListener('click', () => {
	            this.selectFirstStepItem(button, itemData);
	        });
	        
	        li.appendChild(button);
	        if (firstStepList) {
	            firstStepList.appendChild(li);
	        }
	    });
	    
	    console.log('2단계 필터 옵션 생성 완료');
	}

	

	// 2단계 필터 요소가 없을 때의 대체 메서드
	updateFilterOptionsAsFallback(category) {
	    console.log('2단계 필터 대체 메서드 실행');
	    
	    // 1단계 필터 컨테이너 사용 시도
	    const container = document.querySelector('.filter-options-content');
	    if (container) {
	        return this.updateFilterOptions(category);
	    }
	    
	    // 그것도 없으면 대체 컨테이너 생성
	    return this.createFallbackContainer(category);
	}

	// 기존 단일 단계 필터 옵션 업데이트
	updateFilterOptions(category) {
	    console.log('=== updateFilterOptions 시작 ===');
	    console.log('category:', category);
	    
	    // DOM 요소 확인
	    const elements = this.checkDOMElements();
	    
	    const container = elements.singleStepContainer;
	    if (!container) {
	        console.log('filter-options-content 컨테이너를 찾을 수 없음');
	        // 대체 컨테이너 찾기 시도
	        return this.createFallbackContainer(category);
	    }
	    
	    // 컨테이너 표시
	    container.style.display = 'block';
	    
	    console.log('category:::: ', category);
	    
	    container.innerHTML = '';

	    const items = this.categoryData[category];
	    console.log('category:', category, 'items:', items);
	    
	    if (!items || !Array.isArray(items)) {
	      //  container.innerHTML = '<div class="empty-state">옵션이 없습니다</div>';
	        return;
	    }
	    
	    // ul 요소 생성
	    const ul = document.createElement('ul');
	    
	 // updateFilterOptions 메서드에서 날짜 처리 부분 수정
	    items.forEach(itemData => {
	        if (itemData.type === 'date') {
	        	 const li = document.createElement('li');
	             li.innerHTML = `
	                 <p>${itemData.text}</p>
	                 <div class="pickrBox">
	                     <input type="date" class="pickr" placeholder="시작일을 선택하세요" data-category="${category}" data-value="${itemData.value}-start">
	                     <p>~</p>
	                     <input type="date" class="pickr" placeholder="종료일을 선택하세요" data-category="${category}" data-value="${itemData.value}-end">
	                 </div>
	             `;
	             
	             // 날짜 입력 이벤트 처리 수정
	             const dateInputs = li.querySelectorAll('.pickr');
	             const startInput = dateInputs[0];
	             const endInput = dateInputs[1];
	             
	             // 시작일 변경 이벤트
	             startInput.addEventListener('change', (e) => {
	                 this.validateAndUpdateDateRange(startInput, endInput, itemData);
	             });
	             
	             // 종료일 변경 이벤트
	             endInput.addEventListener('change', (e) => {
	                 this.validateAndUpdateDateRange(startInput, endInput, itemData);
	             });
	             
	             // 달력 라이브러리 초기화 (사용하는 라이브러리에 맞게)
	             dateInputs.forEach(input => {
	                 if (typeof flatpickr !== 'undefined') {
	                     flatpickr(input, {
	                         dateFormat: "Y-m-d",
	                     });
	                 }
	             });
	             
	             ul.appendChild(li);
	            
	        } else if (itemData.type === 'budget') {  // 새로 추가
	        	
	        		    const li = document.createElement('li');
	        		    li.innerHTML = `
	        		        <p>${itemData.text}</p>
	        		        <div class="iptBox">
	        		            <input type="number" placeholder="금액을 입력하세요." data-category="${category}" data-value="${itemData.value}" min="0">
	        		            <button type="button" class="btn cnf">확인</button>
	        		        </div>
	        		    `;
	        		    
	        		    // 확인 버튼 이벤트 처리
	        		    const confirmBtn = li.querySelector('.btn.cnf');
	        		    const budgetInput = li.querySelector('input[type="number"]');
	        		    
	        		    // 자동 포커스 추가
	        		    setTimeout(() => {
	        		        budgetInput.focus();
	        		    }, 100);
	        		    
	        		    // 숫자만 입력 가능하도록 추가 처리
	        		    budgetInput.addEventListener('input', (e) => {
	        		        // 음수 입력 방지
	        		        if (e.target.value < 0) {
	        		            e.target.value = '';
	        		        }
	        		        // 소수점 입력 방지 (정수만 허용하려면)
	        		        e.target.value = e.target.value.replace(/[^0-9]/g, '');
	        		    });
	        		    
	        		    confirmBtn.addEventListener('click', () => {
	        		        const budgetValue = budgetInput.value.trim();
	        		        if (budgetValue && budgetValue > 0) {
	        		            this.selectBudgetItem(budgetInput, itemData.text, budgetValue);
	        		        } else {
	        		            alert('올바른 금액을 입력하세요.');
	        		        }
	        		    });
	        		    
	        		    // 엔터키 처리
	        		    budgetInput.addEventListener('keypress', (e) => {
	        		        if (e.key === 'Enter') {
	        		            confirmBtn.click();
	        		        }
	        		    });
	        		    
	        		    ul.appendChild(li);
	        
	    }else {
	            const li = document.createElement('li');
	            const button = document.createElement('button');
	            button.type = 'button';
	            button.className = 'filter-option-item';
	            button.textContent = itemData.text;
	            button.dataset.category = category;
	            button.dataset.value = itemData.value;
	            button.id = category + '-' + itemData.value;
	            
	            button.addEventListener('click', () => {
	                button.classList.add('on');
	                this.selectFilterItem(button);
	            });

	            button.addEventListener('mouseenter', () => {
	                button.style.background = '#e9ecef';
	            });

	            button.addEventListener('mouseleave', () => {
	                button.style.background = '#f8f9fa';
	            });
	            
	            li.appendChild(button);
	            ul.appendChild(li);
	        }
	    });
	    
	    container.appendChild(ul);
	    console.log('1단계 필터 옵션 생성 완료');
	}
	
	validateAndUpdateDateRange(startInput, endInput, itemData) {
	    const category = startInput.dataset.category;
	    const startDate = startInput.value;
	    const endDate = endInput.value;
	    
	    
	    
	    // 둘 다 입력되지 않은 경우 - 기존 필터 제거
	    if (!startDate || !endDate) {
	        const key = `${category}-${itemData.value}`;
	        this.selectedFilters.delete(key);
	        this.updateSelectedDisplay();
	        return;
	    }
	    
	    // 날짜 유효성 검사
	    if (!this.validateDateRange(startDate, endDate)) {
	        return;
	    }
	    
	    
	    // 둘 다 입력된 경우 필터 추가
	    const text = `사업기간: ${startDate} ~ ${endDate}`;
	    const key = `${category}-${itemData.value}`;
	    
	    this.selectedFilters.set(key, {
	        category,
	        value: itemData.value,
	        text,
	        startDate,
	        endDate,
	        dateRange: `${startDate},${endDate}` // 검색 파라미터용
	    });

	    this.updateSelectedDisplay();
	}

	validateDateRange(startDate, endDate) {
	    const start = new Date(startDate);
	    const end = new Date(endDate);
	    
	  
	    
	    // 시작일이 종료일보다 늦은 경우
	    if (start > end) {
	        alert('시작일은 종료일보다 이전이어야 합니다.');
	        return false;
	    }
	    
	    // 현재 날짜보다 너무 과거인 경우 (선택사항)
	    const today = new Date();
	    const minDate = new Date(today.getFullYear() - 50, 0, 1); // 50년 전까지 허용
	    
	    if (start < minDate) {
	        alert('시작일이 너무 과거입니다.');
	        return false;
	    }
	    
	    // 현재 날짜보다 너무 미래인 경우 (선택사항)
	    const maxDate = new Date(today.getFullYear() + 10, 11, 31); // 10년 후까지 허용
	    
	    if (end > maxDate) {
	        alert('종료일이 너무 미래입니다.');
	        return false;
	    }
	    
	    return true;
	}
	
	selectBudgetItem(input, labelText, budgetValue) {
	    const category = input.dataset.category;
	    const value = input.dataset.value;
	    // 숫자에 천단위 콤마 추가하여 표시
	    const formattedValue = Number(budgetValue).toLocaleString();
	    //const text = `${labelText}: ${formattedValue}원`;
	    const text = `사업예산: ${formattedValue}원`;

	    const key = `${category}-${value}`;
	    
	    this.selectedFilters.set(key, {
	        category,
	        value,
	        text,
	        budgetValue: budgetValue // 실제 숫자값 저장
	    });

	    this.updateSelectedDisplay();
	    
	    // 입력 필드 초기화
	    input.value = '';
	}
	
	createFallbackContainer(category) {
	    console.log('대체 컨테이너 생성 시도');
	    
	    // 필터 영역 찾기
	    const filterArea = document.getElementById('searchFilterArea');
	    if (!filterArea) {
	        console.log('필터 영역을 찾을 수 없음');
	        return;
	    }
	    
	    // 기존 대체 컨테이너가 있는지 확인
	    let fallbackContainer = document.querySelector('.fallback-filter-container');
	    if (!fallbackContainer) {
	        fallbackContainer = document.createElement('div');
	        fallbackContainer.className = 'fallback-filter-container';
	        fallbackContainer.style.cssText = 'margin-top: 10px; padding: 10px; border: 1px solid #ddd; border-radius: 4px;';
	        filterArea.appendChild(fallbackContainer);
	    }
	    
	    // 컨테이너 내용 생성
	    this.populateContainer(fallbackContainer, category);
	}
	
	// 모든 필터 옵션 숨기기 (새로운 메서드)
	hideAllFilterOptions() {
	    // 2단계 필터 요소들 숨기기
	    const firstStepContainer = document.querySelector('.first-step-options');
	    const secondStepContainer = document.querySelector('.second-step-options');
	    
	    if (firstStepContainer) {
	        firstStepContainer.style.display = 'none';
	    }
	    if (secondStepContainer) {
	        secondStepContainer.style.display = 'none';
	    }
	    
	    // 1단계 필터 요소 숨기기
	    const singleStepContainer = document.querySelector('.filter-options-content');
	    if (singleStepContainer) {
	        singleStepContainer.style.display = 'none';
	    }
	}
	
	// 컨테이너에 필터 옵션 추가
	populateContainer(container, category) {
	    container.innerHTML = '';
	    
	    const items = this.categoryData[category];
	    if (!items || !Array.isArray(items)) {
	        container.innerHTML = '<div class="empty-state">옵션이 없습니다</div>';
	        return;
	    }
	    
	    const ul = document.createElement('ul');
	    ul.style.cssText = 'list-style: none; padding: 0; margin: 0;';
	    
	    items.forEach(itemData => {
	        const li = document.createElement('li');
	        li.style.cssText = 'margin-bottom: 5px;';
	        
	        const button = document.createElement('button');
	        button.type = 'button';
	        button.className = 'filter-option-item';
	        button.textContent = itemData.text;
	        button.dataset.category = category;
	        button.dataset.value = itemData.value;
	        button.id = category + '-' + itemData.value;
	        button.style.cssText = 'padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; background: #f8f9fa; cursor: pointer; width: 100%;';
	        
	        button.addEventListener('click', () => {
	            button.classList.add('on');
	            this.selectFilterItem(button);
	        });

	        button.addEventListener('mouseenter', () => {
	            button.style.background = '#e9ecef';
	        });

	        button.addEventListener('mouseleave', () => {
	            button.style.background = '#f8f9fa';
	        });
	        
	        li.appendChild(button);
	        ul.appendChild(li);
	    });
	    
	    container.appendChild(ul);
	}
	
	// 1단계 항목 선택 (2단계 필터용)
	async selectFirstStepItem(button, itemData) {
		const category = button.dataset.category;
		const value = button.dataset.value;
		
		// 1단계 선택 표시
		document.querySelectorAll('.first-step-list .filter-option-item').forEach(btn => {
			btn.classList.remove('selected');
		});
		button.classList.add('selected');
		
		this.activeSubCategory = value;
		
		// 2단계 데이터 로드
		await this.loadSubCategoryData(category, value);
		
		// 2단계 옵션 표시
		this.showSecondStepOptions(category, value);
	}

	// 2단계 데이터 로드
	async loadSubCategoryData(category, parentValue) {
		const key = `${category}-${parentValue}`;
		
		
		var isSearchNtcCd='';
		if(category=='schNtnCd'){
			isSearchNtcCd='Y';
		}
		
		// 이미 로드된 데이터가 있으면 재사용
		if (this.subCategoryData[key]) {
			return this.subCategoryData[key];
		}
		
		try {
			// 여기서 2단계 데이터를 Ajax로 로드
			// 예: 시행기관 선택 후 해당 시행기관의 세부 항목들 로드
			
			
			const response = await $.ajax({
				url: '/common/selectCode', // 2단계 데이터 로드 API
				type: 'get',
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",
				data: { 
					cdGroupSn: parentValue,
					code: '',
					isSearchNtcCd: isSearchNtcCd
				}
			});
			
			
			//// 테스트용 하드코딩 데이터
			//const response = this.getTestSubCategoryData(category, parentValue);
			
			// 받은 데이터를 변환
			const transformedData = response.map(item => ({
				value: item.code,
				text: item.text,
				parentValue: parentValue
			}));
			
			this.subCategoryData[key] = transformedData;
			return transformedData;
			
		} catch (error) {
			console.error("Failed to load sub category data:", error);
			this.subCategoryData[key] = [];
			return [];
		}
	}

	// 2단계 옵션 표시 (기존 구조의 우측 영역 활용)
	showSecondStepOptions(category, parentValue) {
	    const secondStepContainer = document.querySelector('.second-step-options');
	    
	    if (!secondStepContainer) {
	        console.log('2단계 컨테이너를 찾을 수 없음');
	        return;
	    }
	    
	    const key = `${category}-${parentValue}`;
	    const subItems = this.subCategoryData[key] || [];
	    
	    // 2단계 옵션 영역 표시
	    secondStepContainer.style.display = 'flex';
	    
	    // 기존 2단계 옵션들 제거
	    const secondStepList = secondStepContainer.querySelector('.second-step-list');
	    if (secondStepList) {
	        secondStepList.innerHTML = '';
	    
	        if (subItems.length === 0) {
	            secondStepList.innerHTML = '<li><div class="empty-state">하위 옵션이 없습니다</div></li>';
	            return;
	        }
	        
	        // 2단계 옵션들 추가
	        subItems.forEach(subItem => {
	            const li = document.createElement('li');
	            
	            const button = document.createElement('button');
	            button.type = 'button';
	            button.className = 'filter-option-item sub-option';
	            button.textContent = subItem.text;
	            button.dataset.category = category;
	            button.dataset.value = subItem.value;
	            button.dataset.parentValue = parentValue;
	            button.id = `${category}-${parentValue}-${subItem.value}`;
	            
	            button.addEventListener('click', () => {
	                button.classList.add('on');
	                this.selectSubFilterItem(button, subItem);
	            });
	            
	            li.appendChild(button);
	            secondStepList.appendChild(li);
	        });
	    }
	}

	// 카테고리 제목 반환
	getCategoryTitle(category) {
		return this.categoryTitles[category] || category;
	}

	// 2단계 필터 항목 선택
	selectSubFilterItem(button, subItem) {
		const category = button.dataset.category;
		const value = button.dataset.value;
		const parentValue = button.dataset.parentValue;
		const text = subItem.text;
		
		// 부모 항목 텍스트 찾기
		const parentItem = this.categoryData[category].find(item => item.value === parentValue);
		const parentText = parentItem ? parentItem.text : parentValue;
		
		const displayText = `${parentText} > ${text}`;
		const key = `${category}-${parentValue}-${value}`;
		
		if (this.selectedFilters.has(key)) {
			return;
		}

		this.selectedFilters.set(key, {
			category,
			value,
			text: displayText,
			parentValue,
			subValue: value
		});

		this.updateSelectedDisplay();
	}

	/*
	selectDateItem(input, labelText) {
		const category = input.dataset.category;
		const value = input.dataset.value;
		const dateValue = input.value;
		const text = `${labelText}: ${dateValue}`;

		const key = `${category}-${value}`;
		
		this.selectedFilters.set(key, {
			category,
			value,
			text,
			dateValue
		});

		this.updateSelectedDisplay();
	}
*/
	selectFilterItem(item) {
		const category = item.dataset.category;
		const value = item.dataset.value;
		const text = item.textContent;

		const key = `${category}-${value}`;
		if (this.selectedFilters.has(key)) {
			return;
		}

		this.selectedFilters.set(key, {
			category,
			value,
			text
		});

		this.updateSelectedDisplay();
	}

	clearFilterOptions() {
	    console.log('=== clearFilterOptions 실행 ===');
	    
	    // clearAllFilterOptions 호출
	    this.clearAllFilterOptions();
	    
	    $('#pTxt').text('');
	}

	updateSelectedDisplay() {
		const container = document.getElementById('selectedFilters');
		const applyBtn = document.getElementById('applyFiltersBtn');
		const resetBtn = document.getElementById('resetFiltersBtn');
		
		if (!container) return;
		
		if (this.selectedFilters.size === 0) {
			container.innerHTML = '<li class="empty-state"><p>검색 조건을 설정해주세요.</p></li>';
			if (resetBtn) resetBtn.disabled = true;
			return;
		}

		if (resetBtn) resetBtn.disabled = false;

		container.innerHTML = '';
		
		this.selectedFilters.forEach((filter, key) => {
			const li = document.createElement('li');
			li.innerHTML = `
				<p>${filter.text}</p>
				<button type="button" class="btn-del" onclick="window.odaFilterSystem.removeFilter('${key}')">
					<span class="sr-only">검색조건 삭제</span>
				</button>
			`;
			container.appendChild(li);
		});
	}


	removeFilter(key) {
	    // ✅ 추가: 트리 필터인지 확인하고 관련 DOM 상태도 완전히 초기화
	    const filterData = this.selectedFilters.get(key);
	    if (filterData && filterData.treeData) {
	        // 트리 필터인 경우 관련된 모든 경로 버튼 상태 제거
	        this.clearTreeFilterPath(filterData.category, filterData.treeData);
	    }
	    
	    // 해당 필터 버튼의 선택 상태 제거
	    const parts = key.split('-');
	    if (parts.length >= 3) {
	        // 2단계 필터인 경우
	        const $id = $('#'+key);
	        $id.removeClass('on selected final-selected in-path');
	    } else {
	        // 1단계 필터인 경우
	        const $id = $('#'+key);
	        $id.removeClass('on selected final-selected in-path');
	    }
	    
	    // ✅ 중요: selectedFilters에서 완전히 삭제
	    this.selectedFilters.delete(key);
	    this.updateSelectedDisplay();
	    
	    // ✅ 추가: 필터 삭제 후 자동으로 재검색 실행
	    this.applyFiltersAfterRemoval();
	    
	    // ✅ 추가: 디버깅용 로그
	    console.log('필터 삭제 후 남은 필터들:', this.selectedFilters);
	    console.log('필터 삭제 후 getSelectedFilters():', this.getSelectedFilters());
	}
	
	// ✅ 새로 추가: 트리 필터 경로 완전 초기화
	clearTreeFilterPath(category, treeData) {
	    if (!treeData) return;
	    
	    // 선택된 항목부터 루트까지의 경로 구성
	    const pathItems = [];
	    let currentItem = treeData;
	    
	    while (currentItem) {
	        pathItems.unshift(currentItem);
	        
	        if (currentItem.upOdaFldCd) {
	            currentItem = this.treeData[category].find(item => 
	                item.odaFldCd === currentItem.upOdaFldCd
	            );
	        } else {
	            currentItem = null;
	        }
	    }
	    
	    // 경로상의 모든 버튼에서 선택 상태 제거
	    pathItems.forEach(item => {
	        const button = document.querySelector(`#${category}-${item.odaFldCd}`);
	        if (button) {
	            button.classList.remove('selected', 'final-selected', 'in-path');
	        }
	    });
	}
	
	
	// 필터 제거 후 그리드 검색을 재실행하는 메서드
	applyFiltersAfterRemoval() {
		const filters = this.getSelectedFilters();
		const projectNameInput = document.getElementById('projectNameInput');
		const searchTerm = projectNameInput ? projectNameInput.value.trim() : '';
		
		console.log('필터 제거 후 재검색:', filters);
		console.log('검색어:', searchTerm);
		
		this.executeSearch({ filters, searchTerm });
	}

	resetFilters() {
	    // 모든 선택된 필터 버튼들의 상태 제거
	    this.selectedFilters.forEach((filter, key) => {
	        const $id = $('#' + key);
	        $id.removeClass('on selected final-selected in-path'); // ✅ 트리 필터 클래스도 추가
	    });
	    
	    // ✅ 추가: 모든 트리 필터 버튼 상태 초기화
	    document.querySelectorAll('.filter-option-item').forEach(btn => {
	        btn.classList.remove('selected', 'on', 'final-selected', 'in-path', 'already-selected');
	    });
	    
	    // 필터 데이터 초기화
	    this.selectedFilters.clear();
	    this.updateSelectedDisplay();
	    this.clearActiveCategory();
	    
	    // 텍스트 검색어 초기화
	    const projectNameInput = document.getElementById('projectNameInput');
	    if (projectNameInput) {
	        projectNameInput.value = '';
	    }
	    
	    // 그리드 검색 폼 초기화
	    this.clearAllGridParams();
	    
	    // 빈 검색 실행 (모든 데이터 표시)
	    this.executeSearch({ filters: {}, searchTerm: '' });
	}
	
	// 모든 그리드 파라미터 초기화
	clearAllGridParams() {
	    this.gridInstances.forEach((grid, index) => {
	        if (!grid || !grid.searchFormId) {
	            return;
	        }
	        
	        const searchForm = $(`#${grid.searchFormId}`);
	        if (!searchForm.length) {
	            return;
	        }
	        
	        // 모든 필터 관련 필드 초기화
	        Object.keys(this.categoryData).forEach(category => {
	            const field = searchForm.find(`[name="${category}"]`);
	            if (field.length) {
	                if (field.hasClass('select2-hidden-accessible') || field.data('select2')) {
	                    field.val('').trigger('change');
	                } else {
	                    field.val('');
	                }
	            }
	        });
	        
	        // 텍스트 검색 필드 초기화
	        const searchTermFields = searchForm.find('[name="searchTerm"], [name="projectName"]');
	        searchTermFields.val('');
	        
	        // gridData 초기화
	        if (grid.gridData) {
	            Object.keys(this.categoryData).forEach(category => {
	                delete grid.gridData[category];
	            });
	            delete grid.gridData.searchTerm;
	            delete grid.gridData.projectName;
	        }
	        
	        console.log(`그리드 ${index + 1}: 모든 파라미터 초기화 완료`);
	    });
	}
	

	performTextSearch() {
		const projectNameInput = document.getElementById('projectNameInput');
		const searchTerm = projectNameInput ? projectNameInput.value.trim() : '';
		
		if (!searchTerm) {
			alert('검색어를 입력하세요.');
			return;
		}

		console.log('텍스트 검색:', searchTerm);
		this.executeSearch({ searchTerm });
	}

	applyFilters() {
		const filters = this.getSelectedFilters();
		const projectNameInput = document.getElementById('projectNameInput');
		const searchTerm = projectNameInput ? projectNameInput.value.trim() : '';
		
		console.log('필터 적용:', filters);
		console.log('검색어:', searchTerm);
		
		this.executeSearch({ filters, searchTerm });
	}

	getSelectedFilters() {
	    const filters = {};
	    
	    // ✅ 디버깅용 로그 추가
	    console.log('getSelectedFilters 실행 - selectedFilters Map:', this.selectedFilters);
	    
	    this.selectedFilters.forEach((filter, key) => {
	        console.log('처리 중인 필터:', key, filter);
	        
	        if (!filters[filter.category]) {
	            filters[filter.category] = [];
	        }
	        
	        // 날짜 범위 필터의 경우
	        if (filter.dateRange) {
	            filters[filter.category].push(filter.dateRange);
	        } else if (filter.budgetValue) {
	            filters[filter.category].push(filter.budgetValue);
	        } else if (filter.subValue) {
	            filters[filter.category].push(filter.subValue);
	        } else {
	            filters[filter.category].push(filter.value);
	        }
	    });
	    
	    console.log('최종 반환될 filters:', filters);
	    return filters;
	}

	// 그리드 검색 파라미터 생성
	// 그리드 검색 파라미터 생성 (개선된 버전)
	buildSearchParams(searchData) {
	    const params = {};
	    
	    // 텍스트 검색어가 있으면 추가
	    if (searchData.searchTerm) {
	        params.searchTerm = searchData.searchTerm;
	        params.projectName = searchData.searchTerm;
	    }
	    
	    // 필터 조건들을 파라미터로 변환
	    if (searchData.filters) {
	        Object.entries(searchData.filters).forEach(([category, values]) => {
	            if (values.length > 0) {
	                // 빈 값들 제거
	                const validValues = values.filter(v => v && v.toString().trim() !== '');
	                if (validValues.length > 0) {
	                    params[category] = validValues.length === 1 ? validValues[0] : validValues.join(',');
	                }
	            }
	        });
	    }
	    
	    console.log('최종 생성된 검색 파라미터:', params);
	    return params;
	}


	executeSearch(searchData) {
	    console.log('검색 실행:', searchData);
	    
	    if (this.gridInstances && this.gridInstances.length > 0) {
	        const searchParams = this.buildSearchParams(searchData);
	        
	        this.gridInstances.forEach((gridInstance, index) => {
	            console.log(`그리드 ${index + 1} 검색 실행`);
	            this.setGridSearchParams(searchParams, gridInstance);
	            gridInstance.searchData();
	        });
	    } else {
	        const filterCount = this.selectedFilters.size;
	        const searchTerm = searchData.searchTerm || '';
	        
	        let message = '검색이 완료되었습니다.\n';
	        if (searchTerm) {
	            message += `검색어: ${searchTerm}\n`;
	        }
	        if (filterCount > 0) {
	            message += `적용된 필터: ${filterCount}개`;
	        }
	        
	        alert(message);
	    }
	}

	setGridSearchParams(params, gridInstance = null) {
	    console.log('설정할 파라미터:', params);
	    
	    const targetGrids = gridInstance ? [gridInstance] : this.gridInstances;
	    
	    targetGrids.forEach((grid, index) => {
	        if (!grid || !grid.searchFormId) {
	            console.log(`그리드 ${index + 1}: 그리드 인스턴스나 검색폼 ID가 없음`);
	            return;
	        }
	        
	        const searchForm = $(`#${grid.searchFormId}`);
	        if (!searchForm.length) {
	            console.log(`그리드 ${index + 1}: 검색폼을 찾을 수 없음:`, grid.searchFormId);
	            return;
	        }
	        
	        // ✅ 수정: 먼저 모든 필터 필드를 완전히 초기화
	        this.clearGridFilterFields(searchForm);
	        
	        // ✅ 추가: 잠깐 기다린 후 새로운 파라미터 설정 (DOM 업데이트 대기)
	        setTimeout(() => {
	            Object.entries(params).forEach(([key, value]) => {
	                let field = searchForm.find(`[name="${key}"]`);
	                
	                if (field.length === 0) {
	                    console.log(`그리드 ${index + 1}: 필드 ${key}가 없어서 동적 생성`);
	                    field = $(`<input type="hidden" name="${key}" />`);
	                    searchForm.append(field);
	                }
	                
	                if (field.length) {
	                    if (field.hasClass('select2-hidden-accessible') || field.data('select2')) {
	                        field.val(value).trigger('change');
	                    } else {
	                        field.val(value);
	                    }
	                    console.log(`그리드 ${index + 1}: 필드 ${key} 최종 설정값:`, field.val());
	                }
	            });
	        }, 100);
	    });
	}
	
	// 그리드 폼에서 필터 관련 필드들을 초기화하는 메서드
	clearGridFilterFields(searchForm) {
	    // 현재 선택된 필터 카테고리들 수집
	    const filterCategories = new Set();
	    this.selectedFilters.forEach(filter => {
	        filterCategories.add(filter.category);
	    });
	    
	    // 모든 필터 카테고리에 대해 필드 초기화
	    Object.keys(this.categoryData).forEach(category => {
	        const field = searchForm.find(`[name="${category}"]`);
	        if (field.length) {
	            // 현재 활성화된 필터가 아닌 경우 값 제거
	            if (!filterCategories.has(category)) {
	                if (field.hasClass('select2-hidden-accessible') || field.data('select2')) {
	                    field.val('').trigger('change');
	                } else {
	                    field.val('');
	                }
	                console.log(`필드 ${category} 초기화됨`);
	            }
	        }
	    });
	    
	    // 텍스트 검색어 관련 필드도 확인
	    const projectNameInput = document.getElementById('projectNameInput');
	    if (!projectNameInput || !projectNameInput.value.trim()) {
	        const searchTermFields = searchForm.find('[name="searchTerm"], [name="projectName"]');
	        searchTermFields.val('');
	    }
	    
	 // gridData도 초기화 (만약 있다면)
	    this.gridInstances.forEach(grid => {
	        if (grid.gridData) {
	            Object.keys(this.categoryData).forEach(category => {
	                delete grid.gridData[category];
	            });
	        }
	    });
	    
	}
	
	// 테스트용 2단계 데이터 생성 메서드
	getTestSubCategoryData(category, parentValue) {
	    const testData = {
	        'biz_tpcd': {
	            'A00': [
	                { code: 'KOICA_01', text: '교육사업' },
	                { code: 'KOICA_02', text: '보건사업' },
	                { code: 'KOICA_03', text: '농업사업' }
	            ],
	            'B00': [
	                { code: 'EDCF_01', text: '인프라구축' },
	                { code: 'EDCF_02', text: '산업개발' }
	            ]
	        },
	        'biz_seccd': {
	            'ASIA': [
	                { code: 'ASIA_01', text: '베트남' },
	                { code: 'ASIA_02', text: '필리핀' },
	                { code: 'ASIA_03', text: '캄보디아' }
	            ],
	            'AFRICA': [
	                { code: 'AFRICA_01', text: '가나' },
	                { code: 'AFRICA_02', text: '케냐' }
	            ]
	        }
	    };
	    
	    return testData[category]?.[parentValue] || [];
	}
	
	checkDOMElements() {
	    console.log('=== DOM 요소 확인 ===');
	    
	    // 2단계 필터 요소들
	    const firstStepContainer = document.querySelector('.first-step-options');
	    const secondStepContainer = document.querySelector('.second-step-options');
	    const firstStepList = document.querySelector('.first-step-list');
	    const secondStepList = document.querySelector('.second-step-list');
	    
	    console.log('first-step-options:', firstStepContainer);
	    console.log('second-step-options:', secondStepContainer);
	    console.log('first-step-list:', firstStepList);
	    console.log('second-step-list:', secondStepList);
	    
	    // 1단계 필터 요소들
	    const singleStepContainer = document.querySelector('.filter-options-content');
	    console.log('filter-options-content:', singleStepContainer);
	    
	    // 전체 필터 영역
	    const filterArea = document.getElementById('searchFilterArea');
	    console.log('searchFilterArea:', filterArea);
	    
	    // 모든 가능한 컨테이너 확인
	    const allContainers = document.querySelectorAll('[class*="filter"], [class*="step"], [class*="options"]');
	    console.log('모든 필터 관련 요소들:', allContainers);
	    
	    return {
	        firstStepContainer,
	        secondStepContainer,
	        firstStepList,
	        secondStepList,
	        singleStepContainer,
	        filterArea
	    };
	}
}