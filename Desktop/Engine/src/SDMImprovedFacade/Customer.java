package SDMImprovedFacade;

import generatedClasses.Location;
import generatedClasses.SDMCustomer;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final int id;
    private final String name;
    private final Location location;
    private int totalNumberOfOrders;
    private double averageOrdersPriceWithoutDelivery;
    private double averageOrdersDeliveryPrice;
    private List<Order> customerOrders;

    public Customer (SDMCustomer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.location = customer.getLocation();
        this.totalNumberOfOrders = 0;
        this.averageOrdersPriceWithoutDelivery = 0.0;
        this.averageOrdersDeliveryPrice = 0.0;
        customerOrders = new ArrayList<>();
    }

    public Customer(int id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.totalNumberOfOrders = 0;
        this.averageOrdersPriceWithoutDelivery = 0.0;
        this.averageOrdersDeliveryPrice = 0.0;
        customerOrders = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Location getLocation() {
        return location;
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

    @Override
    public String toString() {
        return String.format("%d | %s", this.id, this.name);
    }
}
