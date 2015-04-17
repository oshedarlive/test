/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.swing;
import javax.swing.table.*;
import radhey.util.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author hoshi
 */
public class FastFilterTableModel extends AbstractTableModel {

    SortedFastGrowingList2<String[]> dataList;
    String[] columnLabels;//includes keycolumn
    int[] visibleRows;
    int visibleCount;
    int filterColumn=0;
    int addlSortColumn=-1;
    int filterColumnWidthInChars;
    boolean freeTextFilter;
    boolean hideKeyColumn=false;
    int[] maxColumnWidths;
    int[] maxChars;
    String filterValue="";
    int highLightFieldIndex=-1;
    String highlightValue;
    boolean hideHighlightField;
    public String getFilterColumnLabel(){
        return columnLabels[filterColumn];
    }

    public String getColumnLabel(int column){
        return columnLabels[column];
    }

    public int getMaxChars(int column){
        return maxChars[column];
    }

    public int getMaxWidthInChars(int column){
        return maxColumnWidths[column];
    }
    
    public int getFilterColumnWidthInChars() {
        return filterColumnWidthInChars;
    }

    public FastFilterTableModel(){
        dataList=new SortedFastGrowingList2<String[]>(new SortedListComparator(), 10);
    }

    public FastFilterTableModel(ResultSet rst,String filterFieldName,boolean freeTextFilter,boolean hideKeyColumn) throws SQLException{
        this(rst, filterFieldName, freeTextFilter, hideKeyColumn, null);
    }

    public FastFilterTableModel(ResultSet rst,String filterFieldName,boolean freeTextFilter,boolean hideKeyColumn,String addlSortFieldName) throws SQLException{
        this();
        this.hideKeyColumn=hideKeyColumn;
        this.freeTextFilter=freeTextFilter;
        setData(rst,filterFieldName,null,null,addlSortFieldName);
    }

    public FastFilterTableModel(ResultSet rst,String filterFieldName,boolean freeTextFilter,boolean hideKeyColumn,String highlightFieldName,Object highlightValue,boolean hideHighlightField,String addlSortFieldName) throws SQLException{
        this();
        this.hideKeyColumn=hideKeyColumn;
        this.freeTextFilter=freeTextFilter;
        this.hideHighlightField=hideHighlightField;
        setData(rst,filterFieldName,highlightFieldName,highlightValue,addlSortFieldName);
    }

    public FastFilterTableModel(ResultSet rst,String filterFieldName,boolean freeTextFilter,boolean hideKeyColumn,String highlightFieldName,Object highlightValue,boolean hideHighlightField) throws SQLException{
        this(rst, filterFieldName, freeTextFilter, hideKeyColumn, highlightFieldName, highlightValue, hideHighlightField, null);
    }

    public void clear(){
        int size=dataList.size();
        dataList.clear(true);
        if(size>0)
            fireTableRowsInserted(0, size-1);
    }

     void setData(ResultSet rst,String filterFieldName,String highlightFieldName,Object highlightValue,String addlSortFieldName) throws SQLException{
        ResultSetMetaData rsm=rst.getMetaData();
        if(columnLabels==null){
            columnLabels=new String[rsm.getColumnCount()];
            maxChars=new int[columnLabels.length];
            for(int ctr=0;ctr<columnLabels.length;ctr++){
                columnLabels[ctr]=rsm.getColumnLabel(ctr+1);
                maxChars[ctr]=rsm.getPrecision(ctr+1);
            }
            for(int ctr=0;ctr<columnLabels.length;ctr++){
                if(rsm.getColumnName(ctr+1).equalsIgnoreCase(filterFieldName)){
                    filterColumn=ctr;
                    filterColumnWidthInChars=rsm.getColumnDisplaySize(ctr+1);
                    break;
                }
            }
            for(int ctr=0;ctr<columnLabels.length;ctr++){
                if(rsm.getColumnName(ctr+1).equalsIgnoreCase(addlSortFieldName)){
                    addlSortColumn=ctr;
                    break;
                }
            }
            if(highlightFieldName!=null){
                for(int ctr=0;ctr<columnLabels.length;ctr++){
                    if(rsm.getColumnName(ctr+1).equalsIgnoreCase(highlightFieldName)){
                        highLightFieldIndex=ctr;
                        break;
                    }
                }
            }
        }
        else{
            int oldSize=visibleCount;
            dataList.clear(true);//clear but maintain capacity
            if(oldSize>0)
                fireTableRowsDeleted(0, oldSize-1);
        }
        String[] row;
        int[] columnTypes=new int[columnLabels.length];
        maxColumnWidths=new int[columnLabels.length];
        for(int ctr=0;ctr<columnLabels.length;ctr++){
            maxColumnWidths[ctr]=columnLabels[ctr].length();
            columnTypes[ctr]=rsm.getColumnType(ctr+1);
        }
        if(highLightFieldIndex>=0)
            this.highlightValue=getValueAsString(highlightValue, columnTypes[highLightFieldIndex]);
        int strLength;
        while(rst.next()){
            row=new String[columnLabels.length];
            for(int ctr2=0;ctr2<columnLabels.length;ctr2++){
                row[ctr2]=getValueAsString(rst,ctr2+1,columnTypes[ctr2]);
                if(row[ctr2]==null) row[ctr2]="";
                strLength=row[ctr2].length();
                if(maxColumnWidths[ctr2]<strLength)
                    maxColumnWidths[ctr2]=strLength;
            }
            dataList.add(row);
        }
        visibleRows=new int[dataList.size()];
        visibleCount=dataList.size();
        int size=dataList.size();
        if(size>0)
            fireTableRowsInserted(0, size-1);
    }

