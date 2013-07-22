/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pronosticafallo;

import DeMaSoft.GestorConexion;
import DeMaSoft.GestorVehiculos;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.OrdenEntrega;
import models.Turno;
import models.Chofer;
import models.Mapa;
import models.Almacen;
import models.Vehicle;
import DeMaSoft.ManejaDatos;
import DeMaSoft.Reloj;
import antcolony.AntColony;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletContext;
import models.Ruta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Adrian
 */
public class RouteCalculatorPronostico implements Runnable {

    public static final int ventana = 10;
    public static Mapa mapa;
    public Almacen almacen;
    public ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
    private static ArrayList<OrdenEntrega> setOrd = new ArrayList<>();
    public static String prefijo = "dat";
    public static ArrayList<Turno> turnos = new ArrayList<>();
    public static ArrayList<Chofer> choferes = new ArrayList<>();
    public ArrayList<ArrayList<Ruta>> rutasDeHoy;
    private ArrayList<ArrayList<OrdenEntrega>> setOrddeHoy;
    public ArrayList<ArrayList<Ruta>> rutasDeAyer;
    private ArrayList<ArrayList<OrdenEntrega>> setOrddeAyer;
    private ArrayList<ArrayList<Integer>> capacidadDeHoy;
    private ArrayList<ArrayList<Integer>> capacidadDeAyer;
    public int _diaFallo;
    public int _mesFallo;
    public int _anhoFallo;
    public int _horaFallo;
    public int _minutoFallo;
    private boolean _detente = false;
    private static boolean _enEjecucion = false;
    private static Reloj _reloj;
    private static ServletContext servletContext;
    private static GestorVehiculos gestorVehiculos;
    private int posXAlmacen=45;
    private int posYAlmacen=30;
    private int idPronostico;
    

    
    public String getBloqueosyAlmacen() {
        JSONObject obj = new JSONObject();
        JSONArray arrayBloqueos = new JSONArray();
        for (int i = 0; i < this.mapa.getBloqueados().size(); i++) {
            JSONObject nodoBloqueado = new JSONObject();
            nodoBloqueado.put("x", this.mapa.getBloqueados().get(i).getPosX());
            nodoBloqueado.put("y", this.mapa.getBloqueados().get(i).getPosY());
            arrayBloqueos.add(nodoBloqueado);
        }
        obj.put("bloqueos", arrayBloqueos);
        JSONObject almacen = new JSONObject();
        almacen.put("x", this.almacen.getUbicacion().getPosX());
        almacen.put("y", this.almacen.getUbicacion().getPosY());
        obj.put("almacen", almacen);
        return obj.toJSONString();
    }
    
    /**
     * @return the setOrd
     */
    public static ArrayList<OrdenEntrega> getSetOrd() {
        return setOrd;
    }

    /**
     * @param aSetOrd the setOrd to set
     */
    public static void setSetOrd(ArrayList<OrdenEntrega> aSetOrd) {
        setOrd = aSetOrd;
    }
    

    /**
     * @return the _reloj
     */
    public static Reloj getReloj() {
        return _reloj;
    }

    /**
     * @param aReloj the _reloj to set
     */
    public static void setReloj(Reloj aReloj) {
        _reloj = aReloj;
    }

    /**
     * @return the _enEjecucion
     */
    public synchronized static boolean isEnEjecucion() {
        return _enEjecucion;
    }

    /**
     * @param aEnEjecucion the _enEjecucion to set
     */
    public synchronized static void setEnEjecucion(boolean aEnEjecucion) {
        _enEjecucion = aEnEjecucion;
    }

    /**
     * @return the _enEjecucion
     */
    public void crearTurnos() {
        Calendar c = (Calendar) _reloj.getFechaActual().clone();
        for (int i = 0; i < 3; i++) {
            Turno turno = new Turno();
            turno.setIdTurno(i);
            turno.setHoraInicio(c.getTime());
            c.add(Calendar.HOUR_OF_DAY, 8);
            turno.setHoraFin(c.getTime());
            turnos.add(turno);
        }
    }

