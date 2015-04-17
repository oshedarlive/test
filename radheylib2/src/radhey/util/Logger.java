/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radhey.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Lappie
 */
public class Logger {

    static SimpleDateFormat errorDate = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss");
    List<String> errors = null;
    String logFileName = null;
    int capacity=5;;

    public Logger(String fileName) {
        this.logFileName = fileName;
    }

    public Logger(String fileName,int capacity) {
        this.logFileName = fileName;
        if(capacity>=1)
            this.capacity=capacity;
    }
    public void log(String error) throws IOException {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        errors.add(errorDate.format(new Date())+"\t"+error);
        if(errors.size()>=capacity)
            save();
    }

    public void save() throws IOException {
        BufferedWriter writer = null;
        BufferedReader reader=null;
        File logFile=new File(logFileName);
        File tmpFile=null;
        if(errors==null)
            return;
         try {
             if(logFile.length()>1024*1024){
                 tmpFile=File.createTempFile("tmplog", null);
                 reader=new BufferedReader(new FileReader(logFile));
                 writer = new BufferedWriter(new FileWriter(tmpFile));
                 reader.skip(1024*400);
                 int charRead=reader.read();
                 while(charRead!=-1){
                     writer.write(charRead);
                     charRead=reader.read();
                 }
                 reader.close();
                 writer.close();
                 logFile.delete();
                 tmpFile.renameTo(logFile);
             }
             writer = new BufferedWriter(new FileWriter(logFile, true));
             for(String error : errors){
                 writer.write(error);
                 writer.newLine();
             }

             errors.clear();
        }
        finally{
            try {
                if(writer!=null)
                    writer.close();
            } catch (IOException ex) { }
            try {
                if(reader!=null)
                    reader.close();
            } catch (IOException ex2) { }
        }
    }

    public static void main(String[] args) throws Exception{
        Logger logger=new Logger("./log.txt");
        for(int ctr=1;ctr<100;ctr++)
            logger.log("h");
        logger.save();

    }

}
