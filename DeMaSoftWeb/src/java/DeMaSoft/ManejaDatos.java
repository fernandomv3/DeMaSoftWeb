/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;

//import static DeMaSoft.RouteCalculator.mapa;
import models.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import pronosticafallo.CuartodeTurnoPronostico;
import pronosticafallo.DiaPronostico;
import pronosticafallo.RouteCalculatorPronostico;
import pronosticafallo.TurnoPronostico;

/**
 *
 * @author Adrian
 */
@Component
@RequestMapping("/simulacion")
public class ManejaDatos implements Runnable{
    private static int idPedido =0;
    private Reloj _relojDatos;
    private String prefijo;
    private boolean _detente;
    private int codSimulacion;
    private int maxIdSacaSet=-1;
    
    public static ArrayList<Punto> leeBloqueos(ServletContext servelt, Mapa mapa){
        ArrayList<Punto> bloqueados = new ArrayList<>();
        String nombreArchivo="bloqueados.txt";
        String ruta ="";
        if(servelt != null){
            ruta=servelt.getRealPath(""); 
            ruta = ruta + "/WEB-INF/files/";
        }
        else{
            ruta = ruta + "web/WEB-INF/files/";
        }
        File archivo = new File (ruta+nombreArchivo);
        BufferedReader br = null;
        if (!archivo.exists()) return null;
        try{
            FileReader fr = new FileReader (archivo);
            br= new BufferedReader(fr);
            String linea="";
            while (true){
                linea=br.readLine();
                if(linea==null || linea.equals("")) break;
                String[] tokens = linea.split(",");
                int x = Integer.parseInt(tokens[0]);
                int y = Integer.parseInt(tokens[1]);
                if(x > 100 || y > 150)continue;
                Punto p = mapa.getPuntos().get(y + x * mapa.getAlto());
                bloqueados.add(p);
            }
            fr.close();
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }
        return bloqueados;
    }
   
    public static boolean existeSiguienteArchivo(Reloj reloj){
        String nombreArchivo="dat"+  reloj.getFechaString("yyyyMMdd") ;// Poner en trunk/Demasoft
        String ruta="";
        if(RouteCalculator.getInstancia().getServletContext() != null){
            ruta=RouteCalculator.getInstancia().getServletContext().getRealPath(""); 
            ruta = ruta + "/WEB-INF/files/";
        }
        else{
            ruta = ruta + "web/WEB-INF/files/";
        }
        File archivo = new File (ruta+nombreArchivo+".txt");
        return archivo.exists();
    }
    
    public static ArrayList<OrdenEntrega> leeArchivo(String prefijo, Reloj reloj, Mapa mapa) throws Exception{        
        ManejaDatos m= new ManejaDatos();
        ArrayList<OrdenEntrega> pedidos= new ArrayList<>();
        String nombreArchivo=prefijo+  reloj.getFechaString("yyyyMMdd") ;// Poner en trunk/Demasoft
        String ruta="";
        if(RouteCalculator.getInstancia().getServletContext() != null){
            ruta=RouteCalculator.getInstancia().getServletContext().getRealPath(""); 
            ruta = ruta + "/WEB-INF/files/";
        }
        else{
            ruta = ruta + "web/WEB-INF/files/";
        }
        File archivo = new File (ruta+nombreArchivo+".txt");
        BufferedReader br = null;
        if (!archivo.exists()) return null;
        try{
            FileReader fr = new FileReader (archivo);
            br= new BufferedReader(fr);
            String linea="";          
            while (true){
                Calendar c = (Calendar)reloj.getFechaActual().clone();
                linea=br.readLine();
                if(linea==null || linea.equals("")) break;
                String hora="";
                String minuto="";
                String posX="";
                String posY="";
                String numPaquete="";
                String idCliente="";
                String hLimit="";
                Integer idSimulacion=1;
                
                int i=0;
                for (; (i<linea.length() && linea.charAt(i)!=':');i++){
                    
                    hora=hora + linea.charAt(i);
                    
                }
                                                
                for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){
                    
                    minuto=minuto + linea.charAt(i);
                    
                }
                
                for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){
                    
                    posX=posX + linea.charAt(i);
                    
                }
                for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){
                    
                    posY=posY + linea.charAt(i);
                    
                }
                