    protected String getValueAsString(ResultSet rst,int columnIndex,int type) throws SQLException{
        return rst.getString(columnIndex);
    }

    protected String getValueAsString(Object value,int type){
        return value.toString();
    }

    public int getColumnCount() {
        if(columnLabels!=null){
            if(hideKeyColumn){
                if(highLightFieldIndex>0 && hideHighlightField)
                    return columnLabels.length-2;
                return columnLabels.length-1;//key column is hidden so 1 less
            }
            if(highLightFieldIndex>=0 && hideHighlightField)
                return columnLabels.length-1;
            return columnLabels.length;
        }
        return 0;
    }

    public int getRowCount() {
        return visibleCount;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {//
        if(visibleCount==dataList.size()){
            if(hideKeyColumn){
                if(highLightFieldIndex>0 && hideHighlightField){
                    if(columnIndex>=highLightFieldIndex-1)
                        return dataList.get(rowIndex)[columnIndex+2];
                }
                return dataList.get(rowIndex)[columnIndex+1];//key column is hidden so 1 more
            }
            if(highLightFieldIndex>=0 && hideHighlightField){
                if(columnIndex>=highLightFieldIndex)
                    return dataList.get(rowIndex)[columnIndex+1];
            }
            return dataList.get(rowIndex)[columnIndex];
        }
        if(hideKeyColumn){
            if(highLightFieldIndex>0 && hideHighlightField){
                if(columnIndex>=highLightFieldIndex-1)
                    return dataList.get(visibleRows[rowIndex])[columnIndex+2];
            }
            return dataList.get(visibleRows[rowIndex])[columnIndex+1];//key column is hidden so 1 more
        }
        if(highLightFieldIndex>=0 && hideHighlightField){
            if(columnIndex>=highLightFieldIndex)
                return dataList.get(visibleRows[rowIndex])[columnIndex+1];
        }
        return dataList.get(visibleRows[rowIndex])[columnIndex];
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
            if(hideKeyColumn){
                if(highLightFieldIndex>0 && hideHighlightField){
                    if(column>=highLightFieldIndex-1)
                        return columnLabels[column+2];
                }
                return columnLabels[column+1];//key column is hidden so always 1 more
            }
            if(highLightFieldIndex>=0 && hideHighlightField){
                if(column>=highLightFieldIndex)
                    return columnLabels[column+1];
            }
            return columnLabels[column];
        }
        return null;
    }

    public String getKeyAt(int rowIndex){
        if(rowIndex<0)
            return null;
        if(visibleCount==dataList.size())
            return dataList.get(rowIndex)[0];
        return dataList.get(visibleRows[rowIndex])[0];
    }

    public void filter(String txt){
        if(dataList.size()<1)
            return;
        int oldVisibleCount=visibleCount;        
        if(freeTextFilter)
            filterFreeText(txt);
        else
            filterText(txt);
        if(oldVisibleCount>0)
            fireTableRowsDeleted(0, oldVisibleCount-1);
        if(visibleCount>0)
            fireTableRowsInserted(0, visibleCount-1);
    }

