package nz.co.guruservices.stockmgt.orderpicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import nz.co.guruservices.stockmgt.orderpicker.custom.NonFocusCheckbox;
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

        setLayout(new BorderLayout());

        initTopPanel();

        // content middle panel
        contentScrollPane = new JScrollPane();
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        getContentPane().add(contentScrollPane, BorderLayout.CENTER);

        // bottom panel
        msgPane = new JTextPane();
        msgScrollPane = new JScrollPane(msgPane);
        msgScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
        getContentPane().add(msgScrollPane, BorderLayout.SOUTH);

        setVisible(true);

        // Make textField get the focus whenever frame is activated.
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(final WindowEvent e) {
                orderNumberField.requestFocusInWindow();
            }
        });

    }

    private void focuseOrderNumberField() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                orderNumberField.requestFocusInWindow();
            }
        });
    }

    private void initTopPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        getContentPane().add(panel, BorderLayout.NORTH);

        panel.add(initProcessOrderNumberPanel());
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(initSearchPanel());
    }

    // private void initTopPanel() {
    // final JTabbedPane tabbedPane = new JTabbedPane();
    //
    // tabbedPane.addTab("Scan", initProcessOrderNumberPanel());
    // tabbedPane.addTab("Search", initSearchPanel());
    //
    // tabbedPane.addChangeListener(new ChangeListener() {
    //
    // @Override
    // public void stateChanged(final ChangeEvent changeEvent) {
    // final JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
    // final int index = sourceTabbedPane.getSelectedIndex();
    // if (index == 0) {
    // focuseOrderNumberField();
    // }
    //
    // }
    // });
    //
    // getContentPane().add(tabbedPane, BorderLayout.NORTH);
    // }

    private NonFocusCheckbox newCheckbox;
    private NonFocusCheckbox inProgressCheckbox;
    private NonFocusCheckbox completeCheckbox;

    private JPanel initSearchPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("                        "), constraints);

        final JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(0, 3));
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.8;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(checkboxPanel, constraints);

        newCheckbox = new NonFocusCheckbox("New", orderNumberField);
        newCheckbox.setSelected(true);
        checkboxPanel.add(newCheckbox);

        inProgressCheckbox = new NonFocusCheckbox("In Progress", orderNumberField);
        inProgressCheckbox.setSelected(true);
        checkboxPanel.add(inProgressCheckbox);

        completeCheckbox = new NonFocusCheckbox("Complete", orderNumberField);
        checkboxPanel.add(completeCheckbox);

        final JButton searchOrderButton = new JButton("Load Orders");
        searchOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                searchOrders();
                focuseOrderNumberField();
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchOrderButton, constraints);

        return panel;
    }

    private JPanel initProcessOrderNumberPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.05;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Order number: "), constraints);

        orderNumberField = new JTextField();
        orderNumberField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startProcessOrder(orderNumberField.getText(), true);
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.85;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(orderNumberField, constraints);

        final JButton processButton = new JButton("Process");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startProcessOrder(orderNumberField.getText(), true);
            }

        });
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
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

                // criteria.setUsername(usernameField.getText());

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
                                startProcessOrder(order.getOrderNumber(), false);
                            } else if (OrderStatus.COMPLETE.equals(newStatus)) {
                                completeOrder(order.getOrderNumber());
                            }

                        }

                    });
                    table = new JTable(model);
                    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    table.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(final java.awt.event.MouseEvent evt) {
                            focuseOrderNumberField();
                        }
                    });

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
    private void startProcessOrder(final String orderNumber, final boolean clearOrderNumberField) {
        if (orderNumber == null || orderNumber.trim().equals("")) {
            focuseOrderNumberField();
            return;
        }
        final ProcessOrderWorker processOrderWorker = new ProcessOrderWorker(orderNumber, OrderStatus.IN_PROGRESS);
        if (clearOrderNumberField) {
            processOrderWorker.setProcessOrderListener(new ProcessOrderListener() {

                @Override
                public void onSuccess() {
                    orderNumberField.setText("");
                }

            });
        }
        processOrderWorker.execute();
        orderNumberField.setText("");
        focuseOrderNumberField();
    }

    private void completeOrder(final String orderNumber) {
        new ProcessOrderWorker(orderNumber, OrderStatus.COMPLETE).execute();
        focuseOrderNumberField();
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

        private ProcessOrderListener processOrderListener;

        public ProcessOrderWorker(final String orderNUmber, final OrderStatus newStatus) {
            this.orderNumber = orderNUmber;
            this.newStatus = newStatus;
        }

        @Override
        protected Order doInBackground()
                throws Exception {

            try {

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
                    logMessage(MessageType.WARNING, String.format("Order %s is already in progress", orderNumber));
                    return null;
                }

                if (OrderStatus.COMPLETE.equals(newStatus) && !order.isInProgress()) {
                    logMessage(MessageType.WARNING, String.format("Cannot complete order %s, it is not in_progress", orderNumber));
                    return null;
                }

                orderService.updateOrderStatus(order.getId(), newStatus);
                logMessage(MessageType.INFO, String.format("Order %s has been updated to be %s", orderNumber, newStatus));

                return order;
            } catch (final Exception e) {
                e.printStackTrace();
                logMessage(MessageType.ERROR, "Process order failed: " + e.getMessage());
                return null;
            }
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
                    if (processOrderListener != null) {
                        processOrderListener.onSuccess();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                logMessage(MessageType.ERROR, e.getMessage());
                e.printStackTrace();
            }

        }

        public void setProcessOrderListener(final ProcessOrderListener processOrderListener) {
            this.processOrderListener = processOrderListener;
        }

    }

    private interface ProcessOrderListener {
        void onSuccess();
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
