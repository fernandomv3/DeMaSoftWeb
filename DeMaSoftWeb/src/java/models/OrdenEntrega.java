/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;


import DeMaSoft.GestorConexion;
import DeMaSoft.Reloj;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fernando
 */
public class OrdenEntrega {
    private int _idOrden;
    private int _idOrdenBD;
    private Punto _ubicacion;
    private int _numPaquetes;
    private int _prioridad;
    private int _prioridadInicial=0;
    private int _horaPedido;
    private int _minutoPedido;
    private int _horaMaxPedido;
    private int _minutoMaxPedido;
    private String estado;
    private boolean pagoPorAdelantado;
    private Punto _puntoEntrega;
    private Cliente _cliente;
    private Date _fechaPedido;
    private Date _fechaEntrega;
    private Date _fechaMaxEntrega;
    private int _idSimulacion;
    private Date _fechaSimulacion;
    private String fusion;
    private int idPronostico;
    private Date fechaPronostico;
    private int numPaquetesExtra;
    
    public int getIdOrden() {
        return _idOrden;
    }

    public void setIdOrden(int idOrden) {
        this._idOrden = idOrden;
    }

    public Punto getUbicacion() {
        return _ubicacion;
    }

    public void setUbicacion(Punto ubicacion) {
        this._ubicacion = ubicacion;
    }
    
    public Cliente getCliente() {
        return _cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this._cliente = cliente;
    }
    
    public int getNumPaquetes() {
        return _numPaquetes;
    }

    public void setNumPaquetes(int numPaquetes) {
        this._numPaquetes = numPaquetes;
    }

    public int getPrioridad() {
        return _prioridad;
    }

    public void setPrioridad(int prioridad) {
        this._prioridad = prioridad;
    }

    public int getHoraPedido() {
        return _horaPedido;
    }


    public void setHoraPedido(int horaPedido) {
        this._horaPedido = horaPedido;
    }

    public int getMinutoPedido() {
        return _minutoPedido;
    }

    public void setMinutoPedido(int minutoPedido) {
        this._minutoPedido = minutoPedido;
    }
    
    public int getHoraMaxPedido() {
        return _horaMaxPedido;
    }

    public void setHoraMaxPedido(int horaMaxPedido) {
        this._horaMaxPedido = horaMaxPedido;
    }

    public int getMinutoMaxPedido() {
        return _minutoMaxPedido;
    }

