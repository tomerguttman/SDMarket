package Engine;

import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import SDMImprovedFacade.SuperDuperMarket;
import jaxb.generatedClasses.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;

public class SuperMarketLogic {
    private SuperDuperMarket SDMImproved;

    public boolean loadData(String filePath, StringBuilder outputMessage) throws JAXBException {
        boolean successFlag = true;
        try {
            if (filePath.isEmpty()) {
                updateOutputMessage(outputMessage,"<The input file path was empty, please enter a valid path>");
                successFlag = false;
            }

            if (filePath.length() >= 4 && filePath.substring(filePath.length() - 3).toLowerCase().equals("xml")) {
                File file = new File(filePath);
                if (file.exists()) {
                    SDMImproved = loadXML(file, outputMessage);
                } else {
                    updateOutputMessage(outputMessage,"<The file does not exist in the path that was given>");
                    successFlag = false;
                }
            } else {
                updateOutputMessage(outputMessage,"<The file type that was given is not .xml>");
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

            return validateSDMDataLoaded(temp, outputMessage) ? new SuperDuperMarket(temp) : SDMImproved ;
        } catch (JAXBException e) {
            throw new JAXBException(e.getMessage());
        }
    }

    private boolean validateSDMDataLoaded(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        try{
            updateOutputMessage(outputMessage,"<The data was loaded successfully from the XML file>");
            return isSDMItemsDataValid(SDMtoValidate, outputMessage) && isSDMStoresDataValid(SDMtoValidate, outputMessage);
        }
        catch (NullPointerException e){
            throw new NullPointerException("<One of the members in the system was Null>");
        }
        catch(IllegalArgumentException e){
            throw new IllegalArgumentException("<There was an error instantiating a data structure>");
        }
    }

    private boolean isSDMItemsDataValid(SuperDuperMarketDescriptor SDMtoValidate, StringBuilder outputMessage) {
        if( isItemsIdUnique(SDMtoValidate.getSDMItems())) {
            if( isItemBeingSoldByAtLeastOneStore(SDMtoValidate.getSDMItems().getSDMItem(), SDMtoValidate.getSDMStores().getSDMStore())) {
                return true;
            }
            else {
                updateOutputMessage(outputMessage,"<There is an item that is not being sold by any of the stores in the system>"); }
        }
        else {
            updateOutputMessage(outputMessage,"<There are two items with the same ID>"); }

        return false;
    }

    private boolean isItemBeingSoldByAtLeastOneStore(List<SDMItem> itemsToValidate, List<SDMStore> systemStores) {
        Set<Integer> allStoresItemsId = new HashSet<>();
        systemStores.forEach(store->store.getSDMPrices().getSDMSell().
                forEach(sellItem -> allStoresItemsId.add(sellItem.getItemId())));
        for (SDMItem item : itemsToValidate) {
            if(!allStoresItemsId.contains(item.getId())){
                return false;
            }
        }

        return true;
    }

    private boolean isItemsIdUnique(SDMItems itemsToValidate) {
        HashSet<Integer> hashSet = new HashSet<Integer>();

        for ( SDMItem item:  itemsToValidate.getSDMItem()) {
            if(!hashSet.contains(item.getId()))
            {
                hashSet.add(item.getId());
            }
            else { return false; }
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
                    }
                    else {
                        updateOutputMessage(outputMessage, "<One of the stores is selling an item twice>"); }
                }
                else { updateOutputMessage(outputMessage,"<One of the stores is selling items that are not recognized by the system>"); }
            }
            else { updateOutputMessage(outputMessage,"<There is a store whose coordinates are not in range [1-50]>"); }
        }
        else { updateOutputMessage(outputMessage,"<There are two stores with the same ID>"); }

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
                if (setOfItemsInStore.contains(itemBeingSold.getItemId())) { return false; }
                else { setOfItemsInStore.add(itemBeingSold.getItemId()); }
            }

            setOfItemsInStore.clear();
        }

        return true;
    }

    private boolean isStoreItemsExistInSystem(SDMStores storesToValidate , SDMItems systemItemsAvailable) {

        Set<Integer> setOfItemsInSystem = new HashSet<>();
        setOfItemsInSystem = listToSet(systemItemsAvailable.getSDMItem());

        for (SDMStore store : storesToValidate.getSDMStore()) {
            for (SDMSell sdmSellItem : store.getSDMPrices().getSDMSell()) {
                if(!setOfItemsInSystem.contains(sdmSellItem.getItemId())) { return false; }
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
            for ( SDMStore store: storesToValidate.getSDMStore() ) {

                if(!(store.getLocation().getX() <= 50 && store.getLocation().getX() >= 1 &&
                        store.getLocation().getY() >= 1 && store.getLocation().getY() <= 50))
                {
                    return false;
                }
            }

            return true;
        }
        catch (Exception e) { throw new NullPointerException("<NullPointerException: The list of stores in the system is NULL>"); }

    }

    private boolean isStoreIdUnique(SDMStores storesToValidate) {
        HashSet<Integer> hashSet = new HashSet<Integer>();

        for ( SDMStore store: storesToValidate.getSDMStore() ) {
            if(!hashSet.contains(store.getId()))
            {
                hashSet.add(store.getId());
            }
            else { return false; }
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
        boolean locationIsValidFlag;
        Map<Integer, Store> systemStores;
        Location userLocation = new Location();
        userLocation.setX(x);
        userLocation.setY(y);
        //iterate over the values and stop when one of the stores location equals to the user order location - return false because one of them matches.
        locationIsValidFlag = this.SDMImproved.getSystemStores().values().stream().noneMatch(store -> store.getStoreLocation().equals(userLocation));

        return locationIsValidFlag;
    }
}
