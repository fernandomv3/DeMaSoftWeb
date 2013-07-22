package DeMaSoft;

import models.*;
import java.util.*;
import antcolony.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.json.simple.*;

/**
 *
 * @author Fernando
 */
public class RouteCalculator implements Runnable {

    public static RouteCalculator _routeCalculator;
    public static Reloj reloj = new Reloj();

    /**
     * @return the _diaFin
     */
    public static int getDiaFin() {
        return _diaFin;
    }

    /**
     * @param aDiaFin the _diaFin to set
     */
    public static void setDiaFin(int aDiaFin) {
        _diaFin = aDiaFin;
    }

    /**
     * @return the _mesFin
     */
    public static int getMesFin() {
        return _mesFin;
    }

    /**
     * @param aMesFin the _mesFin to set
     */
    public static void setMesFin(int aMesFin) {
        _mesFin = aMesFin;
    }

    /**
     * @return the _anhoFin
     */
    public static int getAnhoFin() {
        return _anhoFin;
    }

    /**
     * @param aAnhoFin the _anhoFin to set
     */
    public static void setAnhoFin(int aAnhoFin) {
        _anhoFin = aAnhoFin;
    }
    public final int ventana = 8;
    public static Mapa mapa;
    public static Almacen almacen;
    public ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
    
