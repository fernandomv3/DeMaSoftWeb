/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import DeMaSoft.GestorConexion;
import DeMaSoft.Reloj;
import java.util.*;
import DeMaSoft.RouteCalculator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import pronosticafallo.RouteCalculatorPronostico;
/**
 *
 * @author Fernando
 */
public class Vehicle {
    private int _idVehiculo;
    private Date _fechaCompra;
    private int _color;
    private int _capacidadActual;
    private int _capacidadTotal;
    private float _velocidadMaxima;
    private boolean _disponible;
    private String _placa;
    private Punto _posicion;
    private int _indice;
    //private ArrayList<Punto> _ruta;
    private float _residuo;
    private ArrayList<OrdenEntrega> _ordenes ;
    private Recorrido _recorrido;
    private Chofer _chofer;    
    
    public static void borraOrden(OrdenEntrega orden, ArrayList<OrdenEntrega> setOrd){
        int i=0;
        while(i< setOrd.size()){
            if(setOrd.get(i).getIdOrden()== orden.getIdOrden()){
                break;
            }
            i++;
        }
        if(i==setOrd.size())return;
        setOrd.remove(i);
    }
    
    public void actualizaOrdenes(Punto p, ArrayList<OrdenEntrega> setOrd,Reloj reloj, int minIdSimulacion, int codSimulacion){   
        //if(p == RouteCalculator.almacen.getUbicacion())return;
        for(int i =0; i< _ordenes.size();i++){
            if(_ordenes.get(i).getPuntoEntrega()==p){
                if(_ordenes.get(i).getNumPaquetes() > this.getCapacidadActual()){
                    _ordenes.get(i).setNumPaquetes(_ordenes.get(i).getNumPaquetes()-this.getCapacidadActual());
                    
                    if (minIdSimulacion>=0)
                        _ordenes.get(i).registraEntregaIncompleta(_idVehiculo, minIdSimulacion, this.getCapacidadActual(), reloj, codSimulacion, this._chofer.getIdPersona());
                    //System.out.println(minIdSimulacion);
                    //System.out.println(codSimulacion);
                    this.setCapacidadActual(0);
                    
                }
                else{
                    this.setCapacidadActual(this.getCapacidadActual()-_ordenes.get(i).getNumPaquetes());
                    _ordenes.get(i).setNumPaquetes(0);
                    //borraOrden(_ordenes.get(i), setOrd);
                    if(_ordenes.get(i).getFechaMaxEntrega().before(reloj.getFechaActual().getTime())){
                        RouteCalculator._pedidosTarde++;
                        System.out.println("Se debio entregar: "+_ordenes.get(i).getFechaMaxEntrega());
                        System.out.println("Se entrego a: "+reloj.getFechaActual().getTime());
                        System.out.println("Llego a las: "+_ordenes.get(i).getFechaPedido());
                    }
                    if (minIdSimulacion>=0)
                        _ordenes.get(i).registraEntregaCompleta(_idVehiculo, minIdSimulacion, this.getCapacidadActual(), reloj, codSimulacion,this._chofer.getIdPersona());
                    //System.out.println(minIdSimulacion);
                    //System.out.println(codSimulacion);
                    setOrd.remove(_ordenes.get(i));
                    
                    _ordenes.remove(i);   
                }
            }
        }
    }
    public void avanzar2(float tiempoSeg, Reloj reloj, ArrayList<OrdenEntrega> setOrd, Almacen almacen, ArrayList<Chofer>choferes, int minIdSimulacion, int codSimulacion) {
        if (!this.isDisponible()) {
            return;//si el auto no esta disponible no avanza
        }
        if (_posicion == almacen.getUbicacion()) {
                this._capacidadActual = this._capacidadTotal;
            }
        float dist = (_velocidadMaxima * tiempoSeg) / 3600 + _residuo; //distancia en KM
        int numNodos = (int) Math.round(Math.floor(dist));
        _residuo = (dist - numNodos) > 0 ? (dist - numNodos) : 0;        
        int tam = _recorrido.getRutaActual().getPuntos().size();
        for (int i = 0; i < numNodos && i < tam && !_recorrido.getRutaActual().getPuntos().isEmpty(); i++) {
            _posicion = _recorrido.getRutaActual().getPuntos().get(0);
            if (_posicion == almacen.getUbicacion()) {
                this._capacidadActual = this._capacidadTotal;
            }
            Calendar now = (Calendar) reloj.getFechaActual().clone();//obtener la hora actual
            Calendar endOfTurn = Calendar.getInstance();
            endOfTurn.setTime(this.getChofer().getTurno().getHoraFin());//obtener la hora de find e turno

            if (now.compareTo(endOfTurn) >= 0) {//si estamos en el almacen y ya se paso la hora de turno...
                Date horaIni = this.getChofer().getTurno().getHoraInicio();
                Date horaFin = this.getChofer().getTurno().getHoraFin();
                Calendar c = Calendar.getInstance();
                c.setTime(horaIni);
                c.add(Calendar.HOUR_OF_DAY, 8);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    if (minIdSimulacion>=0) Asistencia.getInstancia().insertaAsistencia(Asistencia.getInstancia().getIdAsistencia(), this.getChofer().getIdPersona(), sdf.format(this._chofer.getTurno().getHoraInicio()), sdf.format(reloj.getFechaActual().getTime()), codSimulacion);
                } catch (Exception ex) {
                    Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.getChofer().getTurno().setHoraInicio(c.getTime());
                c.setTime(horaFin);
                c.add(Calendar.HOUR_OF_DAY, 8);
                this.getChofer().getTurno().setHoraFin(c.getTime());
                int idChofer = this.getChofer().getIdPersona();
                idChofer += 60;
                if (idChofer >= 1181) {
                    idChofer -= 180;
                }
                try{
                    this.setChofer(choferes.get(idChofer-1001));
                }catch(Exception e){
                    
                    System.out.println(e.getMessage() +"   El id chofer es: " +  idChofer);
                }
            }
            actualizaOrdenes(_posicion, setOrd,reloj,minIdSimulacion, codSimulacion);
            
            _recorrido.getRutaActual().getPuntos().remove(_posicion);
        }
    }

