import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * The {@code GameServer} class represents a server for the Pac-Man game.
 * 
 * It handles client connections and communication.
 */
public class GameServer {
    private ServerSocket sSocket = null;
    private static final int PORT = 12345;

    /**
     * Constructs a new GameServer instance and starts the server thread.
     */
    public GameServer() {
        ServerThread st = new ServerThread();
        st.start();
    }

    /**
     * The main method that creates a new GameServer instance.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        new GameServer();
    }

    /**
     * The {@code ServerThread} class represents a thread that listens for client
     * connections.
     */
    public class ServerThread extends Thread {

        /**
         * Constructs a new ServerThread instance.
         */
        public ServerThread() {
        }

        /**
         * The run method of the ServerThread that accepts client connections and starts
         * a new client thread for each client.
         */
        public void run() {
            try {
                System.out.println("Waiting for client to connect!");
                sSocket = new ServerSocket(PORT);
                int count = 1;
                while (true) {
                    Socket cSocket = sSocket.accept();
                    System.out.println("Client " + count + " connected!");

                    ClientThread ct = new ClientThread(cSocket, count);
                    ct.start();
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * The {@code ClientThread} class represents a thread that handles communication
     * with a specific client.
     */
    public class ClientThread extends Thread {

        int id;
        String name;

        Socket cSocket = null;

        /**
         * Constructs a new ClientThread instance.
         * 
         * @param cSocket the client socket
         * @param id      the client ID
         */
        public ClientThread(Socket cSocket, int id) {
            this.id = id;
            this.name = "Player " + id;
            this.cSocket = cSocket;
        }

        /**
         * The run method of the ClientThread that handles client communication.
         */
        public void run() {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(cSocket.getInputStream());
                oos = new ObjectOutputStream(cSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    Object o = ois.readObject();
                    if (o instanceof String) {
                        String command = (String) o;
                        switch (command) {
                            case "POS":
                                int x = (int) ois.readObject();
                                int y = (int) ois.readObject();
                                System.out.println(name + ": " + command + " is equal X: " + x + " Y: " + y);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                System.out.println("Client " + id + " disconnected!");
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                    if (cSocket != null) {
                        cSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}