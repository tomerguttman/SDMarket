package SDMImprovedFacade;

import generatedClasses.*;
import javafx.beans.binding.Bindings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private final int Id;
    private int deliveryPpk;
    private double totalOrdersRevenue = 0;
    private String name;
    private Location storeLocation;
    private Map<Integer, StoreItem> itemsBeingSold;
    private final Map<Integer, List<Discount>> storeDiscounts; // Integer -> IfYouBuyItemId, List<Discount> -> Discount associated with the item.
    private final List<Order> storeOrdersHistory;


    public Store(SDMStore inputStore){
        this.Id = inputStore.getId();
        this.deliveryPpk = inputStore.getDeliveryPpk();
        this.name = inputStore.getName();
        this.storeLocation = inputStore.getLocation();
        storeOrdersHistory = new ArrayList<>();
        this.storeDiscounts = generateNewDiscountsMap(inputStore);
    }

    private Map<Integer, List<Discount>> generateNewDiscountsMap(SDMStore inputStore) {
        Map<Integer, List<Discount>> discountsMap = new HashMap<>();
        Discount newDiscountToAdd;
        for (SDMSell sdmSell : inputStore.getSDMPrices().getSDMSell()) {
            if(inputStore.getSDMDiscounts() != null){
                for (SDMDiscount discount : inputStore.getSDMDiscounts().getSDMDiscount()) {
                    if(discount.getIfYouBuy().getItemId() == sdmSell.getItemId()) {
                        newDiscountToAdd= new Discount(discount);
                        newDiscountToAdd.setStoreIdOfDiscount(inputStore.getId());
                        if(discountsMap.containsKey(sdmSell.getItemId())) {
                            discountsMap.get(sdmSell.getItemId()).add(newDiscountToAdd);
                        }
                        else {
                            ArrayList<Discount> discountsList = new ArrayList<>();
                            discountsList.add(newDiscountToAdd);
                            discountsMap.put(sdmSell.getItemId(), discountsList);
                        }
                    }
                }
            }
        }

        return discountsMap;
    }

    public Map<Integer, List<Discount>> getStoreDiscounts() {
        return storeDiscounts;
    }

    public void setTotalOrdersRevenue(double totalOrdersRevenue) {
        this.totalOrdersRevenue = totalOrdersRevenue;
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

    public void generateOrder(String orderDate, int orderId, List<StoreItem> itemsInOrder, Location userCoordinates){
        Order order = new Order(orderDate, orderId, this.Id, calculateDistance(userCoordinates) * deliveryPpk, this.name, itemsInOrder, userCoordinates);
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

    public String getStringStoreItemsShort(){
        StringBuilder storeItemsStringBuilder = new StringBuilder();
        storeItemsStringBuilder.append("-----\tStore items available\t-----\n");
        itemsBeingSold.values().forEach(item -> storeItemsStringBuilder.append(item.getStringItemForPurchase()));
        return storeItemsStringBuilder.toString();

    }

    @Override
    public String toString() {
        return String.format("%d | %s", this.Id, this.name);
    }

    public int getTotalAmountOfDiscounts() {
        int sumOfDiscounts = 0;

        for (Integer itemId: storeDiscounts.keySet()) {
            sumOfDiscounts += storeDiscounts.get(itemId).size();
        }

        return sumOfDiscounts;
    }
}
