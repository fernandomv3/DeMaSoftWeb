/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;

/**
 *
 * @author Adrian
 */
public class utiles {
    
    public static String cast_int_to_String(int numChar,int id){
        
        String tempId= String.valueOf(id);
        
        for (;tempId.length()<numChar ;)tempId="0" + tempId;
        
        
        return tempId;
    }
}
