package SDMImprovedFacade;

import java.util.List;

public class UserJsonObject {
    private List<Zone> systemZones;
    private List<Transaction> userTransactions;
    private List<User> otherUsers;

    public UserJsonObject(List<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers) {
        this.systemZones = systemZones;
        this.userTransactions = userTransactions;
        this.otherUsers = otherUsers;
    }

    public List<Zone> getSystemZones() {
        return systemZones;
    }

    public void setSystemZones(List<Zone> systemZones) {
        this.systemZones = systemZones;
    }

    public List<Transaction> getUserTransactions() {
        return userTransactions;
    }

    public void setUserTransactions(List<Transaction> userTransactions) {
        this.userTransactions = userTransactions;
    }

    public List<User> getOtherUsers() {
        return otherUsers;
    }

    public void setOtherUsers(List<User> otherUsers) {
        this.otherUsers = otherUsers;
    }
}

