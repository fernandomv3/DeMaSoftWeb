/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import DeMaSoft.GestorConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
 * @author Marce
 */
public class Chofer extends Empleado {
    private Turno _turno;
    private Recorrido _recorrido;
    private boolean _estaAlmorzando;
    private boolean _yaAlmorzo;
    private Date _horaAlmuerzo;
    
    public Chofer(){
        this._turno=null;
        this._recorrido=null;
        this._estaAlmorzando=false;
        this._yaAlmorzo=false;
        this._horaAlmuerzo=null;
    }

    public Turno getTurno() {
        return _turno;
    }

    public void setTurno(Turno turno) {
        this._turno = turno;
    }

    public Recorrido getRecorrido() {
        return _recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this._recorrido = recorrido;
    }

    public boolean isEstaAlmorzando() {
        return _estaAlmorzando;
    }

    public void setEstaAlmorzando(boolean estaAlmorzando) {
        this._estaAlmorzando = estaAlmorzando;
    }

    public boolean isYaAlmorzo() {
        return _yaAlmorzo;
    }

    public void setYaAlmorzo(boolean yaAlmorzo) {
        this._yaAlmorzo = yaAlmorzo;
    }

    public Date getHoraAlmuerzo() {
        return _horaAlmuerzo;
    }

    public void setHoraAlmuerzo(Date horaAlmuerzo) {
        this._horaAlmuerzo = horaAlmuerzo;
    }
    
    public static int maxChofer(){
        
        int max=0;
        
        PreparedStatement pstmt=null;
        
        ResultSet rset= null;
       
        StringBuffer query= new StringBuffer();
        
        
        query.append("select IFNULL(MAX(idPersona),0) from PERSONA");
        
       
       try{
           
           Connection con =GestorConexion.getConexion();
           pstmt= con.prepareStatement(query.toString());
           
           rset=pstmt.executeQuery();
           
           if (rset.next()){
               
               max=rset.getInt(1);
           }
           
       }catch(Exception e){
           
           System.out.println(e.getMessage());
           
       }finally{
           try{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
           }catch(Exception e){
               
               System.out.println(e.getMessage());
           }
           
       }

        return max;
        
    }
    
    public void insertaChofer(){
        
        PreparedStatement pstmt=null;
                       
        StringBuffer query= new StringBuffer();
        
        
        query.append(" INSERT INTO PERSONA(`idPERSONA`,`idTIPOPERSONA`,`idTURNO`) ");
        query.append(" VALUES (?, 'TCHOFERS',  ?) ");
        
       
       try{
           
           Connection con =GestorConexion.getConexion();
           pstmt= con.prepareStatement(query.toString());
           pstmt.setInt(1, this.getIdPersona());
           String turno="";
           if (this._turno.getIdTurno()==0){
               
               turno="00000001";
           }
           
           if (this._turno.getIdTurno()==1){
               
               turno="00000002";
           }
           
           if (this._turno.getIdTurno()==2){
               
               turno="00000003";
           }
           
           pstmt.setString(2, turno);
           pstmt.executeUpdate();
           
           
           
       }catch(Exception e){
           
           System.out.println(e.getMessage());
           
       }finally{
           try{
            if (pstmt!=null) pstmt.close();
            
           }catch(Exception e){
               
               System.out.println(e.getMessage());
           }
           
       }
    }
}
