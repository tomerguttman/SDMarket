package SDMImprovedFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Customer extends User {

    private double averageOrdersPriceWithoutDelivery;
    private double averageOrdersDeliveryPrice;
    private List<Order> customerOrders;

    public Customer(int id, String name, String userType) {
        super(name, userType, id);
        this.averageOrdersPriceWithoutDelivery = 0.0;
        this.averageOrdersDeliveryPrice = 0.0;
        customerOrders = new ArrayList<>();
    }

    public int getTotalNumberOfOrders() {
        return super.getUserOrdersMap().size();
    }

    public double getAverageOrdersPriceWithoutDelivery() {
        return averageOrdersPriceWithoutDelivery;
    }

    public double getAverageOrdersDeliveryPrice() {
        return averageOrdersDeliveryPrice;
    }

    public List<Order> getCustomerOrders() {
        return customerOrders;
    }

    public int getMostLovedItem() {
        Map<Integer,Double> itemsBucketMap = new HashMap<>();
        for (Order order : this.getUserOrdersMap().values()) {
            for (StoreItem sItem : order.getItemsInOrder()) {
                if(itemsBucketMap.containsKey(sItem.getId())) {
                    itemsBucketMap.put(sItem.getId(), itemsBucketMap.get(sItem.getId()) + sItem.getTotalItemsSold());
                }
                else { itemsBucketMap.put(sItem.getId(), sItem.getTotalItemsSold()); }
            }
        }
        int lovedItemId = -1;
        double maxAmount = -1;
        for (Integer itemId : itemsBucketMap.keySet()) {
            if(maxAmount < itemsBucketMap.get(itemId)) {
                maxAmount = itemsBucketMap.get(itemId);
                lovedItemId = itemId;
            }
        }

        return lovedItemId;
    }

    public List<Order> getCustomerOrdersOfSelectedZone(String zoneName) {
        return this.customerOrders.stream().filter(order -> order.zoneNameOfOrder.equals(zoneName)).collect(Collectors.toList()); // CHECK IF IT WORKS :")
    }
}
