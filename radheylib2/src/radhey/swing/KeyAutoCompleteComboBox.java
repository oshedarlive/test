/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.*;
import radhey.util.KeyDataList;
import radhey.util.SortedKeyDataList;
import radhey.util.SortedKeyDataList2;

/**
 *
 * @author hoshi
 */
public class KeyAutoCompleteComboBox extends JComboBox{

    private boolean isSelectingItem=false;

    public boolean isSorted(){
        List data=((KeyDataComboBoxModel)dataModel).getData();
        return data instanceof SortedKeyDataList || data instanceof SortedKeyDataList2;
    }

    public boolean isIgnoreCase(){
        List data=((KeyDataComboBoxModel)dataModel).getData();
        if(data instanceof SortedKeyDataList)
            return ((SortedKeyDataList)data).isIgnoreCase();
        return false;
    }

    public KeyAutoCompleteComboBox() {
        super(new KeyDataComboBoxModel());
        addPopupMenuListener(new APopupMenuListener());
        this.setEditable(true);
        JTextComponent editComponent;
        editComponent=(JTextComponent)getEditor().getEditorComponent();
        editComponent.addFocusListener(new AFocusListener());
        editComponent.addKeyListener(new AKeyListener());
        editComponent.setDocument(new AutoCompleteDocument(editComponent));
        editComponent.addMouseListener(new AMouseListener());
    }

    public KeyAutoCompleteComboBox(boolean sorted,boolean ignoreCase){
        super(new KeyDataComboBoxModel(new SortedKeyDataList(ignoreCase)));
        addPopupMenuListener(new APopupMenuListener());
        if(!sorted)
            setModel(new KeyDataComboBoxModel());
        this.setEditable(true);
        JTextComponent editComponent;
        editComponent=(JTextComponent)getEditor().getEditorComponent();
        editComponent.addFocusListener(new AFocusListener());
        editComponent.addKeyListener(new AKeyListener());
        editComponent.setDocument(new AutoCompleteDocument(editComponent));
        editComponent.addMouseListener(new AMouseListener());
    }


    public KeyAutoCompleteComboBox(KeyDataList<?> items) {
        super(new KeyDataComboBoxModel(items));
        addPopupMenuListener(new APopupMenuListener());
        this.setEditable(true);
        JTextComponent editComponent;
        editComponent=(JTextComponent)getEditor().getEditorComponent();
        editComponent.addFocusListener(new AFocusListener());
        editComponent.addKeyListener(new AKeyListener());
        editComponent.setDocument(new AutoCompleteDocument(editComponent));
        editComponent.addMouseListener(new AMouseListener());
    }

    public KeyAutoCompleteComboBox(KeyDataComboBoxModel aModel) {
        super(aModel);
        addPopupMenuListener(new APopupMenuListener());
        this.setEditable(true);
        JTextComponent editComponent;
        editComponent=(JTextComponent)getEditor().getEditorComponent();
        editComponent.addFocusListener(new AFocusListener());
        editComponent.addKeyListener(new AKeyListener());
        editComponent.setDocument(new AutoCompleteDocument(editComponent));
        editComponent.addMouseListener(new AMouseListener());
    }

    public void setModel(ComboBoxModel model){
        if(!(model instanceof KeyDataComboBoxModel))
                throw new UnsupportedOperationException("Model not supported by KeyAutoCompleteComboBox. Use KeyDataComboboxModel");
        super.setModel(model);
    }

    public void setModel(KeyDataComboBoxModel model){
        super.setModel(model);
    }

    public void contentsChanged(ListDataEvent e) {
        Object oldSelection = selectedItemReminder;
        Object newSelection = dataModel.getSelectedItem();
        if (oldSelection == null || !oldSelection.equals(newSelection)) {
            selectedItemChanged();
            if (!isSelectingItem) {
                fireActionEvent();
            }
        }
    }

    public Object getSelectedKey(){
            return ((KeyDataComboBoxModel)dataModel).getSelectedKey();
    }

    public void setSelectedKey(Object key){
        KeyDataComboBoxModel model=(KeyDataComboBoxModel)dataModel;
        if(key!=null && key.equals(model.getSelectedKey()))
                return;
        int index=model.getData().indexOfKey(key);
        if(index>=0)
            setSelectedIndex(index);
        else
            setSelectedIndex(-1);
    }

    public int getSelectedIndex() {
            return ((KeyDataComboBoxModel)dataModel).getSelectedIndex();
    }

