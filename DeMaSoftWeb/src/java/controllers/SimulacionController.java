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
import DeMaSoft.ObjetoFiltro;
import javax.servlet.ServletContext;
import models.Vehicle;
import DeMaSoft.Reloj;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Fernando
 */
@Controller
public class SimulacionController {

    private static ObjetoFiltro objeto = new ObjetoFiltro();
    private @Autowired
    ServletContext servletContext;

    @RequestMapping("/getIniciales")//declarar que este metodo respondera a /cambiaVelocidad    
    public ModelAndView getDatosIniciales() {
        String textoJSON =RouteCalculator.getInstancia().getBloqueosyAlmacen();
        return new ModelAndView("ruta", "textoJSON", textoJSON);
    }
    
    @RequestMapping("/cambiaVelocidad")//declarar que este metodo respondera a /cambiaVelocidad    
    public void cambiaVelocidad(@RequestParam("velocidad") int velocidad) {
        RouteCalculator.reloj.setMinutosXSegundo(velocidad);
    }
    
    @RequestMapping("/cliente")
    public ModelAndView simulacionCliente(@RequestParam(value = "_idCliente") String idCliente) {
        ModelAndView m = new ModelAndView("simulacionCliente");
        m.addObject("_idCliente",idCliente);
        if (RouteCalculator.isEnEjecucion()) {
            m.addObject("message","Planificación ya corriendo");
            return m;
        }

        RouteCalculator.getInstancia().setServletContext(servletContext);
        RouteCalculator.getInstancia().setTipo(0);//Nueva simulación
        RouteCalculator.getInstancia().correrSimulacion();
        m.addObject("message","se inicio la planificación");
        return m;
    }
    @RequestMapping("/chofer")
    public ModelAndView simulacionPedido(@RequestParam(value = "_idChofer") String idChofer) {
        ModelAndView m = new ModelAndView("simulacionChofer");
        m.addObject("_idChofer",idChofer);
        if (RouteCalculator.isEnEjecucion()) {
            m.addObject("message","Planificación ya corriendo");
            return m;
        }

        RouteCalculator.getInstancia().setServletContext(servletContext);
        RouteCalculator.getInstancia().setTipo(0);//Nueva simulación
        RouteCalculator.getInstancia().correrSimulacion();
        m.addObject("message","se inicio la planificación");
        return m;
    }

    @RequestMapping("/simulacion")
    public ModelAndView simulacion() {
        if (RouteCalculator.isEnEjecucion()) {
            return new ModelAndView("simulacion", "message", "Planificación ya corriendo");
        }

        RouteCalculator.getInstancia().setServletContext(servletContext);
        RouteCalculator.getInstancia().setTipo(0);//Nueva simulación
        RouteCalculator.getInstancia().correrSimulacion();
        return new ModelAndView("simulacion", "message", "se inicio la planificación");
    }

    @RequestMapping("/actualizaPosRutaFiltrada")
    public void filtros(
            @RequestParam(value = "numPlaca") String numPlaca,
            @RequestParam(value = "autos") Boolean autos,
            @RequestParam(value = "motos") Boolean motos,
            @RequestParam(value = "ida",required=false) Boolean ida,
            @RequestParam(value = "vuelta",required=false) Boolean vuelta,
            @RequestParam(value = "_idCliente") String _idCliente,
            @RequestParam(value = "_idChofer") String _idChofer,
            @RequestParam(value = "_idPedido",required=false) String _idPedido) {

        objeto.setNumPlaca(numPlaca);
        objeto.setAutos(autos);
        objeto.setMotos(motos);
        objeto.setIda(ida);
        objeto.setVuelta(vuelta);

        if (_idCliente == "") {
            _idCliente = "0";
        }
        objeto.setIdCliente(Integer.parseInt(_idCliente));
        if (_idChofer == "") {
            _idChofer = "0";
        }
        objeto.setIdChofer(Integer.parseInt(_idChofer));

    }

    @RequestMapping("/detener")
    public void detenerSimulacion() {
        RouteCalculator.reloj.detenerReloj();
        RouteCalculator.getInstancia().detenerSimulacion();
        RouteCalculator.gestorVehiculos.detener();

    }

    @RequestMapping("/actualizaPosRuta")
    public ModelAndView actualizaPosRuta(
            @RequestParam(value = "numPlaca") String numPlaca,
            @RequestParam(value = "autos") Boolean autos,
            @RequestParam(value = "motos") Boolean motos,
            @RequestParam(value = "ida",required =false) Boolean ida,
            @RequestParam(value = "vuelta",required =false) Boolean vuelta,
            @RequestParam(value = "_idCliente") String _idCliente,
            @RequestParam(value = "_idChofer") String _idChofer,
            @RequestParam(value = "_idPedido",required=false) String _idPedido) {
        GestorVehiculos gestorV = RouteCalculator.gestorVehiculos;
        ObjetoFiltro params = new ObjetoFiltro();
        params.setNumPlaca(numPlaca);
        params.setAutos(autos);
        params.setMotos(motos);
        params.setIda(ida);
        params.setVuelta(vuelta);
        

        if (_idCliente == "") {
            _idCliente = "0";
        }
        params.setIdCliente(Integer.parseInt(_idCliente));
        if (_idChofer == "") {
            _idChofer = "0";
        }
        params.setIdChofer(Integer.parseInt(_idChofer));
        String textoJSON = gestorV.getJson(new Filtro() {
            public boolean filtroPorChofer(Vehicle v, Object o) {
                Integer _idPersona = (Integer) o;
                if (_idPersona == null || _idPersona==0) {
                    return true;
                }
                if (v.getChofer().getIdPersona() == _idPersona.intValue()) {
                    return true;
                }
                return false;
            }

            public boolean filtroPorCliente(Vehicle v, Object o) {
                Integer _idPersona = (Integer) o;
                if (_idPersona == null || _idPersona==0) {
                    return true;
                }
                for (int i = 0; i < v.getOrdenes().size(); i++) {
                    if (v.getOrdenes().get(i).getCliente().getIdPersona() == _idPersona.intValue()) {
                        return true;
                    }
                }

                return false;

            }

            public boolean filtroPorPlaca(Vehicle v, Object o) {
                String placa = (String) o;
                if (placa == "" || v.getPlaca().compareTo(placa) == 0) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean filtro(Vehicle v, Object o) {
                boolean pasa = true;

                pasa = pasa && filtroPorPlaca(v, ((ObjetoFiltro)o).getNumPlaca());
                pasa = pasa && filtroPorChofer(v, ((ObjetoFiltro)o).getIdChofer());
                pasa = pasa && filtroPorCliente(v, ((ObjetoFiltro) o).getIdCliente());
                return pasa;
            }
        }, params, RouteCalculator.reloj, RouteCalculator.setOrd);
        
        return new ModelAndView("ruta", "textoJSON", textoJSON);//el primer parametro es el nombre de la vista
    }
}
