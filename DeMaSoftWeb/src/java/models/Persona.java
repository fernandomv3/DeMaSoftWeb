/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import java.util.ArrayList;
import java.sql.*;
import java.sql.Connection;
import java.util.Date;
import DeMaSoft.GestorConexion;

/**
 *
 * @author Marce
 */
public class Persona {
    private int _idPersona;
    private String _apellidoPaterno;
    private String _apellidoMaterno;
    private String _nombres;
    private String _tipoDocumento;
    private String _email;
    private String _telefono;
    private TipoPersona _tipoPersona;
    private Usuario _usuario;
    
    private Date _fechaNacimiento;

    public int getIdPersona() {
        return _idPersona;
    }

    public void setIdPersona(int idPersona) {
        this._idPersona = idPersona;
    }

    public String getApellidoPaterno() {
        return _apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this._apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return _apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this._apellidoMaterno = apellidoMaterno;
    }

    public String getNombres() {
        return _nombres;
    }

    public void setNombres(String nombres) {
        this._nombres = nombres;
    }

    public String getTipoDocumento() {
        return _tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this._tipoDocumento = tipoDocumento;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public String getTelefono() {
        return _telefono;
    }

    public void setTelefono(String telefono) {
        this._telefono = telefono;
    }

    public Date getFechaNacimiento() {
        return _fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this._fechaNacimiento = fechaNacimiento;
    }

    public TipoPersona getTipoPersona() {
        return _tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this._tipoPersona = tipoPersona;
    }

    public Usuario getUsuario() {
        return _usuario;
    }

    public void setUsuario(Usuario usuario) {
        this._usuario = usuario;
    }
    
    
    //Conexion a BD
    
    //insertar nueva persona
    
    public static void insertaPersona(int idPersona, String apellidoPaterno, String apellidoMaterno, String nombre, String tipoDocumento,String email, String telefono, String fechaNacimiento,String idTipoPersona, String idTurno, int idSimulacion )throws Exception{
        
        Persona persona= getPersonaById(idPersona);
        if (persona==null){
        
            if (idTurno.equals("")) idTurno="00000000";

            PreparedStatement pstmt = null;
            ResultSet rset = null;
            StringBuffer query = new StringBuffer();

            query.append(" INSERT INTO PERSONA(IDPERSONA,APELLIDOPATERNO,APELLIDOMATERNO,NOMBRES,TIPODOCUMENTO,EMAIL,TELEFONO,FECHANACIMIENTO,IDTIPOPERSONA,IDTURNO,IDSIMULACION) ");
            query.append(" VALUES(?,?,?,?,?,?,?,STR_TO_DATE(?, '%d-%m-%Y'),?,?,?) ");


            Connection con= GestorConexion.getConexion() ;

            pstmt = con.prepareStatement(query.toString());

            String strIdpersona=String.valueOf(idPersona);
            for (;strIdpersona.length()<8;) strIdpersona="0" + strIdpersona;

            pstmt.setInt(1, idPersona);
            pstmt.setString(2, apellidoPaterno);
            pstmt.setString(3, apellidoMaterno);
            pstmt.setString(4, nombre);
            pstmt.setString(5, tipoDocumento);
            pstmt.setString(6, email);
            pstmt.setString(7, telefono);
            if (fechaNacimiento.equals(""))fechaNacimiento="01-01-2000";
            pstmt.setString(8, fechaNacimiento);
            pstmt.setString(9, idTipoPersona);        
            pstmt.setString(10, idTurno);  
            pstmt.setInt(11, idSimulacion);

            try{
                pstmt.executeUpdate();
            }catch(Exception e){
                con.rollback();
                throw e;    
            }finally{
                if (pstmt!=null) pstmt.close();
                if (rset!=null) rset.close();

            }
        }
    }
    
    
    public static ArrayList<Persona> seleccionaPersona(int idPersona, String apellidoPaterno, String apellidoMaterno, String nombre, String tipoDocumento,String email, String telefono, String fechaNacimiento,String idTipoPersona, String idTurno )throws Exception{
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        ArrayList<Persona> personas= new ArrayList();
        int previo=0;
        
        query.append(" SELECT IDPERSONA,APELLIDOPATERNO,APELLIDOMATERNO,NOMBRES,TIPODOCUMENTO,EMAIL,TELEFONO,FECHANACIMIENTO,IDTIPOPERSONA,IDTURNO ");
        query.append(" FROM PERSONA WHERE ");
        
        if (idPersona!=-1){ 
            query.append(" IDPERSONA=? ");
            previo=1;
        }
        
        if (!apellidoPaterno.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" APELLIDOPATERNO=? ");
            previo=1;
        }
        
        if (!apellidoMaterno.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" APELLIDOMATERNO=? ");
            previo=1;
        }
        
