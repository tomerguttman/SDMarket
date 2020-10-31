package SDMImprovedFacade;

import java.util.List;

public class CustomerJsonObject extends UserJsonObject {
    private final String userName;
    private double currentBalance;
    private int totalOrders;
    private double averageOrdersCost;
    private String mostLovedItem;

    public CustomerJsonObject(List<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers, double currentBalance,
                              int totalOrders, double averageOrdersCost, String mostLovedItem, String userName) {
        super(systemZones, userTransactions, otherUsers);
        this.currentBalance = currentBalance;
        this.totalOrders = totalOrders;
        this.averageOrdersCost = averageOrdersCost;
        this.mostLovedItem = mostLovedItem;
        this.userName = userName;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getUserName() {
        return userName;
    }

    public double getAverageOrdersCost() {
        return averageOrdersCost;
    }

    public void setAverageOrdersCost(double averageOrdersCost) {
        this.averageOrdersCost = averageOrdersCost;
    }

    public String getMostLovedItem() {
        return mostLovedItem;
    }

    public void setMostLovedItem(String mostLovedItem) {
        this.mostLovedItem = mostLovedItem;
    }
}
