<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>MapTiler Server Integration</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <!-- MapTiler SDK CSS -->
  <link href="/css/gis/maptiler-sdk.css" rel="stylesheet" />
  <style>
    body { margin: 0; padding: 0; }
    #map { position: absolute; top: 0; bottom: 0; width: 100%; }
  </style>
</head>
<body>
  <div id="map"></div>

  <!-- MapTiler SDK JS -->
  <script src="/js/gis/maptiler-sdk.umd.min.js"></script>
  
 <!-- //streets, basic, satellite, topo, hybrid, bright, dark-matter, dataviz--> 
  <script>
    // Initialize the map without exposing the API key
   
    // 기존 MapTiler 설정 재정의
  //  if (maptilersdk.config) {
    //  maptilersdk.config.apiKey = 'dummy'; // 빈 값이면 내부적으로 fallback할 수 있음
  //  }
   
    const map = new maptilersdk.Map({ 
    	
      container: 'map',
      style: 'http://localhost:8080/maps/streets/style.json', // Replace 'your-map-id' with your actual map ID
      //style: 'http://localhost:8080/maps/basic-maptiler/style.json', // Replace 'your-map-id' with your actual map ID
      //맵테일러는 경도,위도순으로 넣음
      center: [ 139.6917, 35.6895], // Example coordinates (Zurich)
      zoom: 12
    });
  </script> 
</body>
</html>
// center: [139.6917, 35.6895], // Example coordinates (Zurich)
