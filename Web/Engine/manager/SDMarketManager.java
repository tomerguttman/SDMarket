package manager;

import SDMImprovedFacade.*;
import SuperMarketLogic.SuperMarketLogic;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import generatedClasses.Location;


import java.util.*;
import java.util.stream.Collectors;

public class SDMarketManager {
    public static final String CUSTOMER = "customer";
    private final HashMap<String, User> systemUsersMap = new HashMap<>();
    SuperMarketLogic SDMLogic = new SuperMarketLogic();
    private int userID = 1;

    public SuperMarketLogic getSDMLogic() {
        return SDMLogic;
    }

    public synchronized void addUser(String username, String userType) {
        if (!systemUsersMap.containsKey(username)) {
            if(userType.equals(CUSTOMER)) { systemUsersMap.put(username, new Customer(userID, username, userType)); }
            else { systemUsersMap.put(username, new ShopOwner(userID, username, userType)); }
            userID += 1;
        }
    }

    public synchronized User getUser(String username) {
        return systemUsersMap.get(username);
    }

    public synchronized void removeUser(String username) {
        systemUsersMap.remove(username);
    }

    public synchronized HashMap<String, User> getUsers() {
        return (HashMap<String, User>) Collections.unmodifiableMap(systemUsersMap);
    }

    public boolean isUserExists(String username) {
        return systemUsersMap.containsKey(username);
    }

    public Map<String, Zone> getSystemZones() {
        return this.SDMLogic.getSystemZones();
    }

    public List<User> getOtherUsers(String currentUserName) {
        return this.systemUsersMap.values().stream().filter(user -> (!user.getName().equals(currentUserName))).collect(Collectors.toList());
    }

    public Map<Integer, StoreItem> getSystemItems() {
        return this.SDMLogic.getItems();
    }

    public List<Zone> getSystemZonesAsList() {
        List<Zone> listOfZones = new ArrayList<>();
        for (Zone zone : SDMLogic.getSystemZones().values()) {
            listOfZones.add(zone);
        }

        return listOfZones;
    }

    public String getOrderHistoryJsonForCustomer(String username) {
        Customer currentCustomer = (Customer)systemUsersMap.get(username);
        List<Order> customerOrderHistory =  currentCustomer.getCustomerOrders();
        return new Gson().toJson(customerOrderHistory);
    }

    public String getOrderHistoryJsonForShopOwner(String username, String storeName) {
        ShopOwner currentShopOwner = (ShopOwner)systemUsersMap.get(username);
        List<Order> pickedStoreOrdersList =  currentShopOwner.getStoresOwned().get(storeName).getStoreOrdersHistory();
        return new Gson().toJson(pickedStoreOrdersList);
    }

    public void createNewStoreAndAddToZoneAndUser(ShopOwner currentShopOwner, String currentZoneName
            , String storeName, int ppk, int xCoordinate, int yCoordinate, Map<Integer, StoreItem> storeItems) {
        Location storeLocation = new Location();
        storeLocation.setX(xCoordinate);
        storeLocation.setY(yCoordinate);
        Zone currentZone = this.SDMLogic.getSystemZones().get(currentZoneName);
        Store newStoreToAdd = new Store(currentZone.getAmountOfStoresInZone() + 1, storeName, currentZoneName, ppk, storeLocation, storeItems, currentShopOwner.getName());
        this.SDMLogic.addStoreToSystem(newStoreToAdd);
        currentShopOwner.addStoreToUser(currentZoneName, newStoreToAdd);
        //newStore is already updated with the zone average price. (not the whole system)
        this.SDMLogic.addStoreToZoneInSystem(currentZoneName, newStoreToAdd);

    }

    public Map<Integer, StoreItem> createItemsBeingSoldFromJson(String storeItems, String currentZoneName) {
        Map<Integer, StoreItem> outputItemsBeingSoldMap = new HashMap<>();
        JsonElement itemsBeingSoldJsonElement = new JsonParser().parse(storeItems);
        for (JsonElement jsonItem : itemsBeingSoldJsonElement.getAsJsonArray()) {
            StoreItem sItem = createStoreItemFromJson(jsonItem, currentZoneName);
            outputItemsBeingSoldMap.put(sItem.getId(), sItem);
        }

        return outputItemsBeingSoldMap;
    }

    private StoreItem createStoreItemFromJson(JsonElement jsonItem, String currentZoneName) {
        StoreItem sItem;
        Zone currentZone = this.SDMLogic.getSystemZones().get(currentZoneName);
        int id = jsonItem.getAsJsonObject().get("id").getAsInt();
        int price = jsonItem.getAsJsonObject().get("price").getAsInt();
        StoreItem systemItem = currentZone.getItemsAvailableInZone().get(id);
        sItem = new StoreItem(id, systemItem.getName(), systemItem.getPurchaseCategory());
        sItem.setPricePerUnit(price);
        sItem.setIsAvailable(true);

        return sItem;
    }

    public List<String> getZonesNames() {
        List<String> zonesNames = new ArrayList<>();
        this.SDMLogic.getSystemZones().values().forEach(zone -> zonesNames.add(zone.getZoneName()));
        return zonesNames;
    }
}
