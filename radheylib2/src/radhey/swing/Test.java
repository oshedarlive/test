/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
/**
 *
 * @author hoshi
 */
public class Test {

    /**
     * @param args the command line arguments
     */

    static JPanel panel;
    static JFrame frame;
    static KeyAutoCompleteComboBox combo;
    static JButton button;
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        frame=new JFrame();
        System.out.println("".equals(null));
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        panel=new JPanel();
        combo=new KeyAutoCompleteComboBox(true, true);
        combo.addItem(4,511111111);combo.addItem(5,122222222);combo.addItem(3,333333333);combo.addItem(1,444444444);
        combo.setEditable(true);
        button=new JButton("Click me");
        //ActionListener action=combo.new MyAction();
        button.addActionListener(new AListener());
        frame.setBounds(300, 300, 600, 450);
        panel.add(combo);
        panel.add(button);
        DateChooser2 datechooser=new DateChooser2();
        datechooser.addFocusListener(new AFocusListener());
        panel.add(datechooser);
        //frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(panel);
        //frame.getContentPane().add(button);
        frame.setVisible(true);
    }

    static int indexOf(int[] data,int item){
        int low=0,hi=data.length-1,mid;
        while(hi>low){
            mid=(low+hi)/2;
            if(item==data[mid])
                return mid;
            else if(item>data[mid])
                low=mid+1;
            else
                hi=mid-1;
        }
        if(hi==low && item==data[hi])
            return hi;

        return -1;
    }
    static class AListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            //panel.setVisible(false);
            
            //panel.remove(combo);
            //panel.validate();
            //panel.repaint();
            combo.setModel(new KeyDataComboBoxModel());
            if(1==1) return;
            frame.getContentPane().remove(panel);
            frame.getContentPane().validate();
            frame.getContentPane().repaint();
            panel=null;
            combo=null;            
            System.gc();
            System.gc();
            //frame.getContentPane().doLayout();
            //frame.getContentPane().add
            if(1==1) return;
            if(combo.isVisible())
                combo.setVisible(false);
            else
                combo.setVisible(true);
        }

    }

    static class AFocusListener implements FocusListener{

        public void focusGained(FocusEvent e) {
            System.out.println("focus gained");
        }

        public void focusLost(FocusEvent e) {
            System.out.println("focus lost");
        }

    }

}


class MyCombo extends JComboBox{

    public MyCombo() {
        setUI(new MyComboUI());
    }

    public MyCombo(ComboBoxModel model){
        super(model);
        setUI(new MyComboUI());
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
}
class MyComboUI extends BasicComboBoxUI{
    public void uninstallUI(JComponent c){
        System.out.println("in uninstallui");
        super.uninstallUI(c);
    }

    @Override
    protected void uninstallListeners() {
        System.out.println("in uninstalllisteners");
        super.uninstallListeners();
    }

}