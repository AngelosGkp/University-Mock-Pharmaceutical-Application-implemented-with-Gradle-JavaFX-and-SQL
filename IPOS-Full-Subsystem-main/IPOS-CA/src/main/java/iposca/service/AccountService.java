package iposca.service;

import iposca.dao.AccountHolderDAO;
import iposca.model.AccountHolder;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AccountService {
    private static final AccountHolderDAO accountDAO = new AccountHolderDAO();

    // run this on app startup, implements fig 1 state machine
    public static void updateAccountStatuses() throws SQLException {
        LocalDate today = LocalDate.now();
        List<AccountHolder> accounts = accountDAO.getAll();

        for (AccountHolder acc : accounts) {
            if ("In Default".equals(acc.getAccountStatus())) continue;

            // 15th of month: if balance > 0 and status is Normal -> Suspended
            if (today.getDayOfMonth() >= 15 &&
                    acc.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0 &&
                    "Normal".equals(acc.getAccountStatus())) {
                accountDAO.updateStatus(acc.getAccountID(), "Suspended");
                accountDAO.updateReminderStatus(acc.getAccountID(), "1st", "due");
            }

            // end of month: if still unpaid and Suspended -> In Default
            boolean isEndOfMonth = today.equals(today.withDayOfMonth(today.lengthOfMonth()));
            if (isEndOfMonth &&
                    acc.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0 &&
                    "Suspended".equals(acc.getAccountStatus())) {
                accountDAO.updateStatus(acc.getAccountID(), "In Default");
                accountDAO.updateReminderStatus(acc.getAccountID(), "2nd", "due");
            }
        }
    }

    // manager only, restore In Default to Normal
    public static boolean restoreFromDefault(String accountID) throws SQLException {
        if (!AuthService.isManager()) return false;
        AccountHolder acc = accountDAO.findByID(accountID);
        if (acc == null) return false;
        if (acc.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) return false;
        accountDAO.updateStatus(accountID, "Normal");
        accountDAO.updateReminderStatus(accountID, "1st", "no_need");
        accountDAO.updateReminderStatus(accountID, "2nd", "no_need");
        return true;
    }

    // called when account holder makes a payment
    public static boolean recordPayment(String accountID, BigDecimal amount) throws SQLException {
        AccountHolder acc = accountDAO.findByID(accountID);
        if (acc == null) return false;
        BigDecimal newBalance = acc.getCurrentBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) newBalance = BigDecimal.ZERO;
        accountDAO.updateBalance(accountID, newBalance);

        // if fully paid and not In Default, reset to Normal
        if (newBalance.compareTo(BigDecimal.ZERO) == 0 &&
                !"In Default".equals(acc.getAccountStatus())) {
            accountDAO.updateStatus(accountID, "Normal");
            accountDAO.updateReminderStatus(accountID, "1st", "no_need");
            accountDAO.updateReminderStatus(accountID, "2nd", "no_need");
        }
        return true;
    }
}