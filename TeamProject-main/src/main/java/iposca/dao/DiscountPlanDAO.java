package iposca.dao;

import iposca.db.DatabaseManager;
import iposca.model.DiscountPlan;
import iposca.model.FlexibleDiscountTier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountPlanDAO {

    public DiscountPlan findByID(int planID) throws SQLException {
        String sql = "SELECT * FROM discount_plans WHERE plan_id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, planID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DiscountPlan plan = mapRow(rs);
                if ("Flexible".equals(plan.getDiscountType())) {
                    plan.setTiers(getTiersForPlan(planID));
                }
                return plan;
            }
        }
        return null;
    }

    public List<DiscountPlan> getAll() throws SQLException {
        List<DiscountPlan> list = new ArrayList<>();
        String sql = "SELECT * FROM discount_plans WHERE is_active = TRUE";
        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<FlexibleDiscountTier> getTiersForPlan(int planID) throws SQLException {
        List<FlexibleDiscountTier> tiers = new ArrayList<>();
        String sql = "SELECT * FROM flexible_discount_tiers WHERE plan_id = ? ORDER BY min_amount";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, planID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FlexibleDiscountTier tier = new FlexibleDiscountTier();
                tier.setTierID(rs.getInt("tier_id"));
                tier.setPlanID(rs.getInt("plan_id"));
                tier.setMinAmount(rs.getBigDecimal("min_amount"));
                tier.setMaxAmount(rs.getBigDecimal("max_amount"));
                tier.setDiscountPercentage(rs.getBigDecimal("discount_percentage"));
                tiers.add(tier);
            }
        }
        return tiers;
    }

    private DiscountPlan mapRow(ResultSet rs) throws SQLException {
        DiscountPlan plan = new DiscountPlan();
        plan.setPlanID(rs.getInt("plan_id"));
        plan.setPlanName(rs.getString("plan_name"));
        plan.setDiscountType(rs.getString("discount_type"));
        plan.setDiscountPercentage(rs.getBigDecimal("discount_percentage"));
        plan.setDescription(rs.getString("description"));
        plan.setActive(rs.getBoolean("is_active"));
        return plan;
    }
}