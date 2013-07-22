/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pronosticafallo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Adrian
 */
public class CuartodeTurnoPronostico {

    
 
    private int _numpedidos;
    private int _numpaquetes;
    private HashMap _prioridadxnumpedidos=null; // Cuantas veces se repite dicha prioridad en el cuarto
    private static ArrayList<Double>  _factorTamaño;
    private static ArrayList<HashMap> _factorPrioridad=null;
    
    
    /**
     * @return the _factorPrioridad
     */
    public static ArrayList<HashMap> getFactorPrioridad() {
        return _factorPrioridad;
    }

    /**
     * @param aFactorPrioridad the _factorPrioridad to set
     */
    public static void setFactorPrioridad(ArrayList<HashMap> aFactorPrioridad) {
        _factorPrioridad = aFactorPrioridad;
    }
    
    /**
     * @return the _factorTamaño
     */
    public static ArrayList<Double> getFactorTamaño() {
        return _factorTamaño;
    }

    /**
     * @param aFactorTamaño the _factorTamaño to set
     */
    public static void setFactorTamaño(ArrayList<Double> aFactorTamaño) {
        _factorTamaño = aFactorTamaño;
    }

    /**
     * @return the _numpedidos
     */
    public int getNumpedidos() {
        return _numpedidos;
    }

    /**
     * @param numpedidos the _numpedidos to set
     */
    public void setNumpedidos(int numpedidos) {
        this._numpedidos = numpedidos;
    }

    /**
     * @return the _numpaquetes
     */
    public int getNumpaquetes() {
        return _numpaquetes;
    }

    /**
     * @param numpaquetes the _numpaquetes to set
     */
    public void setNumpaquetes(int numpaquetes) {
        this._numpaquetes = numpaquetes;
    }

    /**
     * @return the _priridadxnumpedidos
     */
    public HashMap getPrioridadxnumpedidos() {
        return _prioridadxnumpedidos;
    }

    /**
     * @param priridadxnumpedidos the _priridadxnumpedidos to set
     */
    public void setPrioridadxnumpedidos(HashMap prioridadxnumpedidos) {
        this._prioridadxnumpedidos = prioridadxnumpedidos;
    }
    
    
    
    public void agregaPrioridadxnumpedidos(int prioridad, int numpedidos ){
        
        if (this._prioridadxnumpedidos==null) this._prioridadxnumpedidos= new HashMap();
        
        this._prioridadxnumpedidos.put(prioridad, numpedidos);
        
    }
    
    public int obtenerMediaDistanciaxHora(int prioridad){
        
        if (_prioridadxnumpedidos==null) return 0;
        else
            return (int)_prioridadxnumpedidos.get(prioridad) ;
    }
    
    
}
