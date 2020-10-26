package SDMImprovedFacade;

import java.util.*;
import java.util.stream.Collectors;

public class ShopOwner extends User {

    HashMap<String, Zone> zonesOwned;
    HashMap<String, Store> storesOwned; //String -> store name
    Map<String, List<Feedback>> feedbackMapPerZone;

    public ShopOwner(int userID, String username, String userType) {
        super(username, userType, userID);
        zonesOwned = new HashMap<>();
        storesOwned = new HashMap<>();
        feedbackMapPerZone = new HashMap<>();
    }

    public HashMap<String, Zone> getZoneOwned() {
        return zonesOwned;
    }

    public HashMap<String, Store> getStoresOwned() {
        return storesOwned;
    }

    public double getAverageRating() {
        double sumRating = 0, amountOfFeedbacks = 0;

        for (List<Feedback> feedbackOfZone : feedbackMapPerZone.values()) {
            for(Feedback feedback : feedbackOfZone){
                sumRating += feedback.getRating();
                amountOfFeedbacks += 1;
            }
        }

        //sumRating must be at least 1 !
        return (amountOfFeedbacks != 0) ? (sumRating / amountOfFeedbacks) : 0 ;
    }

    public void addZoneToShopOwner(Zone zoneToAdd) {
        this.zonesOwned.put(zoneToAdd.getZoneName().replaceAll("\\s+",""), zoneToAdd);
        zoneToAdd.getStoresInZone().forEach((storeId, store) ->{
            this.storesOwned.put(store.getName(), store);
        });
    }

    public List<Feedback> getZoneFeedbacks(String currentZoneName) {
        return feedbackMapPerZone.getOrDefault(currentZoneName, null);
    }

    public List<Store> getZoneStores(String currentZoneName) {
        return zonesOwned.containsKey(currentZoneName) ? new ArrayList<>(zonesOwned.get(currentZoneName).getStoresInZone().values()) : null;
    }

    public List<StoreItem> getZoneItems(String currentZoneName) {
        return zonesOwned.containsKey(currentZoneName) ? new ArrayList<>(zonesOwned.get(currentZoneName).getItemsAvailableInZone().values()) : null;
    }

    public void addStoreToUser(String currentZoneName, Store newStoreToAdd) {
        this.zonesOwned.get(currentZoneName).getStoresInZone().put(newStoreToAdd.getId(), newStoreToAdd); // the amount of stores in the zone was already updated ! the shopOwner and the system have the same reference to the zones
        this.zonesOwned.get(currentZoneName).initializeAveragePriceOfItemAndAmountOfStoresSellingAnItem();
        this.storesOwned.put(newStoreToAdd.getName(), newStoreToAdd);
        /*
        Alerts?
        Alerts?
        Alerts?
        Alerts?
        Alerts?
         */
    }

    public void addFeedback(String zoneName, Feedback feedback) {
        if(!this.feedbackMapPerZone.containsKey(zoneName)) {
            this.feedbackMapPerZone.put(zoneName, new ArrayList<Feedback>());
        }

        this.feedbackMapPerZone.get(zoneName).add(feedback);
    }
}
