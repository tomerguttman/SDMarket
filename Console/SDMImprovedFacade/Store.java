package SDMImprovedFacade;

import jaxb.generatedClasses.Location;
import jaxb.generatedClasses.SDMStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Store {
    private final int Id;
    private int deliveryPpk;
    private double totalOrdersRevenue = 0;
    private String name;
    private Location storeLocation;
    private Map<Integer, StoreItem> itemsBeingSold;
    private final List<Order> storeOrdersHistory;

    public Store(SDMStore inputStore){
        this.Id = inputStore.getId();
        this.deliveryPpk = inputStore.getDeliveryPpk();
        this.name = inputStore.getName();
        this.storeLocation = inputStore.getLocation();
        storeOrdersHistory = new ArrayList<>();
    }

    public double getTotalOrdersRevenue() {
        return totalOrdersRevenue;
    }

    public Map<Integer, StoreItem> getItemsBeingSold() {
        return itemsBeingSold;
    }

    public List<Order> getStoreOrdersHistory() {
        return storeOrdersHistory;
    }

    public Order getLastOrder(){
        return this.storeOrdersHistory.get(this.storeOrdersHistory.size() - 1);
    }

    public void setItemBeingSold(Map<Integer, StoreItem> itemBeingSold) {
        this.itemsBeingSold = itemBeingSold;
    }

    public int getId() {
        return Id;
    }

    public int getDeliveryPpk() {
        return deliveryPpk;
    }

    public void setDeliveryPpk(int deliveryPpk) {
        this.deliveryPpk = deliveryPpk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(Location storeLocation) {
        this.storeLocation = storeLocation;
    }

    public void generateOrder(String orderDate, int orderId, List<StoreItem> itemsInOrder, Location userCoordinates, Location userLocation){
        Order order = new Order(orderDate, orderId, this.Id, calculateDistance(userCoordinates) * deliveryPpk, this.name, itemsInOrder, userLocation);
        storeOrdersHistory.add(order);
        this.totalOrdersRevenue += order.getTotalOrderCost();
    }

    public double calculateDistance(Location userCoordinates) {
        int x1 = userCoordinates.getX(), x2 = storeLocation.getX(), y1 = userCoordinates.getY(), y2 = storeLocation.getY();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public String displayStoreForPurchase() {
        return "- ID: " + this.Id + ", Name: " +
                this.name + ", PPK: " + this.deliveryPpk + "\n";
    }

    @Override
    public String toString() {
        StringBuilder storeInformation = new StringBuilder();
        storeInformation.append("\n<><><><><>   Store - ").append(Id).append("  <><><><><>\n");
        storeInformation.append("Store ID: ").append(Id).append("\n").append("Store Name: ").append(name).append("\n");
        storeInformation.append("Store PPK: ").append(deliveryPpk).append("\n");
        storeInformation.append("Total Orders Revenue: ").append(String.format("%.2f", totalOrdersRevenue)).append("\n\n");
        storeInformation.append("Store Item List: ").append(itemsBeingSold.size()).append(" items\n");
        storeInformation.append("\n-----  Store Items  -----\n\n");
        itemsBeingSold.values().forEach(item -> storeInformation.append(item.toString()));
        storeInformation.append("\n-----  Store Orders  -----\n\n");

        if(!storeOrdersHistory.isEmpty()) {
            storeOrdersHistory.forEach(order -> storeInformation.append((order.toString())));
        }
        else{
            storeInformation.append("\tThere are no orders that were made from this store.\n\n");
        }

        return storeInformation.toString();
    }
}
