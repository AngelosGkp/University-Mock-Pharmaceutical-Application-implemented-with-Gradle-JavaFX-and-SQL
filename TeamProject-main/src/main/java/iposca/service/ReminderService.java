package iposca.service;

import iposca.dao.AccountHolderDAO;
import iposca.dao.ReminderDAO;
import iposca.model.AccountHolder;
import iposca.model.Reminder;
import iposca.model.ReminderTemplate;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReminderService {
    private static final ReminderDAO reminderDAO = new ReminderDAO();
    private static final AccountHolderDAO accountDAO = new AccountHolderDAO();

    public static int generateReminders() throws SQLException {
        LocalDate today = LocalDate.now();
        List<AccountHolder> accounts = accountDAO.getAccountsWithReminderDue();
        int generated = 0;

        for (AccountHolder acc : accounts) {

            // 1st reminder
            if ("due".equals(acc.getStatus1stReminder())) {
                ReminderTemplate tmpl = reminderDAO.getTemplate("1st Reminder");
                String text = fillTemplate(tmpl.getBodyText(), acc, today);
                Reminder reminder = new Reminder(acc.getAccountID(), "1st Reminder",
                        acc.getCurrentBalance(), text, today);
                reminderDAO.insertReminder(reminder);
                accountDAO.updateReminderStatus(acc.getAccountID(), "1st", "sent");
                // Schedule 2nd reminder 15 days from now
                accountDAO.setDate2ndReminder(acc.getAccountID(), today.plusDays(15));
                generated++;
            }

            // 2nd reminder, only if date has arrived
            if ("due".equals(acc.getStatus2ndReminder())) {
                LocalDate date2nd = acc.getDate2ndReminder();
                if (date2nd != null && !today.isBefore(date2nd)) {
                    ReminderTemplate tmpl = reminderDAO.getTemplate("2nd Reminder");
                    String text = fillTemplate(tmpl.getBodyText(), acc, today);
                    Reminder reminder = new Reminder(acc.getAccountID(), "2nd Reminder",
                            acc.getCurrentBalance(), text, today);
                    reminderDAO.insertReminder(reminder);
                    accountDAO.updateReminderStatus(acc.getAccountID(), "2nd", "sent");
                    generated++;
                }
            }
        }
        return generated;
    }

    private static String fillTemplate(String template, AccountHolder acc, LocalDate today) {
        return template
                .replace("{customer_name}", acc.getFullName())
                .replace("{account_id}", acc.getAccountID())
                .replace("{amount_owed}", String.format("%.2f", acc.getCurrentBalance()))
                .replace("{payment_due_date}", today.plusDays(7).toString());
    }
}