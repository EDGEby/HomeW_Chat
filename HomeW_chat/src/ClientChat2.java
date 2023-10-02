import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientChat2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server. Enter 'exit' to disable.");
            int receivedQuotesCount = 0;
            while (true) {
                if (receivedQuotesCount >= ServerChat.ClientHandler.MAX_QUOTES_PER_CLIENT) {
                    System.out.println("The maximum number of citations has been reached. Connection completed.");
                    break;
                }

                String userInput = consoleIn.readLine();
                if (userInput.equals("exit")) {
                    break;
                }

                out.println(userInput);
                String serverResponse = in.readLine();
                System.out.println("Received from the server: " + serverResponse);

                receivedQuotesCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
