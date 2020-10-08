package SDMImprovedFacade;

import generatedClasses.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
}
