/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package antcolony;

import java.util.*;
import DeMaSoft.*;
import static antcolony.Ant.Q;
import models.*;

/**
 *
 * @author Fernando
 */
public class AntColony {

    public ArrayList<ArrayList<Node>> bestSolutions = new ArrayList<>();
    public static PheromoneMatrix pheromoneArcs;
    public static int pheromonasInicializadas = 0;
    public ArrayList<Node> orders = new ArrayList<>();
    public ArrayList<Node> nodeMap = new ArrayList<>();
    public static final float initialPheromoneValue = 1;
    public static final int numAnts = 3;
    public ArrayList<ArrayList<Node>> previousSolutions = new ArrayList<>();
    public Calendar clock;
    public boolean isPronostico;

    public void initializeArcsPheromoneLevel() {
        if (pheromonasInicializadas == 0) {
            pheromoneArcs = PheromoneMatrix.getInstance(nodeMap, initialPheromoneValue);//usar constanteeees!
            pheromonasInicializadas++;
        }
    }

    public ArrayList<Ant> createAntSet(ArrayList<Vehicle> vehicles, Mapa mapa, Reloj reloj) {
        int v_size = vehicles.size();
        ArrayList<Ant> ants = new ArrayList<>(v_size);
        for (int i = 0; i < v_size; i++) {
            ants.add(new Ant(vehicles.get(i), this, mapa));
            ants.get(i).setClock((Calendar) reloj.getFechaActual().clone());
        }
        return ants;
    }

    public ArrayList<Ant> createAnts(int numAnts, Vehicle v, Mapa mapa) {
        ArrayList<Ant> list = new ArrayList<Ant>();
        for (int i = 0; i < numAnts; i++) {
            Ant newAnt = new Ant(v, this, mapa);
            list.add((newAnt));
        }
        return list;
    }

    public void createNodeMap(Mapa mapa) {
        if (!this.nodeMap.isEmpty()) {
            return;
        }
        int tamPuntos = mapa.getPuntos().size();
        Node node = null;
        Punto p = null;
        for (int i = 0; i < tamPuntos; i++) {
            node = new Node();
            p = mapa.getPuntos().get(i);
            node.setX(p.getPosX());
            node.setY(p.getPosY());
            nodeMap.add(node);
        }
    }

    public void createOrdersList(ArrayList<OrdenEntrega> ordenes, Mapa mapa) {

        orders = new ArrayList<>();
        Punto p;
        Node n;
        for (int i = 0; i < ordenes.size(); i++) {
            p = ordenes.get(i).getPuntoEntrega();
            n = nodeMap.get(p.getPosX() * mapa.getAlto() + p.getPosY());
            n.setIdOrden(ordenes.get(i).getIdOrden());
            n.setDemand(ordenes.get(i).getNumPaquetes());
            n.setInitialDemand(ordenes.get(i).getNumPaquetes());
            n.setInitialTimeWindow(ordenes.get(i).getPrioridad());
            n.setActualTimeWindow(ordenes.get(i).getPrioridad());
            n.setFechaMaxEntrega(ordenes.get(i).getFechaMaxEntrega());
            orders.add(n);
        }
    }

    public void updateNodeMapAndOrders(Ant ant) {
        int cap = ant.getInitialCapacity();
        int tamRoute = ant.getRoute().size();
        int dem;

        for (int i = 0; i < tamRoute; i++) {
            dem = ant.getRoute().get(i).getDemand();
            if (cap >= dem) {
                ant.getRoute().get(i).setDemand(0);
                orders.remove(ant.getRoute().get(i));
            } else {
                ant.getRoute().get(i).setDemand(dem - cap);
            }
        }
    }

