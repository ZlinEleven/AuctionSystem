import java.io.Serializable;

public class Auction implements Serializable{
    private int timeRemaining;
    private double currentBid;
    private String auctionID;
    private String sellerName;
    private String buyerName;
    private String itemInfo;

    public Auction(String id, double bid, String seller, String buyer, int time, String itemInfo){
        auctionID = id;
        currentBid = bid;
        sellerName = seller;
        buyerName = buyer;
        timeRemaining = time;
        this.itemInfo = itemInfo;
    }

    public int getTimeRemaining(){
        return timeRemaining;
    }

    public double getCurrentBid(){
        return currentBid;
    }

    public String getAuctionID(){
        return auctionID;
    }

    public String getSellerName(){
        return sellerName;
    }

    public String getBuyerName(){
        return buyerName;
    }

    public String getItemInfo(){
        return itemInfo;
    }

    public void decrementTimeRemaining(int time){
        timeRemaining -= time;

        if(timeRemaining < 0){
            timeRemaining = 0;
        }
    }

    public void newBid(String bidderName, double bidAmt) throws ClosedAuctionException{
        if(timeRemaining == 0){
            throw new ClosedAuctionException("Auction is over.");
        }

        if(bidAmt > currentBid){
            currentBid = bidAmt;
            buyerName = bidderName;
            System.out.println("Bid accepted.");
        }
        else{
            System.out.println("Bid rejected.");
        }
    }

    public String toString(){
        String bid = "";
        if(currentBid > 0){
            bid = String.format("%,.2f", currentBid);
        }
        return String.format("%-12s%3s%9s%2s%-23s%1s%-24s%1s%10s%2s%2s", "  " + auctionID, "|  $", bid, "|", " " + sellerName, "|", " " + buyerName, "|", " " + timeRemaining + " hours", "|", " " + itemInfo.substring(0, Math.min(itemInfo.length(), 43)));
    }
}