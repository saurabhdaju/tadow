import java.io.*;
import java.net.*;

class Server {

    ServerSocket serverSocket;
    Socket clientSocket;

    BufferedReader br;
    PrintWriter out;

    //Constructor
    public Server() {
        try {
            serverSocket = new ServerSocket(7777);
            System.out.println("Server is running on Port 7777\n");
            System.out.println("Server is ready to accept connection\n");
            System.out.println("Waiting...\n");

            clientSocket = serverSocket.accept();

            //For reading-writing data through client socket
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            reader();
            writer();
        } 
        catch (IOException e) {
            System.out.println("SERVER CONSTRUCTOR KA CATCH BLOCK HIT HUA HAI");
            e.printStackTrace();
            System.out.println("SERVER CONSTRUCTOR KA CATCH BLOCK KHATAM");
        }
    }

    public void reader() {

        //Creating a thread to read data from client
        Runnable r1=()-> {
            System.out.println("[ R e a d e r  S t a r t e d ]\n");
            while(!clientSocket.isClosed()) {
                try {
                    String msg = br.readLine();

                    if(msg.equals("sudo exit")) {
                        System.out.println("Client Terminated the chat\n");
                        System.out.println("C O N N E C T I O N   I N T E R R U P T E D\n");
                        clientSocket.close();
                        break;
                    }

                    System.out.println("Client: " + msg + "\n");
                } 
                catch(Exception e) {
                    if(e instanceof SocketException) {
                        // System.out.println("C O N N E C T I O N   C L O S E D\n");
                    } 
                    else {
                        e.printStackTrace();
                    }
                }
                
            }
        };

        //Starting the thread
        new Thread(r1).start();
    }

    public void writer() {
            
            //Creating a thread to write data to client
            Runnable r2=()-> {
                System.out.println("[ W r i t e r  S t a r t e d ]\n");
                while(!clientSocket.isClosed()) {
                    try {
                        BufferedReader ServerInput = new BufferedReader(new InputStreamReader(System.in));
                        String response = ServerInput.readLine();
                        out.println(response);
                        out.flush();
                        System.out.println();

                        if(response.equals("sudo exit")) {
                            clientSocket.close();
                            break;
                        }
                    }
                    catch(Exception e) {
                        System.out.println("WRITER KA CATCH BLOCK HIT HUA HAI");
                        e.printStackTrace();
                        System.out.println("WRITER KA CATCH BLOCK KHATAM");
                    }
                }
                System.out.println("C O N N E C T I O N   C L O S E D\n");
            };

            //Starting the thread
            new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("This is Server...going to start Server\n");

        new Server();
    }
}