    public void setSelectedIndex(int anIndex) {
        int size = dataModel.getSize();
        if ( anIndex == -1 ) {
            setSelectedItem( null );
        } else if ( anIndex < -1 || anIndex >= size ) {
            throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
        } else {
            KeyDataComboBoxModel ldataModel=(KeyDataComboBoxModel)dataModel;
            int oldIndex=ldataModel.getSelectedIndex();
            int indexToSelect=anIndex;
            if(oldIndex!=indexToSelect){
                // Must toggle the state of this flag since this method
                // call may result in ListDataEvents being fired.
                isSelectingItem = true;
                ldataModel.setSelectedIndex(indexToSelect);
                isSelectingItem = false;
                if (selectedItemReminder != dataModel.getSelectedItem()) {
                    // in case a users implementation of ComboBoxModel
                    // doesn't fire a ListDataEvent when the selection
                    // changes.
                    selectedItemChanged();
                }
            }
            fireActionEvent();
        }
    }

    public void setSelectedItem(Object anObject) {
        Object oldSelection = selectedItemReminder;
        int index=-1;
        if ((oldSelection!=anObject) &&  (oldSelection == null || !oldSelection.equals(anObject))) {
            if (anObject != null && !isEditable()) {
                // For non editable combo boxes, an invalid selection
                // will be rejected.
                index=((KeyDataComboBoxModel)dataModel).indexOf(anObject);
                if (index<0) { //if not found
                    return;
                }
            }

            // Must toggle the state of this flag since this method
            // call may result in ListDataEvents being fired.
            isSelectingItem = true;
            ((KeyDataComboBoxModel)dataModel).setSelectedIndex(index);
            isSelectingItem = false;

            if (selectedItemReminder != dataModel.getSelectedItem()) {
                // in case a users implementation of ComboBoxModel
                // doesn't fire a ListDataEvent when the selection
                // changes.
                selectedItemChanged();
            }
        }
        fireActionEvent();
    }

    public void addItem(Object key,Object data) {
        ((KeyDataComboBoxModel)dataModel).addElement(key,data);
    }

    public void insertItemAt(Object key,Object data, int index) {
        ((KeyDataComboBoxModel)dataModel).insertElementAt(key, data, index);
    }

    class AutoCompleteDocument extends javax.swing.text.PlainDocument{
        boolean selecting=false;
        boolean replacing=false;
        javax.swing.text.JTextComponent editor;

        public AutoCompleteDocument(javax.swing.text.JTextComponent editor){
            this.editor=editor;
        }

        private String getItemAt(int index){
            Object item=dataModel.getElementAt(index);
            if(item==null)
                return "";
            if(item instanceof String)
                return (String)item;
            return item.toString();
        }

        public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
            if(selecting){//called as a result of selecting not typing
                super.insertString(i, s, attributeset);
                return;
            }
            if(s == null)
                return;

            String s1 = getText(0, i);
            String s2 = s1 + s;
            int matchedIndex=match(s2);
            if(matchedIndex<0){//match not found
                if(i>0){
                    s2=s1;
                    matchedIndex=match(s2);
                }
            }
            if(matchedIndex>=0){ //match found
                if(!isPopupVisible()) setPopupVisible(true);
                selecting=true;
                setSelectedIndex(matchedIndex);
                selecting=false;
                String item=getItemAt(matchedIndex);
                super.remove(0, getLength());
                super.insertString(0, item, attributeset);
                editor.select(s2.length(), item.length());
            }
         }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            if(replacing){
                super.remove(offs, len);
                return;
            }
            //if the whole text is being removed then simply removed everything and select -1
            if(len==getLength()){
                selecting=true;
                setSelectedIndex(-1);
                selecting=false;
                super.remove(0, len);
                return;
            }
            //if firstitem is being removed then match index with remaining if not matched remove everthing and select -1
            int matchedIndex=-1;
            if(offs==0){
                matchedIndex=match(getText(len, getLength()-2));
                if(matchedIndex==-1){
                    selecting=true;
                    setSelectedIndex(-1);
                    selecting=false;
                    super.remove(0, getLength());
                    return;
                }
                selecting=true;
                setSelectedIndex(matchedIndex);
                selecting=false;
                super.remove(0, getLength());
                super.insertString(0, getItemAt(matchedIndex),null);
                editor.select(len, getLength());
                return;
            }

