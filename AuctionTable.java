import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import big.data.*;

/**
 * This class represents a AuctionTable object that stores all of the auctions in a hashMap.
 * Field variables include auctions, a hashMap containing the auctions, and auctionIDs, an
 * ArrayList containing the ids of all the auctions in auctions
 */
public class AuctionTable extends HashMap<String, Auction> implements Serializable{

    /**
     * Constructor for a AuctionTable object
     * @param auctions A hashMap to store Auction by using an auction's id as its key
     */
    public AuctionTable(){
        super();
    }

    /**
     * Creates auctions from a given URL 
     * @param URL The URL to fetch auction data from
     * @return An AuctionTable object with auctions from the URL already initialized within it
     * @throws IllegalArgumentException If the URL does not exist or could not be read, throw an exception
     */
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

            AuctionTable auctions = new AuctionTable();

            for(int i = 0; i < memoryList.length; i++){
                bidList[i] = bidList[i].replaceAll("[$,]", "");
                sellerList[i] = sellerList[i].replaceAll("[\r\n]", "");
                buyerList[i] = buyerList[i].trim();

                String[] temp = timeList[i].split(" ");
                if(temp[1].contains("day")){
                    timeList[i] = "" + (Integer.parseInt(temp[0]) * 24);
                    if(temp.length > 2)
                        timeList[i] = "" + (Integer.parseInt(timeList[i]) + Integer.parseInt(temp[2]));
                }
                else{
                    timeList[i] = temp[0];
                }

                itemInfoList[i] = cpuList[i] + " - " + memoryList[i] + " - " + hard_driveList[i];

                auctions.put(auctionIDList[i], new Auction(auctionIDList[i], Double.parseDouble(bidList[i]), sellerList[i], buyerList[i], Integer.parseInt(timeList[i]), itemInfoList[i]));
            }
            System.out.println("Auction data loaded successfully!");
            return auctions;
        }
        catch(DataSourceException e){
            throw new IllegalArgumentException(URL + " does not exist or could not be read.");
        }
    }

    

    /**
     * Manually stores an auction to auctions (the hashMap containing the auctions)
     * @param auctionID The auction id used as the key in the hashMap
     * @param auction The auction paired with the auction id
     * @throws IllegalArgumentException If the auction is already contained within the hashMap, throw an exception
     */
    public void putAuction(String auctionID, Auction auction) throws IllegalArgumentException {
        if(containsKey(auctionID)){
            throw new IllegalArgumentException("Auction with ID " + auctionID + " is already stored in the table.");
        }
        put(auctionID, auction);
    }

    public void copyAuctions(AuctionTable at){
        for(String id : at.keySet()){
            try{
                putAuction(id, at.getAuction(id));
            }
            catch(IllegalArgumentException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Gives the auction paired with the key represented by auctionID
     * @param auctionID The key to search for in the hashMap
     * @return The auction corresponding to auctionID in the hashMap
     * @throws NullPointerException If auctionID is not a key in the hashMap, throw an exception
     */
    public Auction getAuction(String auctionID) throws NullPointerException{
        if(get(auctionID) == null){
            throw new NullPointerException("ERROR: Auction " + auctionID + " does not exist.");
        }
        return get(auctionID);
    }

    /**
     * Decreases the timeRemaining of all auctions in the AuctionTable by numHours hours
     * @param numHours The amount of hours to decrease an auction's timeRemaining by
     * @throws IllegalArgumentException If numHours is a negative value, throw an exception
     */
    public void letTimePass(int numHours) throws IllegalArgumentException {
        if(numHours < 0){
            throw new IllegalArgumentException("Time cannot be negative.");
        }

        System.out.println("Time passing...");
        for (Auction auction : values()) {
			auction.decrementTimeRemaining(numHours);
		}

        System.out.println("Auction times updated.");
    }

    /**
     * Searches through all the auctions in the hashMap and removes the auction from both auctions and auctionIDs
     */
    public void removeExpiredAuctions() {
        System.out.println("Removing expired auctions...");
        
        ArrayList<String> removeList = new ArrayList<String>();
        
        for(Auction auction : values()){
            if(auction.getTimeRemaining() == 0){
                removeList.add(auction.getAuctionID());
            }
        }

        keySet().removeAll(removeList);
        System.out.println("All expired Auctions removed.");

    }

    /**
     * Prints all the auctions contained in the hashMap in a neatly formatted table
     */
    public void printTable(){
        System.out.println(String.format("%2s%2s%10s%4s%14s%10s%15s%10s%8s%4s%11s", " Auction ID", "|", "Bid", "|", "Seller", "|", "Buyer", "|", "Time", "|", "Item Info"));
        System.out.println("===================================================================================================================================");

        ArrayList<Auction> auctionSet = new ArrayList<Auction>(20);
        for (Auction auction : values()) {
            int index = 0;
            while(index < auctionSet.size() && auction.getTimeRemaining() <= auctionSet.get(index).getTimeRemaining()){
                if(auction.getTimeRemaining() == auctionSet.get(index).getTimeRemaining()){
                    if(Integer.parseInt(auction.getAuctionID()) > Integer.parseInt(auctionSet.get(index).getAuctionID())){
                        break;
                    }
                }
                index++;
            }
            auctionSet.add(index, auction);
		}

        for(Auction auction : auctionSet){
            System.out.println(auction);
        }
    }
}
