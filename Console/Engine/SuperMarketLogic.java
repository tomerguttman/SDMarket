package Engine;

import SDMImprovedFacade.Order;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import SDMImprovedFacade.SuperDuperMarket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jaxb.generatedClasses.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SuperMarketLogic {
    private SuperDuperMarket SDMImproved;
    private String orderHistoryFilesPath;
    private boolean wasHistorySaved;

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
                    SDMImproved = loadXML(file, outputMessage);
                    if (SDMImproved == null) {
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

            return validateSDMDataLoaded(temp, outputMessage) ? new SuperDuperMarket(temp) : SDMImproved;
        } catch (JAXBException e) {
            throw new JAXBException(e.getMessage());
        }
    }

    private boolean validateSDMDataLoaded(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        try {
            updateOutputMessage(outputMessage, "<The data was loaded successfully from the XML file>");
            return isSDMItemsDataValid(SDMtoValidate, outputMessage) && isSDMStoresDataValid(SDMtoValidate, outputMessage);
        } catch (NullPointerException e) {
            throw new NullPointerException("<One of the members in the system was Null>");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("<There was an error instantiating a data structure>");
        }
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

                if (!(store.getLocation().getX() <= 50 && store.getLocation().getX() >= 1 &&
                        store.getLocation().getY() >= 1 && store.getLocation().getY() <= 50)) {
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
        return SDMImproved.getSystemStores();
    }

    public Map<Integer, StoreItem> getItems() {
        return SDMImproved.getSystemItems();
    }

    public boolean checkUserLocationAgainstAllStoresLocations(int x, int y) {
        AtomicBoolean locationIsValidFlag = new AtomicBoolean(true);
        Location userLocation = new Location();
        userLocation.setX(x);
        userLocation.setY(y);
        //iterate over the values and stop when one of the stores location equals to the user order location - return false because one of them matches.
        for (Store store : this.SDMImproved.getSystemStores().values()) {
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

            currentItemAmount = this.SDMImproved.getSystemItems().get(itemInOrder.getId()).getTotalItemsSold();
            this.SDMImproved.getSystemItems().get(itemInOrder.getId()).setTotalItemsSold(currentItemAmount + itemInOrder.getTotalItemsSold());
        });
    }

    public int getLastOrderID() {
        return this.SDMImproved.getOrderID();
    }

    public void updatePriceOfAnItem(int storeOfChoiceId, int itemId, double newPrice) {
        this.getStores().get(storeOfChoiceId).getItemsBeingSold().get(itemId).setPricePerUnit(newPrice);
    }

    public void updateAllStoresItemsAveragePricesAndAmountOfStoresSellingAnItem() {
        this.SDMImproved.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public void addItemToStore(StoreItem itemToAdd, Store storeOfChoice) {
        this.SDMImproved.getSystemStores().get(storeOfChoice.getId()).getItemsBeingSold().put(itemToAdd.getId(), itemToAdd);
        this.SDMImproved.getSystemStores().get(storeOfChoice.getId()).
                getItemsBeingSold().get(itemToAdd.getId()).setPricePerUnit(itemToAdd.getAveragePriceOfTheItem());
        this.SDMImproved.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public void removeItemFromStore(StoreItem itemToRemove, Store storeOfChoice) {
        this.SDMImproved.getSystemStores().get(storeOfChoice.getId()).getItemsBeingSold().remove(itemToRemove.getId());
        this.SDMImproved.initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
    }

    public boolean isValidStoreChoice(String userStoreIDInput) {
        int storeID = Integer.parseInt(userStoreIDInput);
        return this.getStores().containsKey(storeID);
    }

    public boolean validateLocationBorders(int x, int y) {
        return (1 <= x && x <= 50) && (1 <= y && y <= 50);
    }

    public void generateOrderForStore(Store storeToOrderFrom, String userDateInput, int lastOrderID, List<StoreItem> orderItems, Location userLocationInput) {
        storeToOrderFrom.generateOrder(userDateInput, lastOrderID, orderItems, userLocationInput);
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

        itemsToOrderWithAmount.forEach((itemID, amount) -> {
            this.getStores().values().forEach(store -> {
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
            });
        });

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

    public boolean wasHistorySaved() {
        return wasHistorySaved;
    }

    public void setWasHistorySaved(boolean wasHistorySaved) {
        this.wasHistorySaved = wasHistorySaved;
    }

    public Map<Integer, List<StoreItem>> generateItemsListForEachStore(List<StoreItem> itemsToOrder,
                                                                       Map<Integer, Store> cheapestStoresForEachProduct) {
        // Map<Integer, Store> cheapestStoresForEachProduct, Integer -> itemID, Store -> Store that sells it in the lowest price
        Map<Integer, List<StoreItem>> itemsListForEachStore = new HashMap<>(); // Integer -> storeID

        itemsToOrder.forEach(item -> {
            int currentStoreID = cheapestStoresForEachProduct.get(item.getId()).getId();
            List<StoreItem> currentItemList = itemsListForEachStore.get(currentStoreID);

            if (currentItemList == null) {
                currentItemList = new ArrayList<StoreItem>();
            }

            currentItemList.add(item);
            itemsListForEachStore.put(currentStoreID, currentItemList);
        });

        return itemsListForEachStore;
    }

    public Map<Integer, Order> getDynamicOrders() {
        return this.SDMImproved.getSystemDynamicOrders();
    }

    public double updateStoreRevenue(List<StoreItem> listOfItems, Store storeOfChoice,
                                     Location userLocationInput, String userDateInput, int orderIDForAllOrdersIncluded) {
        storeOfChoice.generateOrder(userDateInput, orderIDForAllOrdersIncluded, listOfItems, userLocationInput);
        return storeOfChoice.calculateDistance(userLocationInput) * storeOfChoice.getDeliveryPpk();
    }

    public void generateDynamicOrderAndRecord(List<StoreItem> itemsToOrder, Double totalDeliveryCost, String userDateInput, Location userLocationInput, int amountOfStoresParticipating, int orderIDForAllOrdersIncluded) {
        Order dynamicOrder = new Order(userDateInput, userLocationInput, orderIDForAllOrdersIncluded,
                totalDeliveryCost, amountOfStoresParticipating, itemsToOrder);
        this.SDMImproved.addDynamicOrder(dynamicOrder);
    }

    public String getStringOfAllDynamicSystemOrders() {
        Map<Integer, Order> dynamicOrders = this.SDMImproved.getSystemDynamicOrders();
        StringBuilder dynamicOrderHistory = new StringBuilder();

        if (dynamicOrders != null) {
            dynamicOrders.forEach((orderID, order) -> {
                dynamicOrderHistory.append(order.toStringDynamicOrder());
            });
        } else {
            dynamicOrderHistory.setLength(0);
            dynamicOrderHistory.append("\tCurrently no dynamic orders were made in the system\n\n");
        }

        return dynamicOrderHistory.toString();
    }

    public void writeStaticOrdersToFile(String pathToFile, Gson gson) throws Exception {
        try {
            File file = new File(pathToFile + "\\static_orders_history.json");
            List<Order> ordersHistory = new ArrayList<>();

            if (file.exists()) {
                if (!file.delete()) {
                    throw new Exception();
                }
            }

            if (file.createNewFile()) {
                Writer fileWriter = new FileWriter(file, true);

                for (Store store : this.getStores().values()) {
                    ordersHistory.addAll(store.getStoreOrdersHistory());
                }

                fileWriter.write(gson.toJson(ordersHistory)); //Writes all of the static orders in the system to "static_order_history.json"
                fileWriter.close();
            }
            else { throw new Exception(); }
        }
        catch (Exception e) {
            throw new Exception("<There was a problem writing the static orders to a json file>");
        }
    }

    public void writeDynamicOrdersToFile(String pathToFile, Gson gson) throws Exception {
        try {
            File file = new File(pathToFile + "\\dynamic_orders_history.json");
            List<Order> dynamicOrdersHistory;
            Writer fileWriter = new FileWriter(file, true);

            if (file.exists()) {
                if (!file.delete()) {
                    throw new Exception();
                }
            }

            if (file.createNewFile()) {
                dynamicOrdersHistory = new ArrayList<>(this.getDynamicOrders().values());
                fileWriter.write(gson.toJson(dynamicOrdersHistory)); //Writes all of the dynamic orders in the system to "static_order_history.json"
                fileWriter.close();
            }
            else { throw new Exception(); }
        }
        catch (Exception e) {
            throw new Exception("<There was a problem writing the dynamic orders to json file>");
        }
    }

    public String getOrderHistoryFilesPath () {
        return orderHistoryFilesPath;
    }

    public void setOrderHistoryFilesPath (String orderHistoryFilesPath){
        this.orderHistoryFilesPath = orderHistoryFilesPath;
    }

    public boolean deleteOrdersHistoryFiles () {
        if (this.wasHistorySaved) {
            try {
                File staticHistory = new File(this.getOrderHistoryFilesPath() + "\\static_orders_history.json");
                File dynamicHistory = new File(this.getOrderHistoryFilesPath() + "\\dynamic_orders_history.json");
                return staticHistory.delete() && dynamicHistory.delete();
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    public void loadStaticOrdersHistory (String staticOrdersPath, Gson gson) throws IOException {
        try {
            File staticOrdersFile = new File(staticOrdersPath);
            FileReader fReader = new FileReader(staticOrdersFile);

            List<Order> staticOrders = gson.fromJson(fReader, new TypeToken<List<Order>>() {
            }.getType());
            System.out.println(staticOrders.toString());

        } catch (IOException e) {
            throw new IOException("<There was a problem loading the static orders file>");
        }


    }

    public void loadDynamicOrdersHistory (String dynamicOrdersPath, Gson gson) {
        //DO STH;
    }


}
