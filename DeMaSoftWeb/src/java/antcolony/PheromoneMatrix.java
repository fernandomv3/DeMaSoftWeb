/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package antcolony;
import DeMaSoft.RouteCalculator;
import java.util.*;
import models.Mapa;
/**
 *
 * @author Fernando
 */
public class PheromoneMatrix {
    
    private byte[][] _value;
    private float _evaporationRatio;
    private ArrayList<Node> _nodes;
    private static PheromoneMatrix p=null;
    
    private PheromoneMatrix(ArrayList<Node> nodes,float start){
        _nodes = nodes;
        _evaporationRatio = (float) 0.1;
        _value = new byte[nodes.size()][nodes.size()]; 
        
        for(int i=0;i<nodes.size();i++){
            for(int j=0;j<nodes.size();j++){
                _value[i][j]=(byte)(start*100);
            }
        }
        
    }
    
    public static PheromoneMatrix getInstance(ArrayList<Node> nodes,float start){
        if(p==null){
            p= new PheromoneMatrix(nodes,start);
        }
        return p;
    }
    
    
    public void updatePheromoneTrail(ArrayList<Node> route, Mapa mapa){
        int size = route.size();
        for(int i=1;i < size ;i++){
            float t0 = this.getLevel(route.get(i-1), route.get(i), mapa);
            float newValue = (1-_evaporationRatio)*t0 + _evaporationRatio *2;
            this.setLevel(route.get(i-1), route.get(i), newValue, mapa);
        }
    }
    
    public void updateBestPheromoneTrail(ArrayList<Node> route, float bestCost, Mapa mapa){
        int size =route.size();
        //if (bestCost>0.01) System.out.println(bestCost);
        for(int i=1;i <size ;i++){
            float t0 = this.getLevel(route.get(i-1), route.get(i), mapa);
            float newValue =(1-_evaporationRatio)*t0 + _evaporationRatio* 10;
            this.setLevel(route.get(i-1), route.get(i), newValue, mapa);
        }
    }
    
    public float getLevel(Node node1, Node node2,Mapa mapa){
        int n1 = node1.getX()*mapa.getAlto()+ node1.getY();
        int n2 = node2.getX()*mapa.getAlto()+ node2.getY();
        return (float)(_value[n1][n2])/(float)100.0;
    }
    
    public void setLevel(Node node1, Node node2,float level, Mapa mapa){
        int n1 = node1.getX()*mapa.getAlto()+ node1.getY();
        int n2 = node2.getX()*mapa.getAlto()+ node2.getY();
        _value[n1][n2] =  (level *100) > Byte.MAX_VALUE ? Byte.MAX_VALUE: (byte)(level*100);
    }
}
