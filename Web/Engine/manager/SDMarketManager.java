package manager;

import SDMImprovedFacade.*;
import SuperMarketLogic.SuperMarketLogic;
import com.google.gson.*;
import generatedClasses.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        addNotificationToZoneOwner(currentZone, newStoreToAdd, currentShopOwner);
    }

    private void addNotificationToZoneOwner(Zone currentZone, Store newStoreToAdd, ShopOwner currentShopOwner) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        dateTimeFormatter.format(now);
        String subject = String.format("A new store named '%s' was created in %s. The store is located at (%d,%d) and sells %d items, out of %d available in the zone."
                ,newStoreToAdd.getName(), currentZone.getZoneName(), newStoreToAdd.getStoreLocation().getX(),newStoreToAdd.getStoreLocation().getY(),
                newStoreToAdd.getItemsBeingSold().size(), currentZone.getAmountOfItemTypesInZone());
        StoreCreatedNotification notification = new StoreCreatedNotification(currentShopOwner.getName(), currentZone.getOwnerName(),
                subject, newStoreToAdd.getName(), newStoreToAdd.getStoreLocation(), dateTimeFormatter.format(now));
        notification.setTotalItemsSellingOutOfTotalItemsAvailableString(newStoreToAdd.getItemsBeingSold().size(), currentZone.getAmountOfItemTypesInZone());
        ShopOwner zoneOwner = (ShopOwner) getUser(currentZone.getOwnerName());
        zoneOwner.getRelevantNotifications().add(notification);
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
        if (purchaseMethod.equals("dynamic")) { addDynamicOrder(orderString, currentUserName, currentZoneName); }
        else { addStaticOrder(orderString, currentUserName, currentZoneName); }
    }

    private void addDynamicOrder(String orderString, String currentUserName, String currentZoneName) {
        JsonObject jsonDynamicOrder = new JsonParser().parse(orderString).getAsJsonObject();
        double totalDeliveryCost = jsonDynamicOrder.get("totalDeliveryCost").getAsDouble();
        double orderCost = jsonDynamicOrder.get("orderCost").getAsDouble();
        Location location = new Location();
        String dateOfOrder = jsonDynamicOrder.get("date").getAsString();
        location.setX(jsonDynamicOrder.get("location").getAsJsonObject().get("xCoordinate").getAsInt());
        location.setY(jsonDynamicOrder.get("location").getAsJsonObject().get("yCoordinate").getAsInt());
        Map<String, List<StoreItem>> storeToItemListMapOfOrder = createStoreToItemListMapOfOrder(jsonDynamicOrder.get("storesParticipatingWithRelevantItems").getAsJsonObject());
        List<Order> subOrdersList = this.getSDMLogic().generateNewDynamicOrderSubOrders(storeToItemListMapOfOrder, totalDeliveryCost, orderCost, location, dateOfOrder, currentUserName, currentZoneName, this.getSystemZones().get(currentZoneName).getStoresInZone());

        Order dynamicOrder = createDynamicOrderFromSubOrdersList(subOrdersList, currentUserName, currentZoneName, location, dateOfOrder);

        this.getSystemZones().get(currentZoneName).addOrderToZone(dynamicOrder);
        addOrderToCustomer(dynamicOrder, currentUserName);
        addTransactionToCustomer(currentUserName, dynamicOrder);
        this.SDMLogic.addDynamicOrderToSDMarket(dynamicOrder);
        addOrdersToStoresInZone(subOrdersList, currentZoneName);
    }

    private void addOrdersToStoresInZone(List<Order> subOrdersList, String zoneName) {
        for (Order order : subOrdersList) {
            addOrderToStore(order);
            Store currentStore = getSelectedStoreByName(zoneName, order.getStoreName());
            addTransactionToShopOwner(currentStore.getOwnerName(), order);
        }
    }

    private OrderNotification createOrderNotificationToAddToShopOwner(Order order, Store currentStore) {
        String subject = String.format("Order #%d was made by %s from %s. The amount of item types in order is %d, the total item cost is %.2f and the total delivery cost is %.2f.",
                order.getOrderId(), order.getCustomerName(), currentStore.getName(), order.getNumberOfItemsTypesInOrder(),
                order.getCostOfItemsInOrder(), order.getDeliveryCost());
        return new OrderNotification(order.getCustomerName(), currentStore.getOwnerName(), subject,
                order.getOrderId(), order.getNumberOfItemsTypesInOrder(), order.getCostOfItemsInOrder(), order.getDeliveryCost(), order.getDateOrderWasMade());
    }

    private Order createDynamicOrderFromSubOrdersList(List<Order> subOrdersList, String username, String zoneName, Location deliveryLocation, String dateOfOrder) {

        double totalDeliveryCost = 0;
        Order sampleOrder = subOrdersList.get(0);
        int orderId = sampleOrder.getOrderId();
        List<StoreItem> itemsInOrder = new ArrayList<>();

        for (Order order : subOrdersList) {
            totalDeliveryCost += order.getDeliveryCost();
            itemsInOrder.addAll(order.getItemsInOrder());
        }

        return new Order(dateOfOrder, deliveryLocation, orderId, totalDeliveryCost, username, subOrdersList.size(), itemsInOrder, zoneName);
    }

    private Map<String, List<StoreItem>> createStoreToItemListMapOfOrder(JsonObject storesParticipatingWithRelevantItems) {
        Map<String, List<StoreItem>> outStoreToItemListMapOfOrder = new HashMap<>();
        for (String storeKey : storesParticipatingWithRelevantItems.keySet()) {
            List<StoreItem> currentStoreItems = createItemsInOrderList(storesParticipatingWithRelevantItems.get(storeKey).getAsJsonArray());
            outStoreToItemListMapOfOrder.put(storeKey, currentStoreItems);
        }

        return outStoreToItemListMapOfOrder;
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
        List<StoreItem> itemsInOrder = createItemsInOrderList(jObject.get("itemsInOrder").getAsJsonArray());
        Order order = new Order(dateOrderWasMade, getSDMLogic().getLastOrderID(), storeIdToOrderFrom, deliveryCost, currentUserName, storeName, itemsInOrder,orderDestination, currentZoneName);
        addOrderToCustomer(order, currentUserName);
        addOrderToStore(order);
        this.getSystemZones().get(currentZoneName).addOrderToZone(order);
        addTransactionsToShopOwnerAndCustomer(order, currentUserName, currentZoneName);
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
        storeToBuyFrom.updateNumberOfItemsSoldForAllItemsInOrder(order);
        OrderNotification orderNotification = createOrderNotificationToAddToShopOwner(order, storeToBuyFrom);
        ShopOwner currentShopOwner = (ShopOwner)getUser(storeToBuyFrom.getOwnerName());
        currentShopOwner.getRelevantNotifications().add(orderNotification);
    }

    private void addOrderToCustomer(Order order, String currentUserName) {
        this.systemUsersMap.get(currentUserName).addOrder(order);
    }

    private List<StoreItem> createItemsInOrderList(JsonArray itemsInOrderJsonArray) {
        List<StoreItem> outputItemsInOrderList = new ArrayList<>();

        for (JsonElement jsonItem : itemsInOrderJsonArray) {
            StoreItem sItem = createItemFromOrderList(jsonItem.getAsJsonObject());
            outputItemsInOrderList.add(sItem);
        }

        return outputItemsInOrderList;
    }

    private StoreItem createItemFromOrderList(JsonObject jsonItem) {
        int itemId = jsonItem.get("Id").getAsInt();
        String itemName = jsonItem.get("name").getAsString();
        String purchaseCategory = jsonItem.get("purchaseCategory").getAsString();
        double amount = jsonItem.get("amount").getAsFloat();
        int pricePerUnit = jsonItem.get("pricePerUnit").getAsInt();
        boolean wasPartOfDiscount = jsonItem.get("wasPartOfDiscount").getAsString().equals("Yes");

        return new StoreItem(itemId, amount, pricePerUnit, itemName, purchaseCategory, wasPartOfDiscount);
    }

    public void addFeedbacks(String feedbackMap, String currentUserName, String currentZoneName) {
        JsonObject jFeedbackMap = new JsonParser().parse(feedbackMap).getAsJsonObject();
        Map<String, Feedback> realFeedbackMap = new HashMap<>();
        for (String jsonFeedback : jFeedbackMap.keySet()) {
            JsonObject jFeedback = jFeedbackMap.get(jsonFeedback).getAsJsonObject();
            Feedback feedback = createFeedbackFromJsonObject(jFeedback, currentUserName);
            realFeedbackMap.put(jsonFeedback, feedback);
        }

        pushFeedbacksToShopOwners(realFeedbackMap, currentZoneName);
    }

    private void pushFeedbacksToShopOwners(Map<String, Feedback> feedbackMap, String zoneName) {
        feedbackMap.forEach((storeName, feedback) -> {
            int storeId = this.getSelectedStoreByName(zoneName, storeName).getId();
            String ownerName = this.getSystemZones().get(zoneName).getStoresInZone().get(storeId).getOwnerName();
            ShopOwner shopOwner = (ShopOwner) this.getUser(ownerName);
            shopOwner.addFeedback(zoneName, feedback);

            String subject = String.format("A new %d stars rating was given to %s by %s.",feedback.getRating(), storeName, feedback.getCustomerName());
            FeedbackNotification notification = new FeedbackNotification(feedback.getCustomerName(), ownerName, subject, feedback, feedback.getDateOfFeedback());
            shopOwner.getRelevantNotifications().add(notification);
        });
    }

    private Feedback createFeedbackFromJsonObject(JsonObject jsonFeedback, String username) {
        int rating = jsonFeedback.get("rating").getAsInt();
        String textReview = jsonFeedback.get("reviewText").getAsString();
        String storeName = jsonFeedback.get("storeName").getAsString();
        String date = jsonFeedback.get("date").getAsString();
        return new Feedback(rating, textReview, username, date, storeName);
    }
}
