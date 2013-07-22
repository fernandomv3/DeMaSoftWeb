/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package antcolony;

import java.util.*;
import DeMaSoft.*;
import models.*;

/**
 *
 * @author Fernando
 */
public class Ant {

    private boolean _done = false;
    private Node _initialNode;
    private int _initialCapacity;
    private int _currentCapacity;
    private ArrayList<Node> _route;
    private float _speed;
    private Chofer _chofer;
    private float _costoxKM;
    private float _costoExtra;
    private Calendar clock;
    public static final float Q = (float)0.9;
    public static final float BETA = 1;

    public Ant(Vehicle v, AntColony antColony, Mapa mapa) {
        this._initialCapacity = v.getCapacidadActual();
        this._currentCapacity = v.getCapacidadActual();
        this._initialNode = antColony.nodeMap.get(v.getPosicion().getPosX()*mapa.getAlto() + v.getPosicion().getPosY());
        this._route = new ArrayList<>();
        this._route.add(_initialNode);
        this._speed = v.getVelocidadMaxima();
        if(v.isDisponible())this._done=false;
        else this._done = true;
        if(this._initialCapacity ==0)this._done=true;
        this._chofer = v.getChofer();
        if (v.getIdVehiculo() < GestorVehiculos.numAutos){
            this._costoxKM = GestorVehiculos.costoKMCarros;
            this._costoExtra = GestorVehiculos.horaExtraCarros;
        }
        else{
            this._costoxKM = GestorVehiculos.costoKMMotos;
            this._costoExtra = GestorVehiculos.horaExtraMotos;
        }
    }

    public boolean isDone() {
        return _done;
    }

    public float calculateTrailImportance(Node node, PheromoneMatrix pheromoneArcs, Mapa mapa) {//según la feromona
        return pheromoneArcs.getLevel(_route.get(_route.size() - 1), node, mapa);
    }

    public ArrayList<Node> getRoute() {
        return _route;
    }

    public float calculateTrailAtractiveness(Node node, Calendar clock) {
        Node actualNode = _route.get(_route.size()-1);
        float distancia = Node.geomDistance(actualNode, node);
        float tiempoEnLlegar = distancia / this.getSpeed();//en horas
        //tiempo extra -> el tiempo que se debera pagar por exceder el turno del chofer
        float tiempoRestanteTurno = (this.getChofer().getTurno().getHoraFin().getTime()- clock.getTimeInMillis())/(1000*3600);
        float tiempoExtra = tiempoEnLlegar - tiempoRestanteTurno ;
        
        float remainingTime = (node.getFechaMaxEntrega().getTime() - clock.getTimeInMillis())/(1000*3600);
        //deltaTime ->tiempo restante de llegar
        float deltaTime = (remainingTime -tiempoEnLlegar);
        float atrac= 1/tiempoEnLlegar +(float)Math.exp(-1*deltaTime)  +(float)Math.exp(-1*tiempoExtra);
        if(tiempoExtra >0)atrac =0;
        return atrac;
    }
    
    public float calculateTrailAtractivenessReverse(Node node,Calendar clock){
        Node actualNode = _route.get(_route.size()-1);
        float distancia = Node.geomDistance(actualNode, node);//en km
        float tiempoEnLlegar = distancia  *3600 / this.getSpeed();//en segundos
        //tiempo extra -> el tiempo que se debera pagar por exceder el turno del chofer
        float tiempoRestanteTurno = (float)(this.getChofer().getTurno().getHoraFin().getTime()- this.clock.getTimeInMillis())/(1000);
        float tiempoExtra = tiempoEnLlegar - tiempoRestanteTurno ;//en segundos
        
        float remainingTime = (float)(node.getFechaMaxEntrega().getTime() - this.clock.getTimeInMillis())/(1000);
        //deltaTime ->tiempo restante de llegar
        float deltaTime = (remainingTime -tiempoEnLlegar);
        
        float atrac= 1/remainingTime;
        atrac*=deltaTime*deltaTime;
        float capFactor = ((float)this._currentCapacity / (float)node.getDemand());
        atrac *= capFactor;
        
        //if(tiempoExtra >0)atrac *=0.1;
        //atrac /= Math.abs(paquetes);
        if(this.getRoute().size()<= 1) atrac *= 10; 
        if(deltaTime <0)atrac *=0.001;
        
        return atrac;
    }
    
