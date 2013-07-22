var gEngine = {
	mapHeight: 100010,
	mapWidth: 150010,
	nodeDistance: 1000,
	nodeRadius: 70,
	numNodesX:150,
	numNodesY:100,
	nodeArray:[],
	initNodeArray: function(){
		for(var i = 0; i< this.numNodesX;i++){
			for(var j=0; j< this.numNodesY;j++){
				this.nodeArray.push(new Node(this.nodeRadius+i*this.nodeDistance,
				this.nodeRadius+j*this.nodeDistance));
			}
		}
	},
	vehicleArray:[],
        randomColor: function(){
            var color;
            color = (Math.round(Math.random()*16777215)).toString(16);
            color = "000000".substr(0,6-color.length)+color;
            color= "#" + color;
            return color;
        },
	initVehicleArray: function(){
		this.vehicleArray = [];
		for(var i=0; i < 60;i++){
                        var v = new Vehicle(45000,30000,this.images[0]);
                        v.routeColor = gEngine.randomColor();
			this.vehicleArray.push(v);
		}
	},
	images :[],
        pedidoArray:[],
        opacidadDiaria:0,
	imgCount :0,
	imagesLoaded: false,
	loadImages : function(){
		var carImage = new Image();
		carImage.onload = function(){
			gEngine.images.push(this);
			gEngine.imgCount++;
			gEngine.imagesLoaded =true;
			if(gEngine.imagesLoaded){
				gEngine.initVehicleArray();
                                gEngine.timer = setInterval(updatePositions,500); //Atenas
				visualizer.draw();
                                
			}
		}
		carImage.src = "resources/auto.svg";
                
	}
        
}

function Rect(x,y,w,h){
	this.x=x;
	this.y=y;
	this.w=w;
	this.h=h;
}

function Canvas(canvas){
	this.canvas = canvas;
	this.ctx = canvas.getContext("2d");
	this.dirtyRects =[];
}

Canvas.prototype ={
	canvas: null,
	ctx:null,
	dirtyRects:[],
	clearCanvas : function(){
		var rect;
		while(this.dirtyRects.length != 0){
			rect = this.dirtyRects.pop();
			this.ctx.clearRect(rect.x,rect.y,rect.w,rect.h);
		}
	}
}

function Node(x,y){
	this.x =x;
	this.y =y;
}

Node.prototype ={
	x:0,
	y:0,
	borderColor:'#606060',
	fillColor:'#AAAAAA',
	streetColor:'#DDDDDD',
	streetBorderColor:"#000000",
	radius:gEngine.nodeRadius,
	canvasX:0,
	canvasY:0,
	canvasRadius:0,
	canvasDistance:0,
	draw: function(visualizer){
		var ctx = gEngine.mapCanvas.ctx;
		this.canvasX = (this.x - visualizer.x) * visualizer.ratio;
		this.canvasY = (this.y - visualizer.y) * visualizer.ratio;
		this.canvasRadius = this.radius*visualizer.ratio;
		this.canvasDistance = gEngine.nodeDistance*visualizer.ratio;
		//dibujar las 2 calles
		ctx.fillStyle = this.streetColor;
		ctx.beginPath();
		ctx.rect(this.canvasX,this.canvasY - (this.canvasRadius /2),this.canvasDistance,this.canvasRadius);
		ctx.fill();
		gEngine.mapCanvas.dirtyRects.push(new Rect(this.canvasX-1,this.canvasY - (this.canvasRadius /2)-1,this.canvasDistance+2,this.canvasRadius+2));
		
		ctx.beginPath();
		ctx.rect(this.canvasX - (this.canvasRadius /2),this.canvasY,this.canvasRadius,this.canvasDistance);
		ctx.fill();
		gEngine.mapCanvas.dirtyRects.push(new Rect(this.canvasX - (this.canvasRadius /2)-1,this.canvasY-1,this.canvasRadius +2,this.canvasDistance+2));
		//dibujar el circulo
		ctx.beginPath();
		ctx.arc(this.canvasX,this.canvasY,this.canvasRadius,0,360,true);
		ctx.strokeStyle=this.borderColor;
		ctx.stroke();
		ctx.fillStyle = this.fillColor;
		ctx.fill();
		gEngine.mapCanvas.dirtyRects.push(new Rect(this.canvasX-this.canvasRadius -1,this.canvasY-this.canvasRadius -1,this.canvasRadius*2 +2,this.canvasRadius*2+2));
		
	}
}

