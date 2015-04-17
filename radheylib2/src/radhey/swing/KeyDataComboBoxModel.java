/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.swing;

import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import radhey.util.*;

/**
 *
 * @author hoshi
 */
  public class KeyDataComboBoxModel extends AbstractListModel implements MutableComboBoxModel,ListDataListener {
        private int selectedIndex=-1;
        private KeyDataList data;
        private Object selectedObject=null;

    public void contentsChanged(ListDataEvent e) {
        fireContentsChanged(this, e.getIndex0(), e.getIndex1());
    }

    public void intervalAdded(ListDataEvent e) {
        fireIntervalAdded(this, e.getIndex0(), e.getIndex1());
    }

    public void intervalRemoved(ListDataEvent e) {
        fireIntervalRemoved(this, e.getIndex0(), e.getIndex1());
    }

        public KeyDataComboBoxModel(){
            data=new KeyDataList();
            data.addListDataListener(this);
        }

        public KeyDataComboBoxModel(KeyDataList data){
            if(data!=null)
                this.data=data;
            else
                this.data=new KeyDataList();
            this.data.addListDataListener(this);
        }

        public int getSelectedIndex(){
            return selectedIndex;
        }

        public void setSelectedIndex(int index){
            if ( index < -1 || index >= data.size()) {
                throw new IllegalArgumentException("setSelectedIndex: " + index + " out of bounds");
            }
            if((selectedIndex!=-1 && selectedIndex!=index) || selectedIndex==-1 && index!=-1){
                selectedIndex=index;
                if(index==-1)
                    selectedObject=null;
                else
                    selectedObject=data.get(index);
                fireContentsChanged(this, -1, -1);
            }
        }

        public Object getSelectedItem() {
            return selectedObject;
        }

        public void setSelectedItem(Object anObject) {
            if ((selectedObject != null && !selectedObject.equals(anObject)) ||selectedObject == null && anObject != null) {
                selectedIndex=indexOf(anObject);
                selectedObject=anObject;
                fireContentsChanged(this, -1, -1);
            }
        }

        public Object getSelectedKey(){
            if(selectedIndex>=0)
                return data.getKey(selectedIndex);
            return null;
        }

        public void setSelectedKey(Object key){
            if(key!=null && key.equals(getSelectedKey()))
                return;
            int index=data.indexOfKey(key);
            if(index>=0)
                setSelectedIndex(index);
            else
                setSelectedIndex(-1);
        }

        public Object getElementAt(int index) {
            if(index>=0 && index<data.size())
                return data.get(index);
            return null;
        }

        public int getSize() {
            return data.size();
        }

    public int indexOf(Object anItem){
        return data.indexOf(anItem);
    }

    public void addElement(Object obj) {
        throw new UnsupportedOperationException();
    }

    public void addElement(Object key,Object obj) {
        data.add(key,obj);
    }

    public void insertElementAt(Object obj, int index) {
        throw new UnsupportedOperationException();
    }

    public void insertElementAt(Object key,Object obj, int index) {
        data.add(index,key,obj);
    }

    public void removeElement(Object obj) {
        int index = data.indexOf(obj);
        if ( index != -1 ) {
            removeElementAt(index);
        }
    }

    public void removeElementAt(int index) {
        if ( index == selectedIndex ) {
            setSelectedIndex(-1);
        }
        data.remove(index);
    }
    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if ( data.size() > 0 ) {
            data.clear();
            selectedObject = null;
            selectedIndex = -1;
        }
    }

    @Override
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        if(!SwingUtilities.isEventDispatchThread()){
            SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        KeyDataComboBoxModel.super.fireContentsChanged(source, index0, index1);
                    }
                });
        }
        else
            super.fireContentsChanged(source, index0, index1);
    }

    @Override
    protected void fireIntervalAdded(final Object source, final int index0, final int index1) {
        if(!SwingUtilities.isEventDispatchThread()){
            SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        KeyDataComboBoxModel.super.fireIntervalAdded(source, index0, index1);
                    }
                });
        }
        else
            super.fireIntervalAdded(source, index0, index1);
    }

    @Override
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        if(!SwingUtilities.isEventDispatchThread()){
            SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        KeyDataComboBoxModel.super.fireIntervalRemoved(source, index0, index1);
                    }
                });
        }
        else
            super.fireIntervalRemoved(source, index0, index1);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        super.removeListDataListener(l);
        if(l instanceof JComboBox){
            if(data!=null)
                data.removeListDataListener(this);
        }

    }

    public int indexOfStartsWith(String s){ //matches the first item that begins with s
        if(s==null)
            return -1;
        return data.indexOfStartsWith(s);
    }

    public KeyDataList getData(){
        return data;
    }
      protected void finalize() throws Throwable{
        try{
            if(data != null)
                data.removeListDataListener(this);
        }
        finally{
            super.finalize();
        }
    }
}
