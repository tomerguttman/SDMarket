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

                if(validateMenuUserInputChoice(userMenuChoice)) {
                    if(userMenuChoice.equals("q")){
                        exitFlag = true;
                        System.out.println("Thank you for using our SDM system. Come again.");
                    }
                    else if(userMenuChoice.equals("1")){
                        System.out.println("Please enter a path to the .xml file in order to load it.");
                        dataWasLoaded = SDMLogic.loadData(scn.nextLine().trim(), outputMessage) || dataWasLoaded;
                        System.out.println("\n" + outputMessage.toString() + "\n");
                    }
                    else {
                        if(dataWasLoaded) {
                            performActionOfChoice(userMenuChoice);
                        }
                        else {
                            System.out.println("\n<You can't perform this action without loading a system data file first>\n");
                        }
                    }
                }
                else {
                    System.out.println("\n<Please choose one of the options available between [1 - 5] or 'q' to exit the system>\n");
                }
            }
            catch (IllegalInputException | JAXBException e) {
                System.out.println(e.getMessage()); // and re iterate - need to be changed?
            }
        }
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
        String userDateInput;
        Location userLocationInput;
        
        displayShortPresentationOfSystemStores();
        userDateInput = getDateFromUserAndValidate(sc);
        userLocationInput = getLocationFromUserAndValidate(sc);
        /*
            THIS METHOD NEED TO BE IMPLEMENTED ! ! !
        */
    }

    private Location getLocationFromUserAndValidate(Scanner sc) {
        String userLocationInput;
        int x = 0,y = 0,indexOfSeparation;
        boolean isLocationValid = false;
        System.out.println("Please enter your location in the following format x,y where 1 <= x,y <= 50\n");

        while (!isLocationValid) {

            /*
                This whole section need to be parsed first ! ! !
            */

            userLocationInput = sc.nextLine();
            indexOfSeparation = userLocationInput.indexOf(",");
            x = Integer.parseInt(userLocationInput.substring(0,indexOfSeparation));
            y = Integer.parseInt(userLocationInput.substring(indexOfSeparation,userLocationInput.length()));

            isLocationValid = validateLocation(x, y);
        }

        Location outLocation = new Location();
        outLocation.setX(x);
        outLocation.setY(y);
        return outLocation;
    }

    private boolean validateLocation(int x, int y) {
        boolean isLocationValid = (1 <= x && x <= 50) && (1 <= y && y <= 50);
        if(isLocationValid){
            return SDMLogic.checkUserLocationAgainstAllStoresLocations(x, y);
        }

        return false;
    }

    private void displayShortPresentationOfSystemStores() {
        System.out.println("-----\tStores Available\t-----\n");
        this.SDMLogic.getStores().forEach((integer, store) -> store.displayStoreForPurchase());
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

    private class IllegalInputException extends Exception{
        public IllegalInputException(String message) {
            super(message);
        }
    }
}
