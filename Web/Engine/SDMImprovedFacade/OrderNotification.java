package SDMImprovedFacade;

public class OrderNotification extends Notification {
    private final int orderId;
    private final int totalAmountOfItemTypes;
    private final double totalItemsCost;
    private final double totalDeliveryCost;

    public OrderNotification(String originUserName, String receiverUserName, String subject, int orderId,
                             int totalAmountOfItemTypes, double totalItemsCost, double totalDeliveryCost, String dateOfNotification) {
        super(originUserName, receiverUserName, subject, dateOfNotification, "orderNotification");
        this.orderId = orderId;
        this.totalAmountOfItemTypes = totalAmountOfItemTypes;
        this.totalItemsCost = totalItemsCost;
        this.totalDeliveryCost = totalDeliveryCost;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getTotalAmountOfItemTypes() {
        return totalAmountOfItemTypes;
    }

    public double getTotalItemsCost() {
        return totalItemsCost;
    }

    public double getTotalDeliveryCost() {
        return totalDeliveryCost;
    }
}
