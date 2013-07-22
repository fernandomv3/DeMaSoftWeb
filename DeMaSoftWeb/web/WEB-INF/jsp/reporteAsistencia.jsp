<%-- 
    Document   : reporteAsistencia
    Created on : 11/06/2013, 05:02:42 PM
    Author     : Irvin
--%>

<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>

<div class="span12">
    <h3 id="list">Reporte de asistencia</h3>

    
    <form class="form-search" action="asistencia.html" method="post">  
        <h4>Busque los criterios que desee:</h4>
        <h5>(*)Campos obligatorios</h5>
        <label> Código de chofer(*)</label>
        <input id ="codigo" type="text" >
        <br>
        <label class="control-label" style="margin-right: 10px">Fecha Simulación: </label>
        <form:select path="ordenes2">
               <form:options items="${ordenes2}" itemValue="fechaSimulacion" itemLabel="fechaSimulacion" />
          </form:select>
        <br>
        <label>Fecha Inicio (*) :</label> <input type="date" id ="fechaIni" class="input-large" value="2011-01-01">
        <label>Fecha Fin (*) :</label> <input type="date" id ="fechaFin" class="input-large" value="2013-01-01">        
        
        <button class="btn btn-primary" id ="buscarchofer" onclick="llenarHistorial();" >Generar reporte</button>    
    </form>
    
    
    <div id ="formAtenas" style ="display:none">
        <!--saori: <input type ="text" id ="inputatenas" >-->
        <a href ="descargarPDF.html"><button class="btn btn-primary" id ="exportarPDF11"  >Exportar PDF</button></a>
        <br><br>    
        <table class="table table-striped" id="tabla">
            <tr>
                <th>Código de chofer</th>
                <th>Fecha de Entrada</th>
                <th>Hora de Entrada</th>
                <th>Fecha de Salida</th>
                <th>Hora de Salida</th>
            </tr>
        </table>
    </div>
</div>

<div class="span12">
    <%@include file="../jspf/footer.jspf" %>
    <script src ="resources/atenas.js"></script>
    <script>
            var xmlhttp;
            function llenarHistorial() {
                if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                    xmlhttp = new XMLHttpRequest();
                }
                else {// code for IE6, IE5
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                url = "JSONAsistencia.html";
                url = url + "?idChofer=" + document.getElementById("codigo").value;
                url = url + "&fechaIni=" + document.getElementById("fechaIni").value;
                url = url + "&fechaFin=" + document.getElementById("fechaFin").value;
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                        var responseText = xmlhttp.responseText;
                        var lista = JSON.parse(responseText).listaHistoriaAsistencia;
                        var tabla = document.getElementById("tabla");
                        tabla.innerHTML = "<tr><th>Código del chofer</th>" +
                                "<th>Fecha de Entrada</th>" +
                                "<th>Hora de Entrada</th>" +
                                "<th>Fecha de Salida</th>" +
                                "<th>Hora de Salida</th>" +
                                "</tr>";
                        for (var i = 0, l = lista.length; i < l; i++) {
                            var fila = document.createElement("tr");
                            fila.innerHTML = "<sx ss>" + lista[i].idChofer + "</td>" +
                                    "<td>" + lista[i].fechaEntrada + "</td>" +
                                    "<td>" + lista[i].horaEntrada + "</td>" +
                                    "<td>" + lista[i].fechaSalida + "</td>" +
                                    "<td>" + lista[i].horaSalida + "</td>";
                            tabla.appendChild(fila);
                        }
                    }
                };
                xmlhttp.open("GET", url, true);
                xmlhttp.send();
            }
    </script>
</div>