    public float calculateRouteCostPronostico(Reloj reloj){//solo considera el costo de llegar tarde
        float costo =0;
        int tamRoute = _route.size();
        float distancia=0;
        float tiempoTarde=0;
        for(int i=0;i < tamRoute;i++){
            if(i== 0)distancia += Node.geomDistance(this.getInitialNode(), _route.get(i));
            else distancia += Node.geomDistance(_route.get(i-1), _route.get(i));
            float tiempoEnLlegar = distancia / this.getSpeed();
            float tiempoRestanteParaLlegar=0; 
            if(i!=0 && _route.get(i).getFechaMaxEntrega() != null)
                tiempoRestanteParaLlegar= _route.get(i).getFechaMaxEntrega().getTime()- 
                    (reloj.getFechaActual().getTimeInMillis()  +(tiempoEnLlegar *3600 *1000));
            if(tiempoRestanteParaLlegar < 0 ) tiempoTarde += Math.floor(tiempoRestanteParaLlegar/3600/1000);
        }
        costo +=  (tiempoTarde * -20);
        return 0;
    }
    

    public float calculateRouteCost(Reloj reloj){
        float costo =0;
        int tamRoute = _route.size();
        float distancia=0;
        float tiempoTarde=0;
        float horaDeLlegada =reloj.getFechaActual().getTimeInMillis();
        float tiempoExtraDelChofer=0;
        for(int i=0;i < tamRoute;i++){
            if(i== 0)distancia += Node.geomDistance(this.getInitialNode(), _route.get(i));
            else distancia += Node.geomDistance(_route.get(i-1), _route.get(i));
            float tiempoEnLlegar = distancia / this.getSpeed();
            horaDeLlegada += tiempoEnLlegar*3600*1000;
            float tiempoRestanteParaLlegar = reloj.getFechaActual().getTimeInMillis() - (tiempoEnLlegar *3600 *1000);
            if(tiempoRestanteParaLlegar < 0 ) tiempoTarde += Math.abs(tiempoRestanteParaLlegar)/3600/1000;
        }
        if(horaDeLlegada > this.getChofer().getTurno().getHoraFin().getTime())
            tiempoExtraDelChofer= horaDeLlegada - this.getChofer().getTurno().getHoraFin().getTime();
        costo += (distancia * this.getCostoxKM()) + (tiempoTarde * 20)+tiempoExtraDelChofer * this.getCostoExtra();
        return costo;
    }
    
    public boolean isPossibleReverse(Node nextNode, AntColony antColony){
        /*Node actualNode = this._route.get(this._route.size()-1);
        float tiempoRestante = (nextNode.getFechaMaxEntrega().getTime()- this.getClock().getTimeInMillis())/(1000);
        float tiempoEnLlegar = (Node.geomDistance(actualNode, nextNode)/this.getSpeed()) *3600;
        if(tiempoEnLlegar > tiempoRestante)return false;*/
        return true;
    }
    
    public boolean isPossible(Node nextNode, AntColony antColony){
        Node actualNode = this._route.get(this._route.size()-1);
        float tiempoRestante = (nextNode.getFechaMaxEntrega().getTime()- this.clock.getTimeInMillis())/(1000);
        float tiempoEnLlegar = (float)((Node.geomDistance(actualNode, nextNode)*3600)/this.getSpeed());
        if(tiempoEnLlegar > tiempoRestante)return false;
        return true;
    }
    
