package iposca.dao;

import iposca.db.DatabaseManager;
import iposca.model.Order;
import iposca.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int insertOrderWithItems(Order order, List<OrderItem> items) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);
        try {
            String orderSql = "INSERT INTO orders_to_infopharma " +
                    "(order_reference, order_status, total_amount, placed_by, notes) " +
                    "VALUES (?, ?, ?, ?, ?)";
            int orderID;
            try (PreparedStatement stmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, order.getOrderReference());
                stmt.setString(2, "Submitted");
                stmt.setBigDecimal(3, order.getTotalAmount());
                stmt.setInt(4, order.getPlacedBy());
                stmt.setString(5, order.getNotes());
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                keys.next();
                orderID = keys.getInt(1);
            }

            String itemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_cost, total_cost) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
                for (OrderItem item : items) {
                    stmt.setInt(1, orderID);
                    stmt.setString(2, item.getItemID());
                    stmt.setInt(3, item.getQuantity());
                    stmt.setBigDecimal(4, item.getUnitCost());
                    stmt.setBigDecimal(5, item.getTotalCost());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            return orderID;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Order> getAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders_to_infopharma ORDER BY order_date DESC";
        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean updateStatus(int orderID, String status) throws SQLException {
        String sql = "UPDATE orders_to_infopharma SET order_status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderID);
            return stmt.executeUpdate() > 0;
        }
    }

    // Called when a delivery arrives - updates status and triggers stock increase
    public boolean confirmDelivery(int orderID, String courier, String trackingNumber) throws SQLException {
        String sql = "UPDATE orders_to_infopharma SET order_status = 'Delivered', " +
                "delivery_date = CURRENT_DATE, courier = ?, tracking_number = ? " +
                "WHERE order_id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, courier);
            stmt.setString(2, trackingNumber);
            stmt.setInt(3, orderID);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<OrderItem> getItemsForOrder(int orderID) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemID(rs.getInt("order_item_id"));
                item.setOrderID(rs.getInt("order_id"));
                item.setItemID(rs.getString("item_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitCost(rs.getBigDecimal("unit_cost"));
                item.setTotalCost(rs.getBigDecimal("total_cost"));
                list.add(item);
            }
        }
        return list;
    }

    public int getThisMonthCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders_to_infopharma " +
                "WHERE EXTRACT(MONTH FROM order_date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM order_date) = EXTRACT(YEAR FROM CURRENT_DATE)";
        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("order_id"));
        order.setOrderReference(rs.getString("order_reference"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setOrderStatus(rs.getString("order_status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setPlacedBy(rs.getInt("placed_by"));
        order.setCourier(rs.getString("courier"));
        order.setTrackingNumber(rs.getString("tracking_number"));
        order.setNotes(rs.getString("notes"));
        Date d1 = rs.getDate("dispatch_date");
        Date d2 = rs.getDate("delivery_date");
        if (d1 != null) order.setDispatchDate(d1.toLocalDate());
        if (d2 != null) order.setDeliveryDate(d2.toLocalDate());
        return order;
    }
}
