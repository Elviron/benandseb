// implementation of AR-Experience (aka "World")
var World = {
	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,

	// list of AR.GeoObjects that are currently shown in the scene / World
	markerList: [],

	// The last selected marker
	currentMarker: null,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

		// show radar & set click-listener
		PoiRadar.show();
		PoiRadar.setMaxDistance(600);
		$('#radarContainer').unbind('click');
		$("#radarContainer").click(PoiRadar.clickedRadar);

		// empty list of visible markers
		World.markerList = [];

		// start loading marker assets
		World.markerDrawable_idle = new AR.ImageResource("assets/coin.png");
		World.markerDrawable_selected = new AR.ImageResource("assets/marker_selected.png");

		// loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description
			};

			World.markerList.push(new Marker(singlePoi));
		}
		// updates distance information of all placemarks
		World.updateDistanceToUserValues();

		World.updateStatusMessage(currentPlaceNr + ' places loaded');
	},

	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
		for (var i = 0; i < World.markerList.length; i++) {
			World.markerList[i].distanceToUser = World.markerList[i].markerObject.locations[0].distanceToUser();
		}
	},

	// updates status message shon in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

		// request data if not already present
		if (!World.initiallyLoadedData) {
			World.requestDataFromLocal(lat, lon);
			World.initiallyLoadedData = true;
		}
	},

	// fired when user pressed maker in cam
	onMarkerSelected: function onMarkerSelectedFn(marker) {

		// deselect previous marker
		if (World.currentMarker) {
			if (World.currentMarker.poiData.id == marker.poiData.id) {
				return;
			}
			World.currentMarker.setDeselected(World.currentMarker);
		}

		// highlight current one
		marker.setSelected(marker);
		World.currentMarker = marker;
	},

	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		// you may handle clicks on empty AR space too
		if (World.currentMarker) {
			World.currentMarker.setDeselected(World.currentMarker);
		}
	},

	// request POI data
	requestDataFromLocal: function requestDataFromLocalFn(centerPointLatitude, centerPointLongitude) {
		var poisToCreate = 10;
		var poiData = [];
		/*
		for (var i = 0; i < poisToCreate; i++) {
			poiData.push({
				"id": (i + 1),
				"longitude": (centerPointLongitude + (Math.random() / 5 - 0.1)),
				"latitude": (centerPointLatitude + (Math.random() / 5 - 0.1)),
				"description": ("This is the description of POI#" + (i + 1)),
				"altitude": "100.0",
				"name": ("POI#" + (i + 1))
			});
		}
		*/
		poiData.push({
			"id": 11,
			"latitude": 57.703438,
			"longitude": 11.932321,
			"description": "This is a park bench",
			"altitude": 10.0,
			"name": "Bench"
		});
		poiData.push({
			"id": 12,
			"latitude": 57.706658,
			"longitude": 11.938874,
			"description": "This is Kuggen",
			"altitude": 1.773,
			"name": "Kuggen"
		});
		poiData.push({
			"id": 13,
			"latitude": 57.708101,
			"longitude": 11.938045,
			"description": "Lindholmen bus stop",
			"altitude": 2.496,
			"name": "Lindholmen"
		});
		poiData.push({
			"id": 14,
			"latitude": 57.707799,
			"longitude": 11.930795,
			"description": "Sannegårdshamnen bus stop",
			"altitude": 1008.150,
			"name": "Sannegårdshamnen"
		});
		poiData.push({
			"id": 15,
			"latitude": 57.705509,
			"longitude": 11.939935,
			"description": "Lidholmspiren boat stop",
			"altitude": 0.352,
			"name": "Lidholmspiren"
		});
		poiData.push({
			"id": 16,
			"latitude": 57.705483,
			"longitude": 11.933771,
			"description": "Lindholmens pizzeria",
			"altitude": 12.785,
			"name": "Pizza"
		});
		poiData.push({
			"id": 17,
			"latitude": 57.705956,
			"longitude": 11.987276,
			"description": "Nya Ullevi",
			"altitude": 5.785,
			"name": "Nya Ullevi"
		});
		poiData.push({
			"id": 18,
			"latitude": 57.652208,
			"longitude": 11.909814,
			"description": "Shopping mall Frölunda Torg",
			"altitude": 29.233,
			"name": "Frölunda Torg"
		});
		
		World.loadPoisFromJsonData(poiData);
	}

};

/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;

/* forward clicks in empty area to World */
AR.context.onScreenClick = World.onScreenClick;