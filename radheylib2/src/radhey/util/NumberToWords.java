/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;

/**
 *
 * @author hoshi
 */
public class NumberToWords {
    private static String[] units ={"", " One", " Two", " Three", " Four", " Five",
         " Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve",
         " Thirteen", " Fourteen", " Fifteen",  " Sixteen", " Seventeen",
         " Eighteen", " Nineteen"};
    private static String[] tens =  {"", "Ten", " Twenty", " Thirty", " Forty", " Fifty",
         " Sixty", " Seventy", " Eighty"," Ninety"};
    
    private String[] weightWords;
    private long[] weightValues;

    //eg new NumberToWords(new long[]{100,1000,100000,10000000}, new String[] {"Hundred", "Thousand", "Lakh", "Crore"});
    public NumberToWords(long[] weightValues,String[] weightWords) {
        this.weightWords=weightWords;
        this.weightValues=weightValues;
        if(weightWords!=null){//prefix space if not there
            for(int ctr=0;ctr<weightWords.length;ctr++){
                if(weightWords[ctr].length()>0){
                    if(!Character.isWhitespace(weightWords[ctr].charAt(0)))
                        weightWords[ctr]=" " + weightWords[ctr];
                }
            }
        }
    }

    private static String twoNum(int num){
        if (num>19)
            return tens[num/10]+units[num%10];
        return units[num];
    }

    public String convertToWords(long num){
        if(weightWords==null || weightValues==null)
            return twoNum((int)(num%100));
        return toWords(num, true);
    }

    private String toWords(long num,boolean addAnd){
         String str = "";
         if(num==0)
             return "Zero";
         if (num <0)
             return toWords(Math.abs(num),true);

         for(int ctr=weightValues.length-1;ctr>=0;ctr--){
             if(num>=weightValues[ctr]){
                str +=toWords(num/weightValues[ctr],false)+weightWords[ctr];
                num = num%weightValues[ctr];
             }
         }
         if(num>0){
             if(addAnd)
                return str + " and" + twoNum((int)num);
             return str + twoNum((int)num);
        }
         return str;
    }

    public static void main(String[] args){
        NumberToWords nToW=new NumberToWords(new long[]{100,1000,100000,10000000}, new String[] {"Hundred", "Thousand", "Lakh", "Crore"});
        System.out.println(nToW.convertToWords(879500005l));
    }
}
