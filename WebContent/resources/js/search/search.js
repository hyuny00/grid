class ODAFilterSystem {
	
	constructor(categoryData, gridInstances = [], multiStepCategories = [], categoryTitles = {},categoryCodeMapping = {}) {
	    this.selectedFilters = new Map();
	    this.activeCategory = null;
	    
	    // 다단계 관리를 위한 새로운 속성들
	    this.currentStep = 0; // 현재 단계 (0: 카테고리 선택, 1: 1단계, 2: 2단계...)
	    this.stepSelections = []; // 각 단계별 선택된 값들 저장 [step1Value, step2Value, ...]
	    this.maxSteps = 5; // 최대 단계 수
	    this.stepData = {}; // 모든 단계의 데이터를 저장하는 객체
	    this.stepTexts = {}; // 각 단계별 텍스트 저장
	    
	    this.isFilterOpen = false;
	    this.categoryData = categoryData || {};
	    this.gridInstances = Array.isArray(gridInstances) ? gridInstances : [gridInstances].filter(Boolean);
	    this.multiStepCategories = Array.isArray(multiStepCategories) ? multiStepCategories : [];
	    this.categoryTitles = categoryTitles || {};
	    
	    this.categoryCodeMapping = categoryCodeMapping || {};
	    
	    this.init();
	}
	
	init() {
		this.bindEvents();
		this.updateSelectedDisplay();
		
		
		const filterArea = document.getElementById('searchFilterArea');
		filterArea.style.display = 'none';
		
		const applyFiltersBtn = document.getElementById('applyFiltersBtn');
		applyFiltersBtn.style.display = 'none';
		
		
	}
	
	// 초기 필터 설정 - 데이터 검증 없이 바로 추가
	setSimpleInitialFilters(initialFilters) {
	    console.log('간단한 초기 검색조건 설정:', initialFilters);
	    
	    // 기존 필터 초기화
	    this.selectedFilters.clear();
	    
	    // 초기 필터 적용
	    Object.entries(initialFilters).forEach(([category, valueData]) => {
	        if (Array.isArray(valueData)) {
	            // 배열인 경우 각각 추가
	            valueData.forEach(item => {
	                if (typeof item === 'string' || typeof item === 'number') {
	                    // 단순 값인 경우 기본 텍스트 생성
	                    this.addSimpleFilter(category, item, `${category}: ${item}`);
	                } else if (item.value) {
	                    // 객체인 경우
	                    this.addSimpleFilter(category, item.value, item.text || `${category}: ${item.value}`);
	                }
	            });
	        } else if (typeof valueData === 'string' || typeof valueData === 'number') {
	            // 단순 값인 경우
	            this.addSimpleFilter(category, valueData, `${category}: ${valueData}`);
	        } else if (valueData.value) {
	            // 객체인 경우
	            this.addSimpleFilter(category, valueData.value, valueData.text || `${category}: ${valueData.value}`);
	        }
	    });
	    
	    // 화면 업데이트
	    this.updateSelectedDisplay();
	    
	    // 검색 실행
	    this.executeSearch({ filters: this.getSelectedFilters() });
	}
	
	// 개별 필터 추가 - 데이터 검증 없음
	addSimpleFilter(category, value, text) {
	    const key = `${category}-${value}`;
	    
	    this.selectedFilters.set(key, {
	        category: category,
	        value: value,
	        text: text,
	        isSimpleFilter: true  // 간단한 필터임을 표시
	    });
	    
	    console.log(`간단한 필터 추가: ${category} = ${value} (${text})`);
	}

	setGridInstance(gridInstance) {
	    if (Array.isArray(gridInstance)) {
	        this.gridInstances = gridInstance;
	    } else {
	        this.gridInstances = [gridInstance];
	    }
	}

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
		/*
		const projectNameInput = document.getElementById('projectNameInput');
		if (projectNameInput) {
			projectNameInput.addEventListener('keypress', (e) => {
				if (e.key === 'Enter') {
					this.performTextSearch();
				}
			});
		}
		*/
	}

	toggleFilterArea() {
		const filterArea = document.getElementById('searchFilterArea');
		const toggleBtn = document.getElementById('filterToggleBtn');
		
		if (!filterArea || !toggleBtn) return;
		
		this.isFilterOpen = !this.isFilterOpen;
		
		const applyFiltersBtn = document.getElementById('applyFiltersBtn');
		
		
		const resetFiltersBtn = document.getElementById('resetFiltersBtn');
		
		
		if (this.isFilterOpen) {
			filterArea.style.display = 'block';
			toggleBtn.textContent = '검색 필터 닫기';
			
			resetFiltersBtn.disabled = false;
			
			applyFiltersBtn.style.display = 'inline-flex';
			
		} else {
			filterArea.style.display = 'none';
			toggleBtn.textContent = '검색 필터 열기';
			
			
			
			applyFiltersBtn.style.display = 'none';
			this.clearActiveCategory();
		}
	}

	toggleCategory(btn, category) {
	    if (this.activeCategory === category) {
	        return;
	    }

	    this.clearAllFilterOptions();

	    document.querySelectorAll('.filter-category-btn').forEach(b => {
	        b.classList.remove('active');
	    });

	    $("#pTxt").text(btn.innerText);
	    
	    btn.classList.add('active');
	    this.activeCategory = category;
	    this.resetStepState(); // 단계 상태 초기화
	    
	    if (this.isMultiStepCategory(category)) {
	        this.updateMultiStepFilterOptions(category);
	    } else {
	        this.updateFilterOptions(category);
	    }
	}
	
	resetStepState() {
	    this.currentStep = 0;
	    this.stepSelections = [];
	    this.stepTexts = {};
	}
	
	// 모든 필터 옵션 정리하는 새로운 메서드
	clearAllFilterOptions() {
	  //  console.log('=== clearAllFilterOptions 실행 ===');
	    
	    // 모든 단계 컨테이너 숨기기
	    this.hideAllStepContainers();
	    
	    // 1단계 필터 컨테이너 초기화
	    const singleStepContainer = document.querySelector('.filter-options-content');
	    if (singleStepContainer) {
	        singleStepContainer.style.display = 'none';
	        singleStepContainer.innerHTML = '';
	    }
	    
	    // 기존 선택 상태 초기화
	    document.querySelectorAll('.filter-option-item.selected, .filter-option-item.on').forEach(btn => {
	        btn.classList.remove('selected', 'on');
	    });
	}
	
	// 모든 단계 컨테이너 숨기기
	hideAllStepContainers() {
	    const stepNames = ['first', 'second', 'third', 'fourth', 'fifth'];
	    
	    stepNames.forEach((stepName, index) => {
	        const container = document.querySelector(`.${stepName}-step-options`);
	        if (container) {
	            container.style.display = 'none';
	            const list = container.querySelector(`.${stepName}-step-list`);
	            if (list) {
	                list.innerHTML = '';
	            }
	        }
	    });
	}

	isMultiStepCategory(category) {
		return this.multiStepCategories.includes(category);
	}

	clearActiveCategory() {
		document.querySelectorAll('.filter-category-btn').forEach(btn => {
			btn.classList.remove('active');
		});
		this.activeCategory = null;
		this.resetStepState();
		this.clearFilterOptions();
	}
	

	// 2단계 필터 옵션 업데이트 (기존 구조 활용)
	updateMultiStepFilterOptions(category) {
	   // console.log('=== updateMultiStepFilterOptions 시작 ===');
	  //  console.log('category:', category);
	    
	    // 모든 단계 컨테이너 숨기기
	    this.hideAllStepContainers();
	    
	    // 1단계 시작
	    this.currentStep = 1;
	    this.showStepContainer(1, category);
	}

	showStepContainer(step, category, isInitialLoad = false) {
	    const stepNames = ['', 'first', 'second', 'third', 'fourth', 'fifth'];
	    const stepName = stepNames[step];
	    
	    if (!stepName || step > this.maxSteps) {
	        console.log('잘못된 단계:', step);
	        return;
	    }
	    
	    const container = document.querySelector(`.${stepName}-step-options`);
	    if (!container) {
	        console.log(`${step}단계 컨테이너를 찾을 수 없음`);
	        return;
	    }
	    
	    // 컨테이너 표시
	    container.style.display = 'flex';
	    
	    // 제목 설정
	    const titleElement = container.querySelector('p');
	    if (titleElement && step === 1) {
	        titleElement.textContent = this.getCategoryTitle(category);
	    }
	    
	    // 초기 로드시에만 데이터 로드
	    if (isInitialLoad || step === 1) {
	        this.loadAndPopulateStepData(step, category, stepName);
	    }
	}
	
	
	// 단계별 데이터 로드 및 표시
	async loadAndPopulateStepData(step, category, stepName) {
	    const list = document.querySelector(`.${stepName}-step-list`);
	    if (!list) return;
	    
	    list.innerHTML = '';
	    
	    let items;
	    if (step === 1) {
	        // 1단계는 기본 카테고리 데이터 사용
	        items = this.categoryData[category];
	    } else {
	        // 2단계 이상은 서버에서 로드
	        items = await this.loadNextStepData(category, this.stepSelections.slice(0, step - 1));
	    }
	    
	 //   console.log(`${step}단계 items:`, items);
	    
	    if (!items || !Array.isArray(items) || items.length === 0) {
	        list.innerHTML = '<li><div class="empty-state">옵션이 없습니다</div></li>';
	        return;
	    }

	    // 옵션들 추가
	    items.forEach(itemData => {
	        const li = document.createElement('li');
	        
	        const button = document.createElement('button');
	        button.type = 'button';
	        button.className = 'filter-option-item';
	        button.textContent = itemData.text;
	        button.dataset.category = category;
	        button.dataset.value = itemData.value;
	        button.dataset.step = step;
	        button.id = this.generateStepItemId(category, step, itemData.value);
	        
	        button.addEventListener('click', () => {
	            this.selectStepItem(button, itemData, step);
	        });
	        
	        li.appendChild(button);
	        list.appendChild(li);
	    });
	}
	
	// 단계별 아이템 ID 생성
	generateStepItemId(category, step, value) {
	    const stepPrefix = this.stepSelections.slice(0, step - 1).join('-');
	    return stepPrefix ? `${category}-${stepPrefix}-${value}` : `${category}-${value}`;
	}
	
	async selectStepItem(button, itemData, currentStep) {
	    const category = button.dataset.category;
	    const value = button.dataset.value;
	    
	  //  console.log(`${currentStep}단계 선택:`, value, itemData.text);
	    
	    // 현재 단계의 선택 상태 업데이트
	    this.updateStepSelection(currentStep, value, itemData.text, button);
	    
	    // 다음 단계 확인
	    if (currentStep < this.maxSteps) {
	        const nextStepData = await this.loadNextStepData(category, this.stepSelections);
	        
	        if (nextStepData && nextStepData.length > 0) {
	            // 다음 단계가 있으면 표시
	            this.currentStep = currentStep + 1;
	            this.showStepContainer(this.currentStep, category, true);
	        } else {
	            // 다음 단계가 없으면 선택 완료
	            this.completeMultiStepSelection(category);
	        }
	    } else {
	        // 최대 단계에 도달하면 선택 완료
	        this.completeMultiStepSelection(category);
	    }
	}

	// 단계 선택 상태 업데이트
	updateStepSelection(step, value, text, button) {
	    // 해당 단계 이후의 선택들과 컨테이너들 초기화
	    this.clearStepsAfter(step);
	    
	    // 현재 단계까지의 선택값과 텍스트 저장
	    this.stepSelections[step - 1] = value;
	    this.stepTexts[step - 1] = text;
	    
	    // UI 상태 업데이트
	    this.updateStepButtonStates(step, button);
	}
	
	// 특정 단계 이후의 모든 단계 초기화
	clearStepsAfter(step) {
	    // 선택값 배열 자르기
	    this.stepSelections = this.stepSelections.slice(0, step);
	    
	    // 텍스트 배열 정리
	    for (let i = step; i < this.maxSteps; i++) {
	        delete this.stepTexts[i];
	    }
	    
	    // 해당 단계 이후의 컨테이너들 숨기기
	    const stepNames = ['', 'first', 'second', 'third', 'fourth', 'fifth'];
	    for (let i = step + 1; i <= this.maxSteps; i++) {
	        const container = document.querySelector(`.${stepNames[i]}-step-options`);
	        if (container) {
	            container.style.display = 'none';
	            const list = container.querySelector(`.${stepNames[i]}-step-list`);
	            if (list) {
	                list.innerHTML = '';
	            }
	        }
	    }
	}
	
	// 단계별 버튼 상태 업데이트
	updateStepButtonStates(step, selectedButton) {
	    const stepNames = ['', 'first', 'second', 'third', 'fourth', 'fifth'];
	    const stepName = stepNames[step];
	    
	    // 해당 단계의 모든 버튼 선택 해제
	    document.querySelectorAll(`.${stepName}-step-list .filter-option-item`).forEach(btn => {
	        btn.classList.remove('selected');
	    });
	    
	    // 선택된 버튼만 활성화
	    selectedButton.classList.add('selected');
	}
	
	async loadNextStepData(category, parentSelections) {
	    if (!parentSelections || parentSelections.length === 0) {
	        return [];
	    }
	    
	    
	    const key = this.buildStepDataKey(category, parentSelections);
	    
	    if (this.stepData[key]) {
	        return this.stepData[key];
	    }
	   
	    var cdGroupSn = this.categoryCodeMapping[category];
	    
	    
	    var schCodeDiv;
	    if (category == 'schNtnCd') {
	        schCodeDiv='ntnCd';
	    }
	    
	    if (category == 'schBizFldCd') {
	    	 schCodeDiv='bizFldCd';
	    	 cdGroupSn=-1;
	    }
	    
	    
	    if(cdGroupSn == 16  && schCodeDiv=='ntnCd' && parentSelections.length == 2){
	    	return [];
	    } 
	    
	    try {
	        const response = await $.ajax({
	            url: '/common/selectCode',
	            type: 'get',
	            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
	            data: { 
	            	cdGroupSn: cdGroupSn, // 가장 최근 선택값
	                code: parentSelections[parentSelections.length - 1],
	                schCodeDiv: schCodeDiv,
	                parentSelections: parentSelections.join(','), // 모든 상위 선택값들
	                step: parentSelections.length + 1 // 다음 단계 번호
	            }
	        });
	        
	        const transformedData = response.map(item => ({
	            value: item.code,
	            text: item.text,
	            step: parentSelections.length + 1
	        }));
	        
	        this.stepData[key] = transformedData;
	        return transformedData;
	        
	    } catch (error) {
	        console.error("Failed to load next step data:", error);
	        this.stepData[key] = [];
	        return [];
	    }
	}
	
	// 단계 데이터 키 생성
	buildStepDataKey(category, parentSelections) {
	    return `${category}-${parentSelections.join('-')}`;
	}
	
	// 다단계 선택 완료
	completeMultiStepSelection(category) {
	    //console.log('다단계 선택 완료:', this.stepSelections, this.stepTexts);
	    
	    // 모든 단계의 텍스트를 조합하여 표시 텍스트 생성
	    const displayTexts = [];
	    for (let i = 0; i < this.stepSelections.length; i++) {
	        if (this.stepTexts[i]) {
	            displayTexts.push(this.stepTexts[i]);
	        }
	    }
	    
	    const displayText = displayTexts.join(' > ');
	    const finalValue = this.stepSelections[this.stepSelections.length - 1];
	    const key = `${category}-${this.stepSelections.join('-')}`;
	    
	    if (this.selectedFilters.has(key)) {
	        return;
	    }
	    
	    this.selectedFilters.set(key, {
	        category,
	        value: finalValue,
	        text: displayText,
	        stepSelections: [...this.stepSelections],
	        isMultiStep: true
	    });
	    
	    this.updateSelectedDisplay();
	}
	
	
	// 2단계 필터 요소가 없을 때의 대체 메서드
	updateFilterOptionsAsFallback(category) {
	    //console.log('2단계 필터 대체 메서드 실행');
	    
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
	    //console.log('=== updateFilterOptions 시작 ===');
	    //console.log('category:', category);
	    
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
	    
	   // console.log('category:::: ', category);
	    
	    container.innerHTML = '';

	    const items = this.categoryData[category];
	   // console.log('category:', category, 'items:', items);
	    
	    if (!items || !Array.isArray(items)) {
	        container.innerHTML = '<div class="empty-state">옵션이 없습니다</div>';
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
	   // console.log('1단계 필터 옵션 생성 완료');
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
	    //console.log('대체 컨테이너 생성 시도');
	    
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
	
	
	// 나머지 기존 메서드들...
	getCategoryTitle(category) {
		return this.categoryTitles[category] || category;
	}
	
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
	   // console.log('=== clearFilterOptions 실행 ===');
	    
	    // clearAllFilterOptions 호출
	    this.clearAllFilterOptions();
	    
	    $('#pTxt').text('');
	}

	updateSelectedDisplay() {
		const container = document.getElementById('selectedFilters');
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
	    console.log('필터 제거 시도:', key);
	    
	    // 필터 데이터 가져오기
	    const filter = this.selectedFilters.get(key);
	    
	    if (!filter) {
	        console.log('제거할 필터를 찾을 수 없음:', key);
	        return;
	    }
	    
	    console.log('제거할 필터:', filter);
	    
	    // 간단한 필터인 경우
	    if (filter.isSimpleFilter) {
	        console.log('간단한 필터 제거:', key);
	        this.selectedFilters.delete(key);
	        this.updateSelectedDisplay();
	        this.applyFiltersAfterRemoval();
	        return;
	    }
	    
	    // 다단계 필터인지 확인
	    if (filter && filter.isMultiStep) {
	        // 다단계 필터의 모든 관련 버튼 상태 제거
	        this.clearMultiStepButtonStates(key);
	    } else {
	        // 단일 단계 필터 - DOM에서 버튼 찾아서 상태 제거
	        const button = document.getElementById(key);
	        if (button) {
	            button.classList.remove('on', 'selected');
	        } else {
	            // jQuery로도 시도
	            const $button = $('#' + key);
	            if ($button.length) {
	                $button.removeClass('on selected');
	            }
	        }
	    }
	    
	    this.selectedFilters.delete(key);
	    this.updateSelectedDisplay();
	    this.applyFiltersAfterRemoval();
	}
	
	
	// 다단계 필터 버튼 상태 제거
	clearMultiStepButtonStates(key) {
		const stepNames = ['first', 'second', 'third', 'fourth', 'fifth'];
		stepNames.forEach(stepName => {
			const buttons = document.querySelectorAll(`.${stepName}-step-list .filter-option-item.selected`);
			buttons.forEach(btn => {
				if (btn.id.includes(key.split('-')[0])) { // 같은 카테고리면
					btn.classList.remove('selected');
				}
			});
		});
	}
	
	// 필터 제거 후 그리드 검색을 재실행하는 메서드
	applyFiltersAfterRemoval() {
		const filters = this.getSelectedFilters();
		/*
		const projectNameInput = document.getElementById('projectNameInput');
		const searchTerm = projectNameInput ? projectNameInput.value.trim() : '';
		*/
		//console.log('필터 제거 후 재검색:', filters);
		this.executeSearch({ filters, searchTerm: '' });
	}
	

	resetFilters() {
		
	
		
	    // 모든 선택된 필터 버튼들의 상태 제거
	    this.selectedFilters.forEach((filter, key) => {
	        const $id = $('#' + key);
	        $id.removeClass('on selected');
	    });
	    
	    // 필터 데이터 초기화
	    this.selectedFilters.clear();
	    this.updateSelectedDisplay();
	    this.clearActiveCategory();
	    
	    // 단계 상태 초기화
	    this.resetStepState();
	    
	    // 텍스트 검색어 초기화
	   // const projectNameInput = document.getElementById('projectNameInput');
	   // if (projectNameInput) {
	       // projectNameInput.value = '';
	   // }
	    
	    this.clearAllGridParams();
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
	        
	        const searchTermFields = searchForm.find('[name="searchTerm"], [name="projectName"]');
	        searchTermFields.val('');
	        
	        if (grid.gridData) {
	            Object.keys(this.categoryData).forEach(category => {
	                delete grid.gridData[category];
	            });
	            delete grid.gridData.searchTerm;
	            delete grid.gridData.projectName;
	        }
	    });
	}


	performTextSearch() {
		/*
		const projectNa
		meInput = document.getElementById('projectNameInput');
		const searchTerm = projectNameInput ? projectNameInput.value.trim() : '';
		
		if (!searchTerm) {
			alert('검색어를 입력하세요.');
			return;
		}
		 */
		this.executeSearch({ searchTerm:'' });
	}

	applyFilters() {
		const filters = this.getSelectedFilters();
		/*
		const projectNameInput = document.getElementById('projectNameInput');
		const searchTerm = projectNameInput ? projectNameInput.value.trim() : '';
		*/
		this.executeSearch({ filters, searchTerm:'' });
	}
	

	getSelectedFilters() {
	    const filters = {};
	    this.selectedFilters.forEach((filter, key) => {
	        if (!filters[filter.category]) {
	            filters[filter.category] = [];
	        }
	        
	        if (filter.dateRange) {
	            filters[filter.category].push(filter.dateRange);
	        } else if (filter.budgetValue) {
	            filters[filter.category].push(filter.budgetValue);
	        } else {
	            filters[filter.category].push(filter.value);
	        }
	    });
	    return filters;
	}

	buildSearchParams(searchData) {
	    const params = {};
	    
	    if (searchData.searchTerm) {
	        params.searchTerm = searchData.searchTerm;
	        params.projectName = searchData.searchTerm;
	    }
	    
	    if (searchData.filters) {
	        Object.entries(searchData.filters).forEach(([category, values]) => {
	            if (values.length > 0) {
	                const validValues = values.filter(v => v && v.toString().trim() !== '');
	                if (validValues.length > 0) {
	                    params[category] = validValues.length === 1 ? validValues[0] : validValues.join(',');
	                }
	            }
	        });
	    }
	    
	    return params;
	}

	
	executeSearch(searchData) {
	   // console.log('검색 실행:', searchData);
	    
	    if (this.gridInstances && this.gridInstances.length > 0) {
	        const searchParams = this.buildSearchParams(searchData);
	        
	        this.gridInstances.forEach((gridInstance, index) => {
	            this.setGridSearchParams(searchParams, gridInstance);
	            gridInstance.searchData();
	        });
	    }
	}

	setGridSearchParams(params, gridInstance = null) {
	    const targetGrids = gridInstance ? [gridInstance] : this.gridInstances;
	    
	    targetGrids.forEach((grid, index) => {
	        if (!grid || !grid.searchFormId) {
	            return;
	        }
	        
	        const searchForm = $(`#${grid.searchFormId}`);
	        if (!searchForm.length) {
	            return;
	        }
	        
	        this.clearGridFilterFields(searchForm);
	        
	        Object.entries(params).forEach(([key, value]) => {
	            let field = searchForm.find(`[name="${key}"]`);
	            
	            if (field.length === 0) {
	                field = $(`<input type="hidden" name="${key}" />`);
	                searchForm.append(field);
	            }
	            
	            if (field.length) {
	                if (field.hasClass('select2-hidden-accessible') || field.data('select2')) {
	                    field.val(value).trigger('change');
	                } else {
	                    field.val(value);
	                }
	            }
	        });
	    });
	}
	

	
	clearGridFilterFields(searchForm) {
	    console.log('그리드 필터 필드 초기화');
	    
	    // 현재 선택된 필터의 카테고리들
	    const activeCategories = new Set();
	    this.selectedFilters.forEach(filter => {
	        activeCategories.add(filter.category);
	    });
	    
	    console.log('활성 카테고리들:', Array.from(activeCategories));
	    
	    // categoryData에 있는 모든 카테고리와 추가로 자주 사용되는 필드들 초기화
	    /*
	    const allCategories = new Set([
	        ...Object.keys(this.categoryData),
	        'schNtnCd', 'schBgnDe', 'schEndDe', 'searchTerm', 'projectName' // 공통 필드들 추가
	    ]);
	    */
	    const allCategories = new Set([
	        ...Object.keys(this.categoryData)
	    ]);
	    
	    allCategories.forEach(category => {
	        if (!activeCategories.has(category)) {
	            const field = searchForm.find(`[name="${category}"]`);
	            if (field.length) {
	                console.log(`필드 초기화: ${category}`);
	                if (field.hasClass('select2-hidden-accessible') || field.data('select2')) {
	                    field.val('').trigger('change');
	                } else {
	                    field.val('');
	                }
	            }
	        }
	    });
	    
	    // 텍스트 검색어도 확인
	    /*
	    const projectNameInput = document.getElementById('projectNameInput');
	    if (!projectNameInput || !projectNameInput.value.trim()) {
	        const searchTermFields = searchForm.find('[name="searchTerm"], [name="projectName"]');
	        searchTermFields.val('');
	    }
	    */
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
	   // console.log('=== DOM 요소 확인 ===');
	    
	    // 2단계 필터 요소들
	    const firstStepContainer = document.querySelector('.first-step-options');
	    const secondStepContainer = document.querySelector('.second-step-options');
	    const firstStepList = document.querySelector('.first-step-list');
	    const secondStepList = document.querySelector('.second-step-list');
	    
	   // console.log('first-step-options:', firstStepContainer);
	   // console.log('second-step-options:', secondStepContainer);
	   // console.log('first-step-list:', firstStepList);
	   // console.log('second-step-list:', secondStepList);
	    
	    // 1단계 필터 요소들
	    const singleStepContainer = document.querySelector('.filter-options-content');
	  //  console.log('filter-options-content:', singleStepContainer);
	    
	    // 전체 필터 영역
	    const filterArea = document.getElementById('searchFilterArea');
	   // console.log('searchFilterArea:', filterArea);
	    
	    // 모든 가능한 컨테이너 확인
	    const allContainers = document.querySelectorAll('[class*="filter"], [class*="step"], [class*="options"]');
	   // console.log('모든 필터 관련 요소들:', allContainers);
	    
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