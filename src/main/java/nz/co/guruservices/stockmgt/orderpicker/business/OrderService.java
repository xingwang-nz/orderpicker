package nz.co.guruservices.stockmgt.orderpicker.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.stockmgt.orderpicker.common.OrderSearchCriteria;
import nz.co.guruservices.stockmgt.orderpicker.db.DBManager;
import nz.co.guruservices.stockmgt.orderpicker.model.Order;
import nz.co.guruservices.stockmgt.orderpicker.model.OrderStatus;

public class OrderService {

    private DBManager dbManager;

    public void setDbManager(final DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Order> searchOrders(final OrderSearchCriteria criteria)
            throws SQLException {

        final StringBuilder sql = new StringBuilder("select o.*, u.user_name from \"order\" o join \"user\" u on u.id=o.user_id");

        boolean hasUsernameSearch = false;
        if (criteria.getUsername() != null && !criteria.getUsername().trim().equals("")) {
            sql.append(" where lower(u.user_name) = '").append(criteria.getUsername().toLowerCase()).append("'");
            hasUsernameSearch = true;
        }

        final List<OrderStatus> statusList = criteria.getStatusList();
        if (statusList.size() > 0) {
            sql.append(hasUsernameSearch ? " and " : " where ");
            sql.append("status in (");
            boolean firstStatus = true;
            for (final OrderStatus orderStatus : statusList) {
                sql.append(!firstStatus ? "," : "");
                sql.append("'").append(orderStatus.toString()).append("'");
                firstStatus = false;
            }
            sql.append(")");
        }

        try (Connection connection = dbManager.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            final List<Order> orders = new ArrayList<>();

            final ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                final Order order = mapRow(rs);
                order.setUsername(rs.getString("user_name"));
                orders.add(order);
            }

            return orders;
        }

    }

    private Order mapRow(final ResultSet rs)
            throws SQLException {
        final Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("order_no"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        return order;
    }

    public Order getOrderByNumber(final String orderNumber)
            throws SQLException {
        final String sql = "select * from \"order\" where order_no = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orderNumber);
            final ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            } else {
                return null;
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

    public void updateOrderStatus(final long id, final OrderStatus status)
            throws SQLException {
        final String sql = "update \"order\" set status=? where id=?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, status.toString());
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
            connection.commit();
        } catch (final SQLException e) {
            e.printStackTrace();
            connection.rollback();

            throw e;

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

    private List<Order> getDummy() {
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

}
