package nz.co.guruservices.stockmgt.orderpicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultStyledDocument;

import nz.co.guruservices.stockmgt.orderpicker.business.OrderService;
import nz.co.guruservices.stockmgt.orderpicker.common.OrderHandler;
import nz.co.guruservices.stockmgt.orderpicker.common.OrderSearchCriteria;
import nz.co.guruservices.stockmgt.orderpicker.custom.JTableButtonMouseListener;
import nz.co.guruservices.stockmgt.orderpicker.custom.JTableButtonRenderer;
import nz.co.guruservices.stockmgt.orderpicker.db.DBManager;
import nz.co.guruservices.stockmgt.orderpicker.model.MessageType;
import nz.co.guruservices.stockmgt.orderpicker.model.Order;
import nz.co.guruservices.stockmgt.orderpicker.model.OrderStatus;
import nz.co.guruservices.stockmgt.orderpicker.model.OrderTableModel;

public class MainFrame
        extends JFrame {

    private JTextField orderNumberField;

    private JTextField usernameField;

    private JTable table;

    private JScrollPane contentScrollPane;

    private JScrollPane msgScrollPane;
    private JTextPane msgPane;

    // service object
    private AppProperties appProperties;

    private DBManager dbManager;

    private OrderService orderService;

    public MainFrame() {
        initUI();
        initService();

    }

    private void initService() {
        try {
            appProperties = new AppProperties();
            appProperties.loaddProperties();
            dbManager = new DBManager(appProperties);
            orderService = new OrderService();
            orderService.setDbManager(dbManager);
        } catch (final Exception e) {
            e.printStackTrace();
            logMessage(MessageType.ERROR, String.format("Error occurred during initialization: %s", e.getMessage()));
        }

    }

    private void initUI() {
        this.setTitle("Orders");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // center the jframe on screen
        setLocationRelativeTo(null);
        setVisible(true);

        setLayout(new BorderLayout());

        initTopPanelFields();

        // content middle panel
        contentScrollPane = new JScrollPane();
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        getContentPane().add(contentScrollPane, BorderLayout.CENTER);

        // bottom panel
        msgPane = new JTextPane();
        msgScrollPane = new JScrollPane(msgPane);
        msgScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
        getContentPane().add(msgScrollPane, BorderLayout.SOUTH);
    }

    private void initTopPanelFields() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        getContentPane().add(panel, BorderLayout.NORTH);

        initProcessOrderNumberPanel(panel);

        panel.add(new JSeparator(SwingConstants.HORIZONTAL));

        initSearchPanel(panel);

    }

    private JCheckBox newCheckbox;
    private JCheckBox inProgressCheckbox;
    private JCheckBox completeCheckbox;

    private void initSearchPanel(final JPanel parentPanel) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        parentPanel.add(panel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.05;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("User: "), constraints);

        usernameField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.45;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, constraints);

        newCheckbox = new JCheckBox("New");
        newCheckbox.setSelected(true);
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(newCheckbox, constraints);

        inProgressCheckbox = new JCheckBox("In Progress");
        inProgressCheckbox.setSelected(true);
        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(inProgressCheckbox, constraints);

        completeCheckbox = new JCheckBox("Complete");
        constraints = new GridBagConstraints();
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(completeCheckbox, constraints);

        final JButton searchOrderButton = new JButton("Search Orders");
        searchOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                searchOrders();
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchOrderButton, constraints);
    }

    private JPanel initProcessOrderNumberPanel(final JPanel parentPanel) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        parentPanel.add(panel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Order number: "), constraints);

        orderNumberField = new JTextField();
        orderNumberField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startProcessOrder(orderNumberField.getText());
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.7;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(orderNumberField, constraints);

        final JButton processButton = new JButton("Process");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startProcessOrder(orderNumberField.getText());
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(processButton, constraints);
        return panel;
    }

    private void searchOrders() {

        new SwingWorker<List<Order>, Void>() {

            @Override
            protected List<Order> doInBackground()
                    throws Exception {

                final OrderSearchCriteria criteria = new OrderSearchCriteria();
                criteria.setUsername(usernameField.getText());
                if (newCheckbox.isSelected()) {
                    criteria.addSearchStatus(OrderStatus.NEW);
                }
                if (inProgressCheckbox.isSelected()) {
                    criteria.addSearchStatus(OrderStatus.IN_PROGRESS);
                }

                if (completeCheckbox.isSelected()) {
                    criteria.addSearchStatus(OrderStatus.COMPLETE);
                }
                return orderService.searchOrders(criteria);

            }

            @Override
            protected void done() {
                try {
                    final List<Order> orders = get();
                    final OrderTableModel model = new OrderTableModel(orders, new OrderHandler() {

                        @Override
                        public void process(final Order order, final OrderStatus newStatus) {

                            if (OrderStatus.IN_PROGRESS.equals(newStatus)) {
                                startProcessOrder(order.getOrderNumber());
                            } else if (OrderStatus.COMPLETE.equals(newStatus)) {
                                completeOrder(order.getOrderNumber());
                            }

                        }

                    });
                    table = new JTable(model);
                    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    table.getColumnModel().getColumn(3).setCellRenderer(new JTableButtonRenderer());
                    table.addMouseListener(new JTableButtonMouseListener(table));
                    table.setRowSelectionAllowed(true);

                    table.setCellSelectionEnabled(true);
                    table.getTableHeader().setBackground(new Color(153, 204, 255));
                    table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));

                    contentScrollPane.setViewportView(table);

                    logMessage(MessageType.INFO, "Orders are loaded successfully");

                } catch (final Exception e) {
                    e.printStackTrace();
                    logMessage(MessageType.ERROR, String.format("Failed load order: %s", e.getMessage()));
                }
            }

        }.execute();

    }

    // change order to in_progress
    private void startProcessOrder(final String orderNumber) {
        if (orderNumber == null || orderNumber.trim().equals("")) {
            return;
        }
        new ProcessOrderWorker(orderNumber, OrderStatus.IN_PROGRESS).execute();
    }

    private void completeOrder(final String orderNumber) {
        new ProcessOrderWorker(orderNumber, OrderStatus.COMPLETE).execute();
    }

    /**
     * the process order worker to chagne order with the number to be the new status
     * 
     *
     */
    private class ProcessOrderWorker
            extends SwingWorker<Order, Void> {

        private final String orderNumber;

        private final OrderStatus newStatus;

        public ProcessOrderWorker(final String orderNUmber, final OrderStatus newStatus) {
            this.orderNumber = orderNUmber;
            this.newStatus = newStatus;
        }

        @Override
        protected Order doInBackground()
                throws Exception {

            if (!OrderStatus.IN_PROGRESS.equals(newStatus) && !OrderStatus.COMPLETE.equals(newStatus)) {
                logMessage(MessageType.WARNING, String.format("Process order to be %s", newStatus));
                return null;
            }

            final Order order = orderService.getOrderByNumber(orderNumber.trim());
            if (order == null) {
                logMessage(MessageType.WARNING, String.format("Order %s doesn't not exist", orderNumber));
                return null;
            }

            if (OrderStatus.IN_PROGRESS.equals(newStatus) && !order.isNewOrder()) {
                logMessage(MessageType.WARNING, String.format("Cannot process the order %s, it is not a new order", orderNumber));
                return null;
            }

            if (OrderStatus.COMPLETE.equals(newStatus) && !order.isInProgress()) {
                logMessage(MessageType.WARNING, String.format("Cannot complete order %s, it is not in_progress", orderNumber));
                return null;
            }

            orderService.updateOrderStatus(order.getId(), newStatus);
            logMessage(MessageType.INFO, String.format("Order %s has been updated to be %s", orderNumber, newStatus));

            return order;
        }

        @Override
        protected void done() {
            try {
                if (get() != null && table != null) {
                    final OrderTableModel model = (OrderTableModel) table.getModel();
                    final Order order = model.getOrderByNumber(orderNumber);
                    if (order != null) {
                        order.setStatus(newStatus);
                        model.fireTableDataChanged();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                logMessage(MessageType.ERROR, e.getMessage());
                e.printStackTrace();
            }

        }

    }

    public void logMessage(final MessageType messageType, final String message) {
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
                msgPane.setForeground(new Color(184, 110, 37));
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
