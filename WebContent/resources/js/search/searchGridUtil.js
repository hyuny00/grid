/**
 * 공통 그리드 매니저
 * 카테고리 데이터 로드 및 그리드 초기화를 담당하는 공통 모듈
 */
class CommonGridManager {
    constructor() {
        this.categoryData = {};
        this.codeMap = {};
    }

    /**
     * Ajax로 카테고리 데이터를 가져오는 함수
     * @param {string} category - 카테고리명
     * @param {object} categoryCodeMapping - 카테고리 코드 매핑 객체
     * @returns {Promise} 변환된 데이터
     */
    loadCategoryData(category, categoryCodeMapping) {
        return new Promise((resolve, reject) => {
            var cdGroupSn = categoryCodeMapping[category];

            // null, undefined 체크 추가
            if (!category || cdGroupSn === undefined || cdGroupSn === null) {
                console.error("Invalid category or cdGroupSn:", category, cdGroupSn);
                reject(new Error("Invalid category or cdGroupSn"));
                return;
            }

            var schCodeDiv = null; // 초기값을 null로 명시적 설정
            if(cdGroupSn=='bizFldCd'){
                schCodeDiv='bizFldCd';
                cdGroupSn= -1;
            }

            if(cdGroupSn=='instCd'){
                schCodeDiv='instCd';
                cdGroupSn= -1;
            }

            if(cdGroupSn=='conditionYn'){
                schCodeDiv='conditionYn';
                cdGroupSn= -1;
            }


            // 요청 데이터에서 null 값 제거
            var requestData = {
                cdGroupSn: cdGroupSn
            };

            // schCodeDiv가 null이 아닐 때만 추가
            if(schCodeDiv !== null && schCodeDiv !== undefined) {
                requestData.schCodeDiv = schCodeDiv;
            }

            $.ajax({
                url: '/common/selectCode',
                type: 'get',
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                data: requestData,
                success: function(data) {

                    // 데이터 유효성 검사
                    if (!data || !Array.isArray(data)) {
                        console.warn("Invalid data received for category:", category);
                        resolve([]);
                        return;
                    }

                    // 받은 데이터를 categoryData 구조로 변환
                    const transformedData = data.map(item => ({
                        value: item.code || '',  // null 방지
                        text: item.text || ''    // null 방지
                    })).filter(item => item.value !== null && item.value !== undefined); // null 값 제거

                    resolve(transformedData);
                },
                error: function(xhr, status, error) {
                    console.error("Failed to loaddata for category:", category, error);
                    reject(error);
                }
            });
        });
    }

    /**
     * 모든 동적 카테고리 데이터를 로드하는 함수
     * @param {Array} dynamicCategories - 동적 카테고리 배열
     * @param {object} categoryCodeMapping - 카테고리 코드 매핑 객체
     * @returns {Promise} 카테고리 데이터 객체
     */
    async initializeCategoryData(dynamicCategories, categoryCodeMapping) {
        const categoryData = {
            'schPeriod': [
                {
                    value: 'period_range',
                    text: '',
                    type: 'date'
                }
            ],

            'schBudget': [
                {
                    value: 'current_year_budget',
                    text: '',
                    type: 'budget'
                }
            ]
        };

        // 입력값 유효성 검사
        if (!Array.isArray(dynamicCategories) || !categoryCodeMapping) {
            console.error("Invalid parameters for initializeCategoryData");
            return categoryData;
        }

        // 동적 카테고리들을 순차적으로 로드
        for (const category of dynamicCategories) {
            // 카테고리가 유효하고 매핑이 존재하는지 확인
            if (!category || !(category in categoryCodeMapping)) {
                console.warn("Skipping invalid category:", category);
                categoryData[category] = [];
                continue;
            }

            try {
                categoryData[category] = await this.loadCategoryData(category, categoryCodeMapping);
            } catch (error) {
                console.error("Failed to load category:", category, error);
                // 실패 시 빈 배열로 초기화
                categoryData[category] = [];
            }
        }

        this.categoryData = categoryData;
        return categoryData;
    }

