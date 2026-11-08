import java.io.*;
import java.net.*;

public class Client 
{
    private Socket s = null;
    private BufferedReader in = null;   // reads data from the server socket
    private DataOutputStream out = null; // sends data to the server socket

    public Client(String address, int port)
    {
        // establish a connection
        try
        {
            s = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            in = new BufferedReader(new InputStreamReader(System.in));

            // sends output to the socket
            out = new DataOutputStream(s.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        // string to read message from input
        String line = "";

        // keep reading until "Over" is sent
        while (!line.equals("Over"))
        {
            try
            {
                line = in.readLine();
                out.writeUTF(line);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

        // close the connection
        try
        {
            in.close();
            out.close();
            s.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
    public static void main(String[] args)
    {
        Client client = new Client("localhost", 5000);
    }
}