    public ArrayList<Incidente> incidentes = new ArrayList<>();
    public static ArrayList<OrdenEntrega> setOrd = new ArrayList<>();
    public static String prefijo = "dat";
    public static ArrayList<Turno> turnos = new ArrayList<>();
    public static ArrayList<Chofer> choferes = new ArrayList<>();
    public static GestorVehiculos gestorVehiculos;
    public static int posXAlmacen = 45;
    public static int posYAlmacen = 30;
    private static int _diaInicio = 17;
    private static int _mesInicio = 05;
    private static int _anhoInicio = 2012;
    private static int _diaFin = 20;
    private static int _mesFin = 05;
    private static int _anhoFin = 2013;
    private int _tipo = 0;//0 nueva simualción, 1 usando data ya pasada
    private int _codSimulacion = -1;
    private int _minIdOrdenEntrega = -1;
    public static int _pedidosTarde = 0;

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
        almacen.put("x", RouteCalculator.almacen.getUbicacion().getPosX());
        almacen.put("y", RouteCalculator.almacen.getUbicacion().getPosY());
        obj.put("almacen", almacen);
        obj.put("numAutos", GestorVehiculos.numAutos);
        obj.put("numMotos", GestorVehiculos.numMotos);
        return obj.toJSONString();
    }

    /**
     * @return the _tipo
     */
    public int getTipo() {
        return _tipo;
    }

    /**
     * @param aTipo the _tipo to set
     */
    public void setTipo(int aTipo) {
        _tipo = aTipo;
    }

    /**
     * @return the _codSimulacion
     */
    public int getCodSimulacion() {
        return _codSimulacion;
    }

    /**
     * @param aCodSimulacion the _codSimulacion to set
     */
    public void setCodSimulacion(int aCodSimulacion) {
        _codSimulacion = aCodSimulacion;
    }

    /**
     * @return the _diaInicio
     */
    public static int getDiaInicio() {
        return _diaInicio;
    }

    /**
     * @param aDiaInicio the _diaInicio to set
     */
    public static void setDiaInicio(int aDiaInicio) {
        _diaInicio = aDiaInicio;
    }

    /**
     * @return the _mesInicio
     */
    public static int getMesInicio() {
        return _mesInicio;
    }

    /**
     * @param aMesInicio the _mesInicio to set
     */
    public static void setMesInicio(int aMesInicio) {
        _mesInicio = aMesInicio;
    }

    /**
     * @return the _anho
     */
    public static int getAnhoInicio() {
        return _anhoInicio;
    }

    /**
     * @param aAnho the _anho to set
     */
    public static void setAnhoInicio(int aAnho) {
        _anhoInicio = aAnho;
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
    private boolean _detente = false;
    private static boolean _enEjecucion = false;
    private ServletContext servletContext;

    public static RouteCalculator getInstancia() {
        if (_routeCalculator == null) {
            _routeCalculator = new RouteCalculator();
        }
        return _routeCalculator;
    }

    public synchronized void correrSimulacion() {
        if (RouteCalculator.isEnEjecucion()) {
            return;
        }
        RouteCalculator.setEnEjecucion(true);
        Thread simulacion = new Thread(this);
        simulacion.start();
    }

    public void detenerSimulacion() {
        this._detente = true;
    }

    public void crearTurnos(Reloj reloj) {
        if (!turnos.isEmpty()) {
            return;
        }
        Calendar c = (Calendar) reloj.getFechaActual().clone();
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
        if (!choferes.isEmpty()) {
            return;
        }
        int numChoferes = GestorVehiculos.numAutos + GestorVehiculos.numMotos;

        int maxChoferenBD = 0;

        numChoferes = numChoferes * turnos.size();
        int maxCod = Chofer.maxChofer();
        for (int i = 0; i < numChoferes; i++) {
            Chofer chofer = new Chofer();
            chofer.setIdPersona(1001 + i);
            chofer.setTurno(turnos.get(i / (GestorVehiculos.numAutos + GestorVehiculos.numMotos)));
            choferes.add(chofer);

            if (chofer.getIdPersona() > maxCod) {

                chofer.insertaChofer();
            }
        }
    }

    public void inicializaValores() {

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuffer query = new StringBuffer();


        query.append(" select PS.FechaInicio, PS.FechaFin , PS.Prefijo, A.posX, A.posY from Parametros_Simulacion PS, ALMACEN A ");
        query.append(" where idParametros_Simulacion= (select MAX(idParametros_Simulacion) from Parametros_Simulacion) AND A.IDSIMULACION=PS.IDSIMULACION ");


        try {
            Connection con = GestorConexion.getConexion();

            pstmt = con.prepareStatement(query.toString());

            rset = pstmt.executeQuery();

            if (rset.next()) {
                String fechaInicio = rset.getString(1);
                String fechaFin = rset.getString(2);
                if (fechaInicio != null && fechaInicio.length() == 10) {

                    _diaInicio = Integer.parseInt(fechaInicio.substring(8));
                    _mesInicio = Integer.parseInt(fechaInicio.substring(5, 7));
                    _anhoInicio = Integer.parseInt(fechaInicio.substring(0, 4));
                }

                if (fechaFin != null && fechaFin.length() == 10) {

                    _diaFin = Integer.parseInt(fechaFin.substring(8));
                    _mesFin = Integer.parseInt(fechaFin.substring(5, 7));
                    _anhoFin = Integer.parseInt(fechaFin.substring(0, 4));
                }

                prefijo = rset.getString(3);
                if (prefijo == null) {
                    prefijo = "dat";
                }
                posXAlmacen = rset.getInt(4);
                posYAlmacen = rset.getInt(5);
            } else {
                _diaInicio = 17;
                _mesInicio = 5;
                _anhoInicio = 2012;
                prefijo = "dat";
                posXAlmacen = 45;
                posYAlmacen = 30;
            }

        } catch (Exception e) {

            _diaInicio = 17;
            _mesInicio = 5;
            _anhoInicio = 2012;
            prefijo = "dat";
            posXAlmacen = 45;
            posYAlmacen = 30;

            System.out.println("inicializaValores " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rset != null) {
                    rset.close();
                }
            } catch (Exception e) {

                System.out.println("inicializaValores " +e.getMessage());
            }

        }


    }

    public void grabaParametros() {

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuffer query = new StringBuffer();


        query.append(" UPDATE Parametros_Simulacion SET ");
        query.append(" FechaInicio=?, FechaFin=? , Prefijo=? WHERE idSimulacion=?");


        try {
            Connection con = GestorConexion.getConexion();

            pstmt = con.prepareStatement(query.toString());

            pstmt.setString(1, _anhoInicio + "-" + _mesInicio + "-" + _diaInicio);
            pstmt.setString(2, _anhoFin + "-" + _mesFin + "-" + _diaFin);
            pstmt.setString(3, prefijo);
            pstmt.setInt(4, this._codSimulacion);
            pstmt.executeUpdate();





        } catch (Exception e) {

            System.out.println("grabaParametros " + e.getMessage());
        }
    }

    public void grabaAlamacen() {

        if (almacen == null) {
            if (mapa == null) {
                mapa = new Mapa(150, 100,servletContext);
            }
            almacen = new Almacen(mapa.getPuntos().get(posYAlmacen + posXAlmacen * mapa.getAlto()));// se ubica en 45,30
        }
        almacen.grabarAlmacen(_codSimulacion);
    }

    @Override
    public void run() {

        this.inicializaValores();
        Incidente.setMaxIDIncidente(0);
        reloj.setReloj(0, 0, _diaInicio, _mesInicio, _anhoInicio); //setear la fecha de inicio
        //reloj.setReloj(0, 0, 21, 05, 2013);
        gestorVehiculos = new GestorVehiculos(reloj);
        gestorVehiculos.setVentana(ventana);

        this._detente = false;
        //crear mapa
        if (mapa == null) {
            mapa = new Mapa(150, 100,servletContext);
        }

        //ubicar el almacen
        if (almacen == null) {
            almacen = new Almacen(mapa.getPuntos().get(posYAlmacen + posXAlmacen * mapa.getAlto()));// se ubica en 45,30
        }
        gestorVehiculos.setAlmacen(almacen);
        this.grabaSimulacion();

        AntColony antColony = new AntColony();
        antColony.clock = reloj.getFechaActual();
        antColony.isPronostico = true;//correr en modo simulacion o en modo pronostico
        //crear el mapa de nodos para el algoritmo
        antColony.createNodeMap(mapa);
        //empezar simulacion
        antColony.initializeArcsPheromoneLevel();//inicializar la matriz de feromonas
        //iniciar tiempo a 00:00
        Calendar proxTurno = (Calendar) reloj.getFechaActual().clone();
        proxTurno.add(Calendar.HOUR, 8);//el porx turno son 8 horas despues
        Calendar proxVentana = (Calendar) reloj.getFechaActual().clone();
        proxVentana.add(Calendar.MINUTE, ventana);//la proxima vez que recalcule seran "ventana" minutos despues

        Calendar proxIncidencia = (Calendar) reloj.getFechaActual().clone();
        proxIncidencia.add(Calendar.MINUTE, 270);//mitad del turno + 30 min sera la proxima incidencia

        Calendar proxDia = (Calendar) reloj.getFechaActual().clone();
        proxDia.add(Calendar.DAY_OF_MONTH, 1);
        ManejaDatos m = new ManejaDatos();
        m.setCodSimulacion(_codSimulacion);
        m.setPrefijo(prefijo);
        m.setReloj(reloj);
        try {
            m.correr();//empezar a cargar los datos de los archivos en la bd
            Thread.sleep(3500);//esperar 3 segundo y medio para empezar la simulacion
            ordenes = ManejaDatos.leeArchivo(prefijo, reloj, mapa);//leer el primer archivo
            OrdenEntrega.obtenCodBD(ordenes, _codSimulacion);
            _minIdOrdenEntrega = m.minIdOrden(_codSimulacion);
            //System.out.println(_minIdOrdenEntrega);
            //System.out.println(_codSimulacion);

        } catch (Exception ex) {
            Logger.getLogger(RouteCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

        //setOrd = m.sacaSetOrdenes(ordenes, 0, ventana, ventana);
        setOrd = m.sacaSetOrdenes2(ordenes, reloj, ventana);//sacar las ordenes de la primera ventana de tiempo
        gestorVehiculos.setSetOrd(setOrd);
        crearTurnos(reloj);//crear los turnos
        crearChoferes();//crear los choferes
        //crear los vehículos(se crean en el almacen)
        gestorVehiculos.setChoferes(choferes);//asignar un chofer a cada vehiculo
        gestorVehiculos.createVehicles(almacen, choferes, this._codSimulacion);//crear los vehiculos
        reloj.correrReloj();//comenzar a correr el reloj
        //boolean actualizaVehiculo = false;
        while (_detente == false) {
            if (proxVentana.compareTo(reloj.getFechaActual()) > 0) {//si aun no es momento de recalcular
                for (int o = 1; o < setOrd.size(); o++) {//for para chequear que las ordenes no hayan vencido
                    if (setOrd.get(o).getFechaMaxEntrega().before(reloj.getFechaActual().getTime())) {
                        //System.out.println("Tarde");
                        break;
                    }
                }
                //incidencias
                int minutAleat = (int)Math.round(Math.random()*60);//aleatorizar la mitad del truno
                 Calendar aleat = (Calendar)proxIncidencia.clone();
                 aleat.add(Calendar.MINUTE, minutAleat);// entre las 4:30 y 5:30
                 if (aleat.before(reloj.getFechaActual())) {//si es la hora de la incidencia
                 siniestrarVehiculo();
                 proxIncidencia.add(Calendar.HOUR, 8);//siguiente turno
                 }
                //fin incidencias

                //para reestablecer los vehiculos que quedaron varados
                if (proxTurno.before(reloj.getFechaActual())) {
                    proxTurno.add(Calendar.HOUR, 8);
                    for (int i = 0; i < gestorVehiculos.getVehiculos().size(); i++) {
                        if (!gestorVehiculos.getVehiculos().get(i).isDisponible()) {
                            gestorVehiculos.getVehiculos().get(i).setPosicion(almacen.getUbicacion());
                            gestorVehiculos.getVehiculos().get(i).setDisponible(true);
                        }
                    }
                }
                //fin reestablecer
                continue;
            }
            //en caso sea hora de planificar
            reloj.pausa();//pausar el reloj
            proxVentana.add(Calendar.MINUTE, ventana);//setear la sguiente hora a replanifiar

            gestorVehiculos.pausar();

            System.out.println("Fecha " + reloj.getFechaString("dd-MM-yyyy"));
            System.out.println("Hora: " + reloj.getHoraActual() + ":" + reloj.getMinutoActual());
            System.out.println("Numero pedidos: " + setOrd.size() + " ");
            System.out.print("Tiempo del algoritmo: ");
            Date b = new Date();
            antColony.reverseAntColonyProcedure(gestorVehiculos.getVehiculos(), setOrd, reloj, almacen, mapa);//correr la planificcion

            /*for(int z = 0 ; z< 1;z++){
             System.out.println("Vehiculo "+z+":");
             for(int y =0; y<AntColony.bestSolutions.get(z).size();y++){
             System.out.print("("+AntColony.bestSolutions.get(z).get(y).getX()+","+AntColony.bestSolutions.get(z).get(y).getY()+")");
             }
             System.out.println();
             }*/

            Date a = new Date();
            System.out.println(a.getTime() - b.getTime());
            System.out.println("Pedidos tarde hasta la fecha: " + RouteCalculator._pedidosTarde);
            //ArrayList<OrdenEntrega> setOrdtemp = m.sacaSetOrdenes(ordenes, reloj.getHoraActual(), reloj.getMinutoActual(), ventana);
            ArrayList<OrdenEntrega> setOrdtemp = m.sacaSetOrdenes2(ordenes, reloj, ventana);//sacar el siguiente grupo de ordenes de la ventana
            setOrd.addAll(setOrdtemp);//aadirlas al conjunto de ordenes pendientes
            gestorVehiculos.setSetOrd(setOrd);
            //if (AntColony.bestSolutions!=null && AntColony.bestSolutions.size()>0)
            gestorVehiculos.asignarRutas(antColony.bestSolutions, setOrd, mapa, almacen);//asiganr las rutas calculadas por antColony
            /*if(!actualizaVehiculo){
             actualizaVehiculo=true;
             gestorVehiculos.actualizaVehiculos();
             }*/
            gestorVehiculos.avanzarVehiculos(ventana, setOrd, reloj, almacen, _minIdOrdenEntrega, _codSimulacion);

            /*for(int i=0;i<GestorVehiculos.getInstancia().getVehiculos().size();i++){
             Vehicle v = GestorVehiculos.getInstancia().getVehiculos().get(i);
             System.out.println("Vehiculo "+i+":");
             for(int j=0;j< v.getRuta().size();j++ ){
             System.out.print("("+v.getRuta().get(j).getPosX()+","+v.getRuta().get(j).getPosY()+") ");
             }
             System.out.println();
             }*/

            if (proxDia.compareTo(reloj.getFechaActual()) <= 0) {//si hay cambo de dia
                proxDia = (Calendar) reloj.getFechaActual().clone();
                proxDia.add(Calendar.DAY_OF_MONTH, 1);//calcular el siguiente dia
                try {
                    ordenes = ManejaDatos.leeArchivo(prefijo, reloj, mapa);//leer el siguiente archivo
                    OrdenEntrega.obtenCodBD(ordenes, _codSimulacion);
                } catch (Exception ex) {
                    Logger.getLogger(RouteCalculator.class.getName()).log(Level.SEVERE, null, ex);
                }
                Calendar cFin = Calendar.getInstance();
                cFin.set(_anhoFin, _mesFin, _diaFin);
                //condicion de parada
                if (setOrd == null || (setOrd.size() == 0 && !ManejaDatos.existeSiguienteArchivo(reloj)) || reloj.getFechaActual().after(cFin)) {
                    reloj.detenerReloj();//detener reloj
                    _detente = true;//detener bucle
                    //gestorVehiculos.detener();
                    m.detener();//detener la subid de datos a la bd
                }
            }

            //gestorVehiculos.continuar();
            reloj.sigue();
        }
        RouteCalculator.setEnEjecucion(false);
        gestorVehiculos.detener();
        reloj.detenerReloj();
        m.detener();
        System.out.println("Numero de pedidos tarde: " + _pedidosTarde);
        return;
    }

    public static void main(String[] args) {
        RouteCalculator rC = RouteCalculator.getInstancia();
        rC.correrSimulacion();
    }

    /**
     * @return the servletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @param servletContext the servletContext to set
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void grabaSimulacion() {

        if (this._codSimulacion == -1) {

            this._codSimulacion = insertaNuevaSimulacion();
        }
        GestorVehiculos.grabaParametros(this._codSimulacion);
        almacen.grabarAlmacen(_codSimulacion);
        this.grabaParametros();
    }

    public int insertaNuevaSimulacion() {

        int cod = 0;

        PreparedStatement pstmt = null;
        StringBuffer query = new StringBuffer("");


        query.append("Insert into SIMULACION(fecha) values(sysdate())");


        PreparedStatement pstmt2 = null;
        ResultSet rset = null;
        StringBuffer query2 = new StringBuffer("SELECT IFNULL(MAX(idSIMULACION),0) FROM SIMULACION ");

        try {

            Connection con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            pstmt2 = con.prepareStatement(query2.toString());

            pstmt.executeUpdate();

            rset = pstmt2.executeQuery();

            if (rset.next()) {

                cod = rset.getInt(1);
            }

        } catch (Exception e) {
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (pstmt2 != null) {
                    pstmt2.close();
                }
            } catch (Exception e) {
            }
        }



        return cod;
    }

    /**
     * @return the _minIdOrdenEntrega
     */
    public int getMinIdOrdenEntrega() {
        return _minIdOrdenEntrega;
    }

    /**
     * @param minIdOrdenEntrega the _minIdOrdenEntrega to set
     */
    public void setMinIdOrdenEntrega(int minIdOrdenEntrega) {
        this._minIdOrdenEntrega = minIdOrdenEntrega;
    }

    private void siniestrarVehiculo() {
        OrdenEntrega selected = null;
        for (int i = 0; i < setOrd.size(); i++) {//recorrer todos los pedidos actuales
            if (setOrd.get(i).getPrioridadInicial() == 2) {//si encuentra algun pedido de 2 horas
                selected = setOrd.get(i);
                for (int j = 0; j < gestorVehiculos.getVehiculos().size(); j++) {//recorrer los vehiculos
                    if (gestorVehiculos.getVehiculos().get(j).getOrdenes().contains(selected)) {
                        gestorVehiculos.getVehiculos().get(j).setDisponible(false);



                        Incidente incidente = new Incidente();
                        incidente.setFecha(reloj.getFechaString("yyyy-MM-dd hh:mm:ss"));
                        incidente.setVehiculo(gestorVehiculos.getVehiculos().get(j));
                        incidente.grabarIncidente(_codSimulacion);


                        return;
                    }
                }
            }
        }
        if (selected == null) {
            return;// se deberia escoger un aleatorio
        }
    }
}
