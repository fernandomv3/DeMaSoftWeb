
<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>


<div class="span12">
    <h3 id="list">Reporte de órdenes de entrega</h3>

    <form class="form-inline" action="pedido.html" method="post" id="form1" name="form1">
        <h4>Busque los criterios que desee:</h4>
        <h5>(*)Campos obligatorios</h5>
        <div class="control-group">
            <label class="control-label" style="margin-right: 10px"> Fecha Simulación: </label>
            <form:select path="ordenes2"  id="fechaSimul"  class="input-large" onchange="llenarCombo('submit')">
                <form:options items="${ordenes2}" itemValue="fechaSimulacion"  itemLabel="fechaSimulacion" />
            </form:select>

        </div>
   
        <div class="control-group">
            <label class="control-label" > Pedidos: </label>
            <input id ="codigo" type="text" name="codigo" style="margin-left: 73px" class="input-large">
        </div>
        
       
        <div class="control-group">
            <label class="control-label" for="select01" style="margin-right: 30px">Estado Pedido: </label>
            <select id="select01" class="input-large">
                <option>Entregado</option>
                <option>En camino</option>  
            </select>
        </div>

        <label style="margin-right: 19px">Fecha Inicio (*) :</label> <input type="date"  id="fechaIni" class="input-large" value="2011-01-01">
        <label style="margin-left:10px">Fecha Fin (*) :</label> <input type="date" id="fechaFin" class="input-large" value="2013-01-01">


        <button class="btn btn-primary" id ="buscarpedido" onclick="llenarHistorial();" >Generar reporte</button>

    </form>
</div>  
<div id ="formAtenas" style ="display:none">
    <!--saori: <input type ="text" id ="inputatenas" >-->
    <a href ="descargarPDF.html"><button class="btn btn-primary" id ="exportarPDF11"  >Exportar PDF</button></a>
    <br><br>
    <table class="table table-striped" id ="tabla">
        <tr>
            <th>Código de pedido</th>
            <th>Fecha de pedido</th>
            <th>Hora de pedido</th>
            <th>Fecha de recibo</th>
            <th>Hora de recibo</th>
            <th>Estado</th>
            <th>Num. Paquetes</th>
        </tr>
    </table>
</div>






<div class="span12">
    <%@include file="../jspf/footer.jspf" %>
    <script src ="resources/atenas.js"></script>
    <script src ="resources/jquery.validate.min.js"></script>
    <script>
            var xmlhttp;
            function llenarHistorial() {
                if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                    xmlhttp = new XMLHttpRequest();
                }
                else {// code for IE6, IE5
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                url = "JSONPedido.html";
                url = url + "?idPedido=" + document.getElementById("codigo").value;
                url = url + "&idEstado=" + document.getElementById("select01").value;
                url = url + "&fechaIni=" + document.getElementById("fechaIni").value;
                url = url + "&fechaFin=" + document.getElementById("fechaFin").value;
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                        var responseText = xmlhttp.responseText;
                        var lista = JSON.parse(responseText).listaHistoriaOrdenes;
                        var tabla = document.getElementById("tabla");
                        tabla.innerHTML = "<tr><th>Código de pedido</th>" +
                                "<th>Fecha de pedido</th>" +
                                "<th>Hora de pedido</th>" +
                                "<th>Fecha de recibo</th>" +
                                "<th>Hora de recibo</th>" +
                                "<th>Estado</th>" +
                                "<th>Num. Paquetes</th>" +
                                "</tr>";
                        for (var i = 0, l = lista.length; i < l; i++) {
                            var fila = document.createElement("tr");
                            fila.innerHTML = "<th>" + lista[i].idOrden + "</th>" +
                                    "<th>" + lista[i].fechaPedido + "</th>" +
                                    "<th>" + lista[i].horaPedido + "</th>" +
                                    "<th>" + lista[i].fechaEntrega + "</th>" +
                                    "<th>" + lista[i].horaEntrega + "</th>" +
                                    "<th>" + lista[i].estado + "</th>" +
                                    "<th>" + lista[i].numPaquetes + "</th>";
                            tabla.appendChild(fila);
                        }
                    }
                };
                xmlhttp.open("GET", url, true);
                xmlhttp.send();
            }
    </script>
    <script >


        var xmlhttp;
        function llenarCombo() {

            if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp = new XMLHttpRequest();
            }
            else {// code for IE6, IE5
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            url = "JSONPed.html";
            url = url + "?fechaSimul=" + document.getElementById("fechaSimul").value;
            // $('select#select2').removeChild(); 
            xmlhttp.onreadystatechange = function() {
                if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                    var responseText = xmlhttp.responseText;
                    var lista = JSON.parse(responseText).listaPedido;
                    //alert(lista[1].idOrden);
                    //var select = document.getElementById("select2");
                    var opciones = '';
                    for (var i = 0, l = lista.length; i < l; i++) {
                        pedido = lista[i].idSimulacion;
                        // select.innerHTML += '<option value="' + pedido + '">' + pedido + '</option>';
                        opciones += '<option value="' + pedido + '">' + pedido + '</option>';
                        $("select#select2").html(opciones);
                    }



                }
            };
            xmlhttp.open("GET", url, true);
            xmlhttp.send();
        }




    </script>
    <script>



        $('#form1').validate(
                {
                    rules: {
                        codigo: {
                            minlength: 1,
                            required: true,
                             digits: true
                        },
                        email: {
                            required: true,
                            email: true
                        },
                        subject: {
                            minlength: 2,
                            required: true
                        },
                        message: {
                            minlength: 2,
                            required: true
                        }
                    },
                    messages: {
                        codigo: {
                            minlength: 'Debe ingresar mínimo 2 caracteres',
                            digits:'Solo se permiten números'
                        }
                    },
                    highlight: function(element) {
                        $(element).closest('.control-group').removeClass('success').addClass('error');
                    },
                    success: function(element) {
                        element
                                .text('OK!').addClass('valid')
                                .closest('.control-group').removeClass('error').addClass('success');
                    }
                });


    </script>

</div>

