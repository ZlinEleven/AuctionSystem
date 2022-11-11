import java.io.*;
import java.util.Scanner;

/**
 * This class contains the main method which is a menu driven application.
 * This program promts the user for commands that interacts with an AuctionTable
 * object. The user can also input "Q" to termiante the program. 
 */
public class AuctionSystem{
    private static AuctionTable auctionTable;
    private static String username;

    public static void main(String[] args) throws IOException, ClassNotFoundException{
        Scanner scan = new Scanner(System.in);
        
        String menu = "Menu:\n    (D) - Import Data from URL\n    (A) - Create a New Auction\n" +
                            "    (B) - Bid on an Item\n    (I) - Get Info on Auction\n" +
                            "    (P) - Print All Auctions\n    (R) - Remove Expired Auctions\n" +
                            "    (T) - Let Time Pass\n    (Q) - Quit";
        
        System.out.println("Starting...");

        try {
			FileInputStream infile = new FileInputStream("auction.obj");
			ObjectInputStream inStream = new ObjectInputStream(infile);

			auctionTable = (AuctionTable) inStream.readObject();
            System.out.println("Loading previous Auction Table...");
			infile.close();
			inStream.close();
		} 
        catch (IOException e) {
			System.out.println("No previous auction table detected.\nCreating new table...");
			auctionTable = new AuctionTable();
		}
        catch (ClassNotFoundException e) {
			System.out.println("No previous auction table detected.\nCreating new table...");
			auctionTable = new AuctionTable();
		}
        
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
                try{
                    auctionTable = AuctionTable.buildFromURL(url);
                }
                catch(IllegalArgumentException e){
                    System.out.println(e.getMessage());
                }        
            }

            else if(selection.toUpperCase().equals("A")){
                System.out.println();
                System.out.println("Creating new auction as " + username + ".");
                System.out.print("Please enter an Auction ID: ");
                String id = scan.nextLine();
                System.out.print("Please enter an Auction time (hours): ");
                int time = Integer.parseInt(scan.nextLine());
                System.out.print("Please enter some Item Info: ");
                String info = scan.nextLine();

                auctionTable.putAuction(id, new Auction(id, 0, username, "", time, info));
                System.out.println("\nAuction " + id + " inserted into table.");
            }

            else if(selection.toUpperCase().equals("B")){
                System.out.print("Please enter an Auction ID: ");
                String id = scan.nextLine();

                try{
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
                        }
                    }
                    else{
                        System.out.println("CLOSED");
                        System.out.print("    Current bid: ");
                        if(auction.getCurrentBid() == 0){
                            System.out.println("None");
                        }
                        else{
                            System.out.println("$ " + String.format("%.2f", auction.getCurrentBid()));
                        }
                        System.out.println();
                        System.out.println("You can no longer bid on this item.");
                    }
                }
                catch(NullPointerException e){
                    System.out.println(e.getMessage());
                }
            }

            else if(selection.toUpperCase().equals("I")){
                System.out.print("Please enter an Auction ID: ");
                String id = scan.nextLine();
                System.out.println();
                Auction auction = auctionTable.getAuction(id);
                if(auction == null){
                    System.out.println("Auction "+ id + " could not be found.");
                }
                else{
                    System.out.println("Auction " + id + ":");
                    System.out.println("    Seller: " + auction.getSellerName());
                    System.out.println("    Buyer: " + auction.getBuyerName());
                    System.out.println("    Time: " + auction.getTimeRemaining() + " hours");
                    System.out.println("    Info: " + auction.getItemInfo());
                }
            }

            else if(selection.toUpperCase().equals("P")){
                System.out.println();
                auctionTable.printTable();
            }

            else if(selection.toUpperCase().equals("R")){
                System.out.println();
                auctionTable.removeExpiredAuctions();
            }

            else if(selection.toUpperCase().equals("T")){
                System.out.print("How many hours should pass: ");
                int time = Integer.parseInt(scan.nextLine());
                System.out.println();

                auctionTable.letTimePass(time);
            }

            System.out.println();
            System.out.println(menu);
            System.out.println();
            System.out.print("Please select an option: ");
            selection = scan.nextLine();
        }

        System.out.println("\nWriting Auction Table to file...");

        File f = new File("auction.obj");
        f.createNewFile();
        FileOutputStream file = new FileOutputStream("auction.obj");
        ObjectOutputStream outStream = new ObjectOutputStream(file);
        outStream.writeObject(auctionTable);
        // auctionTable.printTable();

        System.out.println("Done!\n\nGoodbye.\n");

        scan.close();
        file.close();
        outStream.close();

        System.exit(0);
    }
}
