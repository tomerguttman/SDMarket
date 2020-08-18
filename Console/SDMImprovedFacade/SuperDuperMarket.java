package SDMImprovedFacade;

import jaxb.generatedClasses.SDMItem;
import jaxb.generatedClasses.SDMSell;
import jaxb.generatedClasses.SDMStore;
import jaxb.generatedClasses.SuperDuperMarketDescriptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperDuperMarket {
    private final Map<Integer, Store> systemStores;
    private final Map<Integer, StoreItem> systemItems;
    private Map<Integer, Order> systemDynamicOrders = null;

    private int orderID = 1;

    public SuperDuperMarket(SuperDuperMarketDescriptor inputSDM){
        systemStores = inputSDM.getSDMStores().getSDMStore().stream().collect(Collectors.toMap(SDMStore::getId, Store::new));
        systemItems = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, StoreItem::new));
        initializeStoresItems(inputSDM);
        initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();

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
}
