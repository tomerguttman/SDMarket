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