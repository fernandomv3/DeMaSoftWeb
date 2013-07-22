/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Cliente;
import models.Persona;

/**
 *
 * @author Adrian
 */
public class GestorConexion {
    private static Connection conexion=null;
    
    public static Connection getConexion() throws Exception{   
        if (conexion==null || conexion.isClosed()){
            
            try{
            
            Class.forName("com.mysql.jdbc.Driver");            
            conexion= DriverManager.getConnection("jdbc:mysql://localhost:3306/inf2260981g2?user=inf2260981g2dba&password=intercambio");
            }catch(ClassNotFoundException | SQLException ex){ 
                throw ex;
            }
        }        
        
        
        return conexion;
    }
    
    public static void main(String[] args){//para pruebas
        String query= "SELECT \"holi\" FROM DUAL";  
        try {
            GestorConexion.getConexion();
            /*for (int x=1; x<1000;x++){
                try{
                Cliente.insertaCliente(x, "", "", "", "", "", "", "", "", x);
                }catch(Exception e){
                    
                    System.out.println(e.getMessage());
                }
            
            }*/
        } catch (Exception ex) {
            Logger.getLogger(GestorConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
