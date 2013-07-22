<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>

<div class="page-header">
    <h3>B�squeda de Datos Finales Pron�stico </h3>
    <b> 1.</b> Escoja la fecha de Pron�stico<br>
    <b> 2.</b> Le mostrar� los n�meros de paquetes atendidos hasta la fecha.
</div>


<li class="span7">
    <form  action="pedido.html" method="post" id="form1" name="form1">

        <h4>(*)Criterios de b�squeda</h4>
        <div class="well">
            <div class="control-group">
            <label class="control-label" style="margin-right: 10px"> Fecha Pron�stico   : </label>
            <form:select path="ordenes2"  id="fecha"  class="input-large" onchange="llenarCombo('submit')">
                <form:options items="${ordenes2}" itemValue="fechaPronostico"  itemLabel="fechaPronostico" />
            </form:select>

        
   
            
         
            <button class="btn btn-primary pull-right" id ="buscarpedido" onclick="llenarHistory();" >Buscar</button>
        </div>
            </div>
    </form>
    </li>



        <li class="span4">
            <div class="thumbnail">
                <img src="resources/images/p6.jpg" alt="">
                <div class="caption">
                    <h5>Buscar Datos Finales Pron�stico</h5>
                    <p>Busque los datos del pron�stico realizado</p>

                </div>
            </div>
        </li>


<div id ="formAtenas" style ="display:none">
    <!--saori: <input type ="text" id ="inputatenas" >-->
    <table class="table table-striped" id ="tabla">
        <tr>
            <th>C�digo de Pron�stico</th>
            <th>Fecha de Pron�stico</th>
            <th>N�mero de Paquetes</th>
           
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
                url = "JSONPaquetes.html";
                url = url + "?fecha=" + document.getElementById("fecha").value;
                
               
                xmlhttp.onreadystatechange = function() {
                    if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                        var responseText = xmlhttp.responseText;
                        var lista = JSON.parse(responseText).listaPaquetes;
                        var tabla = document.getElementById("tabla");
                       
                        tabla.innerHTML = "<tr><th>C�digo de Pron�stico</th>" +
                                "<th>Fecha de Pron�stico</th>" +
                                "<th>numPaquetes</th>" +
                        "</tr>";
                        for (var i = 0, l = lista.length; i < l; i++) {
                            var fila = document.createElement("tr");
                            fila.innerHTML = "<th>" + lista[i].idPronostico + "</th>" +
                                    "<th>" + lista[i].fecha + "</th>" +
                                    "<th>" + lista[i].numPaquetes + "</th>" ;
                                    
                            tabla.appendChild(fila);
                        }
                    }
                };
                xmlhttp.open("GET", url, true);
                xmlhttp.send();
            }
    </script>
</div>

