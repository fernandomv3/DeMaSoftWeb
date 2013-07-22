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
public class Ruta {
    private int _idRuta;
    private Date _hora;
    private ArrayList<Punto> _puntos;
    
    public Ruta(){
        _puntos =new ArrayList<>();
    }

    public int getIdRuta() {
        return _idRuta;
    }

    public void setIdRuta(int idRuta) {
        this._idRuta = idRuta;
    }

    public Date getHora() {
        return _hora;
    }

    public void setHora(Date hora) {
        this._hora = hora;
    }

    public ArrayList<Punto> getPuntos() {
        return _puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this._puntos = puntos;
    }
    
     /////////////////////////////////////////////////////
    /////////////////                  ///////////////////////
    //////////          O         O        //////////////////////////////
    ///                      3                      ////////////////
    ////////////////                               ////////////////
    ///////////////                          /////////////////////////
    ////////////////////////////////////////////
    
    
    public void insertaPuntos(ArrayList <Punto> puntos){
        for (Punto punto : puntos){
            this._puntos.add(punto);
        }
    }
    
    
    
     //Conexion a BD
    
    //insertar nueva ruta
    
    public static void insertaRuta(int _idRuta, String _hora, ArrayList<Punto> _puntos)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();

        query.append(" INSERT INTO RUTA(IDRUTA,HORA) ");
        query.append(" VALUES(?,STR_TO_DATE(?, '%d-%m-%Y')) ");
        
        
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
            
        pstmt.setInt(1, _idRuta);
        pstmt.setString(2, _hora);
            
        try{
            rset=pstmt.executeQuery();
        }catch(Exception e){
            con.rollback();
            throw e;    
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }
        for (int i=0; i < _puntos.size();i++){
            Punto p = _puntos.get(i);
            /*p.insertPuntosdeRuta(parametros--> idRuta, );*/
        }
    }
    
    
    public static ArrayList<Ruta> seleccionaRuta(int _idRuta, String _hora)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        ArrayList<Ruta> rutas= new ArrayList();
        int previo=0;
        
        query.append(" SELECT IDRUTA,HORA");
        query.append(" FROM RUTA WHERE ");
        
        if (_idRuta!=-1){ 
            query.append(" IDRUTA=? ");
            previo=1;
        }
        
        if (!_hora.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" HORA=? ");
            previo=1;
        }
        
 
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
        
        int contador=0;
            
        
        if (_idRuta!=-1){ 
            pstmt.setInt(++contador, _idRuta);
            
        }
        
        if (!_hora.equals("")){
            pstmt.setString(++contador, _hora);
            
        }

            
        try{
            rset=pstmt.executeQuery();
            
            while (rset.next()){
                
                Ruta ruta=new Ruta();
                ruta.setIdRuta(rset.getInt("IDRUTA"));
                ruta.setHora(rset.getDate("HORA"));
                
                rutas.add(ruta);
            }
            
            
        }catch(Exception e){
            con.rollback();
            throw e;    
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }
        
        return rutas;
        
    }
    
    public static Ruta getRutaById(String idRuta)throws Exception{
        
        Ruta ruta= null;
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        
        query.append(" SELECT IDRUTA,HORA ");
        query.append(" FROM RUTA ");
        query.append(" WHERE RUTA = ? ");
        
        Connection con= GestorConexion.getConexion();
        
        pstmt= con.prepareStatement(query.toString());
        
        pstmt.setString(1, idRuta);
        
        
        try{
            
            rset=pstmt.executeQuery();
            
            if (rset.next()){
            
                ruta = new Ruta();
                ruta.setIdRuta(rset.getInt("IDRUTA"));
                ruta.setHora(rset.getDate("HORA"));
            }
        }catch(Exception e){
            
            con.rollback();
            throw e;
        }
        
        return ruta;
    }
    
}
