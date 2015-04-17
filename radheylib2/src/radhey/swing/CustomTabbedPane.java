package radhey.swing;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public class CustomTabbedPane extends JTabbedPane {

    @Override
    public void setSelectedIndex(int index) {
        Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
// if no tabs are selected
// -OR- the current focus owner is me
// -OR- I request focus from another component and get it
// then proceed with the tab switch
        if(comp instanceof JComponent)
            if (getSelectedIndex() == -1 || comp == this || requestFocusInWindow(false)|| ((JComponent)comp).getInputVerifier() == null)
                super.setSelectedIndex(index);
    }
}
