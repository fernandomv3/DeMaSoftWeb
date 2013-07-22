/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author IRVIN
 */
public class Reporte {
 
    private static String FILE = "";
    private static ArrayList<String> LISTACABECERA;
    private static JSONArray LISTA;
    private static ArrayList<String> LISTAAUX;
    private static String TITULO, FINI, FFIN;

    public Reporte(String file, ArrayList<String> listaCabecera, JSONArray lista,
                    ArrayList<String> listaAux, String titulo, Date fini, Date ffin){
        FILE = file;
        LISTACABECERA = listaCabecera;
        LISTA = lista;
        LISTAAUX = listaAux;
        TITULO = titulo;
        
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm:ss a");
        FINI = sdfFecha.format(fini);
        FFIN = sdfFecha.format(ffin);
         try {
              Document document = new Document();
              PdfWriter.getInstance(document, new FileOutputStream(FILE));
              document.open();
              
              Paragraph title = new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD,18,
                      Font.UNDERLINE,BaseColor.RED));
              title.setAlignment(Element.ALIGN_CENTER);
              document.add(title);
              
              document.add(new Paragraph(" "));
              document.add(new Paragraph("Desde " + FINI + " hasta " + FFIN));              
              document.add(new Paragraph("Fecha de Creación: " + sdfFecha.format(new Date())));
              document.add(new Paragraph("Hora de Creación: " + sdfHora.format(new Date())));
              document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
              document.add(new Paragraph(" "));
              addMetaData(document);       
              //addContent(document);
              
            //INICIO de creacion de  la tabla;             
              
            PdfPTable table = new PdfPTable(LISTACABECERA.size());
            
            for (int i=0;i<LISTACABECERA.size();i++) {
                PdfPCell c1 = new PdfPCell(new Phrase(LISTACABECERA.get(i), FontFactory.getFont(FontFactory.HELVETICA_BOLD,12,Font.BOLD, BaseColor.BLACK)));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                c1.setVerticalAlignment(Element.ALIGN_CENTER);
                c1.setBackgroundColor(BaseColor.CYAN);
                c1.setBorderWidth(2);                
                table.addCell(c1);
            }

            JSONObject o = null;
            for ( int k = 0 ; k < LISTA.size(); k++ ){
                o = (JSONObject) LISTA.get(k);
                for (int m = 0 ; m < LISTAAUX.size() ; m++) {
                    table.addCell(o.get(LISTAAUX.get(m)).toString());
                }
            }
            
            document.add(table);
            
            //FIN de creacion
            
            document.close();
            } catch (Exception e) {
              e.printStackTrace();
            } 
    }

    private static void addMetaData(Document document) {
              document.addTitle(TITULO);
              document.addSubject("Using iText");
              document.addKeywords("Java, PDF, iText");
              document.addAuthor("DeMaSoft");
              document.addCreator("DeMaSoft");
    }     
}