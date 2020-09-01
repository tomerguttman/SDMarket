package SDMImprovedFacade;

import generatedClasses.SDMItem;
import generatedClasses.SDMSell;
import generatedClasses.SDMStore;
import generatedClasses.SuperDuperMarketDescriptor;
import javafx.beans.property.SimpleStringProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperDuperMarket {
    private final Map<Integer, Store> systemStores;
    private final Map<Integer, StoreItem> systemItems;
    private Map<Integer, Order> systemDynamicOrders;
    //private Map<Integer, Customer> systemCustomers;

    private SimpleStringProperty amountCustomersProperty;
    private SimpleStringProperty amountOrdersProperty;
    private SimpleStringProperty amountItemsProperty;
    private SimpleStringProperty amountStoresProperty;

    private int orderID = 1;

    public SuperDuperMarket(SuperDuperMarketDescriptor inputSDM){
        systemStores = inputSDM.getSDMStores().getSDMStore().stream().collect(Collectors.toMap(SDMStore::getId, Store::new));
        systemItems = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, StoreItem::new));
        systemDynamicOrders = new HashMap<>();
        initializeStoresItems(inputSDM);
        initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();

        amountStoresProperty = new SimpleStringProperty(Integer.toString(this.getSystemStores().values().size()));
        amountItemsProperty = new SimpleStringProperty(Integer.toString(this.getSystemItems().values().size()));
        amountOrdersProperty = new SimpleStringProperty(Integer.toString(orderID - 1));
        //amountCustomersProperty = new SimpleStringProperty(Integer.toString(this.getSystemStores().values().size()));


    }

    public void initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem() {
        Collection<Store> storesInSystem = systemStores.values();
        double sum;
        int amountOfStoresSellingAnItem;

        for (StoreItem systemItem : systemItems.values() ) {
            amountOfStoresSellingAnItem = 0;
            sum = 0;

            for (Store store: systemStores.values()) {
                if(store.getItemsBeingSold().containsKey(systemItem.getId())) {
                    amountOfStoresSellingAnItem += 1;
                    sum += store.getItemsBeingSold().get(systemItem.getId()).getPricePerUnit();
                }

            }

            systemItem.setAveragePriceOfTheItem(sum / (double)amountOfStoresSellingAnItem);
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
                storeItemsForSaleImproved.put(currentItem.getItemId(),new StoreItem(SDMItemsMap.get(currentItem.getItemId()), currentItem));
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
    /*
        NEED TO BE CHECKED ! ! ! !
     */
    public int getOrderID() {
        return orderID++;
    }

    public Map<Integer, Order> getSystemDynamicOrders() {
        return systemDynamicOrders;
    }

    public void addDynamicOrder(Order dynamicOrder) {
        if (this.systemDynamicOrders == null){
            this.systemDynamicOrders = new HashMap<>();
        }

        this.systemDynamicOrders.put(dynamicOrder.getOrderId(), dynamicOrder);
    }

    public SimpleStringProperty getAmountStoresProperty() {
        return amountStoresProperty;
    }

    public SimpleStringProperty getAmountCustomersProperty() {
        return amountCustomersProperty;
    }

    public SimpleStringProperty getAmountOrdersProperty() {
        return amountOrdersProperty;
    }

    public SimpleStringProperty getAmountItemsProperty() {
        return amountItemsProperty;
    }
}