    public void setMinutoMaxPedido(int minutoMaxPedido) {
        this._minutoMaxPedido = minutoMaxPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isPagoPorAdelantado() {
        return pagoPorAdelantado;
    }

    public void setPagoPorAdelantado(boolean pagoPorAdelantado) {
        this.pagoPorAdelantado = pagoPorAdelantado;
    }

    public Punto getPuntoEntrega() {
        return _puntoEntrega;
    }

    public void setPuntoEntrega(Punto puntoEntrega) {
        this._puntoEntrega = puntoEntrega;
    }

    public int getPrioridadInicial() {
        return _prioridadInicial;
    }

    public void setPrioridadInicial(int prioridadInicial) {
        this._prioridadInicial = prioridadInicial;
    }
    
    public Date getFechaPedido() {
        return _fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this._fechaPedido = fechaPedido;
    }

    public Date getFechaEntrega() {
        return _fechaEntrega;
    }

    public void setFechaEntrega(Timestamp fechaEntrega) {
        this._fechaEntrega = fechaEntrega;
    }
    
    public void actualizaPrioridad(int hora, int minuto){
        
        if (this._prioridadInicial==0) this._prioridadInicial=this._prioridad;
        
        if (this._minutoPedido<=minuto && ((hora-this._horaPedido)>(this._prioridadInicial-this._prioridad)))
              this._prioridad--;
    }
    
    
    public int getIdSimulacion() {
        return _idSimulacion;
    }

 
    public void setIdSimulacion(int idSimulacion) {
        this._idSimulacion = idSimulacion;
    }
  
    public Date getFechaSimulacion() {
        return _fechaSimulacion;
    }

    public void setFechaSimulacion(Date fechaSimulacion) {
        this._fechaSimulacion = fechaSimulacion;
    }
    
    public static ArrayList<Incidente> SeleccionarIncidenciaporFecha(Date fecha) throws Exception{
          ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String query = "SELECT IDINCIDENTE, IDVEHICULO, FECHA FROM INCIDENTE WHERE DATE(FECHA) = '" + sdf.format(fecha)+ "'";
        Connection con;
        ArrayList<Incidente> listaIncidentes = new ArrayList<>();
       try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                Incidente inc=new Incidente();
                inc.setIdIncidente(rset.getInt(1));
                inc.setIdVehiculo(rset.getString(2));
                inc.setFechaInc(rset.getTimestamp(3));
                listaIncidentes.add(inc);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaIncidentes;
    }
    
    
     public static ArrayList<OrdenEntrega> SeleccionarNumPaquetesporFecha(Date fecha) throws Exception{
          ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String query = "SELECT IDPRONOSTICO,  FECHA, NUMPAQUETES FROM PAQUETEXDIAXPRONOSTICO WHERE DATE(FECHA) = '" + sdf.format(fecha)+ "'";
        Connection con;
    
       try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord=new OrdenEntrega();
                ord.setIdPronostico(rset.getInt(1));
                ord.setFechaPronostico(rset.getTimestamp(2));
                ord.setNumPaquetesExtra(rset.getInt(3));
                ordenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return ordenes;
    }
     public static ArrayList<Incidente> SeleccionarFechasIncidencias() throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT DISTINCT DATE(FECHA) FROM INCIDENTE ";
        Connection con;
        ArrayList<Incidente> incidencias = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
               Incidente inc= new Incidente();
                inc.setFecha(rset.getString(1));
                incidencias.add(inc);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return incidencias;
    }
      public static ArrayList<OrdenEntrega> SeleccionarFechasPronostico() throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT DISTINCT DATE(FECHA) FROM PAQUETEXDIAXPRONOSTICO ";
        Connection con;
        ArrayList<OrdenEntrega> ordenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
              OrdenEntrega orden= new OrdenEntrega();
                orden.setFechaPronostico(rset.getTimestamp(1));
                ordenes.add(orden);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return ordenes;
    }
    
     public static ArrayList<OrdenEntrega> SeleccionarIdSimulaciones(String fechaSimulacion) throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT IDSIMULACION FROM SIMULACION WHERE DATE(FECHA)= ? ";
        Connection con;
        ArrayList<OrdenEntrega> listaOrdenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, fechaSimulacion);
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord= new OrdenEntrega();
                ord.setIdSimulacion(rset.getInt(1));
                listaOrdenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaOrdenes;
    }
    
    public static ArrayList<OrdenEntrega> SeleccionarSimulaciones() throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT DISTINCT DATE(FECHA) FROM SIMULACION ";
        Connection con;
        ArrayList<OrdenEntrega> listaOrdenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord= new OrdenEntrega();
                ord.setFechaSimulacion(rset.getDate(1));
                listaOrdenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaOrdenes;
    }
     public static ArrayList<OrdenEntrega> SeleccionarTotalOrdenesDeEntrega() throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT IDORDEN, HORAPEDIDO, HORAENTREGA,NUMPAQUETES,ESTADO FROM ORDENDEENTREGA LIMIT 1,30 ";
        System.out.println(query);
        Connection con;
        ArrayList<OrdenEntrega> listaOrdenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord= new OrdenEntrega();
                ord.setIdOrden(rset.getInt(1));
                ord.setFechaPedido(rset.getTimestamp(2));
                ord.setFechaEntrega(rset.getTimestamp(3));
                ord.setNumPaquetes(rset.getInt(4));
                ord.setEstado(rset.getString(5));
             
                listaOrdenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaOrdenes;
    }
        
    public static ArrayList<OrdenEntrega> SeleccionarOrdenEntrega(int _idOrden, String _idEstado,Date fini, Date ffin) throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String query = "SELECT IDORDEN, HORAPEDIDO, HORAENTREGA,NUMPAQUETES,ESTADO FROM ORDENDEENTREGA WHERE IDORDEN = " + _idOrden + " AND ESTADO = '"+ _idEstado + "' AND HORAPEDIDO BETWEEN '" + sdf.format(fini) + "' AND '" +sdf.format(ffin)+ "'";
        Connection con;
        ArrayList<OrdenEntrega> listaOrdenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord= new OrdenEntrega();
                ord.setIdOrden(rset.getInt(1));
                ord.setFechaPedido(rset.getTimestamp(2));
                ord.setFechaEntrega(rset.getTimestamp(3));
                ord.setNumPaquetes(rset.getInt(4));
                ord.setEstado(rset.getString(5));
                
                ord.setEstado(_idEstado);
                listaOrdenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaOrdenes;
    }
        
    
       public static int GetIdPedido(int _idVehiculo) throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT IDORDEN FROM ORDENDEENTREGAXVEHICULO WHERE IDVEHICULO = " + _idVehiculo;
        Connection con;
      
        int idPedido=0;
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            rset.next();
            OrdenEntrega ord= new OrdenEntrega();
            ord.setIdOrden(rset.getInt(1));
            idPedido=ord.getIdOrden();
        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return idPedido;
    }
    
    public static String reseteaIncremento(){
        
        String error="Exito";
        
        PreparedStatement pstmt1=null;        
        StringBuffer query1= new StringBuffer();
        
        
        
        query1.append("ALTER TABLE ORDENDEENTREGA AUTO_INCREMENT = 1 ");
                
        
        
        try{
        
            Connection con =GestorConexion.getConexion();
                        
            pstmt1= con.prepareStatement(query1.toString());
                        
            pstmt1.execute();
            
        }catch(Exception e){
            
            error= e.getMessage();
        }finally{
            
            try{
            
            
            if (pstmt1!=null)pstmt1.close();

            
            }catch(Exception ex){
                
                error= ex.getMessage();
                
            }
            
            return error;
        }
              
    }
    

    public static ArrayList<OrdenEntrega> SeleccionarOrden(int _idCliente, Date fini, Date ffin) throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String query = "SELECT IDORDEN, HORAPEDIDO, HORAENTREGA FROM ORDENDEENTREGA WHERE IDPERSONA = " + _idCliente + " AND HORAPEDIDO BETWEEN '" + sdf.format(fini) + "' AND '" +sdf.format(ffin)+ "'";
        System.out.println(query);
        Connection con;
        ArrayList<OrdenEntrega> listaOrdenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord= new OrdenEntrega();
                ord.setIdOrden(rset.getInt(1));
                ord.setFechaPedido(rset.getTimestamp(2));
                ord.setFechaEntrega(rset.getTimestamp(3));
                listaOrdenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaOrdenes;
    }
    
    public static ArrayList<OrdenEntrega> SeleccionarTotalOrdenes() throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT IDORDEN, HORAPEDIDO, HORAENTREGA FROM ORDENDEENTREGA LIMIT 1,30";
        System.out.println(query);
        Connection con;
        ArrayList<OrdenEntrega> listaOrdenes = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            while(rset.next()){
                OrdenEntrega ord= new OrdenEntrega();
                ord.setIdOrden(rset.getInt(1));
                ord.setFechaPedido(rset.getTimestamp(2));
                ord.setFechaEntrega(rset.getTimestamp(3));
                listaOrdenes.add(ord);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
        
        return listaOrdenes;
    }
    
    public Date getFechaMaxEntrega() {
        return _fechaMaxEntrega;
    }

    public void setFechaMaxEntrega(Date fechaMaxEntrega) {
        this._fechaMaxEntrega = fechaMaxEntrega;
    }
    
    
    public OrdenEntrega copiar(){
        
        OrdenEntrega copia= new OrdenEntrega();
        copia._fechaMaxEntrega=this._fechaEntrega;
        copia._fechaMaxEntrega=this._fechaMaxEntrega;
        copia._horaPedido= this._horaPedido;
        copia._idOrden= this._idOrden;
        copia._numPaquetes=this._numPaquetes;
        copia._puntoEntrega=this._puntoEntrega;
        copia._ubicacion= this._ubicacion;
        copia.estado= this.estado;
               
        return copia;
    }
    
    public void registraEntregaIncompleta(int idVehiculo, int minIdOrdenSimulacion, int numPaquetes, Reloj reloj, int codSimulacion, int codChofer){
        
        PreparedStatement pstmt=null;
        
        
        StringBuffer query=new StringBuffer("");
        
        query.append(" INSERT INTO ORDENDEENTREGAXVEHICULO (idORDEN, idVEHICULO, cantPaquetes, fechaEntrega, idSimulacion, idChofer) ");
        query.append(" values (? , ? , ? , ? , ?, ?) ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setInt(1, this._idOrdenBD);
            pstmt.setInt(2, idVehiculo);
            pstmt.setInt(3, numPaquetes);
            pstmt.setString(4, reloj.getFechaString("yyyy-MM-dd HH:mm:ss"));
            pstmt.setInt(5, codSimulacion);
            pstmt.setInt(6, codChofer);
            pstmt.executeUpdate();
            
            
            
        }catch(Exception e){
            System.out.println(e.getMessage() + " " + this._idOrdenBD);
        
        }finally{
            try{
                if (pstmt!=null)pstmt.close();
        
                
            }catch(Exception ex){
                
                System.out.println(ex.getMessage());
            }
            
        }
        
    }
    
    public void registraEntregaCompleta(int idVehiculo, int minIdOrdenSimulacion, int numPaquetes, Reloj reloj, int codSimulacion, int codChofer){
        
        PreparedStatement pstmt=null;
        PreparedStatement pstmt2=null;
        
        
        StringBuffer query=new StringBuffer("");
        StringBuffer query2=new StringBuffer("");
        
        query.append(" INSERT INTO ORDENDEENTREGAXVEHICULO (idORDEN, idVEHICULO, cantPaquetes, fechaEntrega, idSimulacion, idChofer) ");
        query.append(" values (? , ? , ? , ? , ?, ? ) ");
        
        query2.append(" UPDATE ORDENDEENTREGA SET estado='Entregado', horaEntrega= ? ");
        query2.append(" WHERE idORDEN= ? and idSIMULACION= ? ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt= con.prepareStatement(query.toString());
            pstmt2= con.prepareStatement(query2.toString());
            
            String fecha=reloj.getFechaString("yyyy-MM-dd H:mm:ss");
            
            pstmt.setInt(1, this._idOrdenBD);
            pstmt.setInt(2, idVehiculo);
            pstmt.setInt(3, numPaquetes);
            pstmt.setString(4,fecha );
            pstmt.setInt(5, codSimulacion);
            pstmt.setInt(6, codChofer);
                    
            pstmt2.setString(1, fecha);
            pstmt2.setInt(2, this._idOrdenBD);
            pstmt2.setInt(3, codSimulacion);
            
            pstmt2.executeUpdate();
            pstmt.executeUpdate();
            
            
            
        }catch(Exception e){
            System.out.println(e.getMessage() + " " + (this._idOrdenBD));
        
        }finally{
            try{
                if (pstmt!=null)pstmt.close();
                if (pstmt2!=null)pstmt2.close();
                
            }catch(Exception ex){
                
                System.out.println(ex.getMessage() + " " + (this._idOrden + minIdOrdenSimulacion));
            }
            
        }
        
    }

    /**
     * @return the _idOrdenBD
     */
    public int getIdOrdenBD() {
        return _idOrdenBD;
    }

    /**
     * @param idOrdenBD the _idOrdenBD to set
     */
    public void setIdOrdenBD(int idOrdenBD) {
        this._idOrdenBD = idOrdenBD;
    }
    
    
    public static void obtenCodBD(ArrayList<OrdenEntrega>ordenes, int codSimulacion){
        
        if (codSimulacion>=0){
            
            if (ordenes!=null && ordenes.size()>0){
                
                int num= minIdOrdenFechaSimulacion(ordenes.get(0)._fechaPedido , codSimulacion);
                
                int tam=ordenes.size();
                
                for (int i=0; i<tam; i++){
                    System.out.println(num);
                    ordenes.get(i)._idOrdenBD=num+i;
                }
                
            }
            
        }
        
    }
    
    
    public static int minIdOrdenFechaSimulacion(Date fechaPedido, int codSimulacion){
        
        int minId=0;
        
        PreparedStatement pstmt=null;
        ResultSet rset=null;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        StringBuffer query=new StringBuffer("");
        
        query.append(" select IFNULL(MIN(IDORDEN),-1) FROM ORDENDEENTREGA WHERE ");
        query.append(" IDSIMULACION=? AND HORAPEDIDO>=? ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setInt(1, codSimulacion);
            pstmt.setString(2, (sdf.format(fechaPedido) + " 00:00:00"));
            
            rset=pstmt.executeQuery();
            
            if (rset.next()){
                
                minId=rset.getInt(1);
            }
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        
        }finally{
            try{
                if (pstmt!=null)pstmt.close();
                if (rset!=null)rset.close();
                
            }catch(Exception ex){
                
                System.out.println(ex.getMessage());
            }
            
        }
        
        
        return minId;
        
    }

    /**
     * @return the fusion
     */
    public String getFusion() {
        return fusion;
    }

    /**
     * @param fusion the fusion to set
     */
    public void setFusion(String fusion) {
        this.fusion = fusion;
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

    /**
     * @return the fechaPronostico
     */
    public Date getFechaPronostico() {
        return fechaPronostico;
    }

    /**
     * @param fechaPronostico the fechaPronostico to set
     */
    public void setFechaPronostico(Date fechaPronostico) {
        this.fechaPronostico = fechaPronostico;
    }

    /**
     * @return the numPaquetesExtra
     */
    public int getNumPaquetesExtra() {
        return numPaquetesExtra;
    }

    /**
     * @param numPaquetesExtra the numPaquetesExtra to set
     */
    public void setNumPaquetesExtra(int numPaquetesExtra) {
        this.numPaquetesExtra = numPaquetesExtra;
    }

    /**
     * @return the idPronostico
     */
 



    
}
