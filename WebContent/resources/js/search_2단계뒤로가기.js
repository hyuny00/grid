class ODAFilterSystem {
	
	constructor(categoryData, gridInstances = [], multiStepCategories = []) {
	    this.selectedFilters = new Map();
	    this.activeCategory = null;
	    this.activeSubCategory = null; // 2단계 카테고리 추가
	    this.isFilterOpen = false;
	    this.categoryData = categoryData || {};
	    this.subCategoryData = {}; // 2단계 데이터 저장
	    this.gridInstances = Array.isArray(gridInstances) ? gridInstances : [gridInstances].filter(Boolean);
	    this.multiStepCategories = Array.isArray(multiStepCategories) ? multiStepCategories : []; // 2단계 필터 카테고리 설정
	    
	    this.init();
	}
	
	init() {
		this.bindEvents();
		this.updateSelectedDisplay();
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
			this.clearActiveCategory();
			return;
		}

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
			this.updateFilterOptions(category);
		} else {
			this.updateFilterOptions(category);
		}
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

	updateFilterOptions(category) {
		const container = document.querySelector('.filter-options-content');
		if (!container) {
			console.log('container를 찾을 수 없음');
			return;
		}
		
		console.log('category:::: ',category);
		
		container.innerHTML = '';

		const items = this.categoryData[category];
		console.log('category:', category, 'items:', items);
		
		if (!items || !Array.isArray(items)) {
			container.innerHTML = '<div class="empty-state">옵션이 없습니다</div>';
			return;
		}
		
		// ul 요소 생성
		const ul = document.createElement('ul');
		
		items.forEach(itemData => {
			if (itemData.type === 'date') {
				const li = document.createElement('li');
				li.innerHTML = `
					<label style="font-weight: bold; margin-bottom: 5px; display: block;">${itemData.text}</label>
					<input type="date" class="date-input" data-category="${category}" data-value="${itemData.value}" style="padding: 8px; border: 1px solid #ddd; border-radius: 4px; width: 200px;">
				`;
				
				const dateInput = li.querySelector('.date-input');
				dateInput.addEventListener('change', (e) => {
					if (e.target.value) {
						this.selectDateItem(e.target, itemData.text);
					}
				});
				
				ul.appendChild(li);
			} else {
				const li = document.createElement('li');
				const button = document.createElement('button');
				button.type = 'button';
				button.className = 'filter-option-item';
				button.textContent = itemData.text;
				button.dataset.category = category;
				button.dataset.value = itemData.value;
				button.id = category+'-'+itemData.value;
				
				// 2단계 필터가 필요한 경우와 일반 필터 구분
				if (this.isMultiStepCategory(category)) {
					button.addEventListener('click', () => {
						this.selectFirstStepItem(button, itemData);
					});
				} else {
					button.addEventListener('click', () => {
						button.classList.add('on');
						this.selectFilterItem(button);
					});
				}

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
	}

	// 1단계 항목 선택 (2단계 필터용)
	async selectFirstStepItem(button, itemData) {
		const category = button.dataset.category;
		const value = button.dataset.value;
		
		// 1단계 선택 표시
		document.querySelectorAll('.filter-option-item').forEach(btn => {
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
		
		// 이미 로드된 데이터가 있으면 재사용
		if (this.subCategoryData[key]) {
			return this.subCategoryData[key];
		}
		
		try {
			// 여기서 2단계 데이터를 Ajax로 로드
			// 예: 시행기관 선택 후 해당 시행기관의 세부 항목들 로드
			
			/*
			const response = await $.ajax({
				url: '/common/selectCode', // 2단계 데이터 로드 API
				type: 'get',
				contentType: "application/x-www-form-urlencoded; charset=UTF-8",
				data: { 
					cdGroupSn: category,
					code: parentValue
				}
			});
			*/
			
			
			
			// 테스트용 하드코딩 데이터
			const response = this.getTestSubCategoryData(category, parentValue);
			
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

	// 2단계 옵션 표시
	showSecondStepOptions(category, parentValue) {
		const container = document.querySelector('.filter-options-content');
		if (!container) return;
		
		const key = `${category}-${parentValue}`;
		const subItems = this.subCategoryData[key] || [];
		
		// 기존 내용 지우고 2단계 옵션 표시
		container.innerHTML = '';
		
		if (subItems.length === 0) {
			container.innerHTML = '<div class="empty-state">하위 옵션이 없습니다</div>';
			return;
		}
		
		// 뒤로가기 버튼 추가
		const backButton = document.createElement('button');
		backButton.type = 'button';
		backButton.className = 'back-button';
		backButton.textContent = '← 뒤로가기';
		backButton.style.cssText = `
			margin-bottom: 10px;
			padding: 8px 12px;
			background: #6c757d;
			color: white;
			border: none;
			border-radius: 4px;
			cursor: pointer;
		`;
		backButton.addEventListener('click', () => {
			this.updateFilterOptions(category);
		});
		
		container.appendChild(backButton);
		
		// 2단계 옵션들
		const ul = document.createElement('ul');
		
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
			ul.appendChild(li);
		});
		
		container.appendChild(ul);
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
		const container = document.querySelector('.filter-options-content');
		if (container) {
			container.innerHTML = '';
		}
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
		// 해당 필터 버튼의 선택 상태 제거
		const parts = key.split('-');
		if (parts.length >= 3) {
			// 2단계 필터인 경우
			const $id = $('#'+key);
			$id.removeClass('on');
		} else {
			// 1단계 필터인 경우
			const $id = $('#'+key);
			$id.removeClass('on');
		}
		
		this.selectedFilters.delete(key);
		this.updateSelectedDisplay();
	}

	resetFilters() {
		this.selectedFilters.clear();
		this.updateSelectedDisplay();
		this.clearActiveCategory();
		
		const projectNameInput = document.getElementById('projectNameInput');
		if (projectNameInput) {
			projectNameInput.value = '';
		}
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
		this.selectedFilters.forEach((filter, key) => {
			if (!filters[filter.category]) {
				filters[filter.category] = [];
			}
			
			// 날짜 필터의 경우 실제 날짜 값 사용
			if (filter.dateValue) {
				filters[filter.category].push(filter.dateValue);
			} else if (filter.subValue) {
				// 2단계 필터인 경우 subValue 사용
				filters[filter.category].push(filter.subValue);
			} else {
				filters[filter.category].push(filter.value);
			}
		});
		return filters;
	}

	// 그리드 검색 파라미터 생성
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
					params[category] = values.length === 1 ? values[0] : values.join(',');
				}
			});
		}
		
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
	        
	        Object.entries(params).forEach(([key, value]) => {
	            let field = searchForm.find(`[name="${key}"]`);
	            
	            if (field.length === 0) {
	                console.log(`그리드 ${index + 1}: 필드 ${key}가 없어서 동적 생성`);
	                field = $(`<input type="hidden" name="${key}" />`);
	                searchForm.append(field);
	            }
	            
	            console.log(`그리드 ${index + 1}: 필드 ${key} 찾기:`, field.length, '값:', value);
	            
	            if (field.length) {
	                if (field.hasClass('select2-hidden-accessible') || field.data('select2')) {
	                    field.val(value).trigger('change');
	                } else {
	                    field.val(value);
	                }
	                console.log(`그리드 ${index + 1}: 필드 ${key} 설정 후 값:`, field.val());
	            }
	        });
	    });
	}
	
	
	
	
	// 테스트용 2단계 데이터 생성 메서드 (loadSubCategoryData 메서드 뒤에 추가)
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
	
	
}