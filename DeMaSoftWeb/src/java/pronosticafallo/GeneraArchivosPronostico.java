/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pronosticafallo;

import DeMaSoft.Reloj;
import DeMaSoft.RouteCalculator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import models.Punto;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import models.OrdenEntrega;
import java.util.Random;
import models.Cliente;
import models.Mapa;
import static pronosticafallo.PronosticaFallo.procesaDatos;

/**
 *
 * @author Adrian
 */
public class GeneraArchivosPronostico {
    
    
    private int exactitud=1000;
    private int maxId=0;
    private static int check=0;
    
    
    public  ArrayList<OrdenEntrega> generaArchivo(int dia, Reloj reloj, Mapa mapa) throws Exception{
        
        Random randomGenerator= new Random();
        
        ArrayList<OrdenEntrega> entregas=new ArrayList<>();
        
        ArrayList<ArrayList<Double>>datosFormulaPaquetes =TurnoPronostico.getPolinomioPaquetes();
        
        ArrayList<Double> formulaPaquetes1=datosFormulaPaquetes.get(0);
        ArrayList<Double> formulaPaquetes2=datosFormulaPaquetes.get(1);
        ArrayList<Double> formulaPaquetes3=datosFormulaPaquetes.get(2);
        
        int div=1;
        
        if (check==1) div=22;
        
        /*int numPaquetesturno1= (int)(formulaPaquetes1.get(2)*Math.pow(dia, 2) + formulaPaquetes1.get(1)*Math.pow(dia, 1) + formulaPaquetes1.get(0)*Math.pow(dia, 0) )/4;
        int numPaquetesturno2= (int)(formulaPaquetes2.get(2)*Math.pow(dia, 2) + formulaPaquetes2.get(1)*Math.pow(dia, 1) + formulaPaquetes2.get(0)*Math.pow(dia, 0) )/4;
        int numPaquetesturno3= (int)(formulaPaquetes3.get(2)*Math.pow(dia, 2) + formulaPaquetes3.get(1)*Math.pow(dia, 1) + formulaPaquetes3.get(0)*Math.pow(dia, 0) )/4;*/

        int numPaquetesturno1= ((int)(36 + randomGenerator.nextInt(2)*1 + Math.pow(dia, TurnoPronostico.getExponenciales().get(0))))/div;
        int numPaquetesturno2= ((int)(60 + randomGenerator.nextInt(2)*1 + Math.pow(dia, TurnoPronostico.getExponenciales().get(1))))/div;
        int numPaquetesturno3= ((int)(58 + randomGenerator.nextInt(2)*1 + Math.pow(dia, TurnoPronostico.getExponenciales().get(2))))/div;
        /*
        System.out.println("Numpaq turno 1 dia " + dia+": " + numPaquetesturno1);
        System.out.println("Numpaq turno 2 dia " + dia+": " + numPaquetesturno2);
        System.out.println("Numpaq turno 3 dia " + dia+": " + numPaquetesturno3);
        */
        ArrayList<Double> factoresTamaño=CuartodeTurnoPronostico.getFactorTamaño();
        
        ArrayList<HashMap> factoresprioridad=CuartodeTurnoPronostico.getFactorPrioridad();
        
        
        int hora=0;
        double minuto=0;
        
        for (int turno=0; turno<3;turno++){
        //Turno1
            hora=turno*8;
            minuto=0;
            double sumFactores= 0;    
            int numPaquetesturno=0;
            int paqReales=0;
            
            if (turno==0)numPaquetesturno=numPaquetesturno1;
            if (turno==1)numPaquetesturno=numPaquetesturno2;
            if (turno==2)numPaquetesturno=numPaquetesturno3;

            for (int i=0 + 4*turno;i<4*(turno + 1); i++){

                sumFactores=sumFactores + factoresTamaño.get(i);

            }
            
            int compRand=91;
            
            if (check==1) compRand=50;
            
            for(int cuarto=0;cuarto<4;cuarto++){

                int numPaquetesCuarto= (int)(factoresTamaño.get(cuarto + 4*turno)*numPaquetesturno/sumFactores);

                int paquetesRepartidos=0;

                for (;paquetesRepartidos<numPaquetesCuarto;){
                    
                    int numero=(numPaquetesCuarto-paquetesRepartidos)/2;
                    
                    int temp=0;
                    
                   
                    if (numero>0) temp= randomGenerator.nextInt(numero);
                    if (temp==0) temp++;

                    
                    if (temp>3){
                        
                        if (randomGenerator.nextInt(100)<compRand){
                            
                            temp=randomGenerator.nextInt(3);
                            
                        }
                        
                    }                    
                    
                    
                    if (temp==0) temp++;
                    paqReales=paqReales+temp;
                    paquetesRepartidos=paquetesRepartidos+temp;
                    OrdenEntrega orden= new OrdenEntrega();

                    orden.setNumPaquetes(temp);

                    orden.setMinutoPedido((int)minuto);

                    orden.setHoraPedido(hora);

                    int x= randomGenerator.nextInt(30)+30;
                    int y= randomGenerator.nextInt(20)+20;

                    if (x==45 && y==30){ x=40;y=30;}


                    orden.setPuntoEntrega(mapa.getPuntos().get(y + x * mapa.getAlto()));

                    HashMap mapeoPrioridad= factoresprioridad.get(cuarto + 4*turno);

                    double sumaFactor=0;
                    ArrayList<Integer> prioridades= new ArrayList<>();
                    ArrayList<Integer> rango= new ArrayList<>();

                    for (int prio=0;prio<=24;prio++){

                       double tempsumFactor=  mapeoPrioridad.get(prio)==null? 0:(double)mapeoPrioridad.get(prio);
                       sumaFactor = sumaFactor + tempsumFactor;
                       
                       if (mapeoPrioridad.get(prio)!=null){
                        prioridades.add(prio);
                        rango.add((int)(sumaFactor*exactitud));
                       }
                    }

                    int tempSolRand= randomGenerator.nextInt((int)(sumaFactor*exactitud));

                    int prioridadElegida=0;
                    for(int prioridadesPermitidas=0;prioridadesPermitidas<prioridades.size();prioridadesPermitidas++){

                        if (rango.get(prioridadesPermitidas)<tempSolRand) prioridadElegida++;
                        
                        
                        
                    }

                    orden.setPrioridad(prioridades.get(prioridadElegida));                    
                    entregas.add(orden);
                    
                    Calendar cal= Calendar.getInstance();                    
                    cal.set(reloj.getAnho(),reloj.getMes()-1, reloj.getDia(), hora, (int)minuto);
                    orden.setFechaPedido(cal.getTime());
                    Calendar calMax= Calendar.getInstance();                    
                    calMax.set(reloj.getAnho(),reloj.getMes()-1, reloj.getDia(), hora, (int)minuto);
                    calMax.add(Calendar.HOUR_OF_DAY, orden.getPrioridad());
                    orden.setFechaMaxEntrega(calMax.getTime());                                        

                    orden.setIdOrden(randomGenerator.nextInt(400)+1);

                    double tempMinuto= 2*60*temp/numPaquetesCuarto;
                    minuto=minuto+tempMinuto;

                    if ((int)minuto>=60){ hora++;minuto=0;}

                    if (hora>=((1+cuarto)*2 + 8*turno)) break;

                }

            }
            
            //System.out.println("Numero paquetes reales turno " + turno + ": " + paqReales);
        }
        int numEntregas= entregas.size();
        //System.out.println("Numero entregas dia" + dia + ": " + numEntregas);
        for (int i=0;i<numEntregas;i++){
            
            entregas.get(i).setIdOrden(maxId);
            maxId++;
        }
        
        return entregas;
    }
    
