<%@include file="../jspf/header.jspf" %>
<header class="jumbotron subhead" id="overview">
    <div style="text-align:center; min-height: 100px">
        <h1>DeMaSoft</h1>
        <p class="lead"><b>Delivery Management Software</b></p>
    </div>
</header>
<div class="span10">
    <div class="well">

        <h2>BIENVENIDOS</h2>
        <b>
            Sistema de planificación de entrega de pedidos.
            Siga la ruta completa de su paquete.

        </b></div>
</div>
    <div class="span5">
        <div class="page-header">
            <h2> Ubique su pedido</h2>
        </div>  

        <form id="busquedaCliente" action="cliente.html">
            <div class="well">

                <label>Código de cliente: </label>
                <input name="_idCliente" type="text" id="idCliente" class="span3"  autocomplete="off">
                <button id="busqueda-submit" type="submit" class="btn btn-primary input-medium pull-right">Buscar</button>
            </div> 
        </form>
        <form id="busquedaChofer" action="chofer.html">
            <div class="well">

                <label>Código de chofer: </label>
                <input name="_idChofer" type="text" id="idChofer" class="span3"  autocomplete="off">
                <button id="busqueda-submit" type="submit" class="btn btn-primary input-medium pull-right">Buscar</button>
            </div> 
        </form> 


    </div>

<div class="span5">
     <div class="page-header">
         <h3></h3>
     </div>
    <div id="this-carousel-id" class="carousel slide"><!-- class of slide for animation -->
        <div class="carousel-inner">
            <div class="item active"><!-- class of active since it's the first item -->
                <img src="resources/images/p2.jpg" alt="" />
                <div class="carousel-caption">
                    <p>Pedidos</p>
                </div>
            </div>
            <div class="item">
                <img src="resources/images/p3.jpg" alt="" />
                <div class="carousel-caption">
                    <p>Vehículos</p>
                </div>
            </div>
            <div class="item">
                <img src="resources/images/p4.jpg" alt="" />
                <div class="carousel-caption">
                    <p>Entregas</p>
                </div>
            </div>
            <div class="item">
                <img src="resources/images/p5.jpg" alt="" />
                <div class="carousel-caption">
                    <p>Trabajo</p>
                </div>
            </div>
        </div><!-- /.carousel-inner -->
        <!--  Next and Previous controls below
              href values must reference the id for this carousel -->
        <a class="carousel-control left" href="#this-carousel-id" data-slide="prev">&lsaquo;</a>
        <a class="carousel-control right" href="#this-carousel-id" data-slide="next">&rsaquo;</a>
    </div><!-- /.carousel -->
</div>



<div class="span12">
    <div class="container-fluid container-contact container-blue connector connector-left connector-contact">
        <div class="page-header">
            <h1>Contáctenos</h1>
        </div>

        <div class="row">


            <div class="span6">
                <form id="contact-form">
                    <div class="controls controls-row">
                        <input id="contact-name" type="text" class="span3" placeholder="Nombre"> 
                        <input id="contact-email" type="email" class="span3" placeholder="Email">
                    </div>
                    <div class="controls">
                        <textarea id="contact-message" class="span6 non-resizable" placeholder="Mensaje" rows="5"></textarea>
                    </div>

                    <div class="form-actions">
                        <button id="contact-submit" type="submit" class="btn btn-primary input-medium pull-right">Enviar</button>
                    </div>
                </form>
            </div>
        </div>

    </div>
</div>

<div class="span12">
    <script>
        window.onload = function() {
        $('.carousel').carousel({
        interval: 2500
        });
        };
    </script>
    <script src="//maps.google.com/maps?file=api&amp;v=2&amp;sensor=false&amp;key="
    type="text/javascript"></script>
    <script type="text/javascript">
        var map;

        function initialize() {
        if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map_canvas"));
        map.setCenter(new GLatLng(37.4419, -122.1419), 13);
        map.setUIToDefault();
        }
        }
    </script>
    <style type="text/css"> 
        body{
            background: url('resources/images/fondo.jpg');
            background-size:100%;

        }

    </style>

    <%@include file="../jspf/footer.jspf" %>
</div>
