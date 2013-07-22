/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DeMaSoft;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.text.SimpleDateFormat ;
import java.util.ArrayList;

/**
 *
 * @author Adrian
 */
public class Reloj implements Runnable{
    private boolean _cambioDia;
        
    private Calendar _fechaActual;
    private int _minutosXSegundo;//minutos SIMULADOS por segundo
    private boolean _detente;
    private boolean _pausa;
    
        
    public void pausa(){
        this._pausa=true;
    }
    public void sigue(){
        this._pausa=false;
    }
    
    @Override
    public void run(){
        while(!_detente){
            if(_pausa)continue;
            for(int i=1; i< getMinutosXSegundo();i++){
                if(_pausa)break;
                this.avanzaReloj();
                //System.out.println(this._fechaActual.get(Calendar.HOUR_OF_DAY)+":"+this._fechaActual.get(Calendar.MINUTE));
                try {
                    Thread.sleep(1000/getMinutosXSegundo());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Reloj.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void correrReloj(){
        Thread reloj = new Thread(this);
        this._detente=false;
        reloj.start();
    }
    
    public void detenerReloj(){
        this._detente=true;
    }
    
    public Reloj(){
        this._fechaActual = Calendar.getInstance();
        this._fechaActual.set(2013,1,1,0,0);
        _cambioDia=false;
        _minutosXSegundo = 90;
        _detente = false;
        _pausa=false;
        
    }
    
    public boolean esCambioDia(){
        
        return _cambioDia;
    }
    
    public void setReloj(int minuto, int hora, int dia, int mes, int anho){
        this._fechaActual.set(anho,mes-1,dia,hora,minuto);
    }
    
    public void saltaReloj(int minutos){
        this._fechaActual.add(Calendar.MINUTE,minutos);
    }
        
    public void avanzaReloj(){
        this._fechaActual.add(Calendar.MINUTE, 1);
        _cambioDia=false;
    }

    public int getHoraActual() {
        return _fechaActual.get(Calendar.HOUR_OF_DAY);
    }

    public void setHoraActual(int horaActual) {
        this._fechaActual.set(Calendar.HOUR_OF_DAY, horaActual);
    }

    public int getMinutoActual() {
        return _fechaActual.get(Calendar.MINUTE);
    }

    public void setMinutoActual(int minutoActual) {
        this._fechaActual.set(Calendar.MINUTE, minutoActual);
    }

    public int getDia() {
        return _fechaActual.get(Calendar.DAY_OF_MONTH);
    }

    public void setDia(int dia) {
        this._fechaActual.set(Calendar.DAY_OF_MONTH, dia);
    }

    public int getMes() {
        return _fechaActual.get(Calendar.MONTH)+1;
    }

    public void setMes(int mes) {
        this._fechaActual.set(Calendar.MONTH, mes-1);
    }

    public int getAnho() {
        return _fechaActual.get(Calendar.YEAR);
    }

    public void setAnho(int anho) {
        this._fechaActual.set(Calendar.YEAR, anho);
    }

    public Calendar getFechaActual() {
        return _fechaActual;
    }

    public void setFechaActual(Calendar fechaActual) {
        this._fechaActual = fechaActual;
    }

    public int getMinutosXSegundo() {
        return _minutosXSegundo;
    }

    public synchronized void setMinutosXSegundo(int minutosXSegundo) {
        _minutosXSegundo = minutosXSegundo;
    }
    
    public String getFechaString(String formato){
        
        SimpleDateFormat sdf= new SimpleDateFormat(formato);
        
        return sdf.format(this._fechaActual.getTime() );
    }
    public int ObtenerOpacidad () {//Irvin
        ArrayList<Integer> porcentajeOpacidad = new ArrayList<>();
        //System.out.println("Hora Actual:" + reloj.getHoraActual() + ":" + reloj.getMinutoActual());
        //En medianoche (horaActual = 0) se tendrá opacidad 100%
        //De 10am a 2pm se tendrá opacidad 0%
        porcentajeOpacidad.add(100); //00
        porcentajeOpacidad.add(90);  //01
        porcentajeOpacidad.add(80);  //02
        porcentajeOpacidad.add(70);  //03
        porcentajeOpacidad.add(60);  //04
        porcentajeOpacidad.add(50);  //05
        porcentajeOpacidad.add(40);  //06
        porcentajeOpacidad.add(30);  //07
        porcentajeOpacidad.add(20);  //08
        porcentajeOpacidad.add(10);  //09
        porcentajeOpacidad.add(0);   //10
        porcentajeOpacidad.add(0);   //11
        porcentajeOpacidad.add(0);   //12
        porcentajeOpacidad.add(0);   //13
        porcentajeOpacidad.add(0);   //14
        porcentajeOpacidad.add(10);  //15
        porcentajeOpacidad.add(20);  //16
        porcentajeOpacidad.add(30);  //17
        porcentajeOpacidad.add(40);  //18
        porcentajeOpacidad.add(50);  //19
        porcentajeOpacidad.add(60);  //20
        porcentajeOpacidad.add(70);  //21
        porcentajeOpacidad.add(80);  //22
        porcentajeOpacidad.add(90);  //23
        
        return porcentajeOpacidad.get(this.getHoraActual());
    }

}
