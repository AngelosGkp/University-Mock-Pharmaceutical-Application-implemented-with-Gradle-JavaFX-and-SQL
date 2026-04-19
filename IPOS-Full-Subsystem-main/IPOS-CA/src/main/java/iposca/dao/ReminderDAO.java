package iposca.dao;

import iposca.db.DatabaseManager;
import iposca.model.Reminder;
import iposca.model.ReminderTemplate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {

    public boolean insertReminder(Reminder reminder) throws SQLException {
        String sql = "INSERT INTO reminders (account_id, reminder_type, amount_owed, " +
                "reminder_text, date_generated, sent_via, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, reminder.getAccountID());
            stmt.setString(2, reminder.getReminderType());
            stmt.setBigDecimal(3, reminder.getAmountOwed());
            stmt.setString(4, reminder.getReminderText());
            stmt.setDate(5, Date.valueOf(reminder.getDateGenerated()));
            stmt.setString(6, reminder.getSentVia());
            stmt.setString(7, reminder.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Reminder> getRemindersForAccount(String accountID) throws SQLException {
        List<Reminder> list = new ArrayList<>();
        String sql = "SELECT * FROM reminders WHERE account_id = ? ORDER BY date_generated DESC";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, accountID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public ReminderTemplate getTemplate(String templateType) throws SQLException {
        String sql = "SELECT * FROM reminder_templates WHERE template_type = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, templateType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ReminderTemplate t = new ReminderTemplate();
                t.setTemplateID(rs.getInt("template_id"));
                t.setTemplateType(rs.getString("template_type"));
                t.setSubject(rs.getString("subject"));
                t.setBodyText(rs.getString("body_text"));
                return t;
            }
        }
        return null;
    }

    public boolean updateTemplate(ReminderTemplate template) throws SQLException {
        String sql = "UPDATE reminder_templates SET subject = ?, body_text = ?, updated_by = ? " +
                "WHERE template_type = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, template.getSubject());
            stmt.setString(2, template.getBodyText());
            stmt.setInt(3, template.getUpdatedBy());
            stmt.setString(4, template.getTemplateType());
            return stmt.executeUpdate() > 0;
        }
    }

    private Reminder mapRow(ResultSet rs) throws SQLException {
        Reminder r = new Reminder();
        r.setReminderID(rs.getInt("reminder_id"));
        r.setAccountID(rs.getString("account_id"));
        r.setReminderType(rs.getString("reminder_type"));
        r.setAmountOwed(rs.getBigDecimal("amount_owed"));
        r.setReminderText(rs.getString("reminder_text"));
        r.setDateGenerated(rs.getDate("date_generated").toLocalDate());
        Date sent = rs.getDate("date_sent");
        if (sent != null) r.setDateSent(sent.toLocalDate());
        r.setSentVia(rs.getString("sent_via"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}