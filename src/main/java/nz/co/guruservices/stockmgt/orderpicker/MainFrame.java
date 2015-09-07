package nz.co.guruservices.stockmgt.orderpicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultStyledDocument;

import nz.co.guruservices.stockmgt.orderpicker.business.OrderService;
import nz.co.guruservices.stockmgt.orderpicker.custom.JTableButtonMouseListener;
import nz.co.guruservices.stockmgt.orderpicker.custom.JTableButtonRenderer;
import nz.co.guruservices.stockmgt.orderpicker.model.MessageType;
import nz.co.guruservices.stockmgt.orderpicker.model.Order;
import nz.co.guruservices.stockmgt.orderpicker.model.OrderStatus;
import nz.co.guruservices.stockmgt.orderpicker.model.OrderTableModel;

public class MainFrame
        extends JFrame {

    private JTextField orderNumberField;

    private JTextField usernameField;

    private final OrderService orderService = new OrderService();

    private JTable table;

    private JScrollPane contentScrollPane;

    private JScrollPane msgScrollPane;
    private JTextPane msgPane;

    public MainFrame() {
        this.setTitle("Orders");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // center the jframe on screen
        setLocationRelativeTo(null);
        setVisible(true);

        setLayout(new BorderLayout());

        initFields();

        contentScrollPane = new JScrollPane();
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        getContentPane().add(contentScrollPane, BorderLayout.CENTER);

        msgPane = new JTextPane();
        msgScrollPane = new JScrollPane(msgPane);
        msgScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
        getContentPane().add(msgScrollPane, BorderLayout.SOUTH);

        setMessage(MessageType.ERROR, "dsdsdsds");

        // loadOrders();

    }

    private void initFields() {
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("Order number: "), constraints);

        orderNumberField = new JTextField();
        orderNumberField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startProcessOrder();
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.7;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        topPanel.add(orderNumberField, constraints);

        final JButton processButton = new JButton("Process");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startProcessOrder();
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(processButton, constraints);

        // load order line
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("User: "), constraints);

        usernameField = new JTextField();
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.7;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        topPanel.add(usernameField, constraints);

        final JButton loadOrderButton = new JButton("Load Orders");
        loadOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                loadOrders();
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0.2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(loadOrderButton, constraints);

        getContentPane().add(topPanel, BorderLayout.NORTH);

    }

    private void loadOrders() {

        new SwingWorker<List<Order>, Void>() {

            @Override
            protected List<Order> doInBackground()
                    throws Exception {
                final List<Order> orders = new ArrayList<>();
                Order order = new Order();
                order.setId(1L);
                order.setOrderNumber("A11111");
                order.setStatus(OrderStatus.NEW);
                order.setUsername("Xing");
                orders.add(order);

                order = new Order();
                order.setId(2L);
                order.setOrderNumber("A11112");
                order.setStatus(OrderStatus.NEW);
                order.setUsername("Xing2");
                orders.add(order);

                order = new Order();
                order.setId(3L);
                order.setOrderNumber("A11113");
                order.setStatus(OrderStatus.NEW);
                order.setUsername("Xing3");
                orders.add(order);

                return orders;
            }

            @Override
            protected void done() {
                try {
                    final List<Order> orders = get();
                    final OrderTableModel model = new OrderTableModel(orders);
                    table = new JTable(model);
                    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    table.getColumnModel().getColumn(3).setCellRenderer(new JTableButtonRenderer());
                    table.addMouseListener(new JTableButtonMouseListener(table));
                    table.setRowSelectionAllowed(true);

                    table.setCellSelectionEnabled(true);
                    table.getTableHeader().setBackground(new Color(153, 204, 255));
                    table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));

                    contentScrollPane.setViewportView(table);

                    setMessage(MessageType.INFO, "Orders are loaded successfully");

                } catch (final Exception e) {
                    e.printStackTrace();
                    setMessage(MessageType.ERROR, String.format("Failed load order: %s", e.getMessage()));
                }
            }

        }.execute();

    }

    private void startProcessOrder() {
        if (table == null) {
            return;
        }
        final String barcode = orderNumberField.getText();
        if (barcode == null || barcode.trim().equals("")) {
            return;
        }
        new ProcessOrderWorker(barcode).execute();

    }

    private class ProcessOrderWorker
            extends SwingWorker<Order, Void> {

        private String orderNumber;

        public ProcessOrderWorker(final String orderNumber) {
            this.orderNumber = orderNumber;
        }

        @Override
        protected Order doInBackground()
                throws Exception {
            final OrderTableModel model = (OrderTableModel) table.getModel();
            final Order order = model.getOrderByNumber(orderNumber);

            // TODO: call OrderService to update status
            order.setStatus(OrderStatus.IN_PROGRESS);
            return order;
        }

        @Override
        protected void done() {
            try {
                final Order order = get();
                final OrderTableModel model = (OrderTableModel) table.getModel();
                model.fireTableDataChanged();
            } catch (final Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void setMessage(final MessageType messageType, final String message) {
        try {
            // msgPane.setText(message);
            final DefaultStyledDocument doc = new DefaultStyledDocument();
            doc.insertString(0, message, null);
            msgPane.setDocument(doc);
            switch (messageType) {
            case INFO:
                msgPane.setForeground(Color.blue);
                break;
            case WARNING:
                msgPane.setForeground(Color.orange);
                break;
            case ERROR:
                msgPane.setForeground(Color.red);
                break;

            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

    }

}