    protected void filterText(String filter){
        if(filter==null || filter.trim().length()<1){
            visibleCount=dataList.size();
            filterValue="";
            return;
        }

        filter=filter.toUpperCase();
        int endIndex=indexOfFirstGreater(filter);
        int size=dataList.size();
        visibleCount=0;
        filterValue=filter;
        if(endIndex==-1)
            return ;
        int firstIndex=-1;
        int index=endIndex;
        while(index>0 && dataList.get(index-1)[filterColumn]!=null && dataList.get(index-1)[filterColumn].toUpperCase().startsWith(filter))
            index--;
        if(index<endIndex)
            firstIndex=index;
        else if(endIndex<size && dataList.get(endIndex)[filterColumn]!=null && dataList.get(endIndex)[filterColumn].toUpperCase().startsWith(filter))
            firstIndex=endIndex++;

        if(firstIndex>=0){
            for(index=firstIndex;index<endIndex;index++)
                visibleRows[visibleCount++]=index;
//            while(index<size && dataList.get(index)[filterColumn].toUpperCase().startsWith(filter))
//                visibleRows[visibleCount++]=index++;
        }
    }

    protected void filterFreeText(String filter){
        if(filter==null || filter.trim().length()<1){
            visibleCount=dataList.size();
            filterValue="";
            return;
        }
        visibleCount=0;
        filter=filter.toUpperCase();
        filterValue=filter;
        int size=dataList.size();
        for(int ctr=0;ctr<size;ctr++){
            if(dataList.get(ctr)[filterColumn].toUpperCase().indexOf(filter)>=0)
                visibleRows[visibleCount++]=ctr;
        }
    }

    private int indexOfFirstGreater(String item){
        int low=0,hi=dataList.size()-1,mid=0;
        if(hi<0)
            return -1;
        while(hi>low){
            mid=(low+hi)/2;
            if(item.compareToIgnoreCase(dataList.get(mid)[filterColumn])>=0)
                low=mid+1;
            else
                hi=mid;
        }
        if(hi<=low && item.compareToIgnoreCase(dataList.get(hi)[filterColumn])<0){
            return hi;
        }
        return hi+1;
    }

    private int indexOfStartsWith(String s){
        Object item;
        int size=dataList.size();
        int endIndex=indexOfFirstGreater(s);
        if(endIndex==-1)
            return -1;
        s=s.toUpperCase();
        int index=endIndex;
        while(index>0 && dataList.get(index-1)[filterColumn]!=null && dataList.get(index-1)[filterColumn].toUpperCase().startsWith(s))
            index--;
        if(index<endIndex)
            return index;
        if(endIndex<size && dataList.get(endIndex)[filterColumn]!=null && dataList.get(endIndex)[filterColumn].toUpperCase().startsWith(s))
            return endIndex;
        return -1;
    }

    private int indexOfKey(String key){
        if(filterColumn==0){//keycolumn is the filtercolumn
            int index=indexOfFirstGreater(key);
            if(index==-1)
                return -1;
            if(index>0 && dataList.get(index-1)[0]!=null && dataList.get(index-1)[0].equalsIgnoreCase(key))
                return index-1;
            if(index<dataList.size() && dataList.get(index)[0]!=null && dataList.get(index)[0].equalsIgnoreCase(key))
                return index;
            return -1;
        }
        int size=dataList.size();
        for(int ctr=0;ctr<size;ctr++){
            if(dataList.get(ctr)[0].equalsIgnoreCase(key))
                return ctr;
        }
        return -1;
    }

    private int getVisibleIndex(int normalIndex){
        for(int ctr=0;ctr<visibleCount;ctr++){
            if(visibleRows[ctr]==normalIndex)
                return ctr;
        }
        return -1;
    }

    class SortedListComparator implements Comparator<String[]>{
        public int compare(String[] o1, String[] o2) {
            int result=o1[filterColumn].compareToIgnoreCase(o2[filterColumn]);
            if(result==0 && addlSortColumn!=-1)
                return o1[addlSortColumn].compareToIgnoreCase(o2[addlSortColumn]);
            return result;
        }
    }

