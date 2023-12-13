import java.io.*;
import java.net.*;
import java.util.*;


class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1234);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner sc = new Scanner(System.in);
        String line = null;

        while (!"exit".equalsIgnoreCase(line) && Server.running) {
            System.out.print("-> ");
            line = sc.nextLine();

            out.println(line);
            out.flush();

            System.out.println("server> " + in.readLine());
        }

        System.out.println("shutting down");
        sc.close();
    }
}
