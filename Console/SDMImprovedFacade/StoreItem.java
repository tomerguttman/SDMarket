package SDMImprovedFacade;

import jaxb.generatedClasses.SDMItem;
import jaxb.generatedClasses.SDMSell;

public class StoreItem {
    private final int Id;
    private double totalItemsSold = 0;
    private int amountOfStoresSellingThisItem = 0;
    private double averagePriceOfTheItem;
    private double pricePerUnit = 0;
    private final String name;
    private final String purchaseCategory;

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

    public StoreItem(StoreItem sItem) {
        this.Id = sItem.Id;
        this.name = sItem.name;
        this.pricePerUnit = sItem.pricePerUnit;
        this.purchaseCategory = sItem.purchaseCategory;
    }

    public void setTotalItemsSold(double totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getId() {
        return Id;
    }

    public double getTotalItemsSold() {
        return totalItemsSold;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public String getName() {
        return name;
    }

    public int getAmountOfStoresSellingThisItem() {
        return amountOfStoresSellingThisItem;
    }

    public void setAmountOfStoresSellingThisItem(int amountOfStoresSellingThisItem) {
        this.amountOfStoresSellingThisItem = amountOfStoresSellingThisItem;
    }

    public double getAveragePriceOfTheItem() {
        return averagePriceOfTheItem;
    }

    public void setAveragePriceOfTheItem(double averagePriceOfTheItem) {
        this.averagePriceOfTheItem = averagePriceOfTheItem;
    }

    public String getPurchaseCategory() {
        return purchaseCategory;
    }

    public String getStringItemForPurchase(){
        return "\tItem ID: " + Id + "\n" +
                "\t\tItem Name: " + name + "\n" +
                "\t\tPurchase Category: " + purchaseCategory +"\n" +
                "\t\tPrice Per Unit: " + pricePerUnit + "\n";
    }

    public String getStringItemForPurchaseWithNotSoldProperty(){
        return "\tItem ID: " + Id + "\n" +
                "\t\tItem Name: " + name + "\n" +
                "\t\tPurchase Category: " + purchaseCategory +"\n" +
                "\t\t-THIS ITEM IS NOT BEING SOLD BY THE STORE YOU CHOSE-\n";
    }

    public String getStringItemForAllSystemItemsDisplay(){
        return  "\tItem ID: " + Id + "\n" +
                "\t\tItem Name: " + name + "\n" +
                "\t\tPurchase Category: " + purchaseCategory +"\n" +
                "\t\tAmount Of Stores Selling The Item: " + amountOfStoresSellingThisItem + "\n" +
                "\t\tAverage Price In System: " + averagePriceOfTheItem + "\n" +
                "\t\tAmount Of Units Sold: " + totalItemsSold + "\n";
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
