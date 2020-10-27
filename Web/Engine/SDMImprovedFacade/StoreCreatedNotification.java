package SDMImprovedFacade;

import generatedClasses.Location;

public class StoreCreatedNotification extends Notification {
    private final String storeName;
    private final Location storeLocation;
    private String totalItemsSellingOutOfTotalItemsAvailableString;

    public StoreCreatedNotification(String originUserName, String receiverUserName,
                                    String subject, String storeName, Location storeLocation, String dateOfNotification) {
        super(originUserName, receiverUserName, subject, dateOfNotification, "storeCreatedNotification");
        this.storeName = storeName;
        this.storeLocation = storeLocation;
    }

    public String getStoreName() {
        return storeName;
    }

    public Location getStoreLocation() {
        return storeLocation;
    }

    public String getTotalItemsSellingOutOfTotalItemsAvailableString() {
        return totalItemsSellingOutOfTotalItemsAvailableString;
    }

    public void setTotalItemsSellingOutOfTotalItemsAvailableString(int itemsSelling, int itemsAvailableInZone) {
        this.totalItemsSellingOutOfTotalItemsAvailableString = String.format("%d / %d", itemsSelling, itemsAvailableInZone);
    }
}
