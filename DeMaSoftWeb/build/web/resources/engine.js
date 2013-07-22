var gEngine = {
    vehicleArray:[],
    pedidoArray:[],
    bloqueoarray:[],
    numAutos:20,
    numMotos:40,
    opacidadDiaria:0,
    /*rectOpacity: new google.maps.Rectangle({
        fillColor:"#222222",
        fillOpacity:0.0,
        clickable:false,
        bounds:allowedBounds,
        map: map,
        zIndex:1
    }),*/
    imagenAlmacen: new google.maps.MarkerImage(
        'resources/almacen.svg',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(12,12),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(24,24)    
    ),
    imagenBloqueo: new google.maps.MarkerImage(
        'resources/bloqueado.svg',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(4,4),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(8, 8)    
    ),
    imagenPedido: new google.maps.MarkerImage(
        'resources/pedido.png',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(4,4),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(8, 8)    
    ),
    imagenMoto: new google.maps.MarkerImage(
        'resources/moto.svg',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(8,8),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(16, 16)    
    ),
    imagenMotoRoja: new google.maps.MarkerImage(
        'resources/motoRoja.svg',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(8,8),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(16, 16)    
    ),
    imagenAuto: new google.maps.MarkerImage(
        'resources/auto.svg',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(6,6),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(12, 12)    
    ),
    imagenAutoRojo: new google.maps.MarkerImage(
        'resources/autoRojo.svg',
        null,//tamaño de la imagen real
        null,//pixel de inicio
        new google.maps.Point(6,6),//pixel de manejo (deberia ser el centro de la imagen)
        new google.maps.Size(12, 12)    
    ),
    almacen : null,
    randomColor: function(){
        var color;
        color = (Math.round(Math.random()*16777215)).toString(16);
        color = "000000".substr(0,6-color.length)+color;
        color= "#" + color;
        return color;
    },
    initVehicleArray: function(){
        //this.rectOpacity.setMap(map);
        obtenerDatosIniciales();
        this.vehicleArray = [];
        for(var i=0; i < gEngine.numAutos + gEngine.numMotos;i++){
            var img;
            if(i>=gEngine.numMotos)img = gEngine.imagenAuto;
            else img =gEngine.imagenMoto;
            var v = new Vehicle(45,30,img,gEngine.randomColor());
            this.vehicleArray.push(v);
        }
    }
};

Vehicle.prototype ={
    marker:null,
    ruta : [],
    x:0,
    y:0,
    infoWindow:null,
    cambiarInformacion: function(vehicle){
        var contenido="";
        contenido += "Placa: "+vehicle.placa+"<br>";
        contenido+= "Capacidad: "+vehicle.cap+"<br>";
        if(!vehicle.disp){
            contenido+= "Estado: En Incidencia<br>";
            if(this.marker.icon.url === "resources/auto.svg"||
               this.marker.icon.url === "resources/autoRojo.svg"){
                this.marker.setIcon(gEngine.imagenAutoRojo);
            }
            else{
                this.marker.setIcon(gEngine.imagenMotoRoja);
            }
        }
        else {
            contenido+= "Estado: Disponible<br>";
            if(this.marker.icon.url === "resources/auto.svg"||
               this.marker.icon.url === "resources/autoRojo.svg"){
                this.marker.setIcon(gEngine.imagenAuto);
            }
            else{
                this.marker.setIcon(gEngine.imagenMoto);
            }
        }
        this.infoWindow.setContent(contenido);
    },
    cambiarPos: function(x,y){
        this.marker.setPosition(new google.maps.LatLng(lat(y),lng(x),false));
    },
    cambiarRuta: function(ruta){
        this.ruta.setPath(arrayXYToarrayLL(ruta));
    },
    borrar : function(){
        this.marker.setMap(null);
        this.ruta.setMap(null);
    }
};

function Vehicle(x,y,img,color){
    var latLng = new google.maps.LatLng(lat(y),lng(x),false);
    var infoWindow = new google.maps.InfoWindow({
        content:"Vehiculo"
    });
    this.infoWindow = infoWindow;

    this.marker = new google.maps.Marker({
        map: map,//a que mapa pertenece
        position: latLng,//en que posicion esta
        icon: img,//que imagen usa
        zIndex:4
    });
    google.maps.event.addListener(this.marker,"click",function(){
        infoWindow.open(map,this);
    });
    this.ruta = new google.maps.Polyline({
        path: [],
        strokeColor: color,
        strokeOpacity: 0.9,
        strokeWeight: 2,
        map : map,
        zIndex: 2
    });
    
}

Pedido.prototype= {
    marker : null,
    infoWindow:null,
    cambiarInformacion: function(pedido){
        var contenido="";
        contenido += "Nro de Paquetes: "+pedido.numP+"<br>";
        contenido+= "Fecha Entrega: "+pedido.date+"<br>";
        this.infoWindow.setContent(contenido);
    },
    borrar : function(){
        this.marker.setMap(null);
        //this.marker.setIcon(null);
    }
};

function Pedido(x,y){
    var latLng = new google.maps.LatLng(lat(y),lng(x),false);
    var infoWindow = new google.maps.InfoWindow({
        content:"Pedido"
    });
    this.infoWindow = infoWindow;
    this.marker = new google.maps.Marker({
        position: latLng,
        icon:gEngine.imagenPedido,
        map: map,
        zIndex : 3
    });
    google.maps.event.addListener(this.marker,"click",function(){
        infoWindow.open(map,this);
    });
}

