package SDMImprovedFacade;

public class Notification {
    private final String notificationType;
    private final String originUserName;
    private final String receiverUserName;
    private final String subject;
    private final String dateOfNotification;

    public Notification(String originUserName, String receiverUserName, String subject, String dateOfNotification, String notificationType) {
        this.originUserName = originUserName;
        this.receiverUserName = receiverUserName;
        this.subject = subject;
        this.dateOfNotification = dateOfNotification;
        this.notificationType = notificationType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getOriginUserName() {
        return originUserName;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public String getSubject() {
        return subject;
    }

    public String getDateOfNotification() {
        return dateOfNotification;
    }
}
