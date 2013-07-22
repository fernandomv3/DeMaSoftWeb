/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;
import models.*;
import org.json.simple.*;
import DeMaSoft.Reporte;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 *
 * @author Atenas
 */
@Controller
public class ReportesController {

    @RequestMapping("/descargarPDF")
    public ResponseEntity<byte[]>  pdf ( ) throws IOException{
        Path path = Paths.get("..\\Reporte.pdf");
        byte[] contents = Files.readAllBytes(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "Reporte.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
        return response;
    }
    
    @RequestMapping("/ordenesEntrega")//declarar que este metodo respondera a /hello    
    public ModelAndView reporteOrdenEntrega() {
        ArrayList<OrdenEntrega> ordenes2 = new ArrayList<>();

        try {
            ordenes2 = OrdenEntrega.SeleccionarSimulaciones();
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("ordenesEntrega", "ordenes2", ordenes2);//el primer parametro es el nombre de la vista
    }

    @RequestMapping("/JSONPed")
    public ModelAndView buscaFecha(@RequestParam("fechaSimul") String fechaSimul) {
        ArrayList<OrdenEntrega> ordenes = new ArrayList<>();

        try {
            if (fechaSimul != null) {
                ordenes = OrdenEntrega.SeleccionarIdSimulaciones(fechaSimul);
           
            }

        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
   
        JSONObject obj = new JSONObject();
        JSONArray lista = new JSONArray();
        for (int i = 0; i < ordenes.size(); i++) {
            JSONObject o = new JSONObject();
            o.put("idSimulacion", ordenes.get(i).getIdSimulacion());
            lista.add(o);
        }

        obj.put("listaPedido", lista);

        return new ModelAndView("ruta", "textoJSON", obj.toJSONString());
    }
    @RequestMapping("/JSONPedido")
    public ModelAndView JSONPedido(@RequestParam("idPedido") Integer idPedido, @RequestParam("idEstado") String idEstado,  @RequestParam("fechaIni") String fechaIni,  @RequestParam("fechaFin") String fechaFin) {
        ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fini = new Date();
        Date ffin = new Date();
        
        try{
                  fini = sdf.parse(fechaIni);
                  ffin = sdf.parse(fechaFin);
        
        }catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
          if (idPedido == null) 
            ordenes = OrdenEntrega.SeleccionarTotalOrdenesDeEntrega();
          else 
            ordenes = OrdenEntrega.SeleccionarOrdenEntrega(idPedido, idEstado,fini,ffin);
            
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss a");
        JSONObject obj = new JSONObject();
        JSONArray lista = new JSONArray();
        for (int i = 0; i < ordenes.size(); i++) {
            JSONObject o = new JSONObject();
            //o.put("idCliente", ordenes.get(i).getCliente().getIdPersona());
            o.put("idOrden", ordenes.get(i).getIdOrden());
            o.put("fechaPedido", sdfFecha.format(ordenes.get(i).getFechaPedido()));
            o.put("horaPedido", sdfHora.format(ordenes.get(i).getFechaPedido()));
            o.put("fechaEntrega", sdfFecha.format(ordenes.get(i).getFechaEntrega()));
            o.put("horaEntrega", sdfHora.format(ordenes.get(i).getFechaEntrega()));
            o.put("numPaquetes", ordenes.get(i).getNumPaquetes());
            o.put("estado", ordenes.get(i).getEstado());
            lista.add(o);
        }

        obj.put("listaHistoriaOrdenes", lista);
                
        ArrayList<String> listaCabecera = new ArrayList<>();
        listaCabecera.add("Código de Pedido");
        listaCabecera.add("Fecha de Pedido");
        listaCabecera.add("Hora del Pedido");
        listaCabecera.add("Fecha de Recibo");
        listaCabecera.add("Hora de Recibo");
        listaCabecera.add("Num. Paquetes");
        listaCabecera.add("Estado");
                
        ArrayList<String> listaAux = new ArrayList<>();
        listaAux.add("idOrden");
        listaAux.add("fechaPedido");
        listaAux.add("horaPedido");
        listaAux.add("fechaEntrega");
        listaAux.add("horaEntrega");
        listaAux.add("numPaquetes"); 
        listaAux.add("estado");
        
        String titulo = "Reporte de Ordenes de Entrega";
        
        Reporte rp = new Reporte("..\\Reporte.pdf", listaCabecera, lista, listaAux, titulo, fini, ffin);
                
        return new ModelAndView("ruta", "textoJSON", obj.toJSONString());
    }

    @RequestMapping("/reporteAsistencia")
    public ModelAndView reporteAsistencia() {
        ArrayList<OrdenEntrega> ordenes2 = new ArrayList<>();

        try {
            ordenes2 = OrdenEntrega.SeleccionarSimulaciones();
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("reporteAsistencia", "ordenes2", ordenes2);
    }

    @RequestMapping("/JSONAsistencia")
    public ModelAndView JSONAsistencia(@RequestParam("idChofer") Integer idChofer, @RequestParam("fechaIni") String fechaIni, @RequestParam("fechaFin") String fechaFin) {
        ArrayList<Asistencia> asistencias = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fini = new Date();
        Date ffin = new Date();
        try {
            fini = sdf.parse(fechaIni);
            ffin = sdf.parse(fechaFin);
        } catch (ParseException ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        try {
            if (idChofer ==  null)
                asistencias = Asistencia.SeleccionarTotalAsistencias();
            else
                asistencias = Asistencia.SeleccionarAsistencia(idChofer, fini, ffin);
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss a");
        JSONObject obj = new JSONObject();
        JSONArray lista = new JSONArray();
        for (int i = 0; i < asistencias.size(); i++) {
            JSONObject o = new JSONObject();
            //o.put("idCliente", ordenes.get(i).getCliente().getIdPersona());
            o.put("idChofer", asistencias.get(i).getIdChofer());
            o.put("fechaEntrada", sdfFecha.format(asistencias.get(i).getFechaEntrada()));
            o.put("horaEntrada", sdfHora.format(asistencias.get(i).getFechaEntrada()));
            o.put("fechaSalida", sdfFecha.format(asistencias.get(i).getFechaSalida()));
            o.put("horaSalida", sdfHora.format(asistencias.get(i).getFechaSalida()));
            lista.add(o);
        }

        obj.put("listaHistoriaAsistencia", lista);
        
        ArrayList<String> listaCabecera = new ArrayList<>();
        listaCabecera.add("ID del Chofer");
        listaCabecera.add("Fecha de Entrada");
        listaCabecera.add("Hora de Entrada");
        listaCabecera.add("Fecha de Salida");
        listaCabecera.add("Hora de Salida");
        
        ArrayList<String> listaAux = new ArrayList<>();
        listaAux.add("idChofer");
        listaAux.add("fechaEntrada");    
        listaAux.add("horaEntrada");
        listaAux.add("fechaSalida");
        listaAux.add("horaSalida");
        
        String titulo = "Reporte de Asistencias";
        
        Reporte rp = new Reporte("..\\Reporte.pdf", listaCabecera, lista, listaAux, titulo, fini, ffin);
        
        return new ModelAndView("ruta", "textoJSON", obj.toJSONString());
    }
    
    @RequestMapping("/historialEnvios")
    public ModelAndView historialdeEnviosPorCliente() {
        ArrayList<OrdenEntrega> ordenes2 = new ArrayList<>();

        try {
            ordenes2 = OrdenEntrega.SeleccionarSimulaciones();
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("historialEnvios", "ordenes2", ordenes2);
    }

    @RequestMapping("/JSONEnvios")
    public ModelAndView JSONEnvios(@RequestParam("idCliente") Integer idCliente, @RequestParam("fechaIni") String fechaIni, @RequestParam("fechaFin") String fechaFin) {
        ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fini = new Date();
        Date ffin = new Date();
        try {
            fini = sdf.parse(fechaIni);
            ffin = sdf.parse(fechaFin);
        } catch (ParseException ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (idCliente == null)
                ordenes = OrdenEntrega.SeleccionarTotalOrdenes();
            else
                ordenes = OrdenEntrega.SeleccionarOrden(idCliente, fini, ffin);
        } catch (Exception ex) {
            Logger.getLogger(ReportesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss a");
        JSONObject obj = new JSONObject();
        JSONArray lista = new JSONArray();
        for (int i = 0; i < ordenes.size(); i++) {
            JSONObject o = new JSONObject();
            //o.put("idCliente", ordenes.get(i).getCliente().getIdPersona());
            o.put("idOrden", ordenes.get(i).getIdOrden());
            o.put("fechaPedido", sdfFecha.format(ordenes.get(i).getFechaPedido()));
            o.put("horaPedido", sdfHora.format(ordenes.get(i).getFechaPedido()));
            o.put("fechaEntrega", sdfFecha.format(ordenes.get(i).getFechaEntrega()));
            o.put("horaEntrega", sdfHora.format(ordenes.get(i).getFechaEntrega()));
            lista.add(o);
        }

        obj.put("listaHistoriaCliente", lista);
        
        ArrayList<String> listaCabecera = new ArrayList<>();
        listaCabecera.add("ID de la Orden");
        listaCabecera.add("Fecha del Pedido");
        listaCabecera.add("Hora del Pedido");
        listaCabecera.add("Fecha de Entrega");
        listaCabecera.add("Hora de Entrega");
        
        ArrayList<String> listaAux = new ArrayList<>();
        listaAux.add("idOrden");
        listaAux.add("fechaPedido");    
        listaAux.add("horaPedido");
        listaAux.add("fechaEntrega");
        listaAux.add("horaEntrega");
        
        String titulo = "Reporte de Envíos";
        
        Reporte rp = new Reporte("..\\Reporte.pdf", listaCabecera, lista, listaAux, titulo, fini, ffin);
        
        return new ModelAndView("ruta", "textoJSON", obj.toJSONString());
    }
}