                for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){
                    
                    numPaquete=numPaquete + linea.charAt(i);
                    
                }
                
                for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){
                    
                    idCliente=idCliente + linea.charAt(i);
                    
                }
                for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){
                    
                    hLimit=hLimit + linea.charAt(i);
                    
                }
                
                
             //   Cliente.insertaCliente(Integer.parseInt(idCliente),"","","","","","","","",idSimulacion);
                OrdenEntrega pedido= new OrdenEntrega();
                
                
                pedido.setHoraPedido(Integer.parseInt(hora));
                pedido.setMinutoPedido(Integer.parseInt(minuto));
                int x =Integer.parseInt(posX);
                int y = Integer.parseInt(posY);
                Punto nodo = mapa.getPuntos().get(y+ x*mapa.getAlto());
                while (mapa.getBloqueados().contains(nodo)){
                    nodo = mapa.getPuntos().get((y-1)+ (x-1)*mapa.getAlto());
                }
                //hora de pedido en Date
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora));
                c.set(Calendar.MINUTE,Integer.parseInt(minuto));
                pedido.setFechaPedido(c.getTime());
                //
                pedido.setPuntoEntrega(nodo);
                Cliente cliente= new Cliente();
                cliente.setIdPersona(Integer.parseInt(idCliente));
                pedido.setCliente(cliente);
                pedido.setNumPaquetes(Integer.parseInt(numPaquete));
                pedido.setPrioridad(Integer.parseInt(hLimit));//no borrar
                pedido.setPrioridadInicial(pedido.getPrioridad());//lineas usadas para los incidentes
                //hora Max Pedido en Date
                c.add(Calendar.HOUR,Integer.parseInt(hLimit) );
                pedido.setFechaMaxEntrega(c.getTime());
                //
                pedido.setIdOrden(idPedido);
                idPedido++;
                pedidos.add(pedido);                                
            }
            fr.close();
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }
        
        return pedidos;
    }    
    
    public ArrayList<OrdenEntrega> sacaSetOrdenes2(ArrayList<OrdenEntrega> ordenes, Reloj reloj, int ventana){
        ArrayList<OrdenEntrega> subSet = new ArrayList<>();
        if(ordenes == null)return subSet;
        
        Calendar c= (Calendar)reloj.getFechaActual().clone();
        c.set(Calendar.SECOND, 0);
        Date now = c.getTime();
        c.add(Calendar.MINUTE,-ventana);
        Date prevTW =c.getTime();
        int numOrdenes = ordenes.size();
        for(int i=0; i < numOrdenes;i++){
            if(ordenes.get(i).getFechaPedido().compareTo(now) <=0 && 
               ordenes.get(i).getFechaPedido().compareTo(prevTW)>=0){
                
                if (ordenes.get(i).getIdOrden()>this.maxIdSacaSet ){ 
                    subSet.add(ordenes.get(i));
                    maxIdSacaSet=ordenes.get(i).getIdOrden();
                }
            }
        }
        return subSet;
    }
        
    public ArrayList<OrdenEntrega> sacaSetOrdenes(ArrayList<OrdenEntrega> ordenes ,int horaAct, int minAct, int ventana){
        
        ArrayList<OrdenEntrega> subset= new ArrayList<OrdenEntrega>();
        
        if (ordenes==null) return new ArrayList();
        
        int numOrdenes= ordenes.size();
        
        for (int i=0; i< numOrdenes;i++){
            
            if ((ordenes.get(i).getHoraPedido()<horaAct) || ((ordenes.get(i).getHoraPedido()==horaAct) && (ordenes.get(i).getMinutoPedido()<=minAct))){
                
                if (minAct>=ventana){
                    
                    if ((ordenes.get(i).getHoraPedido()==horaAct)&& ((minAct-ordenes.get(i).getMinutoPedido())<=ventana)){
                        
                        if (ordenes.get(i).getIdOrden()>this.maxIdSacaSet ){ 
                            subset.add(ordenes.get(i));
                            maxIdSacaSet=ordenes.get(i).getIdOrden();
                            
                        }
                    }
                    
                }else{
                    
                    if ((((horaAct-1)==ordenes.get(i).getHoraPedido()) && (ordenes.get(i).getMinutoPedido()>(60-ventana))) || ((horaAct-1)<=ordenes.get(i).getHoraPedido()) ){
                    
                        if (ordenes.get(i).getIdOrden()>this.maxIdSacaSet ){ 
                            subset.add(ordenes.get(i));
                            maxIdSacaSet=ordenes.get(i).getIdOrden();
                            
                        }
                    }
                    
                }

            }
            
        }
        
        
        return subset;
    }
    
    public ArrayList<DiaPronostico> sacaDatosPronostico(String prefijo, ArrayList<DiaPronostico>dias,Reloj relojP ,ServletContext servlet) throws Exception{
        
        dias= new ArrayList<>();
        int numdia=0;
        try{
            
            String ruta="";
            
            if(servlet != null){
                    ruta=servlet.getRealPath(""); 
                    ruta = ruta + "/WEB-INF/files/";
            }
            else{
                ruta = ruta + "web/WEB-INF/files/";
            }
            
            Reloj reloj = new Reloj();
            reloj.setReloj(0, 0, relojP.getDia(), relojP.getMes(), relojP.getAnho());
            while (true){                
                
                
                String nombreArchivo=prefijo+  reloj.getFechaString("yyyyMMdd") ;// Poner en trunk/Demasoft               
                
                File archivo = new File (ruta+nombreArchivo+".txt");                
                BufferedReader br = null;        
                if (!archivo.exists()) break;
                
                FileReader fr = new FileReader (archivo);
                br= new BufferedReader(fr);
                String linea="";
                int numPaqueteCuarto=0;
                int numPedidoCuarto=0;
                HashMap numpedidosxprioridad= new HashMap();
                int proxTurno=8;
                int proxCuarto=2;
                
                DiaPronostico dia= new DiaPronostico();                
                TurnoPronostico turno= new TurnoPronostico();
                CuartodeTurnoPronostico cuarto= new CuartodeTurnoPronostico();
                while (true){
                    
                    String hora="";
                    String minuto="";
                    String posX="";
                    String posY="";
                    String numPaquete="";
                    String idCliente="";
                    String hLimit="";
                    
                    
                    
                    linea=br.readLine();
                    if(linea==null || linea.equals("")){ 
                        
                        cuarto.setNumpaquetes(numPaqueteCuarto);
                        cuarto.setNumpedidos(numPedidoCuarto);
                        cuarto.setPrioridadxnumpedidos(numpedidosxprioridad);
                        turno.agregaCuarto(cuarto);                        
                        numPaqueteCuarto=0;
                        numPedidoCuarto=0;
                        numpedidosxprioridad= new HashMap();
                        proxCuarto=proxCuarto+2;
                        cuarto= new CuartodeTurnoPronostico();
                        
                        
                        dia.agregaCuarto(turno);
                        dia.setNumDia(numdia);
                        dia.setDia(reloj.getDia());
                        dia.setMes(reloj.getMes());
                        dia.setAnho(reloj.getAnho());
                        turno= new TurnoPronostico();
                        proxTurno=proxTurno+8;
                        
                        break;
                    }
                    

                    int i=0;
                    for (; (i<linea.length() && linea.charAt(i)!=':');i++){

                        hora=hora + linea.charAt(i);

                    }

                    for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){

                        minuto=minuto + linea.charAt(i);

                    }

                    for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){

                        posX=posX + linea.charAt(i);

                    }
                    for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){

                        posY=posY + linea.charAt(i);

                    }

                    for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){

                        numPaquete=numPaquete + linea.charAt(i);

                    }

                    for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){

                        idCliente=idCliente + linea.charAt(i);

                    }
                    for (i++; (i<linea.length() && linea.charAt(i)!=',');i++){

                        hLimit=hLimit + linea.charAt(i);

                    }

                    
                    if (Integer.valueOf(hora)<proxCuarto){
                        numPaqueteCuarto+=Integer.valueOf(numPaquete);
                        numPedidoCuarto++;
                        if (numpedidosxprioridad.get(hLimit)==null){
                            
                            numpedidosxprioridad.put(hLimit, 1);
                            
                        }else{
                            
                            numpedidosxprioridad.put(hLimit,(int)(numpedidosxprioridad.get(hLimit)) + 1);
                            
                        }
                        
                    }else{
                        
                        cuarto.setNumpaquetes(numPaqueteCuarto);
                        cuarto.setNumpedidos(numPedidoCuarto);
                        cuarto.setPrioridadxnumpedidos(numpedidosxprioridad);
                        turno.agregaCuarto(cuarto);                        
                        numPaqueteCuarto=0;
                        numPedidoCuarto=0;
                        numpedidosxprioridad= new HashMap();
                        proxCuarto=proxCuarto+2;
                        cuarto= new CuartodeTurnoPronostico();
                        numPaqueteCuarto+=Integer.valueOf(numPaquete);
                        numPedidoCuarto++;
                        if (numpedidosxprioridad.get(hLimit)==null){
                            
                            numpedidosxprioridad.put(hLimit, 1);
                            
                        }else{
                            
                            numpedidosxprioridad.put(hLimit,(int)(numpedidosxprioridad.get(hLimit)) + 1);
                            
                        }
                        
                    }
                    
                    if (!(Integer.valueOf(hora)<proxTurno)){
                        
                        dia.agregaCuarto(turno);
                        dia.setNumDia(numdia);
                        dia.setDia(reloj.getDia());
                        dia.setMes(reloj.getMes());
                        dia.setAnho(reloj.getAnho());
                        turno= new TurnoPronostico();
                        proxTurno=proxTurno+8;
                        
                    }

                }
            dias.add(dia);
            fr.close();
            reloj.saltaReloj(60*24);
            numdia++;
                        
            }
        }catch(Exception e){
            
            System.out.println(e.getMessage());
            return null;
        }
        return dias;
                
    }
    
    public int cargaDatosBD(String nombreArchivo, String fecha) throws Exception{
        
        String ruta="";
        if(RouteCalculator.getInstancia().getServletContext() != null){
            ruta=RouteCalculator.getInstancia().getServletContext().getRealPath(""); 
            ruta = ruta + "/WEB-INF/files/";
        }
        else{
            ruta = ruta + "web/WEB-INF/files/";
        }
        
        ruta=ruta+nombreArchivo+".txt";
        File archivo = new File (ruta);
        
        if (archivo.exists()){
            
            PreparedStatement pstmt = null;
            ResultSet rset = null;
            StringBuffer query = new StringBuffer();
            
            
            query.append(" LOAD DATA LOCAL INFILE ? INTO TABLE ORDENDEENTREGA ");
            query.append(" FIELDS TERMINATED BY ',' ");
            query.append(" LINES TERMINATED BY '\\n' ");
            query.append(" (@hora,puntoX,puntoY,numPaquetes,idPersona,prioridad) ");
            query.append(" SET horaPedido = concat(? , ' ' , @hora , ':00'), ");
            query.append(" horaEntrega = concat(? , ' ' , @hora , ':30'), ");
            query.append(" idSIMULACION = ? ");
            
            
            Connection con=GestorConexion.getConexion();
            try{
            
                
                pstmt=con.prepareStatement(query.toString());
                pstmt.setString(1, ruta);
                pstmt.setString(2, fecha);
                pstmt.setString(3, fecha);
                pstmt.setInt(4, this.codSimulacion );
                pstmt.execute();
            
            }catch(Exception e){
                
                return 2;                                                
            }finally{
                
                if (pstmt!=null) pstmt.close();
                
            }
            
        }else{
            
            return 0;//NO EXISTE EL ARCHIVO
        }
        
        return 1;
    }
    
        
    
    
    public static String cargasDatos(String prefijo,int dia, int mes, int año){
        
        String error="";
        
        //error= OrdenEntrega.borraOrdenesyAsociados();
        
        
        if (error.equals("")){
        
            Calendar dias= Calendar.getInstance();

            dias.set(año, mes-1, dia);

            SimpleDateFormat sdf1= new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd");

            int retorno=0;

            for (;;dias.add(Calendar.DAY_OF_MONTH, 1)){

                try{

                    //retorno=cargaDatosBD((prefijo + sdf1.format(dias.getTime())),sdf2.format(dias.getTime()));

                    if (retorno==0){
                        error="El archivo" + (prefijo + sdf1.format(dias.getTime())) + "no existe ";
                        break;
                    }

                }catch(Exception e){

                    error="Ocurrio un error";
                    break;                    
                }
            }                                                   
        }
        if (error.equals(""))error="Se cargo exitosamente los datos";
        return error;
    }
    
    
    
    
    
    
    public int cargarsDatosDia(String prefijo,int dia, int mes, int año){
        
        int error=0;
        
        //error= OrdenEntrega.borraOrdenesyAsociados();
        
        //if (!OrdenEntrega.reseteaIncremento().equals(""))error=1;
        if (error==0){
        
            Calendar dias= Calendar.getInstance();

            dias.set(año, mes-1, dia);

            SimpleDateFormat sdf1= new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd");

            int retorno=0;

            for (;;dias.add(Calendar.DAY_OF_MONTH, 1)){

                try{

                    retorno=cargaDatosBD((prefijo + sdf1.format(dias.getTime())),sdf2.format(dias.getTime()));

                    if (retorno==0){
                        error=1;
                        break;
                    }

                }catch(Exception e){

                    error=2;
                    break;                    
                }
            }                                                   
        }
        
        return error;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    public static void main(String[] args){//para pruebas

        try{
        
        //System.out.println(cargasDatos("dat",17,5,2012));
        //System.out.println(OrdenEntrega.reseteaIncremento());
        
        }catch(Exception e){
            System.out.println(e.getMessage());
            
        }
    }
    
    public int minIdOrden(int codSimulacion){
        
        int minOrden=0;
        
        PreparedStatement pstmt=null;
        ResultSet rset= null;
        
        StringBuffer query=new StringBuffer();
        
        query.append("SELECT  IFNULL(MIN(idOrden),-1) FROM inf2260981g2.ORDENDEENTREGA where idSimulacion=? ");
        
        try{
            Connection con = GestorConexion.getConexion();
            pstmt= con.prepareStatement(query.toString());
            pstmt.setInt(1, codSimulacion);
            rset=pstmt.executeQuery();
            
            if (rset.next()){
                
                minOrden=rset.getInt(1);
                
            }
            
        }catch(Exception e){
            
            minOrden=-1;
        }finally{
            try{
                if (pstmt!=null)pstmt.close();
                if (rset!=null)rset.close();
                
            }catch(Exception ex){
                
                System.out.println(ex.getMessage());
            }
            
        }
        
        
        return minOrden;
        
    }
    
    
    

    @Override
    public void run() {
        
        //OrdenEntrega.reseteaIncremento();
        
        while(!this._detente){
            
            if (this.cargarsDatosDia(prefijo, this._relojDatos.getDia(), this._relojDatos.getMes(), this._relojDatos.getAnho())!=0)break;
               
            this._relojDatos.getFechaActual().add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    /**
     * @return the _reloj
     */
    public Reloj getReloj() {
        return _relojDatos;
    }

    /**
     * @param reloj the _reloj to set
     */
    public void setReloj(Reloj reloj) {
        this._relojDatos =new Reloj();
        this._relojDatos.setReloj(0, 0, reloj.getDia(), reloj.getMes(), reloj.getAnho());
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
        return prefijo;
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    /**
     * @return the _detente
     */
    public boolean isDetente() {
        return _detente;
    }

    /**
     * @param detente the _detente to set
     */
    public void setDetente(boolean detente) {
        this._detente = detente;
    }

    /**
     * @return the codSimulacion
     */
    public int getCodSimulacion() {
        return codSimulacion;
    }

    /**
     * @param codSimulacion the codSimulacion to set
     */
    public void setCodSimulacion(int codSimulacion) {
        this.codSimulacion = codSimulacion;
    }
    
    
    
    public void correr(){
        Thread carga = new Thread(this);
        this._detente=false;
        carga.start();
        
    }
    
    public void detener(){
        this._detente=true;
    }
    
    
}
