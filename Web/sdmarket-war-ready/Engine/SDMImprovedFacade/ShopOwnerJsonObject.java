package SDMImprovedFacade;

import java.util.List;

public class ShopOwnerJsonObject extends UserJsonObject {
    private final String userName;
    private double totalEarnings;
    private int amountOfStoresOwned;
    private int ordersMadeFromOwnedStores;
    private double averageRating;
    private List<Notification> notifications;

    public ShopOwnerJsonObject(List<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers, List<Notification> notifications,
                               double totalEarnings, int amountOfStoresOwned, int ordersMadeFromOwnedStores, double averageRating, String userName) {
        super(systemZones, userTransactions, otherUsers);
        this.totalEarnings = totalEarnings;
        this.amountOfStoresOwned = amountOfStoresOwned;
        this.ordersMadeFromOwnedStores = ordersMadeFromOwnedStores;
        this.averageRating = averageRating;
        this.notifications = notifications;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public int getAmountOfStoresOwned() {
        return amountOfStoresOwned;
    }

    public void setAmountOfStoresOwned(int amountOfStoresOwned) {
        this.amountOfStoresOwned = amountOfStoresOwned;
    }

    public int getOrdersMadeFromOwnedStores() {
        return ordersMadeFromOwnedStores;
    }

    public void setOrdersMadeFromOwnedStores(int ordersMadeFromOwnedStores) {
        this.ordersMadeFromOwnedStores = ordersMadeFromOwnedStores;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
