/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dawid
 */
public class ValuesFormater {
    
    private static SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");
    private static Pattern shortNamesPattern = Pattern.compile("([A-Z][A-Z][A-Z][A-Z0-9]*).*");
    
    public static Date stringToDate(String date) throws ParseException{
        return formatter.parse(date);
    }
    
    public static String dateToString(Date date){
        return formatter.format(date);
    }
    
    public static String toShortName(String name){
        Matcher m = shortNamesPattern.matcher(name);
        if(m.matches())
            return m.group(1);
        return null;
    }
    
}
