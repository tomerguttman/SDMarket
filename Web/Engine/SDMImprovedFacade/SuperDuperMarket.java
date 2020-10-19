package SDMImprovedFacade;

import generatedClasses.*;
import javafx.beans.property.SimpleStringProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperDuperMarket {
    //ClassMembers
    private final Map<Integer, Store> systemStores;
    private final Map<Integer, StoreItem> systemItems;
    private Map<Integer, Order> systemDynamicOrders;
    private Map<String, Zone> systemZones;
    private int orderID = 1;
    private int userID = 1;

    public SuperDuperMarket() {
        systemStores = new HashMap<>();
        systemItems = new HashMap<>();
        systemDynamicOrders = new HashMap<>();
        systemZones = new HashMap<>();
    }

    private void updateStoreDiscountsOffersItemName(Map<Integer, Store> systemStores) {
        for (Store store : systemStores.values()) {
            Map<Integer, List<Discount>> currentStoreDiscounts = store.getStoreDiscounts();
            currentStoreDiscounts.forEach((itemToBuyID, listOfDiscountOffers) -> {
                for (Discount discount : listOfDiscountOffers) {
                    for (Discount.ThenGet.Offer offer : discount.getGetThat().getOfferList()) {
                        offer.setItemName(store.getItemsBeingSold().get(offer.getOfferItemId()).getName());
                    }
                }
            });
        }
    }

    private void updateStoreDiscountsItemToBuyName(Map<Integer, Store> systemStores) {
        for (Store store : systemStores.values()) {
            Map<Integer, List<Discount>> currentStoreDiscounts = store.getStoreDiscounts();
            currentStoreDiscounts.forEach((itemToBuyID, listOfDiscountOffers) -> {
                listOfDiscountOffers.forEach(discount -> discount.setItemToBuyName(store.getItemsBeingSold().get(itemToBuyID).getName()));
            });
        }
    }

    public void initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem() {
        double sum;
        int amountOfStoresSellingAnItem;

        for (StoreItem systemItem : systemItems.values()) {
            amountOfStoresSellingAnItem = 0;
            sum = 0;

            for (Store store : systemStores.values()) {
                if (store.getItemsBeingSold().containsKey(systemItem.getId())) {
                    amountOfStoresSellingAnItem += 1;
                    sum += store.getItemsBeingSold().get(systemItem.getId()).getPricePerUnit();
                }
            }

            if(amountOfStoresSellingAnItem != 0 ) {
                systemItem.setAveragePriceOfTheItem(sum / (double) amountOfStoresSellingAnItem);
            }

            systemItem.setAmountOfStoresSellingThisItem(amountOfStoresSellingAnItem);
        }
    }

    private void initializeStoresItems(SuperDuperMarketDescriptor inputSDM) {
        Map<Integer, SDMItem> SDMItemsMap = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, item -> item));
        Map<Integer, StoreItem> storeItemsForSaleImproved;

        for (SDMStore currentStore : inputSDM.getSDMStores().getSDMStore()) {
            storeItemsForSaleImproved = new HashMap<>();
            Store improvedStore = systemStores.get(currentStore.getId());

            for (SDMSell currentItem : currentStore.getSDMPrices().getSDMSell()) {
                storeItemsForSaleImproved.put(currentItem.getItemId(), new StoreItem(SDMItemsMap.get(currentItem.getItemId()), currentItem, true));
            }

            improvedStore.setItemBeingSold(storeItemsForSaleImproved);
        }
    }

    public Map<Integer, Store> getSystemStores() {
        return systemStores;
    }

    public Map<Integer, StoreItem> getSystemItems() {
        return systemItems;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getOrderID() {
        return orderID++;
    }

    public int getUserID() {
        return userID++;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Map<Integer, Order> getSystemDynamicOrders() {
        return systemDynamicOrders;
    }

    public void addDynamicOrder(Order dynamicOrder) {
        if (this.systemDynamicOrders == null) {
            this.systemDynamicOrders = new HashMap<>();
        }

        this.systemDynamicOrders.put(dynamicOrder.getOrderId(), dynamicOrder);
    }

    public Map<String, Zone> getSystemZones() {
        return systemZones;
    }

    public void addNewZoneToSystem(Zone zoneToAdd) {
        this.systemZones.put(zoneToAdd.getZoneName().replaceAll("\\s+",""), zoneToAdd);
        this.systemItems.putAll(zoneToAdd.getItemsAvailableInZone());
        this.systemStores.putAll(zoneToAdd.getStoresInZone());
        initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }
}