    /**
     * 다중 코드 데이터를 가져오는 함수
     * @param {Array} requests - 요청 배열 (기본값 제공)
     * @returns {Promise} 코드 데이터
     */
    loadMultipleCodeData(requests = null) {
        return new Promise((resolve, reject) => {
            // 기본 요청이 없거나 빈 배열이면 빈 객체 반환
            if (!requests || !Array.isArray(requests) || requests.length === 0) {
                console.log("No requests provided for loadMultipleCodeData, returning empty object");
                resolve({});
                return;
            }

            // 요청 데이터에서 null 값 제거 및 유효성 검사
            const validRequests = requests.filter(req => {
                if (!req) return false;

                // 필수 필드 체크
                const hasCodeDiv = req.schCodeDiv !== null && req.schCodeDiv !== undefined && req.schCodeDiv !== '';
                const hasCdGroupSn = req.cdGroupSn !== null && req.cdGroupSn !== undefined;

                if (!hasCodeDiv || !hasCdGroupSn) {
                    console.warn("Invalid request item:", req);
                    return false;
                }

                return true;
            }).map(req => {
                // 안전한 객체 생성
                const cleanReq = {
                    schCodeDiv: String(req.schCodeDiv).trim(),
                    code: req.code || '',
                    cdGroupSn: req.cdGroupSn
                };

                // code 필드가 빈 문자열이 아닐 때만 포함
                if (req.code && String(req.code).trim() !== '') {
                    cleanReq.code = String(req.code).trim();
                } else {
                    cleanReq.code = '';
                }

                return cleanReq;
            });

            if (validRequests.length === 0) {
                console.warn("No valid requests after filtering, returning empty object");
                resolve({});
                return;
            }

            // 요청 데이터 로깅

            // 요청 객체를 완전히 깨끗하게 만들기
            const requestPayload = {
                requests: validRequests
            };

            // JSON 문자열로 변환하여 null 키 문제 방지
            let jsonString;
            try {
                jsonString = JSON.stringify(requestPayload);
            } catch (stringifyError) {
                console.error("Failed to stringify request:", stringifyError);
                reject(new Error("Failed to create request JSON"));
                return;
            }

            $.ajax({
                url: '/common/selectCodeMultiple',
                type: 'post',
                contentType: "application/json; charset=UTF-8",
                data: jsonString,
                success: function(data) {

                    // 응답 데이터 처리
                    let cleanData = {};

                    if (data && typeof data === 'object') {
                        // 응답이 객체인 경우 null 키 제거
                        Object.keys(data).forEach(key => {
                            if (key && key !== null && key !== undefined && key !== 'null' && key.trim() !== '') {
                                cleanData[key] = data[key];
                            } else {
                                console.warn("Removing invalid key from response:", key);
                            }
                        });
                    } else if (Array.isArray(data)) {
                        // 응답이 배열인 경우 (예상치 못한 경우)
                        console.warn("Unexpected array response, converting to object");
                        cleanData = { result: data };
                    } else {
                        console.warn("Unexpected response format:", data);
                        cleanData = {};
                    }
                    console.log(cleanData)
                    resolve(cleanData);
                },
                error: function(xhr, status, error) {
                    console.error("Failed to load multiple code data:");
                    console.error("Status:", status);
                    console.error("Error:", error);
                    console.error("Response:", xhr.responseText);

                    // 에러 발생 시 빈 객체 반환하여 전체 프로세스가 중단되지 않도록 함
                    console.warn("Returning empty object due to error");
                    resolve({});
                }
            });
        });
    }


