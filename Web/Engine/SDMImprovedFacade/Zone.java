package SDMImprovedFacade;

import java.util.HashMap;

public class Zone {
    private String zoneName;
    private String ownerName;
    private int amountOfItemTypesInZone;
    private int amountOfStoresInZone;
    private int amountOfOrdersInZone;
    private double averageOrdersCostWithoutDelivery;

    private final HashMap<Integer, Store> storesInZone = new HashMap<>();
    private final HashMap<Integer, Order> ordersMadeInZone = new HashMap<>();
    private final HashMap<Integer, StoreItem> itemsAvailableInZone = new HashMap<>();

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getAmountOfItemTypesInZone() {
        return amountOfItemTypesInZone;
    }

    public void setAmountOfItemTypesInZone(int amountOfItemTypesInZone) {
        this.amountOfItemTypesInZone = amountOfItemTypesInZone;
    }

    public int getAmountOfStoresInZone() {
        return amountOfStoresInZone;
    }

    public void setAmountOfStoresInZone(int amountOfStoresInZone) {
        this.amountOfStoresInZone = amountOfStoresInZone;
    }

    public int getAmountOfOrdersInZone() {
        return amountOfOrdersInZone;
    }

    public void setAmountOfOrdersInZone(int amountOfOrdersInZone) {
        this.amountOfOrdersInZone = amountOfOrdersInZone;
    }

    public double getAverageOrdersCostWithoutDelivery() {
        return averageOrdersCostWithoutDelivery;
    }

    public void setAverageOrdersCostWithoutDelivery(double averageOrdersCostWithoutDelivery) {
        this.averageOrdersCostWithoutDelivery = averageOrdersCostWithoutDelivery;
    }

    public HashMap<Integer, StoreItem> getItemsAvailableInZone() {
        return itemsAvailableInZone;
    }

    public HashMap<Integer, Store> getStoresInZone() {
        return storesInZone;
    }

    public HashMap<Integer, Order> getOrdersMadeInZone() {
        return ordersMadeInZone;
    }
}
