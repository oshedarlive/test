/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 *
 * @author hoshi
 */
public class DBUtils {

    
    public static void close(Connection conn){
        if(conn!=null){
            try{
                if(!conn.isClosed())
                    conn.close();
            }catch(Exception ex){}
        }
    }
    public static void close(Statement stat){
        if(stat!=null){
            try{
                stat.close();
            }catch(Exception ex){}
        }
        
    }
    
    private static void closeResultSet(ResultSet rst){
        if(rst!=null){
            try{                
                rst.close();
            }catch(Exception ex){}
        }
        
    }
    
    public static void close(ResultSet rst,boolean closeStatement) {
        Statement stat=null;
        if(rst!=null){
            if(closeStatement){
                try{                
                    stat=rst.getStatement();                
                }catch(SQLException ex){}        
                closeResultSet(rst);
                close(stat);
            }
            else
                closeResultSet(rst);
        }
    }

    public static ResultSet executeQuery(Connection conn,String query,boolean usePreparedStatement) throws SQLException{
        Statement stat=null;
        PreparedStatement pStat=null;
        ResultSet rst=null;
        if(conn!=null && query!=null){
            if(usePreparedStatement){
                pStat=conn.prepareStatement(query);
                rst=pStat.executeQuery();                
            }
            else {
                stat=conn.createStatement();
                rst=stat.executeQuery(query);
            }
        }
        else
            throw new SQLException("connection is null or query is null");
        return rst;
    }
        
    public static ResultSet executeQuery(Connection conn,String query, Object... args)throws SQLException{
        PreparedStatement stat=null;
        ResultSet rst=null;
        if(conn!=null && query!=null){
            stat=conn.prepareStatement(query);
            setParams(stat,args);
            rst=stat.executeQuery();
        }
        else
            throw new SQLException("connection is null or query is null");
        return rst;
    }

    public static Object executeQuery1Result(Connection conn,String query,int resultType,boolean usePreparedStatement)throws SQLException{
        Statement stat=null;
        PreparedStatement pStat=null;
        ResultSet rst=null;
        Object result=null;
        if(conn!=null && query!=null){
            try{
                if(usePreparedStatement){
                    pStat=conn.prepareStatement(query);
                    rst=pStat.executeQuery();                
                }
                else {
                    stat=conn.createStatement();
                    rst=stat.executeQuery(query);
                }

                if(rst.next()){
                    if(resultType==Types.INTEGER)
                        result=rst.getInt(1);
                    else if(resultType==Types.BIGINT)
                        result=rst.getLong(1);
                    else if(resultType==Types.FLOAT)
                        result=rst.getFloat(1);
                    else if(resultType==Types.DOUBLE)
                        result=rst.getDouble(1);
                    else if(resultType==Types.DECIMAL)
                        result=rst.getBigDecimal(1);
                    else if(resultType==Types.CHAR || resultType==Types.VARCHAR)
                        result=rst.getString(1);
                    else if(resultType==Types.DATE || resultType==Types.TIMESTAMP)
                        result=rst.getTimestamp(1);
                    else
                        result=rst.getObject(1);
                }
            }
            finally{closeResultSet(rst);if(usePreparedStatement) close(pStat); else close(stat);}
        }
        else
            throw new SQLException("connection is null or query is null");
        return result;
    }
        
    public static Object executeQuery1Result(Connection conn,String query,int resultType,Object... args)throws SQLException{
        PreparedStatement stat=null;
        ResultSet rst=null;
        Object result=null;
        if(conn!=null && query!=null){
            try{
                stat=conn.prepareStatement(query);
                setParams(stat, args);
                rst=stat.executeQuery();
                if(rst.next()){
                    if(resultType==Types.INTEGER)
                        result=rst.getInt(1);
                    else if(resultType==Types.BIGINT)
                        result=rst.getLong(1);
                    else if(resultType==Types.FLOAT)
                        result=rst.getFloat(1);
                    else if(resultType==Types.DOUBLE)
                        result=rst.getDouble(1);
                    else if(resultType==Types.DECIMAL)
                        result=rst.getBigDecimal(1);
                    else if(resultType==Types.CHAR || resultType==Types.VARCHAR)
                        result=rst.getString(1);
                    else if(resultType==Types.DATE || resultType==Types.TIMESTAMP)
                        result=rst.getTimestamp(1);
                    else
                        result=rst.getObject(1);
                }
            }
            finally{closeResultSet(rst);close(stat);}
        }
        else
            throw new SQLException("connection is null or query is null");
        return result;
    }
    
