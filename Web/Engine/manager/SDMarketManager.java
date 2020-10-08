package manager;

import SDMImprovedFacade.*;
import SuperMarketLogic.SuperMarketLogic;


import java.util.*;
import java.util.stream.Collectors;

public class SDMarketManager {
    public static final String CUSTOMER = "customer";
    private final HashMap<String, User> systemUsersMap = new HashMap<>();
    SuperMarketLogic SDMLogic = new SuperMarketLogic();
    private int userID = 1;

    public SuperMarketLogic getSDMLogic() {
        return SDMLogic;
    }

    public synchronized void addUser(String username, String userType) {
        if (!systemUsersMap.containsKey(username)) {
            if(userType.equals(CUSTOMER)) { systemUsersMap.put(username, new Customer(userID, username, userType)); }
            else { systemUsersMap.put(username, new ShopOwner(userID, username, userType)); }
            userID += 1;
        }
    }

    public synchronized User getUser(String username) {
        return systemUsersMap.get(username);
    }

    public synchronized void removeUser(String username) {
        systemUsersMap.remove(username);
    }

    public synchronized HashMap<String, User> getUsers() {
        return (HashMap<String, User>) Collections.unmodifiableMap(systemUsersMap);
    }

    public boolean isUserExists(String username) {
        return systemUsersMap.containsKey(username);
    }

    public Map<String, Zone> getSystemZones() {
        return this.SDMLogic.getSystemZones();
    }

    public List<User> getOtherUsers(String currentUserName) {
        return this.systemUsersMap.values().stream().filter(user -> (!user.getName().equals(currentUserName))).collect(Collectors.toList());
    }

    public Map<Integer, StoreItem> getSystemItems() {
        return this.SDMLogic.getItems();
    }
}
