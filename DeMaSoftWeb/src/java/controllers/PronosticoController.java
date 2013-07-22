/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author Adrian
 */

import DeMaSoft.GestorVehiculos;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;
import pronosticafallo.RouteCalculatorPronostico;
import javax.servlet.ServletContext;
import models.Vehicle;
import pronosticafallo.PronosticaFallo ;
import org.springframework.beans.factory.annotation.Autowired;
import pronosticafallo.GeneraArchivosPronostico;

@Controller
public class PronosticoController {
    
    private @Autowired ServletContext servletContext;
    
    
       @RequestMapping("/carga")
    public ModelAndView cargaPronostico() {
       
        
        return new ModelAndView("carga");
    }
       
       
    @RequestMapping("/getInicialesPronostico")//declarar que este metodo respondera a /cambiaVelocidad    
    public ModelAndView getDatosIniciales() {
        String textoJSON =PronosticaFallo.getRouteCalculatorPronostico().getBloqueosyAlmacen();
        return new ModelAndView("ruta", "textoJSON", textoJSON);
    }
       
       
    @RequestMapping("/pronostico")
    public ModelAndView pronostico(/*@RequestParam("check") int id*/) {
        if (RouteCalculatorPronostico.isEnEjecucion()) {
            return new ModelAndView("pronostico", "message", "simulación ya corriendo");
        }
        
        //GeneraArchivosPronostico.setCheck(id);
        RouteCalculatorPronostico.setServletContext(servletContext);
        PronosticaFallo.setServletContext(servletContext);
        PronosticaFallo pronostico=new PronosticaFallo();
        
        try {
            PronosticaFallo.calculaFallo();
        } catch (Exception ex) {
            Logger.getLogger(PronosticoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("pronostico", "message", "se inicio la simulacion");
    }
    
    
    @RequestMapping("/actualizaPosRutaPronostico")
    public ModelAndView actualizaPosRutaPronostico() {
        GestorVehiculos gestorV = RouteCalculatorPronostico.getGestorVehiculos();
        String textoJSON = gestorV.getJson(null,null,RouteCalculatorPronostico.getReloj(),RouteCalculatorPronostico.getSetOrd());
        
        return new ModelAndView("ruta", "textoJSON", textoJSON);//el primer parametro es el nombre de la vista
    }
    
}
