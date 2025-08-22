<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
  <link rel="stylesheet" href="/css/gis/leaflet.css" />
<script type="text/javascript">
   
    // JWT 쿠키 확인 후 /admin/user/userList로 이동
    function redirectToUserList() {
      
          //  window.location.href = "/admin/user/userList";
       
    }
 
    // 페이지 로드 시 자동 실행
    window.onload = function () {
        redirectToUserList();
    };
</script>
<style>
    #map {
      width: 100%;
      height: 900px;
    }
  </style>
<section id="section" class="section" >
    <div class="main-cont-box">
        main-cont-boxmain-cont-boxmain-cont-box
        
         <div id="map"></div>
  <script src="/js/gis/leaflet.js"></script>

  <script  type="text/javascript" type="module">
    // 지도 생성
    const map = L.map('map').setView([ 23.885942,45.079162], 2); // 자카르타 좌표, 줌 레벨 12

    // OpenStreetMap 타일 레이어 추가
    L.tileLayer('http://localhost:8080/tiles/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, &copy; <a href="https://carto.com/attribution">CARTO</a>, &copy; <a href="https://stadiamaps.com/" target="_blank">Stadia Maps</a>,    &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a>'
    }).addTo(map);

    
    
    // 마커 생성
  //  const marker = L.marker([-6.2088, 106.8456]).addTo(map);
    
  //  const marker = L.marker(23.885942,45.079162]).addTo(map);
    
    
	const locations = [
  {
    lat: -6.2088,
    lng: 106.8456,
    name: "Jakarta",
    population: "10M",
    area: "661.5 km²"
  },
  {
    lat: -6.9147,
    lng: 107.6098,
    name: "Bandung",
    population: "2.5M",
    area: "167.7 km²"
  }
];

locations.forEach(loc => {
	
  const popupHtml = `
    <table border="1" style="border-collapse: collapse; width: 200px;">
      <tr><th colspan="2">${loc.name}</th></tr>
      <tr><td><b>Population</b></td><td>${loc.population}</td></tr>
      <tr><td><b>Area</b></td><td>${loc.area}</td></tr>
    </table>
  `;

  
  L.marker([loc.lat, loc.lng])
    .addTo(map)
    .bindPopup(popupHtml);

});


    	

    // 마커에 팝업 내용 설정
   // marker.bindPopup('<b>자카르타</b><br>인도네시아의 수도');

    // 마커에 마우스 오버 시 팝업 자동 열기
   // marker.on('mouseover', function() {
     // marker.openPopup();
   // });
  </script>
 <!-- 

   <script>
        // 지도를 초기화하고 중심 좌표와 줌 설정
        const map = L.map('map').setView([37.5665, 126.9780], 3); // 서울

        // 타일 레이어 추가 (Spring Proxy를 통해 Mapbox 스타일 타일 호출)
        L.tileLayer('/mapbox/tiles/{z}/{x}/{y}?style=mapbox/streets-v11', {
            tileSize: 256,
            zoomOffset: 0,
            attribution: '&copy; <a href="https://www.mapbox.com/">Mapbox</a>',
            maxZoom: 18
        }).addTo(map);
    </script>
  
 -->

  
  
   <h1>Static Map Example</h1>

    <img id="mapImage" src="" alt="Map will be here" />

    <script>
		    // 지도 이미지를 로드하는 함수
		    function loadMap(lat, lng) {
		        // URL에 lat, lng 값을 정확하게 전달
		        const mapUrl = "http://localhost:8080/maps/static?lat="+lat+"&lng="+lng;
		        
		        console.log("Generated Map URL: ", mapUrl);  // URL이 잘 생성됐는지 확인
		        const imgElement = document.getElementById('mapImage');
		        imgElement.src = mapUrl;
		    }
		
		    // 예시: 샌프란시스코 위도, 경도
		    loadMap(37.7749, -122.4194);
    </script>
  
  
  
  <h2>주소를 입력하세요:</h2>
    <form id="geocodeForm">
        <input type="text" id="address" name="address" placeholder="주소 입력" required>
        <button type="submit">검색</button>
    </form>
    <br>
    <h3>결과:</h3>
    <pre id="result"></pre>

    <script>
        $(document).ready(function() {
            $('#geocodeForm').submit(function(event) {
                event.preventDefault();

                var address = $('#address').val();
                var apiKey = $('#apiKey').val();

                $.ajax({
                    type: 'GET',
                    url: '/geocode',
                    data: { address: address, apiKey: apiKey },
                    success: function(response) {
                        $('#result').text(response);
                    },
                    error: function() {
                        alert('오류가 발생했습니다.');
                    }
                });
            });
        });
    </script>
    
    </div>
</section>