function arrayXYToarrayLL (arrayXY){
    var arrayLL =[];
    for (var i =0;i<arrayXY.length; i++) {
        var puntoLL = new google.maps.LatLng(lat(arrayXY[i].y), lng(arrayXY[i].x), false); 
        arrayLL.push(puntoLL);
    };
    return arrayLL;
}
//Atenas
function procesandoData(stringJson) {
    var myobject = JSON.parse(stringJson);
    var vehicleInfo ="<tr><th>Vehiculo</th><th>Cap</th><th>X</th><th>Y</th></tr>";
    for (var i = 0; i < myobject.vehiculos.length; i++) {
        gEngine.vehicleArray[i].x = myobject.vehiculos[i].x; // el vehiculo i del arreglo de vehiculos
        gEngine.vehicleArray[i].y = myobject.vehiculos[i].y;
        gEngine.vehicleArray[i].cambiarInformacion(myobject.vehiculos[i]);
        gEngine.vehicleArray[i].cambiarPos(myobject.vehiculos[i].x,myobject.vehiculos[i].y);
        gEngine.vehicleArray[i].cambiarRuta(myobject.vehiculos[i].ruta);

        vehicleInfo += "<tr><td>Vehiculo "+ i +"</td><td>"+gEngine.vehicleArray[i].cap+"</td><td>"+gEngine.vehicleArray[i].x+"</td><td>"+gEngine.vehicleArray[i].y+"</td></tr>";
    }
    /*gEngine.rectOpacity.setOptions({
       fillOpacity: myobject.opacidad *0.9
    });*/
    for(var i=0;i<gEngine.pedidoArray.length;){
        gEngine.pedidoArray[i].borrar();
        gEngine.pedidoArray.splice(0,1);
    }
    gEngine.pedidoArray =[];
    for(var i=0;i< myobject.pedidos.length;i++){
        var orden = new Pedido(myobject.pedidos[i].x,myobject.pedidos[i].y);
        orden.cambiarInformacion(myobject.pedidos[i]);
        gEngine.pedidoArray.push(orden);
    }
    var tiempo = myobject.tiempoRestante;
    var hora = myobject.reloj;
    document.getElementById("fechaActual").innerHTML=hora;
    document.getElementById("tiempoRestante").innerHTML = tiempo;
    document.getElementById("vehiculos").innerHTML = vehicleInfo;
}

//Adrian
var xmlhttp;
        
function loadXMLDoc(url,funcion){
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	url=ruta;
        var inputList =document.getElementsByName("filtros")[0].getElementsByTagName("input");
 
        url=url+"?";
      
        for(var i=1, l =inputList.length;i<l;i++){
            url=url+inputList[i].name+"="+inputList[i].value+"&";
            
        }
	xmlhttp.onreadystatechange=funcion;
	//alert('aca1');
	xmlhttp.open("GET",url,true);
	//alert('aca2');
	xmlhttp.send();
}


function llamaActualizaPos(){
    loadXMLDoc("ruta.jsp",function(){
      	//alert('aca3');
      	if (xmlhttp.readyState===4 && xmlhttp.status===200){
                    //alert('aca4');
                    var responseText =xmlhttp.responseText;
                    //myJSONObject = eval('(' + responseTxt + ')');
                    //funcionLlena(responseTxt);
                    procesandoData(responseText);
                     //$("#sonido").trigger("play");
      	}
    });
}


function cambiaFiltro(){
   
    if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	url="actualizaPosRutaFiltrada.html";
        
        
        var inputList =document.getElementsByName("filtros")[0].getElementsByTagName("input");
 
        url=url+"?";
      
        for(var i=0, l =inputList.length;i<l;i++){
            url=url+inputList[i].name+"="+inputList[i].value+"&";
            
        }
        
   
        
        //
//xmlhttp.onreadystatechange=funcion;
	//alert('aca1');
	xmlhttp.open("GET",url,true);
	//alert('aca2');
	xmlhttp.send();
        
}

function cambiaVelocidad(){
    if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	url="cambiaVelocidad.html";
        var slider =document.getElementById("minutosXsegundo");
        url = url +"?velocidad="+slider.value;
	//xmlhttp.onreadystatechange=funcion;
	//alert('aca1');
	xmlhttp.open("GET",url,true);
	//alert('aca2');
	xmlhttp.send();
}

function detenerSimulacion(){
    if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	url="detener.html";
	//xmlhttp.onreadystatechange=funcion;
	//alert('aca1');
	xmlhttp.open("GET",url,true);
	//alert('aca2');
	xmlhttp.send();
}

function procesarDatosIniciales(){
    if (xmlhttp.readyState===4 && xmlhttp.status===200){
        var responseText =xmlhttp.responseText;
        var objectJson = JSON.parse(responseText);
        gEngine.numAutos = objectJson.numAutos;
        gEngine.nummotos = objectJson.numMotos;
        var latLng = new google.maps.LatLng(lat(objectJson.almacen.y),lng(objectJson.almacen.x),false);
        gEngine.almacen= new google.maps.Marker({
            map: map,//a que mapa pertenece
            position: latLng,//en que posicion esta
            icon: gEngine.imagenAlmacen,//que imagen usa
            zIndex:5
        });
        for(var i=0; i< objectJson.bloqueos.length ;i++){
            var ll = new google.maps.LatLng(lat(objectJson.bloqueos[i].y),lng(objectJson.bloqueos[i].x),false);
            var b = new google.maps.Marker({
                map: map,//a que mapa pertenece
                position: ll,//en que posicion esta
                icon: gEngine.imagenBloqueo,//que imagen usa
                zIndex:2
            });
            gEngine.bloqueoarray.push(b);
        }
    }
}

function obtenerDatosIniciales(){
    if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	url=urlInicial;
	xmlhttp.onreadystatechange=procesarDatosIniciales;
	//alert('aca1');
	xmlhttp.open("GET",url,true);
	//alert('aca2');
	xmlhttp.send();
}