import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
// по идее MAX_CLIENTS работает, но сообщение клиенту так и не выводится, но принимать он выше лимита не принимает
public class ServerChat {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 2;
    private static List<String> quotes = Arrays.asList(
            "quotes 1",
            "quotes 2",
            "quotes 3"
    );
    private static ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server is running. Waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (Thread.activeCount() - 1 >= MAX_CLIENTS) {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("The server is under maximum load. Try again later.");
                    clientSocket.close();
                } else {
                    System.out.println("The client is connected: " + clientSocket.getInetAddress());
                    executor.execute(new ClientHandler(clientSocket));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public static final int MAX_QUOTES_PER_CLIENT = 5;


        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String clientAddress = clientSocket.getInetAddress().toString();
                System.out.println("Client " + clientAddress + " connected to " + getCurrentTime());
                int sentQuotesCount = 0;

                while (sentQuotesCount < MAX_QUOTES_PER_CLIENT) {
                    String request = in.readLine();
                    if (request == null || request.equals("exit")) {

                        break;
                    }

                    String randomQuote = getRandomQuote();
                    out.println(randomQuote);
                    System.out.println("Sent to the client " + ": " + randomQuote);
                    sentQuotesCount++;


                   /* if (sentQuotesCount >= MAX_QUOTES_PER_CLIENT) {
                        out.println("The maximum number of citations has been reached. Connection completed.");
                        clientSocket.close();
                    }*/
                }

                System.out.println("Client " + clientAddress + " disconnected to " + getCurrentTime());
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getCurrentTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(new Date());
        }


        private String getRandomQuote() {
            int randomIndex = new Random().nextInt(quotes.size());
            return quotes.get(randomIndex);
        }
    }
}