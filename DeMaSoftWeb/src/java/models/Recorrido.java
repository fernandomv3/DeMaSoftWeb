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
public class Recorrido {
    private int _idRecorrido;
    private Date _horaInicio;
    private Date _horaFin;
    private Date _fecha;
    private ArrayList<Ruta> _rutas;
    private ArrayList<Incidente> _incidentes;
    private Ruta _rutaActual;
    private Chofer _chofer;
    
    public Recorrido(){
        _rutas = new ArrayList<>();
        _incidentes = new ArrayList<>();
        _rutaActual = new Ruta();
    }

    public int getIdRecorrido() {
        return _idRecorrido;
    }

    public void setIdRecorrido(int idRecorrido) {
        this._idRecorrido = idRecorrido;
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

    public Date getFecha() {
        return _fecha;
    }

    public void setFecha(Date fecha) {
        this._fecha = fecha;
    }

    public ArrayList<Ruta> getRutas() {
        return _rutas;
    }

    public void setRutas(ArrayList<Ruta> rutas) {
        this._rutas = rutas;
    }

    public ArrayList<Incidente> getIncidentes() {
        return _incidentes;
    }

    public void setIncidentes(ArrayList<Incidente> incidentes) {
        this._incidentes = incidentes;
    }

    public Ruta getRutaActual() {
        return _rutaActual;
    }

    public void setRutaActual(Ruta rutaActual) {
        this._rutaActual = rutaActual;
    }

    public Chofer getChofer() {
        return _chofer; 
    }

    public void setChofer(Chofer chofer) {
        this._chofer = chofer;
    }
    
}
