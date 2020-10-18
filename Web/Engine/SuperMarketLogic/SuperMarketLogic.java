package SuperMarketLogic;

import SDMImprovedFacade.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import generatedClasses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SuperMarketLogic {
    private SuperDuperMarket SDMarket = new SuperDuperMarket();
    private final int X_TOP_RANGE = 50;
    private final int X_LOW_RANGE = 1;
    private final int Y_TOP_RANGE = 50;
    private final int Y_LOW_RANGE = 1;


    public boolean loadData(String filePath, StringBuilder outputMessage) throws JAXBException {
        boolean successFlag = true;

        try {
            if (filePath.isEmpty()) {
                updateOutputMessage(outputMessage, "<The input file path was empty, please enter a valid path>");
                successFlag = false;
            }

            if (filePath.length() >= 4 && filePath.substring(filePath.length() - 3).toLowerCase().equals("xml")) {
                File file = new File(filePath);
                if (file.exists()) {
                    SDMarket = loadXML(file, outputMessage);
                    if (SDMarket == null) {
                        successFlag = false;
                    }
                } else {
                    updateOutputMessage(outputMessage, "<The file does not exist in the path that was given>");
                    successFlag = false;
                }
            } else {
                updateOutputMessage(outputMessage, "<The file type that was given is not .xml>");
                successFlag = false;
            }

            return successFlag;

        } catch (SecurityException e) {
            throw new SecurityException("<The file access was blocked by the file's security manager>");
        } catch (NullPointerException e) {
            throw new NullPointerException("<The path that was given is NULL>");
        } catch (JAXBException e) {
            throw new JAXBException("<There was an issue unmarshalling from the XML file>");
        }
    }

    private SuperDuperMarket loadXML(File file, StringBuilder outputMessage) throws JAXBException {
        try {
            SuperDuperMarketDescriptor temp;
            JAXBContext jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            temp = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(file);

            return validateSDMDataLoaded(temp, outputMessage) ? new SuperDuperMarket() : SDMarket;
        } catch (JAXBException e) {
            throw new JAXBException(e.getMessage());
        }
    }

    private Zone loadZoneXML(InputStream file, StringBuilder outputMessage, String ownerName) throws JAXBException {
        try {
            SuperDuperMarketDescriptor SDMDescriptor;
            JAXBContext jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SDMDescriptor = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(file);

            return validateZoneData(SDMDescriptor, outputMessage) ? new Zone(SDMDescriptor, ownerName) : null;
        } catch (JAXBException e) {
            throw new JAXBException(e.getMessage());
        }
    }

    private boolean validateSDMDataLoaded(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        try {
            //updateOutputMessage(outputMessage, "<The data was loaded successfully from the XML file>");
            return isSDMItemsDataValid(SDMtoValidate, outputMessage) && isSDMStoresDataValid(SDMtoValidate, outputMessage) &&
                    isSDMDiscountsDataValid(SDMtoValidate, outputMessage);
        } catch (NullPointerException e) {
            throw new NullPointerException("<One of the members in the system was Null>");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("<There was an error instantiating a data structure>");
        }
    }

    private boolean validateZoneData(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        try {
            return isSDMItemsDataValid(SDMtoValidate, outputMessage) && isSDMStoresDataValid(SDMtoValidate, outputMessage) &&
                    isSDMDiscountsDataValid(SDMtoValidate, outputMessage) && isSDMZoneNameUnique(SDMtoValidate, outputMessage);
        } catch (NullPointerException e) {
            throw new NullPointerException("<One of the members in the Zone file was Null>");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("<There was an error instantiating a data structure>");
        }
    }

    private boolean isSDMZoneNameUnique(SuperDuperMarketDescriptor sdMtoValidate, StringBuilder outputMessage) {
        if(SDMarket.getSystemZones().containsKey(sdMtoValidate.getSDMZone().getName())){
            outputMessage.append("The XML file uploaded contains a zone with a name that is not unique");
            return false;
        }
        else{
            return true;
        }
    }

    private boolean isSDMDiscountsDataValid(SuperDuperMarketDescriptor sdMtoValidate, StringBuilder outputMessage) {
        //When entering this method all of the stores items are valid and in the system.
        for (SDMStore store : sdMtoValidate.getSDMStores().getSDMStore()) {
            if (validateStoreDiscountsItemsPurchasable(store.getSDMDiscounts(), store)){
                if(!validateStoreDiscountsUniqueNames(store.getSDMDiscounts())){
                    updateOutputMessage(outputMessage, "<There are two discounts with the same name in one store>");
                    return false;
                }
            }
            else{
                updateOutputMessage(outputMessage, "<There is a discount in the store which invloves an item that is not being sold by the store>");
                return false;
            }
        }

        return true;
    }

    //MIGHT BE FUALTY
    private boolean validateStoreDiscountsUniqueNames(SDMDiscounts sdmDiscounts) {
        HashSet<String> discountsNames = new HashSet<>();
        if(sdmDiscounts != null){
            for (SDMDiscount discount : sdmDiscounts.getSDMDiscount()) {
                if(!discountsNames.contains(discount.getName())) {
                    discountsNames.add(discount.getName());
                }
                else { return false; }
            }
        }

        return true;
    }

    private boolean validateStoreDiscountsItemsPurchasable(SDMDiscounts sdmDiscounts, SDMStore store) {
        if(sdmDiscounts != null){
            for (SDMDiscount discount : sdmDiscounts.getSDMDiscount()) {
                if(isItemExistInStore(discount.getIfYouBuy().getItemId(), store)){
                    for (SDMOffer offer : discount.getThenYouGet().getSDMOffer()) {
                        if(!isItemExistInStore(offer.getItemId(), store)){ return false; }
                    }
                }
                else { return false; }
            }
        }

        return true;
    }

    private boolean isItemExistInStore(int itemId, SDMStore store) {
        for (SDMSell sdmSell : store.getSDMPrices().getSDMSell()) {
            if (itemId == sdmSell.getItemId()) {
                return true;
            }
        }

        return false;
    }

    private boolean isSDMCustomerLocationInRange(Location location) {
        return X_LOW_RANGE <= location.getX() && location.getX() <= X_TOP_RANGE &&
                Y_LOW_RANGE <= location.getY() && location.getY() <= Y_TOP_RANGE;
    }

    private boolean isSDMItemsDataValid(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        if (isItemsIdUnique(SDMtoValidate.getSDMItems())) {
            if (isItemBeingSoldByAtLeastOneStore(SDMtoValidate.getSDMItems().getSDMItem(), SDMtoValidate.getSDMStores().getSDMStore())) {
                return true;
            } else {
                updateOutputMessage(outputMessage, "<There is an item that is not being sold by any of the stores in the system>");
            }
        } else {
            updateOutputMessage(outputMessage, "<There are two items with the same ID>");
        }

        return false;
    }

    private boolean isItemBeingSoldByAtLeastOneStore(List<SDMItem> itemsToValidate, List<SDMStore> systemStores) {
        Set<Integer> allStoresItemsId = new HashSet<>();
        systemStores.forEach(store -> store.getSDMPrices().getSDMSell().
                forEach(sellItem -> allStoresItemsId.add(sellItem.getItemId())));
        for (SDMItem item : itemsToValidate) {
            if (!allStoresItemsId.contains(item.getId())) {
                return false;
            }
        }

        return true;
    }

    private boolean isItemsIdUnique(SDMItems itemsToValidate) {
        HashSet<Integer> hashSet = new HashSet<>();

        for (SDMItem item : itemsToValidate.getSDMItem()) {
            if (!hashSet.contains(item.getId())) {
                hashSet.add(item.getId());
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean isSDMStoresDataValid(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        SDMStores sdmStores = SDMtoValidate.getSDMStores();

        if (isStoreIdUnique(sdmStores)) {
            if (isStoresCoordinatesValid(sdmStores)) {
                if (isStoreItemsExistInSystem(sdmStores, SDMtoValidate.getSDMItems())) {
                    if (isStoreItemsBeingSoldOnce(sdmStores)) {
                        return true;
                    } else {
                        updateOutputMessage(outputMessage, "<One of the stores is selling an item twice>");
                    }
                } else {
                    updateOutputMessage(outputMessage, "<One of the stores is selling items that are not recognized by the system>");
                }
            } else {
                updateOutputMessage(outputMessage, "<There is a store whose coordinates are not in range [1-50]>");
            }
        } else {
            updateOutputMessage(outputMessage, "<There are two stores with the same ID>");
        }

        return false;
    }

    private void updateOutputMessage(StringBuilder outputMessage, String s) {
        outputMessage.setLength(0);
        outputMessage.append(s);
    }

    private boolean isStoreItemsBeingSoldOnce(SDMStores storesToValidate) {
        Set<Integer> setOfItemsInStore = new HashSet<>();

        for (SDMStore store : storesToValidate.getSDMStore()) {
            for (SDMSell itemBeingSold : store.getSDMPrices().getSDMSell()) {
                if (setOfItemsInStore.contains(itemBeingSold.getItemId())) {
                    return false;
                } else {
                    setOfItemsInStore.add(itemBeingSold.getItemId());
                }
            }

            setOfItemsInStore.clear();
        }

        return true;
    }

    private boolean isStoreItemsExistInSystem(SDMStores storesToValidate, SDMItems systemItemsAvailable) {

        Set<Integer> setOfItemsInSystem;
        setOfItemsInSystem = listToSet(systemItemsAvailable.getSDMItem());

        for (SDMStore store : storesToValidate.getSDMStore()) {
            for (SDMSell sdmSellItem : store.getSDMPrices().getSDMSell()) {
                if (!setOfItemsInSystem.contains(sdmSellItem.getItemId())) {
                    return false;
                }
            }
        }

        return true;
    }

    private Set<Integer> listToSet(List<SDMItem> sdmItems) {
        Set<Integer> outputSet = new HashSet<>();
        sdmItems.forEach(item -> outputSet.add(item.getId()));
        return outputSet;
    }

    private boolean isStoresCoordinatesValid(SDMStores storesToValidate) {
        try {
            for (SDMStore store : storesToValidate.getSDMStore()) {

                if (!(store.getLocation().getX() <= X_TOP_RANGE && store.getLocation().getX() >= X_LOW_RANGE &&
                        store.getLocation().getY() >= Y_LOW_RANGE && store.getLocation().getY() <= Y_TOP_RANGE)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            throw new NullPointerException("<NullPointerException: The list of stores in the system is NULL>");
        }

    }

    private boolean isStoreIdUnique(SDMStores storesToValidate) {
        HashSet<Integer> hashSet = new HashSet<>();

        for (SDMStore store : storesToValidate.getSDMStore()) {
            if (!hashSet.contains(store.getId())) {
                hashSet.add(store.getId());
            } else {
                return false;
            }
        }

        return true;
    }

    public Map<Integer, Store> getStores() {
        return SDMarket.getSystemStores();
    }

    public Map<Integer, StoreItem> getItems() {
        return SDMarket.getSystemItems();
    }

    public boolean checkUserLocationAgainstAllStoresLocations(int x, int y) {
        AtomicBoolean locationIsValidFlag = new AtomicBoolean(true);
        Location userLocation = new Location();
        userLocation.setX(x);
        userLocation.setY(y);
        //iterate over the values and stop when one of the stores location equals to the user order location - return false because one of them matches.
        for (Store store : this.SDMarket.getSystemStores().values()) {
            if (store.getStoreLocation().getY() == y && store.getStoreLocation().getX() == x) {
                locationIsValidFlag.set(false);
                break;
            }
        }

        return locationIsValidFlag.get();
    }

    public void updateStoreAndSystemItemAmountInformationAccordingToNewOrder(List<StoreItem> orderItems, Store storeToOrderFrom) {
        orderItems.forEach(itemInOrder -> {
            double currentItemAmount = storeToOrderFrom.getItemsBeingSold().get(itemInOrder.getId()).getTotalItemsSold();
            storeToOrderFrom.getItemsBeingSold().get(itemInOrder.getId()).setTotalItemsSold(currentItemAmount + itemInOrder.getTotalItemsSold());

            currentItemAmount = this.SDMarket.getSystemItems().get(itemInOrder.getId()).getTotalItemsSold();
            this.SDMarket.getSystemItems().get(itemInOrder.getId()).setTotalItemsSold(currentItemAmount + itemInOrder.getTotalItemsSold());
        });
    }

    public int getLastOrderID() {
        return this.SDMarket.getOrderID();
    }

    public void updatePriceOfAnItem(int storeOfChoiceId, int itemId, double newPrice) {
        this.getStores().get(storeOfChoiceId).getItemsBeingSold().get(itemId).setPricePerUnit(newPrice);
        this.SDMarket.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public void updateAllStoresItemsAveragePricesAndAmountOfStoresSellingAnItem() {
        this.SDMarket.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public void addItemToStore(StoreItem itemToAdd, Store storeOfChoice) {
        this.SDMarket.getSystemStores().get(storeOfChoice.getId()).getItemsBeingSold().put(itemToAdd.getId(), itemToAdd);

        this.SDMarket.getSystemItems().put(itemToAdd.getId(), itemToAdd);
        this.SDMarket.getSystemStores().get(storeOfChoice.getId()).
                getItemsBeingSold().get(itemToAdd.getId()).setPricePerUnit(itemToAdd.getPricePerUnit());
        this.SDMarket.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public void removeItemFromStore(StoreItem itemToRemove, Store storeOfChoice) {
        this.SDMarket.getSystemStores().get(storeOfChoice.getId()).getItemsBeingSold().remove(itemToRemove.getId());
        this.SDMarket.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public boolean isValidStoreChoice(String userStoreIDInput) {
        int storeID = Integer.parseInt(userStoreIDInput);
        return this.getStores().containsKey(storeID);
    }

    public boolean validateLocationBorders(int x, int y) {
        return (X_LOW_RANGE <= x && x <= X_TOP_RANGE) && (Y_LOW_RANGE <= y && y <= Y_TOP_RANGE);
    }

    public void generateOrderForStore(String customerName, Store storeToOrderFrom, String userDateInput, int lastOrderID, List<StoreItem> orderItems, Location userLocationInput, String zoneName) {
        storeToOrderFrom.generateOrder(customerName, userDateInput, lastOrderID, orderItems, userLocationInput, zoneName);
    }

    public double calculateDistanceFromUser(Store storeToOrderFrom, Location userLocationInput) {
        return storeToOrderFrom.calculateDistance(userLocationInput);
    }

    public int getStorePpk(Store storeToOrderFrom) {
        return storeToOrderFrom.getDeliveryPpk();
    }

    public String getStringOfStaticLastOrder(List<StoreItem> orderItems, double distanceFromUser, int ppk) {
        StringBuilder orderStringBuilder = new StringBuilder();

        orderStringBuilder.append("-----\tOrder Summary\t-----\n");
        orderItems.forEach(itemOrdered -> {
            orderStringBuilder.append(itemOrdered.getStringItemForPurchase());
            orderStringBuilder.append("\t\tAmount Bought: ").append(itemOrdered.getTotalItemsSold()).append("\n");
            orderStringBuilder.append("\t\tTotal Price: ").append(itemOrdered.getTotalItemsSold() * itemOrdered.getPricePerUnit()).append("\n");
        });

        orderStringBuilder.append("Distance From Destination: ").append(String.format("%.2f", distanceFromUser)).append("\n");
        orderStringBuilder.append("PPK: ").append(ppk).append("\n");
        orderStringBuilder.append("Total Cost Of Delivery: ").append(String.format("%.2f", distanceFromUser * ppk)).append("\n");

        return orderStringBuilder.toString();
    }

    public String getStringOfDynamicLastOrder(List<StoreItem> orderItems) {
        StringBuilder dynamicOrderStringBuilder = new StringBuilder();

        dynamicOrderStringBuilder.append("-----\tOrder Summary\t-----\n");
        orderItems.forEach(itemOrdered -> {
            dynamicOrderStringBuilder.append(itemOrdered.getStringItemForPurchase());
            dynamicOrderStringBuilder.append("\t\tAmount Bought: ").append(itemOrdered.getTotalItemsSold()).append("\n");
            dynamicOrderStringBuilder.append("\t\tTotal Price: ").append(itemOrdered.getTotalItemsSold() * itemOrdered.getPricePerUnit()).append("\n");
        });

        return dynamicOrderStringBuilder.toString();
    }

    public String getStringOfAllStaticSystemOrders() {
        Map<Integer, Store> systemStores = this.getStores();
        AtomicInteger orderCounter = new AtomicInteger();
        StringBuilder orderHistory = new StringBuilder();

        systemStores.values().forEach(store -> store.getStoreOrdersHistory().forEach(order -> {
            orderHistory.append(order.toString());
            orderCounter.addAndGet(store.getStoreOrdersHistory().size());
        }));

        if (orderCounter.get() == 0) {
            orderHistory.setLength(0);
            orderHistory.append("\tCurrently no orders were made in the system\n\n");
        }

        return orderHistory.toString();
    }

    public Map<Integer, Store> getCheapestStoresPerProductMap(Map<Integer, Double> itemsToOrderWithAmount) {
        //Integer -> item ID, Store -> the store that sells the item in the lowest price.
        Map<Integer, Store> cheapestStoresPerProductMap = new HashMap<>();

        itemsToOrderWithAmount.forEach((itemID, amount) -> this.getStores().values().forEach(store -> {
            if (store.getItemsBeingSold().containsKey(itemID)) {
                if (cheapestStoresPerProductMap.containsKey(itemID)) {
                    if (store.getItemsBeingSold().get(itemID).getPricePerUnit() <
                            cheapestStoresPerProductMap.get(itemID).getItemsBeingSold().get(itemID).getPricePerUnit()) {
                        cheapestStoresPerProductMap.put(itemID, store);
                    }
                } else {
                    cheapestStoresPerProductMap.put(itemID, store);
                }
            }
        }));

        return cheapestStoresPerProductMap;
    }

    public List<StoreItem> createListOfOrderedItemsByCheapestPrice(Map<Integer, Double> itemsToOrderWithAmount,
                                                                   Map<Integer, Store> cheapestStoresForEachProduct) {
        List<StoreItem> outputOrderList = new ArrayList<>();

        itemsToOrderWithAmount.forEach((itemID, amountOfTheItem) -> {
            StoreItem currentStoreItem = new StoreItem(cheapestStoresForEachProduct.get(itemID).getItemsBeingSold().get(itemID));
            currentStoreItem.setTotalItemsSold(itemsToOrderWithAmount.get(itemID));
            outputOrderList.add(currentStoreItem);
        });

        return outputOrderList;
    }

    public Map<Integer, List<StoreItem>> generateItemsListForEachStore(List<StoreItem> itemsToOrder,
                                                                       Map<Integer, Store> cheapestStoresForEachProduct) {
        // Map<Integer, Store> cheapestStoresForEachProduct, Integer -> itemID, Store -> Store that sells it in the lowest price
        Map<Integer, List<StoreItem>> itemsListForEachStore = new HashMap<>(); // Integer -> storeID

        itemsToOrder.forEach(item -> {
            int currentStoreID = cheapestStoresForEachProduct.get(item.getId()).getId();
            List<StoreItem> currentItemList = itemsListForEachStore.get(currentStoreID);

            if (currentItemList == null) {
                currentItemList = new ArrayList<>();
            }

            currentItemList.add(item);
            itemsListForEachStore.put(currentStoreID, currentItemList);
        });

        return itemsListForEachStore;
    }

    public Map<Integer, Order> getDynamicOrders() {
        return this.SDMarket.getSystemDynamicOrders();
    }

    public double updateStoreRevenue(String customerName, List<StoreItem> listOfItems, Store storeOfChoice,
                                     Location userLocationInput, String userDateInput, int orderIDForAllOrdersIncluded, String zoneName) {
        storeOfChoice.generateOrder(customerName, userDateInput, orderIDForAllOrdersIncluded, listOfItems, userLocationInput, zoneName);
        return storeOfChoice.calculateDistance(userLocationInput) * storeOfChoice.getDeliveryPpk();
    }

    public void generateDynamicOrderAndRecord(String customerName, List<StoreItem> itemsToOrder, Double totalDeliveryCost, String userDateInput, Location userLocationInput, int amountOfStoresParticipating, int orderIDForAllOrdersIncluded, String zoneName) {
        Order dynamicOrder = new Order(userDateInput, userLocationInput, orderIDForAllOrdersIncluded,
                totalDeliveryCost, customerName, amountOfStoresParticipating, itemsToOrder, zoneName);
        this.SDMarket.addDynamicOrder(dynamicOrder);
    }

    public String getStringOfAllDynamicSystemOrders() {
        Map<Integer, Order> dynamicOrders = this.SDMarket.getSystemDynamicOrders();
        StringBuilder dynamicOrderHistory = new StringBuilder();

        if (!dynamicOrders.isEmpty()) {
            dynamicOrders.forEach((orderID, order) -> dynamicOrderHistory.append(order.toStringDynamicOrder()));
        } else {
            dynamicOrderHistory.setLength(0);
            dynamicOrderHistory.append("\tCurrently no dynamic orders were made in the system\n\n");
        }

        return dynamicOrderHistory.toString();
    }

    public void writeStaticOrdersToFile(String pathToFile, Gson gson) throws Exception {
        try {
            File file = new File(pathToFile + "_static_orders_history.json");
            List<Order> ordersHistory = new ArrayList<>();

            Writer fileWriter = new FileWriter(file);

            for (Store store : this.getStores().values()) {
                ordersHistory.addAll(store.getStoreOrdersHistory());
            }

            fileWriter.write(gson.toJson(ordersHistory)); //Writes all of the static orders in the system to "static_order_history.json"
            fileWriter.close();
        }
        catch (Exception e) {
            throw new Exception("<There was a problem writing the static orders to a json file>");
        }
    }

    public void writeDynamicOrdersToFile(String pathToFile, Gson gson) throws Exception {
        try {
            File file = new File(pathToFile + "_dynamic_orders_history.json");
            List<Order> dynamicOrdersHistory;

            Writer fileWriter = new FileWriter(file);
            dynamicOrdersHistory = new ArrayList<>(this.getDynamicOrders().values());
            fileWriter.write(gson.toJson(dynamicOrdersHistory)); //Writes all of the dynamic orders in the system to "static_order_history.json"
            fileWriter.close();
        }
        catch (Exception e) {
            throw new Exception("<There was a problem writing the dynamic orders to json file>");
        }
    }

    public void loadStaticOrdersHistory (String staticOrdersPath, Gson gson) throws IOException {
        try {
            Map<Integer, Store> systemStores;
            File staticOrdersFile = new File(staticOrdersPath);
            FileReader fReader = new FileReader(staticOrdersFile);
            List<Order> staticOrders = gson.fromJson(fReader, new TypeToken<List<Order>>() {}.getType());
            resetStoresInformationBeforeLoad();//resets orderHistoryList, ordersRevenue = 0, each itemBeingSold -> amount = 0.
            resetSystemItemAmountSoldStats();//each itemBeingSold -> amount = 0
            systemStores = this.getStores();
            addStaticOrdersToStoresAndUpdateSystemInfo(systemStores, staticOrders);

        } catch (IOException e) {
            throw new IOException("<There was a problem loading the static orders file>");
        }
    }

    private void resetSystemItemAmountSoldStats() {
        this.SDMarket.getSystemItems().values().forEach(systemItem -> systemItem.setTotalItemsSold(0));
    }

    private void addStaticOrdersToStoresAndUpdateSystemInfo(Map<Integer, Store> systemStores, List<Order> staticOrders) {
        staticOrders.forEach(order -> {
            int storeID = order.getStoreId();
            systemStores.get(storeID).getStoreOrdersHistory().add(order);
            systemStores.get(storeID).setTotalOrdersRevenue( systemStores.get(storeID).getTotalOrdersRevenue() + order.getTotalOrderCost());
            updateStoreAndSystemItemAmountInformationAccordingToNewOrder(order.getItemsInOrder(), systemStores.get(storeID));
        });
    }

    private void resetStoresInformationBeforeLoad() {
        this.getStores().values().forEach(store -> {
            //in the Store c'tor the storeOrdersHistory is instantiated so null exception can't be thrown.
            store.getStoreOrdersHistory().clear();
            store.setTotalOrdersRevenue(0); //set store revenue to zero so after loading the history it will be updated accordingly
            store.getItemsBeingSold().values().forEach(itemBeingSold -> itemBeingSold.setTotalItemsSold(0));
        });
    }

    public void loadDynamicOrdersHistory (String dynamicOrdersPath, Gson gson) throws IOException {
        try {
            Map<Integer, Order> currentSystemDynamicOrders;
            File staticOrdersFile = new File(dynamicOrdersPath);
            FileReader fReader = new FileReader(staticOrdersFile);
            List<Order> dynamicOrders = gson.fromJson(fReader, new TypeToken<List<Order>>() {}.getType());

            if(dynamicOrders != null){
                if(!dynamicOrders.isEmpty()) {
                    currentSystemDynamicOrders = this.getDynamicOrders();

                    if(currentSystemDynamicOrders == null) {
                        currentSystemDynamicOrders = new HashMap<>();
                    }
                    else { currentSystemDynamicOrders.clear(); }

                    for (Order dynamicOrder : dynamicOrders) {
                        currentSystemDynamicOrders.put(dynamicOrder.getOrderId(), dynamicOrder);
                    }
                }
            }

        } catch (IOException e) {
            throw new IOException("<There was a problem loading the dynamic orders file>");
        }
    }

    public void setOrderIDAfterHistoryLoaded(){
        AtomicInteger maxOrderID = new AtomicInteger();
        maxOrderID.set(-1);

        for (Store store : this.SDMarket.getSystemStores().values()) {
            store.getStoreOrdersHistory().forEach(order -> {
                if (order.getOrderId() > maxOrderID.get()) {
                    maxOrderID.set(order.getOrderId());
                }
            });
        }

        this.SDMarket.setOrderID(maxOrderID.get() + 1);
    }

    public Double getCheapestPriceForItem(int itemId) {
        StoreItem sItem;
        double cheapestPrice = 0;
        for (Store store : this.getStores().values()) {
            if(store.getItemsBeingSold().containsKey(itemId)) {
                sItem = store.getItemsBeingSold().get(itemId);
                if(sItem.getPricePerUnit() < cheapestPrice) {
                    cheapestPrice = sItem.getPricePerUnit();
                }
            }
        }

        return cheapestPrice;
    }

    public Store getStoreForDynamicPurchase(int itemId) {
        StoreItem sItem;
        Store storeToBuyFrom = null;
        double cheapestPrice = 0;
        for (Store store : this.getStores().values()) {
            if(store.getItemsBeingSold().containsKey(itemId)) {
                 sItem = store.getItemsBeingSold().get(itemId);
                 if(sItem.getPricePerUnit() < cheapestPrice || cheapestPrice == 0) { //cheapestPrice == 0 for the first time...
                     cheapestPrice = sItem.getPricePerUnit();
                     storeToBuyFrom = store;
                 }
            }
        }

        return storeToBuyFrom;
    }

    public void addStaticOrderToCustomer(int currentStaticStoreId, int lastOrderId, Customer customer) {
        Order lastOrder = this.SDMarket.getSystemStores().get(currentStaticStoreId).getLastOrder();
        if(lastOrderId == lastOrder.getOrderId()) {
            customer.addOrder(lastOrder);
        }
    }

    public void addDynamicOrderToCustomer(int lastOrderId, Customer customer) {
        customer.addOrder(this.SDMarket.getSystemDynamicOrders().get(lastOrderId));
    }

    public void addStoreToSystem(Store newlyAddedStore) {
        this.SDMarket.getSystemStores().put(newlyAddedStore.getId(), newlyAddedStore);
        this.SDMarket.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public Map<String, Zone> getSystemZones() {
        return this.SDMarket != null ? this.SDMarket.getSystemZones() : null;
    }

    public Zone validateZoneXML(InputStream inputStream, String ownerName, StringBuilder sBuilder) throws JAXBException {
        Zone zoneToAdd = loadZoneXML(inputStream, sBuilder, ownerName);

        if(zoneToAdd != null){
            SDMarket.addNewZoneToSystem(zoneToAdd);
        }

        return zoneToAdd;
    }

    public void addStoreToZoneInSystem(String currentZoneName, Store newStoreToAdd) {
        this.SDMarket.getSystemZones().get(currentZoneName).addStoreToZone(newStoreToAdd);
    }
}
