package UI;

import Engine.SuperMarketLogic;
import SDMImprovedFacade.Order;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import com.sun.deploy.util.StringUtils;
import com.sun.xml.internal.ws.util.StreamUtils;
import jaxb.generatedClasses.Location;
import org.omg.PortableInterceptor.USER_EXCEPTION;

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
        System.out.println("\nPress 'q' at any time to exit the system");
    }

    private boolean validateMenuUserInputChoice(String userInput) throws IllegalInputException {
        try {
            int userInputParsed = Integer.parseInt(userInput);
            return 1 <= userInputParsed && userInputParsed <= 5;
        }
        catch (NumberFormatException e) {
            if(userInput.toLowerCase().equals("q")){
                return true;
            }
            throw (new IllegalInputException("<IllegalInputException: The input you entered is not a number or 'q'>\n"));
        }
    }

    private void run() {
        //D:\Java - SDM\SDM_ConsoleApp\src\ex1-small.xml
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
                else { System.out.println("\n<Please choose one of the options available between [1 - 5] or 'q' to exit the system>\n"); }
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
            default:
                printUserMenuChoiceError();
                break;
        }
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
        displayLastOrder(storeToOrderFrom);

        if(isOrderApproved(sc)){
            this.SDMLogic.updateStoreAndSystemItemAmountInformationAccordingToNewOrder(orderItems, storeToOrderFrom);
            storeToOrderFrom.generateOrder(userDateInput, SDMLogic.getLastOrderID(), orderItems, userLocationInput, userLocationInput);
        }
        else { System.out.println("\nThe order was cancelled\n"); }
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

    private void displayLastOrder(Store storeToOrderFrom) {
        StringBuilder orderStringBuilder = new StringBuilder();
        Order lastOrder = storeToOrderFrom.getLastOrder();
        double distanceFromUser = storeToOrderFrom.calculateDistance(lastOrder.getOrderDestination());
        int ppk = storeToOrderFrom.getDeliveryPpk();

        orderStringBuilder.append(lastOrder.getStringWholeOrder());
        orderStringBuilder.append("Distance From Destination: ").append(String.format("%.2f", distanceFromUser)).append("\n");
        orderStringBuilder.append("PPK: ").append(ppk).append("\n");
        orderStringBuilder.append("Total Cost Of Delivery: ").append(lastOrder.getDeliveryCost()).append("\n");

        System.out.println(orderStringBuilder.toString());
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
        item.setTotalItemsSold(getAmountOfItems(sc, item.getName()));
        return item;
    }

    //<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
    // NEED TO CHANGE STRING TO ITEM AND THE CHECK WHICH PURCHASE CATEGORY IS IT AND HANDLE IT ....

    private int getAmountOfItems(Scanner sc, String name) {
        int intUserInput = 0;
        boolean isInputValid = false;
        String userInput;

        while(!isInputValid){
            System.out.format("How many %s would you like to order?\n", name);
            userInput = sc.nextLine();
            if (isNumber(userInput)){
                intUserInput = Integer.parseInt(userInput);
                if ( intUserInput >= 1) { isInputValid = true; }
            }
        }

        return intUserInput;
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
                System.out.println("Please enter the ID of the store you want to order from");
                userStoreIDInput = sc.nextLine();
                return this.SDMLogic.getStores().getOrDefault(Integer.parseInt(userStoreIDInput), null);
            } catch (NumberFormatException e) {
                System.out.println("<The input you entered wasn't a number representing a store ID>");
            }
        }
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
        systemStores.forEach((id, item) -> System.out.println(item.toString()));
    }

    private void allowXMLReloadAtAllTime(Scanner scn, StringBuilder stb) throws JAXBException {
        handleLoadDataFromXMLAction(scn, stb);
    }

    private class IllegalInputException extends Exception{
        public IllegalInputException(String message) {
            super(message);
        }
    }

}
