/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.swing;
import javax.swing.text.*;

/**
 *
 * @author hoshi
 */
public class DecimalDocument extends PlainDocument {
    private boolean allowNegative;

    public DecimalDocument(boolean allowNegative){
        this.allowNegative=allowNegative;
    }
    
    public DecimalDocument(){
        allowNegative=true;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str == null) return;
      Double num;
      StringBuilder txt=new StringBuilder(12);
      String oldString = getText(0, getLength());
      String newString = txt.append(oldString.substring(0, offs)).append(str).append(oldString.substring(offs)).append('1').toString();
      try {
        num=Double.parseDouble(newString);
        if((!allowNegative) && num<0) return;
        super.insertString(offs, str, a);
      } catch (NumberFormatException ex) {}
    }
}
