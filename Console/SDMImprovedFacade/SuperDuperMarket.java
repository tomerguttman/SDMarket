package SDMImprovedFacade;

import jaxb.generatedClasses.SDMItem;
import jaxb.generatedClasses.SDMSell;
import jaxb.generatedClasses.SDMStore;
import jaxb.generatedClasses.SuperDuperMarketDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperDuperMarket {
    private final Map<Integer, Store> systemStores;
    private final Map<Integer, StoreItem> systemItems;
    private int orderID = 0;

    public SuperDuperMarket(SuperDuperMarketDescriptor inputSDM){
        systemStores = inputSDM.getSDMStores().getSDMStore().stream().collect(Collectors.toMap(SDMStore::getId, Store::new));
        systemItems = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, StoreItem::new));
        initializeStoresItems(inputSDM);
    }

    private void initializeStoresItems(SuperDuperMarketDescriptor inputSDM) {
        Map<Integer, SDMItem> SDMItemsMap = inputSDM.getSDMItems().getSDMItem().stream().collect(Collectors.toMap(SDMItem::getId, item -> item));
        Map<Integer, StoreItem> betterStoreItemsForSale;

        for (SDMStore currentStore : inputSDM.getSDMStores().getSDMStore()) {
            betterStoreItemsForSale = new HashMap<>();
            Store betterStore = systemStores.get(currentStore.getId());

            for (SDMSell currentItem : currentStore.getSDMPrices().getSDMSell()) {
                betterStoreItemsForSale.put(currentItem.getItemId(),new StoreItem(SDMItemsMap.get(currentItem.getItemId()), currentItem));
            }

            betterStore.setItemBeingSold(betterStoreItemsForSale);
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
        return orderID;
    }
}
