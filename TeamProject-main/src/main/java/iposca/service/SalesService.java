package iposca.service;

import iposca.dao.AccountHolderDAO;
import iposca.dao.DiscountPlanDAO;
import iposca.dao.SalesDAO;
import iposca.dao.StockDAO;
import iposca.model.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesService {
    private static final SalesDAO salesDAO = new SalesDAO();
    private static final StockDAO stockDAO = new StockDAO();
    private static final AccountHolderDAO accountDAO = new AccountHolderDAO();
    private static final DiscountPlanDAO discountPlanDAO = new DiscountPlanDAO();

    public static String recordSale(String customerType, String accountID,
                                    List<SaleItem> items, String paymentMethod,
                                    String cardType, String cardFirst4, String cardLast4,
                                    int cardExpiryMonth, int cardExpiryYear) throws SQLException {

        // calculates subtotal
        BigDecimal subtotal = items.stream()
                .map(SaleItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // calculates discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (accountID != null) {
            AccountHolder ah = accountDAO.findByID(accountID);
            if (ah != null && ah.getDiscountPlanID() != null) {
                DiscountPlan plan = discountPlanDAO.findByID(ah.getDiscountPlanID());
                if (plan != null) {
                    discountAmount = plan.calculateDiscount(subtotal);
                }
            }
        }

        // calculates VAT (currently 0% per sample data, but configurable)
        BigDecimal vatRate = BigDecimal.ZERO; // fetch from merchant_config in production
        BigDecimal afterDiscount = subtotal.subtract(discountAmount);
        BigDecimal vatAmount = afterDiscount.multiply(vatRate);
        BigDecimal totalAmount = afterDiscount.add(vatAmount);

        // builds sale object
        Sale sale = new Sale();
        sale.setSaleReference(generateReference());
        sale.setSaleDate(LocalDateTime.now());
        sale.setCustomerType(customerType);
        sale.setAccountID(accountID);
        sale.setSubtotal(subtotal);
        sale.setDiscountAmount(discountAmount);
        sale.setVatAmount(vatAmount);
        sale.setTotalAmount(totalAmount);
        sale.setPaymentMethod(paymentMethod);
        sale.setPaymentStatus("Paid");
        sale.setServedBy(AuthService.getCurrentUser().getUserID());

        // saves sale and items (single transaction in DAO)
        int saleID = salesDAO.insertSaleWithItems(sale, items);

        // deducts stock for each item
        for (SaleItem item : items) {
            stockDAO.deductStock(item.getProductID(), item.getQuantity());
        }

        // if account holder on credit, increase their balance
        if ("Credit".equals(paymentMethod) && accountID != null) {
            accountDAO.addToBalance(accountID, totalAmount);
        }

        return sale.getSaleReference();
    }

    private static String generateReference() {
        return "SALE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    public static List<Sale> getSalesToday() throws SQLException {
        return salesDAO.getSalesToday();
    }

    public static List<Sale> getRecentSales(int limit) throws SQLException {
        return salesDAO.getRecent(limit);
    }
}