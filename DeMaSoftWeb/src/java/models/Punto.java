/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import java.util.*;
/**
 *
 * @author Fernando
 */
public class Punto {
    private int _posX;
    private int _posY;
    private boolean _disponible;
    private ArrayList<Punto> _adj;

    public Punto(int x, int y){
        _posX =x;
        _posY =y;
        _disponible = true;
        _adj = new ArrayList<Punto>();
    }
    
    public int getPosX() {
        return _posX;
    }

    public void setPosX(int posX) {
        this._posX = posX;
    }

    public int getPosY() {
        return _posY;
    }

    public void setPosY(int posY) {
        this._posY = posY;
    }

    public ArrayList<Punto> getAdj() {
        return _adj;
    }

    public void setAdj(ArrayList<Punto> adj) {
        this._adj = adj;
    }

    public boolean isDisponible() {
        return _disponible;
    }

    public void setDisponible(boolean disponible) {
        this._disponible = disponible;
    }
}
