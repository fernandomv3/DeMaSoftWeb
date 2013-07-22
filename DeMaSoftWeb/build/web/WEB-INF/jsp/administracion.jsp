<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>

<div class="page-header">
    <h3>Administración</h3>
    Administre la planificación de los pedidos que maneja su empresa:
</div>

<ul class="thumbnails">
        <li class="span6">
          <div class="thumbnail">
            <img src="resources/images/p5.jpg" alt="">
            <div class="caption">
              <h5>Busca Datos Finales del Pronóstico</h5>
              <p>Conozca el número de paquetes atendidos por pronóstico realizado</p>
              <p><a href="buscaPaquetes.html" class="btn btn-primary pull">Ir</a> </p>
            </div>
          </div>
        </li>
        <li class="span6">
          <div class="thumbnail">
            <img src="resources/images/p1.jpg" alt="">
            <div class="caption">
              <h5>Buscar incidencias</h5>
              <p>Busque las incidencias sucedidas</p>
              <p><a href="buscaHistorico.html" class="btn btn-primary">Ir</a> </p>
            </div>
          </div>
        </li>
     
      </ul>

    <div class="span12">
        <%@include file="../jspf/footer.jspf" %>
        <script src ="resources/atenas.js"></script>
    </div>

