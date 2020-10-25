package manager;

import SDMImprovedFacade.*;
import SuperMarketLogic.SuperMarketLogic;
import com.google.gson.*;
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

    public JsonObject getOrderHistoryJsonForCustomer(String username, String zoneName) {
        JsonObject jsonObject = new JsonObject();

        Customer currentCustomer = (Customer)systemUsersMap.get(username);
        List<Order> customerZoneOrderHistory =  currentCustomer.getCustomerOrdersOfSelectedZone(zoneName);
        jsonObject.add("ordersHistory" , new Gson().toJsonTree(customerZoneOrderHistory));

        return jsonObject;
    }

    public JsonObject getOrderHistoryJsonForShopOwner(String username, String storeName) {
        JsonObject jsonObject = new JsonObject();
        ShopOwner currentShopOwner = (ShopOwner)systemUsersMap.get(username);
        List<Order> pickedStoreOrdersList =  currentShopOwner.getStoresOwned().get(storeName).getStoreOrdersHistory();
        jsonObject.add("ordersHistory" , new Gson().toJsonTree(pickedStoreOrdersList));
        return jsonObject;
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

    public Store getSelectedStoreByName(String currentZoneName, String pickedStoreName) {
        HashMap<Integer, Store> storesInZone = this.SDMLogic.getSystemZones().get(currentZoneName).getStoresInZone();
        for (Store store : storesInZone.values()) {
            if(store.getName().equals(pickedStoreName)) { return store; }
        }

        return null;
    }

    public void addNewOrder(String purchaseMethod, String orderString, String currentUserName, String currentZoneName) {
        if (purchaseMethod.equals("dynamic")) { /*addDynamicOrder(orderString, currentUserName, currentZoneName);*/ }
        else { addStaticOrder(orderString, currentUserName, currentZoneName); }


    }

    private void addStaticOrder(String orderString, String currentUserName, String currentZoneName) {
        JsonObject jObject = new JsonParser().parse(orderString).getAsJsonObject();
        int storeIdToOrderFrom = jObject.get("storeId").getAsInt();
        double deliveryCost = jObject.get("deliveryCost").getAsDouble();
        double costOfItemsInOrder = jObject.get("costOfItemsInOrder").getAsDouble();
        double totalOrderCost =  jObject.get("totalOrderCost").getAsDouble();
        String dateOrderWasMade = jObject.get("dateOrderWasMade").getAsString();
        String storeName = jObject.get("storeName").getAsString();
        Location orderDestination = new Location();
        orderDestination.setX(jObject.get("orderDestination").getAsJsonObject().get("xCoordinate").getAsInt());
        orderDestination.setY(jObject.get("orderDestination").getAsJsonObject().get("yCoordinate").getAsInt());
        int amountOfStoresRelatedToOrder = 1;
        List<StoreItem> itemsInOrder = createItemsInOrderList(jObject.get("itemsInOrder").getAsJsonArray(), currentZoneName);
        Order order = new Order(dateOrderWasMade, getSDMLogic().getLastOrderID(), storeIdToOrderFrom, deliveryCost, currentUserName, storeName, itemsInOrder,orderDestination, currentZoneName);
        addOrderToCustomer(order, currentUserName);
        addOrderToStore(order);
        this.getSystemZones().get(currentZoneName).addOrderToZone(order);
        addTransactionsToShopOwnerAndCustomer(order, currentUserName, currentZoneName);
        //add alerts
    }

    private void addTransactionsToShopOwnerAndCustomer(Order order, String currentUserName, String currentZoneName) {
        String shopOwnerName = this.getSystemZones().get(currentZoneName).getStoresInZone().get(order.getStoreId()).getOwnerName();
        addTransactionToShopOwner(shopOwnerName, order);
        addTransactionToCustomer(currentUserName, order);
    }

    private void addTransactionToCustomer(String currentUserName, Order order) {
        Customer customer = (Customer)getUser(currentUserName);
        customer.addTransaction("Payment Made", Double.toString(order.getTotalOrderCost()*(-1)), order.getDateOrderWasMade());
    }

    private void addTransactionToShopOwner(String shopOwnerName, Order order) {
        ShopOwner shopOwner = (ShopOwner)getUser(shopOwnerName);
        shopOwner.addTransaction("Payment Received", Double.toString(order.getTotalOrderCost()), order.getDateOrderWasMade());
    }

    private void addOrderToStore(Order order) {
        Store storeToBuyFrom = this.getSystemZones().get(order.getZoneNameOfOrder()).getStoresInZone().get(order.getStoreId());
        storeToBuyFrom.addOrderToStore(order);
        getUser(storeToBuyFrom.getOwnerName()).addOrder(order);
    }

    private void addOrderToCustomer(Order order, String currentUserName) {
        this.systemUsersMap.get(currentUserName).addOrder(order);

    }

    private List<StoreItem> createItemsInOrderList(JsonArray itemsInOrderJsonArray, String currentZoneName) {
        List<StoreItem> outputItemsInOrderList = new ArrayList<>();

        for (JsonElement jsonItem : itemsInOrderJsonArray) {
            StoreItem sItem = createItemFromOrderList(jsonItem.getAsJsonObject(), currentZoneName);
            outputItemsInOrderList.add(sItem);
        }

        return outputItemsInOrderList;
    }

    private StoreItem createItemFromOrderList(JsonObject jsonItem, String currentZoneName) {
        int itemId = jsonItem.get("Id").getAsInt();
        String itemName = jsonItem.get("name").getAsString();
        String purchaseCategory = jsonItem.get("purchaseCategory").getAsString();
        double amount = jsonItem.get("amount").getAsFloat();
        int pricePerUnit = jsonItem.get("pricePerUnit").getAsInt();
        boolean wasPartOfDiscount = jsonItem.get("wasPartOfDiscount").getAsString().equals("Yes");

        return new StoreItem(itemId, amount, pricePerUnit, itemName, purchaseCategory, wasPartOfDiscount);
    }
}
