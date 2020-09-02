package SDMImprovedFacade;

import generatedClasses.Location;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Order {
    final int orderId;
    final int storeId;
    final double deliveryCost;
    final double costOfItemsInOrder;
    final double totalOrderCost;
    final String dateOrderWasMade;
    final String storeName;
    final Location orderDestination;
    private int amountOfStoresRelatedToOrder;
    private final int amountItemsInOrder ;
    final List<StoreItem> itemsInOrder;

    public Order(String dateOrderWasMade, int orderId, int storeId, double deliveryCost,
                 String storeName, List<StoreItem> itemsInOrder, Location userLocation) {
        this.dateOrderWasMade = dateOrderWasMade;
        this.orderId = orderId;
        this.storeId = storeId;
        this.deliveryCost = deliveryCost;
        this.storeName = storeName;
        this.itemsInOrder = itemsInOrder;
        this.costOfItemsInOrder = calculateTotalCostOfItemsInOrder();
        this.totalOrderCost = deliveryCost + costOfItemsInOrder;
        this.orderDestination = userLocation;
        this.amountOfStoresRelatedToOrder = 1;
        this.amountItemsInOrder = itemsInOrder.size();
    }

    public Order(String dateOrderWasMade, Location userLocation, int orderId,
                 Double deliveryCost, int amountOfStoresParticipating, List<StoreItem> itemsInOrder) {
        this.dateOrderWasMade = dateOrderWasMade;
        this.orderId = orderId;
        this.storeId = -1;
        this.deliveryCost = deliveryCost;
        this.storeName = String.format("Dynamic Order %d",orderId);
        this.itemsInOrder = itemsInOrder;
        this.costOfItemsInOrder = calculateTotalCostOfItemsInOrder();
        this.totalOrderCost = deliveryCost + costOfItemsInOrder;
        this.orderDestination = userLocation;
        this.amountOfStoresRelatedToOrder = amountOfStoresParticipating;
        this.amountItemsInOrder = itemsInOrder.size();
    }

    public int getNumberOfItemsInOrder(){
        Set<Integer> itemsInOrderSet = new HashSet<>();
        itemsInOrder.forEach(item -> itemsInOrderSet.add(item.getId()));

        return itemsInOrderSet.size();
    }

    private double calculateTotalCostOfItemsInOrder(){
        return itemsInOrder.stream().mapToDouble(itemInOrder -> itemInOrder.getTotalItemsSold() * itemInOrder.getPricePerUnit()).sum();
    }

    public int getOrderId() {
        return orderId;
    }

    public int getStoreId() {
        return storeId;
    }

    public double getCostOfItemsInOrder() {
        return costOfItemsInOrder;
    }

    public double getTotalOrderCost() { return totalOrderCost; }
    
    public double getDeliveryCost() {
        return deliveryCost;
    }

    public Location getOrderDestination() {
        return orderDestination;
    }

    public String getDateOrderWasMade() {
        return dateOrderWasMade;
    }

    public String getStoreName() {
        return storeName;
    }

    public List<StoreItem> getItemsInOrder() {
        return itemsInOrder;
    }

    public int getAmountOfStoresRelated() {
        return amountOfStoresRelatedToOrder;
    }

    public void setAmountOfStoresRelated(int amountOfStoresRelated) {
        this.amountOfStoresRelatedToOrder = amountOfStoresRelated;
    }

    private int getTotalNumberOfItemsInOrder(){
        return itemsInOrder.stream().
                mapToInt((item) -> {
                    if(item.getPurchaseCategory().equals("Weight")){
                        return 1;
                    }
                    else {
                        return (int)item.getTotalItemsSold();//What returns here is a Quantity item.
                    }
                }).
                sum();
    }

    public String getStringWholeOrder(){
        StringBuilder stbOrder = new StringBuilder();
        this.itemsInOrder.forEach(item -> {
            stbOrder.append(item.getStringItemForPurchase());
            stbOrder.append("\t\tAmount Bought: ").append(item.getTotalItemsSold()).append("\n");
            stbOrder.append("\t\tTotal Price: ").append(item.getTotalItemsSold() * item.getPricePerUnit()).append("\n");
        });

        return stbOrder.toString();
    }

    @Override
    public String toString() {
        return  "\tOrder ID: " + orderId + "\n" +
                "\t\tDate Of Order: " + dateOrderWasMade + "\n" +
                "\t\tStore ID: " + storeId + "(" + storeName + ")\n" +
                "\t\tAmount Of Item Types: " + getNumberOfItemsInOrder() + "\n" +
                "\t\tTotal Number Of Items In Order: " + getTotalNumberOfItemsInOrder() + "\n" +
                "\t\tTotal Cost Of Items In Order: " + String.format("%.2f", costOfItemsInOrder) + "\n" +
                "\t\tDelivery Cost: " + String.format("%.2f", deliveryCost) + "\n" +
                "\t\tTotal Cost Of Order: " + String.format("%.2f",totalOrderCost) + "\n";
    }

    public String toStringDynamicOrder() {
        return  "\tOrder ID: " + orderId + "\n" +
                "\t\tDate Of Order: " + dateOrderWasMade + "\n" +
                "\t\tAmount Of Stores Participating In Order: " + amountOfStoresRelatedToOrder + "\n" +
                "\t\tAmount Of Item Types: " + getNumberOfItemsInOrder() + "\n" +
                "\t\tTotal Number Of Items In Order: " + getTotalNumberOfItemsInOrder() + "\n" +
                "\t\tTotal Cost Of Items In Order: " + String.format("%.2f", costOfItemsInOrder) + "\n" +
                "\t\tDelivery Cost: " + String.format("%.2f", deliveryCost) + "\n" +
                "\t\tTotal Cost Of Order: " + String.format("%.2f",totalOrderCost) + "\n";
    }
}