    public boolean assignNextOrder(ArrayList<Ant> ants, ArrayList<Node> nodes, Reloj reloj, Mapa mapa) {
        if (nodes.isEmpty()) {
            return false;
        }
        Node node = nodes.get(0);
        float maxAtractiveness = Float.NEGATIVE_INFINITY;
        int maxIndex = 0;
        float sum = 0;
        Ant selected = null;
        ArrayList<Float> probAcum = new ArrayList<>();
        boolean posible =false;
        for (int i = 0, size = ants.size(); i < size; i++) {
            posible = posible || ants.get(i).isPossible(node, this);
            if (ants.get(i).isDone()) {
                probAcum.add(Float.NEGATIVE_INFINITY);
                continue;
            }
            //que tan frecuente se ha pasado por esa opcion por otras hormigas?
            float t = ants.get(i).calculateTrailImportance(node, pheromoneArcs, mapa);
            //que tan atractiva es la solucion en terminos de distancia, tiempo y combustible?
            float n = ants.get(i).calculateTrailAtractivenessReverse(node, reloj.getFechaActual());
            float product = (float) (t * Math.pow(n, Ant.BETA));
            sum += product;
            probAcum.add(sum);
            if (product > maxAtractiveness) {
                maxAtractiveness = product;
                maxIndex = i;
            }
        }
        //if(!posible)System.out.println("Pedido Imposible");
        if (sum == 0 /*|| maxAtractiveness == Float.NEGATIVE_INFINITY*/) {
            return false;
        }
        if (Math.random() <= Q) {
            selected = ants.get(maxIndex);
        } else {
            float p = (float) (Math.random() * probAcum.get(probAcum.size() - 1));
            for (int i = 0; i < probAcum.size(); i++) {
                if (p <= probAcum.get(i)) {
                    selected = ants.get(i);
                    break;
                }
            }
        }
        if (selected == null) {
            selected = ants.get(maxIndex);
        }
        //actualizar capacidadlected, this._route.get(this._route.size()-1)) * this._fuelConsumedPerKm;
        if (selected.getCurrentCapacity() < node.getDemand()) {
            node.setDemand(node.getDemand() - selected.getCurrentCapacity());
            selected.setCurrentCapacity(0);
            selected.setDone(true);
        } else {
            selected.setCurrentCapacity(selected.getCurrentCapacity() - node.getDemand());
            if (selected.getCurrentCapacity() == 0) {
                selected.setDone(true);
            }
            nodes.remove(node);//solo se retira en caso el vehiculo seleccionado lo cubra todo
        }

        float tiempoEnLlegar = (float) ((Node.geomDistance(selected.getRoute().get(selected.getRoute().size() - 1), node) * 3600) / selected.getSpeed());
        selected.getClock().add(Calendar.SECOND, (int) tiempoEnLlegar);
        try {
            selected.getRoute().add(node);
        } catch (java.lang.OutOfMemoryError exception) {
            System.out.println(selected);
            System.out.println(exception.getMessage());
        }
        return true;
    }
    
    public boolean assignNextOrder2(ArrayList<Ant> ants, ArrayList<Node> nodes, Reloj reloj, Mapa mapa){
        if (nodes.isEmpty()) {
            return false;
        }
        Node node = nodes.get(0);
        Ant selected = null;
        float horasRestantes = (float)(node.getFechaMaxEntrega().getTime()-reloj.getFechaActual().getTimeInMillis())/(float)(1000.0*3600.0);
        
        if(horasRestantes <= 4){
            //mejor moto
            selected = escogerVehiculo(ants,node,0,GestorVehiculos.numMotos,reloj,mapa);//escoger entre las motos la mejor
        }
        else{
            selected = escogerVehiculo(ants,node,GestorVehiculos.numMotos,GestorVehiculos.numMotos+GestorVehiculos.numAutos,reloj,mapa);//escoger entre los autos el mejor
        }
        if(selected == null)//si no se encontro un vehiculo"predeterminado" 
            selected = escogerVehiculo(ants,node,0,GestorVehiculos.numMotos+GestorVehiculos.numAutos,reloj,mapa);//escoger el mejor de todos los vehiculos
        
        if(selected == null)//si despues de todo no encontro un vehiculo es porque ya no habian disponibles
            return false;
        //actualizar el estado del pedido y de la hormiga "vehiculo"
        if (selected.getCurrentCapacity() < node.getDemand()) {
            node.setDemand(node.getDemand() - selected.getCurrentCapacity());
            selected.setCurrentCapacity(0);
            selected.setDone(true);
        } else {
            selected.setCurrentCapacity(selected.getCurrentCapacity() - node.getDemand());
            if (selected.getCurrentCapacity() == 0) {
                selected.setDone(true);
            }
            nodes.remove(node);//solo se retira en caso el vehiculo seleccionado lo cubra todo
        }

        float tiempoEnLlegar = (float) ((Node.geomDistance(selected.getRoute().get(selected.getRoute().size() - 1), node) * 3600) / selected.getSpeed());
        selected.getClock().add(Calendar.SECOND, (int) tiempoEnLlegar);
        selected.getRoute().add(node);
        return true;
    }

