/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.*;

/**
 *
 * @author Marce
 */
public class Cliente extends Persona{
    private ArrayList<OrdenEntrega> _listaordenes;

    public ArrayList<OrdenEntrega> getListaordenes() {
        return _listaordenes;
    }

    public void setListaordenes(ArrayList<OrdenEntrega> listaordenes) {
        this._listaordenes = listaordenes;
    }
    
    public static void insertaCliente(int idPersona, String apellidoPaterno, String apellidoMaterno, String nombre, String tipoDocumento,String email, String telefono, String fechaNacimiento, String idTurno, int idSimulacion)throws Exception{
        Persona.insertaPersona(idPersona, apellidoPaterno, apellidoMaterno, nombre, tipoDocumento, email, telefono, fechaNacimiento, "TCLIENTE", idTurno,idSimulacion);
        Persona.seleccionaPersona(idPersona, apellidoPaterno, apellidoMaterno, nombre, tipoDocumento, email, telefono, fechaNacimiento, "TCLIENTE", idTurno);
        Persona.getPersonaById(idPersona);
    }       
    
}
