/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author Marce
 */
public class Usuario {
    private String _nombreUsuario;
    private String _constrasenha;

    public String getNombreUsuario() {
        return _nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this._nombreUsuario = nombreUsuario;
    }

    public String getConstrasenha() {
        return _constrasenha;
    }

    public void setConstrasenha(String constrasenha) {
        this._constrasenha = constrasenha;
    }
}
