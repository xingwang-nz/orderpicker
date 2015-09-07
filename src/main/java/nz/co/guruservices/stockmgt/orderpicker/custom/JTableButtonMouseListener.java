package nz.co.guruservices.stockmgt.orderpicker.custom;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JTable;

public class JTableButtonMouseListener
        extends MouseAdapter {
    private final JTable table;

    public JTableButtonMouseListener(final JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        final int row = e.getY() / table.getRowHeight();

        if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
            final Object value = table.getValueAt(row, column);
            if (value instanceof JButton) {
                ((JButton) value).doClick();
            }
        }
    }
}
