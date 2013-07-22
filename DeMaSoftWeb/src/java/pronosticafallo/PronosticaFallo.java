/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pronosticafallo;

import DeMaSoft.GestorConexion;
import DeMaSoft.GestorVehiculos;
import java.util.ArrayList;
import java.util.HashMap;
import DeMaSoft.ManejaDatos;
import DeMaSoft.Reloj;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.servlet.ServletContext;
import models.Mapa;
import models.OrdenEntrega;


/**
 *
 * @author Adrian
 */
public class PronosticaFallo{

    /**
     * @param args the command line arguments
     */
    
    private static ArrayList<DiaPronostico> datosPronostico=null;
    private static ServletContext servletContext;
    private static Reloj _reloj= new Reloj();
    private static RouteCalculatorPronostico routeCalculatorPronostico= new RouteCalculatorPronostico();
    private static int idPronostico;
    
    
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        datosPronostico=null;    
        _reloj= new Reloj(); 
        setRouteCalculatorPronostico(new RouteCalculatorPronostico());
        getRouteCalculatorPronostico().inicializaValores();
        ManejaDatos m= new ManejaDatos();        
        _reloj.detenerReloj();
        DiaPronostico.setNumTurnos(3);
        _reloj.setReloj(0, 0, 17, 5, 2012);
        int diaInicio=369;
        //_reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, diaInicio);
        getRouteCalculatorPronostico().setReloj(_reloj);
        insertaPronostico();
        getRouteCalculatorPronostico().setIdPronostico(idPronostico);
        try{
            
            datosPronostico= m.sacaDatosPronostico("dat", datosPronostico,_reloj,servletContext);
            procesaDatos();
            
            GeneraArchivosPronostico generador= new GeneraArchivosPronostico();
            /*ArrayList<OrdenEntrega> ordenes;            
            _reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, 372);
            for(int i=1;i<28;i++){
                _reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, 1);
                ordenes=generador.nuevoGeneraArchivo(372,_reloj, new Mapa(150,100));
            }*/
            
            /*
            System.out.println(ordenes.size());*/
            /*
            Mapa mapa = new Mapa(150, 100);
            generador.nuevoGeneraArchivo(diaInicio, _reloj, mapa);*/
            GestorVehiculos gestorV= new GestorVehiculos(_reloj);                        
            
            getRouteCalculatorPronostico().setGestorVehiculos(gestorV);
            _reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, diaInicio);
            getRouteCalculatorPronostico().calculaPronostico(diaInicio);
            
            getRouteCalculatorPronostico().correrDibujaPronostico();
            
