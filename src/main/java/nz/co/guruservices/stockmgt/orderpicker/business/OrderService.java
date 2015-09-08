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

    public List<Order> loadOrders(final OrderSearchCriteria criteria)
            throws SQLException {

        String sql = "select o.*, u.user_name from \"order\" o join \"user\" u on u.id=o.user_id where o.status != 'COMPLETE'";
        if (criteria.getUsername() != null && !criteria.getUsername().trim().equals("")) {
            sql += " and lower(u.user_name) = '" + criteria.getUsername().toLowerCase() + "'";
        }

        try (Connection connection = dbManager.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            final List<Order> orders = new ArrayList<>();

            final ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                final Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setOrderNumber(rs.getString("order_no"));
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                order.setUsername(rs.getString("user_name"));
                orders.add(order);
            }

            return orders;
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