    public void avanzar(float tiempoSeg,Reloj reloj, ArrayList<OrdenEntrega> setOrd, Almacen almacen,int minIdSimulacion, int codSimulacion){
        if(!this.isDisponible())return;
        float dist = _velocidadMaxima * tiempoSeg/3600 + _residuo;
        int numNodos = (int)Math.round(Math.floor(dist));
        _residuo = dist - numNodos;
        int posActual = getIndice();
        int idSimulacion=1;
        if(_posicion == almacen.getUbicacion()){
            this._capacidadActual= this._capacidadTotal;
        }
        for(int i = posActual+1;i<posActual+numNodos && i<_recorrido.getRutaActual().getPuntos().size();i++){
            _posicion = _recorrido.getRutaActual().getPuntos().get(i);
            if(_posicion == almacen.getUbicacion()){//si estamos en el almacen
                
                this._capacidadActual= this._capacidadTotal;
                Calendar now = (Calendar)reloj.getFechaActual().clone();
                Calendar endOfTurn = Calendar.getInstance();
                endOfTurn.setTime(this.getChofer().getTurno().getHoraFin());
                
                if(now.compareTo(endOfTurn)>=0){//si estamos en el almacen y ya se paso la hora de turno...
                    //this.getChofer().setYaAlmorzo(false);//resetear su hora de almuerzo
                    Date horaIni=this.getChofer().getTurno().getHoraInicio();
                    Date horaFin=this.getChofer().getTurno().getHoraFin();
                    Calendar c = Calendar.getInstance();
                    c.setTime(horaIni);
                    c.add(Calendar.HOUR_OF_DAY, 8);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    try {
                        Asistencia.getInstancia().insertaAsistencia(Asistencia.getInstancia().getIdAsistencia(),this.getChofer().getIdPersona(), sdf.format(this._chofer.getTurno().getHoraInicio()), sdf.format(reloj.getFechaActual().getTime()),codSimulacion);
                    } catch (Exception ex) {
                        Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.getChofer().getTurno().setHoraInicio(c.getTime());
                    c.setTime(horaFin);
                    c.add(Calendar.HOUR_OF_DAY, 8);
                    this.getChofer().getTurno().setHoraFin(c.getTime());
                    int idChofer = this.getChofer().getIdPersona();
                    idChofer +=60;
                    if(idChofer >=180)idChofer-=180;
                    this.setChofer(RouteCalculator.choferes.get(idChofer));
                }
            }
            actualizaOrdenes(_posicion, setOrd,reloj, minIdSimulacion, codSimulacion);
        }
        
    }
    
    
    /*
    public void avanzarPronostico(float tiempoSeg, ArrayList<OrdenEntrega> setOrd, Reloj reloj){
        if(!this.isDisponible())return;

        float dist = _velocidadMaxima * tiempoSeg/3600;
        int numNodos = Math.round(dist);
        float residuo = dist - numNodos;
        _residuo += residuo;
        int nodoExtra= Math.round(_residuo);
        _residuo-=nodoExtra;
        numNodos+=nodoExtra;
        int posActual = getIndice();
        
        for(int i = posActual+1;i<=posActual+numNodos && i<_recorrido.getRutaActual().getPuntos().size();i++){
            _posicion = _recorrido.getRutaActual().getPuntos().get(i);
            if(_posicion == RouteCalculatorPronostico.almacen.getUbicacion()){
                this._capacidadActual= this._capacidadTotal;
                Calendar now = (Calendar)reloj.getFechaActual().clone();
                Calendar endOfTurn = Calendar.getInstance();
                endOfTurn.setTime(this.getChofer().getTurno().getHoraFin());
                if(now.compareTo(endOfTurn)>=0){
                    Date horaIni=this.getChofer().getTurno().getHoraInicio();
                    Date horaFin=this.getChofer().getTurno().getHoraFin();
                    Calendar c = Calendar.getInstance();
                    c.setTime(horaIni);
                    c.add(Calendar.HOUR_OF_DAY, 8);
                    this.getChofer().getTurno().setHoraInicio(c.getTime());
                    c.setTime(horaFin);
                    c.add(Calendar.HOUR_OF_DAY, 8);
                    this.getChofer().getTurno().setHoraFin(c.getTime());
                    int idChofer = this.getChofer().getIdPersona();
                    idChofer +=60;
                    if(idChofer >=180)idChofer-=180;
                    this.setChofer(RouteCalculatorPronostico.choferes.get(idChofer));
                }
            }
            actualizaOrdenesPronostico(_posicion, setOrd);
        }
        
    }
    
    */
    
    public void actualizaOrdenesPronostico(Punto p, ArrayList<OrdenEntrega> setOrd){   
        //if(p == RouteCalculator.almacen.getUbicacion())return;
        for(int i =0; i< _ordenes.size();i++){
            if(_ordenes.get(i).getPuntoEntrega().getPosX()== p.getPosX() && _ordenes.get(i).getPuntoEntrega().getPosY()== p.getPosY()){
                if(_ordenes.get(i).getNumPaquetes() > this.getCapacidadActual()){
                    _ordenes.get(i).setNumPaquetes(_ordenes.get(i).getNumPaquetes()-this.getCapacidadActual());
                    this.setCapacidadActual(0);
                }
                else{
                    this.setCapacidadActual(this.getCapacidadActual()-_ordenes.get(i).getNumPaquetes());
                    _ordenes.get(i).setNumPaquetes(0);
                    borraOrdenPronostico(_ordenes.get(i), setOrd);
                    _ordenes.remove(i);   
                }
            }
        }
    }
    
    
    public static void borraOrdenPronostico(OrdenEntrega orden, ArrayList<OrdenEntrega> setOrd){
        int i;
        for(i=0; i< setOrd.size();i++){
            if(setOrd.get(i).getIdOrden()== orden.getIdOrden()){
                break;
            }
        }
        if(i==setOrd.size())return;
        setOrd.remove(i);
    }
    
    
    
    public Vehicle(int _capacidadTotal, float _velocidadMaxima) {
        this._capacidadTotal = _capacidadTotal;
        this._capacidadActual= _capacidadTotal;      
        this._velocidadMaxima = _velocidadMaxima;
        this._disponible = true;
        this._residuo=0;
        this._indice=0;
        this._ordenes = new ArrayList<>();
        this._recorrido=new Recorrido();
    }

    
    
    public int getIdVehiculo() {
        return _idVehiculo;
    }


    public void setIdVehiculo(int idVehiculo) {
        this._idVehiculo = idVehiculo;
    }

    public Date getFechaCompra() {
        return _fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this._fechaCompra = fechaCompra;
    }

    public int getColor() {
        return _color;
    }

    public void setColor(int color) {
        this._color = color;
    }

    public int getCapacidadActual() {
        return _capacidadActual;
    }

    public void setCapacidadActual(int capacidadInicial) {
        this._capacidadActual = capacidadInicial;
    }

    public int getCapacidadTotal() {
        return _capacidadTotal;
    }

    public void setCapacidadTotal(int capacidadTotal) {
        this._capacidadTotal = capacidadTotal;
    }

    public float getVelocidadMaxima() {
        return _velocidadMaxima;
    }

    public void setVelocidadMaxima(float velocidadMaxima) {
        this._velocidadMaxima = velocidadMaxima;
    }

    public boolean isDisponible() {
        return _disponible;
    }

    public void setDisponible(boolean disponible) {
        this._disponible = disponible;
    }

    public String getPlaca() {
        return _placa;
    }

    public void setPlaca(String placa) {
        this._placa = placa;
    }

    public Punto getPosicion() {
        return _posicion;
    }

    public void setPosicion(Punto posicion) {
        this._posicion = posicion;
    }

//    public ArrayList<Punto> getRuta() {
//        return _ruta;
//    }
//
//    public void setRuta(ArrayList<Punto> ruta) {
//        this._ruta = ruta;
//        this.setIndice(0);
//    }

    public ArrayList<OrdenEntrega> getOrdenes() {
        return _ordenes;
    }

    public void setOrdenes(ArrayList<OrdenEntrega> ordenes) {
        this._ordenes = ordenes;
    }  

    public Recorrido getRecorrido() {
        return _recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this._recorrido = recorrido;
    }
    
    public int getIndice() {
        return _indice;
    }

    public void setIndice(int indice) {
        this._indice = indice;
    }
    
     //Conexion a BD
    
    //insertar nuevo vehiculo
    
     public static void insertaVehicle(int _idVehiculo, String _fechaCompra, int _color, int _capacidadActual, int _capacidadTotal, float _velocidadMaxima, boolean _disponible,String _placa/*,Punto _posicion*/,int _indice,/*ArrayList<Punto> _ruta,*/ float _residuo/*, ArrayList<OrdenEntrega> _ordenes, Recorrido _recorrido*/
     )throws Exception{
         
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();

        query.append(" INSERT INTO VEHICLE(IDVEHICULO,FECHACOMPRA,COLOR,CAPACIDADACTUAL,CAPACIDADTOTAL,VELOCIDADMAXIMA,DISPONIBLE,PLACA,INDICE,RESIDUO) ");
        query.append(" VALUES(?,STR_TO_DATE(?, '%d-%m-%Y'),?,?,?,?,?,?,?,?) ");
        
        
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
            
        pstmt.setInt(1, _idVehiculo);
        pstmt.setString(2, _fechaCompra);
        pstmt.setInt(3, _color);
        pstmt.setInt(4, _capacidadActual);
        pstmt.setInt(5, _capacidadTotal);
        pstmt.setFloat(6, _velocidadMaxima);
        pstmt.setBoolean(7, _disponible);
        pstmt.setString(8, _placa);
        pstmt.setInt(9, _indice);
        pstmt.setFloat(10, _residuo);
            
        try{
            rset=pstmt.executeQuery();
        }catch(Exception e){
            con.rollback();
            throw e;    
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }

    }
    
    
    public static ArrayList<Vehicle> seleccionaVehicle(int _idVehiculo, String _fechaCompra, int _color, int _capacidadTotal, float _velocidadMaxima,String _placa, float _residuo)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        ArrayList<Vehicle> vehiculos= new ArrayList();
        int previo=0;
        
        query.append(" SELECT IDVEHICULO,FECHACOMPRA,COLOR,CAPACIDADTOTAL,VELOCIDADMAXIMA,PLACA,RESIDUO");
        query.append(" FROM VEHICLE WHERE ");
        
        if (_idVehiculo!=-1){ 
            query.append(" IDVEHICULO=? ");
            previo=1;
        }
        
        if (!_fechaCompra.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" FECHACOMPRA=? ");
            previo=1;
        }
        
