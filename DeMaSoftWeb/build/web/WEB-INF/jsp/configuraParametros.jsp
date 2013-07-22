<%-- 
    Document   : configuraParametros
    Created on : 05/06/2013, 07:15:49 PM
    Author     : Adrian
--%>


<%@include file="../jspf/header.jspf" %>




<div class="page-header">
    <h3>Parametros de la simulación</h3>
         Los siguientes parámetros son configurables:

</div>
<div>
    <!--cambiarParametros.html-->
    <form name="formConfiguracion" id="formConfiguracion" action="javascript:verificaDatos();" method="post" style="margin:-100px 0 0 0 ">
    
        <table style="background-color:#d0f0f6;">       
            <tr>
                <td> 
                    <strong>Velocidad del auto:</strong>
                </td>
                <td>
                    <input name="velAuto" id ="velAuto" type="text" size="10" value="${vCarros}">
                </td>

                <td> 
                    <strong>Velocidad de la moto:</strong>
                </td>
                <td>
                    <input name="velMoto" id ="velMoto" type="text" size="10" value="${vMotos}">
                </td>
            </tr>
            <br/>
            <tr>
                <td> 
                    <strong>Costo por KM del Auto:</strong>
                </td>
                <td>
                    <input name="costoCarros" id ="costoCarros" type="text" size="10" value="${costoCarros}">
                </td>

                <td> 
                    <strong>Costo por KM de la Moto:</strong>
                </td>
                <td>
                    <input name="costoMotos" id ="costoMotos" type="text" size="10" value="${costoMotos}">
                </td>
            </tr>
            <br/>


            <tr>
                <td> 
                    <strong>Numero inicial de Autos:</strong>
                </td>
                <td>
                    <input name="numAutos" id ="numAutos" type="text" size="10" value="${numAutos}">
                </td>

                <td>
                    <strong>Numero inicial de Motos:</strong>
                </td>
                <td>
                    <input name="numMotos" id ="numMotos" type="text" size="10" value="${numMotos}">
                </td>
            </tr>
            <br/>
            <tr>
                <td> 
                    <strong>Almacen X:</strong>
                </td>
                <td>
                    <input name="almacenX" id ="almacenX" type="text" size="10" value="${almacenX}">
                </td>

                <td> 
                    <strong>Almacen Y:</strong>
                </td>
                <td>
                    <input name="almacenY" id ="almacenY" type="text" size="10" value="${almacenY}">
                </td>

            </tr>
            <br/>
            <tr>
                <td> 
                    <strong>Fecha Inicio:</strong>
                </td>
                <td>
                    <input name="fechaInicio" id ="fechaInicio" type="Date" size="10" value="${anho}-${mes}-${dia}">
                </td>
                
                <td> 
                    <strong>Fecha Fin:</strong>
                </td>
                <td>
                    <input name="fechaFin" id ="fechaFin" type="Date" size="10" value="${anhoFin}-${mesFin}-${diaFin}">
                </td>
            </tr>
            <tr>                
                <td> 
                    <strong>Prefijo de archivo:</strong>
                </td>
                <td>
                    <input name="prefijo" id ="prefijo" type="text" size="10" value="${prefijo}">
                </td>
            </tr>
            <br/>
        </table>
        <br><br>
        <button id ="cambiaConfiguracion" class="btn btn-primary input-large pull" >Cambiar configuracion</button>
    </form>
</div>

<%@include file="../jspf/footer.jspf" %>
<script>

    function verificaDatos(){

    var formulario= document.getElementById("formConfiguracion");
    error="";
    if (!verificaNumeroRealPositivo(formulario.velAuto.value)){

    error=error + "La velocidad del auto debe ser un número real positivo.";
    }

    if (!verificaNumeroRealPositivo(formulario.velMoto.value)){

    error=error + "La velocidad de la moto debe ser un número real positivo.";
    }

    if (!verificaNumeroReal(formulario.costoCarros.value)){

    error=error + "El costo por Km de los autos debe ser un número real.";
    }

    if (!verificaNumeroReal(formulario.costoMotos.value)){

    error=error + "El costo por Km de las motos debe ser un número real.";
    }
    if (!verificaNumeroEnteroPositivo(formulario.numAutos.value)){

    error=error + "El numero de autos debe ser un número mayor o igual que 0.";
    }

    if (!verificaNumeroEnteroPositivo(formulario.numMotos.value)){

    error=error + "El numero de motos debe ser un número mayor o igual que 0.";
    }


    if (!verificaNumeroEnteroPositivo(formulario.almacenX.value) || formulario.almacenX.value>=150 ){

    error=error + "La posicion X del almacén debe ser un número entero mayor a 0 y menor que 150.";
    }

    if (!verificaNumeroEnteroPositivo(formulario.almacenY.value) || formulario.almacenY.value>=100){

    error=error + "La posicion Y del almacén debe ser un número entero mayor a 0 y menor que 100.";
    }
    if(formulario.fechaInicio.value==""){

        error=error + "Debe elegir una fecha de inicio";
    }
    if(formulario.fechaFin.value==""){

        error=error + "Debe elegir una fecha de fin";
    }
    
    if(formulario.fechaFin.value<formulario.fechaInicio.value){

        error=error + "La fecha de fin debe ser mayor a la fecha de inicio";
    }
    
    if (error!="")alert(error);
    else{
    formulario.action="cambiarParametros.html";
    formulario.submit();

    }


    }

    function verificaNumeroRealPositivo(numero){

    if (isNumber(numero) && numero>0) return true;
    return false;
    }

    function verificaNumeroReal(numero){

    if (isNumber(numero)) return true;
    return false;
    }

    function verificaNumeroEnteroPositivo(numero){

    if (isNumberInt(numero) && numero>=0) return true;
    return false;
    }

    function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
    }

    function isNumberInt(n) {
    if (!isNaN(parseInt(n))){

    if ((parseInt(n)-n)!=0) return false;
    }

    return !isNaN(parseInt(n)) && isFinite(n);
    }


</script>
