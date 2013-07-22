/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import DeMaSoft.GestorConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author Fernando
 */
public class Almacen {
    private String _direccion;
    private Punto _ubicacion;
    
    public Almacen(Punto punto){
        _ubicacion =punto;
    }

    public String getDireccion() {
        return _direccion;
    }

    public void setDireccion(String direccion) {
        this._direccion = direccion;
    }

    public Punto getUbicacion() {
        return _ubicacion;
    }

    public void setUbicacion(Punto ubicación) {
        this._ubicacion = ubicación;
    }
    
    public void grabarAlmacen(int codSimulacion){
        
        PreparedStatement pstmt= null;
        StringBuffer query = new StringBuffer("");
        
        query.append("INSERT INTO inf2260981g2.ALMACEN(idSIMULACION,posX,posY) VALUES (?,?,?) ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setInt(1, codSimulacion);
            pstmt.setInt(2, this._ubicacion.getPosX());
            pstmt.setInt(3, this._ubicacion.getPosY());
            pstmt.executeUpdate();
            
        }catch(Exception e){
            
            int a=0;
        }finally{
            try{
                
                if (pstmt!=null)pstmt.close();                
                
            }catch(Exception e){
                
                
            }
            
        }
    }
}