    public static int executeUpdate(Connection conn,String query,boolean usePreparedStatement)throws SQLException{
        Statement stat=null;
        PreparedStatement pStat=null;
        int rowsAffected=-1;
        if(conn!=null && query!=null){
            try{
                if(usePreparedStatement){
                    pStat=conn.prepareStatement(query);
                    rowsAffected=pStat.executeUpdate();                
                }
                else {
                    stat=conn.createStatement();
                    rowsAffected=stat.executeUpdate(query);
                }
            }
            finally{if(usePreparedStatement) close(pStat); else close(stat);}
        }
        else
            throw new SQLException("connection is null or query is null");
        return rowsAffected;
    }

    public static int executeUpdate(Connection conn,String query, Object... args)throws SQLException{
        PreparedStatement stat=null;
        int rowsAffected=-1;
        if(conn!=null && query!=null){
            try{
                stat=conn.prepareStatement(query);
                setParams(stat, args);                
                rowsAffected=stat.executeUpdate();
            }
            finally{close(stat);}
        }
        else
            throw new SQLException("connection is null or query is null");
        return rowsAffected;
    }
            
    public static String toString(java.sql.Date date){
        
        if(date == null){
            return "";    
        }
        return date.toString();
    }
       
    public static java.sql.Date toSqldate(Calendar calendar){
        if(calendar==null)
            return null;
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    public static java.sql.Date toSqldate(java.util.Date date){
        if(date==null)
            return null;        
        if(date instanceof java.sql.Date)
            return (java.sql.Date)date;
        return new java.sql.Date(date.getTime());
    }
    
    public static java.sql.Timestamp toSqlTimestamp(Calendar calendar){
        if(calendar==null)
                return null;
        return new java.sql.Timestamp(calendar.getTimeInMillis());    
    }

    public static java.sql.Timestamp toSqlTimestamp(java.util.Date date){
        if(date==null)
                return null;
        if(date instanceof java.sql.Timestamp)
            return (java.sql.Timestamp)date;
        return new java.sql.Timestamp(date.getTime());    
    }

    
    public static void setParams(PreparedStatement stat,Object... args) throws SQLException{
        int ctr;
        if(stat==null) return;
        for(ctr=0;ctr<args.length;ctr++){
            if(args[ctr]==null)
                stat.setNull(ctr+1,Types.VARCHAR);
            if(args[ctr] instanceof String)
                stat.setString(ctr+1, (String)args[ctr]);
            else if(args[ctr] instanceof Boolean)
                stat.setBoolean(ctr+1, (Boolean)args[ctr]);
            else if(args[ctr] instanceof Integer)
                stat.setInt(ctr+1, (Integer)args[ctr]);
            else if(args[ctr] instanceof Long)
                stat.setLong(ctr+1, (Long)args[ctr]);
            else if(args[ctr] instanceof Float)
                stat.setFloat(ctr+1, (Float)args[ctr]);
            else if(args[ctr] instanceof java.util.Date)
                stat.setTimestamp(ctr+1, toSqlTimestamp((java.util.Date)args[ctr]));
            else if(args[ctr] instanceof Calendar)
                stat.setTimestamp(ctr+1, toSqlTimestamp((Calendar)args[ctr])); 
            else if(args[ctr] instanceof Double)
                stat.setDouble(ctr+1, (Double)args[ctr]);
            else if(args[ctr] instanceof BigDecimal)
                stat.setBigDecimal(ctr+1, (BigDecimal)args[ctr]);
            else if(args[ctr] instanceof Short)
                stat.setShort(ctr+1, (Short)args[ctr]);
            else if(args[ctr] instanceof Character)
                stat.setString(ctr+1, "" + args[ctr]);
            else if(args[ctr] instanceof Byte)
                stat.setByte(ctr+1, (Byte)args[ctr]);
            else if(args[ctr] instanceof InputStream)
                stat.setBlob(ctr+1, (InputStream)args[ctr]);
            else if(args[ctr] instanceof Reader)
                stat.setClob(ctr+1, (Reader)args[ctr]);
            else if(args[ctr] instanceof Clob)
                stat.setClob(ctr+1, (Clob)args[ctr]);
            else if(args[ctr] instanceof Blob)
                stat.setBlob(ctr+1, (Blob)args[ctr]);
            else
                stat.setObject(ctr+1, args[ctr]);
        }        
    }
}
