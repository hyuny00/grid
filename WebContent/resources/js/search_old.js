class ODAFilterSystem {
	
	constructor(categoryData, gridInstances = []) {
	    this.selectedFilters = new Map();
	    this.activeCategory = null;
	    this.isFilterOpen = false;
	    this.categoryData = categoryData || {};
	    this.gridInstances = Array.isArray(gridInstances) ? gridInstances : [gridInstances].filter(Boolean);
	    
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
				// console.log('this:::: '+ btn.innerText);
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

		// 외부 클릭 시 카테고리 닫기
		// document.addEventListener('click', (e) => {
		// 	if (!e.target.closest('.fltCont') && !e.target.closest('.filter-category-btn')) {
		// 		this.clearActiveCategory();
		// 	}
		// });
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
		this.updateFilterOptions(category);
	}

	clearActiveCategory() {
		document.querySelectorAll('.filter-category-btn').forEach(btn => {
			btn.classList.remove('active');
		});
		this.activeCategory = null;
		this.clearFilterOptions();
	}

	updateFilterOptions(category) {
		const container = document.querySelector('.filter-options-content');
		if (!container) {
			console.log('container를 찾을 수 없음');
			return;
		}
		
		$('button').attr('data-category')
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
		// ul.style.cssText = `
		// 	list-style: none;
		// 	margin: 0;
		// 	padding: 0;
		// 	display: block;
		// `;
		console.log('ul 생성됨:', ul);
		
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
				console.log('일반 아이템 처리:', itemData);
				const li = document.createElement('li');
				// li.style.cssText = `
				// 	list-style: none;
				// 	display: inline-block;
				// 	margin: 0;
				// 	padding: 0;
				// `;
				
				const button = document.createElement('button');
				button.type = 'button';
				button.className = 'filter-option-item';
				button.textContent = itemData.text;
				button.dataset.category = category;
				button.dataset.value = itemData.value;
				button.id = category+'-'+itemData.value;
				// button.style.cssText = `
				// 	padding: 8px 12px;
				// 	margin: 4px;
				// 	background: #f8f9fa;
				// 	border: 1px solid #dee2e6;
				// 	border-radius: 4px;
				// 	cursor: pointer;
				// 	display: inline-block;
				// 	transition: all 0.2s ease;
				// `;

				// console.log('category::: ' ,category);
				// console.log('items::: ' ,items);
				// console.log('itemData.text::: ' ,itemData.text);
				// console.log('itemData.value::: ' ,itemData.value);
				
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
				// console.log('li 추가됨:', li);
			}
		});
		
		container.appendChild(ul);
		// console.log('ul이 container에 추가됨. 최종 HTML:', container.innerHTML);
	}
	
	
	updateFilterOptions_old(category) {
		const container = document.querySelector('.filter-options-content');
		if (!container) return;
		
		container.innerHTML = '';

		const items = this.categoryData[category];
		
		if (!items || !Array.isArray(items)) {
			container.innerHTML = '<div class="empty-state">옵션이 없습니다</div>';
			return;
		}
		
		items.forEach(itemData => {
			if (itemData.type === 'date') {
				const dateContainer = document.createElement('div');
				dateContainer.className = 'date-filter-container';
				// dateContainer.innerHTML = `
				// 	<label style="font-weight: bold; margin-bottom: 5px; display: block;">${itemData.text}</label>
				// 	<input type="date" class="date-input" data-category="${category}" data-value="${itemData.value}" style="padding: 8px; border: 1px solid #ddd; border-radius: 4px; width: 200px;">
				// `;
				
				dateContainer.innerHTML = `
					<label style="font-weight: bold; margin-bottom: 5px; display: block;">${itemData.text}</label>
					<input type="text" class="pickr flatpickr-input active" placeholder="날짜를 선택하세요" readonly="readonly">
					`;
				
				const dateInput = dateContainer.querySelector('.date-input');
				dateInput.addEventListener('change', (e) => {
					if (e.target.value) {
						this.selectDateItem(e.target, itemData.text);
					}
				});
				
				container.appendChild(dateContainer);
			} else {
				
				const optionItem = document.createElement('div');
				optionItem.className = 'filter-option-item';
				optionItem.textContent = itemData.text;
				optionItem.dataset.category = category;
				optionItem.dataset.value = itemData.value;
				optionItem.style.cssText = `
					padding: 8px 12px;
					margin: 4px;
					background: #f8f9fa;
					border: 1px solid #dee2e6;
					border-radius: 4px;
					cursor: pointer;
					display: inline-block;
					transition: all 0.2s ease;
				`;
				
				optionItem.addEventListener('click', () => {
					this.selectFilterItem(optionItem);
				});

				optionItem.addEventListener('mouseenter', () => {
					optionItem.style.background = '#e9ecef';
				});

				optionItem.addEventListener('mouseleave', () => {
					optionItem.style.background = '#f8f9fa';
				});
				
				container.appendChild(optionItem);
			}
		});
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

		// 선택 시각적 효과
		// item.style.background = '#28a745';
		// item.style.color = 'white';
		// item.style.pointerEvents = 'none';

		this.updateSelectedDisplay();
	}

	clearFilterOptions() {
		const container = document.querySelector('.filter-options-content');
		if (container) {
			// container.innerHTML = '<div class="empty-state">카테고리를 선택하면 여기에 옵션이 표시됩니다</div>';
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
			// if (applyBtn) applyBtn.disabled = true;
			if (resetBtn) resetBtn.disabled = true;
			return;
		}

		// if (applyBtn) applyBtn.disabled = false;
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
		// console.log('this::: ' + key);
		const $id = $('#'+key);
		// console.log('$id::: ' + $id);
		// console.log('$id::: ' + '#'+key);
		// console.log('$id.html()::: ' + $id.html());
		$id.removeClass('on');
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
			// 또는 특정 필드명으로 매핑
			params.projectName = searchData.searchTerm;
		}
		
		// 필터 조건들을 파라미터로 변환
		if (searchData.filters) {
			Object.entries(searchData.filters).forEach(([category, values]) => {
				if (values.length > 0) {
					// 배열을 콤마로 구분된 문자열로 변환하거나 그대로 사용
					params[category] = values.length === 1 ? values[0] : values.join(',');
				}
			});
		}
		
		return params;
	}

	executeSearch(searchData) {
	    console.log('검색 실행:', searchData);
	    
	    // 그리드 인스턴스들이 있으면 모든 그리드에서 검색 실행
	    if (this.gridInstances && this.gridInstances.length > 0) {
	        const searchParams = this.buildSearchParams(searchData);
	        
	        this.gridInstances.forEach((gridInstance, index) => {
	            console.log(`그리드 ${index + 1} 검색 실행`);
	            
	            // 각 그리드의 검색폼에 파라미터 설정
	            this.setGridSearchParams(searchParams, gridInstance);
	            
	            // 그리드 검색 실행
	            gridInstance.searchData();
	        });
	    } else {
	        // 그리드 인스턴스가 없으면 기본 알림
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
	

	// 그리드 검색폼에 파라미터 설정
	// 그리드 검색폼에 파라미터 설정
	setGridSearchParams(params, gridInstance = null) {
	    console.log('설정할 파라미터:', params);
	    
	    // 특정 그리드 인스턴스가 전달되지 않으면 모든 그리드에 적용
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
	            
	            // 필드가 없으면 hidden input으로 동적 생성
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
}