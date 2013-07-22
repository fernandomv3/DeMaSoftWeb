/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import DeMaSoft.GestorConexion;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marce
 */
public class Asistencia {
    
    private int _idAsistencia=0;  
    private static Asistencia _instancia = null;
    private int _idSimulacion;
    private int _idChofer;
    private Timestamp _fechaEntrada;
    private Timestamp _fechaSalida;
    private Date _fechaSimulacion;
    
    private Asistencia()throws Exception{
        PreparedStatement pstmt = null;
	ResultSet rset = null;
        String query = "SELECT count(*) FROM ASISTENCIA";
        Connection con;
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset =pstmt.executeQuery();
            if(rset.next()){
                this._idAsistencia = rset.getInt(1)+1;
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }
    }
    
    public static Asistencia getInstancia() {
        if (_instancia == null) {
            try {
                _instancia = new Asistencia();
            } catch (Exception ex) {
                Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return _instancia;
    }
    
    public void insertaAsistencia(int _idAsistencia, int _idPersona, String _horaEntrada, String _horaSalida,int idSimulacion)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        String strIdAsistencia = String.valueOf(_idAsistencia);
        for (;strIdAsistencia.length()<8;) strIdAsistencia="0" + strIdAsistencia;
        String strIdPersona = String.valueOf(_idPersona);
         String strIdSimulacion = String.valueOf(idSimulacion);
        for (;strIdPersona.length()<8;) strIdPersona="0" + strIdPersona;
        query.append(" INSERT INTO ASISTENCIA(IDASISTENCIA, IDPERSONA, HORAENTRADA,HORASALIDA,IDSIMULACION) ");
        query.append(" VALUES(?,?,?,?,?) ");
       
        
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
            
        pstmt.setString(1, strIdAsistencia);
        pstmt.setString(2, strIdPersona);
        pstmt.setString(3, _horaEntrada);
        pstmt.setString(4, _horaSalida);
        pstmt.setInt(5, idSimulacion);
            
        try{
            pstmt.executeUpdate();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally{
            if (pstmt!=null) pstmt.close();
            this._idAsistencia = this._idAsistencia +1;
        }
    }

    public int getIdAsistencia() {
        return _idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this._idAsistencia = idAsistencia;
    }

    public int getIdChofer() {
        return _idChofer;
    }

    public void setIdChofer(int idChofer) {
        this._idChofer = idChofer;
    }
    
    public Timestamp getFechaEntrada() {
        return _fechaEntrada;
    }

    public void setFechaEntrada(Timestamp fechaEntrada) {
        this._fechaEntrada = fechaEntrada;
    }
    
    public Timestamp getFechaSalida() {
        return _fechaSalida;
    }

    public void setFechaSalida(Timestamp fechaSalida) {
        this._fechaSalida = fechaSalida;
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
    
    public static ArrayList<Asistencia> SeleccionarAsistencia(int _idChofer, Date fini, Date ffin) throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String query = "SELECT IDPERSONA, HORAENTRADA, HORASALIDA FROM ASISTENCIA WHERE IDPERSONA = '" + _idChofer + "' AND HORAENTRADA BETWEEN '" + sdf.format(fini) + "' AND '" +sdf.format(ffin)+ "'";
        System.out.println(query);
        Connection con;
        ArrayList<Asistencia> listaAsistencias = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset = pstmt.executeQuery();
            while(rset.next()){
                Asistencia asis = new Asistencia();
                asis.setIdChofer(rset.getInt(1));
                asis.setFechaEntrada(rset.getTimestamp(2));
                asis.setFechaSalida(rset.getTimestamp(3));
                listaAsistencias.add(asis);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }        
        return listaAsistencias;
    }
    
    public static ArrayList<Asistencia> SeleccionarTotalAsistencias() throws Exception{
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String query = "SELECT IDPERSONA, HORAENTRADA, HORASALIDA FROM ASISTENCIA LIMIT 1,30";
        System.out.println(query);
        Connection con;
        ArrayList<Asistencia> listaAsistencias = new ArrayList<>();
        try {
            con = GestorConexion.getConexion();
            pstmt = con.prepareStatement(query.toString());
            rset = pstmt.executeQuery();
            while(rset.next()){
                Asistencia asis = new Asistencia();
                asis.setIdChofer(rset.getInt(1));
                asis.setFechaEntrada(rset.getTimestamp(2));
                asis.setFechaSalida(rset.getTimestamp(3));
                listaAsistencias.add(asis);
            }

        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);

        }
        finally{
            if (pstmt!=null) pstmt.close();
            if (rset !=null) rset.close();
        }        
        return listaAsistencias;
    }
    
/*    public static void main(String[] args){
        try {
            Asistencia.getInstancia().insertaAsistencia(1,1,"03-02-2012 09:00:00","03-04-2012 13:00:00");
        } catch (Exception ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
