package SDMImprovedFacade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Order {
    final int orderId;
    final int storeId;
    final double deliveryCost;
    final double costOfItemsInOrder;
    final double totalOrderCost;
    final String dateOrderWasMade;
    final String storeName;
    final List<StoreItem> itemsInOrder;

    public Order(String dateOrderWasMade, int orderId, int storeId, double deliveryCost, String storeName, List<StoreItem> itemsInOrder) {
        this.dateOrderWasMade = dateOrderWasMade;
        this.orderId = orderId;
        this.storeId = storeId;
        this.deliveryCost = deliveryCost;
        this.storeName = storeName;
        this.itemsInOrder = itemsInOrder;
        costOfItemsInOrder = calculateTotalCostOfItemsInOrder();
        totalOrderCost = deliveryCost + costOfItemsInOrder;
    }

    public int getNumberOfItemsInOrder(){
        Set<Integer> itemsInOrderSet = new HashSet<>();
        itemsInOrder.forEach(item -> itemsInOrderSet.add(item.getId()));

        return itemsInOrderSet.size();
    }

    private double calculateTotalCostOfItemsInOrder(){
        return itemsInOrder.stream().
                mapToDouble(StoreItem::getPricePerUnit).
                sum();
    }

    public int getOrderId() {
        return orderId;
    }

    public int getStoreId() {
        return storeId;
    }

    public double getDeliveryCost() {
        return deliveryCost;
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

    private int getTotalNumberOfItemsInOrder(){
        return itemsInOrder.stream().
                mapToInt((item) -> {
                    if(item.getPurchaseCategory().equals("Weight")){
                        return 1;
                    }
                    else {
                        return item.getTotalItemsSold();
                    }
                }).
                sum();
    }

    @Override
    public String toString() {
        return  "\tOrder ID: " + orderId + "\n" +
                "\t\tDate Of Order: " + dateOrderWasMade + "\n" +
                "\t\tStore ID: " + storeId + "(" + storeName + ")\n" +
                "\t\tAmount Of Item Types: " + getNumberOfItemsInOrder() + "\n" +
                "\t\tTotal Number Of Items In Order: " + getTotalNumberOfItemsInOrder() + "\n" +
                "\t\tTotal Cost Of Items In Order: " + totalOrderCost + "\n" +
                "\t\tDelivery Cost: " + deliveryCost + "\n" +
                "\t\tTotal Cost Of Order: " + totalOrderCost + "\n";
    }
}
