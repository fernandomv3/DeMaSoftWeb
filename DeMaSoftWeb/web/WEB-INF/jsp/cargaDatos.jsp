<%-- 
    Document   : cargaDatos
    Created on : 12/06/2013, 01:18:49 PM
    Author     : Adrian
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../jspf/header.jspf" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        




    </head>
    <body>
        <title>Cargar Datos</title>
        <div class="page-header">
            <h3>Carga Datos</h3>
        </div>
        <div style="background-color:#f0fff0;">
            <form name="formCargaDatos" id="formConfiguracion" action="iniciaCargaDatos.html" method="post">


                <p>${error}</p>

                <table>
                    <tr>
                        <td> 
                            <strong>Elija el prefijo de su archivo(opcional):</strong>
                        </td>
                        <td>
                            <input name="prefijo" id ="prefijo" type="text" value="">
                        </td>
                    </tr>

                    <br/>

                    <tr>
                        <td> 
                            <strong>Fecha:</strong>
                        </td>
                        <td>
                            <input name="fecha" id ="fecha" type="Date" value="">
                        </td>
                    </tr>

                    <br/>



                    <tr>                                        

                        <td>
                            <button id ="cambiaConfiguracion" class="btn btn-primary input-large pull">Cambiar configuracion</button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>
<%@include file="../jspf/footer.jspf" %>

