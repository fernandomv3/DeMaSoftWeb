/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package antcolony;

import java.util.Date;

/**
 *
 * @author Fernando
 */
public class Node {
    private int _idOrden;
    private int _x;
    private int _y;
    private int _demand;
    private int _initialDemand;
    private Date _fechaMaxEntrega;
    private int _initialTimeWindow;
    private int _actualTimeWindow;
    
    public void resetDemand(){
        this._demand = this._initialDemand;
    }
    
    @Override
    public Node clone(){
        Node n = new Node();
        n._idOrden = this._idOrden;
        n._x = this._x;
        n._y=this._y;
        n._demand=this._demand;
        n._fechaMaxEntrega = this._fechaMaxEntrega;
        return n;
    }
    
    public void setInitialTimeWindow(int hours){
        _initialTimeWindow = hours;
    }
    
    public int getInitialTimeWindow(){
        return _initialTimeWindow;
    }
    
    public void setActualTimeWindow(int hours){
        _actualTimeWindow = hours;
    }
    
    public int getActualTimeWindow(){
        return _actualTimeWindow;
    }
    
    public static float geomDistance(Node n1, Node n2){
        int x1 = n1.getX();
        int x2 = n2.getX();
        int y1 = n1.getY();
        int y2 = n2.getY();
        return Math.abs(x1-x2) + Math.abs(y1-y2) ;
    }
    
    public int getDemand(){
        return _demand;
    }
    
    public void setDemand(int demand){
        _demand = demand;
    }
    
    public int getX(){
        return _x;
    }
    
    public void setX(int x){
        this._x = x;
    }
    
    public int getY(){
        return _y;
    }
    
    public void setY(int y){
        this._y = y;
    }

    public int getIdOrden() {
        return _idOrden;
    }

    public void setIdOrden(int idOrden) {
        this._idOrden = idOrden;
    }

    public Date getFechaMaxEntrega() {
        return _fechaMaxEntrega;
    }

    public void setFechaMaxEntrega(Date fechaMaxEntrega) {
        this._fechaMaxEntrega = fechaMaxEntrega;
    }

    public int getInitialDemand() {
        return _initialDemand;
    }

    public void setInitialDemand(int initialDemand) {
        this._initialDemand = initialDemand;
    }
    
    
}
