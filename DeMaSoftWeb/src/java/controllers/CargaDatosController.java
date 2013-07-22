/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author Adrian
 */


import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;
import DeMaSoft.RouteCalculator;
import DeMaSoft.GestorVehiculos;
import DeMaSoft.Filtro;
import DeMaSoft.ManejaDatos;
import javax.servlet.ServletContext;
import models.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class CargaDatosController {
    private @Autowired
    ServletContext servletContext;
          
    @RequestMapping("/cargaDatos")
    public ModelAndView cargaDatos() {
        
        
        
        ModelAndView modelview= new ModelAndView("cargaDatos");
        modelview.addObject("error", "");
        
        return modelview;
    }
    
    @RequestMapping("/iniciaCargaDatos")
    public ModelAndView iniciaCargaDatos(@RequestParam("prefijo") String prefijo,@RequestParam("fecha") String fecha) {
        RouteCalculator.getInstancia().setServletContext(servletContext);
        ModelAndView modelview= new ModelAndView("cargaDatos");
        
        String error= ManejaDatos.cargasDatos(prefijo,Integer.parseInt(fecha.substring(8)), Integer.parseInt(fecha.substring(5,7)), Integer.parseInt(fecha.substring(0,4)));
        
        modelview.addObject("error", error);
        
        return modelview;
    }
    
    
}
