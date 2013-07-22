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
public class Turno {
    private int _idTurno;
    private Date _horaInicio;
    private Date _horaFin;
    private String _nombre;

    public int getIdTurno() {
        return _idTurno;
    }

    public void setIdTurno(int idTurno) {
        this._idTurno = idTurno;
    }

    public Date getHoraInicio() {
        return _horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this._horaInicio = horaInicio;
    }

    public Date getHoraFin() {
        return _horaFin;
    }

    public void setHoraFin(Date horaFin) {
        this._horaFin = horaFin;
    }

    public String getNombre() {
        return _nombre;
    }

    public void setNombre(String nombre) {
        this._nombre = nombre;
    }
}
