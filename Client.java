import java.net.*;

import javax.imageio.ImageIO;
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
import javax.swing.event.DocumentListener;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class Client extends JFrame {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    // Declaring GUI Components
    private JLabel heading = new JLabel("daju");
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
            // writer();

        } catch (Exception e) {
            if (e instanceof ConnectException) {
                System.out.println("HTTP Status Code 503 - S E R V E R  U N A V A I L A B L E \n");
            } else {
                e.printStackTrace();
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
        // Border layout divides our window in 5 parts- North South East West Center.
        // default- center

        // heading
        this.add(heading, BorderLayout.NORTH);
        heading.setFont(font);
        ImageIcon dp = new ImageIcon("./dpC.jpg");
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

        // message area
        JScrollPane scrollPane = new JScrollPane(msgsArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //add a scrollable view of messages-display-area, hides the horizontal and vertical scrollbars
        this.add(scrollPane, BorderLayout.CENTER); //add the scrollable view to the center of the frame
        msgsArea.setFont(font);
        msgsArea.setBackground(Color.LIGHT_GRAY);
        msgsArea.setEditable(false);
        msgsArea.setCaretColor(Color.BLUE);

        // add auto-scrolling to the bottom of the messages-display-area
        Document document = msgsArea.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scrollToEnd();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Handle potential removal scenarios if needed
            }

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
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = msgInput.getText();
                    System.out.println("You tried to send msg: " + contentToSend);

                    if(contentToSend.equals("sudo exit")) {
                        out.println(contentToSend);
                        out.flush();
                        System.out.println("C O N N E C T I O N   I N T E R R U P T E D\n");
                        JOptionPane.showMessageDialog(Client.this, "You end the chat.\n");
                        msgInput.setEnabled(false);
                        try {
                            socket.close();
                        } catch (Exception exception) {
                            if (exception instanceof SocketException) {
                                // System.out.println("C O N N E C T I O N  C L O S E D\n");
                            } else {
                                exception.printStackTrace();
                            }
                        }
                        return;
                    }
                    else if(!(contentToSend.length() == 0
                            || (contentToSend.chars().distinct().count() == 1 && contentToSend.charAt(0) == ' '))) {
                        msgsArea.append("You: " + contentToSend + "\n");
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
                        System.out.println("Server terminated the chat.\n");
                        System.out.println("C O N N E C T I O N   I N T E R R U P T E D\n");

                        JOptionPane.showMessageDialog(this, "Server terminated the chat.\n");
                        msgInput.setEnabled(false);

                        socket.close();
                        break;
                    }
                    msgsArea.append("Server: " + msg + "\n");
                    // System.out.println("Server: " + msg + "\n");
                } catch (Exception e) {
                    if (e instanceof NullPointerException) {
                        System.out.println("[ R e a d e r  S t o p p e d ]\n");
                    } else {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        };

        // Starting the thread
        new Thread(r1).start();
    }

    public void writer() {
        if (socket.isClosed())
            return;

        Runnable r2 = () -> {
            System.out.println("[ W r i t e r  S t a r t e d ]\n");
            while (!socket.isClosed()) {
                try {
                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
                    String msg = clientInput.readLine();
                    out.println(msg);
                    out.flush();
                    System.out.println();

                    if (msg.equals("sudo exit")) {
                        socket.close();
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("WRITER KA CATCH BLOCK HIT HUA HAI");
                    e.printStackTrace();
                    System.out.println("WRITER KA CATCH BLOCK KHATAM");
                }
            }
            System.out.println("C O N N E C T I O N   C L O S E D");
        };

        // Starting the thread
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Client...going to start Client\n");

        new Client();
    }

}
