import java.io.*;
import java.net.*;



public class Server
{   
    //initialize the server socket and input stream
    private ServerSocket ss; 
    private Socket p1, p2;
    
    // constructor with port
    public Server(int port) throws IOException
    {
        ss = new ServerSocket(port);
        System.out.println("Server started on port: " + port);
    }

    public void start() throws IOException
    {
        System.out.println("Waiting for a client ...");

        p1 = ss.accept();
        System.out.println("Client 1 accepted" );
        
        System.out.println("Waiting for a second client ...");

        p2 = ss.accept();
        System.out.println("Client 2 accepted");
        
        // takes input from the client socket
        System.out.println("Starting a New Game Session");
        // GameSession gameSession = new GameSession(p1, p2, gameType);
        // new Thread(gameSession).start();
        
    }
}