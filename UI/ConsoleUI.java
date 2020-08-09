

package UI;
import Engine.SuperMarketLogic;
import jaxb.generatedClasses.*;
import javax.xml.bind.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private SuperMarketLogic SDMLogic;
    private boolean dataLoaded = false;


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
        System.out.println("6.Exit");
    }


    private boolean validateMenuUserInputChoice(String userInput) {
        try {
            int userInputParsed = Integer.parseInt(userInput);
            return 1 <= userInputParsed && userInputParsed <= 6;
        }
        catch (NumberFormatException e) {
            throw (new NumberFormatException("<NumberFormatException: the input you entered is not a number>"));
        }
    }

    private void run() {
        SDMLogic = new SuperMarketLogic();
        Scanner scn = new Scanner(System.in);
        String userMenuChoice;
        boolean exitFlag = false;

        printMenu();
        userMenuChoice = scn.nextLine();
        while (!exitFlag) {
            try {
                if(validateMenuUserInputChoice(userMenuChoice)) //if returns false then userMenuChoise is not in range.
                {
                    if(userMenuChoice.equals("6")){
                        exitFlag = true;
                    }
                    else if(userMenuChoice.equals("1")){
                        //Handle path of data input from user.
                        //SDMLogic.loadData(); //need to be implemented in SuperMarketLogic CLASS ! ! !
                        dataLoaded = true;
                    }
                    else
                    {
                        if(dataLoaded)
                        {
                            //send to switch case in case data was loaded.
                        }
                        else {
                            System.out.println("<You can't perform this action without loading a system data file first>");
                        }
                    }
                }
                else
                {
                    System.out.println("<Please choose one of the options available between [1 - 6]>");
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println(e.getMessage()); // and re iterate
            }

        }
    }












}
