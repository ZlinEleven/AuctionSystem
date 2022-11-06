
import java.util.Scanner;

public class AuctionSystem {
    private static AuctionTable auctionTable;
    private static String username;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        
        String menu = "Menu:\n    (D) - Import Data from URL\n    (A) - Create a New Auction\n" +
                            "    (B) - Bid on an Item\n    (I) - Get Info on Auction\n" +
                            "    (P) - Print All Auctions\n    (R) - Remove Expired Auctions\n" +
                            "    (T) - Let Time Pass\n    (Q) - Quit";
        
        System.out.println("Starting...\nNo previous auction table detected.\nCreating new table...");
        System.out.print("\nPlease select a username: ");
        username = scan.nextLine();


        System.out.println("\n" + menu + "\n\n");
        System.out.print("Please select an option: ");
        String selection = scan.nextLine();
        while(!selection.toUpperCase().equals("Q")){
            
            if(selection.toUpperCase().equals("D")){
                System.out.print("Please enter a URL: ");
                String url = scan.nextLine();

                System.out.println("\nLoading...");
                auctionTable = AuctionTable.buildFromURL(url);
            
                System.out.println("Auction data loaded successfully!");
            }

            else if(selection.toUpperCase().equals("B")){
                System.out.print("Please enter an Auction ID: ");
                String id = scan.nextLine();

                Auction auction = auctionTable.getAuction(id);

                System.out.println();
                System.out.print("Auction " + id + " is ");
                if(auction.getTimeRemaining() > 0){
                    System.out.println("OPEN");
                    System.out.print("    Current bid: ");
                    if(auction.getCurrentBid() == 0){
                        System.out.println("None");
                    }
                    else{
                        System.out.println("$ " + String.format("%.2f", auction.getCurrentBid()));
                    }

                    System.out.println();
                    System.out.print("What would you like to bid?: ");
                    double bid = Double.parseDouble(scan.nextLine());
                    if(bid > auction.getCurrentBid()){
                        try {
                            auction.newBid(username, bid);
                        } catch (ClosedAuctionException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("Bid accepted.");
                    }
                }
                else{
                    System.out.println("CLOSED");
                    System.out.println("    Current bid: None");
                    System.out.println();
                    System.out.println("You can no longer bid on this item.");
                }

            }

            else if(selection.toUpperCase().equals("P")){
                auctionTable.printTable();
            }

            System.out.println();
            System.out.println(menu);
            System.out.println();
            System.out.print("Please select and option: ");
            selection = scan.nextLine();
        }
    
    }
}
