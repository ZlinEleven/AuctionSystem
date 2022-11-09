import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import big.data.*;

public class AuctionTable implements Serializable{
    private HashMap<String, Auction> auctions;
    private static ArrayList<String> auctionIDs = new ArrayList<String>();

    public AuctionTable(HashMap<String, Auction> auctions){
        this.auctions = auctions;
    }

    public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException {
        try{
            DataSource ds = DataSource.connect(URL).load();
            String[] auctionIDList = ds.fetchStringArray("listing/auction_info/id_num");
            String[] bidList = ds.fetchStringArray("listing/auction_info/current_bid");
            String[] sellerList = ds.fetchStringArray("listing/seller_info/seller_name");
            String[] buyerList = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
            String[] timeList = ds.fetchStringArray("listing/auction_info/time_left");
            String[] memoryList = ds.fetchStringArray("listing/item_info/memory");
            String[] cpuList = ds.fetchStringArray("listing/item_info/cpu");
            String[] hard_driveList = ds.fetchStringArray("listing/item_info/hard_drive");
            String[] itemInfoList = new String[memoryList.length];

            HashMap<String, Auction> auctions = new HashMap<String, Auction>();

            for(int i = 0; i < memoryList.length; i++){
                bidList[i] = bidList[i].replaceAll("[$,]", "");
                buyerList[i] = buyerList[i].trim();

                String[] temp = timeList[i].split(" ");
                if(temp[1].contains("days")){
                    timeList[i] = "" + (Integer.parseInt(temp[0]) * 24 + Integer.parseInt(temp[2]));
                }

                itemInfoList[i] = cpuList[i] + " - " + memoryList[i] + " - " + hard_driveList[i];

                auctions.put(auctionIDList[i], new Auction(auctionIDList[i], Double.parseDouble(bidList[i]), sellerList[i], buyerList[i], Integer.parseInt(timeList[i]), itemInfoList[i]));
                auctionIDs.add(auctionIDList[i]);
            }
            System.out.println("Auction data loaded successfully!");

            return new AuctionTable(auctions);
        }
        catch(DataSourceException e){
            throw new IllegalArgumentException(URL + " does not exist or could not be read.");
        }
    }


    public void putAuction(String auctionID, Auction auction) throws IllegalArgumentException {
        if(auctions.containsKey(auctionID)){
            throw new IllegalArgumentException("Auction is already stored in the table.");
        }
        auctions.put(auctionID, auction);
        auctionIDs.add(auctionID);
    }

    public Auction getAuction(String auctionID) throws NullPointerException{
        if(auctions.get(auctionID) == null){
            throw new NullPointerException("ERROR: Auction " + auctionID + " does not exist.");
        }
        return auctions.get(auctionID);
    }

    public void letTimePass(int numHours) throws IllegalArgumentException {
        if(numHours < 0){
            throw new IllegalArgumentException("Time cannot be negative.");
        }

        System.out.println("Time passing...");
        
        for(int i = 0; i < auctionIDs.size(); i++){
            getAuction(auctionIDs.get(i)).decrementTimeRemaining(numHours);
        }
        System.out.println("Auction times updated.");
    }

    public void removeExpiredAuctions(){
        System.out.println("Removing expired auctions...");
        for(int i = 0; i < auctionIDs.size(); i++){
            Auction auction = getAuction(auctionIDs.get(i));
            if(auction.getTimeRemaining() == 0){
                auctions.remove(auctionIDs.get(i));
                auctionIDs.remove(i);
                i--;
            }
        }
        System.out.println("All expired Auctions removed.");
    }

    public void printTable(){
        System.out.println(String.format("%2s%2s%10s%4s%14s%10s%15s%10s%8s%4s%11s", " Auction ID", "|", "Bid", "|", "Seller", "|", "Buyer", "|", "Time", "|", "Item Info"));
        System.out.println("===================================================================================================================================");

        for(int i = 0; i < auctionIDs.size(); i++){
            System.out.println(getAuction(auctionIDs.get(i)));
        }
    }
}
