<%@include file="../jspf/header.jspf" %>
<script src="resources/menu.js"></script>

<div class="page-header">
    <h3>Pronóstico</h3>
    Puede observar la fecha en la cual no se puede realizar más pedidos:
</div>
<div class="span4">
    <ul class="thumbnails">

        <li class="span4">
            <div class="thumbnail">
                <img src="resources/images/p7.jpg" alt="">
                <div class="caption">
                    <form name="formPronostico" id="formPronostico" action="pronostico.html">
                        <input type="hidden" name="check" value="0">
                        <input type="checkbox" name="idPronostico" id="idPronostico" onclick="cambiaVal();" class="pull-right">
                    
                    <p><a class="btn btn-primary " onclick="empezarCarga();">Empezar Pronóstico</a> </p>
                    </form>
                   
                </div>
            </div>

        </li>
    </ul>
</div>

<div class="span6">


    <div id="fotocargando" style="width:100%;text-align:center;display:none">
        <h4>Cargando...Por Favor, espere</h4>
        <img src="resources/images/ajax-loader.gif"/>
    </div>
</div>
<div class="span12">
    <%@include file="../jspf/footer.jspf" %>
    <script src ="resources/atenas.js"></script>

    <script type="text/javascript">
        function empezarCarga(){   
        document.getElementById("formPronostico").submit();
        $('#fotocargando').show();
        



        }
        
        function cambiaVal(){
            if (document.getElementById("formPronostico").check.value==0){
                document.getElementById("formPronostico").check.value=1;
                
            }else{
                
                document.getElementById("formPronostico").check.value=0;
            }
               
            
        }
    </script>
</div>

