var allowedBounds = new google.maps.LatLngBounds(
   new google.maps.LatLng(-12.111165980200362, -77.04111099243164), 
   new google.maps.LatLng(-12.075413541074033, -76.98626518249512)
);

var lastValidCenter;
var centro = new google.maps.LatLng(-12.0932897606372,-77.01368808746338);
function lat(y){
  return (y * 0.03575243912633/100.0)-12.111165980200362;
}
function lng(x){
  return (x * 0.05484580993652/150.0)-77.04111099243164;
}

function CoordMapType(name) {
  this.tileSize = new google.maps.Size(256, 256);
  this.maxZoom = 18;
  this.minZoom = 14;
  this.name = name;
}

CoordMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
  var div = ownerDocument.createElement('DIV');
  div.style.width = this.tileSize.width + 'px';
  div.style.height = this.tileSize.height + 'px';
  return div;
};

var map;

function initialize() { 
  map = new google.maps.Map(document.getElementById('map-canvas'), {
    zoom: 14,
    minZoom:14,
    maxZoom:17,
    center: centro,
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    disableDefaultUI: true,
    panControl: true,
    zoomControl: true
  });

  map.overlayMapTypes.insertAt(0,new CoordMapType(new google.maps.Size(256,256)));

  lastValidCenter= map.getCenter();

  google.maps.event.addListener(map, 'center_changed', function() {
    if (allowedBounds.contains(map.getCenter())) {
        // still within valid bounds, so save the last valid position
        lastValidCenter = map.getCenter();
        return; 
    }
    google.maps.event.addListener(map,"click",function(event){
        console.log(event.latLng.toString());
    });
    // not valid anymore => return to last valid position
    map.panTo(lastValidCenter);
  });
  gEngine.initVehicleArray();
  gEngine.timer = setInterval(llamaActualizaPos,500);
}