    public void reverseAntColonyProcedure(ArrayList<Vehicle> vehiculos, ArrayList<OrdenEntrega> ordenesEntrega, Reloj reloj, Almacen Ralmacen, Mapa mapa) {
        Punto almacenP = Ralmacen.getUbicacion();
        Node almacen = nodeMap.get(mapa.getAlto() * almacenP.getPosX() + almacenP.getPosY());//obtener el almacen
        previousSolutions = bestSolutions;//copiar las soluciones anterirores para no perderlas
        bestSolutions = new ArrayList<>();
        createOrdersList(ordenesEntrega, mapa);//crear la lista de ordenes
        int tamVehiculos = vehiculos.size();
        initializeArcsPheromoneLevel();//se puede resetear o tener algun valor residual

        Collections.sort(orders, new Comparator() {//ordenar las ordenes por tiempo restante!
            @Override
            public int compare(Object o1, Object o2) {
                return ((Node) o1).getFechaMaxEntrega().compareTo(((Node) o2).getFechaMaxEntrega());
            }
        });
        
        //Collections.sort(orders,new nodeComparator(reloj,almacen));
        
        ArrayList<Node> tempOrders = null;
        float bestCost = Float.POSITIVE_INFINITY;
        ArrayList<Ant> setAnts;
        ArrayList<Ant> bestAntSet = null;   
        for (int i = 0; i < numAnts; i++) {
            tempOrders = (ArrayList<Node>) orders.clone();
            resetOrders();//resetear las ordenes para un set nuevo de hormigas
            setAnts = createAntSet(vehiculos, mapa, reloj);//crear un set de 60 hormigas
            setFirstOrders(setAnts, tempOrders, previousSolutions);//setear las primeras ordenes
            while (true) {
                if (!assignNextOrder2(setAnts, tempOrders, reloj, mapa)) {//mientras aun se puede asignar una orden
                    break;
                }
            }
            float costoSol = 0;
            for (int j = 0; j < tamVehiculos; j++) {
                setAnts.get(j).getRoute().add(almacen);
                pheromoneArcs.updatePheromoneTrail(setAnts.get(j).getRoute(), mapa);
                if(isPronostico)costoSol+=setAnts.get(j).calculateRouteCostPronostico(reloj);//calcular el costo si es simulacion
                else costoSol += setAnts.get(j).calculateRouteCost(reloj);//calcular el costo si es pronostico
            }
            Calendar cal = (Calendar) reloj.getFechaActual().clone();
            cal.add(Calendar.HOUR,24);
            for (int j = 0; j < tempOrders.size(); j++) {
                long time = cal.getTimeInMillis() - tempOrders.get(j).getFechaMaxEntrega().getTime();
                if (time < 0) {
                    costoSol += Math.floor((float) time / 1000.0 / 3600.0) * -20;
                }
            }
            if (costoSol < bestCost) {
                bestCost = costoSol;
                bestAntSet = setAnts;
            }
        }
        ArrayList<Node> bestRoute;
        for (int j = 0; j < tamVehiculos; j++) {
            bestRoute = bestAntSet.get(j).getRoute();
            pheromoneArcs.updateBestPheromoneTrail(bestRoute, bestCost, mapa);
            bestSolutions.add(bestRoute);
        }
    }

