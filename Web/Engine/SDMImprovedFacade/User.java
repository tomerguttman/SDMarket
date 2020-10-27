package SDMImprovedFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private int userId;
    private String name;
    private String userType;
    private double balance;
    private final HashMap<Integer, Order> userOrdersMap = new HashMap<>();
    private final List<Transaction> userTransactionsList = new ArrayList<>();

    public User(String username, String userType, int userId) {
        this.name = username;
        this.userType = userType;
        this.userId = userId;
        this.balance = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getUserTransactionsList() {
        return userTransactionsList;
    }

    public HashMap<Integer, Order> getUserOrdersMap() {
        return userOrdersMap;
    }

    synchronized public void addTransaction(String transactionType, String transactionAmountString, String dateOfRecharge) {
        double transactionAmount = Double.parseDouble(transactionAmountString);
        Transaction transaction = new Transaction(transactionType, dateOfRecharge, transactionAmount,this.balance);
        userTransactionsList.add(transaction);
        this.balance = this.balance + transactionAmount;
    }

    @Override
    public String toString() {
        return String.format("%d | %s", this.userId, this.name);
    }

    public double getAverageOrderCost() {
        double sumOrders = 0;
        for (Order order : this.getUserOrdersMap().values()) {
            sumOrders += order.getTotalOrderCost();
        }

        return (userOrdersMap.size() != 0) ? (sumOrders / userOrdersMap.size()) : 0;
    }

    public void addOrder(Order order) {
        this.userOrdersMap.put(order.orderId, order);
    }

    public void updateBalance(double amountToAdd) {
        this.balance += amountToAdd;
    }

    public HashMap<Integer, Order> getOrderHistoryByZoneName(String currentZoneName) {
        HashMap<Integer, Order> outOrderHistoryForZoneMap = new HashMap<>();

        for (Order order : userOrdersMap.values()) {
            if(order.getZoneNameOfOrder().equals(currentZoneName)) { outOrderHistoryForZoneMap.put(order.orderId, order); }
        }

        return outOrderHistoryForZoneMap;
    }
}
