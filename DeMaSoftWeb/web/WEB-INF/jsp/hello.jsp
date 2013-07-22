<%@include file="../jspf/header.jspf" %>

<head>
    <script type="text/JavaScript">
        
        function funcionLlena(cadena){
            
            var areaTexto= document.getElementById("jsonString");
            areaTexto.innerHTML=cadena;
        }
        
        var xmlhttp;
        
        function loadXMLDoc(url,funcion)
        {
        if (window.XMLHttpRequest)
          {// code for IE7+, Firefox, Chrome, Opera, Safari
          xmlhttp=new XMLHttpRequest();
          }
        else
          {// code for IE6, IE5
          xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
          }
        url="actualizaPosRuta.html";
        xmlhttp.onreadystatechange=funcion;        
        xmlhttp.open("GET",url,true);        
        xmlhttp.send();
        }


        function llamaActualizaPos()
        {
        loadXMLDoc("ruta.jsp",function()
          {          
          if (xmlhttp.readyState==4 && xmlhttp.status==200)
            {
                        var responseTxt = xmlhttp.responseText;                        
                        //myJSONObject = eval('(' + responseTxt + ')');
                        funcionLlena(responseTxt);
                        return responseTxt;
            }
          });
        }
        
    </script> 
</head>

<body>
    ${message}
    <br>
    <a href="javascript:llamaActualizaPos()">presiona</a>
    <br>
    <textarea id="jsonString">  </textarea>
    
 </body>
<%@include file="../jspf/footer.jspf" %>

