package iposca.model;

import java.math.BigDecimal;
import java.util.List;

public class DiscountPlan {
    private int planID;
    private String planName;
    private String discountType; // fixed or flexible
    private BigDecimal discountPercentage; // only used for fixed
    private String description;
    private boolean isActive;
    private List<FlexibleDiscountTier> tiers; // only populated for flexible plans

    public DiscountPlan() {}

    public int getPlanID() {
        return planID;
    }
    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public String getPlanName() {
        return planName;
    }
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDiscountType() {
        return discountType;
    }
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public List<FlexibleDiscountTier> getTiers() {
        return tiers;
    }
    public void setTiers(List<FlexibleDiscountTier> tiers) {
        this.tiers = tiers;
    }

    // Calculates discount for a given purchase amount
    public BigDecimal calculateDiscount(BigDecimal purchaseAmount) {
        if (discountType.equals("Fixed")) {
            return purchaseAmount.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100));
        }
        // Flexible: find the right tier
        if (tiers != null) {
            for (FlexibleDiscountTier tier : tiers) {
                boolean aboveMin = purchaseAmount.compareTo(tier.getMinAmount()) >= 0;
                boolean belowMax = tier.getMaxAmount() == null ||
                        purchaseAmount.compareTo(tier.getMaxAmount()) <= 0;
                if (aboveMin && belowMax) {
                    return purchaseAmount.multiply(tier.getDiscountPercentage())
                            .divide(BigDecimal.valueOf(100));
                }
            }
        }
        return BigDecimal.ZERO;
    }
}