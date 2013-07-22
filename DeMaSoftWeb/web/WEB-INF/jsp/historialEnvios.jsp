<%-- 
    Document   : historialEnvios
    Created on : 11-jun-2013, 14:07:58
    Author     : Marce
--%>

<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>

<div class="span12">
    <h3 id="list">Reporte de envíos hacia clientes</h3>

    <form class="form-search" action="pedido.html" method="post">
        <h4>Busque los criterios que desee:</h4>
        <h5>(*)Campos Obligatorios</h5>
        <label> Código de cliente(*)</label>
        <input id ="codigo" type="text" >
        <br>
        <label class="control-label" style="margin-right: 10px"> Fecha Simulación: </label>
        <form:select path="ordenes2">
               <form:options items="${ordenes2}" itemValue="fechaSimulacion" itemLabel="fechaSimulacion" />
          </form:select>
        <br>
        <label>Fecha Inicio (*) :</label> <input type="date" id="fechaIni" class="input-large" value="2011-01-01">
        <label>Fecha Fin (*) :</label> <input type="date" id="fechaFin" class="input-large" value="2013-01-01">


        <button class="btn btn-primary" id ="buscarcliente" onclick="llenarHistorial();" >Generar Reporte</button>
    </form>

    <div id ="formAtenas" style ="display:none">
        <!--saori: <input type ="text" id ="inputatenas" >-->
        <a href ="descargarPDF.html"><button class="btn btn-primary" id ="exportarPDF11"  >Exportar PDF</button></a>
        <br><br>    
        <table class="table table-striped" id="tabla">
            <tr>
                <th>Código de pedido</th>
                <th>Fecha de pedido</th>
                <th>Hora de pedido</th>
                <th>Fecha de recibo</th>
                <th>Hora de recibo</th>
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
                url = "JSONEnvios.html";
                url = url + "?idCliente=" + document.getElementById("codigo").value;
                url = url + "&fechaIni=" + document.getElementById("fechaIni").value;
                url = url + "&fechaFin=" + document.getElementById("fechaFin").value;
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                        var responseText = xmlhttp.responseText;
                        var lista = JSON.parse(responseText).listaHistoriaCliente;
                        var tabla = document.getElementById("tabla");
                        tabla.innerHTML = "<tr><th>Código de pedido</th>" +
                                "<th>Fecha de pedido</th>" +
                                "<th>Hora de pedido</th>" +
                                "<th>Fecha de recibo</th>" +
                                "<th>Hora de recibo</th>" +
                                "</tr>";
                        for (var i = 0, l = lista.length; i < l; i++) {
                            var fila = document.createElement("tr");
                            fila.innerHTML = "<td>" + lista[i].idOrden + "</td>" +
                                    "<td>" + lista[i].fechaPedido + "</td>" +
                                    "<td>" + lista[i].horaPedido + "</td>" +
                                    "<td>" + lista[i].fechaEntrega + "</td>" +
                                    "<td>" + lista[i].horaEntrega + "</td>";
                            tabla.appendChild(fila);
                        }
                    }
                };
                xmlhttp.open("GET", url, true);
                xmlhttp.send();
            }
    </script>
</div>