    public void crearChoferes() {
        int numChoferes = getGestorVehiculos().numAutos + getGestorVehiculos().numMotos;
        numChoferes = numChoferes * turnos.size();
        for (int i = 0; i < numChoferes; i++) {
            Chofer chofer = new Chofer();
            chofer.setIdPersona(1001+i);
            chofer.setTurno(turnos.get(i / (getGestorVehiculos().numAutos + getGestorVehiculos().numMotos)));
            chofer.setEstaAlmorzando(false);
            choferes.add(chofer);
        }
    }
    
    
    public void inicializaValores(){
        
        PreparedStatement pstmt =null;
        ResultSet rset=null;
        StringBuffer query= new StringBuffer();
        
        
        query.append(" select PS.Prefijo, A.posX, A.posY from Parametros_Simulacion PS, ALMACEN A ");
        query.append(" where idParametros_Simulacion= (select MAX(idParametros_Simulacion) from Parametros_Simulacion) AND A.IDSIMULACION=PS.IDSIMULACION ");
        
        
        try{
            Connection con = GestorConexion.getConexion();
            
            pstmt=con.prepareStatement(query.toString());
            
            rset=pstmt.executeQuery();
                        
            if (rset.next()){
                prefijo=rset.getString(1);
                if (prefijo==null) prefijo="dat";
                posXAlmacen=rset.getInt(2);
                posYAlmacen=rset.getInt(3);
            }else{
                
                prefijo="dat";
                posXAlmacen=45;
                posYAlmacen=30;
                
            }
            
        }catch(Exception e){
            
            
            prefijo="dat";
            posXAlmacen=45;
            posYAlmacen=30;
            
            System.out.println(e.getMessage());
        }finally{
            try{
                if (pstmt!=null) pstmt.close();
                if (rset!=null) rset.close();
            }catch(Exception e){
                
                System.out.println(e.getMessage());
            }
            
        }
        
        
    }