        if (_color!=-1){
            if (previo==1) query.append(" AND ");
            query.append(" COLOR=? ");
            previo=1;
        }
        
        if (_capacidadTotal!=-1){
            if (previo==1) query.append(" AND ");
            query.append(" CAPACIDADTOTAL=? ");
            previo=1;
        }
        
        if (_velocidadMaxima!=-1){
            if (previo==1) query.append(" AND ");
            query.append(" VELOCIDADMAXIMA=? ");
            previo=1;
        }
                        
        if (!_placa.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" PLACA=? ");
            previo=1;
        }
        
        if(_residuo!=-1){
            if (previo==1) query.append(" AND ");
            query.append(" RESIDUO=? ");
            previo=1;
        }
                
        
        
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
        
        int contador=0;
            
        
        if (_idVehiculo!=-1){ 
            pstmt.setInt(++contador, _idVehiculo);
            
        }
        
        if (!_fechaCompra.equals("")){
            pstmt.setString(++contador, _fechaCompra);
            
        }
        
        if (_color!=-1){
            pstmt.setInt(++contador, _color);
            
        }
        
        
        if (_capacidadTotal!=-1){
            pstmt.setInt(++contador, _capacidadTotal);
            
        }
        
        if (_velocidadMaxima!=-1){
            pstmt.setFloat(++contador, _velocidadMaxima);
            
        }
        
        if (!_placa.equals("")){
            pstmt.setString(++contador, _placa);            
            
        }
        
        if (_residuo!=-1){
            pstmt.setFloat(++contador, _residuo);
        }

            
        try{
            rset=pstmt.executeQuery();
            
            while (rset.next()){
                
                Vehicle vehicle=new Vehicle(_capacidadTotal, _velocidadMaxima);
                vehicle.setIdVehiculo(rset.getInt("IDVEHICULO"));
                vehicle.setFechaCompra(rset.getDate("FECHACOMPRA"));
                vehicle.setColor(rset.getInt("COLOR"));
                vehicle.setCapacidadTotal(rset.getInt("CAPACIDADTOTAL"));
                vehicle.setVelocidadMaxima(rset.getFloat("VELOCIDADMAXIMA"));
                vehicle.setPlaca(rset.getString("PLACA"));   
                
                vehiculos.add(vehicle);
            }
            
            
        }catch(Exception e){
            con.rollback();
            throw e;    
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }
        
        return vehiculos;
        
    }
    
    public static Vehicle getVehicleById(String idVehiculo)throws Exception{
        
        Vehicle vehicle= null;
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        
        query.append(" SELECT IDVEHICULO,FECHACOMPRA,COLOR,CAPACIDADTOTAL,VELOCIDADMAXIMA,PLACA,RESIDUO " );
        query.append(" FROM VEHICLE ");
        query.append(" WHERE IDVEHICULO = ? ");
        
        Connection con= GestorConexion.getConexion();
        
        pstmt= con.prepareStatement(query.toString());
        
        pstmt.setString(1, idVehiculo);
        
        
        try{
            
            rset=pstmt.executeQuery();
            
            if (rset.next()){
                
                vehicle=new Vehicle(rset.getInt("CAPACIDADTOTAL"), rset.getFloat("VELOCIDADMAXIMA"));
                vehicle.setIdVehiculo(rset.getInt("IDVEHICULO"));
                vehicle.setFechaCompra(rset.getDate("FECHACOMPRA"));
                vehicle.setColor(rset.getInt("COLOR"));
                vehicle.setCapacidadTotal(rset.getInt("CAPACIDADTOTAL"));
                vehicle.setVelocidadMaxima(rset.getFloat("VELOCIDADMAXIMA"));
                vehicle.setPlaca(rset.getString("PLACA"));
            
            }
        }catch(Exception e){
            
            con.rollback();
            throw e;
        }
        
        return vehicle;
    }

    /**
     * @return the _chofer
     */
    public Chofer getChofer() {
        return _chofer;
    }

    /**
     * @param chofer the _chofer to set
     */
    public void setChofer(Chofer chofer) {
        this._chofer = chofer;
    }
    
}
