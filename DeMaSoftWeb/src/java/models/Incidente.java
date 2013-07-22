/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import DeMaSoft.GestorConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 *
 * @author Marce
 */
public class Incidente {

    /**
     * @return the maxIDIncidente
     */
    public static int getMaxIDIncidente() {
        return maxIDIncidente;
    }

    /**
     * @param aMaxIDIncidente the maxIDIncidente to set
     */
    public static void setMaxIDIncidente(int aMaxIDIncidente) {
        maxIDIncidente = aMaxIDIncidente;
    }
    private int _idIncidente;
    private Vehicle _vehiculo;
    private Date _hora;
    private String _descripcion;
    private String Fecha;
    private Recorrido recorrido;
    private static int maxIDIncidente;
    private String idVehiculo;
    private Date fechaInc;

    public int getIdIncidente() {
        return _idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this._idIncidente = idIncidente;
    }

    public Date getHora() {
        return _hora;
    }

    public void setHora(Date hora) {
        this._hora = hora;
    }

    public String getDescripcion() {
        return _descripcion;
    }

    public void setDescripcion(String descripcion) {
        this._descripcion = descripcion;
    }
        
    
    //insertar nuevo incidente
    
    public static void insertaIncidente(int _idIncidente, String fecha, int idrecorrido, int idVehiculo, int idSimulacion){
        
        PreparedStatement pstmt = null;	
	StringBuffer query = new StringBuffer();

        query.append(" INSERT INTO INCIDENTE(IDINCIDENTE,IDRECORRIDO,FECHA, idVEHICULO,idSimulacion) ");
        query.append(" VALUES(?,?,?,?,?) ");
        
        try{
            Connection con= GestorConexion.getConexion() ;

            pstmt = con.prepareStatement(query.toString());

            pstmt.setInt(1, _idIncidente);
            pstmt.setInt(2, idrecorrido);
            pstmt.setString(3, fecha);
            pstmt.setInt(4, idVehiculo);
            pstmt.setInt(5, idSimulacion);
        
            
        
            pstmt.executeUpdate();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally{
            try{
                if (pstmt!=null) pstmt.close();
            }catch(Exception ex){
                
                System.out.println(ex.getMessage());
            }
                
        }
    }
    
    
    public static ArrayList<Incidente> seleccionaIncidente(int idIncidente, String fechaMin,String fechaMax, int idrecorrido, int idVehiculo, int idSimulacion)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        ArrayList<Incidente> incidentes= new ArrayList();
        int previo=0;
        
        query.append(" SELECT IDINCIDENTE,IDRECORRIDO,FECHA, idVEHICULO,idSimulacion ");
        query.append(" FROM INCIDENTE WHERE ");
        
        if (idIncidente!=-1){ 
            query.append(" IDINCIDENTE=? ");
            previo=1;
        }
        
        if (!fechaMin.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" FECHA>? ");
            previo=1;
        }
        
        if (!fechaMax.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" FECHA<? ");
            previo=1;
        }
        
        if (idrecorrido!=-1){ 
            if (previo==1) query.append(" AND ");
            query.append(" IDRECORRIDO=? ");
            previo=1;
        }
        
        if (idVehiculo!=-1){ 
            if (previo==1) query.append(" AND ");
            query.append(" idVEHICULO=? ");
            previo=1;
        }
        
        if (idSimulacion!=-1){ 
            if (previo==1) query.append(" AND ");
            query.append(" idSimulacion=? ");
            previo=1;
        }
        
 
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
        
        int contador=0;
            
        
        if (idIncidente!=-1){ 
            pstmt.setInt(++contador, idIncidente);
            
        }
        
        if (!fechaMin.equals("")){
            pstmt.setString(++contador, fechaMin);
            
        }
        
        if (!fechaMax.equals("")){
            pstmt.setString(++contador, fechaMax);
            
        }
        
        if (idVehiculo!=-1){ 
            pstmt.setInt(++contador, idIncidente);
            
        }
        
        if (idSimulacion!=-1){ 
            pstmt.setInt(++contador, idIncidente);
            
        }

            
        try{
            rset=pstmt.executeQuery();
            //IDINCIDENTE,IDRECORRIDO,FECHA, idVEHICULO,idSimulacion
            while (rset.next()){
                
                Incidente incidente=new Incidente();
                incidente.setIdIncidente(rset.getInt("IDINCIDENTE"));
                Vehicle v= new Vehicle(0,0);
                v.setIdVehiculo(rset.getInt("idVEHICULO"));
                incidente.setVehiculo(v);
                
                
                incidentes.add(incidente);
            }
            
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }
        
        return incidentes;
        
    }
/*    
    public static Incidente getIncidenteById(String idIncidente)throws Exception{
        
        Incidente incidente= null;
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        
        query.append(" SELECT IDINCIDENTE,HORA,DESCRIPCION ");
        query.append(" FROM INCIDENTE ");
        query.append(" WHERE IDINCIDENTE = ? ");
        
        Connection con= GestorConexion.getConexion();
        
        pstmt= con.prepareStatement(query.toString());
        
        pstmt.setString(1, idIncidente);
        
        
        try{
            
            rset=pstmt.executeQuery();
            
            if (rset.next()){
            
                incidente= new Incidente();
                incidente.setIdIncidente(rset.getInt("IDINCIDENTE"));
                incidente.setHora(rset.getDate("HORA"));
                incidente.setDescripcion(rset.getString("DESCRIPCION"));
            }
        }catch(Exception e){
            
            con.rollback();
            throw e;
        }
        
        return incidente;
    }
*/
    
    public void grabarIncidente(int codSimulacion){
        this._idIncidente= (Incidente.maxIDIncidente++);
        Incidente.insertaIncidente(_idIncidente, Fecha, 1, this._vehiculo.getIdVehiculo(), codSimulacion);
        
    }
    
    /**
     * @return the _vehiculo
     */
    public Vehicle getVehiculo() {
        return _vehiculo;
    }

    /**
     * @param vehiculo the _vehiculo to set
     */
    public void setVehiculo(Vehicle vehiculo) {
        this._vehiculo = vehiculo;
    }

    /**
     * @return the Fecha
     */
    public String getFecha() {
        return Fecha;
    }

    /**
     * @param Fecha the Fecha to set
     */
    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    /**
     * @return the recorrido
     */
    public Recorrido getRecorrido() {
        return recorrido;
    }

    /**
     * @param recorrido the recorrido to set
     */
    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }

    /**
     * @return the idVehiculo
     */
    public String getIdVehiculo() {
        return idVehiculo;
    }

    /**
     * @param idVehiculo the idVehiculo to set
     */
    public void setIdVehiculo(String idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    /**
     * @return the fechaInc
     */
    public Date getFechaInc() {
        return fechaInc;
    }

    /**
     * @param fechaInc the fechaInc to set
     */
    public void setFechaInc(Date fechaInc) {
        this.fechaInc = fechaInc;
    }
}
