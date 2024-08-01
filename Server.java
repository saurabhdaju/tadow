import java.net.*;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

class Server extends JFrame {

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

            createGUI();
            clientSocket = serverSocket.accept();
            JOptionPane.showMessageDialog(this, "Client Connected Successfully");

            //For reading-writing data through client socket
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            handleEvents();
            reader();
        } 
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server Constructor Error\n");
        }
    }


    // Declaring GUI Components
    private JLabel heading = new JLabel("Client");
    private JTextArea msgsArea = new JTextArea();
    private JTextField msgInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    private void createGUI() {

        this.setTitle("tadow");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        //heading properties
        this.add(heading, BorderLayout.NORTH);
        heading.setFont(font);
        ImageIcon dp = new ImageIcon("./dpC.jpg");
        BufferedImage img = new BufferedImage(dp.getImage().getWidth(null), dp.getImage().getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(dp.getImage(), 0, 0, null); // makes a copy of the image to avoid any changes to the original image
        Image image = img.getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        dp = new ImageIcon(image);
        heading.setIcon(dp);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // message display area
        JScrollPane scrollPane = new JScrollPane(msgsArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //add a scrollable view of messages-display-area, hides the horizontal and vertical scrollbars
        this.add(scrollPane, BorderLayout.CENTER); //add the scrollable view to the center of the frame
        msgsArea.setFont(font);
        msgsArea.setBackground(Color.LIGHT_GRAY);
        msgsArea.setEditable(false);

        // add auto-scrolling to the bottom of the messages-display-area
        Document document = msgsArea.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scrollToEnd();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {}

            @Override
            public void changedUpdate(DocumentEvent e) {
                scrollToEnd();
            }

            private void scrollToEnd() {
                SwingUtilities.invokeLater(() -> {
                    msgsArea.setCaretPosition(msgsArea.getDocument().getLength());
                });
            }
        });

        // message input area
        this.add(msgInput, BorderLayout.SOUTH);
        msgInput.setFont(font);

        this.setVisible(true);
    }

    private void handleEvents() {
        msgInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = msgInput.getText();

                    if(contentToSend.equals("sudo exit")) {
                        out.println(contentToSend);
                        out.flush();
                        JOptionPane.showMessageDialog(Server.this, "You end the chat.\n");
                        msgInput.setEnabled(false);
                        try {
                            clientSocket.close();
                        } catch (Exception exception) {
                            if (exception instanceof SocketException) {
                                System.out.println("You end the chat.\n");
                            } else {
                                exception.printStackTrace();
                            }
                        }
                        return;
                    }
                    else if(!(contentToSend.length() == 0
                            || (contentToSend.chars().distinct().count() == 1 && contentToSend.charAt(0) == ' '))) {
                        msgsArea.append("You    : " + contentToSend + "\n");
                        out.println(contentToSend);
                        out.flush();

                        msgInput.setText("");
                        msgInput.requestFocus();
                    }
                }
            }
        });
    }

    public void reader() {

        //Creating a thread to read data from client
        Runnable r1=()-> {
            System.out.println("[ R e a d e r  S t a r t e d ]\n");
            while(!clientSocket.isClosed()) {
                try {
                    String msg = br.readLine();

                    if(msg.equals("sudo exit")) {
                        JOptionPane.showMessageDialog(this, "Client terminated the chat.\n");
                        msgInput.setEnabled(false);
                        clientSocket.close();
                        break;
                    }

                    msgsArea.append("Client : " + msg + "\n");
                } 
                catch(Exception e) {
                    if(e instanceof NullPointerException) {
                        System.out.println("[ R e a d e r  S t o p p e d ]\n");
                    } 
                    else if(e instanceof SocketException) {
                        System.out.println("Chat ended by Client\n");
                    }
                    else {
                        e.printStackTrace();
                    }
                    return;
                }
                
            }
        };

        //Starting the thread
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Server...going to start Server\n");

        new Server();
    }
}