function Vehicle(x,y,img){
	this.x=x;
	this.y=y;
	this.sprite = img;
}

function Pedido(x,y){
    this.x=x;
    this.y=y;
}

Pedido.prototype= {
    x:0,
    y:0,
    prioridad:10,
    side: 450,
    draw: function (visualizer){
        var ctx = gEngine.routesCanvas.ctx;
        var canvasX = (this.x - visualizer.x - this.side/2 + gEngine.nodeRadius/2) * visualizer.ratio;
	var canvasY = (this.y - visualizer.y) * visualizer.ratio;
        ctx.fillStyle = "#0000FF";
        ctx.beginPath();
	ctx.rect(canvasX,canvasY, this.side*visualizer.ratio,this.side*visualizer.ratio);
        ctx.fill();
    }
}

Vehicle.prototype ={
	x:0,
	y:0,
	width:gEngine.nodeRadius*4,
	heigth:gEngine.nodeRadius*4 *0.7,
	type: "auto",
	sprite: gEngine.images[0],
        routeColor: "#FF0000",
	ruta : [],
	draw : function(visualizer){
		var ctx = gEngine.vehicleCanvas.ctx;
		var canvasX = (this.x - visualizer.x - this.width/2 + gEngine.nodeRadius/2) * visualizer.ratio;
		var canvasY = (this.y - visualizer.y) * visualizer.ratio;
		ctx.drawImage(this.sprite,canvasX-3,canvasY-3, this.width*visualizer.ratio+6,this.heigth*visualizer.ratio+6);
		this.drawRoutes(visualizer);
		gEngine.vehicleCanvas.dirtyRects.push(new Rect(canvasX-4,canvasY-4, this.width*visualizer.ratio+8,this.heigth*visualizer.ratio+8));
	},
	drawRoutes: function(visualizer){
		for(var i =0; i < this.ruta.length -1;i++){
			this.drawSegment(this.ruta[i],this.ruta[i+1],visualizer);
		}
	},
	drawSegment: function(n1,n2,v){
		var ctx = gEngine.routesCanvas.ctx;
		var canvasX = (n1.x - v.x) * v.ratio;
		var canvasY = (n1.y - v.y) * v.ratio;
		var canvasRadius = n1.radius*v.ratio;
		var canvasDistance = gEngine.nodeDistance*v.ratio;
                ctx.fillStyle = this.routeColor;
		ctx.beginPath();
		var h,w;

		if(n2.x !== n1.x){
			canvasY -= canvasRadius /2;
			h = canvasRadius;
			var sign = n2.x < n1.x ? -1:1;
			w = canvasDistance * sign;
		}
		else{
			canvasX -= canvasRadius /2;
			w = canvasRadius;
			var sign = n2.y < n1.y ? -1:1;
			h = canvasDistance * sign;
		}
		ctx.rect(canvasX-1,canvasY-1,w+2,h+2);
		ctx.fill();
	}
}

