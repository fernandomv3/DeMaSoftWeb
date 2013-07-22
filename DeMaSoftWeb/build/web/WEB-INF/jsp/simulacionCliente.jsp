<%@include file="../jspf/header.jspf" %>

<div class="span12">

    <div class="span2" style="background-color:#f0fff0; border-color:#4b0082" hidden="hidden">
        <h4> Monitoreo de Pedidos</h4>
      <div class="inner">
          <form class="form-horizontal" name="filtros" >
            <fieldset>
                <legend>Velocidad</legend>
                <label>Minutos simulados por segundo<br>
                    <input type="range" id="minutosXsegundo" min="1" max="90" onchange="cambiaVelocidad();">
              </label>
              <legend>Filtros</legend>
              <span class="help-block">Criterios de filtro</span>
              <div class="control-group"></div>
              <label class="checkbox">
                <input type="checkbox" name="autos" checked="checked"> Mostrar autos
              </label>
              <label class="checkbox">
                <input type="checkbox" name="motos" checked="checked"> Mostrar motos
              </label>


              <div class="control-group"></div>
              <label class="checkbox">
                <input type="checkbox" name ="ida" checked="checked"> Mostrar vehículos de ida
              </label>
              <label class="checkbox">
                  <input type="checkbox" name ="vuelta" checked="checked"> Mostrar vehículos de vuelta
              </label>
            <div class="control-group"></div>
            <div class="control-group">
              <label>Placa
                <input type="text" name="numPlaca" >
              </label>
              <label>Chofer
                <input type="text" name="_idChofer" >
              </label>
              <label>Cliente
                <input type="text" name="_idCliente" value="${_idCliente}">
              </label>

            </div>

           <button type="button" onclick="cambiaFiltro();" class="btn">Filtrar</button>
            <button type="button" onclick="detenerSimulacion();" class="btn">Detener Simulación</button>
            </fieldset>
        </form>
        <div></div>
       
      </div> 
    </div>

     <div class="progress progress-striped active">
        <div class="bar" style="width: 0%;"></div>
    </div>

    <div id="map-canvas" style="width:640px;height:427px" class="span8"></div>
    <div class="span2" style="background-color:#f0fff0;" >
         Fecha Actual:
         <span id="fechaActual"></span>
         <label class="control-label">
             Tiempo restante de llegada:</label>
         <span id="tiempoRestante"></span>
    </div>

</div>

<script src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script src="resources/mapa.js"></script>
<script src="resources/engine.js"></script>
<script type="text/JavaScript">initialize()</script>
<script type="text/JavaScript">    
var ruta="actualizaPosRuta.html";
</script>
<script type="text/javascript">
window.onload = function() {
var ajaxs = 7;
var $bar = $('.bar');
var width = $('.bar').parent().width();
var chunk = width / ajaxs;

var progress = setInterval(function () {
  var $bar = $('.bar');

  if ($bar.width() >= $bar.parent().width()) {
    clearInterval(progress);
    $('.progress').removeClass('active');
    $bar.text("100%");
  } else {
    $bar.width($bar.width() + chunk);
    var perc = parseInt((($bar.width() + chunk) / $bar.parent().width()) * 100);
    $bar.text((perc > 100 ? 100 : perc) + "%");
  }
}, 800);
}
</script>   

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>


<audio id="sonido" src="http://www.sonidosmp3gratis.com/sounds/Buzzer_Alarm_01_Sound_Effect_Mp3_166.mp3"></audio>

<style type="text/css">

    [class*="span"] { 
        min-height: 1px;
        margin-left: 1px;
      }
    
    .span12 {
      width: 1250px;
    }

    .span11 {
      width: 1100px;
    }

    .span10 {
      width: 1000px;
    }

    .span9 {
      width: 900px;
    }

    .span8 {
      width: 780px;
    }

    .span7 {
      width: 700px;
    }

    .span6 {
      width: 600px;
    }

    .span5 {
      width: 500px;
    }

    .span4 {
      width: 400px;
    }

    .span3 {
      width: 300px;
    }

    .span2 {
      width: 230px;
    }

    .span1 {
      width: 100px;
    }
</style>