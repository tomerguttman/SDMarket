package UI;

import Engine.SuperMarketLogic;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import jaxb.generatedClasses.Location;
import javax.xml.bind.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.*;

public class ConsoleUI {

    private SuperMarketLogic SDMLogic;
    private boolean dataWasLoaded = false;

    public static void main(String[] args) {
        ConsoleUI ConsoleApp = new ConsoleUI();
        ConsoleApp.run();
    }

    private void printMenu(){
        System.out.println("Please select one of the following options by entering a number:");
        System.out.println("1.Load system details from XML file.");
        System.out.println("2.Print the details of all the stores that are currently in the system.");
        System.out.println("3.Print the details of all the items that are currently in the system.");
        System.out.println("4.Perform a purchase.");
        System.out.println("5.Print orders history of the entire system.");
        System.out.println("6.Update store products.");
        System.out.println("\nPress 'q' at any time to exit the system");
    }

    private boolean validateMenuUserInputChoice(String userInput) throws IllegalInputException {
        try {
            int userInputParsed = Integer.parseInt(userInput);
            return 1 <= userInputParsed && userInputParsed <= 6;
        }
        catch (NumberFormatException e) {
            if(userInput.toLowerCase().equals("q")){
                return true;
            }
            throw (new IllegalInputException("<IllegalInputException: The input you entered is not a number or 'q'>\n"));
        }
    }

