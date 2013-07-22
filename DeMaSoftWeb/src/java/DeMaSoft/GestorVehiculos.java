/*
  * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;

import java.util.*;
import antcolony.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.simple.*;
import models.*;

/**
 *
 * @author Fernando
 */
public class GestorVehiculos implements Runnable {

    public static  int numAutos=20;
    public static  int numMotos=40;
    public static final int capacidadCarros = 25;
    public static final int capacidadMotos = 4;
    public static float costoKMCarros=60;
    public static float costoKMMotos=30;
    public static float velocidadCarros=30;
    public static float velocidadMotos=60;
    public static final float horaExtraCarros = 12;
    public static final float horaExtraMotos = 8;
    private ArrayList<Vehicle> _vehiculos;
    private ArrayList<Chofer> _choferes;
    private Reloj _reloj;
    private boolean _pausar = false;
    private boolean _detener = false;
    private ArrayList<OrdenEntrega> setOrd;
    private int ventana;
    private Almacen almacen;
    

    public String calcTiempoMinLlegada() {
        int indiceActual, indiceFinal, distancia;
        float tiempo, mint;
        mint = Float.POSITIVE_INFINITY;
        Vehicle vMin =null;
        for (int i = 0; i < this._vehiculos.size(); i++) {
            Ruta ruta = this._vehiculos.get(i).getRecorrido().getRutaActual();
            indiceActual = this._vehiculos.get(i).getIndice();
            indiceFinal = ruta.getPuntos().size() - 1;
            distancia = indiceFinal - indiceActual;
            tiempo = distancia / this._vehiculos.get(i).getVelocidadMaxima();

            if (tiempo < mint) {
                mint = tiempo;
                vMin = this._vehiculos.get(i);
            }
        }
        
        if (mint<=0) {
        return "Hay vehículos disponibles.";
        }
        
        int horas = Math.round(mint);
        int minutos = Math.round((mint - horas) * 60);

        return "El vehículo "+vMin.getIdVehiculo()+" llegará en : "+horas + ":" + minutos;
    }

    public String getJson(Filtro f, Object object, Reloj reloj, ArrayList<OrdenEntrega> setOrd) {

        String hora = reloj.getFechaString("dd-MM-YYYY HH:mm");
        JSONObject obj = new JSONObject();
        JSONArray vehiculosJson = new JSONArray();
        int tam = this._vehiculos.size();
        for (int i = 0; i < tam; i++) {
            JSONObject v = new JSONObject();
            JSONArray ruta = new JSONArray();
            if (f==null||f.filtro(this._vehiculos.get(i), object)) {
                v.put("placa", this._vehiculos.get(i).getPlaca());
                v.put("disp", this._vehiculos.get(i).isDisponible());
                v.put("x", this._vehiculos.get(i).getPosicion().getPosX());
                v.put("y", this._vehiculos.get(i).getPosicion().getPosY());
                v.put("cap", this._vehiculos.get(i).getCapacidadActual());
                int tamRuta = this._vehiculos.get(i).getRecorrido().getRutaActual().getPuntos().size();
                for (int j = 0; j < tamRuta; j++) {
                    JSONObject punto = new JSONObject();
                    punto.put("x", this._vehiculos.get(i).getRecorrido().getRutaActual().getPuntos().get(j).getPosX());
                    punto.put("y", this._vehiculos.get(i).getRecorrido().getRutaActual().getPuntos().get(j).getPosY());
                    ruta.add(punto);
                }
            } else {
                v.put("x", RouteCalculator.almacen.getUbicacion().getPosX());
                v.put("y", RouteCalculator.almacen.getUbicacion().getPosY());
            }
            v.put("ruta", ruta);
            vehiculosJson.add(v);
        }
        JSONArray pedidosJson = new JSONArray();
        for (int i = 0; i < setOrd.size(); i++) {
            JSONObject pedido = new JSONObject();
            pedido.put("x", setOrd.get(i).getPuntoEntrega().getPosX());
            pedido.put("y", setOrd.get(i).getPuntoEntrega().getPosY());
            pedido.put("date", setOrd.get(i).getFechaMaxEntrega().toString());
            pedido.put("numP", setOrd.get(i).getNumPaquetes());
            pedidosJson.add(pedido);
        }
        obj.put("vehiculos", vehiculosJson);
        obj.put("reloj", hora);
        obj.put("tiempoRestante", this.calcTiempoMinLlegada());
        obj.put("pedidos", pedidosJson);
        obj.put("opacidad", reloj.ObtenerOpacidad());
        return obj.toJSONString();
    }