    loadMultipleCodeListData(requests = null) {
        return new Promise((resolve, reject) => {
            // 기본 요청이 없거나 빈 배열이면 빈 객체 반환
            if (!requests || !Array.isArray(requests) || requests.length === 0) {
                console.log("No requests provided for loadMultipleCodeData, returning empty object");
                resolve({});
                return;
            }

            // 요청 데이터에서 null 값 제거 및 유효성 검사
            const validRequests = requests.filter(req => {
                if (!req) return false;

                // 필수 필드 체크
                const hasCodeDiv = req.schCodeDiv !== null && req.schCodeDiv !== undefined && req.schCodeDiv !== '';
                const hasCdGroupSn = req.cdGroupSn !== null && req.cdGroupSn !== undefined;

                if (!hasCodeDiv || !hasCdGroupSn) {
                    console.warn("Invalid request item:", req);
                    return false;
                }

                return true;
            }).map(req => {
                // 안전한 객체 생성
                const cleanReq = {
                    schCodeDiv: String(req.schCodeDiv).trim(),
                    code: req.code || '',
                    cdGroupSn: req.cdGroupSn
                };

                // code 필드가 빈 문자열이 아닐 때만 포함
                if (req.code && String(req.code).trim() !== '') {
                    cleanReq.code = String(req.code).trim();
                } else {
                    cleanReq.code = '';
                }

                return cleanReq;
            });

            if (validRequests.length === 0) {
                console.warn("No valid requests after filtering, returning empty object");
                resolve({});
                return;
            }

            // 요청 데이터 로깅

            // 요청 객체를 완전히 깨끗하게 만들기
            const requestPayload = {
                requests: validRequests
            };

            // JSON 문자열로 변환하여 null 키 문제 방지
            let jsonString;
            try {
                jsonString = JSON.stringify(requestPayload);
            } catch (stringifyError) {
                console.error("Failed to stringify request:", stringifyError);
                reject(new Error("Failed to create request JSON"));
                return;
            }





            $.ajax({
                url: '/common/selectCodeListMultiple',
                type: 'post',
                contentType: "application/json; charset=UTF-8",
                data:  JSON.stringify({ requests: requests }),
                success: function(data) {

                    // 응답 데이터 처리
                    let cleanData = {};

                    if (data && typeof data === 'object') {
                        // 응답이 객체인 경우 null 키 제거
                        Object.keys(data).forEach(key => {
                            if (key && key !== null && key !== undefined && key !== 'null' && key.trim() !== '') {
                                cleanData[key] = data[key];
                            } else {
                                console.warn("Removing invalid key from response:", key);
                            }
                        });
                    } else if (Array.isArray(data)) {
                        // 응답이 배열인 경우 (예상치 못한 경우)
                        console.warn("Unexpected array response, converting to object");
                        cleanData = { result: data };
                    } else {
                        console.warn("Unexpected response format:", data);
                        cleanData = {};
                    }

                    resolve(cleanData);
                },
                error: function(xhr, status, error) {
                    console.error("Failed to load multiple code data:");
                    console.error("Status:", status);
                    console.error("Error:", error);
                    console.error("Response:", xhr.responseText);

                    // 에러 발생 시 빈 객체 반환하여 전체 프로세스가 중단되지 않도록 함
                    console.warn("Returning empty object due to error");
                    resolve({});
                }
            });
        });
    }

