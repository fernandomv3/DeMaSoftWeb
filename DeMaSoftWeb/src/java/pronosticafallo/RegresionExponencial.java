/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pronosticafallo;
import java.math.*;
import java.lang.Math;
import java.util.ArrayList;
/**
 *
 * @author Fernando
 */
public class RegresionExponencial {
     
    /**
     * @param args the command line arguments
     */
    
    private double _constante=0;
    private double _exponente=0;
    
    public static int sumatoria(ArrayList<Integer> lista){
        int res=0;
        for(int i =0; i<lista.size();i++){
            res = res + lista.get(i);
        }
        return res;
    }
    public static int sumaCuadrados(ArrayList<Integer> lista){
        int res=0;
        for(int i =0; i<lista.size();i++){
            res = res + lista.get(i)*lista.get(i);
        }
        return res;
    }
    public static int sumaProductos(ArrayList<Integer> lista1, ArrayList<Integer> lista2){
        int res=0;
        for(int i =0; i<lista1.size();i++){
            res = res + lista1.get(i)*lista2.get(i);
        }
        return res;
    }
    
    public static double sumaLogaritmos(ArrayList<Integer> lista){
        
        double res=0;
        for(int i =0; i<lista.size();i++){
            res = res + Math.log(lista.get(i));
        }
        return res;
        
    }
    
    public static double sumaProductosNyLn(ArrayList<Integer> lista1, ArrayList<Integer> lista2){
        double res=0;
        for(int i =0; i<lista1.size();i++){
            res = res + lista1.get(i)*Math.log(lista2.get(i));
        }
        return res;
    }
   
    public void regresionExponencial(ArrayList<Integer> dias,ArrayList<Integer> paq) {

        
        double sumXLnY,sumX,sumXX;
        double promLnY,promX,a,b;
        
        sumXLnY = sumaProductosNyLn(dias,paq);
        sumX = sumatoria(dias);
        
        sumXX = sumaCuadrados(dias);
        promLnY = sumaLogaritmos(paq)/(double)paq.size();
        promX = sumatoria(dias)/(double)dias.size();
        
        b= (double)(sumXLnY-promLnY*sumX)/(double)(sumXX-(promX*sumX));
        a = Math.exp(b);
        
        
        double btemp=promLnY-(b*promX);
        double constante=Math.exp(btemp);        
                
        
        setConstante(constante);
        setExponente(b);
        
    }

    /**
     * @return the _constante
     */
    public double getConstante() {
        return _constante;
    }

    /**
     * @param constante the _constante to set
     */
    public void setConstante(double constante) {
        this._constante = constante;
    }

    /**
     * @return the _exponente
     */
    public double getExponente() {
        return _exponente;
    }

    /**
     * @param exponente the _exponente to set
     */
    public void setExponente(double exponente) {
        this._exponente = exponente;
    }
}
