package SDMImprovedFacade;

public class FeedbackNotification extends Notification {
    private final Feedback feedbackGiven;

    public FeedbackNotification(String originUserName, String receiverUserName, String subject, Feedback feedbackGiven, String dateOfNotification) {
        super(originUserName, receiverUserName, subject, dateOfNotification, "feedbackNotification");
        this.feedbackGiven = feedbackGiven;
    }

    public Feedback getFeedbackGiven() {
        return feedbackGiven;
    }
}