            setRouteCalculatorPronostico(null);
        }catch(Exception e){
            
            throw e;
            
        }
        
    }
    
    
    public static void calculaFallo() throws Exception{
        
        
        datosPronostico=null;    
        _reloj= new Reloj();                        
        setRouteCalculatorPronostico(new RouteCalculatorPronostico());
        getRouteCalculatorPronostico().inicializaValores();
        ManejaDatos m= new ManejaDatos();        
        _reloj.detenerReloj();
        DiaPronostico.setNumTurnos(3);
        _reloj.setReloj(0, 0, 17, 5, 2012);
        int diaInicio=369;
        //_reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, diaInicio);
        getRouteCalculatorPronostico().setReloj(_reloj);
        insertaPronostico();
        getRouteCalculatorPronostico().setIdPronostico(idPronostico);
        try{
        
            datosPronostico= m.sacaDatosPronostico("dat", datosPronostico,_reloj,servletContext);
            procesaDatos();
            
            GeneraArchivosPronostico generador= new GeneraArchivosPronostico();            
            /*ArrayList<OrdenEntrega> ordenes=generador.generaArchivo(1,_reloj, new Mapa());
            
            System.out.println(ordenes.size());*/
            /*
            Mapa mapa = new Mapa(150, 100);
            generador.nuevoGeneraArchivo(diaInicio, _reloj, mapa);*/
            GestorVehiculos gestorV= new GestorVehiculos(_reloj);    
            
            
            getRouteCalculatorPronostico().setGestorVehiculos(gestorV);
            _reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, diaInicio);
            getRouteCalculatorPronostico().calculaPronostico(diaInicio);
            
            getRouteCalculatorPronostico().correrDibujaPronostico();
            setRouteCalculatorPronostico(null);
        
        }catch(Exception e){
            
            throw e;
            
        }
    }
    
    public static double sacaExponencial(int paq, int numDia, int turno){
        double n=0;
        int resta=0;
        
        if (turno==1)resta=41;
        if (turno==2)resta=65;
        if (turno==3)resta=63;
        
        
        n= (Math.log10(paq-resta)/Math.log10(numDia+2));
        
        if (resta>=paq) n=0;
        
        return n;
    }
    
    public static void procesaDatos(){
        
        int numDias=datosPronostico.size();
        ArrayList<Double> factordeCuartos= new ArrayList<>();
        ArrayList<HashMap> factordePrioridad= new ArrayList<>();
        for (int i=0; i<12;i++){
            
            factordeCuartos.add(0.0);
            factordePrioridad.add(new HashMap());
        }
        
        ArrayList<Integer> numpedidosturno1=new ArrayList<>();
        ArrayList<Integer> numpedidosturno2=new ArrayList<>();
        ArrayList<Integer> numpedidosturno3=new ArrayList<>();
        
        double sumN1=0;
        double sumN2=0;
        double sumN3=0;
        
        for (int i=0; i<numDias;i++){
            datosPronostico.get(i).procesaDatos();
            int paq1=datosPronostico.get(i).getDatoTurnos().get(0).getNumpaquetesTurno();
            int paq2=datosPronostico.get(i).getDatoTurnos().get(1).getNumpaquetesTurno();
            int paq3=datosPronostico.get(i).getDatoTurnos().get(2).getNumpaquetesTurno();
            
            
            double n1=sacaExponencial(paq1,i,1);
            double n2=sacaExponencial(paq2,i,2);
            double n3=sacaExponencial(paq3,i,3);
            
            sumN1+=n1;
            sumN2+=n2;
            sumN3+=n3;
            
            int itn=0;
            
                for(int j=0;j<12;j++){
                    HashMap prioridad= factordePrioridad.get(j);
                    factordeCuartos.set(j,factordeCuartos.get(j) + datosPronostico.get(i).getFactordeTurnos().get(j) );
                    HashMap prioridadCuarto=datosPronostico.get(i).getFactorPrioridadTurnos().get(j);
                    
                    for(int prio=0;prio<=24;prio++){
                        
                        if (prioridadCuarto.containsKey(String.valueOf(prio)) ){
                            
                            if (prioridad.containsKey(prio)){
                                
                                prioridad.put(prio,(int)(prioridadCuarto.get(String.valueOf(prio))) + (int)(prioridad.get(prio) ));
                                
                            }else{
                                
                                
                                prioridad.put(prio,prioridadCuarto.get(String.valueOf(prio)) );
                            }
                            
                            
                        }
                        
                        
                    }
                    
                    factordePrioridad.set(j,prioridad);
                    
                }
                
                //numpedidos x turno
                numpedidosturno1.add((datosPronostico.get(i).getDatoTurnos().get(0).getNumpaquetesTurno()));
                numpedidosturno2.add((datosPronostico.get(i).getDatoTurnos().get(1).getNumpaquetesTurno()));
                numpedidosturno3.add((datosPronostico.get(i).getDatoTurnos().get(2).getNumpaquetesTurno()));
                
                

        }
        
        
        sumN1=sumN1/numDias;
        sumN2=sumN2/numDias;
        sumN3=sumN3/numDias;
                        
        ArrayList<Double> exponenciales= new ArrayList<>();
        exponenciales.add(sumN1);
        exponenciales.add(sumN2);
        exponenciales.add(sumN3);
        
        System.out.println(sumN1);
        System.out.println(sumN2);
        System.out.println(sumN3);
        
        TurnoPronostico.setExponenciales(exponenciales);
        
        for (int i=0; i<12;i++){
            factordeCuartos.set(i,factordeCuartos.get(i)/numDias);
            
            for(int j=0; j<=24;j++){
                HashMap hm=factordePrioridad.get(i);
                
                if (hm.containsKey(j))
                    factordePrioridad.get(i).put(j,(double)(int)(factordePrioridad.get(i).get(j))/numDias );
                
                
            }
            
        }
        CuartodeTurnoPronostico.setFactorTamaño(factordeCuartos);
        CuartodeTurnoPronostico.setFactorPrioridad(factordePrioridad);
        for (int i=0; i<12;i++) System.out.println(factordeCuartos.get(i));
        
        for (int i=0; i<12;i++)
            for(int j=0;j<=24;j++)
                if(factordePrioridad.get(i).containsKey(j))
                    System.out.println(i+": "+ j + ":" + (double)(factordePrioridad.get(i).get(j)));
        
        
        ArrayList<Integer> dias= new ArrayList<>();
        
        for (int i=0;i<numDias;i++) dias.add(i+1);
        
        RegresionExponencial regresion= new RegresionExponencial();
        
        //System.out.println(numpedidosturno1.size());
        //System.out.println(numpedidosturno2.size());
        //System.out.println(numpedidosturno3.size());
        
        regresion.regresionExponencial(dias, numpedidosturno1);
        
        //System.out.println( regresion.getConstante() +" " + regresion.getExponente() );
        
        regresion.regresionExponencial(dias, numpedidosturno2);
        
        //System.out.println( regresion.getConstante() +" " + regresion.getExponente() );
        regresion.regresionExponencial(dias, numpedidosturno3);
        
        //System.out.println( regresion.getConstante() +" " + regresion.getExponente() );
        
        PolynomialFitter2 pf1= new PolynomialFitter2(2);        
        for (int i=1;i<numpedidosturno1.size();i++){
            
            pf1.addPoint(i,numpedidosturno1.get(i));
            
        }
        
        ArrayList<Double>regresionTurno1 =  pf1.getBestFit();
        
        //System.out.println(regresionTurno1.get(2) +" "+regresionTurno1.get(1) +" "+regresionTurno1.get(0));
                
        PolynomialFitter2 pf2= new PolynomialFitter2(2);        
        for (int i=1;i<numpedidosturno2.size();i++){
            
            pf2.addPoint(i,numpedidosturno2.get(i));
            
        }
        
        ArrayList<Double>regresionTurno2 =  pf2.getBestFit();
        
        //System.out.println(regresionTurno2.get(2) +" "+regresionTurno2.get(1) +" "+regresionTurno2.get(0));
        
        PolynomialFitter2 pf3= new PolynomialFitter2(2);        
        for (int i=1;i<numpedidosturno3.size();i++){
            
            pf3.addPoint(i,numpedidosturno3.get(i));
            
        }
        
        ArrayList<Double>regresionTurno3 =  pf3.getBestFit();
        //System.out.println(regresionTurno3.get(2) +" "+regresionTurno3.get(1) +" "+regresionTurno3.get(0));
        
        ArrayList<ArrayList<Double>> turno= new ArrayList<>();
        
        turno.add(regresionTurno1);
        turno.add(regresionTurno2);
        turno.add(regresionTurno3);
        
        TurnoPronostico.setPolinomioPaquetes(turno);
        
    }

    /**
     * @return the servletContext
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @param servletContext the servletContext to set
     */
    public static void setServletContext(ServletContext servletContext1) {
        servletContext = servletContext1;
    }
    
    public static int getMaxIdPronostico(){
        int id=0;
        PreparedStatement pstmt= null;
        ResultSet rset=null;
        StringBuffer query=new StringBuffer();
        
        query.append(" SELECT IFNULL(MAX(idPRONOSTICO),0) FROM PRONOSTICO ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt=con.prepareStatement(query.toString());
            
            rset=pstmt.executeQuery();
            
            if(rset.next()){
                
                id=rset.getInt(1);
            }
            
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }finally{
            
            try{
            
                if (pstmt!=null)pstmt.close();
                if (rset!=null)rset.close();
            }catch(Exception e){

                System.out.println(e.getMessage());
            }
            
        }
        
        return id;
    }
    
    public static void insertaPronostico(){
        
        idPronostico=getMaxIdPronostico() + 1;
        
        PreparedStatement pstmt= null;
        ResultSet rset=null;
        StringBuffer query=new StringBuffer();
        
        query.append(" INSERT INTO PRONOSTICO (`idPRONOSTICO`,`FECHAPRONOSTICO`) ");
        query.append(" VALUES(?,sysdate()) ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt=con.prepareStatement(query.toString());
            pstmt.setInt(1, idPronostico);
            pstmt.executeUpdate();
                        
            
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }finally{
            
            try{
            
                if (pstmt!=null)pstmt.close();
               
            }catch(Exception e){

                System.out.println(e.getMessage());
            }
            
        }
        
    }
    
    public static void actualizaFechaPronostico(int idProno, String fechaColapso){
        
        idPronostico=getMaxIdPronostico() + 1;
        
        PreparedStatement pstmt= null;
        ResultSet rset=null;
        StringBuffer query=new StringBuffer();
        
        query.append(" UPDATE PRONOSTICO SET ");
        query.append(" FECHACOLAPSO = ? WHERE idPRONOSTICO=? ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1, fechaColapso);
            pstmt.setInt(2, idProno);
            
            pstmt.executeUpdate();
                        
            
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }finally{
            
            try{
            
                if (pstmt!=null)pstmt.close();
               
            }catch(Exception e){

                System.out.println(e.getMessage());
            }
            
        }
        
    }
    
    
    public static void insertaPaquetesxdiaPronostico(int idProno, String fechaColapso, int numpaquetes){
        
        idPronostico=getMaxIdPronostico() + 1;
        
        PreparedStatement pstmt= null;
        ResultSet rset=null;
        StringBuffer query=new StringBuffer();
        
        query.append(" INSERT INTO PAQUETEXDIAXPRONOSTICO(`idPRONOSTICO`,`Fecha`,`numPaquetes`) ");
        query.append(" VALUES(?,?,?) ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt=con.prepareStatement(query.toString());
            pstmt.setInt(1, idProno);
            pstmt.setString(2, fechaColapso);
            pstmt.setInt(3, numpaquetes);
            pstmt.executeUpdate();
                        
            
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }finally{
            
            try{
            
                if (pstmt!=null)pstmt.close();
               
            }catch(Exception e){

                System.out.println(e.getMessage());
            }
            
        }
        
    }

    /**
     * @return the routeCalculatorPronostico
     */
    public static RouteCalculatorPronostico getRouteCalculatorPronostico() {
        return routeCalculatorPronostico;
    }

    /**
     * @param aRouteCalculatorPronostico the routeCalculatorPronostico to set
     */
    public static void setRouteCalculatorPronostico(RouteCalculatorPronostico aRouteCalculatorPronostico) {
        routeCalculatorPronostico = aRouteCalculatorPronostico;
    }

           
}