            String s1 = getText(0, offs);
            matchedIndex=match(s1);
            if(matchedIndex==-1){
                selecting=true;
                setSelectedIndex(-1);
                selecting=false;
                super.remove(0, getLength());
                return;
            }
            selecting=true;
            setSelectedIndex(matchedIndex);
            selecting=false;
            super.remove(0, getLength());
            super.insertString(0, getItemAt(matchedIndex),null);
            editor.select(offs-1, getLength());
        }

        @Override
        public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if(selecting) return;
            //if replace is called as a result of selecting an item then simply call super.replace set replacing to true before calling super.replace and then again false;
            //offeset will be 0, length  will be equal getlength() and text will be equal to selectedItem
            Object selectedItem=dataModel.getSelectedItem();
            if(text!=null && selectedItem!=null && text.equals(selectedItem.toString())){//called because a new item was selected
                replacing=true;
                selecting=true;
                super.replace(offset, length, text, attrs);
                replacing=false;
                selecting=false;
                editor.setSelectionStart(0);
                editor.setSelectionEnd(getLength()-1);
                return;
            }
            //if replace is not as a result of selecting an item then call super.replace set relacing to true
            //if first char is being replaced and and no matching index is found prevent replace from happening  by exitimg the function
            int matchedIndex=-1;
            if(offset==0){
                matchedIndex=match(text);
                if(matchedIndex==-1){
                    return;
                }

            }
            replacing=true;
            super.replace(offset, length, text, attrs);
            replacing=false;
        }

        public int match(String s){
            return ((KeyDataComboBoxModel)dataModel).indexOfStartsWith(s);
        }
        //insert string is called even when item is replaced
        //findmatch - if match is found - select from match to remaining - the end - if selecting return
    }

    protected void finalize() throws Throwable{
        try{
            if(dataModel != null)
                dataModel.removeListDataListener(this);
            getUI().uninstallUI(this);
        }
        finally{
            super.finalize();
        }
    }

    class AFocusListener implements FocusListener{
        
        public void focusGained(FocusEvent e) {
            FocusListener[] listeners = getFocusListeners();
            if(listeners.length>1){
                e=new FocusEvent(KeyAutoCompleteComboBox.this, e.getID(), e.isTemporary(), e.getOppositeComponent());
                for(int ctr=1;ctr<listeners.length;ctr++)
                    listeners[ctr].focusGained(e);
            }
        }

        public void focusLost(FocusEvent e) {
            FocusListener[] listeners = getFocusListeners();
            if(listeners.length>1){
                e=new FocusEvent(KeyAutoCompleteComboBox.this, e.getID(), e.isTemporary(), e.getOppositeComponent());
                for(int ctr=1;ctr<listeners.length;ctr++)
                    listeners[ctr].focusLost(e);
            }
        }

    }
    class AKeyListener extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e) {
           KeyListener[] listeners=getKeyListeners();
           if(listeners.length>1){
               e=new KeyEvent(KeyAutoCompleteComboBox.this, e.getID(),e.getWhen(),e.getModifiers(),e.getKeyCode(),e.getKeyChar(),e.getKeyLocation());
               for(int ctr=1;ctr<listeners.length;ctr++)
                    listeners[ctr].keyPressed(e);
           }
        }

        @Override
        public void keyReleased(KeyEvent e) {
           KeyListener[] listeners=getKeyListeners();
           if(listeners.length>1){
               e=new KeyEvent(KeyAutoCompleteComboBox.this, e.getID(),e.getWhen(),e.getModifiers(),e.getKeyCode(),e.getKeyChar(),e.getKeyLocation());
               for(int ctr=1;ctr<listeners.length;ctr++)
                    listeners[ctr].keyReleased(e);
           }
        }

        @Override
        public void keyTyped(KeyEvent e) {
           KeyListener[] listeners=getKeyListeners();
           if(listeners.length>1){
               e=new KeyEvent(KeyAutoCompleteComboBox.this, e.getID(),e.getWhen(),e.getModifiers(),e.getKeyCode(),e.getKeyChar(),e.getKeyLocation());
               for(int ctr=1;ctr<listeners.length;ctr++)
                    listeners[ctr].keyTyped(e);
           }
        }

    }
    class AMouseListener extends MouseAdapter{

        @Override
        public void mouseClicked(MouseEvent e) {
            MouseEvent e2=new MouseEvent(KeyAutoCompleteComboBox.this, e.getID(), System.currentTimeMillis(), e.getModifiers(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
            KeyAutoCompleteComboBox.this.dispatchEvent(e2);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            MouseEvent e2=new MouseEvent(KeyAutoCompleteComboBox.this, e.getID(), System.currentTimeMillis(), e.getModifiers(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
            KeyAutoCompleteComboBox.this.dispatchEvent(e2);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MouseEvent e2=new MouseEvent(KeyAutoCompleteComboBox.this, e.getID(), System.currentTimeMillis(), e.getModifiers(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
            KeyAutoCompleteComboBox.this.dispatchEvent(e2);
        }

    }

    class APopupMenuListener implements PopupMenuListener{
        int prevSelectedIndex;
        public void popupMenuCanceled(PopupMenuEvent e) {
            ;
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            int selectedIndex=getSelectedIndex();
            if(selectedIndex!=prevSelectedIndex)
                firePropertyChange("selectedIndex", prevSelectedIndex, selectedIndex);
        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            prevSelectedIndex=getSelectedIndex();
        }

    }
}
