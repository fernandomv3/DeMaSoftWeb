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
public class TurnoPronostico {

    /**
     * @return the exponenciales
     */
    public static ArrayList<Double> getExponenciales() {
        return exponenciales;
    }

    /**
     * @param aExponenciales the exponenciales to set
     */
    public static void setExponenciales(ArrayList<Double> aExponenciales) {
        exponenciales = aExponenciales;
    }

    
    private int _numpaquetesTurno;
    private ArrayList<Double> _factordeCuartoTurno= new ArrayList<>();
    private ArrayList<HashMap> _prioriadTurno= new ArrayList<>(); 
    private ArrayList<CuartodeTurnoPronostico>_datosCuartoTurno;
    private static ArrayList<Double> _constante;
    private static ArrayList<Double> _exponente;
    private static ArrayList<ArrayList<Double>> _polinomioPaquetes;
    private static ArrayList<Double> exponenciales;
    
    
    /**
     * @return the _polinomioPaquetes
     */
    public static ArrayList<ArrayList<Double>> getPolinomioPaquetes() {
        return _polinomioPaquetes;
    }

    /**
     * @param aPolinomioPaquetes the _polinomioPaquetes to set
     */
    public static void setPolinomioPaquetes(ArrayList<ArrayList<Double>> aPolinomioPaquetes) {
        _polinomioPaquetes = aPolinomioPaquetes;
    }
           
    
    public void procesaDatos(){      
      for(int i=0;i<4;i++){
          int nPaquete=_datosCuartoTurno.get(i).getNumpaquetes();
          _numpaquetesTurno+=nPaquete;
          
      }
      
      for(int i=0;i<4;i++){
          int nPaquete=_datosCuartoTurno.get(i).getNumpaquetes();
          int nPedidos=_datosCuartoTurno.get(i).getNumpedidos() ;
          HashMap _prioriadCuartoTurnoTemp= _datosCuartoTurno.get(i).getPrioridadxnumpedidos();
          _factordeCuartoTurno.add( (double)((double)nPaquete/(double)_numpaquetesTurno));
          
          for(int j=0; j<=24;j++){
              
              if (_prioriadCuartoTurnoTemp.containsKey(j)){
                  
                  HashMap _prioriadTurnoTemp= new HashMap();
                  if (nPedidos>0) _prioriadTurnoTemp.put(j,(double)_prioriadCuartoTurnoTemp.get(j)/nPedidos );
                  
              }
              
          }
          
          _prioriadTurno.add(_prioriadCuartoTurnoTemp);

      }
                
    }
    
    
    
    /**
     * @return the constante
     */
    public static ArrayList<Double> getConstante() {
        return _constante;
    }

    /**
     * @param aConstante the constante to set
     */
    public static void setConstante(ArrayList<Double> aConstante) {
        _constante = aConstante;
    }

    /**
     * @return the exponente
     */
    public static ArrayList<Double> getExponente() {
        return _exponente;
    }

    /**
     * @param aExponente the exponente to set
     */
    public static void setExponente(ArrayList<Double> aExponente) {
        _exponente = aExponente;
    }
    
    

    /**
     * @return the numpaquetesTurno
     */
    public int getNumpaquetesTurno() {
        return _numpaquetesTurno;
    }

    /**
     * @param numpaquetesTurno the numpaquetesTurno to set
     */
    public void setNumpaquetesTurno(int numpaquetesTurno) {
        this._numpaquetesTurno = numpaquetesTurno;
    }

    /**
     * @return the datosCuartoTurno
     */
    public ArrayList<CuartodeTurnoPronostico> getDatosCuartoTurno() {
        return _datosCuartoTurno;
    }

    /**
     * @param datosCuartoTurno the datosCuartoTurno to set
     */
    public void setDatosCuartoTurno(ArrayList<CuartodeTurnoPronostico> datosCuartoTurno) {
        this._datosCuartoTurno = datosCuartoTurno;
    }
    
    public void agregaCuarto(CuartodeTurnoPronostico cuarto){
        
        if (this._datosCuartoTurno==null) this._datosCuartoTurno= new ArrayList<>();
        this._datosCuartoTurno.add(cuarto);
        
    }

    /**
     * @return the _factordeCuartoTurno
     */
    public ArrayList<Double> getFactordeCuartoTurno() {
        return _factordeCuartoTurno;
    }

    /**
     * @param factordeCuartoTurno the _factordeCuartoTurno to set
     */
    public void setFactordeCuartoTurno(ArrayList<Double> factordeCuartoTurno) {
        this._factordeCuartoTurno = factordeCuartoTurno;
    }

    /**
     * @return the _prioriadTurno
     */
    public ArrayList<HashMap> getPrioriadTurno() {
        return _prioriadTurno;
    }

    /**
     * @param prioriadTurno the _prioriadTurno to set
     */
    public void setPrioriadTurno(ArrayList<HashMap> prioriadTurno) {
        this._prioriadTurno = prioriadTurno;
    }
    
}
