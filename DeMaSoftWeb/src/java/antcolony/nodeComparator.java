/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package antcolony;

import DeMaSoft.Reloj;
import java.util.Comparator;
import models.Almacen;

/**
 *
 * @author ladmin
 */
public class nodeComparator implements Comparator{
    private Reloj reloj;
    private Node almacen;
    public nodeComparator(Reloj reloj, Node almacen){
        this.reloj = reloj; 
        this.almacen=almacen;
    }
    @Override
    public int compare(Object o1,Object o2) {
        Node n1 = (Node)o1;
        Node n2 = (Node)o2;
        float d1 = Node.geomDistance(n1, this.almacen);
        float d2 = Node.geomDistance(n2, this.almacen);
        float t1 = (float)(((n1.getFechaMaxEntrega().getTime()- reloj.getFechaActual().getTimeInMillis())*1.0)/(3600*1000));
        float t2 = (float)(((n2.getFechaMaxEntrega().getTime()- reloj.getFechaActual().getTimeInMillis())*1.0)/(3600*1000));
        float c1 = d1*n1.getDemand()/t1;
        float c2 =d2*n2.getDemand()/t2;
        return Math.round(c1-c2);
    }
    
}
