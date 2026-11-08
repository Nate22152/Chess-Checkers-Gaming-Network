// This is the MainS class entry point for Server-Side
import java.io.IOException;

public class MainS 
{
    public static void main(String[] args) {
        try {
            new Server(5000).start();
            System.out.println("Server started on port 5000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
