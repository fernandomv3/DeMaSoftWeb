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
public class DiaPronostico {

    
    private ArrayList<TurnoPronostico> _datoTurnos;
    private ArrayList<Double> _factordeTurnos= new ArrayList<>();
    private ArrayList<HashMap> _factorPrioridadTurnos= new ArrayList<>();
    private static int _numTurnos;
    private int _dia;
    private int _mes;
    private int _anho;
    private int _numDia;
    
    public void procesaDatos(){
      
      ArrayList<Double> factordeTurnotemp= new ArrayList<>();  
      
      
      for(int i=0;i<_numTurnos;i++){
          _datoTurnos.get(i).procesaDatos();
          ArrayList<Double> turnos= _datoTurnos.get(i).getFactordeCuartoTurno();
          ArrayList<HashMap> prioridadTuro=_datoTurnos.get(i).getPrioriadTurno();
          int prueba=0;
          for(int j=0; j<4;j++){
              factordeTurnotemp.add(turnos.get(j));
                getFactorPrioridadTurnos().add(prioridadTuro.get(j));
          }
          
      }
        setFactordeTurnos(factordeTurnotemp);
                
    }
    
    
    /**
     * @return the numTurnos
     */
    public static int getNumTurnos() {
        return _numTurnos;
    }

    /**
     * @param aNumTurnos the numTurnos to set
     */
    public static void setNumTurnos(int aNumTurnos) {
        _numTurnos = aNumTurnos;
    }
 

    /**
     * @return the datoTurnos
     */
    public ArrayList<TurnoPronostico> getDatoTurnos() {
        return _datoTurnos;
    }

    /**
     * @param datoTurnos the datoTurnos to set
     */
    public void setDatoTurnos(ArrayList<TurnoPronostico> datoTurnos) {
        this._datoTurnos = datoTurnos;
    }

    /**
     * @return the dia
     */
    public int getDia() {
        return _dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        this._dia = dia;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return _mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this._mes = mes;
    }

    /**
     * @return the anho
     */
    public int getAnho() {
        return _anho;
    }

    /**
     * @param anho the anho to set
     */
    public void setAnho(int anho) {
        this._anho = anho;
    }

    /**
     * @return the numDia
     */
    public int getNumDia() {
        return _numDia;
    }

    /**
     * @param numDia the numDia to set
     */
    public void setNumDia(int numDia) {
        this._numDia = numDia;
    }
    
    public void agregaCuarto(TurnoPronostico turno){
        
        if (this._datoTurnos==null) this._datoTurnos= new ArrayList<>();
        this._datoTurnos.add(turno);
        
    }

    /**
     * @return the _factordeTurnos
     */
    public ArrayList<Double> getFactordeTurnos() {
        return _factordeTurnos;
    }

    /**
     * @param factordeTurnos the _factordeTurnos to set
     */
    public void setFactordeTurnos(ArrayList<Double> factordeTurnos) {
        this._factordeTurnos = factordeTurnos;
    }

    /**
     * @return the _factorPrioridadTurnos
     */
    public ArrayList<HashMap> getFactorPrioridadTurnos() {
        return _factorPrioridadTurnos;
    }

    /**
     * @param factorPrioridadTurnos the _factorPrioridadTurnos to set
     */
    public void setFactorPrioridadTurnos(ArrayList<HashMap> factorPrioridadTurnos) {
        this._factorPrioridadTurnos = factorPrioridadTurnos;
    }
    
    
}
