import java.net.*;
import GameLogi.Board;
import java.io.*;
// deals with the game session and its states and actions
 
class GameSession implements Runnable
{
    private Socket p1, p2;
    private Board board;

    public GameSession(Socket p1, Socket p2, String gameType) {
        this.p1 = p1;
        this.p2 = p2;
        this.board = new Board(gameType);
    }
    
    @Override 
    public void run() {
        // Implement the game session logic here
        // For example, you can handle turns, send board state to clients, etc.
        try(
        
            BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));

            PrintWriter out1 = new PrintWriter(p1.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(p2.getOutputStream(), true);
        ){
            // provides the color of the pieces for each player
            out1.println("You are playing as White");
            out2.println("You are playing as Black");

            // Shows the initial board state to both players
            out1.println("Initial Board State: " + board);
            out2.println("Initial Board State: " + board);

            //set the current player to player 1 (White)
            BufferedReader currentIn = in1;
            PrintWriter currentOut = out1;
            // Main Game Loop 

            while (true) {
                // Get the move from the current player
                String move = currentIn.readLine();
                if (move == null || move.equalsIgnoreCase("quit")) {
                    break; // Exit the loop if the player quits or the connection is closed
                }

                // Process the move and update the board state
                // For simplicity, we will just echo the move back to both players
                out1.println("Player 1 moved: " + move);
                out2.println("Player 1 moved: " + move);

                // Switch turns
                if (currentIn == in1) {
                    currentIn = in2;
                    currentOut = out2;
                } else {
                    currentIn = in1;
                    currentOut = out1;
                }
            }


        }catch(IOException e) {
            System.out.println("Error in GameSession: " + e.getMessage());
        } finally {
            try {
                p1.close();
                p2.close();
            } catch (IOException e) {
                System.out.println("Error closing sockets: " + e.getMessage());
            }
        }
    
    }
}
