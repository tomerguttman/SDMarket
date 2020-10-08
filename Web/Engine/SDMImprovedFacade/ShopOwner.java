package SDMImprovedFacade;

import java.util.HashMap;

public class ShopOwner extends User {

    HashMap<String, Zone> zonesOwned;
    HashMap<Integer, Store> storesOwned;

    public ShopOwner(int userID, String username, String userType) {
        super(username, userType, userID);
        zonesOwned = new HashMap<>();
        storesOwned = new HashMap<>();
    }

    public HashMap<String, Zone> getZoneOwned() {
        return zonesOwned;
    }

    public HashMap<Integer, Store> getStoresOwned() {
        return storesOwned;
    }
}