    public ArrayList<ArrayList<ArrayList<Ruta>>> calculaPronostico(int numdia) {
        Reloj reloj = _reloj;
        AntColony antColony = new AntColony();
        //crear mapa
        mapa = new Mapa(150, 100, servletContext);
        //ubicar el almacen
        almacen = new Almacen(mapa.getPuntos().get(posYAlmacen + posXAlmacen * mapa.getAlto()));// se ubica en 45,30
        //crear el mapa de nodos para el algoritmo
        antColony.createNodeMap(mapa);
        //empezar simulacion
        antColony.initializeArcsPheromoneLevel();
        //iniciar tiempo a 00:00        

        rutasDeHoy = new ArrayList<>();
        rutasDeAyer = new ArrayList<>();
        setOrddeHoy= new ArrayList<>();
        setOrddeAyer= new ArrayList<>();
        capacidadDeHoy=new ArrayList<>();
        capacidadDeAyer=new ArrayList<>();
        Calendar proxVentana = (Calendar) reloj.getFechaActual().clone();
        proxVentana.add(Calendar.MINUTE, ventana);

        Calendar proxDia = (Calendar) reloj.getFechaActual().clone();
        proxDia.add(Calendar.DAY_OF_MONTH, 1);
        reloj.setMinutoActual(ventana);
        GeneraArchivosPronostico generador = new GeneraArchivosPronostico();
        try {
            
            ordenes = generador.nuevoGeneraArchivo(numdia, reloj, mapa, this.idPronostico);
            //if (numdia==1) generador.transformaaArchivo(ordenes);
        } catch (Exception ex) {
            Logger.getLogger(RouteCalculatorPronostico.class.getName()).log(Level.SEVERE, null, ex);
        }

        //setSetOrd(ManejaDatos.sacaSetOrdenes(ordenes, reloj.getHoraActual(), reloj.getMinutoActual(), ventana));
        gestorVehiculos.setVentana(ventana);
        //gestorVehiculos.setSetOrd(setOrd);
        crearTurnos();
        crearChoferes();
        //crear los vehículos(se crean en el almacen)

        GestorVehiculos gestorV = getGestorVehiculos();
        gestorV.setAlmacen(almacen);
        //GestorVehiculosPronostica gestorV=GestorVehiculosPronostica.getInstancia();
        gestorV.setChoferes(choferes);
        gestorV.createVehicles(almacen, choferes,-1);
        //gestorV.asignarRutas(antColony.bestSolutions, setOrd, mapa, almacen);
        ManejaDatos m= new ManejaDatos();
        while (true) {
            if (proxVentana.compareTo(reloj.getFechaActual()) <= 0) {
                proxVentana.add(Calendar.MINUTE, ventana);
                /*System.out.println("Fecha " + reloj.getFechaString("dd-MM-yyyy"));
                System.out.println("Hora: " + reloj.getHoraActual() + ":" + reloj.getMinutoActual());
                System.out.println("Numero pedidos: " + setOrd.size() + " ");
                System.out.print("Tiempo del algoritmo: ");*/
                Date b = new Date();
                antColony.reverseAntColonyProcedure(gestorV.getVehiculos(), setOrd, _reloj, almacen, mapa);
                Date a = new Date();
                //System.out.println(a.getTime() - b.getTime());
                ArrayList<OrdenEntrega> setOrdtemp = m.sacaSetOrdenes2(ordenes, reloj, ventana);
                setOrd.addAll(setOrdtemp);
                gestorVehiculos.setSetOrd(setOrd);
                /*for(int k =0; k < setOrd.size();k++){
                    System.out.println(setOrd.get(k).getFechaPedido()+"-->"+setOrd.get(k).getFechaMaxEntrega());
                }*/
                //if (AntColony.bestSolutions!=null && AntColony.bestSolutions.size()>0)

                gestorV.asignarRutas(antColony.bestSolutions, setOrd, mapa, almacen);
                ArrayList<Ruta> tempRutaVent = new ArrayList<>();
                ArrayList<OrdenEntrega> tempSetOrd= new ArrayList<>();
                ArrayList<Integer> capacidad= new ArrayList<>();
                for (int i=0; i<setOrd.size();i++){
                    
                    tempSetOrd.add(setOrd.get(i).copiar());
                    
                    
                }
                
                int tamV=gestorVehiculos.getVehiculos().size();
                
                
                for (int i=0; i<tamV; i++){
                    
                    capacidad.add(gestorVehiculos.getVehiculos().get(i).getCapacidadActual());
                }
                                               
                for (int i = 0; i < gestorV.getVehiculos().size(); i++) {
                    Vehicle v = gestorV.getVehiculos().get(i);
                    tempRutaVent.add(v.getRecorrido().getRutaActual());
                    /*if (v.getRecorrido().getRutaActual().getPuntos().size() > 1) {
                        //System.out.println("Vehiculo "+i+":");
                        for (int j = 0; j < v.getRecorrido().getRutaActual().getPuntos().size(); j++) {
                            System.out.print("("+v.getRecorrido().getRutaActual().getPuntos().get(j).getPosX()+","+v.getRecorrido().getRutaActual().getPuntos().get(j).getPosY()+") ");
                        }
                        System.out.println();
                    }*/
                }


                rutasDeHoy.add(tempRutaVent);
                setOrddeHoy.add(tempSetOrd);
                capacidadDeHoy.add(capacidad);
                        

            }
            gestorV.avanzarVehiculos(1, setOrd, _reloj, almacen,-1,1);
            

            /*for (int i=0; i< gestorV.getVehiculos().size();i++) 
             gestorV.actualizarVehiculosYOrdenes(i, setOrd); */

            reloj.avanzaReloj();

            boolean fallo = false;

            for (int j = 0; j < setOrd.size(); j++) {
                /*System.out.print(" Pedido: " + setOrd.get(j).getIdOrden());
                System.out.println(" " + setOrd.get(j).getFechaMaxEntrega().toString());
                System.out.print(" Hora: ");
                System.out.println(reloj.getFechaActual().getTime());
                System.out.print(" Pos: ");
                System.out.println(setOrd.get(j).getPuntoEntrega().getPosX() +"," +setOrd.get(j).getPuntoEntrega().getPosY() );
                System.out.print(" Cantidad: ");
                System.out.println(setOrd.get(j).getNumPaquetes() );*/
                
                if (setOrd.get(j).getFechaMaxEntrega().before(reloj.getFechaActual().getTime())) {
                    fallo = true;
                    break;
                }
            }



            if (fallo) {
                _diaFallo = reloj.getDia();
                _mesFallo = reloj.getMes();
                _anhoFallo = reloj.getAnho();
                _horaFallo = reloj.getHoraActual();
                _minutoFallo = reloj.getMinutoActual();
                
                
                PronosticaFallo.actualizaFechaPronostico(idPronostico, _anhoFallo+"-"+_mesFallo+"-"+_diaFallo+" "+_horaFallo+":"+_minutoFallo+":"+"00" );
                
                break;
            }

            if (proxDia.compareTo(reloj.getFechaActual()) <= 0) {
                rutasDeAyer = rutasDeHoy;
                setOrddeAyer=setOrddeHoy;
                capacidadDeAyer=capacidadDeHoy;
                rutasDeHoy = new ArrayList<>();
                setOrddeHoy=new ArrayList<>();
                capacidadDeHoy=new ArrayList<>();
                proxDia = (Calendar) reloj.getFechaActual().clone();
                proxDia.add(Calendar.DAY_OF_MONTH, 1);
                try {
                    numdia++;                    
                    ordenes = generador.nuevoGeneraArchivo(numdia, reloj, mapa, this.idPronostico);
                } catch (Exception ex) {
                    Logger.getLogger(RouteCalculatorPronostico.class.getName()).log(Level.SEVERE, null, ex);
                }


            }
        }
        ArrayList<ArrayList<ArrayList<Ruta>>> resp = new ArrayList<>();
        resp.add(rutasDeAyer);
        resp.add(rutasDeHoy);
        return resp;
    }

