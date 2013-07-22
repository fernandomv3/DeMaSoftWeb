/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import DeMaSoft.GestorConexion;
import DeMaSoft.ManejaDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import javax.servlet.ServletContext;
/**
 *
 * @author Fernando
 */
public class Mapa {
    private int _ancho;
    private int _alto;
    private ArrayList<Punto> _puntos;
    private ArrayList<Punto> bloqueados = new ArrayList<>();
    
    public void cargarBloqueos(ServletContext servlet){
        setBloqueados(ManejaDatos.leeBloqueos(servlet, this));
        for (int i = 0; i < getBloqueados().size(); i++) {
            getBloqueados().get(i).setDisponible(false);//setea en falso los nodos que esten bloqueados
        }
    }
    
    public Mapa(int ancho, int alto, ServletContext servlet){
        _ancho = ancho;
        _alto = alto;
        _puntos = new ArrayList<Punto>();
        for(int i= 0;i<ancho;i++){
            for(int j=0;j<alto;j++){ 
                Punto punto = new Punto(i,j);
                _puntos.add(punto);
            }
        }
        for(int n= 0;n<ancho*alto;n++){
            Punto p = _puntos.get(n);
            if(p.getPosY() != 0) p.getAdj().add(_puntos.get(n-1));
            
            if(p.getPosY() != alto -1)p.getAdj().add(_puntos.get(n+1));
            
            if(p.getPosX() != 0)p.getAdj().add(_puntos.get(n-alto));
            
            if(p.getPosX() != ancho-1)p.getAdj().add(_puntos.get(n+alto));
        }
        cargarBloqueos(servlet);
    }

    public ArrayList<Punto> getPuntos() {
        return _puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this._puntos = puntos;
    }

    public int getAncho() {
        return _ancho;
    }

    public void setAncho(int ancho) {
        this._ancho = ancho;
    }

    public int getAlto() {
        return _alto;
    }

    public void setAlto(int alto) {
        this._alto = alto;
    }
    
     //Conexion a BD
    
    //insertar nuevo mapa
    
    public static void insertaMapa(int _alto, int _ancho)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();

        query.append(" INSERT INTO MAPA(ALTO,ANCHO) ");
        query.append(" VALUES(?,?) ");
        
        
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
            
        pstmt.setInt(1, _alto);
        pstmt.setInt(2, _ancho);
            
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
    
    /*
    public static ArrayList<Mapa> seleccionaMapa(int _alto, int _ancho)throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        ArrayList<Mapa> mapas= new ArrayList();
        int previo=0;
        
        query.append(" SELECT ALTO, ANCHO ");
        query.append(" FROM MAPA WHERE ");
        
        if (_alto!=-1){ 
            query.append(" ALTO=? ");
            previo=1;
        }
        
        if (_ancho!=-1){ 
            query.append(" ANCHO=? ");
            previo=1;
        }
 
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
        
        int contador=0;
            
        
        if (_alto!=-1){ 
            pstmt.setInt(++contador, _alto);
            
        }
        
        if (_ancho!=-1){ 
            pstmt.setInt(++contador, _ancho);
        
        }

            
        try{
            rset=pstmt.executeQuery();
            
            while (rset.next()){
                
                Mapa mapa=new Mapa(_alto,_ancho);
                mapa.setAlto(rset.getInt("ALTO"));
                mapa.setAncho(rset.getInt("ANCHO"));
                
                mapas.add(mapa);
            }
            
            
        }catch(Exception e){
            con.rollback();
            throw e;    
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }
        
        return mapas;
        
    }*/

    public ArrayList<Punto> getBloqueados() {
        return bloqueados;
    }

    public void setBloqueados(ArrayList<Punto> bloqueados) {
        this.bloqueados = bloqueados;
    }
    

}