    public void antColonyProcedure(ArrayList<Vehicle> vehiculos, ArrayList<OrdenEntrega> ordenesEntrega, Reloj reloj, Almacen Ralmacen, Mapa mapa) {
        Punto almacenP = Ralmacen.getUbicacion();
        Node almacen = nodeMap.get(mapa.getAlto() * almacenP.getPosX() + almacenP.getPosY());//obtener el almacen
        previousSolutions = (ArrayList<ArrayList<Node>>) bestSolutions.clone();//clonar la solucion para no perderla
        bestSolutions = new ArrayList<>();
        createOrdersList(ordenesEntrega, mapa);//se crea la lista de ordenes actuales
        int tamVehiculos = vehiculos.size();
        initializeArcsPheromoneLevel();//se puede resetear o tener algun valor residual
        Node firstNode = null;
        for (int n = 0; n < tamVehiculos; n++) {
            //variables para guardar la mejor solucion
            if (!vehiculos.get(n).isDisponible() /*|| vehiculos.get(n).getChofer().isEstaAlmorzando()*/) {
                //si el vehiculo no esta disponible no se le crea solucion
                //el vehiculo tampoco se considera en caso este almorzando
                bestSolutions.add(new ArrayList<Node>());
                continue;
            }
            if (previousSolutions != null && !previousSolutions.isEmpty()
                    && previousSolutions.get(n) != null && previousSolutions.get(n).size() >= 2) {
                firstNode = previousSolutions.get(n).get(1);//primer nodo de pedido asignado
            }
            float bestCost = Float.POSITIVE_INFINITY;
            Ant bestSolution = null;

            //se crean las hormigas
            ArrayList<Ant> listaHormigas = createAnts(numAnts, vehiculos.get(n), mapa);
            int tamHormiga = listaHormigas.size();
            ArrayList<Node> tempOrders = null;
            Ant currentAnt = null;

            for (int i = 0; i < tamHormiga; i++) {
                //crear una lista temporal de las ordenes por cada hormiga
                tempOrders = (ArrayList<Node>) orders.clone();
                currentAnt = listaHormigas.get(i);
                if (firstNode != null) {
                    if (tempOrders.remove(firstNode)) {
                        currentAnt.getRoute().add(firstNode);
                    }
                }
                clock = (Calendar) reloj.getFechaActual().clone();
                //generar una ruta y actualizar el rastro de fermona
                while (!currentAnt.isDone() && !tempOrders.isEmpty()) {//se van añadiendo las ordenes una a una
                    currentAnt.selectNextDestination(tempOrders, pheromoneArcs, this, reloj, mapa);
                    pheromoneArcs.updatePheromoneTrail(currentAnt.getRoute(), mapa);
                }
                //volver al almacen una vez que se acaba
                currentAnt.getRoute().add(almacen);
                //localSearch();//hacer una optimización con un algoritmo

                //de todas las hormigas cual tuvo el mejor costo
                float currentCost = currentAnt.calculateRouteCost(reloj);
                if (currentCost < bestCost) {
                    bestCost = currentCost;
                    bestSolution = currentAnt;
                }
            }
            /*
             System.out.println("Vehiculo "+n);
             for(int k =0; k<bestSolution.getRoute().size();k++){
             System.out.println(bestSolution.getRoute().get(k).getX()+","+bestSolution.getRoute().get(k).getY());
             }*/
            updateNodeMapAndOrders(bestSolution);//"commitear" los cambios para que el siguiente vehiculo no los considere
            pheromoneArcs.updateBestPheromoneTrail(bestSolution.getRoute(), bestCost, mapa);
            bestSolutions.add(bestSolution.getRoute());
        }
    }

