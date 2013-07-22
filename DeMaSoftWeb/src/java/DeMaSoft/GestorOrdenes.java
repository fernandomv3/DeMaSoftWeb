/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;
import java.util.ArrayList;
import java.util.Calendar;
import models.OrdenEntrega;
/**
 *
 * @author Fernando
 */
public class GestorOrdenes implements Runnable {    
    private boolean _detente =false;
    private Reloj _reloj;
    private ArrayList<OrdenEntrega> setOrd;
    
    private GestorOrdenes(ArrayList<OrdenEntrega> setOrd){
        
        this.setOrd= setOrd;
    }
        
    
    @Override
    public void run() {
        Calendar nextRevision = (Calendar)_reloj.getFechaActual().clone();
        nextRevision.add(Calendar.MINUTE, 30);
        while(true){
            if(_detente)break;
            if(nextRevision.compareTo(_reloj.getFechaActual()) > 0)continue;
            nextRevision.add(Calendar.HOUR, 1);
            ArrayList<OrdenEntrega> ordenes = setOrd;
            for(int i=0;i<ordenes.size();i++){
                
                ordenes.get(i).actualizaPrioridad(_reloj.getHoraActual(), _reloj.getMinutoActual());
            }
        }
        _detente =false;
    }
    
    public void actualizaOrdenes(){
        Thread hiloActualizador = new Thread(this);
        hiloActualizador.start();
    }
    
    public void detente(){
        _detente =true;
    }

    /**
     * @return the setOrd
     */
    public ArrayList<OrdenEntrega> getSetOrd() {
        return setOrd;
    }

    /**
     * @param setOrd the setOrd to set
     */
    public void setSetOrd(ArrayList<OrdenEntrega> setOrd) {
        this.setOrd = setOrd;
    }
}
