import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
    private final ServerSocket server;
    private static final ArrayList<ClientHandler> clients = new ArrayList<>();
    static int secretValue;
    static boolean running = true;

    public Server() throws IOException {
        this.server = new ServerSocket(1234);
        secretValue = ThreadLocalRandom.current().nextInt(0, 100 + 1)* 100;
        System.out.println("Secret value =" + secretValue);
    }

    public void start() throws IOException {
        System.out.println("server> listening on port 1234");
        while (running) {
            Socket connection = server.accept();
            ClientHandler client = new ClientHandler(connection, clients.size());
            clients.add(client);
            System.out.printf("server> new client connected [id: %d] [address: %s]\n", clients.size() - 1, connection.getInetAddress().getHostAddress());
        }
    }

    public static void endGame(int winnerId) throws IOException {
        for (int i = 0; i < clients.size(); i++) {
            if (i != winnerId) {
                PrintWriter out = new PrintWriter(clients.get(i).socket.getOutputStream(), false);
                out.println("Game over!! the winner is client: " + winnerId);
                out.flush();
                running = false;
            }
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private final int id;

        public ClientHandler(Socket socket, int id) {
            this.socket = socket;
            this.id = id;
            this.start();
        }

        public void run() {
            PrintWriter out;
            BufferedReader in;
            try {
                out = new PrintWriter(socket.getOutputStream(), false);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null && Server.running) {
                    System.out.printf("client(%d)> %s\n", id, message);

                    if (Integer.parseInt(message) == Server.secretValue) {
                        out.println("Congratulation you are the winner, the secret value was: " + Server.secretValue);
                        out.flush();
                        Server.endGame(id);
                        continue;
                    }

                    if (Integer.parseInt(message) > Server.secretValue) {
                        out.println("You guessed too high");
                        out.flush();
                        continue;
                    }

                    if (Integer.parseInt(message) < Server.secretValue) {
                        out.println("You guessed too low");
                        out.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