    public OrdenEntrega getOrden(Node node, ArrayList<OrdenEntrega> setOrd) {
        OrdenEntrega orden = null;
        int tamOrdenes = setOrd.size();
        for (int i = 0; i < tamOrdenes; i++) {
            if (setOrd.get(i).getIdOrden() == node.getIdOrden()) {
                orden = setOrd.get(i);
            }
        }
        return orden;
    }

    public void asignarRutas(ArrayList<ArrayList<Node>> bestRoutes, ArrayList<OrdenEntrega> setOrd, Mapa mapa, Almacen almacen) {
        Mapa map = mapa;
        for (int i = 0; i < bestRoutes.size(); i++) {
            ArrayList<Punto> ruta = new ArrayList<>();
            ArrayList<Node> solucion = bestRoutes.get(i);
            int tamSol = solucion.size();
            this._vehiculos.get(i).setOrdenes(new ArrayList<OrdenEntrega>());
            for (int j = 0; j < tamSol; j++) {
                OrdenEntrega orden = getOrden(solucion.get(j),setOrd);
                if (orden != null) {
                    this._vehiculos.get(i).getOrdenes().add(orden);
                }

            }
            for (int j = 1; j < tamSol; j++) {
                Punto p1;
                if (j == 0) {
                    if (this._vehiculos.get(i).getRecorrido().getRutaActual().getPuntos().isEmpty()) {
                        continue;
                    }
                    p1 = this._vehiculos.get(i).getRecorrido().getRutaActual().getPuntos().get(this._vehiculos.get(i).getIndice());
                } else {
                    int indAnt = solucion.get(j - 1).getX() * mapa.getAlto() + solucion.get(j - 1).getY();
                    p1 = map.getPuntos().get(indAnt);
                }


                int ind = solucion.get(j).getX() * mapa.getAlto() + solucion.get(j).getY();
                Punto p2 = map.getPuntos().get(ind);
                ArrayList<Punto> segment = GestorVehiculos.Astar(p1, p2, mapa);
                if (j > 1 && segment != null) {
                    segment.remove(0);
                }
                if(segment != null){
                    ruta.addAll(segment);
                }
                
            }
            if (this._vehiculos.get(i).getOrdenes().isEmpty() || this._vehiculos.get(i).getCapacidadActual() == 0 ) {

                Punto p1 = this._vehiculos.get(i).getPosicion();
                Punto p2 = almacen.getUbicacion();
                ArrayList<Punto> segment = GestorVehiculos.Astar(p1, p2, mapa);
                //ArrayList<Punto> segment = GestorVehiculos.rutaMonse(p1, p2);
                ruta = segment;

            }
            Ruta nuevaRuta = new Ruta();
            nuevaRuta.setPuntos(ruta);
            nuevaRuta.setHora(_reloj.getFechaActual().getTime());
            this._vehiculos.get(i).setIndice(0);
            Recorrido rec = this._vehiculos.get(i).getRecorrido();
            //rec.getRutas().add(nuevaRuta);
            rec.setRutaActual(nuevaRuta);
        }
    }

    public void actualizarVehiculosYOrdenes(int tiempo, ArrayList<OrdenEntrega> ordenes, Reloj reloj,int minIdSimulacion, int codSimulacion) {
        int sizeV = this._vehiculos.size();
        for (int i = 0; i < sizeV; i++) {
            this._vehiculos.get(i).avanzar(tiempo, reloj, getSetOrd(), almacen, minIdSimulacion, codSimulacion);
        }

        /*int sizeO= ordenes.size();
         for(int j=0;j<sizeO;j++){
         Reloj reloj= Reloj.getInstancia();
            
         ordenes.get(j).actualizaPrioridad(reloj.getHoraActual(), reloj.getMinutoActual()); ;
            
         }*/
    }
    
