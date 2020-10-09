package SDMImprovedFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopOwner extends User {

    HashMap<String, Zone> zonesOwned;
    HashMap<Integer, Store> storesOwned;
    List<Feedback> feedbackList;

    public ShopOwner(int userID, String username, String userType) {
        super(username, userType, userID);
        zonesOwned = new HashMap<>();
        storesOwned = new HashMap<>();
        feedbackList = new ArrayList<>();
    }

    public HashMap<String, Zone> getZoneOwned() {
        return zonesOwned;
    }

    public HashMap<Integer, Store> getStoresOwned() {
        return storesOwned;
    }

    public double getAverageRating() {
        double sumRating = 0;
        for (Feedback feedback : feedbackList) {
            sumRating += feedback.getRating();
        }

        return (feedbackList.size() != 0) ? (sumRating / feedbackList.size()) : 0 ;
    }
}