        if (!nombre.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" NOMBRES=? ");
            previo=1;
        }
        
        if (!tipoDocumento.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" TIPODOCUMENTO=? ");
            previo=1;
        }
        
        if (!email.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" EMAIL=? ");
            previo=1;
        }
        
        if (!telefono.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" TELEFONO=? ");
            previo=1;
        }
                        
        if (!idTipoPersona.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" IDTIPOPERSONA=? ");
            previo=1;
        }
        if (!idTurno.equals("")){
            if (previo==1) query.append(" AND ");
            query.append(" IDTURNO=? ");
            previo=1;
        }
                
        
        
        Connection con= GestorConexion.getConexion() ;
            
        pstmt = con.prepareStatement(query.toString());
        
        int contador=0;
            
        String strIdpersona=String.valueOf(idPersona);
        for (;strIdpersona.length()<8;) strIdpersona="0" + strIdpersona;
        
        if (idPersona!=-1){ 
            pstmt.setString(++contador, strIdpersona);
            
        }
        
        if (!apellidoPaterno.equals("")){
            pstmt.setString(++contador, apellidoPaterno);
            
        }
        
        if (!apellidoMaterno.equals("")){
            pstmt.setString(++contador, apellidoMaterno);
            
        }
        
        if (!nombre.equals("")){
            pstmt.setString(++contador, nombre);
            
        }
        
        if (!tipoDocumento.equals("")){
            pstmt.setString(++contador, tipoDocumento);
            
        }
        
        if (!email.equals("")){
            pstmt.setString(++contador, email);
            
        }
        
        if (!telefono.equals("")){
            pstmt.setString(++contador, telefono);            
            
        }
                        
        if (!idTipoPersona.equals("")){
            pstmt.setString(++contador, idTipoPersona);
            
        }
        
        if (!idTurno.equals("")){
            pstmt.setString(++contador, idTurno);
        }

            
        try{
            rset=pstmt.executeQuery();
            
            while (rset.next()){
                
                Persona persona=new Persona();
                persona.setIdPersona(rset.getInt("IDPERSONA"));
                persona.setApellidoPaterno(rset.getString("APELLIDOPATERNO"));
                persona.setApellidoMaterno(rset.getString("APELLIDOMATERNO"));
                persona.setNombres(rset.getString("NOMBRES"));
                persona.setTipoDocumento(rset.getString("TIPODOCUMENTO"));
                persona.setEmail(rset.getString("EMAIL"));
                persona.setTelefono(rset.getString("TELEFONO"));                
                persona.setFechaNacimiento(rset.getDate("FECHANACIMIENTO"));
                
                personas.add(persona);
            }
            
            
        }catch(Exception e){
            con.rollback();
            throw e;    
        }finally{
            if (pstmt!=null) pstmt.close();
            if (rset!=null) rset.close();
                
        }
        
        return personas;
        
    }
    
    public static Persona getPersonaById(int idPersona)throws Exception{
        
        Persona persona= null;
        
        String tempId= String.valueOf(idPersona);
        
        for (;tempId.length()<8 ;)tempId="0" + tempId;
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();
        
        query.append(" SELECT IDPERSONA,APELLIDOPATERNO,APELLIDOMATERNO,NOMBRES,TIPODOCUMENTO,EMAIL,TELEFONO,FECHANACIMIENTO,IDTIPOPERSONA,IDTURNO ");
        query.append(" FROM PERSONA ");
        query.append(" WHERE IDPERSONA = ? ");
        
        
        
        
        Connection con= GestorConexion.getConexion();
        
        pstmt= con.prepareStatement(query.toString());
        
        
        pstmt.setInt(1,idPersona);
        
        
        try{
            
            rset=pstmt.executeQuery();
            
            if (rset.next()){
            
                persona= new Persona();
                persona.setIdPersona(rset.getInt("IDPERSONA"));
                persona.setApellidoPaterno(rset.getString("APELLIDOPATERNO"));
                persona.setApellidoMaterno(rset.getString("APELLIDOMATERNO"));
                persona.setNombres(rset.getString("NOMBRES"));
                persona.setTipoDocumento(rset.getString("TIPODOCUMENTO"));
                persona.setEmail(rset.getString("EMAIL"));
                persona.setTelefono(rset.getString("TELEFONO"));                
                if (rset.getDate("FECHANACIMIENTO")!=null) persona.setFechaNacimiento(rset.getDate("FECHANACIMIENTO"));
            }
        }catch(Exception e){
            
            con.rollback();
            throw e;
        }
        
        return persona;
    }
    
    /*
    public static void main(String[] args){
        try{
         Persona.insertaPersona(22, "", "", "", "","", "", "","TCLIENTE", "" );
         ArrayList<Persona >personas=Persona.seleccionaPersona(-1, "", "", "", "", "", "", "", "TCLIENTE", "");
         
         System.out.println(personas.size());
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }
    }*/
    
    
}