    public static ArrayList<Punto> rutaMonse(Punto p1, Punto p2){
        ArrayList<Punto> resultado = new ArrayList<>();
        Punto p = p1;
        resultado.add(p);
        float maxDist;
        int maxInd=0;
        float dist;
        while(true){
            maxDist=Float.POSITIVE_INFINITY;
            for(int i =0,l = p.getAdj().size(); i< l;i++){
                dist= geomDistance(p,p.getAdj().get(i));
                if(dist < maxDist){
                    maxDist = dist;
                    maxInd=i;
                }
            }
            p= p.getAdj().get(maxInd);
            resultado.add(p);
            if(p == p2)break;
        }
        return resultado;
    }

    public static float geomDistance(Punto p1, Punto p2) {
        int x1 = p1.getPosX();
        int x2 = p2.getPosX();
        int y1 = p1.getPosY();
        int y2 = p2.getPosY();
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private static ArrayList<Punto> getCheapestRoute(ArrayList<ArrayList<Punto>> routes, Punto p) {
        ArrayList<Punto> cheapest = null;
        float minCost = Float.POSITIVE_INFINITY;
        float cost;
        int tamR =routes.size();
        ArrayList<Punto> current;
        for (int i = 0; i < tamR; i++) {
            current = routes.get(i);
            //añadir el costo de las aristas en caso se necesite aristas de difernte peso
            cost = geomDistance(current.get(current.size() - 1), p);
            if (cost <= minCost) {
                minCost = cost;
                cheapest = current;
            }
        }
        routes.remove(cheapest);
        return cheapest;
    }

    public static ArrayList<Punto> Astar(Punto p1, Punto p2, Mapa mapa) {
        ArrayList<ArrayList<Punto>> routes = new ArrayList<>();
        ArrayList<Punto> visitados = new ArrayList<>();
        //inicializar una ruta con el nodo inicial
        ArrayList<Punto> route = new ArrayList<>();
        route.add(p1);
        routes.add(route);
        ArrayList<Punto> cheapest;
        Punto ultimo;
        ArrayList<Punto> nuevaRuta;
        while (visitados.size() != mapa.getPuntos().size()) {
            cheapest = getCheapestRoute(routes, p2);
            ultimo = cheapest.get(cheapest.size() - 1);
            if (ultimo == p2) {
                return cheapest;
            }
            for (int i = 0; i < ultimo.getAdj().size(); i++) {
                if (ultimo.getAdj().get(i).isDisponible() && !visitados.contains(ultimo.getAdj().get(i))) {
                    nuevaRuta = (ArrayList<Punto>) cheapest.clone();
                    nuevaRuta.add(ultimo.getAdj().get(i));
                    routes.add(nuevaRuta);
                }
            }
            visitados.add(ultimo);
        }
        return null;
    }

    public ArrayList<Vehicle> getVehiculos() {
        return _vehiculos;
    }

    public void setVehiculos(ArrayList<Vehicle> _vehiculos) {
        this._vehiculos = _vehiculos;
    }

    public GestorVehiculos(Reloj reloj) {
        
        this._reloj= reloj;        
        inicializarValores();
    }

    public void actualizaVehiculos() {
        Thread actualizador = new Thread(this);
        actualizador.start();
    }
    

    public void createVehicles(Almacen almacen, ArrayList<Chofer> choferes,int codSimulacion) {
        _vehiculos = new ArrayList<>();
        
        PreparedStatement pstmt=obtenIngresaVehiculos();
        for (int i = 0; i < numMotos; i++) {
            Vehicle v = new Motocicleta(capacidadMotos, velocidadMotos);
            v.setPosicion(almacen.getUbicacion());
            v.setChofer(choferes.get(i+numAutos));
            v.setIdVehiculo(i+numAutos);
            String placa=String.valueOf(i+numAutos+1);
            v.setPlaca(placa);
            _vehiculos.add(v);
            if (codSimulacion>=0) this.ingresaVehiculo(pstmt, i+numAutos, capacidadMotos, velocidadMotos, placa, codSimulacion);
        }
        for (int i = 0; i < numAutos; i++) {
            Vehicle v = new Automovil(capacidadCarros, velocidadCarros);
            v.setPosicion(almacen.getUbicacion());
            v.setChofer(choferes.get(i));
            v.setIdVehiculo(i);
            String placa=String.valueOf(i+1);
            v.setPlaca(placa);
            _vehiculos.add(v);
            
            if (codSimulacion>=0) this.ingresaVehiculo(pstmt, i, capacidadCarros, velocidadCarros, placa, codSimulacion);
            
        }
        
        
        try{
            if (pstmt!=null)pstmt.close();
            
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }
    }
    
    
    public PreparedStatement obtenIngresaVehiculos(){
        
        
        PreparedStatement pstmt=null;
        
        StringBuffer query=new StringBuffer();
        
        
        query.append(" INSERT INTO `inf2260981g2`.`VEHICULO` ");
        query.append(" (`idVEHICULO`,`numCapacidad`,`velocidadMaxima`,`estado`,`placa`,`idSimulacion`) ");
        query.append(" VALUES ");
        query.append(" (?,?,?,'A',?,?) ");
        
        try{
            
            Connection con= GestorConexion.getConexion();
            pstmt= con.prepareStatement(query.toString());
            
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }
        
        
        return pstmt;
        
    }
    
    public void ingresaVehiculo(PreparedStatement pstmt, int idVehiculo,int capacidad, float vMax, String placa, int codSimulacion ){
        
        try{
            pstmt.setInt(1, idVehiculo);
            pstmt.setInt(2, capacidad);
            pstmt.setFloat(3, vMax);
            pstmt.setString(4, placa);
            pstmt.setInt(5, codSimulacion);
            
            pstmt.executeUpdate();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }
    

    @Override
    public void run() {
        ArrayList<Calendar> ultimaAct = new ArrayList<>();

        for (int i = 0; i < this.getVehiculos().size(); i++) {
            ultimaAct.add((Calendar) _reloj.getFechaActual().clone());
        }
        int minutos = getVentana();
        Calendar proxRevision = (Calendar) _reloj.getFechaActual().clone();
        proxRevision.add(Calendar.MINUTE, minutos);
        while (true) {
            if (_detener) {
                _detener = false;
                break;
            }
            if (_pausar) {
                continue;
            }
            if (proxRevision.compareTo(_reloj.getFechaActual()) > 0) {
                continue;
            }
            proxRevision.add(Calendar.MINUTE, minutos);
            for (int i = 0; i < _vehiculos.size(); i++) {
                if (_detener || _pausar) {
                    break;
                }
                Vehicle vehiculo = _vehiculos.get(i);
                Calendar nuevaHora = (Calendar) _reloj.getFechaActual().clone();
                float variacion = nuevaHora.getTimeInMillis() - ultimaAct.get(i).getTimeInMillis();
                variacion = variacion / 1000;
                vehiculo.avanzar2(variacion, _reloj, setOrd, getAlmacen(), getChoferes(),-1, 1);
                ultimaAct.set(i, nuevaHora);
            }
        }
        
    }

    /**
     * @return the _pausar
     */
    public boolean isPausar() {
        return _pausar;
    }

    /**
     * @param pausar the _pausar to set
     */
    public void pausar() {
        this._pausar = true;
    }

    public void continuar() {
        this._pausar = false;
    }

    /**
     * @param detener the _detener to set
     */
    public void detener() {
        this._detener = true;
    }
    
    private static void inicializarValores(){
        
        numAutos = 20;
        numMotos = 40;
        costoKMCarros = 5;
        costoKMMotos = 3;
        velocidadCarros = 30;
        velocidadMotos = 60;
        
        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();        
        
        
        query.append(" select numAutos,numMotos,CostoKmAutos,CostoKmMotos,VelocidadCarros,VelocidadMotos from Parametros_Simulacion where idParametros_Simulacion in ( select max(idParametros_Simulacion) from Parametros_Simulacion) ");
        
        try{
            
            Connection con= GestorConexion.getConexion();
            
            try{
                pstmt=con.prepareStatement(query.toString());
                rset=pstmt.executeQuery();

                if (rset.next()){

                       numAutos=rset.getInt("numAutos");
                       numMotos=rset.getInt("numMotos");
                       costoKMCarros=rset.getFloat("CostoKmAutos");
                       costoKMMotos=rset.getFloat("costoKmMotos");
                       velocidadCarros=rset.getFloat("VelocidadCarros");
                       velocidadMotos=rset.getFloat("VelocidadMotos");

                }
            }catch(Exception e){
                numAutos = 20;
                numMotos = 40;
                costoKMCarros = 5;
                costoKMMotos = 3;
                velocidadCarros = 30;
                velocidadMotos = 60;
                
            }finally{
                
                if(pstmt!=null)pstmt.close();
                if(rset!=null)rset.close();
            }
            
            
        }catch(Exception e){
            
            numAutos = 20;
            numMotos = 40;
            costoKMCarros = 5;
            costoKMMotos = 3;
            velocidadCarros = 30;
            velocidadMotos = 60;
        }
        
    }
    
    
    public static void grabaParametros(int cod){
                        
        PreparedStatement pstmt = null;
	ResultSet rset = null;
	StringBuffer query = new StringBuffer();        
        
        
        query.append(" insert into Parametros_Simulacion (numAutos,numMotos,CostoKmAutos,CostoKmMotos,VelocidadCarros,VelocidadMotos,idSimulacion ,FechaCambio) values (?,?,?,?,?,?,?,sysdate()) ");
        
        try{
            
            Connection con= GestorConexion.getConexion();
            
            try{
                pstmt=con.prepareStatement(query.toString());
                pstmt.setInt(1, numAutos);
                pstmt.setInt(2, numMotos);
                pstmt.setFloat(3, costoKMCarros);
                pstmt.setFloat(4, costoKMMotos);
                pstmt.setFloat(5, velocidadCarros);
                pstmt.setFloat(6, velocidadMotos);
                pstmt.setInt(7, cod);
                
                pstmt.executeUpdate();
                //con.commit();
                
            }catch(Exception e){
                
                int i=0;
            }finally{
                
                if(pstmt!=null)pstmt.close();
                if(rset!=null)rset.close();
            }
            
            
        }catch(Exception e){
            int i=0;
        }
    }

    /**
     * @return the reloj
     */
    public Reloj getReloj() {
        return _reloj;
    }

    /**
     * @param reloj the reloj to set
     */
    public void setReloj(Reloj reloj) {
        this._reloj = reloj;
    }
    
    public void avanzarVehiculos(int ventana, ArrayList<OrdenEntrega> setOrd, Reloj reloj, Almacen almacen,int minIdSimulacion, int codSimulacion){
        
        int minutos = ventana;
        for (int i = 0; i < _vehiculos.size(); i++) {
            Vehicle vehiculo = _vehiculos.get(i);
            vehiculo.avanzar2(60*ventana, reloj,setOrd, almacen, getChoferes(),minIdSimulacion, codSimulacion);
            
        }
        
    }
    
    public void asignaRutasSinCalcular(ArrayList<Ruta> rutas){
        
        int numRuta= rutas.size();
        
        for (int i=0; i<numRuta;i++){
            this.getVehiculos().get(i).getRecorrido().setRutaActual(rutas.get(i));
            
        }
        
        
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

    /**
     * @return the ventana
     */
    public int getVentana() {
        return ventana;
    }

    /**
     * @param ventana the ventana to set
     */
    public void setVentana(int ventana) {
        this.ventana = ventana;
    }

    /**
     * @return the almacen
     */
    public Almacen getAlmacen() {
        return almacen;
    }

    /**
     * @param almacen the almacen to set
     */
    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    /**
     * @return the _choferes
     */
    public ArrayList<Chofer> getChoferes() {
        return _choferes;
    }

    /**
     * @param choferes the _choferes to set
     */
    public void setChoferes(ArrayList<Chofer> choferes) {
        this._choferes = choferes;
    }
    
}