function Visualizer(x,y,w,h){
	this.x =x;
	this.y =y;
	this.w=w;
	this.h=h;

	this.nodeIsContained= function(node){
		var xConstraint = (node.x >= (this.x - node.radius) )&& 
		(node.x <= (this.x + this.w + node.radius) );
		var yConstraint = (node.y >= (this.y - node.radius) )&& 
		(node.y <= (this.y + this.h + node.radius) );
		return xConstraint || yConstraint; 
	};

	this.vehicleIsContained = function(v){
		var xConstraint = (v.x + v.width >=(this.x )) &&
		(v.x <= (this.x + this.w));
		var yConstraint = (v.y + v.height  >= (this.y) )&& 
		(v.y <= (this.y + this.h ) );
		return xConstraint || yConstraint; 
	};
        
        this.drawDayLight = function(){
            var color ='#1010FF';
            
            ctx = gEngine.lightCanvas.canvas.getContext('2d');
            ctx.clearRect(0,0,gEngine.lightCanvas.canvas.width,gEngine.lightCanvas.canvas.height);
            ctx.fillStyle = color; 
            ctx.globalAlpha = gEngine.opacidadDiaria/100;  
            ctx.fillRect(0,0,gEngine.lightCanvas.canvas.width,gEngine.lightCanvas.canvas.height);

            
        };

	this.draw= function(){
		this.ratio = gEngine.mapCanvas.canvas.width/this.w;
		for(var i =0; i < gEngine.nodeArray.length;i++){
			var n = gEngine.nodeArray[i];
			if(this.nodeIsContained(n)){
				n.draw(this);
			}
		}
		//gEngine.mapCanvas.dirtyRects.push(new Rect(0,0,gEngine.mapCanvas.canvas.width,gEngine.mapCanvas.canvas.height));
		gEngine.routesCanvas.dirtyRects.push(new Rect(0,0,gEngine.routesCanvas.canvas.width,gEngine.routesCanvas.canvas.height));
		for(var i=0; i < gEngine.vehicleArray.length;i++){
			var v = gEngine.vehicleArray[i];
			if(this.vehicleIsContained(v)){
				v.draw(this);
			}
		}
                for(var i=0; i < gEngine.pedidoArray.length;i++){
                    var ped = gEngine.pedidoArray[i];
                    ped.draw(this);
                }
                this.drawDayLight();
	};
        this.drawUpdates= function(){
		//gEngine.mapCanvas.dirtyRects.push(new Rect(0,0,gEngine.mapCanvas.canvas.width,gEngine.mapCanvas.canvas.height));
		gEngine.routesCanvas.dirtyRects.push(new Rect(0,0,gEngine.routesCanvas.canvas.width,gEngine.routesCanvas.canvas.height));
		for(var i=0; i < gEngine.vehicleArray.length;i++){
			var v = gEngine.vehicleArray[i];
			if(this.vehicleIsContained(v)){
				v.draw(this);
			}
		}
                for(var i=0; i < gEngine.pedidoArray.length;i++){
                    var ped = gEngine.pedidoArray[i];
                    ped.draw(this);
                }
                this.drawDayLight();
	};
}

//Atenas

function procesandoData(stringJson) {
    var myobject = JSON.parse(stringJson);
    for (var i = 0; i < myobject.vehiculos.length; i++) {
        gEngine.vehicleArray[i].x = myobject.vehiculos[i].x*gEngine.nodeDistance; // el vehiculo i del arreglo de vehiculos
        gEngine.vehicleArray[i].y = myobject.vehiculos[i].y*gEngine.nodeDistance;
        //alert(gEngine.vehicleArray[0].x);
        //crea lista de rutas
        gEngine.vehicleArray[i].ruta =[];
        for (var j = 0; j < myobject.vehiculos[i].ruta.length; j++) {
            node = gEngine.nodeArray[myobject.vehiculos[i].ruta[j].y + gEngine.numNodesY * myobject.vehiculos[i].ruta[j].x];   // obtener el nodo                           
            gEngine.vehicleArray[i].ruta.push(node);   //ingresar el nodo     
            
        }

    }
    gEngine.opacidadDiaria = myobject.opacidad;
    gEngine.pedidoArray =[];
    for(var i=0;i< myobject.pedidos.length;i++){
        orden = new Pedido(myobject.pedidos[i].x*gEngine.nodeDistance,myobject.pedidos[i].y*gEngine.nodeDistance);
        gEngine.pedidoArray.push(orden);
    }
    var tiempo = myobject.tiempoRestante;
    document.getElementById("tiempoRestante").innerHTML = tiempo;
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
                     $("#sonido").trigger("play");
      	}
    });
}

function updatePositions(){
	llamaActualizaPos();
	gEngine.vehicleCanvas.clearCanvas();
	gEngine.routesCanvas.clearCanvas();
	visualizer.drawUpdates();
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



