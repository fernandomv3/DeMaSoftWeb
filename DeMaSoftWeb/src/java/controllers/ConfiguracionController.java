/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;
import DeMaSoft.RouteCalculator;
import DeMaSoft.GestorVehiculos;
import DeMaSoft.Filtro;
import javax.servlet.ServletContext;
import models.Almacen;
import models.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author Adrian
*/
@Controller
public class ConfiguracionController {
    
    private @Autowired
    ServletContext servletContext;
    
    
    @RequestMapping("/configuraParametros")
    public ModelAndView configuraParametros() {
        RouteCalculator.getInstancia().detenerSimulacion();
        /*if (RouteCalculator.isEnEjecucion()) {
            return new ModelAndView("configuraParametros", "message", "La simulación esta corriendo. Detenga la simulación para cambiar los parametros");
        }*/
        RouteCalculator.getInstancia().inicializaValores();
                                
        GestorVehiculos gestorVehiculo= new GestorVehiculos(RouteCalculator.reloj);
        float vCarros=GestorVehiculos.velocidadCarros;
        float costoCarros=GestorVehiculos.costoKMCarros;        
        float vMotos=GestorVehiculos.velocidadMotos;
        float costoMotos=GestorVehiculos.costoKMMotos;
        int numMotos=GestorVehiculos.numMotos;
        int numAutos=GestorVehiculos.numAutos;
        int almacenX=RouteCalculator.posXAlmacen;
        int almacenY=RouteCalculator.posYAlmacen;        
        int dia=RouteCalculator.getDiaInicio();
        int mes=RouteCalculator.getMesInicio() ;
        int anho=RouteCalculator.getAnhoInicio();
        int diaFin=RouteCalculator.getDiaFin();
        int mesFin=RouteCalculator.getMesFin() ;
        int anhoFin=RouteCalculator.getAnhoFin();
        String prefijo=RouteCalculator.prefijo;
        
        ModelAndView modelview= new ModelAndView("configuraParametros");
        modelview.addObject("vCarros", vCarros);
        modelview.addObject("costoCarros", costoCarros);
        modelview.addObject("vMotos", vMotos);
        modelview.addObject("costoMotos", costoMotos);
        modelview.addObject("numMotos", numMotos);
        modelview.addObject("numAutos", numAutos);
        modelview.addObject("almacenX", almacenX);
        modelview.addObject("almacenY", almacenY);
        modelview.addObject("dia", ((dia<10)?"0":"") + dia);
        modelview.addObject("mes", ((mes<10)?"0":"") + mes);
        modelview.addObject("anho", anho);
        modelview.addObject("diaFin", ((diaFin<10)?"0":"") + diaFin);
        modelview.addObject("mesFin", ((mesFin<10)?"0":"") + mesFin);
        modelview.addObject("anhoFin", anhoFin);
        modelview.addObject("prefijo", prefijo);
        
        return modelview;
    }
    
    
    @RequestMapping("/cambiarParametros")
    public ModelAndView cambiarParametros(@RequestParam("velAuto") float velAuto,@RequestParam("costoCarros") float costoCarro,@RequestParam("velMoto") float velMoto,@RequestParam("costoMotos") float costoMoto,@RequestParam("numMotos") int numMoto,@RequestParam("numAutos") int numAuto,
                                          @RequestParam("almacenX")  int almaceX, @RequestParam("almacenY")  int almaceY, @RequestParam("fechaInicio") String fechaInicio, @RequestParam("prefijo")String prefij,
                                            @RequestParam("fechaFin") String fechaFin) {
        
        RouteCalculator.getInstancia().detenerSimulacion();
        /*if (RouteCalculator.isEnEjecucion()) {
            return new ModelAndView("configuraParametros", "message", "La simulación esta corriendo. Detenga la simulación para cambiar los parametros");
        }*/
        
        
        int diaInicio=Integer.parseInt(fechaInicio.substring(8));
        int mesInicio=Integer.parseInt(fechaInicio.substring(5,7));
        int anhoInicio=Integer.parseInt(fechaInicio.substring(0,4));
        int diaF=Integer.parseInt(fechaFin.substring(8));
        int mesF=Integer.parseInt(fechaFin.substring(5,7));
        int anhoF=Integer.parseInt(fechaFin.substring(0,4));
        GestorVehiculos.velocidadCarros=velAuto;
        GestorVehiculos.costoKMCarros =costoCarro;
        GestorVehiculos.velocidadMotos=velMoto;
        GestorVehiculos.costoKMMotos =costoMoto;
        GestorVehiculos.numAutos= numAuto;
        GestorVehiculos.numMotos= numMoto;
        RouteCalculator.posXAlmacen= almaceX;
        RouteCalculator.posYAlmacen= almaceY;
        RouteCalculator.setDiaInicio(diaInicio);
        RouteCalculator.setMesInicio(mesInicio);
        RouteCalculator.setAnhoInicio(anhoInicio);
        RouteCalculator.setDiaFin(diaF);
        RouteCalculator.setMesFin(mesF);
        RouteCalculator.setAnhoFin(anhoF);
        RouteCalculator.prefijo= prefij;
        
        RouteCalculator routeCalculator=RouteCalculator.getInstancia();
        int codigoSim=0;
        
        if (routeCalculator.getCodSimulacion()==-1){
            
            codigoSim=routeCalculator.insertaNuevaSimulacion();
            routeCalculator.setCodSimulacion(codigoSim);
        }else{
            
            codigoSim=routeCalculator.getCodSimulacion();
        }
        
        GestorVehiculos.grabaParametros(codigoSim);        
        routeCalculator.setServletContext(servletContext);
        routeCalculator.grabaAlamacen();
        routeCalculator.grabaParametros();
                        
        float vCarros=GestorVehiculos.velocidadCarros;
        float costoCarros=GestorVehiculos.costoKMCarros;        
        float vMotos=GestorVehiculos.velocidadMotos;
        float costoMotos=GestorVehiculos.costoKMMotos;
        int numMotos=GestorVehiculos.numMotos;
        int numAutos=GestorVehiculos.numAutos;
        int almacenX=RouteCalculator.posXAlmacen;
        int almacenY=RouteCalculator.posYAlmacen;
        int dia=RouteCalculator.getDiaInicio();
        int mes=RouteCalculator.getMesInicio() ;
        int anho=RouteCalculator.getAnhoInicio();
        int diaFin=RouteCalculator.getDiaFin();
        int mesFin=RouteCalculator.getMesFin() ;
        int anhoFin=RouteCalculator.getAnhoFin();
        String prefijo=RouteCalculator.prefijo;
        
        ModelAndView modelview= new ModelAndView("configuraParametros");
        modelview.addObject("vCarros", vCarros);
        modelview.addObject("costoCarros", costoCarros);
        modelview.addObject("vMotos", vMotos);
        modelview.addObject("costoMotos", costoMotos);
        modelview.addObject("numMotos", numMotos);
        modelview.addObject("numAutos", numAutos);
        modelview.addObject("almacenX", almacenX);
        modelview.addObject("almacenY", almacenY);
        modelview.addObject("dia", ((dia<10)?"0":"") + dia);
        modelview.addObject("mes", ((mes<10)?"0":"") + mes);
        modelview.addObject("anho", anho);
        modelview.addObject("diaFin", ((diaFin<10)?"0":"") + diaFin);
        modelview.addObject("mesFin", ((mesFin<10)?"0":"") + mesFin);
        modelview.addObject("anhoFin", anhoFin);
        modelview.addObject("prefijo", prefijo);
        return modelview;
    }
    
}
