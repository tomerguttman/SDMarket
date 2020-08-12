package SDMImprovedFacade;

import jaxb.generatedClasses.SDMItem;
import jaxb.generatedClasses.SDMSell;

public class StoreItem {
    private final int Id;
    private int totalItemsSold = 0;
    private double pricePerUnit = 0;
    private final String name;
    private final String purchaseCategory;

    public StoreItem(int id, String name, String purchaseCategory) {
        this.Id = id;
        this.name = name;
        this.purchaseCategory = purchaseCategory;
    }

    public StoreItem(SDMItem inputItem, SDMSell inputPrice){
        this.Id = inputItem.getId();
        this.name = inputItem.getName();
        this.purchaseCategory = inputItem.getPurchaseCategory();
        this.pricePerUnit = inputPrice.getPrice();
    }

    public StoreItem(SDMItem inputItem) {
        this.Id = inputItem.getId();
        this.name = inputItem.getName();
        this.purchaseCategory = inputItem.getPurchaseCategory();
    }

    public void setTotalItemsSold(int totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getId() {
        return Id;
    }

    public int getTotalItemsSold() {
        return totalItemsSold;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public String getName() {
        return name;
    }

    public String getPurchaseCategory() {
        return purchaseCategory;
    }

    @Override
    public String toString() {
        return  "\tItem ID: " + Id + "\n" +
                "\t\tItem Name: " + name + "\n" +
                "\t\tPurchase Category: " + purchaseCategory +"\n" +
                "\t\tPrice Per Unit: " + pricePerUnit + "\n" +
                "\t\tAmount Of Units Sold: " + totalItemsSold + "\n";
    }
}
