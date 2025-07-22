document.addEventListener('DOMContentLoaded', function() {
    // 맵 초기화 - MapLibre GL JS 사용
	const map = new maplibregl.Map({
	    container: 'map',
	    style: '/js/gis/style.json',
	    center: [127.0, 37.5],
	    zoom: 12,
	    transformRequest: function(url, resourceType) {
	        console.log(`요청 URL: ${url}, 타입: ${resourceType}`);
	        
	        // 스프라이트 URL 처리
	        if (url.includes('/sprites/') && !url.endsWith('.json') && !url.endsWith('.png')) {
	            // URL에 .json이나 .png가 없으면 적절한 확장자 추가
	            if (url.includes('@2x')) {
	                // 2x 해상도
	                return { url: url.replace('@2x', '') + '@2x.png' };
	            } else {
	                // 기본 해상도
	                return { url: url + '.png' };
	            }
	        }
	        
	        return { url };
	    }
	});
    
    // URL 스킴 변환 핸들러
    function transformMapboxRequests(url, resourceType) {
        // mapbox:// 스킴 처리
        if (url.startsWith('mapbox://')) {
            const resource = url.replace('mapbox://', '');
            
            // 소스 URL 변환
            if (resourceType === 'Source') {
                return {
                    url: `/mapbox-proxy/sources/${resource}`
                };
            }
            
            // 타일셋 URL 변환
            if (resource.includes('.')) {
                const tileset = resource.split('.')[1];
                return {
                    url: `/mapbox-proxy/tilesets/${tileset}`
                };
            }
        }
        
        // Mapbox API 직접 호출 변환
        if (url.startsWith('https://api.mapbox.com')) {
            // 스타일 요청 변환
            if (url.includes('/styles/')) {
                const styleId = url.split('/styles/v1/mapbox/')[1].split('?')[0];
                return {
                    url: `/mapbox-proxy/styles/${styleId}`
                };
            } 
            // 타일 요청 변환
            else if (url.includes('/v4/')) {
                const tilePattern = /\/v4\/(.+)\/(\d+)\/(\d+)\/(\d+)/;
                const matches = url.match(tilePattern);
                if (matches && matches.length === 5) {
                    const tileset = matches[1];
                    const z = matches[2];
                    const x = matches[3];
                    const y = matches[4].split('.')[0];
                    const format = matches[4].split('.')[1] || 'pbf';
                    return {
                        url: `/mapbox-proxy/tiles/${tileset}/${z}/${x}/${y}.${format}`
                    };
                }
            }
            // 벡터 타일 요청 변환
            else if (url.includes('/v4/')) {
                return {
                    url: url.replace('https://api.mapbox.com', '/mapbox-proxy')
                };
            } 
            // 스프라이트 요청 변환 
            else if (url.includes('/sprites/')) {
                return {
                    url: url.replace('https://api.mapbox.com', '/mapbox-proxy')
                };
            } 
            // 폰트 요청 변환
            else if (url.includes('/fonts/')) {
                return {
                    url: url.replace('https://api.mapbox.com', '/mapbox-proxy')
                };
            }
        }
        
        // 기타 요청은 그대로 유지
        return { url };
    }
    
    // 지도 컨트롤 추가
    map.addControl(new maplibregl.NavigationControl());
    
    // 지도 로드 완료 이벤트
    map.on('load', function() {
        console.log('Map loaded successfully!');
    });
    
    // 오류 이벤트 모니터링
    map.on('error', function(e) {
        console.error('Map error:', e);
    });
});