    /**
     * 모든 데이터를 로드하고 그리드를 초기화하는 함수
     * @param {object} config - 설정 객체
     * @param {Array} config.dynamicCategories - 동적 카테고리 배열
     * @param {object} config.categoryCodeMapping - 카테고리 코드 매핑
     * @param {Array} config.multiStepCategories - 다단계 카테고리 배열
     * @param {object} config.categoryTitles - 카테고리 타이틀 매핑
     * @param {Array} config.gridConfigs - 그리드 설정 배열
     * @param {Array} config.codeRequests - 코드 요청 배열 (선택사항)
     * @returns {Promise} 초기화된 그리드 인스턴스들과 필터 시스템
     */
    async initializeAllData(config) {
        const {
            dynamicCategories,
            categoryCodeMapping,
            maxSelectionCounts,
            multiStepCategories,
            categoryTitles,
            gridConfigs,
            codeRequests,
            codeListRequests=[]
        } = config;

        // 설정 유효성 검사
        if (!this.validateConfig(config)) {
            throw new Error('Invalid configuration provided');
        }

        try {
            // 설정 데이터 정리 (null 키 제거)
            const cleanedCategoryCodeMapping = this.cleanNullKeys(categoryCodeMapping);
            const cleanedCategoryTitles = categoryTitles ? this.cleanNullKeys(categoryTitles) : {};


            // 카테고리 데이터 로드
            const categoryData = await this.initializeCategoryData(dynamicCategories, cleanedCategoryCodeMapping);

            // 다중 코드 데이터 로드 (실패해도 진행)
            /*
            let codeMap = {};
            try {
                codeMap = await this.loadMultipleCodeData(codeRequests);
            } catch (codeError) {
                console.warn('Failed to load multiple code data, continuing with empty codeMap:', codeError);
                codeMap = {};
            }
*/
         // 다중 코드 데이터 로드 (실패해도 진행)
            let codeList = {};
            let mergedCodeListRequests = [];

            try {
                // codeRequests의 항목들을 codeListRequests에 자동 추가
                mergedCodeListRequests = [...codeListRequests];

                // codeRequests에 있는 항목 중 codeListRequests에 없는 것들 추가
                if (Array.isArray(codeRequests) && codeRequests.length > 0) {
                    codeRequests.forEach(codeReq => {
                        const exists = codeListRequests.some(listReq =>
                            listReq.schCodeDiv === codeReq.schCodeDiv &&
                            listReq.cdGroupSn === codeReq.cdGroupSn
                        );

                        if (!exists) {
                            mergedCodeListRequests.push({
                                schCodeDiv: codeReq.schCodeDiv,
                                code: codeReq.code,
                                cdGroupSn: codeReq.cdGroupSn
                            });
                        }
                    });
                }

                console.log("Merged codeListRequests:", mergedCodeListRequests);
                codeList = await this.loadMultipleCodeListData(mergedCodeListRequests);
            } catch (codeError) {
                console.warn('Failed to load multiple code data, continuing with empty codeMap:', codeError);
                codeList = {};
            }
            console.log("codeList...{}", codeList);

            // 최종 코드맵 생성
            let codeMap = {};
            if (Array.isArray(codeRequests) && codeRequests.length > 0) {
                codeRequests.forEach(({ schCodeDiv, cdGroupSn }) => {
                    if (cdGroupSn && cdGroupSn.trim() !== '') {
                        // cdGroupSn이 있을 때: 같은 cdGroupSn을 가진 mergedCodeListRequests의 모든 데이터를 합침
                        const relatedCodeListItems = mergedCodeListRequests
                            .filter(item => item.cdGroupSn === cdGroupSn)
                            .map(item => item.schCodeDiv);
                        const merged = relatedCodeListItems.flatMap(div => codeList[div] || []);
                        const codeObj = {};
                        merged.forEach(item => {
                            codeObj[item.value] = item.text;
                        });
                        codeMap[schCodeDiv] = codeObj;
                    } else {
                        // cdGroupSn이 공백일 때: schCodeDiv가 같은 것을 찾아서 직접 매칭
                        const matchingCodeListItem = mergedCodeListRequests.find(
                            item => item.schCodeDiv === schCodeDiv && (!item.cdGroupSn || item.cdGroupSn.trim() === '')
                        );
                        if (matchingCodeListItem) {
                            if (codeList[schCodeDiv]) {
                                const codeObj = {};
                                codeList[schCodeDiv].forEach(item => {
                                    codeObj[item.value] = item.text;
                                });
                                codeMap[schCodeDiv] = codeObj;
                            } else {
                                console.warn(`Warning: codeList does not contain key '${schCodeDiv}'. Available keys:`, Object.keys(codeList));
                                codeMap[schCodeDiv] = {}; // 빈 객체로 초기화
                            }
                        } else {
                            console.warn(`Warning: No matching codeListRequests found for schCodeDiv '${schCodeDiv}'`);
                            codeMap[schCodeDiv] = {};
                        }
                    }
                });
            } else {
                console.log("❌ codeRequests가 없어서 코드맵 생성 생략");
            }


            // 코드맵 저장 (정리된 버전)
            this.codeMap = this.cleanNullKeys(codeMap);
            this.codeList = this.cleanNullKeys(codeList);

            // 그리드 인스턴스들 초기화
            const gridInstances = [];

            for (const gridConfig of gridConfigs) {
                // 각 그리드 설정에 codeMap 추가


            	this.codeMap = {
            		  ...(gridConfig.codeMap ||{}),
                      ...(this.codeMap||{}),
            	}

            	const configWithCodeMap = {
                        ...gridConfig,
                        codeMap: this.codeMap,
                        selectOption: this.codeList
                };

                try {
                    const gridInstance = initTreeGrid(configWithCodeMap);
                    gridInstances.push(gridInstance);
                } catch (gridError) {
                    console.error('Failed to initialize grid:', gridConfig.gridId, gridError);
                    throw gridError; // 그리드 초기화 실패는 전체 프로세스 중단
                }
            }

            // 필터 시스템 초기화
            const filterSystem = new ODAFilterSystem(
                categoryData,
                gridInstances.length === 1 ? gridInstances[0] : gridInstances,
                multiStepCategories || [],
                cleanedCategoryTitles,
                cleanedCategoryCodeMapping,
                maxSelectionCounts
            );





            // 전역 변수로 필터 시스템 저장
            window.odaFilterSystem = filterSystem;

            return {
                gridInstances,
                filterSystem,
                categoryData,
                codeMap: this.codeMap,
                codeList : this.codeList
            };

        } catch (error) {
            console.error('Failed to initialize data:', error);
            throw error;
        }
    }

