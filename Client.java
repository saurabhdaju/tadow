import java.net.*;
import java.io.*;

public class Client {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    public Client() {
        try {
            System.out.println("Client is running on Port 7777\n");

            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection Established Successfully\n");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            reader();
            writer();

        }
        catch(Exception e) {
            if(e instanceof ConnectException) {
                System.out.println("HTTP Status Code 503 - S E R V E R  U N A V A I L A B L E \n");
            }
            else {
                e.printStackTrace();
            }
        }
    }

    public void reader() {
        Runnable r1 = () -> {
            System.out.println("[ R e a d e r  S t a r t e d ]\n");
            while(!socket.isClosed()) {
                try {
                    String msg = br.readLine();

                    if(msg.equals("sudo exit")) {
                        System.out.println("Server terminated the chat.\n");
                        System.out.println("C O N N E C T I O N   I N T E R R U P T E D\n");
                        socket.close();
                        break;
                    }
                    System.out.println("Server: " + msg + "\n");
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

        // Starting the thread
        new Thread(r1).start();
    }

    public void writer() {
        if(socket.isClosed()) return;

        Runnable r2 = () -> {
            System.out.println("[ W r i t e r  S t a r t e d ]\n");
            while(!socket.isClosed()) {
                try {
                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
                    String msg = clientInput.readLine();
                    out.println(msg);
                    out.flush();
                    System.out.println();
                    
                    if(msg.equals("sudo exit")) {
                        socket.close();
                        break;
                    }
                }
                catch(Exception e) {
                    System.out.println("WRITER KA CATCH BLOCK HIT HUA HAI");
                    e.printStackTrace();
                    System.out.println("WRITER KA CATCH BLOCK KHATAM");
                }
            }
            System.out.println("C O N N E C T I O N   C L O S E D");
        };

        //Starting the thread
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Client...going to start Client\n");

        new Client();
    }
    
}