    private void run() {
        //IN THE README MENTION THAT 2 STORES ON THE SAME LOCATION IS NOT VALID! ! ! ! ! !
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-small.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-error-3.7.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-error-3.6.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-error-3.5.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-error-3.4.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-error-3.3.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-error-3.2.xml [V]
        //D:\Java - SDM\SDM_ConsoleApp\src\tests\ex1-big.xml [V]
        SDMLogic = new SuperMarketLogic();
        Scanner scn = new Scanner(System.in);
        String userMenuChoice;
        StringBuilder outputMessage = new StringBuilder();
        boolean exitFlag = false;

        while (!exitFlag) {
            try
            {
                printMenu();
                userMenuChoice = scn.nextLine().trim();
                outputMessage.setLength(0); //delete the previous output message

                if(validateMenuUserInputChoice(userMenuChoice)) {
                    if(userMenuChoice.equals("q")){
                        exitFlag = true;
                        System.out.println("Thank you for using our SDMarket");
                    }
                    else if(userMenuChoice.equals("1")) {
                        handleLoadDataFromXMLAction(scn, outputMessage); //updating of 'dataWasLoaded' variable is being made inside handleLoadDataFromXMLAction
                        System.out.println("\n" + outputMessage.toString() + "\n");
                    }
                    else {
                        if(dataWasLoaded) { performActionOfChoice(userMenuChoice); }
                        else { System.out.println("\n<You can't perform this action without loading a system data file first>\n"); }
                    }
                }
                else { System.out.println("\n<Please choose one of the options available between [1 - 6] or 'q' to exit the system>\n"); }
            }
            catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }

    private void handleLoadDataFromXMLAction(Scanner scn, StringBuilder outputMessage) throws JAXBException {
        System.out.println("Please enter a path to the .xml file in order to load it.");
        this.dataWasLoaded = this.SDMLogic.loadData(scn.nextLine().trim(), outputMessage) || this.dataWasLoaded;
    }

    private void performActionOfChoice (String userMenuChoice) {

        switch (userMenuChoice)
        {
            case "2":
                displayStoresInformation();
                break;

            case "3":
                displaySystemItemsInformation();
                break;

            case "4":
                receiveOrderFromUser();
                break;

            case "5":
                displayAllStoresOrderHistory();
                break;
            case "6":
                updateStoreProducts();
                break;
            default:
                printUserMenuChoiceError();
                break;
        }
    }

    private void updateStoreProducts() {
        Scanner scn = new Scanner(System.in);
        String userInput;
        userInput = getAndValidateUpdateStoreProductInput(scn);
        performUpdateStoreProductsOfChoice(userInput, scn);
    }

    private void performUpdateStoreProductsOfChoice(String userInput, Scanner scn) {
        switch(userInput){
            case "1":
                updateStoreItemPrice(scn);
                break;
            case "2":
                addItemToStore(scn);
                break;
            case "3":
                removeItemFromStore(scn);
                break;
            default:
                printUserMenuChoiceError();
                break;
        }
    }

    private void removeItemFromStore(Scanner scn) {
        Store storeOfChoice = printAndGetStoreOfChoice(scn);
        StoreItem itemToRemove = getStoreItemToRemove(scn, storeOfChoice);

        if(itemToRemove != null) {
            this.SDMLogic.removeItemFromStore(itemToRemove, storeOfChoice);
            System.out.println("The item was removed successfully!\n");
        }
    }

    private StoreItem getStoreItemToRemove(Scanner scn, Store storeOfChoice) {
        System.out.println(storeOfChoice.getStringStoreItemsShort());
        String userInput;
        StoreItem itemToRemove = null;
        boolean isValidItemToRemove = false;

        while(!isValidItemToRemove) {
            System.out.println("Please select an item to remove");
            userInput = scn.nextLine();

            if(isNumber(userInput)) {
                int userInputInteger = Integer.parseInt(userInput);

                if(storeOfChoice.getItemsBeingSold().containsKey(userInputInteger)) {
                    itemToRemove = this.SDMLogic.getItems().get(userInputInteger);
                    isValidItemToRemove = true;
                }
                else {
                    System.out.println("<The item you chose is not sold by the store>\n");
                }
            }
            else {
                System.out.println("<The input is not an integer>\n");
            }
        }


        /*
            NEED TO SEE WHAT'S AVIAD'S APPROACH ABOUT THIS ! ! ! ! ! !
         */
        if(!(storeOfChoice.getItemsBeingSold().size() > 1)) {
            System.out.println("<This is the only item this store is selling, therefore it cannot be removed>\n");

        }
        else {
            if (this.SDMLogic.getItems().get(itemToRemove.getId()).getAmountOfStoresSellingThisItem() > 1) {
                return itemToRemove;
            }
            else {
                System.out.println("<This store is the only one selling this item, therefore it cannot be removed>\n");
            }
        }

        return null;
    }

    private void addItemToStore(Scanner scn) {
        Store storeOfChoice = printAndGetStoreOfChoice(scn);
        StoreItem itemToAdd = getSystemItemToAdd(scn, storeOfChoice);
        this.SDMLogic.addItemToStore(itemToAdd, storeOfChoice);
        /*
            this.SDMLogic.addItemToStore(itemToAdd, storeOfChoice);
            updates inside the - amount of stores selling the item
                               - average price of item in the system
         */
        System.out.println("The item was added successfully!\n");
    }

    private StoreItem getSystemItemToAdd(Scanner scn, Store storeOfChoice) {
        return getAndValidateSystemItemToAdd(scn, storeOfChoice);
    }

    private StoreItem getAndValidateSystemItemToAdd(Scanner scn, Store storeOfChoice) {
        displaySystemItemsInformation();
        String userInput;
        StoreItem itemToAdd = null;
        boolean isValidItemToAdd = false;

        while(!isValidItemToAdd) {
            System.out.println("Please select an item to add");
            userInput = scn.nextLine();

            if(isNumber(userInput)) {
                int userInputInteger = Integer.parseInt(userInput);

                if(this.SDMLogic.getItems().containsKey(userInputInteger)){
                    if(!storeOfChoice.getItemsBeingSold().containsKey(userInputInteger)) {
                        isValidItemToAdd = true;
                        itemToAdd = this.SDMLogic.getItems().get(userInputInteger);
                    }
                    else {
                        System.out.println("<The item you chose already exists in this store>\n");
                    }
                }
                else {
                    System.out.println("<The ID you chose does not belong to an existing item>\n");
                }
            }
            else {
                System.out.println("<The input is not an integer>\n");
            }
        }

        return itemToAdd;
    }

    private void updateStoreItemPrice(Scanner scn) {
        Store storeOfChoice = printAndGetStoreOfChoice(scn);
        StoreItem itemToUpdate = getItemToUpdate(scn, storeOfChoice);
        updateItemPrice(scn, itemToUpdate, storeOfChoice.getId());
        this.SDMLogic.updateAllStoresItemsAveragePricesAndAmountOfStoresSellingAnItem();
        System.out.println("The price was updated successfully!\n");
    }

    private void updateItemPrice(Scanner scn, StoreItem itemToUpdate, int storeOfChoiceId) {
        double newPrice = getAndValidatePrice(scn);
        this.SDMLogic.updatePriceOfAnItem(storeOfChoiceId, itemToUpdate.getId(), newPrice);
    }

    private double getAndValidatePrice(Scanner scn) {
        boolean isValidPrice = false;
        double priceOfChoice = 0;
        String userInput;

        while(!isValidPrice)
        {
            System.out.println("Please enter the new price of the item");
            userInput = scn.nextLine();

            if(isDouble(userInput)) {
                isValidPrice = true;
                priceOfChoice = Double.parseDouble(userInput);
            }
            else {
                System.out.println("<The input you entered is not a number>");
            }
        }

        return priceOfChoice;
    }

    private StoreItem getItemToUpdate(Scanner scn, Store storeOfChoice) {
        return getAndValidateItemById(scn, storeOfChoice);

    }

    private StoreItem getAndValidateItemById(Scanner scn, Store storeOfChoice) {
        boolean isValidItemId = false;
        StoreItem chosenItem = null;
        String userInput;
        printStoreItemsForUpdate(storeOfChoice);

        while(!isValidItemId) {
            System.out.println("Please choose an item by it's id");
            userInput = scn.nextLine().trim();

            if(isNumber(userInput)) {
                if(storeOfChoice.getItemsBeingSold().containsKey(Integer.parseInt(userInput))) {
                    chosenItem = storeOfChoice.getItemsBeingSold().get(Integer.parseInt(userInput));
                    isValidItemId = true;
                }
                else {
                    System.out.println("<The ID you entered does not exist in the current store>\n");
                }
            }
            else {
                System.out.println("<The input you entered is not an integer>\n");
            }
        }
        
        return chosenItem;
    }

    private void printStoreItemsForUpdate(Store storeOfChoice) {
        StringBuilder allStoreItems = new StringBuilder();
        storeOfChoice.getItemsBeingSold().values().forEach( item ->  allStoreItems.append(item.getStringItemForPurchase()));
        System.out.println(allStoreItems.toString());
    }

    private Store printAndGetStoreOfChoice(Scanner scn) {
        StringBuilder allStoresToChooseFrom = new StringBuilder();
        allStoresToChooseFrom.append("\n-----\tStores to choose from\t-----\n");
        this.SDMLogic.getStores().values().forEach(store ->  allStoresToChooseFrom.append(store.displayStoreForPurchase()));
        System.out.println(allStoresToChooseFrom.toString());
        return getStoreOfChoiceByID(scn);
    }

    private String getAndValidateUpdateStoreProductInput(Scanner scn) {
        String userInput = null;
        int integerUserInput;
        boolean isValidInput = false;
        while(!isValidInput) {

            printStoreUpdateMenu();
            userInput = scn.nextLine().trim();
            if(isNumber(userInput))
            {
                integerUserInput = Integer.parseInt(userInput);
                if(1 <= integerUserInput && integerUserInput <= 3) { isValidInput = true;}
                else { System.out.println("<Please enter a number between [1-3]>\n"); }
            }
            else {
                System.out.println("<The input is not a number>\n");
            }
        }
        
        return userInput;
    }

    private void printStoreUpdateMenu() {
        System.out.println("\tPlease choose one of the following options:");
        System.out.println("\t\t1.Update item price.");
        System.out.println("\t\t2.Add item to store.");
        System.out.println("\t\t3.Remove item from store.\n");
    }

    private void printUserMenuChoiceError() {
        System.out.println("<Something went terribly weird inside of the switch case in the [performActionOfChoice] method>");
    }

    private void receiveOrderFromUser() {
        Scanner sc = new Scanner(System.in);
        Store storeToOrderFrom;
        String userDateInput;
        Location userLocationInput;
        List<StoreItem> orderItems;

        displayShortPresentationOfSystemStores();
        storeToOrderFrom = getStoreOfChoiceByID(sc);
        userDateInput = getDateFromUserAndValidate(sc);
        userLocationInput = getLocationFromUserAndValidate(sc);
        displayAllSystemItemsForPurchaseByStore(storeToOrderFrom);
        orderItems = getAllItemsOfOrder(sc, storeToOrderFrom);

        if(orderItems.size() > 0){
            displayLastOrder(orderItems, storeToOrderFrom, userLocationInput);

            if(isOrderApproved(sc)){
                this.SDMLogic.updateStoreAndSystemItemAmountInformationAccordingToNewOrder(orderItems, storeToOrderFrom);
                storeToOrderFrom.generateOrder(userDateInput, SDMLogic.getLastOrderID(), orderItems, userLocationInput, userLocationInput);
            }
            else { System.out.println("\nThe order was cancelled!\n"); }
        }
        else { System.out.println("\nThe order was cancelled!\n"); }
    }

    private void displayLastOrder(List<StoreItem> orderItems, Store storeToOrderFrom, Location userLocationInput) {
        StringBuilder orderStringBuilder = new StringBuilder();
        double distanceFromUser = storeToOrderFrom.calculateDistance(userLocationInput);
        int ppk = storeToOrderFrom.getDeliveryPpk();

        orderItems.forEach(itemOrdered -> {
            orderStringBuilder.append(itemOrdered.getStringItemForPurchase());
            orderStringBuilder.append("\t\tAmount Bought: ").append(itemOrdered.getTotalItemsSold()).append("\n");
            orderStringBuilder.append("\t\tTotal Price: ").append(itemOrdered.getTotalItemsSold() * itemOrdered.getPricePerUnit()).append("\n");
        });

        orderStringBuilder.append("Distance From Destination: ").append(String.format("%.2f", distanceFromUser)).append("\n");
        orderStringBuilder.append("PPK: ").append(ppk).append("\n");
        orderStringBuilder.append("Total Cost Of Delivery: ").append(String.format("%.2f", distanceFromUser * ppk)).append("\n");

        System.out.println(orderStringBuilder.toString());
    }

    private boolean isOrderApproved(Scanner sc) {
        boolean isInputValid = false;
        boolean acceptOrder = false;
        String userInput;
        while(!isInputValid) {
            System.out.println("Do you approve the order? Please answer by typing 'Y' / 'N'");
            userInput = sc.nextLine().trim().toUpperCase();
            if (userInput.equals("Y")){ isInputValid = true; acceptOrder = true; }
            else if (userInput.equals("N")) { isInputValid = true; }
            else { System.out.println("Please enter 'Y' or 'N'"); }
        }

        return acceptOrder;
    }

    private List<StoreItem> getAllItemsOfOrder(Scanner sc, Store storeToOrderFrom) {
        String userInput;
        boolean orderIsFinished = false;
        List<StoreItem> orderItems = new ArrayList<>();
        while(!orderIsFinished){
            try {
                System.out.println("Please choose an item by it's ID. PRESS 'q' TO FINISH THE ORDER");
                userInput = sc.nextLine();
                if(validateOrderInput(storeToOrderFrom.getItemsBeingSold(), userInput)) {
                    if(userInput.equals("q")){ orderIsFinished = true; }
                    else { orderItems.add(handleAmountForOneItem(sc, Integer.parseInt(userInput), storeToOrderFrom)); }
                }
            }
            catch(Exception e) { System.out.println(e.getMessage()); }
        }

        return orderItems;
    }

    private StoreItem handleAmountForOneItem(Scanner sc, int userInput, Store storeToOrderFrom) {
        StoreItem sItem = storeToOrderFrom.getItemsBeingSold().get(userInput);
        StoreItem item = new StoreItem(sItem);
        item.setTotalItemsSold(getAmountOfItems(sc, item));
        return item;
    }

    private double getAmountOfItems(Scanner sc, StoreItem itemToBuy) {
        double doubleUserInput = 0;
        boolean isInputValid = false;
        String userInput;

        while(!isInputValid){
            System.out.format("How many %s would you like to order?\n", itemToBuy.getName());
            userInput = sc.nextLine();

            if(itemToBuy.getPurchaseCategory().equals("Quantity")) {
                if (isNumber(userInput)){
                    doubleUserInput = Integer.parseInt(userInput);
                    if ( doubleUserInput >= 1) { isInputValid = true; }
                }
                else { System.out.println("<The input you entered is not an integer>"); }
            }
            else {
                if (isDouble(userInput)){
                    doubleUserInput = Double.parseDouble(userInput);
                    if ( doubleUserInput > 0) { isInputValid = true; }
                }
                else { System.out.println("<The input you entered is not a number>"); }
            }
        }

        return doubleUserInput;
    }

    private boolean isDouble(String userInput) {
        try {
            Double.parseDouble(userInput);
            return true;
        }
        catch(NumberFormatException e) { return false;}
    }

    private boolean validateOrderInput(Map<Integer, StoreItem> itemsBeingSold, String userInput) throws Exception {
        try
        {
            if (userInput.equals("q")) { return true; }
            else
            {
                if(isNumber(userInput)) {
                    if(itemsBeingSold.containsKey(Integer.parseInt(userInput))) { return true; }
                    else { throw new IllegalInputException("<The item ID you entered is not sold by the store>"); }
                }
            }
        }
        catch (NumberFormatException e) { throw new Exception("<The input was not 'q', nor a number>"); }

        return false; //The method never goes this far!
    }

    private boolean isNumber(String inputString) {
        try {
            Integer.parseInt(inputString);
            return true;
        }
        catch(NumberFormatException e) { return false;}
    }

    private void displayAllSystemItemsForPurchaseByStore(Store storeToOrderFrom) {
        System.out.println("-----\tItems Available\t-----\n");
        this.SDMLogic.getItems().values().forEach(itemInSystem -> {
            if (storeToOrderFrom.getItemsBeingSold().containsKey(itemInSystem.getId())) {
                System.out.println(storeToOrderFrom.getItemsBeingSold().get(itemInSystem.getId()).getStringItemForPurchase());
            }
            else { System.out.println(itemInSystem.getStringItemForPurchaseWithNotSoldProperty()); }
        });
    }

    private Store getStoreOfChoiceByID(Scanner sc) {
        while (true)
        {
            try {
                String userStoreIDInput;
                System.out.println("Please choose a store using it's ID");
                userStoreIDInput = sc.nextLine();
                if(isValidStoreChoice(userStoreIDInput))
                {
                    return this.SDMLogic.getStores().get(Integer.parseInt(userStoreIDInput));
                }
                else {
                    System.out.println("<The input you entered wasn't an existing store's ID>\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("<The input you entered wasn't a number>\n");
            }
        }
    }

    private boolean isValidStoreChoice(String userStoreIDInput) {
        int storeID;
        storeID = Integer.parseInt(userStoreIDInput);
        return this.SDMLogic.getStores().containsKey(storeID);
    }

    private Location getLocationFromUserAndValidate(Scanner sc) {
        String userLocationInput, xString, yString;
        int x = 0,y = 0,indexOfSeparation;
        boolean isLocationValid = false;
        final String regex = "\\((-?\\d+)\\,(-?\\d+)\\)";
        final Pattern pattern = Pattern.compile(regex);

        while (!isLocationValid) {

            System.out.println("Please enter your location in the following format (x,y) where 1 <= x,y <= 50\n");
            userLocationInput = sc.nextLine().trim();

            final Matcher matcher = pattern.matcher(userLocationInput);

            if (matcher.matches()) {
                indexOfSeparation = userLocationInput.indexOf(",");
                xString = userLocationInput.substring(1, indexOfSeparation);
                yString = userLocationInput.substring(indexOfSeparation + 1, userLocationInput.length() - 1);
                x = Integer.parseInt(xString);
                y = Integer.parseInt(yString);
                if(validateLocationBorders(x, y)) {
                    if(validateLocationAgainstAllStores(x, y)) {
                        isLocationValid = true;
                    }
                    else {
                        System.out.println("<Your location cannot be the same as a an existing store's location>");
                    }
                }
                else { System.out.println("<Please enter x,y values that meet with the requirements 1 <= x,y <= 50>"); }
            }
        }

        Location outLocation = new Location();
        outLocation.setX(x);
        outLocation.setY(y);
        return outLocation;
    }

    private boolean validateLocationAgainstAllStores(int x, int y) {
        return SDMLogic.checkUserLocationAgainstAllStoresLocations(x, y);
    }

    private boolean validateLocationBorders(int x, int y) {
        return (1 <= x && x <= 50) && (1 <= y && y <= 50);
    }

    private void displayShortPresentationOfSystemStores() {
        System.out.println("-----\tStores Available\t-----\n");
        this.SDMLogic.getStores().forEach((integer, store) -> System.out.println(store.displayStoreForPurchase()));
    }

    private String getDateFromUserAndValidate(Scanner scanner) {
        String userDateInput;
        //regular expression string represent a match for the following structure dd/mm-hh:mm
        String regexString = "^((0[1-9])|([12][0-9])|(3[01]))/([0]?[1-9]|1[012])-([0-1]?[0-9]|2?[0-3]):([0-5]\\d)$";
        Pattern regex = Pattern.compile(regexString);
        System.out.println("Please enter the delivery date in the following format - dd/mm-hh:mm");
        userDateInput = scanner.nextLine().trim();
        Matcher match = regex.matcher(userDateInput);
        while (!match.matches()){

            System.out.println("Wrong format. Please use the following format -> dd/mm-hh:mm");
            userDateInput = scanner.nextLine().trim();
            match = regex.matcher(userDateInput);
        }

        return userDateInput;
    }

    private void displayAllStoresOrderHistory() {
        Map<Integer, Store> systemStores = SDMLogic.getStores();
        AtomicInteger orderCounter = new AtomicInteger();
        StringBuilder orderHistory = new StringBuilder();
        System.out.println("-----   System Order History    -----\n");
        systemStores.values().forEach(store -> store.getStoreOrdersHistory().forEach(order -> {
            orderHistory.append(order.toString());
            orderCounter.addAndGet(store.getStoreOrdersHistory().size());
        }));

        if(orderCounter.get() > 0) {
            System.out.println(orderHistory.toString());
        }
        else { System.out.println("\tCurrently no orders were made in the system\n"); }
    }

    private void displayStoresInformation() {
        Map<Integer, Store> systemStores = SDMLogic.getStores();
        systemStores.forEach((id, store) -> System.out.println(store.toString()));
    }

    private void displaySystemItemsInformation(){
        System.out.println("-----   System Items    -----\n");
        Map<Integer, StoreItem> systemStores = SDMLogic.getItems();
        systemStores.forEach((id, item) -> System.out.println(item.getStringItemForAllSystemItemsDisplay()));
    }

    private class IllegalInputException extends Exception{
        public IllegalInputException(String message) {
            super(message);
        }
    }

}
