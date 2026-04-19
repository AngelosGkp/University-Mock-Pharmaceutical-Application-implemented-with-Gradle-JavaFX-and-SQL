package iposca.model;

import java.math.BigDecimal;

public class FlexibleDiscountTier {
    private int tierID;
    private int planID;
    private BigDecimal minAmount;
    private BigDecimal maxAmount; // null means no upper limit
    private BigDecimal discountPercentage;

    public FlexibleDiscountTier() {}

    public int getTierID() {
        return tierID;
    }
    public void setTierID(int tierID) {
        this.tierID = tierID;
    }

    public int getPlanID() {
        return planID;
    }
    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }
    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}