    private void setFirstOrders(ArrayList<Ant> setAnts, ArrayList<Node> tempOrders, ArrayList<ArrayList<Node>> previousSolutions) {
        if (previousSolutions == null) {
            return;
        }
        int tamSol = previousSolutions.size();
        Node firstNode = null;
        for (int i = 0; i < tamSol; i++) {
            if(setAnts.get(i).isDone())continue;
            if (previousSolutions.get(i) != null || previousSolutions.get(i).size() >= 2) {
                firstNode = previousSolutions.get(i).get(1);
                int p = tempOrders.indexOf(firstNode);
                if (p != -1) {
                    if(!setAnts.get(i).isPossible(firstNode, this))continue;
                    if (setAnts.get(i).getCurrentCapacity() < firstNode.getDemand()) {
                        firstNode.setDemand(firstNode.getDemand() - setAnts.get(i).getCurrentCapacity());
                        setAnts.get(i).setCurrentCapacity(0);
                        setAnts.get(i).setDone(true);
                    } else {
                        setAnts.get(i).setCurrentCapacity(setAnts.get(i).getCurrentCapacity() - firstNode.getDemand());
                        if (setAnts.get(i).getCurrentCapacity() == 0) {
                            setAnts.get(i).setDone(true);
                        }
                        tempOrders.remove(firstNode);//solo se retira en caso el vehiculo seleccionado lo cubra todo
                    }

                    float tiempoEnLlegar = (float) ((Node.geomDistance(setAnts.get(i).getRoute().get(setAnts.get(i).getRoute().size() - 1), firstNode) * 3600) / setAnts.get(i).getSpeed());
                    setAnts.get(i).getClock().add(Calendar.SECOND, (int) tiempoEnLlegar);
                    setAnts.get(i).getRoute().add(firstNode);
                }
            }
        }
    }
    
    public void resetOrders(){
        for(int i=0; i < orders.size();i++){
            orders.get(i).resetDemand();
        }
    }

    private Ant escogerVehiculo(ArrayList<Ant> ants, Node node, int start, int end, Reloj reloj, Mapa mapa) {
        float maxAtractiveness = Float.NEGATIVE_INFINITY;
        int maxIndex = 0;
        float sum = 0;
        Ant selected = null;
        ArrayList<Float> probAcum = new ArrayList<>();
        boolean posible =false;
        for (int i = start; i < end; i++) {
            posible = posible || ants.get(i).isPossible(node, this);
            if (ants.get(i).isDone()) {
                probAcum.add(Float.NEGATIVE_INFINITY);
                continue;
            }
            //que tan frecuente se ha pasado por esa opcion por otras hormigas?
            float t = ants.get(i).calculateTrailImportance(node, pheromoneArcs, mapa);
            //que tan atractiva es la solucion en terminos de distancia, tiempo y combustible?
            float n = ants.get(i).calculateTrailAtractivenessReverse(node, reloj.getFechaActual());
            float product = (float) (t * Math.pow(n, Ant.BETA));
            sum += product;
            probAcum.add(sum);
            if (product > maxAtractiveness) {
                maxAtractiveness = product;
                maxIndex = i;
            }
        }
        if (sum == 0 /*|| maxAtractiveness == Float.NEGATIVE_INFINITY*/) {
            return null;
        }
        if (Math.random() <= Q) {
            selected = ants.get(maxIndex);
        } else {
            float p = (float) (Math.random() * probAcum.get(probAcum.size() - 1));
            for (int i = 0; i < probAcum.size(); i++) {
                if (p <= probAcum.get(i)) {
                    selected = ants.get(i);
                    break;
                }
            }
        }
        return selected;
    }
            
}