    /**
     * @return the _detente
     */
    public boolean isDetente() {
        return _detente;
    }

    /**
     * @param detente the _detente to set
     */
    public void setDetente(boolean detente) {
        this._detente = detente;
    }

    public synchronized void correrDibujaPronostico() {
        if (RouteCalculatorPronostico.isEnEjecucion()) {
            return;
        }
        RouteCalculatorPronostico.setEnEjecucion(true);
        Thread dibujador = new Thread(this);
        dibujador.start();
    }

    @Override
    public void run() {


        Reloj reloj = _reloj;
        reloj.detenerReloj();
        reloj.setDia(_diaFallo);
        reloj.setMes(_mesFallo);
        reloj.setAnho(_anhoFallo);
        reloj.setHoraActual(0);
        reloj.setMinutoActual(0);

        this._detente = false;

        //mapa = new Mapa(150, 100);
        //ubicar el almacen
        //almacen = new Almacen(mapa.getPuntos().get(30 + 45 * mapa.getAlto()));// se ubica en 45,30

        Calendar proxVentana = (Calendar) reloj.getFechaActual().clone();
        proxVentana.add(Calendar.MINUTE, ventana);

        Calendar proxDia = (Calendar) reloj.getFechaActual().clone();
        proxDia.add(Calendar.DAY_OF_MONTH, 1);
        crearTurnos();
        crearChoferes();
        getGestorVehiculos().createVehicles(almacen, choferes,-1);
        getGestorVehiculos().setReloj(reloj);
        reloj.correrReloj();
        boolean actualizaVehiculo = false;
        int rutaDada = 0;
        while (_detente == false) {

            int hora = reloj.getHoraActual();
            int minuto = reloj.getMinutoActual();

            if (proxVentana.compareTo(reloj.getFechaActual()) > 0) {
                continue;
            }
            reloj.pausa();
            proxVentana.add(Calendar.MINUTE, ventana);

            getGestorVehiculos().pausar();

            System.out.println("Fecha " + reloj.getFechaString("dd-MM-yyyy"));
            System.out.println("Hora: " + reloj.getHoraActual() + ":" + reloj.getMinutoActual());
            System.out.println("Fallo: " + _diaFallo + "/" + _mesFallo + "/" + _anhoFallo + "||" + _horaFallo + ":" + _minutoFallo);
            System.out.println("Numero pedidos: " + getSetOrd().size() + " ");
            System.out.print("Tiempo del algoritmo: ");
            Date b = new Date();


            Date a = new Date();
            System.out.println(a.getTime() - b.getTime());

            //if (AntColony.bestSolutions!=null && AntColony.bestSolutions.size()>0)
            int tamrutas = rutasDeHoy.size();
            int acaba = 0;
            int tamV= gestorVehiculos.getVehiculos().size();
            if (rutaDada < tamrutas) {
                gestorVehiculos.asignaRutasSinCalcular(rutasDeHoy.get(rutaDada));
                gestorVehiculos.setSetOrd(this.setOrddeHoy.get(rutaDada));
                setSetOrd(this.setOrddeHoy.get(rutaDada));
                
                for (int i=0; i<tamV;i++){
                    gestorVehiculos.getVehiculos().get(i).setCapacidadActual(capacidadDeHoy.get(rutaDada).get(i));
                    
                }
                
            } else {
                acaba = 1;
            }
            rutaDada++;

            gestorVehiculos.avanzarVehiculos(ventana, setOrd, reloj, almacen,-1,1);

            if (acaba == 1) {
                reloj.detenerReloj();
                _detente = true;

                //getGestorVehiculos().detener();
                setEnEjecucion(false);
                reloj.detenerReloj();
                return;

            }

            if (proxDia.compareTo(reloj.getFechaActual()) <= 0) {
                proxDia = (Calendar) reloj.getFechaActual().clone();
                proxDia.add(Calendar.DAY_OF_MONTH, 1);
                try {
                    //ordenes=ManejaDatos.leeArchivo(prefijo);
                } catch (Exception ex) {
                    Logger.getLogger(RouteCalculatorPronostico.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (acaba == 1) {
                    reloj.detenerReloj();
                    _detente = true;
                    setEnEjecucion(false);
                    //getGestorVehiculos().detener();
                    reloj.detenerReloj();
                    return;

                }
            }


            //getGestorVehiculos().continuar();
            reloj.sigue();

        }


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
    public static void setServletContext(ServletContext servletContex) {
        servletContext = servletContex;
    }

    /**
     * @return the gestorVehiculos
     */
    public static GestorVehiculos getGestorVehiculos() {
        return gestorVehiculos;
    }

    /**
     * @param gestorVehiculos the gestorVehiculos to set
     */
    public static void setGestorVehiculos(GestorVehiculos gestorVehiculo) {
        gestorVehiculos = gestorVehiculo;
    }

    /**
     * @return the setOrddeAyer
     */
    public ArrayList<ArrayList<OrdenEntrega>> getSetOrddeAyer() {
        return setOrddeAyer;
    }

    /**
     * @param setOrddeAyer the setOrddeAyer to set
     */
    public void setSetOrddeAyer(ArrayList<ArrayList<OrdenEntrega>> setOrddeAyer) {
        this.setOrddeAyer = setOrddeAyer;
    }

    /**
     * @return the setOrddeHoy
     */
    public ArrayList<ArrayList<OrdenEntrega>> getSetOrddeHoy() {
        return setOrddeHoy;
    }

    /**
     * @param setOrddeHoy the setOrddeHoy to set
     */
    public void setSetOrddeHoy(ArrayList<ArrayList<OrdenEntrega>> setOrddeHoy) {
        this.setOrddeHoy = setOrddeHoy;
    }

    /**
     * @return the idPronostico
     */
    public int getIdPronostico() {
        return idPronostico;
    }

    /**
     * @param idPronostico the idPronostico to set
     */
    public void setIdPronostico(int idPronostico) {
        this.idPronostico = idPronostico;
    }
}
