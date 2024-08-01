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

public class Client extends JFrame {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    // Declaring GUI Components
    private JLabel heading = new JLabel("Server");
    private JTextArea msgsArea = new JTextArea();
    private JTextField msgInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        try {
            System.out.println("Client is running on Port 7777\n");

            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection Established Successfully\n");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            createGUI();
            handleEvents();
            reader();

        } catch (Exception e) {
            if (e instanceof ConnectException) {
                System.out.println("HTTP Status Code 503 - S E R V E R  U N A V A I L A B L E \n");
            } else {
                e.printStackTrace();
                System.out.println("Client Constructor Error\n");
            }
        }
    }

    private void createGUI() {
        // basic layout design
        this.setTitle("tadow");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting the layout
        this.setLayout(new BorderLayout());
        // Border layout divides our window in 5 parts- North South East West Center. (default- center)

        // heading
        this.add(heading, BorderLayout.NORTH);
        heading.setFont(font);
        ImageIcon dp = new ImageIcon("./dpS.png");
        BufferedImage img = new BufferedImage(dp.getImage().getWidth(null), dp.getImage().getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(dp.getImage(), 0, 0, null); // makes a copy of the image to avoid any changes to the
                                                                // original image
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
                        JOptionPane.showMessageDialog(Client.this, "You end the chat.\n");
                        msgInput.setEnabled(false);
                        try {
                            socket.close();
                        } catch (Exception exception) {
                            if (exception instanceof SocketException) {
                                System.out.println("C O N N E C T I O N  C L O S E D\n");
                            } else {
                                exception.printStackTrace();
                            }
                        }
                        return;
                    }
                    else if(!(contentToSend.length() == 0
                            || (contentToSend.chars().distinct().count() == 1 && contentToSend.charAt(0) == ' '))) {
                        msgsArea.append("You     : " + contentToSend + "\n");
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
        Runnable r1 = () -> {
            System.out.println("[ R e a d e r  S t a r t e d ]\n");
            while (!socket.isClosed()) {
                try {
                    String msg = br.readLine();

                    if (msg.equals("sudo exit")) {
                        JOptionPane.showMessageDialog(this, "Server terminated the chat.\n");
                        msgInput.setEnabled(false);

                        socket.close();
                        break;
                    }
                    msgsArea.append("Server : " + msg + "\n");
                } 
                catch (Exception e) {
                    if (e instanceof NullPointerException) {
                        System.out.println("[ R e a d e r  S t o p p e d ]\n");
                    } 
                    else if(e instanceof SocketException) {
                        System.out.println("Chat ended by Server\n");
                    }
                    else {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        };

        // Starting the thread
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Client...going to start Client\n");

        new Client();
    }
}