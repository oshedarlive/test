/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.swing;
import javax.swing.table.*;
import radhey.util.*;
import java.sql.*;

/**
 * Creates a Table Model from a resultset - first column will be the keyfield and will be hidden
 *
 * @author hoshi
 */
public class FastTableModel extends AbstractTableModel {

    FastGrowingList<String[]> dataList;
    String[] columnLabels;//includes keycolumn
    boolean hideKeyColumn=true;
    public FastTableModel(){
        dataList=new FastGrowingList<String[]>();
    }

    public FastTableModel(short incrementBy){
        dataList=new FastGrowingList<String[]>(incrementBy);
    }

    public FastTableModel(ResultSet rst) throws SQLException{
        this();
        setData(rst);
    }

    public FastTableModel(ResultSet rst,boolean hideKeyColumn) throws SQLException{
        this();
        this.hideKeyColumn=hideKeyColumn;
        setData(rst);
    }

    public FastTableModel(ResultSet rst,short incrementBy) throws SQLException{
        this(incrementBy);
        setData(rst);
    }

    public FastTableModel(ResultSet rst,short incrementBy,boolean hideKeyColumn) throws SQLException{
        this(incrementBy);
        this.hideKeyColumn=hideKeyColumn;
        setData(rst);
    }


    public void clear(){
        int size=dataList.size();
        dataList.clear(true);
        if(size>0)
            fireTableRowsInserted(0, size-1);
    }

    public void setData(ResultSet rst) throws SQLException{
        ResultSetMetaData rsm=rst.getMetaData();
        if(columnLabels==null){
            columnLabels=new String[rsm.getColumnCount()];
            for(int ctr=0;ctr<columnLabels.length;ctr++)
                columnLabels[ctr]=rsm.getColumnLabel(ctr+1);
        }
        else{
            int oldSize=dataList.size();
            dataList.clear(true);//clear but maintain capacity
            if(oldSize>0)
                fireTableRowsDeleted(0, oldSize-1);
        }
        String[] row;
        while(rst.next()){
            row=new String[columnLabels.length];
            for(int ctr2=0;ctr2<columnLabels.length;ctr2++){
                row[ctr2]=rst.getString(ctr2+1);
                if(row[ctr2]==null)
                    row[ctr2]="";
            }
            dataList.add(row);
        }
        int size=dataList.size();
        if(size>0)
            fireTableRowsInserted(0, size-1);
    }

    public int getColumnCount() {
        if(columnLabels!=null){
            if(hideKeyColumn)
                return columnLabels.length-1;//key column is hidden so 1 less
            return columnLabels.length;
        }
        return 0;
    }

    public int getRowCount() {
        return dataList.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {//
        if(hideKeyColumn)
            return dataList.get(rowIndex)[columnIndex+1];//key column is hidden so 1 more
        return dataList.get(rowIndex)[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnLabels!=null){
            return String.class;
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        if(columnLabels!=null){
            if(hideKeyColumn)
                return columnLabels[column+1];//key column is hidden so always 1 more
            return columnLabels[column];
        }
        return null;
    }

    public String getKeyAt(int rowIndex){
        return dataList.get(rowIndex)[0];
    }

}
