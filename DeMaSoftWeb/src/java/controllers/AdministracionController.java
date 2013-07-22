/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;
import models.*;
import org.json.simple. *;
/**
 *
 * @author Atenas
 */
@Controller 
public class AdministracionController { 

@RequestMapping("/JSONHistorico")
    public ModelAndView buscaHistoricoSimulaciones(@RequestParam("fecha") String fecha) {
     ArrayList<Incidente> incidentes = new ArrayList<>();
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       Date fechaInc=new Date();
       
         
         
          try{
                 fechaInc = sdf.parse(fecha);
        }catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (fecha != null) {
                incidentes = OrdenEntrega.SeleccionarIncidenciaporFecha(fechaInc);
           
            }

        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss a");
        JSONObject obj = new JSONObject();
        JSONArray lista = new JSONArray();
        for (int i = 0; i < incidentes.size(); i++) {
            JSONObject o = new JSONObject();
            o.put("idIncidente", incidentes.get(i).getIdIncidente());
            o.put("idVehiculo", incidentes.get(i).getIdVehiculo());
            o.put("fecha", sdfFecha.format(incidentes.get(i).getFechaInc()));
            o.put("hora", sdfHora.format(incidentes.get(i).getFechaInc()));
            lista.add(o);
        }

        obj.put("listaIncidente", lista);

        
  

        return new ModelAndView("ruta", "textoJSON", obj.toJSONString());
    }

@RequestMapping("/JSONPaquetes")
    public ModelAndView buscaPaquetes(@RequestParam("fecha") String fecha) {
     ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       Date fechaInc=new Date();
        try{
                 fechaInc = sdf.parse(fecha);
        }catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (fecha != null) {
                ordenes = OrdenEntrega.SeleccionarNumPaquetesporFecha(fechaInc);
           
            }

        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss a");
        JSONObject obj = new JSONObject();
        JSONArray lista = new JSONArray();
        for (int i = 0; i < ordenes.size(); i++) {
            JSONObject o = new JSONObject();
            o.put("idPronostico", ordenes.get(i).getIdPronostico());
            o.put("fecha", sdfFecha.format(ordenes.get(i).getFechaPronostico()));
            o.put("numPaquetes", ordenes.get(i).getNumPaquetesExtra());
            lista.add(o);
        }

        obj.put("listaPaquetes", lista);

        return new ModelAndView("ruta", "textoJSON", obj.toJSONString());
    }


@RequestMapping("/administracion")//declarar que este metodo respondera a /hello    
    public ModelAndView helloWorld() {
        String message = "Hello World, Spring 3.0!";
        return new ModelAndView("administracion", "message", message);//el primer parametro es el nombre de la vista
}


@RequestMapping("/buscaPaquetes")//declarar que este metodo respondera a /hello    
    public ModelAndView buscaPaquetes() {
    
      ArrayList<OrdenEntrega> ordenes2 = new ArrayList<>();
    try {
            ordenes2 = OrdenEntrega.SeleccionarFechasPronostico();
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("buscaPaquetes","ordenes2",ordenes2);//el primer parametro es el nombre de la vista
}

@RequestMapping("/buscaHistorico")//declarar que este metodo respondera a /hello    
    public ModelAndView buscaPedidos() {
    
      ArrayList<Incidente> ordenes2 = new ArrayList<>();
    try {
            ordenes2 = OrdenEntrega.SeleccionarFechasIncidencias();
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("buscaHistorico","ordenes2",ordenes2);//el primer parametro es el nombre de la vista
}
@RequestMapping(value = "/pedido", method = RequestMethod.POST)
    public  String pedido(@ModelAttribute("ordendeentrega")
                            OrdenEntrega ordenEntrega, BindingResult result) {
         
        System.out.println("First Name:" + ordenEntrega.getIdOrden()+
                    "Last Name:" + ordenEntrega.getHoraMaxPedido()+ ordenEntrega.getEstado());
      
        
        return "redirect:buscaPedido.html";
    }

}