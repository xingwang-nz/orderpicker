package nz.co.guruservices.stockmgt.orderpicker.custom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

public class NonFocusCheckbox
        extends JCheckBox {

    public NonFocusCheckbox(final String label, final JComponent focusComponent) {
        super(label);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                focusComponent.requestFocusInWindow();
            }
        });
    }

}
