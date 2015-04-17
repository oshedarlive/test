/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;

/**
 *
 * @author hoshi
 */
public class Util {

    public static String toCsvString(String[] values){
        if(values==null)
            return "";
        StringBuilder sb=new StringBuilder();
        int ctr;
        for(ctr=0;ctr<values.length-1;ctr++)
            sb.append(values[ctr]).append(',');
        sb.append(values[ctr]);
        return sb.toString();
    }

    public static String toCsvString(java.util.List list,boolean wrapQuotes){
        int size=list.size();
        int ctr;
        StringBuilder sb=new StringBuilder(150);
        if(wrapQuotes){
            for(ctr=0;ctr<size-1;ctr++)
                sb.append('"').append(list.get(ctr)).append("\",");
            if(size>0)
                sb.append('"').append(list.get(ctr)).append('"');
        }
        else{
            for(ctr=0;ctr<size-1;ctr++)
                sb.append(list.get(ctr)).append(',');
            if(size>0)
                sb.append(list.get(ctr));
        }
        return sb.toString();
    }

    public static String toCsvString(java.util.List list,char wrapChar){
        int size=list.size();
        int ctr;
        StringBuilder sb=new StringBuilder(150);
        for(ctr=0;ctr<size-1;ctr++)
            sb.append(wrapChar).append(list.get(ctr)).append(wrapChar).append(',');
        if(size>0)
            sb.append(wrapChar).append(list.get(ctr)).append(wrapChar);
        return sb.toString();
    }

    public static String duplicate(char c,char seperator,int count){
        if(count==0) return "";
        StringBuilder sb=new StringBuilder(75);
        int ctr;
        for(ctr=1;ctr<count;ctr++)
            sb.append(c).append(seperator);
        sb.append(c);
        return sb.toString();
    }

    public static String duplicate(String txt,char seperator,int count){
        if(count==0) return "";
        StringBuilder sb=new StringBuilder(75);
        int ctr;
        for(ctr=1;ctr<count;ctr++)
            sb.append(txt).append(seperator);
        sb.append(txt);
        return sb.toString();
    }

    public static void fillList(java.util.List list,Object[] items){
        if(items==null)
            return;
        for(Object item:items){
            list.add(item);
        }
    }

}
