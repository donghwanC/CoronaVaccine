<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Vaccine Map</title>
</head>
<body>


	<!-- <div id="data"></div> 기관확인용-->
	<div id="map" style="width: 100%; height: 100%;"></div>



	<!-- 스크립트영역 -->
	<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=d162926df95123e1d5e957ab706d93fc&libraries=services,clusterer,drawing"></script>
	<script type="text/javascript">
		$(function() {
			var markers = [];
			
			var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
		    mapOption = {
		        center: new kakao.maps.LatLng(37.564481840843406, 126.98796154758645), // 지도의 중심좌표
		        level: 11 // 지도의 확대 레벨
		    };
			
			// 지도를 생성합니다    
			var map = new kakao.maps.Map(mapContainer, mapOption);
			
			// 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성합니다
			var mapTypeControl = new kakao.maps.MapTypeControl();

			// 지도에 컨트롤을 추가해야 지도위에 표시됩니다
			// kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미합니다
			map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

			// 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성합니다
			var zoomControl = new kakao.maps.ZoomControl();
			map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
			
					
			// 마커 클러스터러를 생성합니다 
		    var clusterer = new kakao.maps.MarkerClusterer({
		        map: map, // 마커들을 클러스터로 관리하고 표시할 지도 객체 
		        averageCenter: true, // 클러스터에 포함된 마커들의 평균 위치를 클러스터 마커 위치로 설정 
		        minLevel: 10 // 클러스터 할 최소 지도 레벨 
		    });						
		
			// 주소-좌표 변환 객체를 생성합니다
			var geocoder = new kakao.maps.services.Geocoder();
			
			// api 정보 가져오기
			$.ajax({
				type : "get",
				url : "test",
				dataType : "json",
				error : function() {
					alert('통신실패!!');
				},
				success : function(data) {
					//callNum; 사무실전화번호
					//centerName; 센터명
					//operName; 운영기관
					//location; 주소

					for (var i = 0; i < data.ar.length; i++) {
						// 센터명을 split
						var splitName = data.ar[i].centerName.split(" ");
						var centerName = "";
						
						for(var x=1; x<splitName.length; x++){
							centerName += splitName[x] + " ";
						}
						
						// 마커를 생성하는 메소드
						makeMarker(data.ar[i].location, centerName, data.ar[i].callNum, data.ar[i].location);
						
					} // for
				} // success
			}); // ajax
			
			// 마커를 생성하는 메소드
			function makeMarker(location, centerName, callNum, location) {
				//console.log(location+" // "+centerName);
								
				// 주소로 좌표를 검색합니다
				geocoder.addressSearch(location, function(result, status) {

				    // 정상적으로 검색이 완료됐으면 
				    if (status === kakao.maps.services.Status.OK) {

				        var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

				        // 결과값으로 받은 위치를 마커로 표시합니다
				        var marker = new kakao.maps.Marker({
				            map: map,
				            position: coords
				        });

				        // 인포윈도우로 장소에 대한 설명을 표시합니다
				        var infowindow = new kakao.maps.InfoWindow({
				            content: '<div style="width:300px;text-align:center;padding:6px 0;">'
				            			+ '<span>' +centerName + '</span><br/>'
				            			+ '<span style="color: blue;">Tel: ' + callNum + '</span><br/><br/>'
				            			+ '<b>주소: ' + location + '</b>'
				            		+ '</div>'
				        });

				        
				        // 마커에 마우스오버 이벤트를 등록합니다				        
						kakao.maps.event.addListener(marker, 'mouseover', function() {
						  // 마커에 마우스오버 이벤트가 발생하면 인포윈도우를 마커위에 표시합니다
						  infowindow.open(map, marker);
						});
						
						// 마커에 마우스아웃 이벤트를 등록합니다
						kakao.maps.event.addListener(marker, 'mouseout', function() {
						    // 마커에 마우스아웃 이벤트가 발생하면 인포윈도우를 제거합니다
						    infowindow.close();
						});

						// 클러스터러에 보낼 변수에 marker를 푸쉬
				        markers.push(marker);
				       
				    } // if
				    
				  // 클러스터러에 마커들을 추가합니다
				  clusterer.addMarkers(markers);
				    
				});			
			} // makeMarker 메소드
			
		});
	</script>
</body>
</html>