    /**
     * 카테고리 데이터 반환
     */
    getCategoryData() {
        return this.categoryData;
    }

    /**
     * 코드맵 반환
     */
    getCodeMap() {
        return this.codeMap;
    }

    /**
     * 설정 유효성 검사
     * @param {object} config - 설정 객체
     * @returns {boolean} 유효성 여부
     */
    validateConfig(config) {
        const requiredFields = ['dynamicCategories', 'categoryCodeMapping', 'gridConfigs'];

        for (const field of requiredFields) {
            if (!config[field]) {
                console.error(`Missing required field: ${field}`);
                return false;
            }
        }

        if (!Array.isArray(config.dynamicCategories)) {
            console.error('dynamicCategories must be an array');
            return false;
        }

        if (!Array.isArray(config.gridConfigs)) {
            console.error('gridConfigs must be an array');
            return false;
        }

        // categoryCodeMapping에서 null 키 체크
        Object.keys(config.categoryCodeMapping).forEach(key => {
            if (key === null || key === undefined || key === 'null') {
                console.error('Found null key in categoryCodeMapping:', key);
                delete config.categoryCodeMapping[key];
            }
        });

        return true;
    }

    /**
     * 데이터 정리 (null 키 제거)
     * @param {object} obj - 정리할 객체
     * @returns {object} 정리된 객체
     */
    cleanNullKeys(obj) {
        if (!obj || typeof obj !== 'object') return obj;

        const cleaned = {};
        Object.keys(obj).forEach(key => {
            if (key !== null && key !== undefined && key !== 'null' && key !== '') {
                cleaned[key] = obj[key];
            }
        });
        return cleaned;
    }
}

// 전역 인스턴스 생성
window.CommonGridManager = CommonGridManager;


function getGridById(gridId) {
    if (!gridInstances || gridInstances.length === 0) {
        return null;
    }

    return gridInstances.find(grid => {
        // 그리드 인스턴스에서 ID를 찾는 방법은 그리드 구현에 따라 다를 수 있음
        return grid.gridId === gridId ||
               grid.config?.gridId === gridId ||
               grid.element?.attr('id') === gridId;
    });
}

function getCurrentFilters() {
    if (!filterSystem) {
        console.error('필터 시스템이 초기화되지 않았습니다.');
        return {};
    }

    if (typeof filterSystem.getCurrentFilters === 'function') {
        return filterSystem.getCurrentFilters();
    }

    return {};
}