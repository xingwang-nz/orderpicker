package nz.co.guruservices.stockmgt.orderpicker.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import nz.co.guruservices.stockmgt.orderpicker.common.OrderHandler;

public class OrderTableModel
        extends AbstractTableModel {

    private String[] header = { "Order Number", "Status", "User", "" };

    private List<Order> orders = new ArrayList<>();

    private final OrderHandler orderHandler;

    public OrderTableModel(final List<Order> orders, final OrderHandler orderHandler) {
        this.orders = orders;
        this.orderHandler = orderHandler;
    }

    @Override
    public String getColumnName(final int index) {

        return header[index];

    }

    @Override
    public int getRowCount() {
        return orders.size();
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        final Order order = orders.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return order.getOrderNumber();
        case 1:
            return order.getStatus();
        case 2:
            return order.getUsername();
        case 3:
            final JButton button = new JButton();
            if (OrderStatus.IN_PROGRESS.equals(order.getStatus())) {
                button.setText("Complete");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent actionEvent) {
                        if (orderHandler != null) {
                            orderHandler.process(order, OrderStatus.COMPLETE);
                        }
                    }
                });
            } else if (OrderStatus.NEW.equals(order.getStatus())) {
                button.setText("Process");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent actionEvent) {
                        if (orderHandler != null) {
                            orderHandler.process(order, OrderStatus.IN_PROGRESS);
                        }
                    }
                });
            } else {
                button.setEnabled(false);
                button.setVisible(false);
                button.setBorderPainted(false);
                button.invalidate();
                button.setText("");
            }

            return button;
        default:
            return "";
        }
    }

    public List<Order> getOrder() {
        return this.orders;
    }

    public Order getOrderByNumber(final String orderNumber) {
        for (final Order order : orders) {
            if (order.getOrderNumber().equalsIgnoreCase(orderNumber)) {
                return order;
            }
        }
        return null;
    }

}