    public void selectNextDestination(ArrayList<Node> orders, PheromoneMatrix pheromoneArcs, AntColony antColony, Reloj reloj, Mapa mapa) {
        float sum =0;
        int tam = orders.size();
        Node selected = null;
        float max =0;
        int maxIndex =0;
        Node nextNode; 
        ArrayList<Float> probAcum = new ArrayList<>(); 
        for(int i =0; i< tam ;i++ ){
            nextNode = orders.get(i);
            if(!isPossible(nextNode,antColony)){
                probAcum.add(sum);
                continue;
            }
            //que tan frecuente se ha pasado por esa opcion por otras hormigas?
            float t = calculateTrailImportance(nextNode, pheromoneArcs, mapa);
            //que tan atractiva es la solucion en terminos de distancia, tiempo y combustible?
            float n = calculateTrailAtractiveness(nextNode, antColony.clock);
            float product = (float) (t * Math.pow(n, BETA));
            //probAcum.add(sum);
            sum += product;
            probAcum.add(sum);
            if(product > max){
                max = product;
                maxIndex = i;
            }

        }
        if(sum==0 || probAcum.get(maxIndex)==0 ||probAcum.get(probAcum.size()-1)==0){
            this.setDone(true);
            return;
        }
        if(Math.random() <= Q){
            selected = orders.get(maxIndex);
        }
        else{
            float p = (float)(Math.random()*probAcum.get(probAcum.size()-1));
            for (int i = 0; i < probAcum.size(); i++) {
                if (p <= probAcum.get(i)) {
                    selected = orders.get(i);
                    break;
                }
            }
        }
        if(selected == null){
            selected = orders.get(maxIndex);
        }
        //actualizar capacidadlected, this._route.get(this._route.size()-1)) * this._fuelConsumedPerKm;
        if(this.getCurrentCapacity() < selected.getDemand()){
            this.setCurrentCapacity(0);
            this.setDone(true);
        }
        else{
            this.setCurrentCapacity(this.getCurrentCapacity() - selected.getDemand());
        }
        
        float tiempoEnLlegar = (Node.geomDistance(_route.get(_route.size()-1), selected)/this.getSpeed()) *3600;
        antColony.clock.add(Calendar.SECOND, (int)tiempoEnLlegar);
        this._route.add(selected);
        orders.remove(selected);      
    }
    public int getInitialCapacity() {
        return _initialCapacity;
    }

    public void setInitialCapacity(int initialCapacity) {
        this._initialCapacity = initialCapacity;
    }

    /**
     * @param done the _done to set
     */
    public void setDone(boolean done) {
        this._done = done;
    }

    /**
     * @return the _initialNode
     */
    public Node getInitialNode() {
        return _initialNode;
    }

    /**
     * @param initialNode the _initialNode to set
     */
    public void setInitialNode(Node initialNode) {
        this._initialNode = initialNode;
    }

    /**
     * @return the _currentCapacity
     */
    public int getCurrentCapacity() {
        return _currentCapacity;
    }

    /**
     * @param currentCapacity the _currentCapacity to set
     */
    public void setCurrentCapacity(int currentCapacity) {
        this._currentCapacity = currentCapacity;
    }

    /**
     * @param route the _route to set
     */
    public void setRoute(ArrayList<Node> route) {
        this._route = route;
    }

    /**
     * @return the _speed
     */
    public float getSpeed() {
        return _speed;
    }

    /**
     * @param speed the _speed to set
     */
    public void setSpeed(float speed) {
        this._speed = speed;
    }

    /**
     * @return the _chofer
     */
    public Chofer getChofer() {
        return _chofer;
    }

    /**
     * @param chofer the _chofer to set
     */
    public void setChofer(Chofer chofer) {
        this._chofer = chofer;
    }

    /**
     * @return the _costoxKM
     */
    public float getCostoxKM() {
        return _costoxKM;
    }

    /**
     * @param costoxKM the _costoxKM to set
     */
    public void setCostoxKM(float costoxKM) {
        this._costoxKM = costoxKM;
    }

    /**
     * @return the _costoExtra
     */
    public float getCostoExtra() {
        return _costoExtra;
    }

    /**
     * @param costoExtra the _costoExtra to set
     */
    public void setCostoExtra(float costoExtra) {
        this._costoExtra = costoExtra;
    }

    /**
     * @return the clock
     */
    public Calendar getClock() {
        return clock;
    }

    /**
     * @param clock the clock to set
     */
    public void setClock(Calendar clock) {
        this.clock = clock;
    }
}
