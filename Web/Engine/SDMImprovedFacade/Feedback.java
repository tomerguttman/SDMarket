package SDMImprovedFacade;

public class Feedback {
    private int rating; // between 1 - 5
    private String review;
    private String customerName;
    private String dateOfFeedback;
    private String storeName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDateOfFeedback() {
        return dateOfFeedback;
    }

    public void setDateOfFeedback(String dateOfFeedback) {
        this.dateOfFeedback = dateOfFeedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
