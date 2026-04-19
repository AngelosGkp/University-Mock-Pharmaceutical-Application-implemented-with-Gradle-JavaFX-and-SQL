package iposca.model;

public class ReminderTemplate {
    private int templateID;
    private String templateType; // 1st reminder, 2nd reminder
    private String subject;
    private String bodyText;
    private int updatedBy;

    public ReminderTemplate() {}

    public int getTemplateID() {
        return templateID;
    }
    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getTemplateType() {
        return templateType;
    }
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyText() {
        return bodyText;
    }
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }
}