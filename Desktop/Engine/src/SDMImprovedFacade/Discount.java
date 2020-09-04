package SDMImprovedFacade;

import generatedClasses.*;
import java.util.ArrayList;
import java.util.List;

public class Discount {
    private String name;
    private IfBuy buyThis;
    private String itemToBuyName;
    private ThenGet getThat;

    public Discount(SDMDiscount discount) {
        this.name = discount.getName();
        this.buyThis = new IfBuy(discount.getIfYouBuy());
        this.getThat = new ThenGet(discount.getThenYouGet());
    }

    public void setItemToBuyName(String itemToBuyName) {
        this.itemToBuyName = itemToBuyName;
    }

    public String getName() {
        return name;
    }

    public IfBuy getBuyThis() {
        return buyThis;
    }

    public String getItemToBuyName() {
        return itemToBuyName;
    }

    public ThenGet getGetThat() {
        return getThat;
    }

    public class IfBuy {
        private int itemId;
        private double quantity;

        public IfBuy(IfYouBuy item){
            this.itemId = item.getItemId();
            this.quantity = item.getQuantity();
        }

        public int getItemId() {
            return itemId;
        }

        public double getQuantity() {
            return quantity;
        }
    }

    public class ThenGet {
        private String operator;

        List<Offer> offerList;

        public ThenGet(ThenYouGet sdmThenYouGet) {
            this.offerList = new ArrayList<>();
            this.operator = sdmThenYouGet.getOperator();
            for (SDMOffer sdmOffer : sdmThenYouGet.getSDMOffer()) {
                offerList.add(new Offer(sdmOffer));
            }
        }

        public List<Offer> getOfferList() {
            return offerList;
        }

        public String getOperator() {
            return operator;
        }

        public class Offer {
            private final int itemId;
            private final double quantity;
            private final int forAdditional;

            public Offer(SDMOffer offer) {
                this.itemId = offer.getItemId();
                this.quantity = offer.getQuantity();
                this.forAdditional = offer.getForAdditional();
            }

            public int getItemId() {
                return itemId;
            }

            public double getQuantity() {
                return quantity;
            }

            public int getForAdditional() {
                return forAdditional;
            }


        }
    }
}
