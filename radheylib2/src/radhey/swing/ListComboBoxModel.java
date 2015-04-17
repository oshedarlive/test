/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radhey.swing;

import java.util.List;
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
public class ListComboBoxModel extends AbstractListModel implements MutableComboBoxModel, ListDataListener {

    private int selectedIndex = -1;
    private List data;
    private Object selectedObject = null;

    public void contentsChanged(ListDataEvent e) {
        fireContentsChanged(this, e.getIndex0(), e.getIndex1());
    }

    public void intervalAdded(ListDataEvent e) {
        fireIntervalAdded(this, e.getIndex0(), e.getIndex1());
    }

    public void intervalRemoved(ListDataEvent e) {
        fireIntervalRemoved(this, e.getIndex0(), e.getIndex1());
    }

    public ListComboBoxModel() {
        data = new FastGrowingList();
        ((FastGrowingList) data).addListDataListener(this);
    }

    public ListComboBoxModel(List data) {
        if (data != null) {
            if (data instanceof KeyDataList) {
                throw new UnsupportedOperationException("KeyDataList is not supported by ListComboboxModel");
            }
            this.data = data;
        } else {
            this.data = new FastGrowingList();
        }
        if (this.data instanceof FastGrowingList) {
            ((FastGrowingList) this.data).addListDataListener(this);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index < -1 || index >= data.size()) {
            throw new IllegalArgumentException("setSelectedIndex: " + index + " out of bounds");
        }
        if ((selectedIndex != -1 && selectedIndex != index) || selectedIndex == -1 && index != -1) {
            selectedIndex = index;
            if (index == -1) {
                selectedObject = null;
            } else {
                selectedObject = data.get(index);
            }
            fireContentsChanged(this, -1, -1);
        }
    }

    public Object getSelectedItem() {
        return selectedObject;
    }

    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals(anObject)) || selectedObject == null && anObject != null) {
            selectedIndex = indexOf(anObject);
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        }
    }

    public Object getElementAt(int index) {
        if (index >= 0 && index < data.size()) {
            return data.get(index);
        }
        return null;
    }

    public int getSize() {
        return data.size();
    }

    public int indexOf(Object anItem) {
        return data.indexOf(anItem);
    }

    public void addElement(Object obj) {
        data.add(obj);
        if (!(data instanceof FastGrowingList)) {
            fireIntervalAdded(this, data.size() - 1, data.size() - 1);
        }
    }

    public void insertElementAt(Object obj, int index) {
        data.add(index, obj);
        if (!(data instanceof FastGrowingList)) {
            fireIntervalAdded(this, index, index);
        }
    }

    public void removeElement(Object obj) {
        int index = data.indexOf(obj);
        if (index != -1) {
            removeElementAt(index);
        }
    }

    public void removeElementAt(int index) {
        if (index == selectedIndex) {
            setSelectedIndex(-1);
        }

        data.remove(index);
        if (!(data instanceof FastGrowingList)) {
            fireIntervalRemoved(this, index, index);
        }
    }

    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if (data.size() > 0) {
            int firstIndex = 0;
            int lastIndex = data.size() - 1;
            data.clear();
            selectedObject = null;
            selectedIndex = -1;
            if (!(data instanceof FastGrowingList)) {
                fireIntervalRemoved(this, firstIndex, lastIndex);
            }
        }
    }

    @Override
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        if(!SwingUtilities.isEventDispatchThread()){
            SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        ListComboBoxModel.super.fireContentsChanged(source, index0, index1);
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
                        ListComboBoxModel.super.fireIntervalAdded(source, index0, index1);
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
                        ListComboBoxModel.super.fireIntervalRemoved(source, index0, index1);
                    }
                });
        }
        else
            super.fireIntervalRemoved(source, index0, index1);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        super.removeListDataListener(l);
        if (l instanceof JComboBox) {
            if (data != null && data instanceof FastGrowingList) {
                ((FastGrowingList) data).removeListDataListener(this);
            }
        }
    }

    public int indexOfStartsWith(String s) { //matches the first item that begins with s
        if (s == null) {
            return -1;
        }
        if (data instanceof FastGrowingList) {
            return ((FastGrowingList) data).indexOfStartsWith(s);
        }
        int ctr;
        s = s.toUpperCase();
        for (ctr = 0; ctr < data.size(); ctr++) {
            if (data.get(ctr).toString().toUpperCase().startsWith(s)) {
                return ctr;
            }
        }
        return -1;
    }

    public List getData() {
        return data;
    }

    protected void finalize() throws Throwable{
        try{
            if(data != null && data instanceof FastGrowingList)
                ((FastGrowingList)data).removeListDataListener(this);
        }
        finally{
            super.finalize();
        }
    }
}