    public String generaContenido(ArrayList<OrdenEntrega>ordenes){
                
        StringBuffer contenido= new StringBuffer();
            
        for (int i=0; i<ordenes.size();i++){
            StringBuffer linea= new StringBuffer();
            
            int hora= ordenes.get(i).getHoraPedido();
            int minuto= ordenes.get(i).getMinutoPedido();
            
            String agregar="";
            if (hora<10)agregar="0";
            
            String agregar1="";
            if (minuto<10)agregar1="0";
            
            
            
            linea.append(agregar+hora+":"+agregar1+minuto+","+ordenes.get(i).getPuntoEntrega().getPosX()+","+ordenes.get(i).getPuntoEntrega().getPosY()+","+ordenes.get(i).getNumPaquetes()+","+100+","+ordenes.get(i).getPrioridad());
            
            
            
            contenido=contenido.append(linea.toString());
            
            
            contenido=contenido.append("\n");
        }
        
        return contenido.toString();
    }
    
    public void transformaaArchivo(ArrayList<OrdenEntrega>ordenes, String nombreArchivo) {
        
        String content = generaContenido(ordenes);
                                                                       
        
 
	File file = new File("C:/Users/Adrian/Documents/PUCP/DP1/"+nombreArchivo+".txt");
        
        try{
        
            if (!file.exists()) {
                    file.createNewFile();                                
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        }catch(Exception e){
            
            System.out.println(e.getMessage());
        }
        
    }
    
    /**
     * @return the check
     */
    public static int getCheck() {
        return check;
    }

    /**
     * @param aCheck the check to set
     */
    public static void setCheck(int aCheck) {
        check = aCheck;
    }
    
    
    public  ArrayList<OrdenEntrega> nuevoGeneraArchivo(int dia, Reloj reloj, Mapa mapa, int idPronostico) throws Exception{
        
        
        Random randomGenerator= new Random();
        
        ArrayList<OrdenEntrega> entregas=new ArrayList<>();
        
        int numPaquetesturno1= ((int)(36 + randomGenerator.nextInt(10) + Math.pow(dia,1.0222 /*TurnoPronostico.getExponenciales().get(0)*/)));
        int numPaquetesturno2= ((int)(60 + randomGenerator.nextInt(10) + Math.pow(dia,1.0555 /*TurnoPronostico.getExponenciales().get(1)*/)));
        int numPaquetesturno3= ((int)(58 + randomGenerator.nextInt(10) + Math.pow(dia,1.0345 /*TurnoPronostico.getExponenciales().get(2)*/)));
        
        int diaA= reloj.getDia();
        int mes= reloj.getMes();
        int anho= reloj.getAnho();
        int CantMaxPaquete= numPaquetesturno1+numPaquetesturno2+numPaquetesturno3;
        System.out.println("C= "+CantMaxPaquete);
        //reloj.getFechaActual().add(Calendar.DAY_OF_YEAR, dia);
        
        entregas.addAll(generarPaquete(diaA, mes , anho, 1, numPaquetesturno1, mapa ));
        entregas.addAll(generarPaquete(diaA, mes , anho, 2, numPaquetesturno2, mapa ));
        entregas.addAll(generarPaquete(diaA, mes , anho, 3, numPaquetesturno3, mapa ));
                
        
        /*System.out.println("Cantidad Paquete: " +  CantMaxPaquete);
        System.out.println("Numpaq turno 1 dia " + dia+": " + numPaquetesturno1);
        System.out.println("Numpaq turno 2 dia " + dia+": " + numPaquetesturno2);
        System.out.println("Numpaq turno 3 dia " + dia+": " + numPaquetesturno3);*/
        //this.transformaaArchivo(entregas, "dat"+reloj.getFechaString("yyyyMMdd"));
        
        int numEntregas= entregas.size();
        //System.out.println("Numero entregas dia" + dia + ": " + numEntregas);
        
        PronosticaFallo.insertaPaquetesxdiaPronostico(idPronostico, reloj.getFechaString("yyyy-MM-dd HH:mm:ss"), CantMaxPaquete);
        
        for (int i=0;i<numEntregas;i++){
            
            entregas.get(i).setIdOrden(maxId);
            maxId++;
        }
        
        return entregas;
    }
        
    public ArrayList<OrdenEntrega> generarPaquete(int dia, int mes , int anho,int Turno, int CantMaxPaquete, Mapa mapa ){
        
        Random randomGenerator= new Random();
        ArrayList<OrdenEntrega> entregas=new ArrayList<>();
        int prec=1000;
        double sumdiv=0;
        String horabase;
        
        ArrayList<Punto> bloqueos=mapa.getBloqueados();
        
        
        ArrayList<Double>divisores = sacaDivisores(Turno);
        
        for (int i=0; i< divisores.size();i++){
            
            sumdiv=sumdiv+divisores.get(i);
        }
        sumdiv=sumdiv*prec;
        
        ArrayList<Integer> parte= new ArrayList<>();
        

        ArrayList<Integer> frecEntrega= new ArrayList<>();

        int partes;
        int nroReg;
        float delta, porcion;
        int asigna;
        int i,j;

        String tmphora="00:00";
        int posx, posy;
        int npaquete;
        int idcliente;
        int hlimite;
        ArrayList<Integer> tlimite= new ArrayList<>(); 
        tlimite.add(1);
        tlimite.add(2);
        tlimite.add(4);
        tlimite.add(8);
        tlimite.add(24);         


        int tfrecEntrega=0;
        int imax;
        
        
        
        partes = 0;
        for (i=0; i<=2; i++) {
          parte.add(i, (int)(CantMaxPaquete*divisores.get(i)*prec/sumdiv));          
          partes += parte.get(i);
        }
        parte.add(3,CantMaxPaquete - partes);
        
        horabase="00:00";
        if (Turno==1){
            horabase="00:00";
           
            
        }
        if (Turno==2){
           horabase="08:00";
            
        }
        if (Turno==3){
           horabase="16:00";
            
        }
        
        
        for (i=0; i<=3; i++){
            //  para cada tramo, que son 4 tramos cada uno de 120 minutos = 2 horas.
            if (parte.get(i)<3)
              {nroReg = 2;
                parte.set(i, 4);
              }
            else
              nroReg= 2 +  (int)(parte.get(i)/5) + ( randomGenerator.nextInt(( (int)(parte.get(i)/3) )) );
        
            
            
            
            
            
            porcion= (parte.get(i)/nroReg);		
            while (porcion>12){
                nroReg++;
                porcion= (parte.get(i)/nroReg);		
            }
            if (nroReg<120) // si es un número razonable ok, si son demasiados... debo ajustarlo
                delta = (int)(( 120.0 / nroReg *100)/100);
                // delta es cada cuando se genera el pedido en el tiempo
            else
            {	  delta = 1;   //0.4;	
                  nroReg = 120;  // 99
            }
            
            
            
            
            
            
            for (j=0; j<=nroReg; j++) {
               if (parte.get(i)<3)
                  asigna = 1;
               else
                //     asigna = round( 1+ rand()%((int)(parte[i]/2)));
                      asigna = (int)(1+ randomGenerator.nextInt(((int)(porcion))));
              // patron: hh:mm,posx,posy,NroPaq,IdCliente,hlimite
              // arma los datos para grabar      
               tmphora= calchorapedido(horabase, (int)(delta*j+120*i));
        //   printf("La horar pedido es %s \n", tmphora);
        //   getch();      
               posx = randomGenerator.nextInt(150);
               posy = randomGenerator.nextInt(100);
        
        
               while ((posx % 45) == 0) posx++;
               while ((posy % 30) == 0) posy++;
               
               npaquete = asigna;
               idcliente = randomGenerator.nextInt(400) +1;
        
               // se fija el límite máximo de 24 hrs.
                hlimite=tlimite.get(4);

                if (tfrecEntrega==0) {
                 switch (Turno) {
                   case 1:
                     //entrega={1,2,5,8,4};
                     frecEntrega.add(0,1);
                     frecEntrega.add(1,2);
                     frecEntrega.add(2,5);
                     frecEntrega.add(3,8);
                     frecEntrega.add(4,4);
                     tfrecEntrega= 20;
                     break;
                   case 2:	
                     //entrega={5,6,5,3,1};
                     frecEntrega.add(0,5);
                     frecEntrega.add(1,6);
                     frecEntrega.add(2,5);
                     frecEntrega.add(3,3);
                     frecEntrega.add(4,1);
                     tfrecEntrega= 20;
                     break;
                    case 3:	
                     //entrega={5,5,5,3,2};
                     frecEntrega.add(0,5);
                     frecEntrega.add(1,5);
                     frecEntrega.add(2,5);
                     frecEntrega.add(3,3);
                     frecEntrega.add(4,2);
                     tfrecEntrega= 20;
                     break;
               }

             }
        
        imax = itemmaximo(frecEntrega);
	hlimite=tlimite.get(imax);
	frecEntrega.set(imax, frecEntrega.get(imax)-1);
        tfrecEntrega--;           

      if ( ! ((hlimite==1) || (hlimite==2) || (hlimite==4) || (hlimite==8) || (hlimite==24))) {
	  hlimite = tlimite.get(randomGenerator.nextInt(5));
	  };      

        // almacen.... X= 45
        //             Y= 30      
              if ((hlimite==1)||(hlimite==2)||(hlimite==4)){  // pendiente negativo....      
                 posx = 45 + (randomGenerator.nextInt(15*hlimite));
                 posy = 30 + (randomGenerator.nextInt(15*hlimite));
              }	

        //      printf ("Resultado = %d entrega= %d  paquete=%d tiempo= %d\n",i+1,j+1,asigna, delta*j );
        //     printf("hora %s, x= %d, y= %d, paq= %d, Clte= %d, hlim=%d \n", tmphora, posx, posy, npaquete, idcliente, hlimite );
              OrdenEntrega orden= new OrdenEntrega();
             
             
             orden.setNumPaquetes(npaquete);
             orden.setPrioridad(hlimite);
             Cliente cliente= new Cliente();
             cliente.setIdPersona(idcliente);
             orden.setCliente(cliente);
             orden.setPuntoEntrega(mapa.getPuntos().get(posy + posx * mapa.getAlto())); 
             
             for(int nBloq=0; nBloq<bloqueos.size();nBloq++){
                    
                   if (orden.getPuntoEntrega().equals(bloqueos.get(nBloq))){
                       
                       orden.setPuntoEntrega(mapa.getPuntos().get((posy-1) + (posx-1) * mapa.getAlto())); 
                       nBloq=0;
                   }
                    
                }
             
             
             int hora= Integer.parseInt(tmphora.substring(0, 2));
             int minuto= Integer.parseInt(tmphora.substring(3));
             if (hora>=24 && minuto>=0){
                   hora=23;
                   minuto=59;
                   
               }
             Calendar cal= Calendar.getInstance();                    
             cal.set(anho,mes-1, dia, hora, minuto);
             orden.setFechaPedido(cal.getTime());
             Calendar calMax= Calendar.getInstance();                    
             calMax.set(anho,mes-1, dia, hora, (int)minuto);
             calMax.add(Calendar.HOUR_OF_DAY, orden.getPrioridad());
             orden.setFechaMaxEntrega(calMax.getTime());
             orden.setHoraPedido(hora);
             orden.setMinutoPedido(minuto);

             entregas.add(orden);

        //     getch();
               parte.set(i, parte.get(i)-asigna);
            }
            // hora = hora + delta * (nroReg-1);
             

        ///////                                        
             
              if (parte.get(i) >0)
                asigna = parte.get(i);
              else 
                    asigna = 1;
              
              int numdiv=0;
              float ndelta= delta;
              ArrayList<Integer> asignaciones= new ArrayList<>();
              int num1=0;
              int numbucle=0;
              while(asigna>12){
                  
                  if ((asigna%2)==1){
                      
                    asigna=asigna/2;
                    ndelta=delta/2;
                    numdiv=numdiv*2;
                    if (numdiv==0)numdiv=2;
                    num1=num1+numbucle+1;
                  }else{
                    asigna=asigna/2;
                    ndelta=delta/2;
                    numdiv=numdiv*2;                    
                    if (numdiv==0)numdiv=2;
                  }
                  numbucle++;
                  
              }
              if (numbucle>0){
                for(int numAsig=0; numAsig<numdiv;numAsig++){

                    asignaciones.add(asigna);
                    if (num1>0){
                        
                        asignaciones.set(numAsig,asignaciones.get(numAsig)+1);
                        num1--;
                    }
                }
              }
              
              if (ndelta<0.5) ndelta=1;
              
              if (numdiv==0)numdiv++;
              
              for (int numasigna=0; numasigna<numdiv;numasigna++){
                tmphora=calchorapedido(horabase, (int)(delta*(nroReg+1) + ndelta*(numasigna) +120*i) );
                
                posx = randomGenerator.nextInt(150);
                posy = randomGenerator.nextInt(100);

          // almacen.... X= 45
          //             Y= 30
                while ((posx % 45) == 0) posx++;
                while ((posy % 30) == 0) posy++;   	
                
                
                

                if (numbucle>0)
                    npaquete = asignaciones.get(numasigna);
                else
                    npaquete=asigna;
                idcliente = randomGenerator.nextInt(400) +1;

                hlimite = tlimite.get(randomGenerator.nextInt(5));
                if ( !((hlimite==1) || (hlimite==2) || (hlimite==4) || (hlimite==8)|| (hlimite==24))  )       
                      { hlimite = tlimite.get(randomGenerator.nextInt(5));
                  }
                if ((hlimite==1)||(hlimite==2)||(hlimite==4)){  // pendiente negativo....      
                   posx = 45 + (randomGenerator.nextInt(15*hlimite));
                   posy = 30 + (randomGenerator.nextInt(15*hlimite));
                }	
                                

          // +++   OJO OJO OJO OJO OJO OJO

          //    printf("hora %s, x= %d, y= %d, paq= %d, Clte= %d, hlim=%d \n", tmphora, posx, posy, npaquete, idcliente, hlimite );
               //fprintf(fout, "%s,%d,%d,%d,%d,%d\n", tmphora, posx, posy, npaquete, idcliente, hlimite);

               OrdenEntrega orden= new OrdenEntrega();


               orden.setNumPaquetes(npaquete);
               orden.setPrioridad(hlimite);
               Cliente cliente= new Cliente();
               cliente.setIdPersona(idcliente);
               orden.setCliente(cliente);
               orden.setPuntoEntrega(mapa.getPuntos().get(posy + posx * mapa.getAlto())); 

               for(int nBloq=0; nBloq<bloqueos.size();nBloq++){
                    
                   if (orden.getPuntoEntrega().equals(bloqueos.get(nBloq))){
                       
                       orden.setPuntoEntrega(mapa.getPuntos().get(posy-1 + (posx-1) * mapa.getAlto())); 
                       nBloq=0;
                   }
                    
                }


               int hora= Integer.parseInt(tmphora.substring(0, 2));
               int minuto= Integer.parseInt(tmphora.substring(3));
               
               if (hora>=24 && minuto>=0){
                   hora=23;
                   minuto=59;
                   
               }
                   

               Calendar cal= Calendar.getInstance();                    
               cal.set(anho,mes-1, dia, hora, (int)minuto);
               orden.setFechaPedido(cal.getTime());
               Calendar calMax= Calendar.getInstance();                    
               calMax.set(anho,mes-1, dia, hora, (int)minuto);
               calMax.add(Calendar.HOUR_OF_DAY, orden.getPrioridad());
               orden.setFechaMaxEntrega(calMax.getTime());
               orden.setHoraPedido(hora);
               orden.setMinutoPedido(minuto);

               entregas.add(orden);
            }
             
        //    printf ("Resultado tramo= %d entrega= %d paquete=%d tiempo= %d\n",i+1,nroReg-1+1,asigna, delta*(nroReg-1) );
        //    printf ("***\n");
          //  getch();
          }
        int paqsum=0;
        for(int paq=0; paq<entregas.size();paq++){
            paqsum=paqsum+ entregas.get(paq).getNumPaquetes();
            
        }
        //System.out.println("El num de paquetes es: "+paqsum);
        return entregas;
        
    }
    
    public ArrayList<Double> sacaDivisores(int turno){
        
        ArrayList<Double>divisores= new ArrayList<>();
        
        ArrayList<Double> factoresTamaño=CuartodeTurnoPronostico.getFactorTamaño();
        
        if (turno==1){
            
            for (int i=0;i<4;i++)
                divisores.add(factoresTamaño.get(i));
            
        }
        if (turno==2){
            for (int i=4;i<8;i++)
                divisores.add(factoresTamaño.get(i));
            
        }
        if (turno==3){
            for (int i=8;i<12;i++)
                divisores.add(factoresTamaño.get(i));
            
        }
        
        return divisores;
        
    }
    
    
    private String calchorapedido (String horabase, int tiempo)                        
    { // llega 00:00 (base) y se le suma tiempo en hora
        
       String horario="";
       String chor="00";
       String cmin="00";
       int nhor=0;
       int nmin=0;
       int totalmin = 0;

       chor=horabase.substring(0, 2);      // copio el hora
     //  chor[2]='\0';
       cmin=horabase.substring(3, 5);  // copio el minuto
       //cmin[2]='\0';

       nhor = Integer.parseInt(chor);
       nmin = Integer.parseInt(cmin);

       totalmin = 60 * nhor + nmin;

       totalmin += tiempo;

       nhor = (int) ( totalmin / 60 ) ;
       nmin = totalmin % 60 ;

    // convierte la hora.
       if (nhor<10)
        {
         chor="00";           
         chor=chor.substring(0,1) + nhor;
       }
       if (nhor>=10) {
         chor="10";
         chor=chor.substring(0,1) +(nhor-10);	  	
       }
       if (nhor>=20) {
         chor="20";
         chor=chor.substring(0,1) +(nhor-20);	  	
       }
    // convierte los minutos


       if (nmin<10)
        {
            // colocar 0 delante del mes
         cmin="00";
            //
         cmin= cmin.substring(0,1) +nmin;	  	
       }
       if (nmin>=10) {
         cmin="10";
         cmin=cmin.substring(0,1) +(nmin-10);	  	
       }
       if (nmin>=20) {
            cmin="20";
            cmin=cmin.substring(0,1) +(nmin-20);	  	
       }
       if (nmin>=30) {
            cmin="30";
            cmin=cmin.substring(0,1) +(nmin-30);	  	
       }
       if (nmin>=40) {
            cmin="40";
            cmin=cmin.substring(0,1) +(nmin-40);	  	
       }
       if (nmin>=50) {
            cmin="50";
            cmin=cmin.substring(0,1) +(nmin-50);	  	
       }
       horario= chor + ":" + cmin;
       
       return horario;
    }
    
    int itemmaximo(ArrayList<Integer> numero) {

    // devuelve la posición de mayor valor
    int itemp, imax;
     int i;
     // comienza en 0 como si fuera array
      itemp = 5;  // señal de error
      imax = 0;
      for(i=0; i<=4; i++)
        if (imax<numero.get(i)){
          itemp= i;
          imax = numero.get(i);    	
        }
       return itemp;
    }
}
