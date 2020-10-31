package SDMImprovedFacade;

public class Transaction {
    private final String type;
    private final String date;
    private final double amount;
    private final double balanceBefore;
    private final double balanceAfter;

    public Transaction(String type, String dateOfTransaction, double amount, double balanceBefore) {
        this.type = type;
        this.date = dateOfTransaction;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceBefore + amount;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }
}
