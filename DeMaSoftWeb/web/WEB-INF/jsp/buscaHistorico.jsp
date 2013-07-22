<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>

<div class="page-header">
    <h3>Búsqueda de Incidencias </h3>
    <b> 1.</b> Escoja la fecha de Incidencia.<br>
    <b> 2.</b> Le mostrará los vehículos que  han sufrido de una incidencia en esa fecha.
</div>


<li class="span7">
    <form  action="pedido.html" method="post" id="form1" name="form1">

        <h4>(*)Criterios de búsqueda</h4>
        <div class="well">
            <div class="control-group">
            <label class="control-label" style="margin-right: 10px"> Fecha Incidencia: </label>
            <form:select path="ordenes2"  id="fecha"  class="input-large" onchange="llenarCombo('submit')">
                <form:options items="${ordenes2}" itemValue="fecha"  itemLabel="fecha" />
            </form:select>

        
   
            
         
            <button class="btn btn-primary pull-right" id ="buscarpedido" onclick="llenarHistory();" >Buscar</button>
        </div>
            </div>
    </form>
    </li>



        <li class="span4">
            <div class="thumbnail">
                <img src="resources/images/shine.png" alt="">
                <div class="caption">
                    <h5>Buscar Incidencias</h5>
                    <p>Busque los vehículos que han sufrido incidencias</p>

                </div>
            </div>
        </li>


<div id ="formAtenas" style ="display:none">
    <!--saori: <input type ="text" id ="inputatenas" >-->
    <table class="table table-striped" id ="tabla">
        <tr>
            <th>Código de incidencia</th>
            <th>Código de vehículo</th>
            <th>Fecha de incidencia</th>
            <th>Hora de incidencia</th>
           
        </tr>
    </table>
</div>

<div class="span12">
    <%@include file="../jspf/footer.jspf" %>
    <script src ="resources/atenas.js"></script>
     <script src ="resources/jquery.validate.min.js"></script>
    <script>
            var xmlhttp;
            function llenarHistory() {
                if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                    xmlhttp = new XMLHttpRequest();
                }
                else {// code for IE6, IE5
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                url = "JSONHistorico.html";
                url = url + "?fecha=" + document.getElementById("fecha").value;
                
               
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                        var responseText = xmlhttp.responseText;
                        var lista = JSON.parse(responseText).listaIncidente;
                        var tabla = document.getElementById("tabla");
                       
                        tabla.innerHTML = "<tr><th>Código de Incidencia</th>" +
                                "<th>Código de vehículo</th>" +
                                "<th>Fecha de incidencia</th>" +
                                "<th>Hora de incidencia</th>" +
                        "</tr>";
                        for (var i = 0, l = lista.length; i < l; i++) {
                            var fila = document.createElement("tr");
                            fila.innerHTML = "<th>" + lista[i].idIncidente + "</th>" +
                                    "<th>" + lista[i].idVehiculo + "</th>" +
                                    "<th>" + lista[i].fecha + "</th>" +
                                    "<th>" + lista[i].hora + "</th>";
                            tabla.appendChild(fila);
                        }
                    }
                };
                xmlhttp.open("GET", url, true);
                xmlhttp.send();
            }
    </script>
</div>

