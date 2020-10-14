package SDMImprovedFacade;

import generatedClasses.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer extends User {

    private int totalNumberOfOrders;
    private double averageOrdersPriceWithoutDelivery;
    private double averageOrdersDeliveryPrice;
    private List<Order> customerOrders;

    private final HashMap<Integer, Order> userOrdersMap = new HashMap<>();

    public Customer(int id, String name, String userType) {
        super(name, userType, id);
        this.totalNumberOfOrders = 0;
        this.averageOrdersPriceWithoutDelivery = 0.0;
        this.averageOrdersDeliveryPrice = 0.0;
        customerOrders = new ArrayList<>();
    }

    public int getTotalNumberOfOrders() {
        return totalNumberOfOrders;
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

    public void addOrder(Order orderToAdd) {
        if(this.customerOrders == null) {
            customerOrders = new ArrayList<>();
        }

        customerOrders.add(orderToAdd);
        double sumOfOrdersPriceWithoutDelivery = this.averageOrdersPriceWithoutDelivery * this.totalNumberOfOrders + orderToAdd.costOfItemsInOrder;
        double sumOfOrdersDeliveryPrice = this.averageOrdersDeliveryPrice * this.totalNumberOfOrders + orderToAdd.deliveryCost;
        this.totalNumberOfOrders = customerOrders.size(); //do not move!
        this.averageOrdersPriceWithoutDelivery = sumOfOrdersPriceWithoutDelivery / totalNumberOfOrders;
        this.averageOrdersDeliveryPrice = sumOfOrdersDeliveryPrice / totalNumberOfOrders;
    }

    public int getMostLovedItem() {
        Map<Integer,Double> itemsBucketMap = new HashMap<>();
        for (Order order : this.userOrdersMap.values()) {
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
}
