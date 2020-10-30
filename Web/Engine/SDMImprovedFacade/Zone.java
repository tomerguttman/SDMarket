package SDMImprovedFacade;

import generatedClasses.SDMItem;
import generatedClasses.SDMSell;
import generatedClasses.SDMStore;
import generatedClasses.SuperDuperMarketDescriptor;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Zone {
    private String zoneName;
    private String ownerName;
    private int amountOfItemTypesInZone;
    private int amountOfStoresInZone;
    private int amountOfOrdersInZone;
    private double averageOrdersCostWithoutDelivery;
    private double totalRevenue;
    private final HashMap<Integer, Store> storesInZone = new HashMap<>();
    private final HashMap<Integer, Order> ordersMadeInZone = new HashMap<>();
    private final HashMap<Integer, StoreItem> itemsAvailableInZone = new HashMap<>();

    public Zone(String zoneName, String ownerName) {
        this.zoneName = zoneName;
        this.ownerName = ownerName;
        this.totalRevenue = 0;
    }

    public Zone(SuperDuperMarketDescriptor inputSDM, String ownerName) {
        this.zoneName = inputSDM.getSDMZone().getName();
        this.ownerName = ownerName;
        this.storesInZone.putAll(createStoresInZoneMap(inputSDM, ownerName, zoneName));
        updateItemsBeingSoldForEachStore(inputSDM);
        this.itemsAvailableInZone.putAll(createItemsInZoneMap(inputSDM));
        initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
        this.amountOfStoresInZone = storesInZone.size();
        this.amountOfItemTypesInZone = itemsAvailableInZone.size();
        this.amountOfOrdersInZone = 0;
        this.averageOrdersCostWithoutDelivery = 0;
        this.totalRevenue = 0;
    }

    private Map<Integer, StoreItem> createItemsInZoneMap(SuperDuperMarketDescriptor inputSDM) {
        Map<Integer, SDMItem> SDMItemsMap = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, item -> item));
        Map<Integer, StoreItem> itemsInZoneMap = new HashMap<>();

        for (SDMItem item : SDMItemsMap.values()) {
            itemsInZoneMap.put(item.getId(), new StoreItem(item));
        }

        return itemsInZoneMap;
    }

    public void initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem() {
        double sum;
        int amountOfStoresSellingAnItem;

        for (StoreItem systemItem : itemsAvailableInZone.values()) {
            amountOfStoresSellingAnItem = 0;
            sum = 0;

            for (Store store : storesInZone.values()) {
                if (store.getItemsBeingSold().containsKey(systemItem.getId())) {
                    amountOfStoresSellingAnItem += 1;
                    sum += store.getItemsBeingSold().get(systemItem.getId()).getPricePerUnit();
                }

            }

            if(amountOfStoresSellingAnItem != 0) {
                systemItem.setAveragePriceOfTheItem(sum / (double) amountOfStoresSellingAnItem);
            }

            systemItem.setAmountOfStoresSellingThisItem(amountOfStoresSellingAnItem);

        }
    }

    private void updateItemsBeingSoldForEachStore(SuperDuperMarketDescriptor inputSDM) {
        Map<Integer, SDMItem> SDMItemsMap = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, item -> item));
        Map<Integer, StoreItem> storeItemsForSaleImproved;

        for (SDMStore currentStore : inputSDM.getSDMStores().getSDMStore()) {
            storeItemsForSaleImproved = new HashMap<>();
            Store improvedStore = storesInZone.get(currentStore.getId());

            for (SDMSell currentItem : currentStore.getSDMPrices().getSDMSell()) {
                storeItemsForSaleImproved.put(currentItem.getItemId(), new StoreItem(SDMItemsMap.get(currentItem.getItemId()), currentItem, true));
            }

            improvedStore.setItemBeingSold(storeItemsForSaleImproved);
        }
    }

    private Map<Integer, Store> createStoresInZoneMap(SuperDuperMarketDescriptor inputSDM, String ownerName, String zoneName) {
        Map<Integer, SDMStore> SDMStoreMap = inputSDM.getSDMStores().getSDMStore().stream().collect(Collectors.toMap(SDMStore::getId, store -> store));
        Map<Integer, Store> storesInZoneMap = new HashMap<>();

        for (SDMStore store : SDMStoreMap.values()) {
            storesInZoneMap.put(store.getId(), new Store(store, ownerName, zoneName));
        }

        return storesInZoneMap;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getAmountOfItemTypesInZone() {
        return amountOfItemTypesInZone;
    }

    public void setAmountOfItemTypesInZone(int amountOfItemTypesInZone) {
        this.amountOfItemTypesInZone = amountOfItemTypesInZone;
    }

    public int getAmountOfStoresInZone() {
        return amountOfStoresInZone;
    }

    public void setAmountOfStoresInZone(int amountOfStoresInZone) {
        this.amountOfStoresInZone = amountOfStoresInZone;
    }

    public int getAmountOfOrdersInZone() {
        return amountOfOrdersInZone;
    }

    public void setAmountOfOrdersInZone(int amountOfOrdersInZone) {
        this.amountOfOrdersInZone = amountOfOrdersInZone;
    }

    public double getAverageOrdersCostWithoutDelivery() {
        return averageOrdersCostWithoutDelivery;
    }

    public void setAverageOrdersCostWithoutDelivery(double averageOrdersCostWithoutDelivery) {
        this.averageOrdersCostWithoutDelivery = averageOrdersCostWithoutDelivery;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public HashMap<Integer, StoreItem> getItemsAvailableInZone() {
        return itemsAvailableInZone;
    }

    public HashMap<Integer, Store> getStoresInZone() {
        return storesInZone;
    }

    public HashMap<Integer, Order> getOrdersMadeInZone() {
        return ordersMadeInZone;
    }

    public void addStoreToZone(Store storeToAdd) {
        this.storesInZone.put(storeToAdd.getId(), storeToAdd);
        this.amountOfStoresInZone += 1;
    }

    public void addOrderToZone(Order order) {
        this.ordersMadeInZone.put(order.orderId, order);
        amountOfOrdersInZone += 1;
        totalRevenue += order.totalOrderCost;
        this.averageOrdersCostWithoutDelivery = calculateAverageCostOfOrdersInZone();

        for(StoreItem sItem : order.getItemsInOrder()){
            StoreItem itemInZone = this.itemsAvailableInZone.get(sItem.getId());
            itemInZone.setTotalItemsSold(itemInZone.getTotalItemsSold() + sItem.getTotalItemsSold());
        }
    }

    private double calculateAverageCostOfOrdersInZone() {
        double sum = 0;
        for (Order order : this.ordersMadeInZone.values()) {
            sum += order.totalOrderCost;
        }

       return this.ordersMadeInZone.values().size() != 0 ? sum / this.ordersMadeInZone.values().size() : 0 ;
    }

    public List<StoreItem> getItemsAvailableInZoneAsList() {
        List<StoreItem> itemsAvailableInZoneList = new ArrayList<>(this.itemsAvailableInZone.values());
        return itemsAvailableInZoneList;
    }
}
