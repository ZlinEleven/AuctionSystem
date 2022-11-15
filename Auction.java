/*Zhenbin Lin, 114866923, Recitation 04 */

import java.io.Serializable;

/**
 * This class represents an auction object that contains information for an auction.
 * Attributes include auction id, remaining time till auction close, highest bid, 
 * user who placed the highest bid, seller name, and item info
 */
public class Auction implements Serializable{
    private int timeRemaining;
    private double currentBid;
    private String auctionID;
    private String sellerName;
    private String buyerName;
    private String itemInfo;

    /**
     * Constructor for an Auction object. 
     * @param id The id of the auction
     * @param bid Highest bid on the auction
     * @param seller User selling the item
     * @param buyer User who placed the highest bid
     * @param time Time remaining till auction closes
     * @param itemInfo Short description of the item
     */
    public Auction(String id, double bid, String seller, String buyer, int time, String itemInfo){
        auctionID = id;
        currentBid = bid;
        sellerName = seller;
        buyerName = buyer;
        timeRemaining = time;
        this.itemInfo = itemInfo;
    }

    /**
     * @return Time remaining (in hours) till auction closes
     */
    public int getTimeRemaining(){
        return timeRemaining;
    }

    /**
     * @return The highest bid on the item
     */
    public double getCurrentBid(){
        return currentBid;
    }

    /**
     * @return The id of the auction
     */
    public String getAuctionID(){
        return auctionID;
    }

    /**
     * @return The seller of the auction
     */
    public String getSellerName(){
        return sellerName;
    }

    /**
     * @return User who placed the highest bid
     */
    public String getBuyerName(){
        return buyerName;
    }

    /**
     * @return A short description of the item
     */
    public String getItemInfo(){
        return itemInfo;
    }

    /**
     * Decreases the amount of time remaining in hours. If time is greater than timeRemaining, timeRemaining will be set to 0
     * @param time The amount of hours to decrease the auction's open time for
     */
    public void decrementTimeRemaining(int time){
        timeRemaining -= time;

        if(timeRemaining < 0){
            timeRemaining = 0;
        }
    }

    /**
     * Creates a new bid for the auction. If bidAmt is greater than currentBid, currentBid will be set to bidAmt and buyerName will be set to bidderName.
     * If bidAmt is less than currentBid, nothing will happen
     * @param bidderName The name of the user placing the bid
     * @param bidAmt The amount to place the new bid at
     * @throws ClosedAuctionException If timeRemaining for the auction is 0, throw an exception
     */
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
            System.out.println("bid was not accepted.");
        }
    }

    /**
     * Returns a neatly formatted string representation of the auction
     */
    public String toString(){
        String bid = "";
        if(currentBid > 0){
            bid = String.format("%,.2f", currentBid);
        }
        return String.format("%-12s%3s%9s%2s%-23s%1s%-24s%1s%10s%2s%2s", "  " + auctionID, "|  $", bid, "|", " " + sellerName, "|", " " + buyerName, "|", " " + timeRemaining + " hours", "|", " " + itemInfo.substring(0, Math.min(itemInfo.length(), 42)));
    }
}