    public void insertRow(String... values){
        String[] row=new String[columnLabels.length];
        int ctr,strLength;
        for(ctr=0;ctr<row.length && ctr<values.length;ctr++){
            row[ctr]=values[ctr];
            if(row[ctr]==null)
                strLength=0;
            else
                strLength=row[ctr].length();
            if(strLength>maxColumnWidths[ctr])
                maxColumnWidths[ctr]=strLength;
        }
        for(;ctr<row.length;ctr++)
            row[ctr]="";
        dataList.add(row);
        if(visibleRows.length<dataList.size()){
            int[] newVisibleRows=new int[visibleRows.length+10];
            System.arraycopy(visibleRows, 0, newVisibleRows, 0, visibleRows.length);
            visibleRows=newVisibleRows;
            newVisibleRows=null;
        }
        int index=indexOfKey(row[0]);
        if(index>=0 && visibleCount==dataList.size()-1){
            visibleCount++;
            fireTableRowsInserted(index, index);
            return;
        }
        if(freeTextFilter && row[filterColumn].toUpperCase().indexOf(filterValue)>=0){
            filter(filterValue);
            return;
        }
        if(!freeTextFilter && row[filterColumn].toUpperCase().startsWith(filterValue)){
            filter(filterValue);
            return;
        }

    }

    public void updateRow(int row,String ... values){
        int index=visibleRows[row];
        int ctr,strLength;
        for(ctr=0;ctr<columnLabels.length && ctr<values.length;ctr++){
            dataList.get(index)[ctr]=values[ctr];
            if(values[ctr]==null)
                strLength=0;
            else
                strLength=values[ctr].length();
            if(strLength>maxColumnWidths[ctr])
                maxColumnWidths[ctr]=strLength;
        }
        for(;ctr<columnLabels.length;ctr++)
            dataList.get(index)[ctr]="";
        if(visibleCount==dataList.size())
            fireTableRowsUpdated(index, index);
        else{
            int visibleIndex=getVisibleIndex(index);
            if(visibleIndex>=0)
                fireTableRowsUpdated(visibleIndex, visibleIndex);
        }

    }

    public void updateRow(String key,String... values){
        int index=indexOfKey(key);
        if(index>=0){
            int ctr,strLength;
            for(ctr=1;ctr<columnLabels.length && ctr<values.length;ctr++){
                dataList.get(index)[ctr]=values[ctr];
                if(values[ctr]==null)
                    strLength=0;
                else
                    strLength=values[ctr].length();
                if(strLength>maxColumnWidths[ctr])
                    maxColumnWidths[ctr]=strLength;
            }
            for(;ctr<columnLabels.length;ctr++)
                dataList.get(index)[ctr]="";
            if(visibleCount==dataList.size())
                fireTableRowsUpdated(index, index);
            else{
                int visibleIndex=getVisibleIndex(index);
                if(visibleIndex>=0)
                    fireTableRowsUpdated(visibleIndex, visibleIndex);
            }
        }        
    }

    public void deleteRow(String key){
        int index=indexOfKey(key);
        if(index>=0){
//            int oldVisibleCount=visibleCount;
//            visibleCount=dataList.size();
//            fireTableRowsDeleted(0, oldVisibleCount-1);
//            fireTableRowsInserted(0, visibleCount-1);
             if(visibleCount==dataList.size()){
                fireTableRowsDeleted(index, index);
                dataList.remove(index);
                visibleCount--;
             }
             else {
                int visibleIndex=getVisibleIndex(index);
                if(visibleIndex>=0){
                    fireTableRowsDeleted(visibleIndex, visibleIndex);
                    for(int ctr2=visibleIndex;ctr2<visibleCount;ctr2++)
                        visibleRows[ctr2]=visibleRows[ctr2+1];
                    dataList.remove(index);
                    visibleCount--;
                }
                else{
                    int oldVisibleCount=visibleCount;
                    visibleCount=dataList.size();
                    fireTableRowsDeleted(0, oldVisibleCount-1);
                    fireTableRowsInserted(0, visibleCount-1);
                }
             }
        }
    }

    public boolean highlightRow(int rowIndex){
        if(visibleCount==dataList.size())
            return highLightFieldIndex>=0 && dataList.get(rowIndex)[highLightFieldIndex].equals(highlightValue);
        return highLightFieldIndex>=0 && dataList.get(visibleRows[rowIndex])[highLightFieldIndex].equals(highlightValue);
    }

}
