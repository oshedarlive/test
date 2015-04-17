/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;

import java.io.*;

/**
 *
 * @author hoshi
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println(100*0.1);
        if(1==1) return;
        SortedFastGrowingList<String> list=new SortedFastGrowingList<String>(true);
        list.add("haha");
        ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("d:/test.txt"));
        os.writeObject(list);
        os.close();
        if(1==1) return;
        Object[] array2=new Object[0];
        tt(null);
        if(1==1) return;
        int[] array=new int[40];
        int ctr;
        for(ctr=0;ctr<40;ctr++)
            array[ctr]=ctr;
        long startmsec=0;
        long endmsec=0;
        startmsec=System.nanoTime();
        System.arraycopy(array, 0, array, 1, 39);
        endmsec=System.nanoTime();
        System.out.println("time taken for system.arraycopy=" + (endmsec-startmsec));

        startmsec=System.nanoTime();
        for(ctr=1;ctr<40;ctr++)
            array[ctr]=array[ctr-1];
        endmsec=System.nanoTime();
        System.out.println("time taken for simple copy=" + (endmsec-startmsec));
        System.out.println("startmsec=" + startmsec + ", endmsec=" + endmsec);

    }
    static Comparable tt(Comparable a){
        Comparable b=a;
        return b;
    }

}
